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
 * 각 진단 문항에 대한 개별 답변을 저장하는 엔티티
 * SelfAssessmentResult와 다대일 관계를 가지며 개별 문항의 응답 데이터를 관리
 * 
 * 주요 기능:
 * - 문항별 답변 데이터 저장 (예/아니요)
 * - 가중치 기반 점수 계산 지원
 * - 중대위반 항목 관리
 * - 답변에 대한 추가 설명 저장
 * 
 * 답변 처리 규칙:
 * - YES(true): 완전 준수, 가중치 100% 적용
 * - NO(false): 미준수, 가중치 0% 적용
 * - 중대위반 항목에서 NO 응답 시 전체 등급 강등
 * 
 * @author ESG Project Team
 * @version 2.0
 */
@Entity
@Table(name = "self_assessment_answer", indexes = {
        @Index(name = "idx_result_id", columnList = "result_id"),
        @Index(name = "idx_question_id", columnList = "question_id"),
        @Index(name = "idx_category", columnList = "category"),
        @Index(name = "idx_critical_violation", columnList = "critical_violation")
})
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
     * 각 문항을 고유하게 식별하는 코드 (예: Q1_1, Q2_3, Q5_2)
     */
    @Column(name = "question_id", nullable = false, length = 20)
    private String questionId;

    /**
     * 질문 카테고리
     * CSDDD 5개 주요 평가 영역 분류 (인권및노동, 산업안전보건, 환경경영, 공급망및조달, 윤리경영및정보보호)
     */
    @Column(nullable = false, length = 100)
    private String category;

    // ============================================================================
    // 답변 정보 (Answer Information)
    // ============================================================================

    /**
     * 사용자 응답
     * true: 예(준수), false: 아니요(미준수)
     */
    @Column(nullable = false)
    private boolean answer;

    /**
     * 질문 가중치
     * 각 문항의 중요도에 따른 점수 비중 (점수 계산 시 사용)
     */
    @Column(nullable = false)
    private Double weight;

    /**
     * 답변 부가 설명
     * 응답에 대한 상세 설명이나 추가 정보 (선택 사항)
     */
    @Column(length = 500)
    private String remarks;

    // ============================================================================
    // 중대위반 정보 (Critical Violation Information)
    // ============================================================================

    /**
     * 중대위반 항목 여부
     * 해당 문항이 중대위반 대상인지 표시 (NO 응답 시 등급 강등 적용)
     */
    @Builder.Default
    @Column(name = "critical_violation", nullable = false)
    private Boolean criticalViolation = false;

    /**
     * 중대위반 시 적용 등급
     * 중대위반 발생 시 강등될 등급 정보 (프론트엔드에서 전달)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "critical_grade", length = 10)
    private AssessmentGrade criticalGrade;

    // ============================================================================
    // 연관 관계 (Relationships)
    // ============================================================================

    /**
     * 소속 자가진단 결과
     * 이 답변이 속한 자가진단 결과와의 다대일 관계
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

    // ============================================================================
    // 비즈니스 메서드 (Business Methods)
    // ============================================================================

    /**
     * 자가진단 결과와 연관관계 설정
     * 양방향 연관관계 관리를 위한 편의 메서드
     * 
     * @param result 연결할 자가진단 결과
     */
    public void assignToResult(SelfAssessmentResult result) {
        this.result = result;
    }

    /**
     * 중대위반 발생 여부 확인
     * 중대위반 항목이면서 NO 응답인 경우 true 반환
     * 
     * @return 중대위반 발생 시 true
     */
    public boolean hasCriticalViolation() {
        return Boolean.TRUE.equals(this.criticalViolation) && !this.answer;
    }

    /**
     * 답변 점수 계산
     * 응답에 따른 가중치 적용 점수 계산
     * 
     * @return 계산된 점수 (YES: 가중치 100%, NO: 0점)
     */
    public double calculateScore() {
        return this.answer ? this.weight : 0.0;
    }
}