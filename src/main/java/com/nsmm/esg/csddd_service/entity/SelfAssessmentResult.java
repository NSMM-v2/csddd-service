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

@Entity
@Table(name = "self_assessment_results",
        indexes = {
                @Index(name = "idx_member_id", columnList = "memberId"),
                @Index(name = "idx_member_created", columnList = "memberId, createdAt"),
                @Index(name = "idx_grade", columnList = "grade"),
                @Index(name = "idx_status", columnList = "status"),
                @Index(name = "idx_user_type", columnList = "userType"),
                @Index(name = "idx_headquarters_id", columnList = "headquartersId")
        })
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SelfAssessmentResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 사용자 ID (본사 또는 협력사)
    @Column(nullable = false)
    private Long memberId;
    // SelfAssessmentResult.java
    @Column(length = 100)
    private String companyName;
    // 사용자 유형 (HEADQUARTERS or PARTNER)
    @Column(nullable = false, length = 20)
    private String userType;

    // 소속 본사 ID (협력사는 소속 HQ ID, 본사는 자기 ID)
    @Column(nullable = false)
    private Long headquartersId;

    // 정규화된 점수 (0~100)
    @Column(nullable = false)
    private Integer score;

    // 실질적 점수 (ex. 34.5)
    @Column(nullable = false)
    private Double actualScore;

    // 가능한 총점 (ex. 40.0)
    @Column(nullable = false)
    private Double totalPossibleScore;

    // 등급 (A/B/C/D)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private AssessmentGrade grade;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AssessmentStatus status = AssessmentStatus.COMPLETED;

//    // 원본 요청 JSON
//    @Column(nullable = false, columnDefinition = "TEXT")
//    private String answersJson;

    @Column(length = 1000)
    private String summary;

    @Column(length = 2000)
    private String recommendations;

    @Builder.Default
    @Column(nullable = false)
    private Integer criticalViolationCount = 0;

    @OneToMany(mappedBy = "result", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    private List<SelfAssessmentAnswer> answers = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime completedAt;

    // === 편의 메서드 ===

    public void addAnswer(SelfAssessmentAnswer answer) {
        this.answers.add(answer);
    }

    public void removeAnswer(SelfAssessmentAnswer answer) {
        this.answers.remove(answer);
    }

    public List<SelfAssessmentAnswer> getCriticalViolations() {
        return answers.stream()
                .filter(SelfAssessmentAnswer::getCriticalViolation)
                .collect(Collectors.toList());
    }

    public Map<String, List<SelfAssessmentAnswer>> getAnswersByCategory() {
        return answers.stream()
                .collect(Collectors.groupingBy(SelfAssessmentAnswer::getCategory));
    }

    public Double getCompletionRate() {
        if (totalPossibleScore == null || totalPossibleScore == 0) {
            return 0.0;
        }
        return (actualScore / totalPossibleScore) * 100;
    }

    public boolean isHighRisk() {
        return grade == AssessmentGrade.D || criticalViolationCount > 0;
    }

    public boolean isLowRisk() {
        return grade == AssessmentGrade.A && criticalViolationCount == 0;
    }

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

//    public void setAnswersJson(String answersJson) {
//        this.answersJson = answersJson;
//    }

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
                .companyName(this.companyName)
                .build();
    }

    @Override
    public String toString() {
        return "SelfAssessmentResult{" +
                "id=" + id +
                ", memberId=" + memberId +
                ", userType=" + userType +
                ", headquartersId=" + headquartersId +
                ", score=" + score +
                ", grade=" + grade +
                ", status=" + status +
                ", criticalViolationCount=" + criticalViolationCount +
                ", createdAt=" + createdAt +
                '}';
    }
}