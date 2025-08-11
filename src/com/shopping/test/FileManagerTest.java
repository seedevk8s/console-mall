// ===============================================
// 디버깅용 테스트 클래스
// ===============================================
package com.shopping.test;

import com.shopping.persistence.FileManager;
import com.shopping.model.User;
import java.util.ArrayList;
import java.util.List;

/**
 * FileManager 테스트 및 디버깅
 */
public class FileManagerTest {

    public static void main(String[] args) {
        System.out.println("=== FileManager 테스트 시작 ===\n");

        // 1. 작업 디렉토리 확인
        System.out.println("1. 작업 디렉토리 확인");
        FileManager.printWorkingDirectory();
        System.out.println();

        // 2. data 디렉토리 생성
        System.out.println("2. data 디렉토리 생성");
        FileManager.createDataDirectory();
        System.out.println();

        // 3. 테스트 데이터 생성
        System.out.println("3. 테스트 데이터 생성");
        List<User> users = new ArrayList<>();
        users.add(new User("test1", "pass1234", "테스트1"));
        users.add(new User("test2", "pass5678", "테스트2"));
        System.out.println("생성된 사용자: " + users.size() + "명");
        System.out.println();

        // 4. 파일 저장
        System.out.println("4. 파일 저장 테스트");
        FileManager.writeToFile("users.dat", users);
        System.out.println();

        // 5. 파일 정보 확인
        System.out.println("5. 파일 정보 확인");
        FileManager.printFileInfo("users.dat");
        System.out.println();

        // 6. 파일 읽기
        System.out.println("6. 파일 읽기 테스트");
        List<User> loadedUsers = FileManager.readFromFile("users.dat");
        System.out.println("읽어온 사용자: " + loadedUsers.size() + "명");
        for (User user : loadedUsers) {
            System.out.println("  - " + user);
        }
        System.out.println();

        // 7. 파일 존재 확인
        System.out.println("7. 파일 존재 확인");
        System.out.println("users.dat 존재: " + FileManager.fileExists("users.dat"));
        System.out.println("products.dat 존재: " + FileManager.fileExists("products.dat"));
        System.out.println();

        System.out.println("=== FileManager 테스트 완료 ===");
    }
}