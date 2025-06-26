package com.nsmm.esg.csddd_service.entity;

import com.nsmm.esg.csddd_service.enums.AnswerChoice;
import com.nsmm.esg.csddd_service.enums.AssessmentGrade;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "self_assessment_answer",
        indexes = {
                @Index(name = "idx_result_id", columnList = "result_id"),
                @Index(name = "idx_question_id", columnList = "questionId"),
                @Index(name = "idx_category", columnList = "category")
        })
public class SelfAssessmentAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String questionId;

    @Column(name = "question_text", length = 1000)
    private String questionText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private AnswerChoice answer;

    @Column(nullable = false)
    private Double weight;

    @Builder.Default
    @Column(nullable = false)
    private Boolean criticalViolation = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "critical_grade", length = 5)
    private AssessmentGrade criticalGrade;

    @Column(nullable = false, length = 100)
    private String category;

    @Column(length = 500)
    private String remarks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_answer_result"))
    private SelfAssessmentResult result;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public boolean isYes() {
        return this.answer == AnswerChoice.YES;
    }

    public boolean isNo() {
        return this.answer == AnswerChoice.NO;
    }

    public boolean isPartial() {
        return this.answer == AnswerChoice.PARTIAL;
    }

    public void updateAnswer(String answer, String remarks) {
        try {
            this.answer = AnswerChoice.valueOf(answer.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid answer value: " + answer);
        }
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return "SelfAssessmentAnswer{" +
                "id=" + id +
                ", questionId='" + questionId + '\'' +
                ", answer=" + answer +
                ", weight=" + weight +
                ", criticalViolation=" + criticalViolation +
                ", criticalGrade=" + criticalGrade +
                ", category='" + category + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}