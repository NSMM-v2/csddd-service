package com.nsmm.esg.csddd_service.entity;

import com.nsmm.esg.csddd_service.enums.AnswerChoice;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 📄 SelfAssessmentAnswer
 * - 자가진단 결과에 포함된 각 문항의 개별 답변을 저장하는 엔티티
 * - 한 SelfAssessmentResult(결과)와 다대일 관계
 */
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "self_assessment_answer",
        indexes = {
                @Index(name = "idx_result_id", columnList = "result_id"),     // 빠른 검색을 위한 인덱스
                @Index(name = "idx_question_id", columnList = "questionId"),
                @Index(name = "idx_category", columnList = "category")
        })
public class SelfAssessmentAnswer {

    // 기본 키 (자동 증가)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 문항 번호 (예: "1.1", "2.3")
    @Column(nullable = false, length = 20)
    private String questionId;

    // 사용자의 응답 값 (YES, NO, PARTIAL)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private AnswerChoice answer;

    // 해당 문항의 가중치 (ex. 2.5점)
    // ❗ precision, scale 제거로 오류 해결 (MySQL FLOAT 타입은 scale 지정 불가)
    @Column(nullable = false)
    private Double weight;

    // 중대 위반 여부 (true면 리스크로 처리)
    @Builder.Default
    @Column(nullable = false)
    private Boolean criticalViolation = false;

    // 문항 카테고리 (예: 노동, 환경, 공급망 등)
    @Column(nullable = false, length = 100)
    private String category;

    // 사용자의 비고 또는 코멘트
    @Column(length = 500)
    private String remarks;

    // 🔗 SelfAssessmentResult와 다대일 관계 (연결된 결과 ID)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_answer_result"))
    private SelfAssessmentResult result;

    // 생성 시각
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 수정 시각
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // ===== 편의 메서드 =====

    // 응답이 YES인지 확인
    public boolean isYes() {
        return this.answer == AnswerChoice.YES;
    }

    // 응답이 NO인지 확인
    public boolean isNo() {
        return this.answer == AnswerChoice.NO;
    }

    // 응답이 PARTIAL인지 확인
    public boolean isPartial() {
        return this.answer == AnswerChoice.PARTIAL;
    }

    // 응답 업데이트 (answer 문자열을 enum으로 변환)
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
                ", category='" + category + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}