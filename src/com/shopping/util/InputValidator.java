// ===============================================
// InputValidator.java
// ===============================================
package com.shopping.util;

import java.util.regex.Pattern;

/**
 * 입력값 검증을 담당하는 유틸리티 클래스
 * Cross-Cutting Concern의 예시
 */
public class InputValidator {

    // 정규표현식 패턴들 (컴파일된 패턴을 재사용하여 성능 향상)
    private static final Pattern ID_PATTERN = Pattern.compile("[a-zA-Z0-9]+");
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@(.+)$"
    );

    /**
     * private 생성자 - 인스턴스 생성 방지
     */
    private InputValidator() {
        // 유틸리티 클래스이므로 인스턴스 생성 방지
    }

    /**
     * ID 유효성 검증
     * @param id 검증할 ID
     * @return 유효 여부
     */
    public static boolean isValidId(String id) {
        // null 체크
        if (id == null) {
            return false;
        }

        // 길이 체크 (3자 이상)
        if (id.length() < 3) {
            return false;
        }

        // 패턴 체크 (영문, 숫자만)
        return ID_PATTERN.matcher(id).matches();
    }

    /**
     * 숫자 유효성 검증
     * @param input 검증할 문자열
     * @return 유효한 숫자 여부
     */
    public static boolean isValidNumber(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }

        try {
            Integer.parseInt(input.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 양수 검증
     * @param input 검증할 문자열
     * @return 양수 여부
     */
    public static boolean isPositiveNumber(String input) {
        if (!isValidNumber(input)) {
            return false;
        }

        return Integer.parseInt(input) > 0;
    }

    /**
     * 이메일 유효성 검증
     * @param email 검증할 이메일
     * @return 유효 여부
     */
    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }

        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * 패스워드 유효성 검증
     * @param password 검증할 패스워드
     * @return 유효 여부
     */
    public static boolean isValidPassword(String password) {
        // null 체크
        if (password == null) {
            return false;
        }

        // 최소 길이 체크 (4자 이상)
        return password.length() >= 4;
    }

    /**
     * 이름 유효성 검증
     * @param name 검증할 이름
     * @return 유효 여부
     */
    public static boolean isValidName(String name) {
        // null 체크
        if (name == null) {
            return false;
        }

        // 공백 제거 후 빈 문자열 체크
        String trimmedName = name.trim();

        // 최소 길이 체크 (2자 이상)
        return trimmedName.length() >= 2;
    }

    /**
     * 가격 유효성 검증
     * @param price 검증할 가격 문자열
     * @return 유효 여부
     */
    public static boolean isValidPrice(String price) {
        if (price == null || price.trim().isEmpty()) {
            return false;
        }

        try {
            double value = Double.parseDouble(price);
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}