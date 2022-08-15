package com.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ResponseDto {
    private int code = 0;
    private Object data;

    public static ResponseDto of(int code, Object data) {
        return ResponseDto.builder()
                .code(code)
                .data(data)
                .build();
    }

    public static ResponseDto of(Object data) {
        return ResponseDto.builder()
                .data(data)
                .build();
    }

    public static ResponseDto ofError(int code) {
        return ResponseDto.builder().code(code).build();
    }

}
