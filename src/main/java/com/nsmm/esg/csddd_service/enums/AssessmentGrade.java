package com.nsmm.esg.csddd_service.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 *  AssessmentGrade
 * - 자가진단 결과에 따라 매겨지는 평가 등급(Enum)
 * - 점수 기준 또는 위반사항 수에 따라 A ~ D 또는 B/C 로 등급 분류
 *
 * A: 매우 우수
 * B: 양호
 * C: 보통
 * D: 미흡 / 위험 수준
 * B/C: 경계 등급
 */
public enum AssessmentGrade {
    A,
    B,
    C,
    D,
    B_C; // B/C 등급 대응용 내부 표현

    @JsonValue
    public String toValue() {
        return this == B_C ? "B/C" : name();
    }

    @JsonCreator
    public static AssessmentGrade fromValue(String value) {
        if ("B/C".equalsIgnoreCase(value)) {
            return B_C;
        }
        return AssessmentGrade.valueOf(value);
    }
}