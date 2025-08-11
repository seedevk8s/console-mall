// ===============================================
// OrderController.java
// ===============================================
package com.shopping.controller;

import com.shopping.service.OrderService;
import com.shopping.service.ProductService;
import com.shopping.model.Order;
import com.shopping.model.Product;
import com.shopping.util.SessionManager;
import com.shopping.util.InputValidator;
import java.util.List;
import java.util.Scanner;

/**
 * 주문 관련 UI를 담당하는 컨트롤러
 * 주문 생성, 주문 내역 조회 등을 처리
 */
public class OrderController {

    // 비즈니스 로직 처리를 위한 서비스들
    private OrderService orderService;
    private ProductService productService;

    // 사용자 입력을 받기 위한 Scanner
    private Scanner scanner;

    /**
     * OrderController 생성자
     */
    public OrderController() {
        this.orderService = new OrderService();
        this.productService = new ProductService();
        this.scanner = new Scanner(System.in);
    }

    /**
     * 주문 관리 메뉴 표시 및 처리
     */
    public void showOrderMenu() {
        // 로그인 확인 - SessionManager 사용
        if (!SessionManager.isLoggedIn()) {
            System.out.println("❌ 로그인이 필요합니다.");
            return;
        }

        while (true) {
            System.out.println("\n=== 주문 메뉴 ===");
            System.out.println("1. 상품 주문하기");
            System.out.println("2. 내 주문 내역");
            System.out.println("0. 돌아가기");
            System.out.print("선택: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    createOrder();     // 새 주문 생성
                    break;
                case "2":
                    showMyOrders();    // 주문 내역 조회
                    break;
                case "0":
                    return;           // 메인 메뉴로 돌아가기
                default:
                    System.out.println("❌ 잘못된 선택입니다.");
            }
        }
    }

    /**
     * 새 주문 생성
     */
    private void createOrder() {
        System.out.println("\n=== 상품 주문 ===");

        // 구매 가능한 상품 목록 표시
        System.out.println("\n[구매 가능한 상품]");
        List<Product> products = productService.getAllProducts();

        for (Product product : products) {
            // 재고가 있는 상품만 표시
            if (product.getStock() > 0) {
                System.out.printf("%d. %s (%.0f원, 재고: %d개)%n",
                        product.getId(),
                        product.getName(),
                        product.getPrice(),
                        product.getStock()
                );
            }
        }

        // 상품 ID 입력 받기
        System.out.print("\n상품 ID: ");
        String productIdStr = scanner.nextLine();

        // 입력 검증 - InputValidator 사용
        if (!InputValidator.isValidNumber(productIdStr)) {
            System.out.println("❌ 올바른 상품 ID를 입력하세요.");
            return;
        }

        // 수량 입력 받기
        System.out.print("수량: ");
        String quantityStr = scanner.nextLine();

        // 입력 검증
        if (!InputValidator.isValidNumber(quantityStr)) {
            System.out.println("❌ 올바른 수량을 입력하세요.");
            return;
        }

        try {
            int productId = Integer.parseInt(productIdStr);
            int quantity = Integer.parseInt(quantityStr);

            // 상품 정보 확인
            Product product = productService.getProduct(productId);
            double totalPrice = product.getPrice() * quantity;

            // 주문 확인
            System.out.printf("\n주문 확인: %s %d개, 총 %.0f원%n",
                    product.getName(), quantity, totalPrice);
            System.out.print("주문하시겠습니까? (y/n): ");

            String confirm = scanner.nextLine();

            if (confirm.equalsIgnoreCase("y")) {
                // 주문 처리 - SessionManager에서 현재 사용자 정보 가져오기
                Order order = orderService.createOrder(
                        SessionManager.getCurrentUser().getId(),
                        productId,
                        quantity
                );

                // 성공 메시지
                System.out.println("✅ 주문이 완료되었습니다!");
                System.out.println("   주문번호: " + order.getOrderId());
                System.out.println("   결제금액: " + order.getTotalPrice() + "원");

            } else {
                System.out.println("주문이 취소되었습니다.");
            }

        } catch (Exception e) {
            System.out.println("❌ 주문 실패: " + e.getMessage());
        }
    }

    /**
     * 내 주문 내역 표시
     */
    private void showMyOrders() {
        System.out.println("\n=== 내 주문 내역 ===");

        // 현재 사용자의 주문 내역 조회
        String userId = SessionManager.getCurrentUser().getId();
        List<Order> orders = orderService.getUserOrders(userId);

        if (orders.isEmpty()) {
            System.out.println("주문 내역이 없습니다.");
            return;
        }

        // 테이블 헤더
        System.out.println("주문번호 | 상품ID | 수량 | 총액 | 주문일시");
        System.out.println("-".repeat(60));

        // 각 주문 정보 출력
        for (Order order : orders) {
            System.out.printf("%d | %d | %d개 | %.0f원 | %s%n",
                    order.getOrderId(),
                    order.getProductId(),
                    order.getQuantity(),
                    order.getTotalPrice(),
                    order.getOrderDate()
            );
        }

        // 총 주문 금액 계산
        double totalAmount = orders.stream()
                .mapToDouble(Order::getTotalPrice)
                .sum();

        System.out.println("\n총 주문 건수: " + orders.size() + "건");
        System.out.println("총 주문 금액: " + totalAmount + "원");
    }
}