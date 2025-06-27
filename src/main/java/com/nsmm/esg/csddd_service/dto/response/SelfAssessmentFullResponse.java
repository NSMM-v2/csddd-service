package com.nsmm.esg.csddd_service.dto.response;

import com.nsmm.esg.csddd_service.dto.ActionPlanDto;
import com.nsmm.esg.csddd_service.dto.CategoryAnalysisDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * CSDDD 자가진단 전체 결과 응답 DTO
 *
 * 자가진단의 요약 정보와 문항별 상세 응답을 모두 포함하는 완전한 결과 DTO입니다.
 * 클라이언트에서 진단 결과의 전체적인 분석과 상세 내역을 모두 확인할 때 사용됩니다.
 *
 * 포함 정보:
 * - 기본 평가 결과 (점수, 상태 등)
 * - 문항별 상세 답변 목록
 * - 카테고리별 분석 결과
 * - 주요 강점 영역
 * - 구체적인 개선 계획
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SelfAssessmentFullResponse {

    // ============================================================================
    // 기본 식별 정보 (Basic Identification)
    // ============================================================================

    private Long id;
    private Long headquartersId;
    private Long partnerId;
    private String treePath;

    // ============================================================================
    // 평가 점수 정보 (Assessment Scores)
    // ============================================================================

    private Integer score;
    private Double actualScore;
    private Double totalPossibleScore;

    /**
     * 최종 등급 (A, B, C, D)
     * 백엔드에서 점수 및 중대위반 여부를 기반으로 계산
     */
    private String finalGrade;

    // ============================================================================
    // 평가 상태 및 결과 정보 (Assessment Status & Results)
    // ============================================================================

    private String status;
    private Integer criticalViolationCount;
    private Double completionRate;
    private String summary;
    private String recommendations;

    // ============================================================================
    // 타임스탬프 (Timestamps)
    // ============================================================================

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;

    // ============================================================================
    // 상세 분석 정보 (Detailed Analysis Information)
    // ============================================================================

    private List<SelfAssessmentAnswerDto> answers;
    private List<CategoryAnalysisDto> categoryAnalysis;
    private List<String> strengths;
    private List<ActionPlanDto> actionPlan;
}