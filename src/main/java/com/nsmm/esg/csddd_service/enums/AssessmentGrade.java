package com.nsmm.esg.csddd_service.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * AssessmentGrade
 * - 자가진단 결과에 따라 매겨지는 평가 등급 (A ~ D)
 *
 * A: 매우 우수 (90점 이상)
 * B: 양호 (75점 이상)
 * C: 보통 (60점 이상)
 * D: 미흡 / 위험 수준 (60점 미만 또는 중대위반 존재)
 */
public enum AssessmentGrade {
    D, C, B, A;

    @JsonValue
    public String toValue() {
        return name();
    }

    @JsonCreator
    public static AssessmentGrade fromValue(String value) {
        return AssessmentGrade.valueOf(value.toUpperCase());
    }

    public static AssessmentGrade fromScore(int score, boolean hasCriticalViolation) {
        if (hasCriticalViolation) return D;
        if (score >= 90) return A;
        if (score >= 75) return B;
        if (score >= 60) return C;
        return D;
    }

    public static AssessmentGrade fromScoreWithCriticals(int score, java.util.List<AssessmentGrade> criticalGrades) {
        if (criticalGrades != null && !criticalGrades.isEmpty()) {
            return criticalGrades.stream()
                    .min(java.util.Comparator.naturalOrder())
                    .orElse(D);
        }
        return fromScore(score, false);
    }
}