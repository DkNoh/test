package com.example.sms.util;

/**
 * [MaskingUtil]
 * 시스템 내에서 취급되는 개인정보(이름, 전화번호, 주민번호, 카드번호 등)를 UI로 내려보내거나 
 * 로그에 기록할 때 안전하게 마스킹(*) 처리해 주는 전용 유틸리티 클래스입니다.
 * 
 * [사용법(Usage)]
 * 1. 전화번호 마스킹: MaskingUtil.maskPhone("01012345678"); // 결과: 010-****-5678
 * 2. 이름 마스킹:   MaskingUtil.maskName("홍길동");      // 결과: 홍*동
 * 3. 주민번호 마스킹: MaskingUtil.maskRrn("9001011234567");  // 결과: 900101-1******
 * 4. 카드번호 마스킹: MaskingUtil.maskCard("1234567812345678"); // 결과: 1234-****-****-5678
 */
public class MaskingUtil {

    private MaskingUtil() {
        // 인스턴스화 방지
    }

    /**
     * 이름 마스킹
     * - 2글자: 끝 글자 마스킹 (예: 홍길 -> 홍*)
     * - 3글자 이상: 첫 글자와 끝 글자를 제외한 중간 글자 모두 마스킹 (예: 홍길동 -> 홍*동, 남궁민수 -> 남**수)
     * 
     * @param name 원본 이름
     * @return 마스킹 처리된 이름
     */
    public static String maskName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return name;
        }

        String targetName = name.trim();
        int length = targetName.length();

        if (length < 2) {
            return targetName; // 1글자는 마스킹 안 함
        }
        
        if (length == 2) {
            return targetName.charAt(0) + "*";
        }

        StringBuilder masked = new StringBuilder();
        masked.append(targetName.charAt(0));
        for (int i = 0; i < length - 2; i++) {
            masked.append("*");
        }
        masked.append(targetName.charAt(length - 1));

        return masked.toString();
    }

    /**
     * 전화번호 마스킹 (하이픈 자동 삽입 및 가운데 자리 마스킹)
     * 01012345678 -> 010-****-5678
     * 021234567 -> 02-***-4567
     * 
     * @param phone 원본 전화번호 (하이픈 유무 상관없음)
     * @return 마스킹된 전화번호 문자열
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return phone;
        }

        // 숫자만 추출
        String cleanPhone = phone.replaceAll("[^0-9]", "");
        int length = cleanPhone.length();

        if (length < 9) {
            return cleanPhone; // 비정상 번호는 그대로 리턴
        }

        if (length == 9) {
            // 예: 02-123-4567
            return cleanPhone.replaceFirst("(\\d{2})(\\d{3})(\\d{4})", "$1-***-$3");
        } else if (length == 10) {
            // 예: 02-1234-5678 또는 010-123-4567
            if (cleanPhone.startsWith("02")) {
                return cleanPhone.replaceFirst("(\\d{2})(\\d{4})(\\d{4})", "$1-****-$3");
            } else {
                return cleanPhone.replaceFirst("(\\d{3})(\\d{3})(\\d{4})", "$1-***-$3");
            }
        } else if (length == 11) {
            // 예: 010-1234-5678
            return cleanPhone.replaceFirst("(\\d{3})(\\d{4})(\\d{4})", "$1-****-$3");
        }

        return cleanPhone; // 그 외의 길이는 마스킹 없이 숫자만 반환
    }

    /**
     * 주민등록번호 마스킹
     * 하이픈 자동 삽입 후 뒤 7자리 중 첫 글자(성별)만 남기고 나머지 6자리 별표 처리
     * 9001011234567 -> 900101-1******
     * 
     * @param rrn 원본 주민등록번호 (하이픈 유무 상관없음)
     * @return 마스킹된 주민등록번호
     */
    public static String maskRrn(String rrn) {
        if (rrn == null || rrn.trim().isEmpty()) {
            return rrn;
        }
        String cleanRrn = rrn.replaceAll("[^0-9]", "");
        if (cleanRrn.length() != 13) {
            return cleanRrn; // 길이가 13자리가 아니면 원본 숫자만 반환
        }
        return cleanRrn.replaceFirst("(\\d{6})(\\d{1})(\\d{6})", "$1-$2******");
    }

    /**
     * 카드번호 마스킹
     * 가운데 8자리를 별표 처리 (하이픈 자동 삽입)
     * 1234567812345678 -> 1234-****-****-5678
     * 
     * @param card 원본 카드번호 (하이픈 유무 상관없음)
     * @return 마스킹된 카드번호
     */
    public static String maskCard(String card) {
        if (card == null || card.trim().isEmpty()) {
            return card;
        }
        String cleanCard = card.replaceAll("[^0-9]", "");
        if (cleanCard.length() != 16) {
            return cleanCard; // 16자리가 아니면 원본 숫자만 반환
        }
        return cleanCard.replaceFirst("(\\d{4})(\\d{4})(\\d{4})(\\d{4})", "$1-****-****-$4");
    }
}
