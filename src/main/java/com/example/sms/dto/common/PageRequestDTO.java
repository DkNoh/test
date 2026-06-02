package com.example.sms.dto.common;

import lombok.Data;

@Data
public class PageRequestDTO {
    private int    page        = 1;
    private int    size        = 10;
    private String keyword;
    private String searchType;

    public int getOffset() {
        return (page - 1) * size;
    }

    public void validate() {
        if (page < 1)    page = 1;
        if (size < 1)    size = 10;
        if (size > 100)  size = 100;
    }
}
