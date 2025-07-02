package com.nsmm.esg.csddd_service.entity;

import com.nsmm.esg.csddd_service.enums.AssessmentStatus;
import com.nsmm.esg.csddd_service.enums.AssessmentGrade;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * CSDDD 자가진단 결과 엔티티
 * 
 * 본사와 협력사의 CSDDD 자가진단 결과를 저장하는 엔티티입니다.
 * Scope3 방식과 동일하게 headquartersId/partnerId로 사용자를 구분합니다.
 * 
 * 사용자 구분 방식:
 * - 본사: partnerId가 null, headquartersId가 자신의 ID
 * - 협력사: partnerId가 존재, headquartersId는 소속 본사 ID
 * 
 * 등급 계산:
 * - 백엔드에서는 수치값(점수)만 저장
 * - 프론트엔드에서 점수를 기반으로 등급(A/B/C/D) 계산
 * 
 * @author ESG Project Team
 * @version 2.0
 */
@Entity
@Table(name = "self_assessment_results", indexes = {
        @Index(name = "idx_headquarters_id", columnList = "headquarters_id"),
        @Index(name = "idx_partner_id", columnList = "partner_id"),
        @Index(name = "idx_tree_path", columnList = "tree_path"),
        @Index(name = "idx_headquarters_created", columnList = "headquarters_id, created_at"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_score", columnList = "score")
})
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SelfAssessmentResult {

    // ============================================================================
    // 기본 식별자 (Primary Key)
    // ============================================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 자가진단 결과 고유 식별자

    // ============================================================================
    // 사용자 식별 정보 (User Identification)
    // ============================================================================

    /**
     * 소속 본사 ID
     * - 본사인 경우: 자신의 본사 ID
     * - 협력사인 경우: 소속된 본사의 ID
     * Auth Service의 JWT 토큰에서 제공되는 headquartersId와 매핑
     */
    @Column(name = "headquarters_id", nullable = false)
    private Long headquartersId;

    /**
     * 협력사 ID
     * - 본사인 경우: null
     * - 협력사인 경우: 해당 협력사의 ID
     * Auth Service의 JWT 토큰에서 제공되는 partnerId와 매핑
     */
    @Column(name = "partner_id")
    private Long partnerId;

    /**
     * 조직 트리 경로
     * 권한 검증 및 계층 구조 관리를 위한 경로 정보
     * 형식: "HQ001" (본사) 또는 "HQ001/L1-001/L2-003" (협력사)
     */
    @Column(name = "tree_path", nullable = false, length = 500)
    private String treePath;

    @Column(name = "company_name", nullable = false, length = 255)
    private String companyName;

    /**
     * 사용자 유형
     * - "HEADQUARTERS" 또는 "PARTNER" 구분용
     */
    @Column(name = "user_type", nullable = false, length = 30)
    private String userType;
    // ============================================================================
    // 평가 점수 정보 (Assessment Scores)
    // ============================================================================

    /**
     * 정규화된 점수 (0~100)
     * 프론트엔드에서 등급 계산의 기준이 되는 점수
     * 90점 이상: A등급, 75점 이상: B등급, 60점 이상: C등급, 60점 미만: D등급
     */
    @Column(nullable = false)
    private double score;

    /**
     * 실제 획득 점수
     * 가중치가 적용된 실제 점수 (예: 34.5점)
     */
    @Builder.Default
    @Column(name = "actual_score", nullable = false)
    private Double actualScore = 0.0;

    /**
     * 총 가능 점수
     * 해당 진단에서 획득 가능한 최대 점수 (예: 40.0점)
     */
    @Builder.Default
    @Column(name = "total_possible_score", nullable = false)
    private Double totalPossibleScore = 0.0;

    // ============================================================================
    // 평가 상태 및 결과 정보 (Assessment Status & Results)
    // ============================================================================

    /**
     * 진단 상태
     * - COMPLETED: 완료
     * - IN_PROGRESS: 진행중 (향후 확장용)
     * - CANCELLED: 취소됨 (향후 확장용)
     */
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AssessmentStatus status = AssessmentStatus.COMPLETED;

    /**
     * 최종 등급 (A~D)
     * 점수 및 중대위반 여부에 따라 자동 결정
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "final_grade", length = 10)
    private AssessmentGrade finalGrade;


    /**
     * 평가 요약
     * 진단 결과에 대한 간략한 요약 설명
     */
    @Column(length = 1000)
    private String summary;

    /**
     * 개선 권고사항
     * 진단 결과를 바탕으로 한 구체적인 개선 방안 제시
     */
    @Column(length = 2000)
    private String recommendations;

    /**
     * 중대위반 건수
     * 중대위반으로 분류된 항목의 개수
     * 중대위반이 있는 경우 등급이 자동으로 강등됨
     */
    @Builder.Default
    @Column(name = "critical_violation_count", nullable = false)
    private Integer criticalViolationCount = 0;

    // ============================================================================
    // 연관 관계 (Relationships)
    // ============================================================================

    /**
     * 자가진단 답변 목록
     * 하나의 진단 결과에 여러 개의 답변이 포함됨
     */
    @OneToMany(mappedBy = "result", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<SelfAssessmentAnswer> answers = new ArrayList<>();

    // ============================================================================
    // 타임스탬프 (Timestamps)
    // ============================================================================

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성 일시

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt; // 수정 일시

    @Column(name = "completed_at")
    private LocalDateTime completedAt; // 완료 일시

    // ============================================================================
    // 편의 메서드 (Convenience Methods)
    // ============================================================================

    /**
     * 본사 여부 확인
     * partnerId가 null인 경우 본사로 판단
     */
    public boolean isHeadquarters() {
        return this.partnerId == null;
    }

    /**
     * 협력사 여부 확인
     * partnerId가 존재하는 경우 협력사로 판단
     */
    public boolean isPartner() {
        return this.partnerId != null;
    }

    /**
     * 진단 결과 기본 정보 수정
     * - 본사/협력사 구분, 회사명, 트리 경로 등
     */
    public void updateResultInfo(String companyName, String userType, Long headquartersId, Long partnerId, String treePath) {
        this.companyName = companyName;
        this.userType = userType;
        this.headquartersId = headquartersId;
        this.partnerId = partnerId;
        this.treePath = treePath;
    }

    /**
     * 완료율 계산
     * (실제 점수 / 총 가능 점수) * 100
     */
    public Double getCompletionRate() {
        if (totalPossibleScore == null || totalPossibleScore == 0) {
            return 0.0;
        }
        return (actualScore / totalPossibleScore) * 100;
    }

    /**
     * 고위험 여부 판단
     * 점수가 60점 미만이거나 중대위반이 있는 경우
     */
    public boolean isHighRisk() {
        return score < 60 || criticalViolationCount > 0;
    }

    /**
     * 진단 답변 리스트 설정
     * - 점수 계산 전에 답변 목록을 연결해야 함
     */
    public void setAnswers(List<SelfAssessmentAnswer> answers) {
        this.answers = answers;
    }

    /**
     * 평가 완료 처리
     * 최종 점수와 결과를 설정하고 완료 상태로 변경
     */
    public void finalizeAssessment(
            double score,
            double actualScore,
            double totalScore,
            AssessmentGrade finalGrade,
            String summary,
            String recommendations) {
        this.score = score;
        this.actualScore = actualScore;
        this.totalPossibleScore = totalScore;
        this.finalGrade = finalGrade;
        this.summary = summary;
        this.recommendations = recommendations;
        this.criticalViolationCount = (int) answers.stream()
                .filter(SelfAssessmentAnswer::getCriticalViolation)
                .count();
        this.status = AssessmentStatus.COMPLETED;
        if (this.completedAt == null) {
            this.completedAt = LocalDateTime.now();
        }
    }

    @Override
    public String toString() {
        return "SelfAssessmentResult{" +
                "id=" + id +
                ", headquartersId=" + headquartersId +
                ", partnerId=" + partnerId +
                ", treePath='" + treePath + '\'' +
                ", score=" + score +
                ", status=" + status +
                ", criticalViolationCount=" + criticalViolationCount +
                ", createdAt=" + createdAt +
                '}';
    }

    public void updateCriticalViolationCount(int count) {
        this.criticalViolationCount = count;
    }
}