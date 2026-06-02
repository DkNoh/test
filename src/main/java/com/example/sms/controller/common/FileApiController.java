package com.example.sms.controller.common;

import com.example.sms.service.common.FileService;
import com.example.sms.vo.common.AttachFileVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.sms.dto.common.ApiResponse;

import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileApiController {

    private final FileService fileService;

    /**
     * [파일 업로드 API]
     * 프론트엔드에서 FormData로 넘긴 파일을 서버에 저장하고 파일 ID를 반환합니다.
     */
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<Long>> upload(@RequestParam("file") MultipartFile file) {
        AttachFileVO savedFile = fileService.uploadFile(file);
        return ResponseEntity.ok(ApiResponse.success(savedFile.getFileId()));
    }

    /**
     * [파일 다운로드 API]
     * DB에 기록된 파일 ID를 기반으로 실제 서버 디스크의 파일을 읽어 다운로드 시킵니다.
     */
    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> download(@PathVariable Long fileId) throws MalformedURLException {
        // 1. DB에서 파일 메타데이터(원본 파일명, 물리적 저장 경로 등) 조회
        AttachFileVO fileInfo = fileService.getFileInfo(fileId);
        if (fileInfo == null) {
            return ResponseEntity.notFound().build();
        }

        // 2. 물리적 디스크에서 파일 읽기
        Path filePath = Paths.get(fileInfo.getFilePath());
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new RuntimeException("파일을 찾을 수 없거나 읽을 수 없습니다.");
        }

        // 3. 파일 이름 인코딩 (한글 깨짐 방지)
        String encodedUploadFileName = URLEncoder.encode(fileInfo.getOriginalName(), StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        String contentDisposition = "attachment; filename=\"" + encodedUploadFileName + "\"";

        // 4. 브라우저로 전송
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }
}