package com.nsmm.esg.csddd_service.util;

import com.nsmm.esg.csddd_service.entity.SelfAssessmentAnswer;
import com.nsmm.esg.csddd_service.entity.SelfAssessmentResult;
import com.nsmm.esg.csddd_service.enums.AssessmentGrade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class GradeCalculator {

    /**
     * 최종 평가 결과 계산 후 SelfAssessmentResult에 반영
     * - 총점, 실제 점수 계산
     * - 정규화 점수 산출 (0~100점)
     * - 등급 및 중대 위반 개수 반영
     * - 요약 및 권장 사항도 함께 생성
     */
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

        // null 또는 weight가 null인 항목 제거
        List<SelfAssessmentAnswer> validAnswers = answers.stream()
                .filter(answer -> answer != null && answer.getWeight() != null)
                .toList();

        double totalPossible = validAnswers.stream()
                .mapToDouble(SelfAssessmentAnswer::getWeight)
                .sum();

        double actualScore = validAnswers.stream()
                .filter(a -> Boolean.TRUE.equals(a.isYes()))
                .mapToDouble(SelfAssessmentAnswer::getWeight)
                .sum();

        int normalizedScore = totalPossible == 0 ? 0 : (int) Math.round((actualScore / totalPossible) * 100);

        AssessmentGrade grade = assignGrade(normalizedScore);

        int criticalCount = (int) validAnswers.stream()
                .filter(a -> Boolean.TRUE.equals(a.getCriticalViolation()))
                .count();

        String summary = switch (grade) {
            case A -> "탁월한 이행 수준입니다.";
            case B -> "양호한 이행 상태이나 일부 개선 필요.";
            case C -> "보통 수준이며 개선 여지가 큽니다.";
            case D -> "미흡한 이행 상태로 시급한 개선이 필요합니다.";
        };

        String recommendations = switch (grade) {
            case A -> "지속적으로 현재 수준을 유지하세요.";
            case B -> "위반 항목에 대한 문서 보완을 고려하세요.";
            case C -> "중대 항목에 대한 개선 조치 계획이 필요합니다.";
            case D -> "즉각적인 시정조치 및 모니터링 체계 도입이 필요합니다.";
        };

        result.finalizeAssessment(
                normalizedScore,
                actualScore,
                totalPossible,
                grade,
                summary,
                recommendations
        );
    }

    /**
     * 점수 → 등급 매핑 함수
     * 90 이상: A, 75 이상: B, 60 이상: C, 그 외: D
     */
    private AssessmentGrade assignGrade(int score) {
        if (score >= 90) return AssessmentGrade.A;
        else if (score >= 75) return AssessmentGrade.B;
        else if (score >= 60) return AssessmentGrade.C;
        else return AssessmentGrade.D;
    }
}