package com.example.sms.util;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Slf4j
public class ExcelUtil {

    /**
     * [ExcelUtil]
     * 데이터(List<Map>)를 받아 아파치 POI(SXSSFWorkbook) 기반의 대용량 엑셀 스트림으로 
     * 변환하여 클라이언트에게 바로 다운로드 시켜주는 공통 유틸리티 클래스입니다.
     * 
     * [사용법(Usage)]
     * 1. 컨트롤러나 서비스에서 다운로드 호출:
     *    String[] headers = {"이름", "나이"};
     *    String[] keys = {"name", "age"};
     *    List<Map<String, Object>> dataList = ...; // 데이터 조회
     *    
     *    ExcelUtil.downloadExcel(response, "파일명_export", headers, dataList, keys);
     *
     * @param response HttpServletResponse 객체
     * @param fileName 다운로드될 파일명 (확장자 제외)
     * @param headers  엑셀 헤더 컬럼명 배열 (예: {"사번", "이름", "부서"})
     * @param dataList 실제 데이터 (Map 배열 형태로 각 컬럼 인덱스에 매칭되게 구성 권장)
     * @param keys     데이터 맵에서 헤더 순서대로 추출할 키 값 배열
     */
    public static void downloadExcel(HttpServletResponse response, String fileName, String[] headers, List<Map<String, Object>> dataList, String[] keys) {
        // SXSSFWorkbook: 100행 단위로 메모리에 유지하고 나머지는 임시 파일에 플러시하여 OutOfMemory 방지
        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {
            Sheet sheet = workbook.createSheet("Data");
            
            // 헤더 스타일 구성 (회색 배경, 굵은 글씨, 가운데 정렬)
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            // 1. 헤더 생성
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 4000); // 기본 열 너비
            }

            // 2. 데이터 바인딩
            int rowIdx = 1;
            for (Map<String, Object> data : dataList) {
                Row row = sheet.createRow(rowIdx++);
                for (int i = 0; i < keys.length; i++) {
                    Cell cell = row.createCell(i);
                    Object value = data.get(keys[i]);
                    cell.setCellValue(value != null ? value.toString() : "");
                }
            }

            // 3. 브라우저 응답 설정
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName + ".xlsx\"");

            // 4. 전송
            workbook.write(response.getOutputStream());
            response.getOutputStream().flush();

        } catch (IOException e) {
            log.error("엑셀 파일 다운로드 중 오류 발생", e);
            throw new RuntimeException("엑셀 다운로드에 실패했습니다.");
        }
    }
}