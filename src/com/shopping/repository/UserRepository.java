// ===============================================
// UserRepository.java
// ===============================================
package com.shopping.repository;

import com.shopping.model.User;
import com.shopping.persistence.FileManager;
import com.shopping.util.Constants;
import java.util.List;
import java.util.ArrayList;

/**
 * 사용자 데이터 접근을 담당하는 Repository 클래스
 * Data Access Layer의 일부로 CRUD 기능 제공
 */
public class UserRepository {

    // 파일명 상수 (Constants에서 가져옴)
    private static final String FILE_NAME = Constants.USER_DATA_FILE;

    /**
     * 사용자 저장
     * @param user 저장할 User 객체
     * @return 저장된 User 객체
     */
    public User save(User user) {
        // 기존 사용자 목록 조회
        List<User> users = FileManager.readFromFile(FILE_NAME);

        // 새 사용자 추가
        users.add(user);

        // 파일에 저장
        FileManager.writeToFile(FILE_NAME, users);

        return user;
    }

    /**
     * ID로 사용자 조회
     * @param id 사용자 ID
     * @return User 객체 (없으면 null)
     */
    public User findById(String id) {
        List<User> users = FileManager.readFromFile(FILE_NAME);

        // Java 8 Stream API를 사용한 검색
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * 사용자 정보 업데이트
     * @param user 업데이트할 User 객체
     */
    public void update(User user) {
        List<User> users = FileManager.readFromFile(FILE_NAME);

        // 기존 사용자 제거
        users.removeIf(u -> u.getId().equals(user.getId()));

        // 업데이트된 사용자 추가
        users.add(user);

        // 파일에 저장
        FileManager.writeToFile(FILE_NAME, users);
    }

    /**
     * ID 중복 확인
     * @param id 확인할 ID
     * @return 존재 여부
     */
    public boolean existsById(String id) {
        return findById(id) != null;
    }

    /**
     * 모든 사용자 조회
     * @return 사용자 목록
     */
    public List<User> findAll() {
        return FileManager.readFromFile(FILE_NAME);
    }

    /**
     * 사용자 삭제
     * @param id 삭제할 사용자 ID
     * @return 삭제 성공 여부
     */
    public boolean deleteById(String id) {
        List<User> users = FileManager.readFromFile(FILE_NAME);

        // 사용자 제거
        boolean removed = users.removeIf(u -> u.getId().equals(id));

        if (removed) {
            // 변경사항 저장
            FileManager.writeToFile(FILE_NAME, users);
        }

        return removed;
    }
}