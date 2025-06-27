package com.nsmm.esg.csddd_service.util;

import com.nsmm.esg.csddd_service.dto.response.ViolationMeta;

import java.util.HashMap;
import java.util.Map;

public class ViolationMetaMap {
    private static final Map<String, ViolationMeta> map = new HashMap<>();

    static {
        map.put("1.1", new ViolationMeta("인권 및 노동", "연매출 5% 이하 벌금", "EU CSDDD Art.6, ILO 138"));
        map.put("1.2", new ViolationMeta("인권 및 노동", "최대 연매출 2% 또는 €8M", "LkSG §3, CSDDD Art.7"));
        map.put("1.3", new ViolationMeta("인권 및 노동", "징벌적 손해배상", "CSDDD Art.6, EU 인권헌장"));
        map.put("1.4", new ViolationMeta("인권 및 노동", "징계, 명단공개, 배상", "ILO 190"));
        map.put("1.5", new ViolationMeta("인권 및 노동", "과태료 및 민사 배상", "근로기준법 §17"));
        map.put("1.6", new ViolationMeta("인권 및 노동", "행정벌금 및 시정명령", "근로기준법 §53-57"));
        map.put("1.7", new ViolationMeta("인권 및 노동", "민사책임/평가불이익", "ILO 87, 98"));
        map.put("1.8", new ViolationMeta("인권 및 노동", "5% 벌금 또는 소송", "CSDDD Art.6"));
        map.put("1.9", new ViolationMeta("인권 및 노동", "행정 권고/공시 의무", "UNGP Principle 31"));

        map.put("2.1", new ViolationMeta("산업안전·보건", "과태료/산재 시 가중", "산안법 §31"));
        map.put("2.2", new ViolationMeta("산업안전·보건", "산재 발생 시 형사책임", "산안법 §36"));
        map.put("2.3", new ViolationMeta("산업안전·보건", "미흡 시 사고책임 확대", "산안법 §41"));
        map.put("2.4", new ViolationMeta("산업안전·보건", "형사처벌 및 과태료", "근기법 §65, §70"));
        map.put("2.5", new ViolationMeta("산업안전·보건", "벌금/유해사고 발생 시 가중", "산안법 §39"));
        map.put("2.6", new ViolationMeta("산업안전·보건", "의무불이행 시 과태료", "산안법 §43"));

        map.put("3.1", new ViolationMeta("환경경영", "입찰 제한/공시 위반", "CSDDD Art.6, ISO 14001"));
        map.put("3.2", new ViolationMeta("환경경영", "EU 지역 벌금/공시 의무", "CSDDD Art.8"));
        map.put("3.3", new ViolationMeta("환경경영", "과태료, 영업정지", "물환경보전법"));
        map.put("3.4", new ViolationMeta("환경경영", "배출 기준 위반 시 과징금", "대기환경보전법"));
        map.put("3.5", new ViolationMeta("환경경영", "위반시 과태료", "폐기물관리법 §13"));
        map.put("3.6", new ViolationMeta("환경경영", "토지사용 제한, 입찰불이익", "EU Biodiversity Strategy"));
        map.put("3.7", new ViolationMeta("환경경영", "벌금/인증취소/CSR등급 하락", "환경범죄경감법"));
        map.put("3.8", new ViolationMeta("환경경영", "보고누락 시 과징금", "CSDDD Art.8"));

        map.put("4.1", new ViolationMeta("공급망 및 조달", "실사 누락 시 연매출 2% 벌금", "CSDDD Art.6"));
        map.put("4.2", new ViolationMeta("공급망 및 조달", "입찰 제한, 계약무효", "LkSG §6"));
        map.put("4.3", new ViolationMeta("공급망 및 조달", "투명성 미흡 시 제재", "OECD Due Diligence"));
        map.put("4.4", new ViolationMeta("공급망 및 조달", "3천만 유로 벌금 가능", "EU 2017/821"));
        map.put("4.5", new ViolationMeta("공급망 및 조달", "수입 금지, 브랜드 퇴출", "미국 UFLPA, CSDDD Art.7"));
        map.put("4.6", new ViolationMeta("공급망 및 조달", "신뢰도 하락, 입찰 불이익", "RBA 승인 필요"));
        map.put("4.7", new ViolationMeta("공급망 및 조달", "미운영 시 C등급 처리", "UNGP Principle 31"));
        map.put("4.8", new ViolationMeta("공급망 및 조달", "공시누락 시 제재", "CSDDD Art.10"));
        map.put("4.9", new ViolationMeta("공급망 및 조달", "계약 무효 가능", "현대차 공급망 가이드"));

        map.put("5.1", new ViolationMeta("윤리경영 및 정보보호", "형사처벌 및 벌금", "OECD 반부패협약"));
        map.put("5.2", new ViolationMeta("윤리경영 및 정보보호", "공시 위반 시 제재", "CSDDD Art.5"));
        map.put("5.3", new ViolationMeta("윤리경영 및 정보보호", "교육 누락 시 인증불가", "RBA VAP 평가 기준"));
        map.put("5.4", new ViolationMeta("윤리경영 및 정보보호", "침해 시 민형사 책임", "지재권법"));
        map.put("5.5", new ViolationMeta("윤리경영 및 정보보호", "최대 연매출 4% 벌금", "GDPR Art.83"));
        map.put("5.6", new ViolationMeta("윤리경영 및 정보보호", "2년 이하 징역/2천만원 이하 벌금", "GDPR, 개인정보보호법"));
        map.put("5.7", new ViolationMeta("윤리경영 및 정보보호", "보고 지연 시 과징금", "NIS2 Directive"));
        map.put("5.8", new ViolationMeta("윤리경영 및 정보보호", "관리자 미지정 시 이행불가 판단", "CSDDD Art.5"));
    }

    public static ViolationMeta get(String questionId) {
        return map.getOrDefault(questionId, new ViolationMeta("", "", ""));
    }
}