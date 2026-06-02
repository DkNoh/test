package com.example.sms.vo.common;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class AttachFileVO {
    private Long fileId;
    private String originalName;
    private String saveName;
    private String filePath;
    private Long fileSize;
    private String ext;
    private LocalDateTime createdAt;
}