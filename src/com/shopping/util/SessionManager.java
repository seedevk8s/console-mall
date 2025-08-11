// ===============================================
// SessionManager.java - 핵심 파일!
// ===============================================
package com.shopping.util;

import com.shopping.model.User;

/**
 * 사용자 세션을 관리하는 유틸리티 클래스
 * 싱글톤 패턴을 적용하지 않고 static 메소드로 구현
 * Cross-Cutting Concern의 예시
 */
public class SessionManager {

    // 현재 로그인한 사용자 정보 (static 변수로 관리)
    private static User currentUser = null;

    /**
     * private 생성자 - 인스턴스 생성 방지
     */
    private SessionManager() {
        // 유틸리티 클래스이므로 인스턴스 생성 방지
    }

    /**
     * 현재 사용자 설정 (로그인 시 호출)
     * @param user 로그인한 사용자 객체
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
        System.out.println("세션 생성: " + user.getName() + "님 로그인");
    }

    /**
     * 현재 로그인한 사용자 가져오기
     * @return 현재 사용자 객체 (로그인하지 않았으면 null)
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * 로그인 상태 확인
     * @return 로그인 여부
     */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * 로그아웃 처리 (세션 제거)
     */
    public static void logout() {
        if (currentUser != null) {
            System.out.println("세션 종료: " + currentUser.getName() + "님 로그아웃");
            currentUser = null;
        }
    }

    /**
     * 현재 사용자 ID 가져오기
     * @return 사용자 ID (로그인하지 않았으면 null)
     */
    public static String getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : null;
    }

    /**
     * 현재 사용자 이름 가져오기
     * @return 사용자 이름 (로그인하지 않았으면 "Guest")
     */
    public static String getCurrentUserName() {
        return currentUser != null ? currentUser.getName() : "Guest";
    }

    /**
     * 세션 정보 출력 (디버깅용)
     */
    public static void printSessionInfo() {
        if (isLoggedIn()) {
            System.out.println("=== 현재 세션 정보 ===");
            System.out.println("사용자 ID: " + currentUser.getId());
            System.out.println("사용자 이름: " + currentUser.getName());
            System.out.println("잔액: " + currentUser.getBalance());
        } else {
            System.out.println("로그인된 사용자가 없습니다.");
        }
    }
}