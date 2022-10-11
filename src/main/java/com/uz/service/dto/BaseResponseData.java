package com.uz.service.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BaseResponseData<T> extends BaseResponse {

    private T data;

    public BaseResponseData(T data) {
        super(true, "SUCCESS");
        this.data = data;
    }

    public BaseResponseData(boolean status, String message, T data) {
        super(status, message);
        this.data = data;
    }
}
