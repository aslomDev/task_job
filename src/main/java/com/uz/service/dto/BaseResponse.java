package com.uz.service.dto;

import lombok.Data;

@Data
public class BaseResponse {
    private boolean status;
    private String message;


    public BaseResponse() {
        this.status = false;
        this.message = "unknown error";
    }

    public BaseResponse(boolean status, String message) {
        this.status = status;
        this.message = message;
    }
}
