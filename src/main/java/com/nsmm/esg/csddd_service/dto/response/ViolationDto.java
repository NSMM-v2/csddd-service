package com.nsmm.esg.csddd_service.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * CSDDD 자가진단 위반 항목 응답 DTO
 *
 * 자가진단 결과에서 위반이 발생한 항목들만을 필터링하여 제공하는 DTO입니다.
 * 위험 관리 및 개선이 필요한 항목들을 집중적으로 분석할 때 사용됩니다.
 *
 * 위반 분류 기준:
 * - NO 응답: 완전 미준수 (answer = false)
 * - 중대위반 항목: 특별 관리 대상
 */
@Getter
@Builder
public class ViolationDto {

    // ============================================================================
    // 질문 정보 (Question Information)
    // ============================================================================

    private String questionId;
    private String category;

    // ============================================================================
    // 위반 정보 (Violation Information)
    // ============================================================================

    /**
     * 사용자 응답
     * false = 위반 (NO)
     * true = 준수 (YES)
     */
    private boolean answer;

    /**
     * 중대위반 여부
     * true인 경우 특별 관리가 필요한 중요 위반 항목
     */
    private Boolean criticalViolation;

    /**
     * 비고 또는 개선 계획
     * 사용자가 작성한 설명 또는 향후 조치
     */
    private String remarks;
}