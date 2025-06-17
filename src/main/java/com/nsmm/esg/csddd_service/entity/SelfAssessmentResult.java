package com.nsmm.esg.csddd_service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nsmm.esg.csddd_service.dto.response.SelfAssessmentResponse;
import com.nsmm.esg.csddd_service.enums.AssessmentGrade;
import com.nsmm.esg.csddd_service.enums.AssessmentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 📄 SelfAssessmentResult
 * - 자가진단 결과를 저장하는 메인 엔티티
 * - 사용자별로 1개의 결과를 가지며, 여러 개의 문항(SelfAssessmentAnswer)와 연결됨
 */
@Entity
@Table(name = "self_assessment_results",
        indexes = {
                @Index(name = "idx_member_id", columnList = "memberId"),
                @Index(name = "idx_member_created", columnList = "memberId, createdAt"),
                @Index(name = "idx_grade", columnList = "grade"),
                @Index(name = "idx_status", columnList = "status")
        })
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SelfAssessmentResult {

    // 결과 ID (자동 생성)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 회원 식별자
    @Column(nullable = false)
    private Long memberId;

    // 정규화된 점수 (ex. 80)
    @Column(nullable = false)
    private Integer score;

    // 실질적 점수 (ex. 34.5점)
    @Column(nullable = false)
    private Double actualScore;

    // 가능한 총점 (ex. 40.0점)
    @Column(nullable = false)
    private Double totalPossibleScore;

    // 등급 (A, B, C, D)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private AssessmentGrade grade;

    // 상태: 기본값 COMPLETED
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AssessmentStatus status = AssessmentStatus.COMPLETED;

    // 원본 JSON 형태의 응답 저장 (백업용 또는 검증용)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String answersJson;

    // 요약 설명 (최대 1000자)
    @Column(length = 1000)
    private String summary;

    // 권고 사항 (최대 2000자)
    @Column(length = 2000)
    private String recommendations;

    // 중대 위반 항목 수 (기본값 0)
    @Builder.Default
    @Column(nullable = false)
    private Integer criticalViolationCount = 0;

    // 📌 1:N 연관관계 - 자가진단 문항들
    @OneToMany(mappedBy = "result", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    private List<SelfAssessmentAnswer> answers = new ArrayList<>();

    // 생성 시각
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 수정 시각
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // 완료 시각 (최초 제출 시점)
    @Column
    private LocalDateTime completedAt;

    // ===== 편의 메서드 =====

    // 문항 추가
    public void addAnswer(SelfAssessmentAnswer answer) {
        this.answers.add(answer);
    }

    // 문항 제거
    public void removeAnswer(SelfAssessmentAnswer answer) {
        this.answers.remove(answer);
    }

    // 중대 위반 항목 리스트 반환
    public List<SelfAssessmentAnswer> getCriticalViolations() {
        return answers.stream()
                .filter(SelfAssessmentAnswer::getCriticalViolation)
                .collect(Collectors.toList());
    }

    // 카테고리별로 문항 그룹화
    public Map<String, List<SelfAssessmentAnswer>> getAnswersByCategory() {
        return answers.stream()
                .collect(Collectors.groupingBy(SelfAssessmentAnswer::getCategory));
    }

    // 완료율 계산
    public Double getCompletionRate() {
        if (totalPossibleScore == null || totalPossibleScore == 0) {
            return 0.0;
        }
        return (actualScore / totalPossibleScore) * 100;
    }

    // 리스크 판별 - 고위험
    public boolean isHighRisk() {
        return grade == AssessmentGrade.D || criticalViolationCount > 0;
    }

    // 리스크 판별 - 저위험
    public boolean isLowRisk() {
        return grade == AssessmentGrade.A && criticalViolationCount == 0;
    }

    // 결과 확정 처리 메서드
    public void finalizeAssessment(
            int score,
            double actualScore,
            double totalScore,
            AssessmentGrade grade,
            String summary,
            String recommendations
    ) {
        this.score = score;
        this.actualScore = actualScore;
        this.totalPossibleScore = totalScore;
        this.grade = grade;
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

    // 원본 JSON 세터
    public void setAnswersJson(String answersJson) {
        this.answersJson = answersJson;
    }

    // DTO 변환 메서드
    public SelfAssessmentResponse toResponse() {
        return SelfAssessmentResponse.builder()
                .id(this.id)
                .memberId(this.memberId)
                .score(this.score)
                .actualScore(this.actualScore)
                .totalPossibleScore(this.totalPossibleScore)
                .grade(this.grade.name())
                .status(this.status.name())
                .criticalViolationCount(this.criticalViolationCount)
                .completionRate(this.getCompletionRate())
                .summary(this.summary)
                .recommendations(this.recommendations)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .completedAt(this.completedAt)
                .build();
    }

    // 로그용 toString
    @Override
    public String toString() {
        return "SelfAssessmentResult{" +
                "id=" + id +
                ", memberId=" + memberId +
                ", score=" + score +
                ", grade=" + grade +
                ", status=" + status +
                ", criticalViolationCount=" + criticalViolationCount +
                ", createdAt=" + createdAt +
                '}';
    }
}