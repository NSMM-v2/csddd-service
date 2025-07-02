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
 * 본사와 협력사의 CSDDD 자가진단 결과를 저장하는 엔티티
 * 조직 계층 구조를 지원하며 권한 기반 데이터 접근을 제공
 * 
 * 주요 기능:
 * - 자가진단 결과 데이터 저장 및 관리
 * - 점수 계산 및 등급 산정 지원
 * - 중대위반 추적 및 관리
 * - 조직 계층별 권한 제어
 * 
 * 사용자 구분 방식:
 * - 본사: partnerId = null, headquartersId = 자신의 ID
 * - 협력사: partnerId = 협력사 ID, headquartersId = 소속 본사 ID
 * 
 * 등급 체계:
 * - A등급: 90점 이상
 * - B등급: 75점 이상 90점 미만
 * - C등급: 60점 이상 75점 미만
 * - D등급: 60점 미만 또는 중대위반 발생
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
     * 본사인 경우 자신의 ID, 협력사인 경우 소속 본사의 ID
     */
    @Column(name = "headquarters_id", nullable = false)
    private Long headquartersId;

    /**
     * 협력사 ID
     * 본사인 경우 null, 협력사인 경우 해당 협력사의 ID
     */
    @Column(name = "partner_id")
    private Long partnerId;

    /**
     * 조직 계층 경로
     * 권한 검증 및 계층 구조 관리용 경로 (예: HQ001, HQ001/L1-001/L2-003)
     */
    @Column(name = "tree_path", nullable = false, length = 500)
    private String treePath;

    /**
     * 회사명
     * 자가진단을 수행한 조직의 회사명
     */
    @Column(name = "company_name", nullable = false, length = 255)
    private String companyName;

    /**
     * 사용자 유형
     * HEADQUARTERS 또는 PARTNER 구분
     */
    @Column(name = "user_type", nullable = false, length = 30)
    private String userType;

    // ============================================================================
    // 평가 점수 정보 (Assessment Scores)
    // ============================================================================

    /**
     * 정규화된 점수 (0~100)
     * 등급 계산의 기준이 되는 백분율 점수
     */
    @Column(nullable = false)
    private double score;

    /**
     * 실제 획득 점수
     * 가중치가 적용된 실제 점수 합계
     */
    @Builder.Default
    @Column(name = "actual_score", nullable = false)
    private Double actualScore = 0.0;

    /**
     * 총 가능 점수
     * 해당 진단에서 획득 가능한 최대 점수
     */
    @Builder.Default
    @Column(name = "total_possible_score", nullable = false)
    private Double totalPossibleScore = 0.0;

    // ============================================================================
    // 평가 상태 및 결과 정보 (Assessment Status & Results)
    // ============================================================================

    /**
     * 진단 상태
     * COMPLETED(완료), IN_PROGRESS(진행중), CANCELLED(취소됨)
     */
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AssessmentStatus status = AssessmentStatus.COMPLETED;

    /**
     * 최종 등급
     * 점수 및 중대위반 여부에 따라 결정되는 최종 등급
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
     * 진단 결과를 바탕으로 한 구체적인 개선 방안
     */
    @Column(length = 2000)
    private String recommendations;

    /**
     * 중대위반 건수
     * 중대위반으로 분류된 항목의 개수 (등급 강등 적용)
     */
    @Builder.Default
    @Column(name = "critical_violation_count", nullable = false)
    private Integer criticalViolationCount = 0;

    // ============================================================================
    // 연관 관계 (Relationships)
    // ============================================================================

    /**
     * 자가진단 답변 목록
     * 하나의 진단 결과에 포함된 모든 문항 답변
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
    // 비즈니스 메서드 (Business Methods)
    // ============================================================================

    /**
     * 본사 여부 확인
     * partnerId가 null인 경우 본사로 판단
     * 
     * @return 본사인 경우 true
     */
    public boolean isHeadquarters() {
        return this.partnerId == null;
    }

    /**
     * 협력사 여부 확인
     * partnerId가 존재하는 경우 협력사로 판단
     * 
     * @return 협력사인 경우 true
     */
    public boolean isPartner() {
        return this.partnerId != null;
    }

    /**
     * 진단 답변 목록 할당
     * 양방향 연관관계 설정 및 답변 목록 복사본 생성
     * 
     * @param answers 할당할 답변 목록
     */
    public void assignAnswers(List<SelfAssessmentAnswer> answers) {
        this.answers = new ArrayList<>(answers);
        // 양방향 연관관계 설정
        answers.forEach(answer -> answer.assignToResult(this));
    }

    /**
     * 진단 결과 기본 정보 업데이트
     * 회사 정보 및 조직 계층 정보 수정
     * 
     * @param companyName    회사명
     * @param userType       사용자 유형
     * @param headquartersId 본사 ID
     * @param partnerId      협력사 ID
     * @param treePath       계층 경로
     */
    public void updateBasicInfo(String companyName, String userType, Long headquartersId, Long partnerId,
            String treePath) {
        this.companyName = companyName;
        this.userType = userType;
        this.headquartersId = headquartersId;
        this.partnerId = partnerId;
        this.treePath = treePath;
    }

    /**
     * 평가 완료 처리
     * 최종 점수와 결과를 설정하고 완료 상태로 변경
     * 
     * @param score           정규화된 점수
     * @param actualScore     실제 획득 점수
     * @param totalScore      총 가능 점수
     * @param finalGrade      최종 등급
     * @param summary         평가 요약
     * @param recommendations 개선 권고사항
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
        this.status = AssessmentStatus.COMPLETED;

        // 완료 시간 설정 (최초 완료 시에만)
        if (this.completedAt == null) {
            this.completedAt = LocalDateTime.now();
        }

        // 중대위반 건수 재계산
        updateCriticalViolationCount();
    }

    /**
     * 중대위반 건수 업데이트
     * 답변 목록에서 중대위반 발생 건수를 계산하여 업데이트
     */
    public void updateCriticalViolationCount() {
        this.criticalViolationCount = (int) answers.stream()
                .filter(SelfAssessmentAnswer::hasCriticalViolation)
                .count();
    }

    /**
     * 완료율 계산
     * 실제 점수 대비 총 가능 점수의 백분율
     * 
     * @return 완료율 (0~100)
     */
    public Double calculateCompletionRate() {
        if (totalPossibleScore == null || totalPossibleScore == 0) {
            return 0.0;
        }
        return (actualScore / totalPossibleScore) * 100;
    }

    /**
     * 고위험 여부 판단
     * 점수 60점 미만이거나 중대위반이 있는 경우 고위험으로 분류
     * 
     * @return 고위험인 경우 true
     */
    public boolean isHighRisk() {
        return score < 60.0 || criticalViolationCount > 0;
    }
}