package com.shopping.controller;

import com.shopping.service.ProductService;
import com.shopping.model.Product;
import com.shopping.util.InputValidator;
import java.util.List;
import java.util.Scanner;

/**
 * 상품 관련 UI를 담당하는 컨트롤러
 * 상품 목록 표시, 상품 상세 정보 등을 처리
 */
public class ProductController {
    
    // 비즈니스 로직 처리를 위한 서비스
    private ProductService productService;
    
    // 사용자 입력을 받기 위한 Scanner
    private Scanner scanner;
    
    /**
     * ProductController 생성자
     */
    public ProductController() {
        this.productService = new ProductService();
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * 상품 관리 메뉴 표시 및 처리
     */
    public void showProductMenu() {
        while (true) {
            System.out.println("\n=== 상품 메뉴 ===");
            System.out.println("1. 상품 목록 보기");
            System.out.println("2. 상품 상세 보기");
            System.out.println("0. 돌아가기");
            System.out.print("선택: ");
            
            String choice = scanner.nextLine();
            
            switch (choice) {
                case "1":
                    showAllProducts();   // 전체 상품 목록 표시
                    break;
                case "2":
                    showProductDetail();  // 특정 상품 상세 정보
                    break;
                case "0":
                    return;              // 메인 메뉴로 돌아가기
                default:
                    System.out.println("❌ 잘못된 선택입니다.");
            }
        }
    }
    
    /**
     * 전체 상품 목록 표시
     */
    private void showAllProducts() {
        System.out.println("\n=== 상품 목록 ===");
        
        // 테이블 헤더 출력
        System.out.println("ID | 상품명 | 가격 | 재고");
        System.out.println("-".repeat(40));
        
        // 서비스에서 상품 목록 가져오기
        List<Product> products = productService.getAllProducts();
        
        // 각 상품 정보 출력
        for (Product product : products) {
            System.out.printf("%d | %s | %.0f원 | %d개%n",
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock()
            );
        }
        
        System.out.println("\n총 " + products.size() + "개의 상품이 있습니다.");
    }
    
    /**
     * 특정 상품의 상세 정보 표시
     */
    private void showProductDetail() {
        System.out.print("상품 ID를 입력하세요: ");
        String input = scanner.nextLine();
        
        // 입력값 검증
        if (!InputValidator.isValidNumber(input)) {
            System.out.println("❌ 올바른 숫자를 입력하세요.");
            return;
        }
        
        try {
            // 서비스를 통해 상품 정보 조회
            int productId = Integer.parseInt(input);
            Product product = productService.getProduct(productId);
            
            // 상품 상세 정보 출력
            System.out.println("\n=== 상품 상세 정보 ===");
            System.out.println("상품 ID: " + product.getId());
            System.out.println("상품명: " + product.getName());
            System.out.println("가격: " + product.getPrice() + "원");
            System.out.println("재고: " + product.getStock() + "개");
            
            // 재고 상태 표시
            if (product.getStock() > 0) {
                System.out.println("상태: ✅ 구매 가능");
            } else {
                System.out.println("상태: ❌ 품절");
            }
            
        } catch (Exception e) {
            System.out.println("❌ " + e.getMessage());
        }
    }
}