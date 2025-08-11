package com.shopping.model;

import java.io.Serializable;

/**
 * 사용자 정보를 담는 엔티티 클래스
 * Serializable을 구현하여 파일 저장이 가능하도록 함
 *
 * @author Student
 * @version 1.1
 */
public class User implements Serializable {

    // 직렬화 버전 UID (파일 저장/읽기 시 클래스 버전 관리)
    private static final long serialVersionUID = 1L;

    // ===== 필드 =====

    // 사용자 ID (Primary Key 역할, 변경 불가)
    private String id;

    // 사용자 비밀번호 (변경 가능)
    private String password;

    // 사용자 이름 (변경 가능)
    private String name;

    // 사용자 잔액 (초기값: 10000원)
    private double balance;

    // ===== 생성자 =====

    /**
     * User 생성자
     * @param id 사용자 ID
     * @param password 비밀번호
     * @param name 사용자 이름
     */
    public User(String id, String password, String name) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.balance = 10000.0;  // 초기 잔액 설정
    }

    // ===== Getter 메소드들 =====

    /**
     * 사용자 ID 반환
     * @return 사용자 ID
     */
    public String getId() {
        return id;
    }

    /**
     * 사용자 비밀번호 반환
     * @return 비밀번호
     */
    public String getPassword() {
        return password;
    }

    /**
     * 사용자 이름 반환
     * @return 사용자 이름
     */
    public String getName() {
        return name;
    }

    /**
     * 사용자 잔액 반환
     * @return 현재 잔액
     */
    public double getBalance() {
        return balance;
    }

    // ===== Setter 메소드들 =====

    /**
     * 사용자 비밀번호 변경
     * @param password 새로운 비밀번호
     */
    public void setPassword(String password) {
        // 비밀번호는 null이 될 수 없음
        if (password != null && !password.isEmpty()) {
            this.password = password;
        }
    }

    /**
     * 사용자 이름 변경
     * @param name 새로운 이름
     */
    public void setName(String name) {
        // 이름은 null이나 빈 문자열이 될 수 없음
        if (name != null && !name.trim().isEmpty()) {
            this.name = name.trim();
        }
    }

    /**
     * 사용자 잔액 설정
     * @param balance 새로운 잔액
     */
    public void setBalance(double balance) {
        // 잔액은 음수가 될 수 없음
        if (balance >= 0) {
            this.balance = balance;
        } else {
            System.err.println("경고: 잔액은 음수가 될 수 없습니다. 변경 취소.");
        }
    }

    // ===== 비즈니스 메소드들 =====

    /**
     * 잔액 차감
     * @param amount 차감할 금액
     * @return 차감 성공 여부
     */
    public boolean deductBalance(double amount) {
        if (amount <= 0) {
            return false;
        }

        if (this.balance >= amount) {
            this.balance -= amount;
            return true;
        }

        return false;
    }

    /**
     * 잔액 추가
     * @param amount 추가할 금액
     * @return 추가 성공 여부
     */
    public boolean addBalance(double amount) {
        if (amount <= 0) {
            return false;
        }

        this.balance += amount;
        return true;
    }

    /**
     * 잔액이 충분한지 확인
     * @param amount 필요한 금액
     * @return 잔액 충분 여부
     */
    public boolean hasEnoughBalance(double amount) {
        return this.balance >= amount;
    }

    // ===== Object 메소드 오버라이드 =====

    /**
     * 객체 정보를 문자열로 반환
     * @return 사용자 정보 문자열
     */
    @Override
    public String toString() {
        return String.format("User[id=%s, name=%s, balance=%.2f]",
                id, name, balance);
    }

    /**
     * 객체 동등성 비교
     * ID가 같으면 같은 사용자로 판단
     * @param obj 비교할 객체
     * @return 동등 여부
     */
    @Override
    public boolean equals(Object obj) {
        //두 객체의 참조가 같으면(즉, 같은 인스턴스면) 바로 true를 반환합니다. 불필요한 비교를 줄여 성능을 높입니다.
        if (this == obj) {
            return true;
        }

        /*
            비교 대상이 null이거나, 클래스 타입이 다르면(즉, 서로 다른 타입이면) false를 반환합니다.
            이는 User 타입끼리만 동등성 비교를 하겠다는 의미입니다.
         */
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        User user = (User) obj;
        return id != null && id.equals(user.id);
    }

    /**
     * 해시코드 생성
     * ID 기반으로 해시코드 생성
     * @return 해시코드
    id가 null이 아니면 id의 해시코드를 반환하고, null이면 0을 반환합니다.
    즉, User 객체의 해시코드는 id 값에 따라 결정되며, 이는 equals()에서 id로 동등성을 판단하는 것과 일치합니다.
    이렇게 하면 HashSet, HashMap 등에서 올바르게 동작할 수 있습니다.     *
     */
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    // ===== 유효성 검증 메소드 =====

    /**
     * 사용자 정보 유효성 검증
     * @return 유효 여부
     */
    public boolean isValid() {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }

        if (password == null || password.isEmpty()) {
            return false;
        }

        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        if (balance < 0) {
            return false;
        }

        return true;
    }

    /**
     * 패스워드 일치 여부 확인
     * @param inputPassword 입력된 패스워드
     * @return 일치 여부
     */
    public boolean matchPassword(String inputPassword) {
        return this.password != null && this.password.equals(inputPassword);
    }
}