package com.shopping.controller;

import com.shopping.service.UserService;
import com.shopping.model.User;
import com.shopping.util.SessionManager;
import com.shopping.util.InputValidator;
import java.util.Scanner;

/**
 * 사용자 관련 UI를 담당하는 컨트롤러
 * Presentation Layer의 일부로 사용자 입력을 받고 결과를 표시
 */
public class UserController {
    
    // 비즈니스 로직 처리를 위한 서비스
    private UserService userService;
    
    // 사용자 입력을 받기 위한 Scanner
    private Scanner scanner;
    
    /**
     * UserController 생성자
     */
    public UserController() {
        this.userService = new UserService();
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * 사용자 관리 메뉴 표시 및 처리
     */
    public void showUserMenu() {
        while (true) {
            System.out.println("\n=== 사용자 메뉴 ===");
            System.out.println("1. 회원가입");
            System.out.println("2. 로그인");
            System.out.println("3. 로그아웃");
            System.out.println("4. 내 정보 보기");
            System.out.println("0. 돌아가기");
            System.out.print("선택: ");
            
            String choice = scanner.nextLine();
            
            // 사용자 선택에 따른 메소드 호출
            switch (choice) {
                case "1":
                    register();  // 회원가입
                    break;
                case "2":
                    login();     // 로그인
                    break;
                case "3":
                    logout();    // 로그아웃
                    break;
                case "4":
                    showMyInfo(); // 내 정보 보기
                    break;
                case "0":
                    return;      // 메인 메뉴로 돌아가기
                default:
                    System.out.println("❌ 잘못된 선택입니다.");
            }
        }
    }
    
    /**
     * 회원가입 처리
     */
    private void register() {
        System.out.println("\n=== 회원가입 ===");
        
        // 아이디 입력 받기
        System.out.print("아이디 (3자 이상, 영문/숫자): ");
        String id = scanner.nextLine();
        
        // 입력 검증 (Cross-Cutting Concern 활용)
        if (!InputValidator.isValidId(id)) {
            System.out.println("❌ 아이디 형식이 올바르지 않습니다.");
            return;
        }
        
        // 패스워드 입력 받기
        System.out.print("패스워드 (4자 이상): ");
        String password = scanner.nextLine();
        
        // 이름 입력 받기
        System.out.print("이름: ");
        String name = scanner.nextLine();
        
        try {
            // 서비스 계층 호출하여 회원가입 처리
            User user = userService.register(id, password, name);
            
            // 성공 메시지 출력
            System.out.println("✅ 회원가입 성공!");
            System.out.println("   환영합니다, " + user.getName() + "님!");
            System.out.println("   초기 잔액: " + user.getBalance() + "원");
            
        } catch (Exception e) {
            // 실패 시 에러 메시지 출력
            System.out.println("❌ 회원가입 실패: " + e.getMessage());
        }
    }
    
    /**
     * 로그인 처리
     */
    private void login() {
        // 이미 로그인 상태인지 확인
        if (SessionManager.isLoggedIn()) {
            System.out.println("❌ 이미 로그인되어 있습니다.");
            return;
        }
        
        System.out.println("\n=== 로그인 ===");
        
        // 아이디 입력
        System.out.print("아이디: ");
        String id = scanner.nextLine();
        
        // 패스워드 입력
        System.out.print("패스워드: ");
        String password = scanner.nextLine();
        
        try {
            // 서비스 계층을 통한 로그인 처리
            User user = userService.login(id, password);
            
            // 세션에 사용자 정보 저장
            SessionManager.setCurrentUser(user);
            
            System.out.println("✅ 로그인 성공!");
            System.out.println("   환영합니다, " + user.getName() + "님!");
            
        } catch (Exception e) {
            System.out.println("❌ 로그인 실패: " + e.getMessage());
        }
    }
    
    /**
     * 로그아웃 처리
     */
    private void logout() {
        // 로그인 상태 확인
        if (!SessionManager.isLoggedIn()) {
            System.out.println("❌ 로그인 상태가 아닙니다.");
            return;
        }
        
        // 세션 정보 삭제
        SessionManager.logout();
        System.out.println("✅ 로그아웃되었습니다.");
    }
    
    /**
     * 내 정보 표시
     */
    private void showMyInfo() {
        // 로그인 상태 확인
        if (!SessionManager.isLoggedIn()) {
            System.out.println("❌ 로그인이 필요합니다.");
            return;
        }
        
        // 현재 로그인한 사용자 정보 가져오기
        User user = SessionManager.getCurrentUser();
        
        System.out.println("\n=== 내 정보 ===");
        System.out.println("아이디: " + user.getId());
        System.out.println("이름: " + user.getName());
        
        // 최신 잔액 정보는 서비스에서 가져오기
        System.out.println("잔액: " + userService.getBalance(user.getId()) + "원");
    }
}