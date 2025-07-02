package com.nsmm.esg.csddd_service.service;

import com.nsmm.esg.csddd_service.dto.request.SelfAssessmentSubmitRequest;
import com.nsmm.esg.csddd_service.entity.SelfAssessmentAnswer;
import com.nsmm.esg.csddd_service.entity.SelfAssessmentResult;
import com.nsmm.esg.csddd_service.enums.AssessmentStatus;
import com.nsmm.esg.csddd_service.repository.SelfAssessmentAnswerRepository;
import com.nsmm.esg.csddd_service.repository.SelfAssessmentResultRepository;
import com.nsmm.esg.csddd_service.util.GradeCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CSDDD 자가진단 서비스
 * 
 * 자가진단 제출, 조회, 점수 계산 등의 비즈니스 로직을 처리
 * 권한 기반 데이터 접근 제어 포함
 * 
 * @author ESG Project Team
 * @version 3.0
 * @since 2024
 * @lastModified 2024-12-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SelfAssessmentService {

    private final SelfAssessmentResultRepository resultRepository;
    private final SelfAssessmentAnswerRepository answerRepository;
    private final GradeCalculator gradeCalculator;

    // ============================================================================
    // 자가진단 제출 처리 (Submit Assessment)
    // ============================================================================

    /**
     * 자가진단 결과 제출 처리
     * 
     * 1. 결과 객체 생성 및 저장
     * 2. 답변 목록 생성 및 연관관계 설정
     * 3. 점수 및 등급 계산
     * 4. 중대위반 건수 계산
     * 5. 최종 저장
     */
    @Transactional
    public void submitSelfAssessment(
            SelfAssessmentSubmitRequest requestDto,
            String userType,
            String headquartersId,
            String partnerId,
            String treePath) {
        log.info("자가진단 제출 시작: 회사={}, 사용자유형={}", requestDto.getCompanyName(), userType);

        // 1. 결과 객체 생성 및 초기 저장 (ID 생성을 위해)
        SelfAssessmentResult result = createInitialResult(requestDto, userType, headquartersId, partnerId, treePath);
        resultRepository.save(result);

        // 2. 답변 목록 생성 및 연관관계 설정
        List<SelfAssessmentAnswer> answers = createAnswersFromRequest(requestDto, result);

        // 3. 양방향 연관관계 설정
        result.assignAnswers(answers);

        // 4. 점수 및 등급 계산
        gradeCalculator.evaluate(result);

        // 5. 중대위반 건수 계산 (Entity의 updateCriticalViolationCount 메서드 사용)
        result.updateCriticalViolationCount();

        // 6. 최종 저장
        answerRepository.saveAll(answers);
        resultRepository.save(result);

        log.info("자가진단 제출 완료: ID={}, 점수={}, 등급={}",
                result.getId(), result.getScore(), result.getFinalGrade());
    }

    // ============================================================================
    // 자가진단 결과 조회 처리 (Read Assessment Results)
    // ============================================================================

    /**
     * 자가진단 결과 단건 조회
     * 
     * 권한에 따른 접근 제어:
     * - 본사: 본사 ID가 일치하는 모든 결과
     * - 협력사: 자신의 협력사 ID가 일치하는 결과만
     */
    @Transactional(readOnly = true)
    public SelfAssessmentResult getSelfAssessmentResult(
            Long resultId,
            String userType,
            Long headquartersId,
            Long partnerId,
            String treePath) {
        log.info("자가진단 결과 조회: ID={}, 사용자유형={}", resultId, userType);

        SelfAssessmentResult result = resultRepository.findById(resultId)
                .orElseThrow(() -> new IllegalArgumentException("해당 자가진단 결과를 찾을 수 없습니다."));

        // 권한 검증
        validateAccessPermission(result, userType, headquartersId, partnerId);

        return result;
    }

    /**
     * 자가진단 결과 목록 조회 (조건 + 페이징)
     * 
     * 사용자 유형별 필터링:
     * - 본사: 모든 결과 또는 협력사 결과만
     * - 협력사: 자신의 결과 또는 하위 협력사 결과
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
            Boolean onlyPartners) {
        log.info("자가진단 결과 목록 조회: 사용자유형={}, 본사ID={}, 협력사ID={}",
                userType, headquartersId, partnerId);

        Specification<SelfAssessmentResult> spec = createSearchSpecification(
                userType, headquartersId, partnerId, treePath,
                companyName, category, startDate, endDate, onlyPartners);

        return resultRepository.findAll(spec, pageable);
    }

    // ============================================================================
    // 프라이빗 헬퍼 메서드 (Private Helper Methods)
    // ============================================================================

    /**
     * 초기 결과 객체 생성
     */
    private SelfAssessmentResult createInitialResult(
            SelfAssessmentSubmitRequest requestDto,
            String userType,
            String headquartersId,
            String partnerId,
            String treePath) {
        return SelfAssessmentResult.builder()
                .companyName(requestDto.getCompanyName())
                .userType(userType)
                .headquartersId(parseToLong(headquartersId))
                .partnerId(partnerId != null ? parseToLong(partnerId) : null)
                .treePath(treePath)
                .status(AssessmentStatus.IN_PROGRESS)
                .build();
    }

    /**
     * 요청 DTO에서 답변 엔티티 목록 생성
     */
    private List<SelfAssessmentAnswer> createAnswersFromRequest(
            SelfAssessmentSubmitRequest requestDto,
            SelfAssessmentResult result) {
        return requestDto.getAnswers().stream()
                .map(answerRequest -> SelfAssessmentAnswer.builder()
                        .questionId(answerRequest.getQuestionId())
                        .category(answerRequest.getCategory())
                        .remarks(answerRequest.getRemarks())
                        .weight(answerRequest.getWeight())
                        .answer(convertAnswerStringToBoolean(answerRequest.getAnswer()))
                        .criticalViolation(answerRequest.getCritical() != null ? answerRequest.getCritical() : false)
                        .criticalGrade(answerRequest.getCriticalGrade())
                        .result(result)
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 접근 권한 검증
     */
    private void validateAccessPermission(
            SelfAssessmentResult result,
            String userType,
            Long headquartersId,
            Long partnerId) {
        if ("HEADQUARTERS".equalsIgnoreCase(userType)) {
            if (!result.getHeadquartersId().equals(headquartersId)) {
                throw new SecurityException("해당 자가진단 결과에 접근할 권한이 없습니다.");
            }
        } else if ("PARTNER".equalsIgnoreCase(userType)) {
            if (result.getPartnerId() == null || !result.getPartnerId().equals(partnerId)) {
                throw new SecurityException("해당 자가진단 결과에 접근할 권한이 없습니다.");
            }
        } else {
            throw new IllegalArgumentException("유효하지 않은 사용자 유형입니다.");
        }
    }

    /**
     * 검색 조건 Specification 생성
     */
    private Specification<SelfAssessmentResult> createSearchSpecification(
            String userType,
            Long headquartersId,
            Long partnerId,
            String treePath,
            String companyName,
            String category,
            String startDate,
            String endDate,
            Boolean onlyPartners) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new java.util.ArrayList<>();

            // 기본 권한 필터링
            addUserTypePredicates(predicates, root, cb, userType, headquartersId, partnerId, treePath, onlyPartners);

            // 검색 조건 추가
            addSearchPredicates(predicates, root, cb, companyName, category, startDate, endDate);

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * 사용자 유형별 권한 Predicate 추가
     */
    private void addUserTypePredicates(
            List<Predicate> predicates,
            jakarta.persistence.criteria.Root<SelfAssessmentResult> root,
            jakarta.persistence.criteria.CriteriaBuilder cb,
            String userType,
            Long headquartersId,
            Long partnerId,
            String treePath,
            Boolean onlyPartners) {
        if ("PARTNER".equalsIgnoreCase(userType)) {
            predicates.add(cb.equal(root.get("headquartersId"), headquartersId));

            if (Boolean.TRUE.equals(onlyPartners)) {
                // 하위 협력사 결과만 조회
                if (treePath != null && !treePath.isEmpty()) {
                    predicates.add(cb.like(root.get("treePath"), treePath + "/%"));
                    predicates.add(cb.notEqual(root.get("treePath"), treePath));
                }
            } else {
                // 자신의 결과만 조회
                if (treePath != null && !treePath.isEmpty()) {
                    predicates.add(cb.equal(root.get("treePath"), treePath));
                }
            }
        } else if ("HEADQUARTERS".equalsIgnoreCase(userType)) {
            predicates.add(cb.equal(root.get("headquartersId"), headquartersId));

            if (Boolean.TRUE.equals(onlyPartners)) {
                // 협력사 결과만
                predicates.add(cb.isNotNull(root.get("partnerId")));
            } else {
                // 본사 결과만
                predicates.add(cb.isNull(root.get("partnerId")));
            }
        }
    }

    /**
     * 검색 조건 Predicate 추가
     */
    private void addSearchPredicates(
            List<Predicate> predicates,
            jakarta.persistence.criteria.Root<SelfAssessmentResult> root,
            jakarta.persistence.criteria.CriteriaBuilder cb,
            String companyName,
            String category,
            String startDate,
            String endDate) {
        if (companyName != null && !companyName.trim().isEmpty()) {
            predicates.add(cb.like(root.get("companyName"), "%" + companyName.trim() + "%"));
        }

        if (category != null && !category.trim().isEmpty()) {
            // TODO: 카테고리 필터링 로직 (답변 테이블과 조인 필요)
            log.warn("카테고리 필터링은 아직 구현되지 않았습니다: {}", category);
        }

        if (startDate != null && endDate != null) {
            try {
                LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
                LocalDateTime endDateTime = LocalDate.parse(endDate).atTime(23, 59, 59);
                predicates.add(cb.between(root.get("createdAt"), startDateTime, endDateTime));
            } catch (Exception e) {
                log.warn("날짜 파싱 실패: 시작날짜={}, 종료날짜={}", startDate, endDate);
            }
        }
    }

    /**
     * 문자열을 Long으로 안전하게 변환
     */
    private Long parseToLong(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("유효하지 않은 숫자 형식입니다: " + value);
        }
    }

    /**
     * "yes", "no" → boolean 변환
     */
    private boolean convertAnswerStringToBoolean(String answer) {
        return answer != null && "yes".equalsIgnoreCase(answer.trim());
    }
}