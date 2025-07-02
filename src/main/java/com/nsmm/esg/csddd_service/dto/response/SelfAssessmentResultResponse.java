package com.nsmm.esg.csddd_service.dto.response;

import com.nsmm.esg.csddd_service.dto.ActionPlanDto;
import com.nsmm.esg.csddd_service.dto.CategoryAnalysisDto;
import com.nsmm.esg.csddd_service.entity.SelfAssessmentResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CSDDD 자가진단 결과 응답 DTO
 * 
 * 자가진단 결과의 요약 및 상세 정보를 모두 제공
 * 조회 방식에 따라 답변 목록 포함 여부 결정
 * 
 * @author ESG Project Team
 * @version 3.0
 */
@Schema(description = "CSDDD 자가진단 결과 응답")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SelfAssessmentResultResponse {

    // ============================================================================
    // 기본 식별 정보 (Basic Identification)
    // ============================================================================

    @Schema(description = "자가진단 결과 ID", example = "1")
    private Long id;

    @Schema(description = "회사명", example = "삼성전자")
    private String companyName;

    @Schema(description = "사용자 유형", example = "HEADQUARTERS")
    private String userType;

    // ============================================================================
    // 사용자 식별 정보 (User Identification) - 관리용
    // ============================================================================

    @Schema(description = "소속 본사 ID", example = "1")
    private Long headquartersId;

    @Schema(description = "협력사 ID (협력사인 경우)", example = "2")
    private Long partnerId;

    @Schema(description = "조직 계층 경로", example = "HQ001")
    private String treePath;

    // ============================================================================
    // 평가 점수 정보 (Assessment Scores)
    // ============================================================================

    @Schema(description = "정규화된 점수 (0~100)", example = "85.5")
    private double score;

    @Schema(description = "실제 획득 점수", example = "34.2")
    private Double actualScore;

    @Schema(description = "총 가능 점수", example = "40.0")
    private Double totalPossibleScore;

    @Schema(description = "완료율 (%)", example = "85.5")
    private Double completionRate;

    // ============================================================================
    // 평가 상태 및 결과 정보 (Assessment Status & Results)
    // ============================================================================

    @Schema(description = "진단 상태", example = "COMPLETED")
    private String status;

    @Schema(description = "최종 등급", example = "B")
    private String finalGrade;

    @Schema(description = "중대위반 건수", example = "0")
    private Integer criticalViolationCount;

    @Schema(description = "고위험 여부", example = "false")
    private Boolean isHighRisk;

    @Schema(description = "평가 요약")
    private String summary;

    @Schema(description = "개선 권고사항")
    private String recommendations;

    // ============================================================================
    // 타임스탬프 (Timestamps)
    // ============================================================================

    @Schema(description = "생성 일시")
    private LocalDateTime createdAt;

    @Schema(description = "수정 일시")
    private LocalDateTime updatedAt;

    @Schema(description = "완료 일시")
    private LocalDateTime completedAt;

    // ============================================================================
    // 상세 정보 (Optional - 상세 조회 시에만)
    // ============================================================================

    @Schema(description = "문항별 답변 목록 (상세 조회 시)")
    private List<SelfAssessmentAnswerResponse> answers;

    @Schema(description = "카테고리별 분석 결과 (상세 조회 시)")
    private List<CategoryAnalysisDto> categoryAnalysis;

    @Schema(description = "주요 강점 영역 (상세 조회 시)")
    private List<String> strengths;

    @Schema(description = "구체적인 개선 계획 (상세 조회 시)")
    private List<ActionPlanDto> actionPlan;

    // ============================================================================
    // 정적 팩토리 메서드 (Static Factory Methods)
    // ============================================================================

    /**
     * 요약 정보만 포함하는 응답 생성 (목록 조회용)
     */
    public static SelfAssessmentResultResponse fromSummary(SelfAssessmentResult result) {
        return SelfAssessmentResultResponse.builder()
                .id(result.getId())
                .companyName(result.getCompanyName())
                .userType(result.getUserType())
                .headquartersId(result.getHeadquartersId())
                .partnerId(result.getPartnerId())
                .treePath(result.getTreePath())
                .score(result.getScore())
                .actualScore(result.getActualScore())
                .totalPossibleScore(result.getTotalPossibleScore())
                .completionRate(result.calculateCompletionRate())
                .status(result.getStatus().name())
                .finalGrade(result.getFinalGrade() != null ? result.getFinalGrade().name() : null)
                .criticalViolationCount(result.getCriticalViolationCount())
                .isHighRisk(result.isHighRisk())
                .summary(result.getSummary())
                .recommendations(result.getRecommendations())
                .createdAt(result.getCreatedAt())
                .updatedAt(result.getUpdatedAt())
                .completedAt(result.getCompletedAt())
                .build();
    }

    /**
     * 전체 상세 정보 포함하는 응답 생성 (상세 조회용)
     */
    public static SelfAssessmentResultResponse fromDetail(SelfAssessmentResult result) {
        return SelfAssessmentResultResponse.builder()
                .id(result.getId())
                .companyName(result.getCompanyName())
                .userType(result.getUserType())
                .headquartersId(result.getHeadquartersId())
                .partnerId(result.getPartnerId())
                .treePath(result.getTreePath())
                .score(result.getScore())
                .actualScore(result.getActualScore())
                .totalPossibleScore(result.getTotalPossibleScore())
                .completionRate(result.calculateCompletionRate())
                .status(result.getStatus().name())
                .finalGrade(result.getFinalGrade() != null ? result.getFinalGrade().name() : null)
                .criticalViolationCount(result.getCriticalViolationCount())
                .isHighRisk(result.isHighRisk())
                .summary(result.getSummary())
                .recommendations(result.getRecommendations())
                .createdAt(result.getCreatedAt())
                .updatedAt(result.getUpdatedAt())
                .completedAt(result.getCompletedAt())
                .answers(result.getAnswers().stream()
                        .map(SelfAssessmentAnswerResponse::from)
                        .collect(Collectors.toList()))
                // TODO: 필요시 categoryAnalysis, strengths, actionPlan 추가
                .build();
    }

    /**
     * 기존 호환성을 위한 메서드
     */
    public static SelfAssessmentResultResponse from(SelfAssessmentResult result) {
        return fromDetail(result);
    }
}