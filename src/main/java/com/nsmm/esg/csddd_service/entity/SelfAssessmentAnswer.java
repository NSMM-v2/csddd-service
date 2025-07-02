package com.nsmm.esg.csddd_service.entity;


import com.nsmm.esg.csddd_service.enums.AssessmentGrade;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * CSDDD 자가진단 답변 엔티티
 * 
 * 각 진단 문항에 대한 개별 답변을 저장하는 엔티티입니다.
 * SelfAssessmentResult와 1:N 관계를 가집니다.
 * 
 * 답변 선택지:
 * - YES: 완전 준수 (가중치 100% 적용)
 * - NO: 미준수 (가중치 0% 적용)
 * - PARTIAL: 부분 준수 (가중치 50% 적용, 비즈니스 요구사항)
 * 
 * 중대위반:
 * - 특정 문항에서 NO 답변 시 중대위반으로 분류 가능
 * - 중대위반이 있으면 전체 등급이 자동 강등됨
 * 
 * @author ESG Project Team
 * @version 2.0
 */
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "self_assessment_answer", indexes = {
        @Index(name = "idx_result_id", columnList = "result_id"),
        @Index(name = "idx_question_id", columnList = "question_id"),
        @Index(name = "idx_category", columnList = "category"),
        @Index(name = "idx_critical_violation", columnList = "critical_violation")
})
public class SelfAssessmentAnswer {

    // ============================================================================
    // 기본 식별자 (Primary Key)
    // ============================================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 답변 고유 식별자

    // ============================================================================
    // 질문 정보 (Question Information)
    // ============================================================================

    /**
     * 질문 식별자
     * 예시: "Q1_1", "Q2_3", "Q5_2" 등
     * 각 질문을 고유하게 식별하는 코드
     */
    @Column(name = "question_id", nullable = false, length = 20)
    private String questionId;

    /**
     * 질문 카테고리
     * 예시: "인권및노동", "산업안전보건", "환경경영", "공급망및조달", "윤리경영및정보보호"
     * CSDDD 5개 주요 평가 영역별 분류
     */
    @Column(nullable = false, length = 100)
    private String category;

    // ============================================================================
    // 답변 정보 (Answer Information)
    // ============================================================================

    @Column(nullable = false)
    private boolean answer; // 응답 값: true = "예", false = "아니요"

    /**
     * 질문 가중치
     * 각 질문의 중요도에 따른 가중치 값
     * 높은 가중치일수록 전체 점수에 미치는 영향이 큼
     */
    @Column(nullable = false)
    private Double weight;

    /**
     * 부가 설명 및 비고사항
     * 답변에 대한 상세 설명이나 추가 정보
     * 부분 준수인 경우 현재 진행 상황이나 계획 등을 기록
     */
    @Column(length = 500)
    private String remarks;

    // ============================================================================
    // 중대위반 정보 (Critical Violation Information)
    // ============================================================================

    /**
     * 중대위반 여부
     * 해당 문항이 중대위반 항목인지 표시
     * true인 경우 NO 답변 시 전체 등급에 영향을 미침
     */
    @Builder.Default
    @Column(name = "critical_violation", nullable = false)
    private Boolean criticalViolation = false;

    /**
     * 중대위반 발생 시 적용되는 등급
     * - 프론트에서 내려주는 값 저장용
     * - 예: D, C, B 등
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "critical_grade", length = 5)
    private AssessmentGrade criticalGrade;

    // ============================================================================
    // 연관 관계 (Relationships)
    // ============================================================================

    /**
     * 소속 진단 결과
     * 이 답변이 속한 자가진단 결과와의 연관 관계
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_id", nullable = false, foreignKey = @ForeignKey(name = "fk_answer_result"))
    private SelfAssessmentResult result;

    // ============================================================================
    // 타임스탬프 (Timestamps)
    // ============================================================================

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성 일시

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt; // 수정 일시




    /**
     * 중대위반 여부 확인 (getter 메서드)
     * 
     * @return 중대위반인 경우 true
     */
    public Boolean getCriticalViolation() {
        return this.criticalViolation;
    }




    @Override
    public String toString() {
        return "SelfAssessmentAnswer{" +
                "id=" + id +
                ", questionId='" + questionId + '\'' +
                ", answer=" + answer +
                ", weight=" + weight +
                ", criticalViolation=" + criticalViolation +
                ", category='" + category + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}