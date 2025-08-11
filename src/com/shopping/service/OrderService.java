// ===============================================
// OrderService.java
// ===============================================
package com.shopping.service;

import com.shopping.model.Order;
import com.shopping.model.Product;
import com.shopping.repository.OrderRepository;
import java.util.List;

/**
 * 주문 관련 비즈니스 로직을 처리하는 서비스 클래스
 * 여러 서비스를 조합하여 복잡한 비즈니스 로직 처리
 */
public class OrderService {

    // 필요한 Repository와 Service들
    private OrderRepository orderRepository;
    private ProductService productService;
    private UserService userService;

    /**
     * OrderService 생성자
     */
    public OrderService() {
        this.orderRepository = new OrderRepository();
        this.productService = new ProductService();
        this.userService = new UserService();
    }

    /**
     * 주문 생성 - 핵심 비즈니스 로직
     * 트랜잭션 처리가 필요한 복잡한 비즈니스 로직의 예시
     *
     * @param userId 사용자 ID
     * @param productId 상품 ID
     * @param quantity 주문 수량
     * @return 생성된 Order 객체
     * @throws RuntimeException 주문 실패 시
     */
    public Order createOrder(String userId, int productId, int quantity) {
        // 1단계: 상품 정보 확인
        Product product = productService.getProduct(productId);

        // 2단계: 주문 수량 유효성 검증
        if (quantity <= 0) {
            throw new RuntimeException("주문 수량은 1개 이상이어야 합니다.");
        }

        // 3단계: 재고 확인
        if (!productService.checkStock(productId, quantity)) {
            throw new RuntimeException(
                    String.format("재고가 부족합니다. 현재 재고: %d개",
                            product.getStock())
            );
        }

        // 4단계: 총 가격 계산
        double totalPrice = product.getPrice() * quantity;

        // 5단계: 사용자 잔액 확인
        double userBalance = userService.getBalance(userId);
        if (userBalance < totalPrice) {
            throw new RuntimeException(
                    String.format("잔액이 부족합니다. 필요 금액: %.0f원, 현재 잔액: %.0f원",
                            totalPrice, userBalance)
            );
        }

        // 6단계: 주문 ID 생성
        int orderId = orderRepository.getNextOrderId();

        // 7단계: 주문 객체 생성
        Order order = new Order(orderId, userId, productId, quantity, totalPrice);

        // 8단계: 트랜잭션 처리 (실제로는 트랜잭션 매니저가 필요하지만 간단히 구현)
        try {
            // 8-1: 재고 차감
            productService.updateStock(productId, quantity);

            // 8-2: 잔액 차감
            userService.updateBalance(userId, userBalance - totalPrice);

            // 8-3: 주문 저장
            return orderRepository.save(order);

        } catch (Exception e) {
            // 트랜잭션 롤백이 필요하지만, 파일 기반이므로 생략
            throw new RuntimeException("주문 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 특정 사용자의 주문 내역 조회
     * @param userId 사용자 ID
     * @return 주문 목록
     */
    public List<Order> getUserOrders(String userId) {
        return orderRepository.findByUserId(userId);
    }

    /**
     * 주문 취소 (향후 구현)
     * @param orderId 주문 ID
     * @param userId 사용자 ID
     * @return 취소 성공 여부
     */
    public boolean cancelOrder(int orderId, String userId) {
        // 향후 구현 예정
        // 1. 주문 조회
        // 2. 주문 상태 확인 (배송 전인지)
        // 3. 재고 복구
        // 4. 잔액 복구
        // 5. 주문 상태 변경
        return false;
    }
}