package com.example.sms.service.common;

import com.example.sms.mapper.AttachFileMapper;
import com.example.sms.vo.common.AttachFileVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * [FileService]
 * 시스템 내의 모든 첨부파일 업로드/다운로드 및 DB 메타데이터(attach_file) 관리를 담당하는 서비스입니다.
 * 
 * [사용법(Usage)]
 * 1. 파일 업로드:
 *    AttachFileVO fileInfo = fileService.uploadFile(multipartFile);
 *    Long savedFileId = fileInfo.getFileId(); // 이 ID를 비즈니스 테이블(예: 게시판)에 저장
 * 
 * 2. 파일 정보 조회:
 *    AttachFileVO fileInfo = fileService.getFileInfo(fileId);
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    private final AttachFileMapper attachFileMapper;

    /**
     * 클라이언트가 보낸 파일을 물리적 디스크에 저장하고, DB에 메타데이터를 기록합니다.
     */
    public AttachFileVO uploadFile(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 비어 있습니다.");
        }

        // 1. 디렉토리 검사 및 생성
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 2. 파일명 및 확장자 처리
        String originalName = multipartFile.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.lastIndexOf(".") > -1) {
            ext = originalName.substring(originalName.lastIndexOf(".") + 1);
        }
        
        // 3. 서버에 저장할 고유 파일명 생성 (UUID)
        String saveName = UUID.randomUUID().toString() + (ext.isEmpty() ? "" : "." + ext);
        String filePath = uploadDir + File.separator + saveName;

        try {
            // 4. 물리적 디스크에 파일 저장
            multipartFile.transferTo(new File(filePath));
        } catch (IOException e) {
            log.error("파일 저장 실패", e);
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다.");
        }

        // 5. DB 메타데이터 저장
        AttachFileVO fileVO = AttachFileVO.builder()
                .originalName(originalName)
                .saveName(saveName)
                .filePath(filePath)
                .fileSize(multipartFile.getSize())
                .ext(ext)
                .build();
                
        attachFileMapper.insertAttachFile(fileVO); // insert 후 fileId가 세팅됨

        return fileVO;
    }

    /**
     * 파일 ID로 메타데이터 조회
     */
    public AttachFileVO getFileInfo(Long fileId) {
        return attachFileMapper.selectAttachFileById(fileId);
    }
}