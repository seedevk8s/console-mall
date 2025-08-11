package com.shopping.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 주문 정보를 담는 엔티티 클래스
 * Serializable을 구현하여 파일 저장이 가능하도록 함
 */
public class Order implements Serializable {
    
    // 직렬화 버전 UID
    private static final long serialVersionUID = 1L;
    
    // 주문 ID (Primary Key 역할)
    private int orderId;
    
    // 주문한 사용자 ID (Foreign Key - User)
    private String userId;
    
    // 주문한 상품 ID (Foreign Key - Product)
    private int productId;
    
    // 주문 수량
    private int quantity;
    
    // 총 주문 금액
    private double totalPrice;
    
    // 주문 일시
    private Date orderDate;
    
    /**
     * Order 생성자
     * @param orderId 주문 ID
     * @param userId 사용자 ID
     * @param productId 상품 ID
     * @param quantity 주문 수량
     * @param totalPrice 총 금액
     */
    public Order(int orderId, String userId, int productId, 
                 int quantity, double totalPrice) {
        this.orderId = orderId;
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.orderDate = new Date();  // 현재 시간으로 설정
    }
    
    // Getter 메소드들
    public int getOrderId() {
        return orderId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public int getProductId() {
        return productId;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public double getTotalPrice() {
        return totalPrice;
    }
    
    public Date getOrderDate() {
        return orderDate;
    }
    
    @Override
    public String toString() {
        return String.format(
            "Order[orderId=%d, userId=%s, productId=%d, quantity=%d, totalPrice=%.2f, orderDate=%s]",
            orderId, userId, productId, quantity, totalPrice, orderDate
        );
    }
}