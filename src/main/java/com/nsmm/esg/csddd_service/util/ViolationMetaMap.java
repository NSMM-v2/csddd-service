package com.nsmm.esg.csddd_service.util;

import com.nsmm.esg.csddd_service.dto.response.ViolationMeta;

import java.util.HashMap;
import java.util.Map;

public class ViolationMetaMap {
    private static final Map<String, ViolationMeta> map = new HashMap<>();

    static {
        map.put("1.1", new ViolationMeta("인권 및 노동", "연매출 5% 이하 벌금", "EU CSDDD Art.6, ILO 138"));
        map.put("1.2", new ViolationMeta("인권 및 노동", "최대 연매출 2% 또는 €8M", "LkSG §3, CSDDD Art.7"));
        map.put("1.3", new ViolationMeta("인권 및 노동", "징벌적 손해배상 가능", "CSDDD Art.6, EU 인권헌장"));
        map.put("1.4", new ViolationMeta("인권 및 노동", "징계/명단공개/배상", "ILO 190"));
        map.put("1.5", new ViolationMeta("인권 및 노동", "과태료 및 민사 배상", "근로기준법 §17"));
        map.put("1.6", new ViolationMeta("인권 및 노동", "행정벌금 및 시정명령", "근로기준법 §53~57"));
        map.put("1.7", new ViolationMeta("인권 및 노동", "민사책임/평가불이익", "ILO 87, 98"));
        map.put("1.8", new ViolationMeta("인권 및 노동", "5% 벌금 또는 소송", "CSDDD Art.6"));
        map.put("1.9", new ViolationMeta("인권 및 노동", "행정 권고/공시 의무", "UNGP Guiding Principle 31"));
        map.put("2.1", new ViolationMeta("산업안전·보건", "과태료/산재 시 가중", "산안법 §31"));
        map.put("2.7", new ViolationMeta("산업안전·보건", "벌금/책임자 처벌", "산안법 §28"));
        map.put("2.8", new ViolationMeta("산업안전·보건", "과태료 및 보고의무", "산안법 §43, §45"));
        map.put("2.9", new ViolationMeta("산업안전·보건", "형사책임, 과태료", "산안법 §31"));
        map.put("3.1", new ViolationMeta("환경경영", "환경벌금 및 영업정지", "환경정책기본법 §15"));
        map.put("3.2", new ViolationMeta("환경경영", "과태료/처벌", "폐기물관리법 §13"));
        map.put("3.3", new ViolationMeta("환경경영", "민사책임/이미지 타격", "환경범죄 단속법 §4"));
        map.put("3.4", new ViolationMeta("환경경영", "형사처벌 및 벌금", "환경정책기본법 §38"));
        map.put("3.5", new ViolationMeta("환경경영", "이행강제금/사업정지", "대기환경보전법 §19"));
        map.put("3.6", new ViolationMeta("환경경영", "과태료/형사처벌", "수질 및 수생태계 보전법 §12"));
        map.put("3.7", new ViolationMeta("환경경영", "위반 시 환경부 공표", "환경오염시설법 §9"));
        map.put("4.1", new ViolationMeta("공급망 및 조달", "계약 해지 및 불이익", "공정거래법 §23"));
        map.put("4.2", new ViolationMeta("공급망 및 조달", "법적 제재/공정위 조치", "공정거래법 §23"));
        map.put("4.3", new ViolationMeta("공급망 및 조달", "법적 제재 및 손해배상", "공정거래법 §23의2"));
        map.put("4.4", new ViolationMeta("공급망 및 조달", "과징금/영업정지", "하도급법 §12"));
        map.put("4.5", new ViolationMeta("공급망 및 조달", "과징금 및 공정위 시정조치", "대규모유통업법 §17"));
        map.put("4.6", new ViolationMeta("공급망 및 조달", "공정위 제재/계약파기", "전자상거래법 §21"));
        map.put("4.7", new ViolationMeta("공급망 및 조달", "국제분쟁/계약해지", "OECD 공급망지침"));
        map.put("4.8", new ViolationMeta("공급망 및 조달", "법적 책임 및 명단 공개", "공정거래법 §23"));
        map.put("5.1", new ViolationMeta("윤리경영 및 정보보호", "형사처벌 및 과징금", "정보보호법 §28"));
        map.put("5.2", new ViolationMeta("윤리경영 및 정보보호", "위반 시 신고 및 제재", "부패방지법 §8"));
        map.put("5.3", new ViolationMeta("윤리경영 및 정보보호", "위반 시 손해배상", "부정청탁금지법 §5"));
        map.put("5.4", new ViolationMeta("윤리경영 및 정보보호", "형사처벌 가능", "개인정보보호법 §71"));
        map.put("5.5", new ViolationMeta("윤리경영 및 정보보호", "내부고발 시 보호조치", "공익신고자보호법 §12"));
        map.put("5.6", new ViolationMeta("윤리경영 및 정보보호", "불이익 처분 및 과태료", "청탁금지법 §23"));
        map.put("5.7", new ViolationMeta("윤리경영 및 정보보호", "계약 파기 및 명단 공개", "기업윤리헌장"));
        map.put("5.8", new ViolationMeta("윤리경영 및 정보보호", "국제 제재 및 신뢰 하락", "UN Global Compact"));
    }

    public static ViolationMeta get(String questionId) {
        return map.getOrDefault(questionId, new ViolationMeta("", "", ""));
    }
}
