// ===============================================
// Constants.java 수정 버전
// ===============================================
package com.shopping.util;

import com.shopping.model.User;
import com.shopping.persistence.FileManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 애플리케이션 전체에서 사용되는 상수 정의
 * Cross-Cutting Concern의 예시
 *
 * 수정사항: 파일 경로를 간단하게 변경
 */
public class Constants {

    // 파일 이름만 정의 (경로는 FileManager에서 처리)
    public static final String USER_DATA_FILE = "users.dat";
    public static final String PRODUCT_DATA_FILE = "products.dat";
    public static final String ORDER_DATA_FILE = "orders.dat";

    // 비즈니스 규칙 상수
    public static final double INITIAL_BALANCE = 10000.0;  // 초기 잔액
    public static final int MIN_PASSWORD_LENGTH = 4;       // 최소 패스워드 길이
    public static final int MIN_ID_LENGTH = 3;             // 최소 ID 길이
    public static final int MIN_ORDER_QUANTITY = 1;        // 최소 주문 수량

    // 메시지 상수
    public static final String LOGIN_REQUIRED = "❌ 로그인이 필요합니다.";
    public static final String INVALID_INPUT = "❌ 잘못된 입력입니다.";
    public static final String SUCCESS = "✅ 성공적으로 처리되었습니다.";
    public static final String FAILED = "❌ 처리에 실패했습니다.";

    // 포맷 상수
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String CURRENCY_FORMAT = "###,###원";

    // 시스템 설정 상수
    public static final String APP_NAME = "미니 쇼핑몰 시스템";
    public static final String APP_VERSION = "1.0.0";
    public static final String ENCODING = "UTF-8";

    /**
     * private 생성자 - 인스턴스 생성 방지
     */
    private Constants() {
        // 상수 클래스이므로 인스턴스 생성 방지
    }
}

