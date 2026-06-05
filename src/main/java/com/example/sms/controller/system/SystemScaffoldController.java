package com.example.sms.controller.system;

import com.example.sms.dto.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectItem;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Profile("local")
@Controller
@RequestMapping("/system/scaffold")
@RequiredArgsConstructor
public class SystemScaffoldController {

    private final JdbcTemplate jdbcTemplate;

    @GetMapping
    public String scaffoldPage() {
        return "system/scaffold";
    }

    @ResponseBody
    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<Map<String, String>>> generateCode(@RequestBody Map<String, Object> request) {
        String moduleName  = (String) request.get("moduleName");
        String domainId    = (String) request.get("domainId");
        String domainClass = (String) request.get("domainClass");
        String domainName  = (String) request.get("domainName");
        String rawQuery    = (String) request.get("rawQuery");
        boolean includeCud      = Boolean.TRUE.equals(request.get("includeCud"));
        boolean includeExcel    = Boolean.TRUE.equals(request.get("includeExcel"));
        boolean includeExcelGrid= Boolean.TRUE.equals(request.get("includeExcelGrid"));

        if (moduleName == null || domainId == null || domainClass == null || domainName == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "필수 파라미터가 누락되었습니다."));
        }

        try {
            List<String> parsedCols = extractColumnsFromQuery(rawQuery);
            List<String> searchVars = extractSearchVarsFromQuery(rawQuery);
            String[] cols = parsedCols.toArray(new String[0]);

            // ② ResultSetMetaData 타입 추론
            Map<String, String> typeMap = inferColumnTypes(rawQuery, parsedCols);

            // 탭에 실제 파일명 표시
            Map<String, String> results = new LinkedHashMap<>();
            results.put(domainClass + "SearchRequestDTO.java",
                getDtoTemplate(moduleName, domainClass, searchVars));
            results.put(domainClass + "VO.java",
                getVoTemplate(moduleName, domainClass, cols, typeMap));
            results.put(domainClass + "Mapper.java",
                getMapperInterfaceTemplate(moduleName, domainClass, includeCud, includeExcel));
            results.put(domainClass + "Mapper.xml",
                getMapperXmlTemplate(moduleName, domainClass, cols, rawQuery, includeCud, searchVars, includeExcel));
            results.put(domainClass + "Service.java",
                getServiceTemplate(moduleName, domainClass, includeCud, includeExcel, cols));
            results.put(domainClass + "Controller.java",
                getControllerTemplate(moduleName, domainId, domainClass, includeCud, includeExcel));
            results.put(domainId + "-manage.html",
                getHtmlTemplate(moduleName, domainId, domainName, searchVars, includeExcel, includeExcelGrid));
            results.put(domainId + "-manage.js",
                getJsTemplate(moduleName, domainId, domainName, cols, searchVars, includeExcel, includeExcelGrid));
            results.put("메뉴등록.sql",
                getMenuSqlTemplate(moduleName, domainId, domainClass, domainName));
            results.put("docs-api-mapping.md snippet",
                getApiMappingDocSnippet(moduleName, domainId, domainClass, domainName));
            results.put("docs-menu-structure.md snippet",
                getMenuStructureDocSnippet(domainName, searchVars, cols));

            return ResponseEntity.ok(ApiResponse.success(results));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error(500, "생성 실패: " + e.getMessage()));
        }
    }

    @ResponseBody
    @PostMapping("/generate-excel-snippet")
    public ResponseEntity<ApiResponse<Map<String, String>>> generateExcelSnippet(@RequestBody Map<String, Object> request) {
        String moduleName  = (String) request.get("moduleName");
        String domainClass = (String) request.get("domainClass");
        String rawQuery    = (String) request.get("rawQuery");

        List<String> searchVars = extractSearchVarsFromQuery(rawQuery);
        List<String> parsedCols = extractColumnsFromQuery(rawQuery);
        String[] cols = parsedCols.toArray(new String[0]);

        String controllerSnippet =
            "    @GetMapping(\"/excel\")\n" +
            "    public void downloadExcel(@ModelAttribute " + domainClass + "SearchRequestDTO request, jakarta.servlet.http.HttpServletResponse response) {\n" +
            "        service.downloadExcel(request, response);\n" +
            "    }";

        StringBuilder serviceSnippetSb = new StringBuilder();
        serviceSnippetSb.append("    @Transactional(readOnly = true)\n")
            .append("    public void downloadExcel(").append(domainClass).append("SearchRequestDTO request, jakarta.servlet.http.HttpServletResponse response) {\n")
            .append("        String[] headers = {");
        for (int i = 0; i < cols.length; i++) {
            if (i > 0) serviceSnippetSb.append(", ");
            serviceSnippetSb.append("\"").append(cols[i].trim()).append("\"");
        }
        serviceSnippetSb.append("};\n")
            .append("        String[] keys = {");
        for (int i = 0; i < cols.length; i++) {
            if (i > 0) serviceSnippetSb.append(", ");
            serviceSnippetSb.append("\"").append(cols[i].trim().toUpperCase()).append("\"");
        }
        serviceSnippetSb.append("};\n")
            .append("        java.util.List<java.util.Map<String, Object>> list = mapper.selectListForExcel(request);\n")
            .append("        com.example.sms.util.ExcelUtil.downloadExcel(response, \"").append(domainClass).append("_export\", headers, list, keys);\n")
            .append("    }");

        String mapperSnippet =
            "    java.util.List<java.util.Map<String, Object>> selectListForExcel(" + domainClass + "SearchRequestDTO request);";

        StringBuilder xmlSnippet = new StringBuilder();
        xmlSnippet.append("    <select id=\"selectListForExcel\" resultType=\"java.util.HashMap\">\n");
        xmlSnippet.append("        /* TODO: 페이징 없는 순수 데이터 조회 쿼리를 작성하세요 */\n");
        xmlSnippet.append("        <include refid=\"searchConditions\"/>\n");
        xmlSnippet.append("    </select>");

        String htmlSnippet =
            "                <button type=\"button\" class=\"btn btn-success\" id=\"btn-excel\">엑셀 다운로드</button>";

        StringBuilder jsSnippet = new StringBuilder();
        jsSnippet.append("    document.querySelector('#btn-excel')?.addEventListener('click', () => {\n");
        if (searchVars.isEmpty()) {
            jsSnippet.append("        const qs = `?searchKeyword=${document.querySelector('#searchKeyword').value}`;\n");
        } else {
            jsSnippet.append("        const qs = `?");
            for (int i = 0; i < searchVars.size(); i++) {
                if (i > 0) jsSnippet.append("&");
                jsSnippet.append(searchVars.get(i)).append("=${document.querySelector('#").append(searchVars.get(i)).append("').value}");
            }
            jsSnippet.append("`;\n");
        }
        jsSnippet.append("        window.location.href = pageBuilder.config.apiUrl.replace('/data', '') + '/excel' + qs;\n");
        jsSnippet.append("    });");

        return ResponseEntity.ok(ApiResponse.success(Map.of(
            "Controller (.java)", controllerSnippet,
            "Service (.java)", serviceSnippetSb.toString(),
            "Mapper (.java)", mapperSnippet,
            "XML (.xml)", xmlSnippet.toString(),
            "HTML (.html)", htmlSnippet,
            "JavaScript (.js)", jsSnippet.toString()
        )));
    }

    // =========================================================================
    // QUERY HELPERS
    // =========================================================================

    /**
     * ResultSetMetaData 기반 컬럼 타입 추론.
     */
    private Map<String, String> inferColumnTypes(String rawQuery, List<String> columns) {
        Map<String, String> typeMap = new LinkedHashMap<>();
        columns.forEach(c -> typeMap.put(c, "String"));

        if (rawQuery == null || rawQuery.trim().isEmpty() || columns.isEmpty()) return typeMap;

        String safeQuery = rawQuery
            .replaceAll("(?i)<[^>]+>", " ")
            .replaceAll("#\\{[^}]+\\}", "NULL")
            .replaceAll("\\$\\{[^}]+\\}", "NULL")
            .replaceAll("\\$([a-zA-Z0-9_]+)", "NULL")
            .replaceAll("(?m)^\\s*AND\\s*$", "")
            .replaceAll("(?m)^\\s*OR\\s*$", "")
            .replaceAll("(?i)WHERE\\s+(AND|OR)\\b", "WHERE 1=1")
            .trim();
        String wrappedQuery = "SELECT * FROM (" + safeQuery + ") WHERE ROWNUM = 0";

        try {
            jdbcTemplate.execute((Connection conn) -> {
                try (PreparedStatement ps = conn.prepareStatement(wrappedQuery);
                     ResultSet rs = ps.executeQuery()) {
                    ResultSetMetaData meta = rs.getMetaData();
                    for (int i = 1; i <= meta.getColumnCount(); i++) {
                        String label     = meta.getColumnLabel(i);
                        String oraType   = meta.getColumnTypeName(i);
                        int    precision = meta.getPrecision(i);
                        int    scale     = meta.getScale(i);
                        String javaType  = oracleTypeToJava(oraType, precision, scale);
                        String matched   = findMatchingColumn(columns, label);
                        if (matched != null) typeMap.put(matched, javaType);
                    }
                }
                return null;
            });
            log.debug("[Scaffold] 타입 추론 성공: {}", typeMap);
        } catch (Exception e) {
            log.warn("[Scaffold] 타입 추론 실패 → 전부 String 사용. 원인: {}", e.getMessage());
        }
        return typeMap;
    }

    private String oracleTypeToJava(String oracleType, int precision, int scale) {
        return switch (oracleType.toUpperCase().split("\\(")[0].trim()) {
            case "NUMBER"  -> scale > 0 ? "BigDecimal" : (precision > 9 ? "Long" : "Integer");
            case "DATE"    -> "LocalDate";
            case "TIMESTAMP", "TIMESTAMP WITH TIME ZONE",
                 "TIMESTAMP WITH LOCAL TIME ZONE" -> "LocalDateTime";
            case "CLOB", "NCLOB" -> "String";
            case "BLOB"          -> "byte[]";
            default              -> "String";
        };
    }

    private String findMatchingColumn(List<String> columns, String label) {
        for (String col : columns) {
            if (col.equalsIgnoreCase(label)) return col;
        }
        return null;
    }

    /**
     * JSQLParser 기반 컬럼 추출. 파싱 실패 시 정규식 fallback.
     */
    private List<String> extractColumnsFromQuery(String query) {
        if (query == null || query.trim().isEmpty()) return new ArrayList<>();

        String safeQuery = query
            .replaceAll("#\\{[^}]+\\}", "NULL")
            .replaceAll("\\$\\{[^}]+\\}", "NULL")
            .replaceAll("\\$([a-zA-Z0-9_]+)", "NULL");

        try {
            net.sf.jsqlparser.statement.Statement stmt =
                CCJSqlParserUtil.parse(safeQuery);

            PlainSelect select;
            if (stmt instanceof net.sf.jsqlparser.statement.select.Select s
                && s.getPlainSelect() != null) {
                select = s.getPlainSelect();
            } else {
                throw new Exception("PlainSelect 구조 아님");
            }

            List<String> columns = new ArrayList<>();
            for (SelectItem<?> item : select.getSelectItems()) {
                String alias = item.getAlias() != null ? item.getAlias().getName() : null;
                if (alias != null) {
                    columns.add(alias.replaceAll("^\"|\"$", ""));
                } else {
                    if (item.getExpression() instanceof Column col) {
                        columns.add(col.getColumnName());
                    } else {
                        String expr = item.getExpression().toString();
                        String[] tokens = expr.split("[^a-zA-Z0-9_]");
                        String last = "";
                        for (String t : tokens) {
                            if (!t.trim().isEmpty()) last = t.trim();
                        }
                        if (!last.isEmpty()) columns.add(last);
                    }
                }
            }
            log.debug("[Scaffold] JSQLParser 파싱 성공 — 컬럼 {}개: {}", columns.size(), columns);
            return columns;

        } catch (Exception e) {
            log.warn("[Scaffold] JSQLParser 파싱 실패, fallback 사용: {}", e.getMessage());
            return extractColumnsFromQueryFallback(query);
        }
    }

    private List<String> extractColumnsFromQueryFallback(String query) {
        List<String> columns = new ArrayList<>();
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
            "SELECT(.*?)\\s+FROM\\s", java.util.regex.Pattern.CASE_INSENSITIVE | java.util.regex.Pattern.DOTALL);
        java.util.regex.Matcher matcher = pattern.matcher(query);
        if (matcher.find()) {
            String selectPart = matcher.group(1);
            String[] parts = selectPart.split(",");
            for (String part : parts) {
                part = part.trim();
                if (part.isEmpty()) continue;
                String[] tokens = part.split("\\s+");
                String colName = tokens[tokens.length - 1];
                if (colName.contains(".")) colName = colName.substring(colName.lastIndexOf(".") + 1);
                columns.add(colName);
            }
        }
        return columns;
    }

    private List<String> extractSearchVarsFromQuery(String query) {
        List<String> vars = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) return vars;
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\$([a-zA-Z0-9_]+)");
        java.util.regex.Matcher matcher = pattern.matcher(query);
        while (matcher.find()) {
            // $base_dt → baseDt (camelCase) : map-underscore-to-camel-case 설정과 일치시킴
            String varName = toCamelCase(matcher.group(1));
            if (!vars.contains(varName)) vars.add(varName);
        }
        return vars;
    }

    private String processRawQueryLines(String rawQuery, String indent) {
        StringBuilder pq = new StringBuilder();
        String[] lines = rawQuery.split("\n");
        for (String line : lines) {
            if (line.contains("$")) {
                java.util.regex.Matcher lineMatcher =
                    java.util.regex.Pattern.compile("\\$([a-zA-Z0-9_]+)").matcher(line);
                List<String> lineVars = new ArrayList<>();
                // $base_dt → baseDt (camelCase) 로 변환
                while (lineMatcher.find()) lineVars.add(toCamelCase(lineMatcher.group(1)));

                pq.append(indent).append("    <if test=\"");
                for (int i = 0; i < lineVars.size(); i++) {
                    if (i > 0) pq.append(" and ");
                    pq.append(lineVars.get(i)).append(" != null and ").append(lineVars.get(i)).append(" != ''");
                }
                pq.append("\">\n");

                // $base_dt → #{baseDt} (camelCase) 로 치환
                String replacedLine = java.util.regex.Pattern.compile("\\$([a-zA-Z0-9_]+)")
                    .matcher(line)
                    .replaceAll(mr -> "#{" + toCamelCase(mr.group(1)) + "}");
                pq.append(indent).append("        ").append(replacedLine).append("\n");
                pq.append(indent).append("    </if>\n");
            } else {
                pq.append(indent).append(line).append("\n");
            }
        }
        return pq.toString();
    }

    private String toCamelCase(String s) {
        String[] parts = s.toLowerCase().split("_");
        StringBuilder sb = new StringBuilder(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            sb.append(Character.toUpperCase(parts[i].charAt(0))).append(parts[i].substring(1));
        }
        return sb.toString();
    }

    // =========================================================================
    // TEMPLATES
    // =========================================================================

    private String getMenuSqlTemplate(String module, String domainId, String domainClass, String domainName) {
        String menuCd   = ("M_" + module + "_" + domainId).toUpperCase().replace("-", "_");
        String parentCd = ("M_" + module).toUpperCase();
        String menuUrl  = "/" + module + "/" + domainId;

        return "-- ============================================================\n"
             + "-- 메뉴 등록 SQL  ( " + domainName + " )\n"
             + "-- ============================================================\n\n"
             + "-- 1) DB 직접 실행용\n"
             + "INSERT INTO TB_MENU (MENU_CD, MENU_NM, MENU_URL, UP_MENU_CD, SORT_ORD, REG_ID, USE_YN)\n"
             + "VALUES ('" + menuCd + "', '" + domainName + "', '" + menuUrl + "', '" + parentCd + "', 99, 'SYSTEM', 'Y');\n\n"
             + "INSERT INTO TB_MENU_AUTH (MENU_CD, AUTH_CD, CAN_READ, CAN_WRITE, CAN_APPROVE, CAN_EXCEL, REG_ID)\n"
             + "VALUES ('" + menuCd + "', 'ROLE_ADMIN', 'Y', 'Y', 'Y', 'Y', 'SYSTEM');\n\n"
             + "COMMIT;\n\n"
             + "-- 2) 파일 배치 경로\n"
             + "-- src/main/java/.../dto/"        + module + "/" + domainClass + "SearchRequestDTO.java\n"
             + "-- src/main/java/.../vo/"         + module + "/" + domainClass + "VO.java\n"
             + "-- src/main/java/.../mapper/"     + module + "/" + domainClass + "Mapper.java\n"
             + "-- src/main/java/.../service/"    + module + "/" + domainClass + "Service.java\n"
             + "-- src/main/java/.../controller/" + module + "/" + domainClass + "Controller.java\n"
             + "-- src/main/resources/mapper/"    + module + "/" + domainClass + "Mapper.xml\n"
             + "-- src/main/resources/templates/" + module + "/" + domainId + "-manage.html\n"
             + "-- src/main/resources/static/js/" + module + "/" + domainId + "-manage.js\n";
    }

    private String getApiMappingDocSnippet(String module, String domainId, String domainClass, String domainName) {
        String url = "/" + module + "/" + domainId;
        return "| " + domainName + " | ⚠️ | `" + url + "` | `" + domainClass + "Controller` | `"
            + module + "/" + domainId + "-manage.html` |\n"
            + "\n"
            + "- 데이터 API: `GET " + url + "/data`\n"
            + "- JavaScript: `static/js/" + module + "/" + domainId + "-manage.js`\n"
            + "- 상태: 스캐폴드 생성 직후이므로 실제 DB 매핑/업무 로직 확인 필요\n";
    }

    private String getMenuStructureDocSnippet(String domainName, List<String> searchVars, String[] cols) {
        String searchText = searchVars.isEmpty()
            ? "검색어"
            : searchVars.stream().collect(Collectors.joining(", "));
        String gridText = cols.length == 0
            ? "미정의"
            : java.util.Arrays.stream(cols)
                .filter(c -> c != null && !c.trim().isEmpty())
                .map(String::trim)
                .collect(Collectors.joining(", "));

        return "### " + domainName + "\n\n"
            + "| 항목 | 내용 |\n"
            + "|------|------|\n"
            + "| **조회조건** | " + searchText + " |\n"
            + "| **데이터 필드** | " + gridText + " |\n"
            + "\n"
            + "> 상태: 스캐폴드 생성 기준. 실제 DB 컬럼 확인 후 보정 필요.\n";
    }

    private String getDtoTemplate(String module, String domainClass, List<String> searchVars) {
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
            for (String var : searchVars) sb.append("    private String ").append(var).append(";\n");
        }
        sb.append("}\n");
        return sb.toString();
    }

    private String getVoTemplate(String module, String domainClass, String[] cols, Map<String, String> typeMap) {
        boolean hasLocalDate     = typeMap.values().stream().anyMatch(t -> t.equals("LocalDate"));
        boolean hasLocalDateTime = typeMap.values().stream().anyMatch(t -> t.equals("LocalDateTime"));
        boolean hasBigDecimal    = typeMap.values().stream().anyMatch(t -> t.equals("BigDecimal"));

        StringBuilder sb = new StringBuilder();
        sb.append("package com.example.sms.vo.").append(module).append(";\n\n");
        if (hasLocalDate)     sb.append("import java.time.LocalDate;\n");
        if (hasLocalDateTime) sb.append("import java.time.LocalDateTime;\n");
        if (hasBigDecimal)    sb.append("import java.math.BigDecimal;\n");
        sb.append("import jakarta.validation.constraints.NotBlank;\n")
          .append("import jakarta.validation.constraints.Size;\n")
          .append("import lombok.Data;\n\n")
          .append("@Data\n")
          .append("public class ").append(domainClass).append("VO {\n")
          .append("    private long rowNum;\n");
        for (String c : cols) {
            if (!c.trim().isEmpty()) {
                String javaType = typeMap.getOrDefault(c, "String");
                sb.append("    private ").append(javaType).append(" ").append(toCamelCase(c.trim())).append(";\n");
            }
        }
        sb.append("}\n");
        return sb.toString();
    }

    private String getMapperInterfaceTemplate(String module, String domainClass, boolean includeCud, boolean includeExcel) {
        StringBuilder sb = new StringBuilder();
        sb.append("package com.example.sms.mapper.").append(module).append(";\n\n")
          .append("import com.example.sms.dto.").append(module).append(".").append(domainClass).append("SearchRequestDTO;\n")
          .append("import com.example.sms.vo.").append(module).append(".").append(domainClass).append("VO;\n")
          .append("import org.apache.ibatis.annotations.Mapper;\n")
          .append("import java.util.List;\n")
          .append("import java.util.Map;\n\n")
          .append("@Mapper\n")
          .append("public interface ").append(domainClass).append("Mapper {\n\n")
          .append("    int count(").append(domainClass).append("SearchRequestDTO request);\n\n")
          .append("    List<").append(domainClass).append("VO> selectList(").append(domainClass).append("SearchRequestDTO request);\n");
        if (includeCud) {
            sb.append("\n    int insert(").append(domainClass).append("VO vo);\n\n")
              .append("    int update(").append(domainClass).append("VO vo);\n\n")
              .append("    int delete(String id);\n");
        }
        if (includeExcel) {
            sb.append("\n    List<Map<String, Object>> selectListForExcel(").append(domainClass).append("SearchRequestDTO request);\n");
        }
        sb.append("}\n");
        return sb.toString();
    }

    private String getMapperXmlTemplate(String module, String domainClass, String[] cols, String rawQuery,
                                         boolean includeCud, List<String> searchVars, boolean includeExcel) {
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
          .append("    </sql>\n\n");

        sb.append("    <select id=\"count\" resultType=\"int\">\n")
          .append("        SELECT COUNT(1) FROM (\n");
        if (rawQuery != null && !rawQuery.trim().isEmpty()) {
            sb.append(processRawQueryLines(rawQuery, "        "));
        } else {
            sb.append("            SELECT 1 FROM DUAL /* TODO: 조인 쿼리로 수정 */\n");
        }
        sb.append("        ) A\n")
          .append("        <include refid=\"searchConditions\"/>\n")
          .append("    </select>\n\n");

        sb.append("    <select id=\"selectList\" resultType=\"com.example.sms.vo.").append(module).append(".").append(domainClass).append("VO\">\n")
          .append("        SELECT A.*\n")
          .append("        FROM (\n");
        if (rawQuery != null && !rawQuery.trim().isEmpty()) {
            sb.append(processRawQueryLines(rawQuery, "        "));
        } else {
            sb.append("            SELECT 1 AS example FROM DUAL /* TODO: 조회 쿼리 작성 */\n");
        }
        sb.append("        ) A\n")
          .append("        <include refid=\"searchConditions\"/>\n")
          .append("        ORDER BY A.ROWID DESC /* TODO: 실제 정렬 컬럼으로 교체 (결정적 정렬 필수) */\n")
          .append("        OFFSET #{offset} ROWS FETCH NEXT #{size} ROWS ONLY\n")
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
          .append("import com.example.sms.vo.").append(module).append(".").append(domainClass).append("VO;\n")
          .append("import com.example.sms.mapper.").append(module).append(".").append(domainClass).append("Mapper;\n")
          .append("import lombok.RequiredArgsConstructor;\n")
          .append("import org.springframework.stereotype.Service;\n")
          .append("import org.springframework.transaction.annotation.Transactional;\n")
          .append("import java.util.List;\n\n")
          .append("@Service\n")
          .append("@RequiredArgsConstructor\n")
          .append("public class ").append(domainClass).append("Service {\n\n")
          .append("    private final ").append(domainClass).append("Mapper mapper;\n\n")
          .append("    @Transactional(readOnly = true)\n")
          .append("    public PageResponseDTO<").append(domainClass).append("VO> search(").append(domainClass).append("SearchRequestDTO request) {\n")
          .append("        int totalCount = mapper.count(request);\n")
          .append("        List<").append(domainClass).append("VO> list = mapper.selectList(request);\n")
          .append("        return PageResponseDTO.of(list, request, totalCount);\n")
          .append("    }\n");
        if (includeCud) {
            sb.append("\n    @Transactional\n")
              .append("    public void save(").append(domainClass).append("VO vo) {\n")
              .append("        // TODO: 신규/수정 분기 로직 구현\n")
              .append("        mapper.insert(vo);\n")
              .append("    }\n\n")
              .append("    @Transactional\n")
              .append("    public void delete(String id) {\n")
              .append("        mapper.delete(id);\n")
              .append("    }\n");
        }
        if (includeExcel) {
            sb.append("\n    @Transactional(readOnly = true)\n")
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
          .append("import com.example.sms.vo.").append(module).append(".").append(domainClass).append("VO;\n")
          .append("import com.example.sms.service.").append(module).append(".").append(domainClass).append("Service;\n")
          .append("import jakarta.validation.Valid;\n")
          .append("import lombok.RequiredArgsConstructor;\n")
          .append("import org.springframework.http.ResponseEntity;\n")
          .append("import org.springframework.stereotype.Controller;\n")
          .append("import org.springframework.web.bind.annotation.*;\n\n")
          .append("@Controller\n")
          .append("@RequestMapping(\"/").append(module).append("/").append(domainId).append("\")\n")
          .append("@RequiredArgsConstructor\n")
          .append("public class ").append(domainClass).append("Controller {\n\n")
          .append("    private final ").append(domainClass).append("Service service;\n\n")
          .append("    @GetMapping\n")
          .append("    public String page() {\n")
          .append("        return \"").append(module).append("/").append(domainId).append("-manage\";\n")
          .append("    }\n\n")
          .append("    @ResponseBody\n")
          .append("    @GetMapping(\"/data\")\n")
          .append("    public ResponseEntity<ApiResponse<PageResponseDTO<").append(domainClass).append("VO>>> getData(@ModelAttribute ").append(domainClass).append("SearchRequestDTO request) {\n")
          .append("        return ResponseEntity.ok(ApiResponse.success(service.search(request)));\n")
          .append("    }\n");
        if (includeCud) {
            sb.append("\n    @ResponseBody\n")
              .append("    @PostMapping(\"/save\")\n")
              .append("    public ResponseEntity<ApiResponse<String>> save(@Valid @RequestBody ").append(domainClass).append("VO vo) {\n")
              .append("        service.save(vo);\n")
              .append("        return ResponseEntity.ok(ApiResponse.success(\"저장되었습니다.\"));\n")
              .append("    }\n\n")
              .append("    @ResponseBody\n")
              .append("    @PostMapping(\"/delete\")\n")
              .append("    public ResponseEntity<ApiResponse<String>> delete(@RequestParam String id) {\n")
              .append("        service.delete(id);\n")
              .append("        return ResponseEntity.ok(ApiResponse.success(\"삭제되었습니다.\"));\n")
              .append("    }\n");
        }
        if (includeExcel) {
            sb.append("\n    @GetMapping(\"/excel\")\n")
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
        // 날짜 필드 감지
        List<String> dateVars = searchVars.stream()
            .filter(v -> { String l = v.toLowerCase();
                return l.contains("date") || l.contains("dt") || l.endsWith("at"); })
            .collect(Collectors.toList());

        StringBuilder sb = new StringBuilder();
        sb.append("document.addEventListener('DOMContentLoaded', function () {\n");

        if (!dateVars.isEmpty()) {
            sb.append("    // 날짜 필드 당일 기본값 셋팅\n")
              .append("    const today = new Date().toISOString().split('T')[0];\n");
            for (String dv : dateVars) {
                sb.append("    const _el").append(dv).append(" = document.querySelector('#").append(dv).append("');\n")
                  .append("    if (_el").append(dv).append(" && !_el").append(dv).append(".value) {\n")
                  .append("        _el").append(dv).append(".value = _el").append(dv).append(".type === 'datetime-local' ? today + 'T00:00' : today;\n")
                  .append("    }\n");
            }
            sb.append("\n");
        }

        sb.append("    const pageBuilder = new TuiPageBuilder({\n")
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
                if (!cols[i].trim().isEmpty()) {
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
          .append("    document.querySelector('#btn-create')?.addEventListener('click', () => {\n")
          .append("        alert('신규 등록 팝업 구현');\n")
          .append("    });\n");

        if (includeExcel) {
            sb.append("\n    document.querySelector('#btn-excel')?.addEventListener('click', () => {\n");
            if (searchVars.isEmpty()) {
                sb.append("        const qs = `?searchKeyword=${document.querySelector('#searchKeyword').value}`;\n");
            } else {
                sb.append("        const qs = `?");
                for (int i = 0; i < searchVars.size(); i++) {
                    if (i > 0) sb.append("&");
                    sb.append(searchVars.get(i)).append("=${document.querySelector('#").append(searchVars.get(i)).append("').value}");
                }
                sb.append("`;\n");
            }
            sb.append("        window.location.href = pageBuilder.config.apiUrl.replace('/data', '') + '/excel' + qs;\n")
              .append("    });\n");
        }

        if (includeExcelGrid) {
            sb.append("\n    document.querySelector('#btn-excel-grid')?.addEventListener('click', () => {\n")
              .append("        pageBuilder.getGrid().export('xlsx', { fileName: '").append(domainName).append("_화면데이터' });\n")
              .append("    });\n");
        }

        sb.append("});\n");
        return sb.toString();
    }
}
