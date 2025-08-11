package com.shopping.service;

import com.shopping.model.User;
import com.shopping.repository.UserRepository;
import com.shopping.util.ValidationUtils;
import java.util.Objects;

/**
 * 사용자 관련 비즈니스 로직을 처리하는 서비스 클래스
 *
 * 개선 포인트:
 * 1. ValidationUtils로 중복 제거
 * 2. Objects.requireNonNull() 활용
 * 3. 메서드 분리로 가독성 향상
 * 4. 명확한 예외 타입 사용
 */
public class UserService {

    private final UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepository();
    }

    /**
     * 회원가입 처리
     *
     * @param id 사용자 ID
     * @param password 비밀번호
     * @param name 사용자 이름
     * @return 생성된 User 객체
     * @throws IllegalArgumentException 입력값이 유효하지 않을 때
     * @throws IllegalStateException ID가 중복될 때
     */
    public User register(String id, String password, String name) {
        // 1. 입력값 검증 (한 곳에 모아서 처리)
        validateRegistrationInput(id, password, name);

        // 2. 비즈니스 규칙 검증 (중복 체크)
        if (userRepository.existsById(id)) {
            throw new IllegalStateException("이미 존재하는 ID입니다: " + id);
        }

        // 3. User 생성 및 저장
        User user = new User(id, password, name.trim());
        User savedUser = userRepository.save(user);

        System.out.println("[UserService] 새 사용자 등록: " + savedUser.getId());
        return savedUser;
    }

    /**
     * 회원가입 입력값 검증 (private 메서드로 분리)
     */
    private void validateRegistrationInput(String id, String password, String name) {
        // ValidationUtils 사용
        ValidationUtils.requireNonEmpty(id, "ID를 입력해주세요");
        ValidationUtils.requireMinLength(password, 4, "패스워드는 최소 4자리 이상이어야 합니다");
        ValidationUtils.requireNonEmpty(name, "이름을 입력해주세요");
        ValidationUtils.requireMinLength(name.trim(), 2, "이름은 2자 이상이어야 합니다");
    }

    /**
     * 로그인 처리
     *
     * @param id 사용자 ID
     * @param password 비밀번호
     * @return 로그인된 User 객체
     * @throws IllegalArgumentException 입력값이 유효하지 않을 때
     * @throws IllegalStateException 인증 실패 시
     */
    public User login(String id, String password) {
        // 입력값 검증
        ValidationUtils.requireNonEmpty(id, "ID를 입력해주세요");
        ValidationUtils.requireNonEmpty(password, "패스워드를 입력해주세요");

        // 사용자 조회
        User user = findUserById(id);

        // 패스워드 검증
        if (!user.getPassword().equals(password)) {
            throw new IllegalStateException("패스워드가 일치하지 않습니다");
        }

        System.out.println("[UserService] 로그인 성공: " + user.getId());
        return user;
    }

    /**
     * 사용자 조회 (공통 메서드)
     *
     * @param userId 사용자 ID
     * @return User 객체
     * @throws IllegalStateException 사용자를 찾을 수 없을 때
     */
    private User findUserById(String userId) {
        Objects.requireNonNull(userId, "사용자 ID는 필수입니다");

        User user = userRepository.findById(userId);
        if (user == null) {
            throw new IllegalStateException("사용자를 찾을 수 없습니다: " + userId);
        }
        return user;
    }

    /**
     * 사용자 잔액 조회
     */
    public double getBalance(String userId) {
        User user = findUserById(userId);
        return user.getBalance();
    }

    /**
     * 사용자 잔액 업데이트
     */
    public void updateBalance(String userId, double newBalance) {
        // 비즈니스 규칙: 잔액은 음수가 될 수 없음
        if (newBalance < 0) {
            throw new IllegalArgumentException("잔액은 음수가 될 수 없습니다: " + newBalance);
        }

        User user = findUserById(userId);
        double oldBalance = user.getBalance();
        user.setBalance(newBalance);
        userRepository.update(user);

        System.out.printf("[UserService] 잔액 변경: %s (%.0f → %.0f)%n",
                userId, oldBalance, newBalance);
    }

    /**
     * 잔액 차감
     *
     * @return 차감 성공 여부
     */
    public boolean deductBalance(String userId, double amount) {
        validateAmount(amount, "차감");

        try {
            User user = findUserById(userId);

            // 잔액 부족 체크
            if (user.getBalance() < amount) {
                return false;
            }

            // 잔액 차감
            user.setBalance(user.getBalance() - amount);
            userRepository.update(user);

            System.out.printf("[UserService] 잔액 차감: %s (-%.0f)%n", userId, amount);
            return true;

        } catch (IllegalStateException e) {
            // 사용자를 찾을 수 없는 경우
            return false;
        }
    }

    /**
     * 잔액 충전
     */
    public void addBalance(String userId, double amount) {
        validateAmount(amount, "충전");

        User user = findUserById(userId);
        user.setBalance(user.getBalance() + amount);
        userRepository.update(user);

        System.out.printf("[UserService] 잔액 충전: %s (+%.0f)%n", userId, amount);
    }

    /**
     * 금액 유효성 검증 (공통 메서드)
     */
    private void validateAmount(double amount, String operation) {
        if (amount <= 0) {
            throw new IllegalArgumentException(operation + " 금액은 양수여야 합니다: " + amount);
        }
    }

    /**
     * 사용자 정보 조회
     */
    public User getUser(String userId) {
        return findUserById(userId);
    }

    /**
     * 패스워드 변경
     */
    public void changePassword(String userId, String oldPassword, String newPassword) {
        // 입력값 검증
        ValidationUtils.requireNonEmpty(oldPassword, "현재 패스워드를 입력해주세요");
        ValidationUtils.requireMinLength(newPassword, 4, "새 패스워드는 최소 4자리 이상이어야 합니다");

        User user = findUserById(userId);

        // 현재 패스워드 확인
        if (!user.getPassword().equals(oldPassword)) {
            throw new IllegalStateException("현재 패스워드가 일치하지 않습니다");
        }

        // 패스워드 변경
        user.setPassword(newPassword);
        userRepository.update(user);

        System.out.println("[UserService] 패스워드 변경 완료: " + userId);
    }
}