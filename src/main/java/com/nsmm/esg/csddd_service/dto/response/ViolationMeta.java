package com.nsmm.esg.csddd_service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * CSDDD 중대위반 메타데이터 응답 DTO
 *
 * 특정 문항의 중대위반 관련 메타 정보를 제공
 * 법적 근거, 처벌 정보 등을 포함
 *
 */
@Schema(description = "중대위반 메타데이터")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ViolationMeta {

    @Schema(description = "위반 카테고리", example = "인권및노동")
    private String category;

    @Schema(description = "처벌 정보", example = "과태료 500만원 이하")
    private String penaltyInfo;

    @Schema(description = "법적 근거", example = "근로기준법 제5조")
    private String legalBasis;
}