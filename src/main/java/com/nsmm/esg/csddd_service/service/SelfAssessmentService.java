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
            Pageable pageable
    ) {
        Specification<SelfAssessmentResult> spec = (root, query, cb) -> {
            List<Predicate> predicates = new java.util.ArrayList<>();

            if ("PARTNER".equalsIgnoreCase(userType)) {
                // partnerId 조건은 제거하고, treePath 기준으로 자기 자신 + 자식까지 조회
                predicates.add(cb.equal(root.get("headquartersId"), headquartersId));
                predicates.add(cb.like(root.get("treePath"), treePath + "%"));
            } else if ("HEADQUARTERS".equalsIgnoreCase(userType)) {
                // 본사는 전체 treePath 하위 조회 (모든 파트너 포함)
                predicates.add(cb.equal(root.get("headquartersId"), headquartersId));
                predicates.add(cb.like(root.get("treePath"), treePath + "%"));
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