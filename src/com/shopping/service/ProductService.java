// ===============================================
// ProductService.java - 핵심 파일!
// ===============================================
package com.shopping.service;

import com.shopping.model.Product;
import com.shopping.repository.ProductRepository;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 상품 관련 비즈니스 로직을 처리하는 서비스 클래스
 * Business Logic Layer의 핵심 컴포넌트
 *
 * 주요 책임:
 * - 상품 조회 로직
 * - 재고 관리
 * - 상품 검색 및 필터링
 */
public class ProductService {

    // 데이터 접근을 위한 Repository
    private ProductRepository productRepository;

    /**
     * ProductService 생성자
     * Repository 인스턴스 생성
     */
    public ProductService() {
        this.productRepository = new ProductRepository();
    }

    /**
     * 모든 상품 조회
     * @return 전체 상품 목록
     */
    public List<Product> getAllProducts() {
        // Repository에서 모든 상품 조회
        List<Product> products = productRepository.findAll();

        // 로깅
        System.out.println("[ProductService] 전체 상품 조회: " + products.size() + "개");

        return products;
    }

    /**
     * 특정 상품 조회
     * @param productId 상품 ID
     * @return Product 객체
     * @throws RuntimeException 상품을 찾을 수 없을 때
     */
    public Product getProduct(int productId) {
        // ID 유효성 검증
        if (productId <= 0) {
            throw new RuntimeException("유효하지 않은 상품 ID입니다: " + productId);
        }

        // Repository에서 상품 조회
        Product product = productRepository.findById(productId);

        // 비즈니스 규칙: 존재하지 않는 상품 처리
        if (product == null) {
            throw new RuntimeException("상품을 찾을 수 없습니다. (ID: " + productId + ")");
        }

        return product;
    }

    /**
     * 재고 확인
     * 주문 가능 여부를 판단하는 핵심 비즈니스 로직
     *
     * @param productId 상품 ID
     * @param quantity 요청 수량
     * @return 구매 가능 여부
     */
    public boolean checkStock(int productId, int quantity) {
        // 비즈니스 규칙: 요청 수량은 양수여야 함
        if (quantity <= 0) {
            System.out.println("[ProductService] 잘못된 수량 요청: " + quantity);
            return false;
        }

        try {
            Product product = productRepository.findById(productId);

            if (product == null) {
                System.out.println("[ProductService] 상품을 찾을 수 없음: " + productId);
                return false;
            }

            // 재고와 요청 수량 비교
            boolean hasStock = product.getStock() >= quantity;

            System.out.println(String.format(
                    "[ProductService] 재고 확인: 상품ID=%d, 현재재고=%d, 요청수량=%d, 결과=%s",
                    productId, product.getStock(), quantity, hasStock ? "가능" : "부족"
            ));

            return hasStock;

        } catch (Exception e) {
            System.err.println("[ProductService] 재고 확인 중 오류: " + e.getMessage());
            return false;
        }
    }

    /**
     * 재고 차감
     * 주문 완료 시 호출되는 중요한 비즈니스 로직
     *
     * @param productId 상품 ID
     * @param quantity 차감할 수량
     * @throws RuntimeException 재고 부족 또는 상품을 찾을 수 없을 때
     */
    public void updateStock(int productId, int quantity) {
        // 수량 유효성 검증
        if (quantity <= 0) {
            throw new RuntimeException("차감 수량은 양수여야 합니다: " + quantity);
        }

        // 상품 조회
        Product product = productRepository.findById(productId);

        if (product == null) {
            throw new RuntimeException("상품을 찾을 수 없습니다. (ID: " + productId + ")");
        }

        // 비즈니스 규칙: 재고 확인
        if (product.getStock() < quantity) {
            throw new RuntimeException(
                    String.format("재고가 부족합니다. (현재 재고: %d개, 요청: %d개)",
                            product.getStock(), quantity)
            );
        }

        // 재고 차감
        int oldStock = product.getStock();
        product.setStock(oldStock - quantity);

        // 변경사항 영속화
        productRepository.update(product);

        // 로깅
        System.out.println(String.format(
                "[ProductService] 재고 차감: 상품ID=%d, %s (%d → %d)",
                productId, product.getName(), oldStock, product.getStock()
        ));
    }

    /**
     * 재고 추가 (관리자 기능)
     * @param productId 상품 ID
     * @param quantity 추가할 수량
     */
    public void addStock(int productId, int quantity) {
        if (quantity <= 0) {
            throw new RuntimeException("추가 수량은 양수여야 합니다: " + quantity);
        }

        Product product = productRepository.findById(productId);

        if (product == null) {
            throw new RuntimeException("상품을 찾을 수 없습니다. (ID: " + productId + ")");
        }

        int oldStock = product.getStock();
        product.setStock(oldStock + quantity);

        productRepository.update(product);

        System.out.println(String.format(
                "[ProductService] 재고 추가: 상품ID=%d, %s (%d → %d)",
                productId, product.getName(), oldStock, product.getStock()
        ));
    }

    /**
     * 재고가 있는 상품만 조회
     * @return 재고가 있는 상품 목록
     */
    public List<Product> getAvailableProducts() {
        List<Product> allProducts = productRepository.findAll();

        // Stream API를 사용한 필터링
        List<Product> availableProducts = allProducts.stream()
                .filter(product -> product.getStock() > 0)
                .collect(Collectors.toList());

        System.out.println(String.format(
                "[ProductService] 재고 있는 상품: %d개 / 전체: %d개",
                availableProducts.size(), allProducts.size()
        ));

        return availableProducts;
    }

    /**
     * 상품 가격 조회
     * @param productId 상품 ID
     * @return 상품 가격
     */
    public double getProductPrice(int productId) {
        Product product = getProduct(productId);
        return product.getPrice();
    }

    /**
     * 가격 범위로 상품 검색
     * @param minPrice 최소 가격
     * @param maxPrice 최대 가격
     * @return 해당 가격 범위의 상품 목록
     */
    public List<Product> getProductsByPriceRange(double minPrice, double maxPrice) {
        // 가격 유효성 검증
        if (minPrice < 0 || maxPrice < 0) {
            throw new RuntimeException("가격은 0 이상이어야 합니다.");
        }

        if (minPrice > maxPrice) {
            throw new RuntimeException("최소 가격이 최대 가격보다 클 수 없습니다.");
        }

        List<Product> allProducts = productRepository.findAll();

        // 가격 범위로 필터링
        List<Product> filteredProducts = allProducts.stream()
                .filter(product -> product.getPrice() >= minPrice && product.getPrice() <= maxPrice)
                .collect(Collectors.toList());

        System.out.println(String.format(
                "[ProductService] 가격 범위 검색: %.0f원 ~ %.0f원, 결과: %d개",
                minPrice, maxPrice, filteredProducts.size()
        ));

        return filteredProducts;
    }

    /**
     * 상품명으로 검색
     * @param keyword 검색 키워드
     * @return 검색 결과 상품 목록
     */
    public List<Product> searchProductsByName(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new RuntimeException("검색어를 입력해주세요.");
        }

        String searchKeyword = keyword.trim().toLowerCase();
        List<Product> allProducts = productRepository.findAll();

        // 상품명에 키워드가 포함된 상품 검색
        List<Product> searchResults = allProducts.stream()
                .filter(product -> product.getName().toLowerCase().contains(searchKeyword))
                .collect(Collectors.toList());

        System.out.println(String.format(
                "[ProductService] 상품명 검색: '%s', 결과: %d개",
                keyword, searchResults.size()
        ));

        return searchResults;
    }

    /**
     * 재고 부족 상품 조회 (관리자 기능)
     * @param threshold 재고 임계값
     * @return 재고가 임계값 이하인 상품 목록
     */
    public List<Product> getLowStockProducts(int threshold) {
        if (threshold < 0) {
            throw new RuntimeException("임계값은 0 이상이어야 합니다.");
        }

        List<Product> allProducts = productRepository.findAll();

        // 재고가 임계값 이하인 상품 필터링
        List<Product> lowStockProducts = allProducts.stream()
                .filter(product -> product.getStock() <= threshold)
                .collect(Collectors.toList());

        System.out.println(String.format(
                "[ProductService] 재고 부족 상품 조회 (임계값: %d개): %d개 상품",
                threshold, lowStockProducts.size()
        ));

        return lowStockProducts;
    }

    /**
     * 베스트셀러 상품 조회 (향후 구현)
     * 주문 데이터를 기반으로 인기 상품 분석
     *
     * @param limit 조회할 상품 개수
     * @return 베스트셀러 상품 목록
     */
    public List<Product> getBestSellerProducts(int limit) {
        // TODO: OrderRepository와 연동하여 판매량 기준 정렬
        // 현재는 전체 상품 중 일부만 반환
        List<Product> allProducts = productRepository.findAll();

        return allProducts.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * 상품 정보 유효성 검증
     * @param product 검증할 상품
     * @return 유효 여부
     */
    private boolean isValidProduct(Product product) {
        if (product == null) {
            return false;
        }

        if (product.getName() == null || product.getName().trim().isEmpty()) {
            return false;
        }

        if (product.getPrice() <= 0) {
            return false;
        }

        if (product.getStock() < 0) {
            return false;
        }

        return true;
    }
}