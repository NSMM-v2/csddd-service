package com.nsmm.esg.csddd_service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * CSDDD 자가진단 위반 항목 응답 DTO
 * 
 * 자가진단 결과에서 위반이 발생한 항목들만을 필터링하여 제공하는 DTO
 * 위험 관리 및 개선이 필요한 항목들을 집중적으로 분석할 때 사용
 * 
 * 위반 분류 기준:
 * - NO 응답: 완전 미준수 (answer = false)
 * - 중대위반 항목: 특별 관리 대상
 * 
 * @author ESG Project Team
 * @version 2.0
 */
@Schema(description = "자가진단 위반 항목 응답")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViolationDto {

    // ============================================================================
    // 질문 정보 (Question Information)
    // ============================================================================

    @Schema(description = "문항 식별자", example = "Q1_1")
    private String questionId;

    @Schema(description = "문항 카테고리", example = "인권및노동")
    private String category;

    // ============================================================================
    // 위반 정보 (Violation Information)
    // ============================================================================

    /**
     * 사용자 응답
     * false = 위반 (NO)
     * true = 준수 (YES)
     */
    @Schema(description = "사용자 응답 (위반 여부)", example = "false", allowableValues = { "true", "false" })
    private boolean answer;

    /**
     * 중대위반 여부
     * true인 경우 특별 관리가 필요한 중요 위반 항목
     */
    @Schema(description = "중대위반 항목 여부", example = "true")
    private Boolean criticalViolation;

    /**
     * 비고 또는 개선 계획
     * 사용자가 작성한 설명 또는 향후 조치
     */
    @Schema(description = "비고 또는 개선 계획", example = "현재 노동조합 설립을 위한 절차를 진행 중입니다.")
    private String remarks;

    // ============================================================================
    // 추가 위반 정보 (Additional Violation Information)
    // ============================================================================

    @Schema(description = "문항 가중치", example = "2.5")
    private Double weight;

    @Schema(description = "중대위반 시 적용 등급", example = "D", allowableValues = { "A", "B", "C", "D" })
    private String criticalGrade;

    @Schema(description = "위반 위험도", example = "높음", allowableValues = { "낮음", "보통", "높음", "매우높음" })
    private String riskLevel;
}