package com.nsmm.esg.csddd_service.util;

import com.nsmm.esg.csddd_service.entity.SelfAssessmentAnswer;
import com.nsmm.esg.csddd_service.entity.SelfAssessmentResult;
import com.nsmm.esg.csddd_service.enums.AssessmentGrade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class GradeCalculator {

    public void evaluate(SelfAssessmentResult result) {
        List<SelfAssessmentAnswer> answers = result.getAnswers();

        if (answers == null || answers.isEmpty()) {
            result.finalizeAssessment(
                    0, 0, 0,
                    AssessmentGrade.D,
                    "평가할 데이터가 없습니다.",
                    "필수 항목을 모두 입력해주세요."
            );
            return;
        }

        // 유효한 답변 필터링
        List<SelfAssessmentAnswer> validAnswers = answers.stream()
                .filter(a -> a != null && a.getWeight() != null)
                .toList();

        double totalWeight = validAnswers.stream()
                .mapToDouble(SelfAssessmentAnswer::getWeight)
                .sum();

        double actualScore = validAnswers.stream()
                .filter(a -> a.isAnswer())
                .mapToDouble(SelfAssessmentAnswer::getWeight)
                .sum();

        int normalizedScore = totalWeight == 0 ? 0 : (int) Math.round((actualScore / totalWeight) * 100);

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
                normalizedScore,   // 점수 (0~100)
                actualScore,       // 실제 획득 점수
                totalWeight,       // 총 가능 점수
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