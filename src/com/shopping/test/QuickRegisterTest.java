// ===============================================
// QuickRegisterTest.java - 빠른 회원가입 테스트
// ===============================================
package com.shopping.test;

import com.shopping.model.User;
import com.shopping.service.UserService;
import java.io.File;

/**
 * 회원가입 빠른 테스트
 * 가장 기본적인 시나리오만 테스트
 */
public class QuickRegisterTest {

    public static void main(String[] args) {
        System.out.println("=== 회원가입 빠른 테스트 ===\n");

        // 초기화
        new File("data").mkdir();
        UserService userService = new UserService();

        // 테스트 1: 정상 회원가입
        System.out.println("1. 정상 회원가입 테스트");
        try {
            User user = userService.register("quicktest1", "pass1234", "테스트유저");
            System.out.println("✅ 성공: " + user.getName() + " (잔액: " + user.getBalance() + "원)");
        } catch (Exception e) {
            System.out.println("❌ 실패: " + e.getMessage());
        }

        // 테스트 2: 중복 ID
        System.out.println("\n2. 중복 ID 테스트");
        try {
            userService.register("quicktest1", "pass5678", "다른유저");
            System.out.println("❌ 오류: 중복 ID가 허용됨!");
        } catch (Exception e) {
            System.out.println("✅ 정상: 중복 ID 거부됨 - " + e.getMessage());
        }

        // 테스트 3: 짧은 패스워드
        System.out.println("\n3. 짧은 패스워드 테스트");
        try {
            userService.register("quicktest2", "123", "테스트2");
            System.out.println("❌ 오류: 짧은 패스워드가 허용됨!");
        } catch (Exception e) {
            System.out.println("✅ 정상: 짧은 패스워드 거부됨 - " + e.getMessage());
        }

        // 테스트 4: 빈 이름
        System.out.println("\n4. 빈 이름 테스트");
        try {
            userService.register("quicktest3", "pass1234", "");
            System.out.println("❌ 오류: 빈 이름이 허용됨!");
        } catch (Exception e) {
            System.out.println("✅ 정상: 빈 이름 거부됨 - " + e.getMessage());
        }

        // 테스트 5: 여러 명 등록
        System.out.println("\n5. 여러 명 연속 등록 테스트");
        for (int i = 1; i <= 5; i++) {
            try {
                User user = userService.register("batch" + i, "pass1234", "사용자" + i);
                System.out.println("✅ " + user.getId() + " - " + user.getName() + " 등록 완료");
            } catch (Exception e) {
                System.out.println("❌ 실패: " + e.getMessage());
            }
        }

        System.out.println("\n=== 테스트 완료 ===");
    }
}