package com.nsmm.esg.csddd_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * API 응답 공통 DTO
 * 
 * NSMM 프로젝트 표준 응답 형식
 * 모든 API 응답에서 일관된 구조를 제공
 * 
 * @author ESG Project Team
 * @version 2.0
 */
@Schema(description = "공통 API 응답")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    @Schema(description = "성공 여부", example = "true")
    private boolean success;

    @Schema(description = "응답 메시지", example = "요청이 성공적으로 처리되었습니다.")
    private String message;

    @Schema(description = "응답 데이터")
    private T data;

    @Schema(description = "에러 코드", example = "VALIDATION_ERROR")
    private String errorCode;

    @Schema(description = "상세 에러 목록")
    private List<String> errors;

    @Schema(description = "응답 시간")
    private LocalDateTime timestamp;

    /**
     * 성공 응답 생성
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message("요청이 성공적으로 처리되었습니다.")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 성공 응답 생성 (커스텀 메시지)
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 실패 응답 생성
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 실패 응답 생성 (에러 코드 포함)
     */
    public static <T> ApiResponse<T> error(String message, String errorCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 유효성 검증 실패 응답
     */
    public static <T> ApiResponse<T> validationError(String message, List<String> errors) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errorCode("VALIDATION_ERROR")
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build();
    }
}