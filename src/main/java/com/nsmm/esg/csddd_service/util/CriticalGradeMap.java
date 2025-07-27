package com.nsmm.esg.csddd_service.util;

import com.nsmm.esg.csddd_service.enums.AssessmentGrade;

import java.util.HashMap;
import java.util.Map;

public class CriticalGradeMap {

    private static final Map<String, AssessmentGrade> map = new HashMap<>();

    static {
        map.put("1.1", AssessmentGrade.D);
        map.put("1.4", AssessmentGrade.C);
        map.put("1.6", AssessmentGrade.C);
        map.put("1.7", AssessmentGrade.C);
        map.put("1.8", AssessmentGrade.C);
        map.put("1.9", AssessmentGrade.C);
        map.put("2.4", AssessmentGrade.C);
        map.put("2.5", AssessmentGrade.C);
        map.put("3.2", AssessmentGrade.B);
        map.put("3.7", AssessmentGrade.C);
        map.put("4.1", AssessmentGrade.C);
        map.put("4.3", AssessmentGrade.C);
        map.put("4.4", AssessmentGrade.D);
        map.put("4.5", AssessmentGrade.D);
        map.put("4.7", AssessmentGrade.C);
        map.put("4.8", AssessmentGrade.C);
        map.put("5.1", AssessmentGrade.D);
        map.put("5.2", AssessmentGrade.C);
        map.put("5.5", AssessmentGrade.C);
        map.put("5.6", AssessmentGrade.D);
    }

    public static AssessmentGrade getGradeByQuestionId(String questionId) {
        return map.get(questionId);
    }
}