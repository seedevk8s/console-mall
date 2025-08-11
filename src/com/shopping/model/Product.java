package com.shopping.model;

import java.io.Serializable;

/**
 * 상품 정보를 담는 엔티티 클래스
 * Serializable을 구현하여 파일 저장이 가능하도록 함
 */
public class Product implements Serializable {
    
    // 직렬화 버전 UID
    private static final long serialVersionUID = 1L;
    
    // 상품 ID (Primary Key 역할)
    private int id;
    
    // 상품명
    private String name;
    
    // 상품 가격
    private double price;
    
    // 재고 수량
    private int stock;
    
    /**
     * Product 생성자
     * @param id 상품 ID
     * @param name 상품명
     * @param price 상품 가격
     * @param stock 재고 수량
     */
    public Product(int id, String name, double price, int stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }
    
    // Getter 메소드들
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public double getPrice() {
        return price;
    }
    
    public int getStock() {
        return stock;
    }
    
    // Setter 메소드
    public void setStock(int stock) {
        // 재고는 음수가 될 수 없음
        if (stock >= 0) {
            this.stock = stock;
        }
    }
    
    /**
     * 재고 감소 메소드
     * @param quantity 감소할 수량
     * @return 성공 여부
     */
    public boolean decreaseStock(int quantity) {
        if (this.stock >= quantity) {
            this.stock -= quantity;
            return true;
        }
        return false;
    }
    
    @Override
    public String toString() {
        return String.format("Product[id=%d, name=%s, price=%.2f, stock=%d]", 
            id, name, price, stock);
    }
}