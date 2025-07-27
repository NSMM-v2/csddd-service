package com.nsmm.esg.csddd_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * CSDDD 개선 계획 제안 DTO
 * 
 * 자가진단 결과를 바탕으로 한 구체적인 개선 방안 제시
 * 우선순위와 권장 조치사항을 포함
 */
@Schema(description = "개선 계획 제안")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionPlanDto {

    @Schema(description = "문제 요약", example = "중대 위반: 노동권 침해")
    private String issue;

    @Schema(description = "개선 우선순위", example = "긴급", allowableValues = { "긴급", "높음", "보통", "낮음" })
    private String priority;

    @Schema(description = "권장 조치사항", example = "노동조합 설립 지원 및 근로자 권익 보호 체계 구축")
    private String recommendation;
}