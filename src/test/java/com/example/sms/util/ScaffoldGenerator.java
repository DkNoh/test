package com.example.sms.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 프론트엔드/백엔드 표준 템플릿 코드 자동 생성기 (Scaffolding Tool)
 * 주니어 개발자가 신규 화면을 생성할 때 반복되는 보일러플레이트 코드를 최소화하기 위해 사용합니다.
 * 
 * [실행 방법]
 * 1. 하단의 CONFIGURATION 영역에 생성할 모듈과 도메인 정보를 세팅합니다.
 * 2. IDE(Eclipse/IntelliJ)에서 이 클래스를 Run(실행) 합니다.
 * 3. 지정된 경로에 DTO, VO, Mapper, Service, Controller, HTML, JS 파일이 자동 생성됩니다.
 */
public class ScaffoldGenerator {

    // =========================================================================
    // 1. CONFIGURATION (이 부분만 수정하고 실행하세요)
    // =========================================================================
    private static final String MODULE_NAME   = "sample";         // 모듈 경로 (예: system, campaign)
    private static final String DOMAIN_ID     = "test";           // 도메인 식별자 (소문자, URL 및 HTML/JS 파일명)
    private static final String DOMAIN_CLASS  = "Test";           // 도메인 클래스명 (PascalCase, Java 클래스 접두사)
    private static final String DOMAIN_NAME   = "테스트 조회";    // 화면 및 주석에 표기될 한글 명칭

    // 프로젝트 루트 절대/상대 경로 (보통 System.getProperty("user.dir") 사용)
    private static final String BASE_DIR = System.getProperty("user.dir");
    // =========================================================================


    public static void main(String[] args) {
        System.out.println("🚀 [" + DOMAIN_NAME + "] 스캐폴딩 생성을 시작합니다...");

        try {
            createJavaFile("dto/" + MODULE_NAME, DOMAIN_CLASS + "SearchRequestDTO.java", getDtoTemplate());
            createJavaFile("dto/" + MODULE_NAME, DOMAIN_CLASS + "VO.java", getVoTemplate());
            createJavaFile("mapper/" + MODULE_NAME, DOMAIN_CLASS + "Mapper.java", getMapperInterfaceTemplate());
            createFile("src/main/resources/mapper/" + MODULE_NAME, DOMAIN_CLASS + "Mapper.xml", getMapperXmlTemplate());
            createJavaFile("service", DOMAIN_CLASS + "Service.java", getServiceTemplate());
            createJavaFile("controller", DOMAIN_CLASS + "Controller.java", getControllerTemplate());
            createFile("src/main/resources/templates/" + MODULE_NAME, DOMAIN_ID + "-manage.html", getHtmlTemplate());
            createFile("src/main/resources/static/js/" + MODULE_NAME, DOMAIN_ID + "-manage.js", getJsTemplate());

            System.out.println("\n✅ 모든 파일 생성이 완료되었습니다. 프로젝트를 새로고침(Refresh)해 주세요.");
            System.out.println("👉 접속 URL: http://localhost:8080/" + MODULE_NAME + "/" + DOMAIN_ID);

        } catch (Exception e) {
            System.err.println("❌ 파일 생성 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createJavaFile(String subPackage, String fileName, String content) throws IOException {
        String path = "src/main/java/com/example/sms/" + subPackage;
        createFile(path, fileName, content);
    }

    private static void createFile(String path, String fileName, String content) throws IOException {
        File dir = new File(BASE_DIR, path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, fileName);
        if (file.exists()) {
            System.out.println("⚠️ 기 존재하여 스킵됨: " + file.getAbsolutePath());
            return;
        }
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
            System.out.println("✔️ 생성 완료: " + file.getAbsolutePath());
        }
    }

    // =========================================================================
    // 2. TEMPLATES
    // =========================================================================

    private static String getDtoTemplate() {
        return "package com.example.sms.dto." + MODULE_NAME + ";\n\n" +
               "import com.example.sms.dto.common.PageRequestDTO;\n" +
               "import lombok.Data;\n" +
               "import lombok.EqualsAndHashCode;\n\n" +
               "@Data\n" +
               "@EqualsAndHashCode(callSuper = true)\n" +
               "public class " + DOMAIN_CLASS + "SearchRequestDTO extends PageRequestDTO {\n" +
               "    // TODO: 검색 조건 필드 추가 (예: private String searchKeyword;)\n" +
               "}\n";
    }

    private static String getVoTemplate() {
        return "package com.example.sms.dto." + MODULE_NAME + ";\n\n" +
               "import lombok.Data;\n\n" +
               "@Data\n" +
               "public class " + DOMAIN_CLASS + "VO {\n" +
               "    // TODO: 조회 결과 필드 추가 (예: private String id;)\n" +
               "    private long rowNum;\n" +
               "}\n";
    }

    private static String getMapperInterfaceTemplate() {
        return "package com.example.sms.mapper." + MODULE_NAME + ";\n\n" +
               "import com.example.sms.dto." + MODULE_NAME + "." + DOMAIN_CLASS + "SearchRequestDTO;\n" +
               "import com.example.sms.dto." + MODULE_NAME + "." + DOMAIN_CLASS + "VO;\n" +
               "import org.apache.ibatis.annotations.Mapper;\n" +
               "import java.util.List;\n\n" +
               "@Mapper\n" +
               "public interface " + DOMAIN_CLASS + "Mapper {\n" +
               "    int count(" + DOMAIN_CLASS + "SearchRequestDTO request);\n" +
               "    List<" + DOMAIN_CLASS + "VO> selectList(" + DOMAIN_CLASS + "SearchRequestDTO request);\n" +
               "}\n";
    }

    private static String getMapperXmlTemplate() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
               "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n" +
               "<mapper namespace=\"com.example.sms.mapper." + MODULE_NAME + "." + DOMAIN_CLASS + "Mapper\">\n\n" +
               "    <sql id=\"searchConditions\">\n" +
               "        <where>\n" +
               "            -- TODO: 검색 조건 쿼리 작성\n" +
               "        </where>\n" +
               "    </sql>\n\n" +
               "    <select id=\"count\" resultType=\"int\">\n" +
               "        SELECT COUNT(1)\n" +
               "        FROM DUAL /* TODO: 테이블명 수정 */\n" +
               "        <include refid=\"searchConditions\"/>\n" +
               "    </select>\n\n" +
               "    <select id=\"selectList\" resultType=\"com.example.sms.dto." + MODULE_NAME + "." + DOMAIN_CLASS + "VO\">\n" +
               "        SELECT * FROM (\n" +
               "            SELECT A.*, ROWNUM AS RNUM FROM (\n" +
               "                SELECT \n" +
               "                    1 AS rowNum /* TODO: 컬럼명 매핑 */\n" +
               "                FROM DUAL /* TODO: 테이블명 수정 */\n" +
               "                <include refid=\"searchConditions\"/>\n" +
               "                ORDER BY 1 DESC\n" +
               "            ) A WHERE ROWNUM <![CDATA[<=]]> #{offset} + #{size}\n" +
               "        ) WHERE RNUM > #{offset}\n" +
               "    </select>\n\n" +
               "</mapper>\n";
    }

    private static String getServiceTemplate() {
        return "package com.example.sms.service;\n\n" +
               "import com.example.sms.dto.common.PageResponseDTO;\n" +
               "import com.example.sms.dto." + MODULE_NAME + "." + DOMAIN_CLASS + "SearchRequestDTO;\n" +
               "import com.example.sms.dto." + MODULE_NAME + "." + DOMAIN_CLASS + "VO;\n" +
               "import com.example.sms.mapper." + MODULE_NAME + "." + DOMAIN_CLASS + "Mapper;\n" +
               "import lombok.RequiredArgsConstructor;\n" +
               "import org.springframework.stereotype.Service;\n" +
               "import org.springframework.transaction.annotation.Transactional;\n" +
               "import java.util.List;\n\n" +
               "@Service\n" +
               "@RequiredArgsConstructor\n" +
               "public class " + DOMAIN_CLASS + "Service {\n\n" +
               "    private final " + DOMAIN_CLASS + "Mapper mapper;\n\n" +
               "    @Transactional(readOnly = true)\n" +
               "    public PageResponseDTO<" + DOMAIN_CLASS + "VO> search(" + DOMAIN_CLASS + "SearchRequestDTO request) {\n" +
               "        int totalCount = mapper.count(request);\n" +
               "        List<" + DOMAIN_CLASS + "VO> list = mapper.selectList(request);\n" +
               "        return PageResponseDTO.of(list, request, totalCount);\n" +
               "    }\n" +
               "}\n";
    }

    private static String getControllerTemplate() {
        return "package com.example.sms.controller;\n\n" +
               "import com.example.sms.dto.common.ApiResponse;\n" +
               "import com.example.sms.dto.common.PageResponseDTO;\n" +
               "import com.example.sms.dto." + MODULE_NAME + "." + DOMAIN_CLASS + "SearchRequestDTO;\n" +
               "import com.example.sms.dto." + MODULE_NAME + "." + DOMAIN_CLASS + "VO;\n" +
               "import com.example.sms.service." + DOMAIN_CLASS + "Service;\n" +
               "import lombok.RequiredArgsConstructor;\n" +
               "import org.springframework.http.ResponseEntity;\n" +
               "import org.springframework.stereotype.Controller;\n" +
               "import org.springframework.web.bind.annotation.GetMapping;\n" +
               "import org.springframework.web.bind.annotation.ModelAttribute;\n" +
               "import org.springframework.web.bind.annotation.RequestMapping;\n" +
               "import org.springframework.web.bind.annotation.ResponseBody;\n\n" +
               "@Controller\n" +
               "@RequestMapping(\"/" + MODULE_NAME + "/" + DOMAIN_ID + "\")\n" +
               "@RequiredArgsConstructor\n" +
               "public class " + DOMAIN_CLASS + "Controller {\n\n" +
               "    private final " + DOMAIN_CLASS + "Service service;\n\n" +
               "    @GetMapping\n" +
               "    public String page() {\n" +
               "        return \"" + MODULE_NAME + "/" + DOMAIN_ID + "-manage\";\n" +
               "    }\n\n" +
               "    @ResponseBody\n" +
               "    @GetMapping(\"/data\")\n" +
               "    public ResponseEntity<ApiResponse<PageResponseDTO<" + DOMAIN_CLASS + "VO>>> getData(@ModelAttribute " + DOMAIN_CLASS + "SearchRequestDTO request) {\n" +
               "        return ResponseEntity.ok(ApiResponse.success(service.search(request)));\n" +
               "    }\n" +
               "}\n";
    }

    private static String getHtmlTemplate() {
        return "<!DOCTYPE html>\n" +
               "<html xmlns:th=\"http://www.thymeleaf.org\"\n" +
               "      xmlns:layout=\"http://www.ultraq.net.nz/thymeleaf/layout\"\n" +
               "      layout:decorate=\"~{defaultLayout}\">\n" +
               "<head>\n" +
               "    <title>" + DOMAIN_NAME + "</title>\n" +
               "</head>\n" +
               "<body>\n" +
               "<main layout:fragment=\"content\">\n" +
               "    <div class=\"content-header\">\n" +
               "        <h2>" + DOMAIN_NAME + "</h2>\n" +
               "    </div>\n\n" +
               "    <div class=\"search-section\">\n" +
               "        <div class=\"search-row\">\n" +
               "            <span class=\"search-label\">검색어</span>\n" +
               "            <input type=\"text\" id=\"searchKeyword\" class=\"search-input\" placeholder=\"검색어 입력\">\n" +
               "            \n" +
               "            <div class=\"search-actions\">\n" +
               "                <button type=\"button\" class=\"btn btn-primary\" id=\"btn-search\">조회</button>\n" +
               "                <button type=\"button\" class=\"btn btn-secondary\" id=\"btn-reset\">초기화</button>\n" +
               "            </div>\n" +
               "        </div>\n" +
               "    </div>\n\n" +
               "    <div class=\"grid-toolbar\">\n" +
               "        <span class=\"total-count\">총 <strong>0</strong>건</span>\n" +
               "        <div class=\"page-size-wrap\">\n" +
               "            페이지당\n" +
               "            <select id=\"pageSizeSelect\">\n" +
               "                <option value=\"10\">10건</option>\n" +
               "                <option value=\"20\">20건</option>\n" +
               "                <option value=\"50\">50건</option>\n" +
               "            </select>\n" +
               "        </div>\n" +
               "    </div>\n\n" +
               "    <div id=\"grid\"></div>\n" +
               "    <div id=\"pagination\" class=\"pagination-wrap\"></div>\n" +
               "</main>\n\n" +
               "<th:block layout:fragment=\"script\">\n" +
               "    <script th:src=\"@{/js/" + MODULE_NAME + "/" + DOMAIN_ID + "-manage.js}\"></script>\n" +
               "</th:block>\n" +
               "</body>\n" +
               "</html>\n";
    }

    private static String getJsTemplate() {
        return "/**\n" +
               " * " + DOMAIN_ID + "-manage.js\n" +
               " * " + DOMAIN_NAME + " 화면 스크립트\n" +
               " */\n" +
               "document.addEventListener('DOMContentLoaded', function () {\n" +
               "    const pageBuilder = new TuiPageBuilder({\n" +
               "        el: 'grid',\n" +
               "        apiUrl: '/" + MODULE_NAME + "/" + DOMAIN_ID + "/data',\n" +
               "        searchInputs: ['searchKeyword'],\n" +
               "        rowHeaders: ['rowNum'],\n" +
               "        columns: [\n" +
               "            { header: '예시컬럼', name: 'example', align: 'center', width: 150 }\n" +
               "        ],\n" +
               "        autoModal: true,\n" +
               "        autoModalTitle: '" + DOMAIN_NAME + " 상세'\n" +
               "    });\n" +
               "});\n";
    }
}
