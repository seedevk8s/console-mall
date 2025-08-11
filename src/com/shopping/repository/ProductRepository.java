// ===============================================
// ProductRepository.java
// ===============================================
package com.shopping.repository;

import com.shopping.model.Product;
import com.shopping.persistence.FileManager;
import com.shopping.util.Constants;
import java.util.List;
import java.util.ArrayList;

/**
 * 상품 데이터 접근을 담당하는 Repository 클래스
 * 초기 데이터 생성 기능 포함
 */
public class ProductRepository {

    // 파일명 상수
    private static final String FILE_NAME = Constants.PRODUCT_DATA_FILE;

    /**
     * 초기 상품 데이터 생성
     * 파일이 없거나 비어있을 때 기본 상품 목록 생성
     */
    public void initializeProducts() {
        List<Product> products = new ArrayList<>();

        // 기본 상품 목록
        products.add(new Product(1, "노트북", 1500000, 10));
        products.add(new Product(2, "마우스", 30000, 50));
        products.add(new Product(3, "키보드", 80000, 30));
        products.add(new Product(4, "모니터", 400000, 20));
        products.add(new Product(5, "이어폰", 50000, 100));
        products.add(new Product(6, "웹캠", 120000, 15));
        products.add(new Product(7, "USB 메모리", 25000, 80));
        products.add(new Product(8, "외장 하드", 150000, 25));

        // 파일에 저장
        FileManager.writeToFile(FILE_NAME, products);
    }

    /**
     * 모든 상품 조회
     * @return 상품 목록
     */
    public List<Product> findAll() {
        List<Product> products = FileManager.readFromFile(FILE_NAME);

        // 상품이 없으면 초기화
        if (products.isEmpty()) {
            initializeProducts();
            products = FileManager.readFromFile(FILE_NAME);
        }

        return products;
    }

    /**
     * ID로 상품 조회
     * @param id 상품 ID
     * @return Product 객체 (없으면 null)
     */
    public Product findById(int id) {
        return findAll().stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * 상품 정보 업데이트
     * @param product 업데이트할 Product 객체
     */
    public void update(Product product) {
        List<Product> products = findAll();

        // 기존 상품 제거
        products.removeIf(p -> p.getId() == product.getId());

        // 업데이트된 상품 추가
        products.add(product);

        // ID 순으로 정렬
        products.sort((p1, p2) -> Integer.compare(p1.getId(), p2.getId()));

        // 파일에 저장
        FileManager.writeToFile(FILE_NAME, products);
    }

    /**
     * 상품 저장 (새 상품 추가)
     * @param product 저장할 Product 객체
     * @return 저장된 Product 객체
     */
    public Product save(Product product) {
        List<Product> products = findAll();

        // 새 상품 추가
        products.add(product);

        // ID 순으로 정렬
        products.sort((p1, p2) -> Integer.compare(p1.getId(), p2.getId()));

        // 파일에 저장
        FileManager.writeToFile(FILE_NAME, products);

        return product;
    }
}
