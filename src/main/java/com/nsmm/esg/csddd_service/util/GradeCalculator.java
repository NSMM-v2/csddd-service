package com.nsmm.esg.csddd_service.util;

import com.nsmm.esg.csddd_service.entity.SelfAssessmentAnswer;
import com.nsmm.esg.csddd_service.entity.SelfAssessmentResult;
import com.nsmm.esg.csddd_service.enums.AssessmentGrade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * CSDDD 자가진단 점수 및 등급 계산 유틸리티
 * - 각 문항은 기본점수 2.5점이며 가중치(weight)가 곱해져 총점이 결정됨
 * - YES 응답 항목만 실제 점수로 인정됨
 * - 최종 점수는 100점 만점 기준으로 환산됨
 * - 중대 위반 항목(NO 응답) 존재 시 등급은 자동 강등될 수 있음
 */
@Slf4j
@Component
public class GradeCalculator {

    public void evaluate(SelfAssessmentResult result) {
        List<SelfAssessmentAnswer> answers = result.getAnswers();

        if (answers == null || answers.isEmpty()) {
            log.warn("자가진단 답변이 비어있습니다. 평가를 건너뜁니다.");
            result.finalizeAssessment(
                    0, 0, 0,
                    AssessmentGrade.D,
                    "평가할 데이터가 없습니다.",
                    "필수 항목을 모두 입력해주세요."
            );
            return;
        }

        final double BASE_SCORE = 2.5;

        // 유효한 답변 필터링
        List<SelfAssessmentAnswer> validAnswers = answers.stream()
                .filter(a -> a != null && a.getWeight() != null)
                .toList();

        // 총점 및 실제 점수 계산 (2.5 × weight)
        double totalPossibleScore = validAnswers.stream()
                .mapToDouble(a -> BASE_SCORE * a.getWeight())
                .sum();

        double actualScore = validAnswers.stream()
                .filter(SelfAssessmentAnswer::isAnswer)
                .mapToDouble(a -> BASE_SCORE * a.getWeight())
                .sum();

        int normalizedScore = totalPossibleScore == 0 ? 0 :
                (int) Math.round((actualScore / totalPossibleScore) * 100);

        // 중대위반 항목 중 사용자가 '아니오(false)'로 응답한 항목만 필터링
        List<AssessmentGrade> criticalGrades = validAnswers.stream()
                .filter(a -> Boolean.TRUE.equals(a.getCriticalViolation()) && !a.isAnswer())
                .map(SelfAssessmentAnswer::getQuestionId)
                .map(this::getCriticalGrade)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        AssessmentGrade finalGrade = criticalGrades.isEmpty()
                ? AssessmentGrade.fromScore(normalizedScore, false)
                : criticalGrades.stream().min(Comparator.naturalOrder()).orElse(AssessmentGrade.D);

        String summary = switch (finalGrade) {
            case A -> "탁월한 이행 수준입니다.";
            case B -> "양호한 이행 상태이나 일부 개선 필요.";
            case C -> "보통 수준이며 개선 여지가 큽니다.";
            case D -> "미흡한 이행 상태로 시급한 개선이 필요합니다.";
        };

        String recommendation = switch (finalGrade) {
            case A -> "지속적으로 현재 수준을 유지하세요.";
            case B -> "위반 항목에 대한 문서 보완을 고려하세요.";
            case C -> "중대 항목에 대한 개선 조치 계획이 필요합니다.";
            case D -> "즉각적인 시정조치 및 모니터링 체계 도입이 필요합니다.";
        };

        result.finalizeAssessment(
                normalizedScore,
                actualScore,
                totalPossibleScore,
                finalGrade,
                summary,
                recommendation
        );
    }

    /**
     * 프론트에서 관리 중인 criticalViolation.grade 정보를 기반으로 등급 반환
     */
    private Optional<AssessmentGrade> getCriticalGrade(String questionId) {
        return Optional.ofNullable(CriticalGradeMap.getGradeByQuestionId(questionId));
    }
}