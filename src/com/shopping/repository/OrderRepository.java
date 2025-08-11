// ===============================================
// OrderRepository.java
// ===============================================
package com.shopping.repository;

import com.shopping.model.Order;
import com.shopping.persistence.FileManager;
import com.shopping.util.Constants;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * 주문 데이터 접근을 담당하는 Repository 클래스
 * 주문 ID 자동 생성 기능 포함
 */
public class OrderRepository {

    // 파일명 상수
    private static final String FILE_NAME = Constants.ORDER_DATA_FILE;

    // 다음 주문 ID (static으로 관리)
    private static int nextOrderId = 1;

    /**
     * 주문 저장
     * @param order 저장할 Order 객체
     * @return 저장된 Order 객체
     */
    public Order save(Order order) {
        List<Order> orders = FileManager.readFromFile(FILE_NAME);

        // 새 주문 추가
        orders.add(order);

        // 파일에 저장
        FileManager.writeToFile(FILE_NAME, orders);

        // 다음 주문 ID 증가
        nextOrderId++;

        return order;
    }

    /**
     * 사용자 ID로 주문 조회
     * @param userId 사용자 ID
     * @return 해당 사용자의 주문 목록
     */
    public List<Order> findByUserId(String userId) {
        List<Order> orders = FileManager.readFromFile(FILE_NAME);

        // Stream API를 사용한 필터링
        return orders.stream()
                .filter(o -> o.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    /**
     * 주문 ID로 주문 조회
     * @param orderId 주문 ID
     * @return Order 객체 (없으면 null)
     */
    public Order findById(int orderId) {
        List<Order> orders = FileManager.readFromFile(FILE_NAME);

        return orders.stream()
                .filter(o -> o.getOrderId() == orderId)
                .findFirst()
                .orElse(null);
    }

    /**
     * 다음 주문 ID 가져오기
     * @return 사용 가능한 다음 주문 ID
     */
    public int getNextOrderId() {
        List<Order> orders = FileManager.readFromFile(FILE_NAME);

        // 파일에 주문이 있으면 최대값 + 1로 설정
        if (!orders.isEmpty()) {
            nextOrderId = orders.stream()
                    .mapToInt(Order::getOrderId)
                    .max()
                    .orElse(0) + 1;
        }

        return nextOrderId;
    }

    /**
     * 모든 주문 조회
     * @return 전체 주문 목록
     */
    public List<Order> findAll() {
        return FileManager.readFromFile(FILE_NAME);
    }
}