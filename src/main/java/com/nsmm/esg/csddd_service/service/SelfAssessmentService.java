package com.nsmm.esg.csddd_service.service;

import com.nsmm.esg.csddd_service.dto.request.SelfAssessmentSubmitRequest;
import com.nsmm.esg.csddd_service.entity.SelfAssessmentAnswer;
import com.nsmm.esg.csddd_service.entity.SelfAssessmentResult;
import com.nsmm.esg.csddd_service.repository.SelfAssessmentAnswerRepository;
import com.nsmm.esg.csddd_service.repository.SelfAssessmentResultRepository;
import com.nsmm.esg.csddd_service.util.GradeCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SelfAssessmentService {

    private final SelfAssessmentResultRepository resultRepository;
    private final SelfAssessmentAnswerRepository answerRepository;
    private final GradeCalculator gradeCalculator;

    /**
     * 자가진단 결과 제출 처리
     */
    @Transactional
    public void submitSelfAssessment(
            SelfAssessmentSubmitRequest requestDto,
            String userType,
            String headquartersId,
            String partnerId,
            String treePath
    ) {
        // 1. 결과 객체 생성 및 저장 (ID 필요하므로 먼저 save)
        SelfAssessmentResult result = SelfAssessmentResult.builder()
                .companyName(requestDto.getCompanyName())
                .userType(userType)
                .headquartersId(Long.parseLong(headquartersId))
                .partnerId(partnerId != null ? Long.parseLong(partnerId) : null)
                .treePath(treePath)
                .build();

        resultRepository.save(result);

        // 2. 답변 목록 생성 (result 연결 필요)
        List<SelfAssessmentAnswer> answers = requestDto.getAnswers().stream()
                .map(req -> SelfAssessmentAnswer.builder()
                        .questionId(req.getQuestionId())
                        .category(req.getCategory())
                        .remarks(req.getRemarks())
                        .weight(req.getWeight())
                        .answer(convertAnswerStringToBoolean(req.getAnswer()))
                        .criticalViolation(req.getCritical() != null ? req.getCritical() : false)
                        .criticalGrade(req.getCriticalGrade())
                        .result(result)
                        .build())
                .collect(Collectors.toList());

        // 3. 점수 계산
        result.setAnswers(answers); // 연관관계 설정
        gradeCalculator.evaluate(result);


        // 3.5 중대위반 건수 계산 추가
        long criticalCount = answers.stream()
                .filter(a -> Boolean.TRUE.equals(a.getCriticalViolation()) && !a.isAnswer())
                .count();
        result.updateCriticalViolationCount((int) criticalCount);


        // 4. 저장
        answerRepository.saveAll(answers);     // 답변 저장
        resultRepository.save(result);         // 계산된 점수 포함하여 다시 저장
    }

    /**
     * 자가진단 결과 수정 처리
     */


    /**
     * 자가진단 결과 단건 조회 처리
     */
    @Transactional(readOnly = true)
    public SelfAssessmentResult getSelfAssessmentResult(
            Long resultId,
            String userType,
            Long headquartersId,
            Long partnerId,
            String treePath
    ) {
        if ("HEADQUARTERS".equalsIgnoreCase(userType)) {
            return resultRepository.findById(resultId)
                    .filter(result -> result.getHeadquartersId().equals(headquartersId))
                    .orElseThrow(() -> new IllegalArgumentException("해당 자가진단 결과를 찾을 수 없습니다."));
        } else if ("PARTNER".equalsIgnoreCase(userType)) {
            return resultRepository.findById(resultId)
                    .filter(result -> result.getPartnerId() != null && result.getPartnerId().equals(partnerId))
                    .orElseThrow(() -> new IllegalArgumentException("해당 자가진단 결과를 찾을 수 없습니다."));
        } else {
            throw new IllegalArgumentException("유효하지 않은 사용자 유형입니다.");
        }
    }

    /**
     * 자가진단 결과 목록 조회 (조건 + 페이징)
     */
    @Transactional(readOnly = true)
    public Page<SelfAssessmentResult> getSelfAssessmentResults(
            String userType,
            Long headquartersId,
            Long partnerId,
            String treePath,
            String companyName,
            String category,
            String startDate,
            String endDate,
            Pageable pageable,
            Boolean onlyPartners
    ) {
        System.out.println("🔍 HQ: " + headquartersId + ", PartnerID: " + partnerId + ", TreePath: " + treePath + ", UserType: " + userType);
        Specification<SelfAssessmentResult> spec = (root, query, cb) -> {
            List<Predicate> predicates = new java.util.ArrayList<>();

            if ("PARTNER".equalsIgnoreCase(userType)) {
                predicates.add(cb.equal(root.get("headquartersId"), headquartersId));

                if (Boolean.TRUE.equals(onlyPartners)) {
                    // partnerEvaluation: 하위 파트너만 (자기 제외)
                    if (treePath != null && !treePath.isEmpty()) {
                        predicates.add(cb.like(root.get("treePath"), treePath + "/%"));
                        predicates.add(cb.notEqual(root.get("treePath"), treePath));
                    }
                } else {
                    // evaluation: 자기 결과만
                    if (treePath != null && !treePath.isEmpty()) {
                        predicates.add(cb.equal(root.get("treePath"), treePath));
                    }
                }
            } else if ("HEADQUARTERS".equalsIgnoreCase(userType)) {
                predicates.add(cb.equal(root.get("headquartersId"), headquartersId));

                if (Boolean.TRUE.equals(onlyPartners)) {
                    predicates.add(cb.isNotNull(root.get("partnerId")));
                } else {
                    predicates.add(cb.isNull(root.get("partnerId")));
                }
            }

            if (companyName != null && !companyName.isEmpty()) {
                predicates.add(cb.like(root.get("companyName"), "%" + companyName + "%"));
            }

            if (category != null && !category.isEmpty()) {
                predicates.add(cb.equal(root.get("category"), category));
            }

            if (startDate != null && endDate != null) {
                predicates.add(cb.between(
                        root.get("createdAt"),
                        java.time.LocalDate.parse(startDate).atStartOfDay(),
                        java.time.LocalDate.parse(endDate).atTime(23, 59, 59)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return resultRepository.findAll(spec, pageable);
    }

    /**
     * "yes", "no" → boolean 변환
     */
    private boolean convertAnswerStringToBoolean(String answer) {
        return answer != null && "yes".equalsIgnoreCase(answer.trim());
    }
}