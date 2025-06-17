package com.nsmm.esg.csddd_service.util;

import com.nsmm.esg.csddd_service.entity.SelfAssessmentAnswer;
import com.nsmm.esg.csddd_service.entity.SelfAssessmentResult;
import com.nsmm.esg.csddd_service.enums.AssessmentGrade;
import com.nsmm.esg.csddd_service.enums.AssessmentStatus;
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

        // 전체 문항의 가중치 총합
        double totalPossible = answers.stream()
                .mapToDouble(SelfAssessmentAnswer::getWeight)
                .sum();

        // "YES"로 답한 문항들의 가중치 총합 = 실제 점수
        double actualScore = answers.stream()
                .filter(SelfAssessmentAnswer::isYes)
                .mapToDouble(SelfAssessmentAnswer::getWeight)
                .sum();

        // 실제 점수 / 총점 → 100점 기준 정규화
        int normalizedScore = totalPossible == 0 ? 0 : (int) Math.round((actualScore / totalPossible) * 100);

        // 점수 기반 등급 계산
        AssessmentGrade grade = assignGrade(normalizedScore);

        // 중대 위반 문항 수 계산
        int criticalCount = (int) answers.stream()
                .filter(SelfAssessmentAnswer::getCriticalViolation)
                .count();

        // 간단한 요약 문구 (등급 기반)
        String summary = switch (grade) {
            case A -> "탁월한 이행 수준입니다.";
            case B -> "양호한 이행 상태이나 일부 개선 필요.";
            case C -> "보통 수준이며 개선 여지가 큽니다.";
            case D -> "미흡한 이행 상태로 시급한 개선이 필요합니다.";
        };

        // 권장 조치 문구
        String recommendations = switch (grade) {
            case A -> "지속적으로 현재 수준을 유지하세요.";
            case B -> "위반 항목에 대한 문서 보완을 고려하세요.";
            case C -> "중대 항목에 대한 개선 조치 계획이 필요합니다.";
            case D -> "즉각적인 시정조치 및 모니터링 체계 도입이 필요합니다.";
        };

        // 결과에 모든 계산값 반영 (finalizeAssessment는 엔티티 편의 메서드)
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