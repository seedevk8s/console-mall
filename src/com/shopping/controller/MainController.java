package com.shopping.controller;

import com.shopping.util.SessionManager;
import com.shopping.model.User;
import java.util.Scanner;

/**
 * 메인 컨트롤러 클래스
 * 애플리케이션의 전체 흐름을 제어하고 메뉴를 관리
 */
public class MainController {
    
    // 다른 컨트롤러들에 대한 참조
    private UserController userController;
    private ProductController productController;
    private OrderController orderController;
    
    // 사용자 입력을 받기 위한 Scanner
    private Scanner scanner;
    
    /**
     * MainController 생성자
     * 필요한 컨트롤러들과 Scanner를 초기화
     */
    public MainController() {
        this.userController = new UserController();
        this.productController = new ProductController();
        this.orderController = new OrderController();
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * 애플리케이션 시작 메소드
     * 메인 메뉴를 표시하고 사용자 선택을 처리
     */
    public void start() {
        // 환영 메시지 출력
        printWelcomeMessage();
        
        // 메인 루프 - 사용자가 종료를 선택할 때까지 반복
        while (true) {
            showMainMenu();  // 메인 메뉴 표시
            String choice = scanner.nextLine();  // 사용자 입력 받기
            
            // 사용자 선택에 따른 처리
            switch (choice) {
                case "1":
                    // 사용자 관리 메뉴로 이동
                    userController.showUserMenu();
                    break;
                    
                case "2":
                    // 상품 보기 메뉴로 이동
                    productController.showProductMenu();
                    break;
                    
                case "3":
                    // 주문 관리 메뉴로 이동
                    orderController.showOrderMenu();
                    break;
                    
                case "0":
                    // 프로그램 종료
                    System.out.println("\n프로그램을 종료합니다. 감사합니다!");
                    return;  // start() 메소드 종료
                    
                default:
                    // 잘못된 입력 처리
                    System.out.println("❌ 잘못된 선택입니다. 다시 선택해주세요.");
            }
        }
    }
    
    /**
     * 환영 메시지 출력
     */
    private void printWelcomeMessage() {
        System.out.println("╔══════════════════════════════╗");
        System.out.println("║   미니 쇼핑몰 시스템 v1.0   ║");
        System.out.println("║   MVC 패턴 학습용 데모      ║");
        System.out.println("╚══════════════════════════════╝");
    }
    
    /**
     * 메인 메뉴 표시
     */
    private void showMainMenu() {
        System.out.println("\n=== 메인 메뉴 ===");
        
        // 로그인 상태 표시
        if (SessionManager.isLoggedIn()) {
            User user = SessionManager.getCurrentUser();
            System.out.println("👤 현재 로그인: " + user.getName() + "님");
        } else {
            System.out.println("👤 로그인되지 않음");
        }
        
        // 메뉴 옵션 표시
        System.out.println("\n1. 사용자 관리");
        System.out.println("2. 상품 보기");
        System.out.println("3. 주문 관리");
        System.out.println("0. 종료");
        System.out.print("\n선택: ");
    }
}