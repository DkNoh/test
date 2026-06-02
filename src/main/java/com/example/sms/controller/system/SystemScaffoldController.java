package com.example.sms.controller.system;

import com.example.sms.dto.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/system/scaffold")
public class SystemScaffoldController {

    private static final String BASE_DIR = System.getProperty("user.dir");

    @GetMapping
    public String scaffoldPage() {
        return "system/scaffold";
    }

    @ResponseBody
    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<Map<String, String>>> generateCode(@RequestBody Map<String, Object> request) {
        String moduleName = (String) request.get("moduleName");
        String domainId = (String) request.get("domainId");
        String domainClass = (String) request.get("domainClass");
        String domainName = (String) request.get("domainName");
        String rawQuery = (String) request.get("rawQuery");
        boolean includeCud = Boolean.TRUE.equals(request.get("includeCud"));
        boolean includeExcel = Boolean.TRUE.equals(request.get("includeExcel"));
        boolean includeExcelGrid = Boolean.TRUE.equals(request.get("includeExcelGrid"));

        if (moduleName == null || domainId == null || domainClass == null || domainName == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "필수 파라미터가 누락되었습니다."));
        }

        try {
            // 쿼리 파싱하여 컬럼 목록 및 검색 조건 추출
            List<String> parsedCols = extractColumnsFromQuery(rawQuery);
            List<String> searchVars = extractSearchVarsFromQuery(rawQuery);
            String[] cols = parsedCols.toArray(new String[0]);
            
            Map<String, String> results = new java.util.LinkedHashMap<>();
            results.put("SearchRequestDTO.java", getDtoTemplate(moduleName, domainClass, cols, searchVars));
            results.put("VO.java", getVoTemplate(moduleName, domainClass, cols));
            results.put("Mapper.java", getMapperInterfaceTemplate(moduleName, domainClass, includeCud, includeExcel));
            results.put("Mapper.xml", getMapperXmlTemplate(moduleName, domainClass, cols, rawQuery, includeCud, searchVars, includeExcel));
            results.put("Service.java", getServiceTemplate(moduleName, domainClass, includeCud, includeExcel, cols));
            results.put("Controller.java", getControllerTemplate(moduleName, domainId, domainClass, includeCud, includeExcel));
            results.put(domainId + "-manage.html", getHtmlTemplate(moduleName, domainId, domainName, searchVars, includeExcel, includeExcelGrid));
            results.put(domainId + "-manage.js", getJsTemplate(moduleName, domainId, domainName, cols, searchVars, includeExcel, includeExcelGrid));

            return ResponseEntity.ok(ApiResponse.success(results));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error(500, "생성 실패: " + e.getMessage()));
        }
    }

    @ResponseBody
    @PostMapping("/generate-excel-snippet")
    public ResponseEntity<ApiResponse<Map<String, String>>> generateExcelSnippet(@RequestBody Map<String, Object> request) {
        String moduleName = (String) request.get("moduleName");
        String domainClass = (String) request.get("domainClass");
        String rawQuery = (String) request.get("rawQuery");
        
        List<String> searchVars = extractSearchVarsFromQuery(rawQuery);

        String controllerSnippet = 
            "    @GetMapping(\"/excel\")\n" +
            "    public void downloadExcel(@ModelAttribute " + domainClass + "SearchRequestDTO request, jakarta.servlet.http.HttpServletResponse response) {\n" +
            "        service.downloadExcel(request, response);\n" +
            "    }";

        String serviceSnippet = 
            "    @Transactional(readOnly = true)\n" +
            "    public void downloadExcel(" + domainClass + "SearchRequestDTO request, jakarta.servlet.http.HttpServletResponse response) {\n" +
            "        java.util.List<" + domainClass + "VO> list = mapper.selectListForExcel(request);\n" +
            "        com.example.sms.util.ExcelUtils.download(response, list, " + domainClass + "VO.class, \"" + domainClass + "\");\n" +
            "    }";

        String mapperSnippet = 
            "    java.util.List<" + domainClass + "VO> selectListForExcel(" + domainClass + "SearchRequestDTO request);";

        StringBuilder xmlSnippet = new StringBuilder();
        xmlSnippet.append("    <select id=\"selectListForExcel\" resultType=\"com.example.sms.dto.").append(moduleName).append(".").append(domainClass).append("VO\">\n");
        xmlSnippet.append("        /* TODO: 이곳에 ROWNUM 페이징을 제외한 순수 데이터 조회 쿼리를 작성하세요 */\n");
        xmlSnippet.append("        <include refid=\"searchConditions\"/>\n");
        xmlSnippet.append("    </select>");

        String htmlSnippet = 
            "                <button type=\"button\" class=\"btn btn-success\" id=\"btn-excel\">엑셀 다운로드</button>";

        StringBuilder jsSnippet = new StringBuilder();
        jsSnippet.append("    document.getElementById('btn-excel')?.addEventListener('click', () => {\n");
        if (searchVars.isEmpty()) {
            jsSnippet.append("        const qs = `?searchKeyword=${document.getElementById('searchKeyword').value}`;\n");
        } else {
            jsSnippet.append("        const qs = `?");
            for (int i = 0; i < searchVars.size(); i++) {
                if (i > 0) jsSnippet.append("&");
                jsSnippet.append(searchVars.get(i)).append("=${document.getElementById('").append(searchVars.get(i)).append("').value}");
            }
            jsSnippet.append("`;\n");
        }
        jsSnippet.append("        window.location.href = pageBuilder.apiUrl.replace('/data', '') + '/excel' + qs;\n");
        jsSnippet.append("    });");

        return ResponseEntity.ok(ApiResponse.success(Map.of(
            "Controller (.java)", controllerSnippet,
            "Service (.java)", serviceSnippet,
            "Mapper (.java)", mapperSnippet,
            "XML (.xml)", xmlSnippet.toString(),
            "HTML (.html)", htmlSnippet,
            "JavaScript (.js)", jsSnippet.toString()
        )));
    }

    private java.util.List<String> extractColumnsFromQuery(String query) {
        java.util.List<String> columns = new java.util.ArrayList<>();
        if (query == null || query.trim().isEmpty()) return columns;
        
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("SELECT(.*?)\\s+FROM\\s", java.util.regex.Pattern.CASE_INSENSITIVE | java.util.regex.Pattern.DOTALL);
        java.util.regex.Matcher matcher = pattern.matcher(query);
        if (matcher.find()) {
            String selectPart = matcher.group(1);
            String[] parts = selectPart.split(",");
            for (String part : parts) {
                part = part.trim();
                if (part.isEmpty()) continue;
                String[] tokens = part.split("\\s+");
                String colName = tokens[tokens.length - 1]; // 마지막 단어 (AS 별칭 또는 컬럼명)
                if (colName.contains(".")) {
                    colName = colName.substring(colName.lastIndexOf(".") + 1);
                }
                columns.add(colName);
            }
        }
        return columns;
    }

    private java.util.List<String> extractSearchVarsFromQuery(String query) {
        java.util.List<String> vars = new java.util.ArrayList<>();
        if (query == null || query.trim().isEmpty()) return vars;
        
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\$([a-zA-Z0-9_]+)");
        java.util.regex.Matcher matcher = pattern.matcher(query);
        while (matcher.find()) {
            String varName = matcher.group(1);
            if (!vars.contains(varName)) {
                vars.add(varName);
            }
        }
        return vars;
    }

    private void createJavaFile(String subPackage, String fileName, String content) throws IOException {
        String path = "src/main/java/com/example/sms/" + subPackage;
        createFile(path, fileName, content);
    }

    private void createFile(String path, String fileName, String content) throws IOException {
        File dir = new File(BASE_DIR, path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, fileName);
        if (file.exists()) {
            return;
        }
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
    }

    private String toCamelCase(String s) {
        String[] parts = s.toLowerCase().split("_");
        StringBuilder camelCaseString = new StringBuilder(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            camelCaseString.append(parts[i].substring(0, 1).toUpperCase()).append(parts[i].substring(1));
        }
        return camelCaseString.toString();
    }

    // =========================================================================
    // TEMPLATES
    // =========================================================================

    private String getDtoTemplate(String module, String domainClass, String[] cols, List<String> searchVars) {
        StringBuilder sb = new StringBuilder();
        sb.append("package com.example.sms.dto.").append(module).append(";\n\n")
          .append("import com.example.sms.dto.common.PageRequestDTO;\n")
          .append("import lombok.Data;\n")
          .append("import lombok.EqualsAndHashCode;\n\n")
          .append("@Data\n")
          .append("@EqualsAndHashCode(callSuper = true)\n")
          .append("public class ").append(domainClass).append("SearchRequestDTO extends PageRequestDTO {\n");
        if (searchVars.isEmpty()) {
            sb.append("    private String searchKeyword;\n");
        } else {
            for (String var : searchVars) {
                sb.append("    private String ").append(var).append(";\n");
            }
        }
        sb.append("}\n");
        return sb.toString();
    }

    private String getVoTemplate(String module, String domainClass, String[] cols) {
        StringBuilder sb = new StringBuilder();
        sb.append("package com.example.sms.dto.").append(module).append(";\n\n")
          .append("import lombok.Data;\n\n")
          .append("@Data\n")
          .append("public class ").append(domainClass).append("VO {\n")
          .append("    private long rowNum;\n");
        
        for (String c : cols) {
            if(!c.trim().isEmpty()) {
                sb.append("    private String ").append(toCamelCase(c.trim())).append(";\n");
            }
        }
        sb.append("}\n");
        return sb.toString();
    }

    private String getMapperInterfaceTemplate(String module, String domainClass, boolean includeCud, boolean includeExcel) {
        StringBuilder sb = new StringBuilder();
        sb.append("package com.example.sms.mapper.").append(module).append(";\n\n")
          .append("import com.example.sms.dto.").append(module).append(".").append(domainClass).append("SearchRequestDTO;\n")
          .append("import com.example.sms.dto.").append(module).append(".").append(domainClass).append("VO;\n")
          .append("import org.apache.ibatis.annotations.Mapper;\n")
          .append("import java.util.List;\n")
          .append("import java.util.Map;\n\n")
          .append("@Mapper\n")
          .append("public interface ").append(domainClass).append("Mapper {\n")
          .append("    /**\n")
          .append("     * 조건에 맞는 전체 데이터 건수 조회\n")
          .append("     */\n")
          .append("    int count(").append(domainClass).append("SearchRequestDTO request);\n\n")
          .append("    /**\n")
          .append("     * 페이징 처리된 목록 데이터 조회\n")
          .append("     */\n")
          .append("    List<").append(domainClass).append("VO> selectList(").append(domainClass).append("SearchRequestDTO request);\n");
        
        if (includeCud) {
            sb.append("\n    /**\n")
              .append("     * 신규 데이터 등록\n")
              .append("     */\n")
              .append("    int insert(").append(domainClass).append("VO vo);\n\n")
              .append("    /**\n")
              .append("     * 기존 데이터 수정\n")
              .append("     */\n")
              .append("    int update(").append(domainClass).append("VO vo);\n\n")
              .append("    /**\n")
              .append("     * 데이터 삭제\n")
              .append("     */\n")
              .append("    int delete(String id);\n");
        }
        if (includeExcel) {
            sb.append("\n    /**\n")
              .append("     * 엑셀 다운로드용 전체 데이터 조회 (페이징 없음)\n")
              .append("     */\n")
              .append("    List<Map<String, Object>> selectListForExcel(").append(domainClass).append("SearchRequestDTO request);\n");
        }
        sb.append("}\n");
        return sb.toString();
    }

    private String getMapperXmlTemplate(String module, String domainClass, String[] cols, String rawQuery, boolean includeCud, List<String> searchVars, boolean includeExcel) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
          .append("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n")
          .append("<mapper namespace=\"com.example.sms.mapper.").append(module).append(".").append(domainClass).append("Mapper\">\n\n")
          .append("    <sql id=\"searchConditions\">\n")
          .append("        <where>\n");
        if (searchVars.isEmpty() && cols.length > 0) {
            sb.append("            <if test=\"searchKeyword != null and searchKeyword != ''\">\n")
              .append("                AND A.").append(cols[0].trim().toUpperCase()).append(" LIKE '%' || #{searchKeyword} || '%'\n")
              .append("            </if>\n");
        }
        sb.append("        </where>\n")
          .append("    </sql>\n\n")
          .append("    <select id=\"count\" resultType=\"int\">\n")
          .append("        SELECT COUNT(1) FROM (\n");
        
        if (rawQuery != null && !rawQuery.trim().isEmpty()) {
            StringBuilder pq = new StringBuilder();
            String[] lines = rawQuery.split("\n");
            for (String line : lines) {
                if (line.contains("$")) {
                    java.util.regex.Matcher lineMatcher = java.util.regex.Pattern.compile("\\$([a-zA-Z0-9_]+)").matcher(line);
                    java.util.List<String> lineVars = new java.util.ArrayList<>();
                    while (lineMatcher.find()) { lineVars.add(lineMatcher.group(1)); }
                    
                    pq.append("            <if test=\"");
                    for (int i = 0; i < lineVars.size(); i++) {
                        if (i > 0) pq.append(" and ");
                        pq.append(lineVars.get(i)).append(" != null and ").append(lineVars.get(i)).append(" != ''");
                    }
                    pq.append("\">\n");
                    pq.append("                ").append(line.replaceAll("\\$([a-zA-Z0-9_]+)", "#{$1}")).append("\n");
                    pq.append("            </if>\n");
                } else {
                    pq.append("            ").append(line).append("\n");
                }
            }
            sb.append(pq.toString());
        } else {
            sb.append("            SELECT 1 FROM DUAL /* TODO: 조인 쿼리로 수정 */\n");
        }
        
        sb.append("        ) A\n")
          .append("        <include refid=\"searchConditions\"/>\n")
          .append("    </select>\n\n")
          .append("    <select id=\"selectList\" resultType=\"com.example.sms.dto.").append(module).append(".").append(domainClass).append("VO\">\n")
          .append("        SELECT * FROM (\n")
          .append("            SELECT A.*, ROWNUM AS RNUM FROM (\n")
          .append("                SELECT X.* FROM (\n");
        
        if (rawQuery != null && !rawQuery.trim().isEmpty()) {
            String processedQuery = rawQuery.replaceAll("\\$([a-zA-Z0-9_]+)", "#{$1}");
            sb.append("                    ").append(processedQuery.replaceAll("\n", "\n                    ")).append("\n");
        } else {
            sb.append("                    SELECT 1 AS example FROM DUAL /* TODO: 다중 조인 쿼리 작성 */\n");
        }

        sb.append("                ) X\n")
          .append("                <include refid=\"searchConditions\"/>\n")
          .append("            ) A WHERE ROWNUM <![CDATA[<=]]> #{offset} + #{size}\n")
          .append("        ) WHERE RNUM > #{offset}\n")
          .append("    </select>\n");

        if (includeCud) {
            sb.append("\n    <insert id=\"insert\">\n")
              .append("        INSERT INTO /* TODO: 테이블명 */ (REG_DT) VALUES (SYSDATE)\n")
              .append("    </insert>\n\n")
              .append("    <update id=\"update\">\n")
              .append("        UPDATE /* TODO: 테이블명 */ SET MOD_DT = SYSDATE WHERE ID = #{id}\n")
              .append("    </update>\n\n")
              .append("    <delete id=\"delete\">\n")
              .append("        DELETE FROM /* TODO: 테이블명 */ WHERE ID = #{id}\n")
              .append("    </delete>\n");
        }
        if (includeExcel) {
            sb.append("\n    <select id=\"selectListForExcel\" resultType=\"java.util.HashMap\">\n");
            if (rawQuery != null && !rawQuery.trim().isEmpty()) {
                String processedQuery = rawQuery.replaceAll("\\$([a-zA-Z0-9_]+)", "#{$1}");
                sb.append("        ").append(processedQuery.replaceAll("\n", "\n        ")).append("\n");
                sb.append("        <include refid=\"searchConditions\"/>\n");
                if (rawQuery.toUpperCase().contains("ORDER BY")) {
                    int orderByIndex = rawQuery.toUpperCase().indexOf("ORDER BY");
                    sb.append("            ").append(rawQuery.substring(orderByIndex)).append("\n");
                }
            } else {
                sb.append("        SELECT 1 AS example FROM DUAL\n");
                sb.append("        <include refid=\"searchConditions\"/>\n");
            }
            sb.append("    </select>\n");
        }
        sb.append("\n</mapper>\n");
        return sb.toString();
    }

    private String getServiceTemplate(String module, String domainClass, boolean includeCud, boolean includeExcel, String[] cols) {
        StringBuilder sb = new StringBuilder();
        sb.append("package com.example.sms.service.").append(module).append(";\n\n")
          .append("import com.example.sms.dto.common.PageResponseDTO;\n")
          .append("import com.example.sms.dto.").append(module).append(".").append(domainClass).append("SearchRequestDTO;\n")
          .append("import com.example.sms.dto.").append(module).append(".").append(domainClass).append("VO;\n")
          .append("import com.example.sms.mapper.").append(module).append(".").append(domainClass).append("Mapper;\n")
          .append("import lombok.RequiredArgsConstructor;\n")
          .append("import org.springframework.stereotype.Service;\n")
          .append("import org.springframework.transaction.annotation.Transactional;\n")
          .append("import java.util.List;\n\n")
          .append("@Service\n")
          .append("@RequiredArgsConstructor\n")
          .append("public class ").append(domainClass).append("Service {\n\n")
          .append("    private final ").append(domainClass).append("Mapper mapper;\n\n")
          .append("    /**\n")
          .append("     * 목록 조회 및 페이징 처리\n")
          .append("     */\n")
          .append("    @Transactional(readOnly = true)\n")
          .append("    public PageResponseDTO<").append(domainClass).append("VO> search(").append(domainClass).append("SearchRequestDTO request) {\n")
          .append("        int totalCount = mapper.count(request);\n")
          .append("        List<").append(domainClass).append("VO> list = mapper.selectList(request);\n")
          .append("        return PageResponseDTO.of(list, request, totalCount);\n")
          .append("    }\n");

        if (includeCud) {
            sb.append("\n    /**\n")
              .append("     * 데이터 저장 (신규 등록 및 수정)\n")
              .append("     */\n")
              .append("    @Transactional\n")
              .append("    public void save(").append(domainClass).append("VO vo) {\n")
              .append("        // TODO: 신규/수정 분기 로직 구현\n")
              .append("        mapper.insert(vo);\n")
              .append("    }\n\n")
              .append("    /**\n")
              .append("     * 데이터 삭제\n")
              .append("     */\n")
              .append("    @Transactional\n")
              .append("    public void delete(String id) {\n")
              .append("        mapper.delete(id);\n")
              .append("    }\n");
        }
        if (includeExcel) {
            sb.append("\n    /**\n")
              .append("     * 대용량 엑셀 다운로드 처리\n")
              .append("     */\n")
              .append("    @Transactional(readOnly = true)\n")
              .append("    public void downloadExcel(").append(domainClass).append("SearchRequestDTO request, jakarta.servlet.http.HttpServletResponse response) {\n")
              .append("        String[] headers = {");
            for (int i = 0; i < cols.length; i++) {
                if (i > 0) sb.append(", ");
                sb.append("\"").append(cols[i].trim()).append("\"");
            }
            sb.append("};\n")
              .append("        String[] keys = {");
            for (int i = 0; i < cols.length; i++) {
                if (i > 0) sb.append(", ");
                sb.append("\"").append(cols[i].trim().toUpperCase()).append("\"");
            }
            sb.append("};\n")
              .append("        java.util.List<java.util.Map<String, Object>> list = mapper.selectListForExcel(request);\n")
              .append("        com.example.sms.util.ExcelUtil.downloadExcel(response, \"").append(domainClass).append("_export\", headers, list, keys);\n")
              .append("    }\n");
        }
        sb.append("}\n");
        return sb.toString();
    }

    private String getControllerTemplate(String module, String domainId, String domainClass, boolean includeCud, boolean includeExcel) {
        StringBuilder sb = new StringBuilder();
        sb.append("package com.example.sms.controller.").append(module).append(";\n\n")
          .append("import com.example.sms.dto.common.ApiResponse;\n")
          .append("import com.example.sms.dto.common.PageResponseDTO;\n")
          .append("import com.example.sms.dto.").append(module).append(".").append(domainClass).append("SearchRequestDTO;\n")
          .append("import com.example.sms.dto.").append(module).append(".").append(domainClass).append("VO;\n")
          .append("import com.example.sms.service.").append(module).append(".").append(domainClass).append("Service;\n")
          .append("import lombok.RequiredArgsConstructor;\n")
          .append("import org.springframework.http.ResponseEntity;\n")
          .append("import org.springframework.stereotype.Controller;\n")
          .append("import org.springframework.web.bind.annotation.*;\n\n")
          .append("@Controller\n")
          .append("@RequestMapping(\"/").append(module).append("/").append(domainId).append("\")\n")
          .append("@RequiredArgsConstructor\n")
          .append("public class ").append(domainClass).append("Controller {\n\n")
          .append("    private final ").append(domainClass).append("Service service;\n\n")
          .append("    /**\n")
          .append("     * 화면(HTML) 반환\n")
          .append("     */\n")
          .append("    @GetMapping\n")
          .append("    public String page() {\n")
          .append("        return \"").append(module).append("/").append(domainId).append("-manage\";\n")
          .append("    }\n\n")
          .append("    /**\n")
          .append("     * TUI Grid 용 데이터 API (JSON 반환)\n")
          .append("     */\n")
          .append("    @ResponseBody\n")
          .append("    @GetMapping(\"/data\")\n")
          .append("    public ResponseEntity<ApiResponse<PageResponseDTO<").append(domainClass).append("VO>>> getData(@ModelAttribute ").append(domainClass).append("SearchRequestDTO request) {\n")
          .append("        return ResponseEntity.ok(ApiResponse.success(service.search(request)));\n")
          .append("    }\n");

        if (includeCud) {
            sb.append("\n    /**\n")
              .append("     * 단건 저장/수정 API\n")
              .append("     */\n")
              .append("    @ResponseBody\n")
              .append("    @PostMapping(\"/save\")\n")
              .append("    public ResponseEntity<ApiResponse<String>> save(@RequestBody ").append(domainClass).append("VO vo) {\n")
              .append("        service.save(vo);\n")
              .append("        return ResponseEntity.ok(ApiResponse.success(\"저장되었습니다.\"));\n")
              .append("    }\n\n")
              .append("    /**\n")
              .append("     * 단건 삭제 API\n")
              .append("     */\n")
              .append("    @ResponseBody\n")
              .append("    @PostMapping(\"/delete\")\n")
              .append("    public ResponseEntity<ApiResponse<String>> delete(@RequestParam String id) {\n")
              .append("        service.delete(id);\n")
              .append("        return ResponseEntity.ok(ApiResponse.success(\"삭제되었습니다.\"));\n")
              .append("    }\n");
        }
        if (includeExcel) {
            sb.append("\n    /**\n")
              .append("     * 엑셀 파일 다운로드 요청 처리\n")
              .append("     */\n")
              .append("    @GetMapping(\"/excel\")\n")
              .append("    public void downloadExcel(@ModelAttribute ").append(domainClass).append("SearchRequestDTO request, jakarta.servlet.http.HttpServletResponse response) {\n")
              .append("        service.downloadExcel(request, response);\n")
              .append("    }\n");
        }
        sb.append("}\n");
        return sb.toString();
    }

    private String getHtmlTemplate(String module, String domainId, String domainName, List<String> searchVars, boolean includeExcel, boolean includeExcelGrid) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n")
          .append("<html xmlns:th=\"http://www.thymeleaf.org\"\n")
          .append("      xmlns:layout=\"http://www.ultraq.net.nz/thymeleaf/layout\"\n")
          .append("      layout:decorate=\"~{defaultLayout}\">\n")
          .append("<head>\n")
          .append("    <title>").append(domainName).append("</title>\n")
          .append("</head>\n")
          .append("<body>\n")
          .append("<main layout:fragment=\"content\">\n")
          .append("    <div class=\"content-header\">\n")
          .append("        <h2>").append(domainName).append(" <small style=\"color:#888; font-size:14px; margin-left:10px;\">(").append(module).append(" > ").append(domainId).append(")</small></h2>\n")
          .append("    </div>\n\n")
          .append("    <div class=\"search-section\">\n")
          .append("        <div class=\"search-row\">\n");

        if (searchVars.isEmpty()) {
            sb.append("            <span class=\"search-label\">검색어</span>\n")
              .append("            <input type=\"text\" id=\"searchKeyword\" class=\"search-input\" placeholder=\"검색어 입력\">\n");
        } else {
            for (String var : searchVars) {
                String varLower = var.toLowerCase();
                String inputType = (varLower.contains("date") || varLower.contains("dt") || varLower.endsWith("at")) ? "date" : "text";
                sb.append("            <span class=\"search-label\">").append(var).append("</span>\n")
                  .append("            <input type=\"").append(inputType).append("\" id=\"").append(var).append("\" class=\"search-input\" placeholder=\"입력\" style=\"width:150px; margin-right:15px;\">\n");
            }
        }
        
        sb.append("            \n")
          .append("            <div class=\"search-divider\"></div>\n")
          .append("            \n")
          .append("            <div class=\"search-actions\">\n")
          .append("                <button type=\"button\" class=\"btn btn-primary\" id=\"btn-search\">조회</button>\n")
          .append("                <button type=\"button\" class=\"btn btn-secondary\" id=\"btn-reset\">초기화</button>\n");
        if (includeExcel) {
            sb.append("                <button type=\"button\" class=\"btn btn-success\" id=\"btn-excel\" style=\"margin-left:5px;\">대용량 엑셀 다운로드</button>\n");
        }
        if (includeExcelGrid) {
            sb.append("                <button type=\"button\" class=\"btn btn-info\" id=\"btn-excel-grid\" style=\"margin-left:5px;\">현재 화면 엑셀 다운로드</button>\n");
        }
        sb.append("            </div>\n")
          .append("        </div>\n")
          .append("    </div>\n\n")
          .append("    <div class=\"grid-toolbar\">\n")
          .append("        <span class=\"total-count\">총 <strong>0</strong>건</span>\n")
          .append("        <div class=\"page-size-wrap\">\n")
          .append("            페이지당\n")
          .append("            <select id=\"pageSizeSelect\">\n")
          .append("                <option value=\"10\">10건</option>\n")
          .append("                <option value=\"20\">20건</option>\n")
          .append("                <option value=\"50\">50건</option>\n")
          .append("            </select>\n")
          .append("            <button type=\"button\" class=\"btn btn-success btn-sm\" id=\"btn-create\">+ 신규 등록</button>\n")
          .append("        </div>\n")
          .append("    </div>\n\n")
          .append("    <div id=\"grid\"></div>\n")
          .append("    <div id=\"pagination\" class=\"pagination-wrap\"></div>\n")
          .append("</main>\n\n")
          .append("<th:block layout:fragment=\"script\">\n")
          .append("    <script th:src=\"@{/js/").append(module).append("/").append(domainId).append("-manage.js}\"></script>\n")
          .append("</th:block>\n")
          .append("</body>\n")
          .append("</html>\n");
        return sb.toString();
    }

    private String getJsTemplate(String module, String domainId, String domainName, String[] cols, List<String> searchVars, boolean includeExcel, boolean includeExcelGrid) {
        StringBuilder sb = new StringBuilder();
        sb.append("/**\n")
          .append(" * ").append(domainId).append("-manage.js\n")
          .append(" * ").append(domainName).append(" 화면 스크립트\n")
          .append(" */\n")
          .append("document.addEventListener('DOMContentLoaded', function () {\n")
          .append("    const pageBuilder = new TuiPageBuilder({\n")
          .append("        el: 'grid',\n")
          .append("        apiUrl: '/").append(module).append("/").append(domainId).append("/data',\n");
          
        if (searchVars.isEmpty()) {
            sb.append("        searchInputs: ['searchKeyword'],\n");
        } else {
            sb.append("        searchInputs: [");
            for (int i = 0; i < searchVars.size(); i++) {
                sb.append("'").append(searchVars.get(i)).append("'");
                if (i < searchVars.size() - 1) sb.append(", ");
            }
            sb.append("],\n");
        }
        
        sb.append("        rowHeaders: ['rowNum'],\n")
          .append("        columns: [\n");
        
        if (cols.length == 0) {
            sb.append("            { header: '예시 데이터', name: 'exampleColumn', align: 'center', width: 250 }\n");
        } else {
            for (int i = 0; i < cols.length; i++) {
                if(!cols[i].trim().isEmpty()) {
                    sb.append("            { header: '").append(cols[i].trim()).append("', name: '").append(toCamelCase(cols[i].trim())).append("', align: 'center', width: 150 }");
                    if (i < cols.length - 1) sb.append(",");
                    sb.append("\n");
                }
            }
        }
        sb.append("        ],\n")
          .append("        autoModal: true,\n")
          .append("        autoModalTitle: '").append(domainName).append(" 상세'\n")
          .append("    });\n\n")
          .append("    // 신규 등록 버튼 이벤트 예시\n")
          .append("    document.getElementById('btn-create')?.addEventListener('click', () => {\n")
          .append("        alert('신규 등록 팝업 구현');\n")
          .append("    });\n");

        if (includeExcel) {
            sb.append("\n    // 대용량 엑셀 다운로드 이벤트 (Backend)\n")
              .append("    document.getElementById('btn-excel')?.addEventListener('click', () => {\n");
            if (searchVars.isEmpty()) {
                sb.append("        const qs = `?searchKeyword=${document.getElementById('searchKeyword').value}`;\n");
            } else {
                sb.append("        const qs = `?");
                for (int i = 0; i < searchVars.size(); i++) {
                    if (i > 0) sb.append("&");
                    sb.append(searchVars.get(i)).append("=${document.getElementById('").append(searchVars.get(i)).append("').value}");
                }
                sb.append("`;\n");
            }
            sb.append("        window.location.href = pageBuilder.config.apiUrl.replace('/data', '') + '/excel' + qs;\n")
              .append("    });\n");
        }

        if (includeExcelGrid) {
            sb.append("\n    // 현재 화면 엑셀 다운로드 이벤트 (TUI Grid Native)\n")
              .append("    document.getElementById('btn-excel-grid')?.addEventListener('click', () => {\n")
              .append("        pageBuilder.getGrid().export('xlsx', { fileName: '").append(domainName).append("_화면데이터' });\n")
              .append("    });\n");
        }

        sb.append("});\n");
        return sb.toString();
    }
}
