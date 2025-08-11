package com.shopping.persistence;

import com.shopping.model.User;

import java.io.*;
import java.util.*;

/**
 * 파일 입출력을 담당하는 유틸리티 클래스
 * Persistence Layer의 핵심 컴포넌트
 *
 * 개선사항:
 * - data 디렉토리 자동 생성
 * - 파일 경로 문제 해결
 * - 더 나은 예외 처리
 *
 * @version 2.0
 */
public class FileManager {

    // data 디렉토리 경로
    private static final String DATA_DIR = "data";

    // static 초기화 블록 - 클래스 로드 시 data 디렉토리 자동 생성
    static {
        createDataDirectory();
    }

    /**
     * data 디렉토리 생성
     * 디렉토리가 없으면 자동으로 생성
     */
    public static void createDataDirectory() {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            boolean created = dataDir.mkdir();
            if (created) {
                System.out.println("[FileManager] data 디렉토리가 생성되었습니다.");
            } else {
                System.err.println("[FileManager] data 디렉토리 생성 실패!");
            }
        }
    }

    // 파일 경로 정규화 -- 파일 경로를 표준 형태로 만들어주는 것
    // "다양한 형태의 파일 경로를 컴퓨터가 이해할 수 있는 표준 형태로 통일시키는 작업"
    /*
        쉬운 비유로 설명하면:
        주소를 적는 방법이 여러 가지 있는 것처럼:

        "서울시 강남구"
        "서울/강남"
        "서울\강남"

    이렇게 다양한 표현을 하나의 표준 형태로 통일하는 것이 정규화입니다.

    // 사용자가 "users.dat"만 입력하면
    // → "data/users.dat" 또는 "data_\_users.dat"로 만들어줌

    // Windows: data_\_users.dat
    // Mac/Linux: data/users.dat
     */
    // 운영체제에 관계없이 올바른 경로 반환
    /**
     * 파일 경로 정규화 메소드
     * - 정규화란? 다양한 형태의 파일 경로를 표준 형태로 통일하는 작업
     * - 예시: "users.dat" → "data/users.dat" 또는 "data_\_users.dat"
     * - 목적: 운영체제별 경로 차이 해결 (Windows는 \, Mac/Linux는 / 사용)
     *
     * @param {fileName 정규화할 파일명 또는 경로
     * @return 표준화된 전체 파일 경로
     */
    private static String normalizePath(String filename) {
        // 이미 경로가 포함된 경우 그대로 반환
        if (filename.contains(File.separator) || filename.contains("/") || filename.contains("\\")) {
            return filename;
        }

        // data 디렉토리 경로 추가
        return DATA_DIR + File.separator + filename;
    }

    /**
     * 파일에서 객체 리스트 읽기
     * @param <T> 읽을 객체의 타입
     * @param filename 파일명
     * @return 객체 리스트 (파일이 없으면 빈 리스트)
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> readFromFile(String filename) {
        // 파일 경로 정규화
        String fullPath = normalizePath(filename);
        File file = new File(fullPath);         //✅ 수정: fullPath 사용

        // 파일이 존재하지 않는 경우
        if (!file.exists()) {
            System.out.println("[FileManager] 파일이 존재하지 않습니다: " + fullPath);
            System.out.println("[FileManager] 빈 리스트를 반환합니다.");
            return new ArrayList<>();
        }

        // 파일이 비어있는 경우
        if (file.length() == 0) {
            System.out.println("[FileManager] 파일이 비어있습니다: " + fullPath);
            return new ArrayList<>();
        }

        // 파일 읽기 시도
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(file))) {

            List<T> data = (List<T>) ois.readObject();
            System.out.println("[FileManager] 파일 읽기 성공: " + fullPath + " (" + data.size() + "개 항목)");
            return data;

        } catch (FileNotFoundException e) {
            System.out.println("[FileManager] 파일을 찾을 수 없음: " + fullPath);
            return new ArrayList<>();

        } catch (EOFException e) {
            System.out.println("[FileManager] 파일이 비어있거나 손상됨: " + fullPath);
            return new ArrayList<>();

        } catch (IOException e) {
            System.err.println("[FileManager] 파일 읽기 오류: " + fullPath);
            System.err.println("  오류 내용: " + e.getMessage());
            return new ArrayList<>();

        } catch (ClassNotFoundException e) {
            System.err.println("[FileManager] 클래스를 찾을 수 없음: " + e.getMessage());
            return new ArrayList<>();

        } catch (Exception e) {
            System.err.println("[FileManager] 예상치 못한 오류: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * 객체 리스트를 파일에 저장
     * @param <T> 저장할 객체의 타입
     * @param filename 파일명
     * @param data 저장할 데이터
     */
    public static <T> void writeToFile(String filename, List<T> data) {
        // null 체크
        if (data == null) {
            System.err.println("[FileManager] 저장할 데이터가 null입니다.");
            return;
        }

        // 파일 경로 정규화
        String fullPath = normalizePath(filename);
        File file = new File(fullPath);

        // 부모 디렉토리 확인 및 생성
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            boolean created = parentDir.mkdirs();
            if (created) {
                System.out.println("[FileManager] 디렉토리 생성: " + parentDir.getPath());
            }
        }

        // 파일 저장 시도
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(file))) {

            oos.writeObject(data);
            oos.flush();  // 버퍼 강제 flush

            System.out.println("[FileManager] 파일 저장 성공: " + fullPath + " (" + data.size() + "개 항목)");

        } catch (IOException e) {
            System.err.println("[FileManager] 파일 저장 실패: " + fullPath);
            System.err.println("  오류 내용: " + e.getMessage());
            e.printStackTrace();

        } catch (Exception e) {
            System.err.println("[FileManager] 예상치 못한 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 파일 존재 여부 확인
     * @param filename 파일명
     * @return 존재 여부
     */
    public static boolean fileExists(String filename) {
        String fullPath = normalizePath(filename);
        File file = new File(fullPath);
        boolean exists = file.exists() && file.isFile();

        System.out.println("[FileManager] 파일 존재 확인: " + fullPath + " -> " + exists);
        return exists;
    }

    /**
     * 파일 삭제
     * @param filename 삭제할 파일명
     * @return 삭제 성공 여부
     */
    public static boolean deleteFile(String filename) {
        String fullPath = normalizePath(filename);
        File file = new File(fullPath);

        if (!file.exists()) {
            System.out.println("[FileManager] 삭제할 파일이 없음: " + fullPath);
            return false;
        }

        boolean deleted = file.delete();
        if (deleted) {
            System.out.println("[FileManager] 파일 삭제 성공: " + fullPath);
        } else {
            System.err.println("[FileManager] 파일 삭제 실패: " + fullPath);
        }

        return deleted;
    }

    /**
     * 모든 데이터 파일 삭제
     * 테스트나 초기화 시 사용
     */
    public static void deleteAllDataFiles() {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            return;
        }

        File[] files = dataDir.listFiles((dir, name) -> name.endsWith(".dat"));
        if (files != null) {
            for (File file : files) {
                if (file.delete()) {
                    System.out.println("[FileManager] 파일 삭제: " + file.getName());
                }
            }
        }
    }

    /**
     * 파일 크기 확인
     * @param filename 파일명
     * @return 파일 크기 (바이트)
     */
    public static long getFileSize(String filename) {
        String fullPath = normalizePath(filename);
        File file = new File(fullPath);
        return file.exists() ? file.length() : 0;
    }

    /**
     * 파일 정보 출력
     * 디버깅용
     */
    public static void printFileInfo(String filename) {
        String fullPath = normalizePath(filename);
        File file = new File(fullPath);

        System.out.println("\n=== 파일 정보 ===");
        System.out.println("파일명: " + filename);
        System.out.println("전체 경로: " + fullPath);
        System.out.println("절대 경로: " + file.getAbsolutePath());
        System.out.println("존재 여부: " + file.exists());
        System.out.println("파일 크기: " + file.length() + " bytes");
        System.out.println("읽기 가능: " + file.canRead());
        System.out.println("쓰기 가능: " + file.canWrite());
        System.out.println("===============\n");
    }

    /**
     * 현재 작업 디렉토리 출력
     * 디버깅용
     */
    public static void printWorkingDirectory() {
        System.out.println("현재 작업 디렉토리: " + System.getProperty("user.dir"));
        System.out.println("data 디렉토리 경로: " + new File(DATA_DIR).getAbsolutePath());
    }
}

