package com.nsmm.esg.csddd_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

/**
 * CSDDD 카테고리별 분석 결과 DTO
 * 
 * 5개 주요 평가 영역별 점수 및 상태 분석 결과
 * 자가진단 상세 조회 시 카테고리별 현황 제공
 * 
 */
@Schema(description = "카테고리별 분석 결과")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryAnalysisDto {

    @Schema(description = "카테고리명", example = "인권및노동")
    private String category;

    @Schema(description = "카테고리별 점수 (0~100)", example = "85")
    private int score;

    @Schema(description = "평가 상태", example = "우수", allowableValues = { "우수", "보통", "개선필요" })
    private String status;

    @Schema(description = "UI 표시용 색상", example = "green", allowableValues = { "green", "yellow", "red" })
    private String color;
}