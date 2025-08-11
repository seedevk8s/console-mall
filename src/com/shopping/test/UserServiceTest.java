package com.shopping.test;

import com.shopping.model.User;
import com.shopping.service.UserService;
import java.io.File;

/**
 * UserService 비즈니스 로직 테스트
 * - Service 레이어는 비즈니스 규칙의 핵심이므로 가장 중요한 테스트
 * - 실무에서 반드시 작성해야 하는 테스트
 *
 * 테스트 범위:
 * 1. 정상 케이스 (Happy Path)
 * 2. 예외 케이스 (Edge Cases)
 * 3. 비즈니스 규칙 검증
 */
public class UserServiceTest {

    private static UserService userService;
    private static int totalTests = 0;
    private static int passedTests = 0;
    private static int failedTests = 0;

    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("     UserService 비즈니스 로직 테스트");
        System.out.println("=========================================\n");

        // 테스트 환경 준비
        setUp();

        // 테스트 실행
        System.out.println("[SECTION 1] 회원가입 기능 테스트");
        System.out.println("-----------------------------------------");
        testRegisterSuccess();
        testRegisterWithDuplicateId();
        testRegisterWithInvalidPassword();
        testRegisterWithInvalidName();
        testRegisterInitialBalance();

        System.out.println("\n[SECTION 2] 비즈니스 규칙 검증");
        System.out.println("-----------------------------------------");
        testIdValidationRules();
        testPasswordValidationRules();
        testNameValidationRules();

        System.out.println("\n[SECTION 3] 예외 처리 테스트");
        System.out.println("-----------------------------------------");
        testExceptionMessages();
        testNullHandling();

        // 테스트 결과 출력
        printTestResults();

        // 테스트 환경 정리
        tearDown();
    }

    /**
     * 테스트 환경 설정
     */
    private static void setUp() {
        // 기존 테스트 데이터 삭제
        clearTestData();

        // UserService 인스턴스 생성
        userService = new UserService();

        System.out.println("[환경설정] 테스트 준비 완료\n");
    }

    /**
     * 테스트 환경 정리
     */
    private static void tearDown() {
        // 테스트 후 데이터 정리 (선택적)
        // clearTestData();
        System.out.println("\n[환경정리] 테스트 종료");
    }

    // ============== SECTION 1: 회원가입 기능 테스트 ==============

    /**
     * 테스트 1: 정상적인 회원가입
     * 목적: Happy Path 검증
     */
    private static void testRegisterSuccess() {
        String testName = "정상 회원가입";
        totalTests++;

        try {
            // Given (준비)
            String id = "testuser01";
            String password = "pass1234";
            String name = "홍길동";

            // When (실행)
            User user = userService.register(id, password, name);

            // Then (검증)
            boolean success = true;
            String failReason = "";

            if (user == null) {
                success = false;
                failReason = "User 객체가 null";
            } else if (!id.equals(user.getId())) {
                success = false;
                failReason = "ID 불일치";
            } else if (!name.equals(user.getName())) {
                success = false;
                failReason = "이름 불일치";
            } else if (user.getBalance() != 10000.0) {
                success = false;
                failReason = "초기 잔액 오류";
            }

            printTestResult(testName, success, failReason);

        } catch (Exception e) {
            printTestResult(testName, false, e.getMessage());
        }
    }

    /**
     * 테스트 2: 중복 ID로 회원가입 시도
     * 목적: ID 중복 체크 로직 검증
     */
    private static void testRegisterWithDuplicateId() {
        String testName = "중복 ID 방지";
        totalTests++;

        try {
            // Given: 첫 번째 사용자 등록
            String duplicateId = "duplicate01";
            userService.register(duplicateId, "pass1234", "첫번째사용자");

            // When: 같은 ID로 두 번째 등록 시도
            try {
                userService.register(duplicateId, "pass5678", "두번째사용자");
                printTestResult(testName, false, "중복 ID가 허용됨");
            } catch (IllegalStateException e) {  // ✅ 변경: RuntimeException → IllegalStateException
                // Then: 예외 발생 확인
                boolean isCorrectException = e.getMessage().contains("이미 존재하는 ID");
                printTestResult(testName, isCorrectException,
                        isCorrectException ? "" : "잘못된 예외 메시지");
            } catch (Exception e) {
                printTestResult(testName, false, "예상치 못한 예외: " + e.getClass().getSimpleName());
            }

        } catch (Exception e) {
            printTestResult(testName, false, "첫 번째 등록 실패: " + e.getMessage());
        }
    }

    /**
     * 테스트 3: 잘못된 패스워드로 회원가입
     * 목적: 패스워드 유효성 검사
     */
    private static void testRegisterWithInvalidPassword() {
        String testName = "패스워드 유효성";
        totalTests++;

        // Sub-test 1: 3자리 패스워드
        boolean subTest1 = false;
        try {
            userService.register("test02", "123", "테스트");
        } catch (IllegalArgumentException e) {  // ✅ 변경: RuntimeException → IllegalArgumentException
            subTest1 = e.getMessage().contains("4자리 이상");
        } catch (Exception e) {
            // 다른 예외는 실패
        }

        // Sub-test 2: null 패스워드
        boolean subTest2 = false;
        try {
            userService.register("test03", null, "테스트");
        } catch (IllegalArgumentException e) {  // ✅ 변경
            subTest2 = e.getMessage().contains("패스워드");
        } catch (Exception e) {
            // 다른 예외는 실패
        }

        // Sub-test 3: 빈 패스워드
        boolean subTest3 = false;
        try {
            userService.register("test04", "", "테스트");
        } catch (IllegalArgumentException e) {  // ✅ 변경
            subTest3 = e.getMessage().contains("4자리 이상");
        } catch (Exception e) {
            // 다른 예외는 실패
        }

        boolean allPassed = subTest1 && subTest2 && subTest3;
        String detail = String.format("3자리:%s, null:%s, 빈문자:%s",
                subTest1 ? "✓" : "✗",
                subTest2 ? "✓" : "✗",
                subTest3 ? "✓" : "✗");

        printTestResult(testName, allPassed, allPassed ? "" : detail);
    }

    /**
     * 테스트 4: 잘못된 이름으로 회원가입
     * 목적: 이름 유효성 검사
     */
    private static void testRegisterWithInvalidName() {
        String testName = "이름 유효성";
        totalTests++;

        // Sub-test 1: null 이름
        boolean subTest1 = false;
        try {
            userService.register("test05", "pass1234", null);
        } catch (IllegalArgumentException e) {  // ✅ 변경
            subTest1 = e.getMessage().contains("이름을 입력");
        } catch (Exception e) {
            // 다른 예외는 실패
        }

        // Sub-test 2: 빈 이름
        boolean subTest2 = false;
        try {
            userService.register("test06", "pass1234", "");
        } catch (IllegalArgumentException e) {  // ✅ 변경
            subTest2 = e.getMessage().contains("이름을 입력");
        } catch (Exception e) {
            // 다른 예외는 실패
        }

        // Sub-test 3: 공백만 있는 이름
        boolean subTest3 = false;
        try {
            userService.register("test07", "pass1234", "   ");
        } catch (IllegalArgumentException e) {  // ✅ 변경
            subTest3 = e.getMessage().contains("이름을 입력");
        } catch (Exception e) {
            // 다른 예외는 실패
        }

        // Sub-test 4: 1자리 이름
        boolean subTest4 = false;
        try {
            userService.register("test08", "pass1234", "김");
        } catch (IllegalArgumentException e) {  // ✅ 변경
            subTest4 = e.getMessage().contains("2자 이상");
        } catch (Exception e) {
            // 다른 예외는 실패
        }

        boolean allPassed = subTest1 && subTest2 && subTest3 && subTest4;
        String detail = String.format("null:%s, 빈문자:%s, 공백:%s, 1자:%s",
                subTest1 ? "✓" : "✗",
                subTest2 ? "✓" : "✗",
                subTest3 ? "✓" : "✗",
                subTest4 ? "✓" : "✗");

        printTestResult(testName, allPassed, allPassed ? "" : detail);
    }

    /**
     * 테스트 5: 초기 잔액 부여
     * 목적: 비즈니스 규칙 - 신규 회원 10000원 지급
     */
    private static void testRegisterInitialBalance() {
        String testName = "초기 잔액 10000원";
        totalTests++;

        try {
            User user = userService.register("test09", "pass1234", "잔액테스트");

            boolean success = (user.getBalance() == 10000.0);
            printTestResult(testName, success,
                    success ? "" : "잔액: " + user.getBalance());

        } catch (Exception e) {
            printTestResult(testName, false, e.getMessage());
        }
    }

    // ============== SECTION 2: 비즈니스 규칙 검증 ==============

    /**
     * 테스트 6: ID 규칙 검증
     * 규칙: 3자 이상, 영문/숫자 (현재는 길이만 체크)
     */
    private static void testIdValidationRules() {
        String testName = "ID 비즈니스 규칙";
        totalTests++;

        // 현재 UserService는 ID 길이/형식 체크가 없음
        // 향후 추가될 기능 테스트 준비

        try {
            // 정상 케이스
            User user1 = userService.register("abc123", "pass1234", "테스트1");

            // 영문만
            User user2 = userService.register("abcdef", "pass1234", "테스트2");

            // 숫자만
            User user3 = userService.register("123456", "pass1234", "테스트3");

            printTestResult(testName, true, "");

        } catch (Exception e) {
            printTestResult(testName, false, e.getMessage());
        }
    }

    /**
     * 테스트 7: 패스워드 규칙 상세 검증
     * 규칙: 최소 4자리
     */
    private static void testPasswordValidationRules() {
        String testName = "패스워드 경계값";
        totalTests++;

        boolean test4Char = false;
        boolean test5Char = false;

        // 4자리 (최소값) - 성공해야 함
        try {
            userService.register("pw01", "1234", "테스트");
            test4Char = true;
        } catch (Exception e) {
            test4Char = false;
        }

        // 5자리 - 성공해야 함
        try {
            userService.register("pw02", "12345", "테스트");
            test5Char = true;
        } catch (Exception e) {
            test5Char = false;
        }

        boolean success = test4Char && test5Char;
        printTestResult(testName, success,
                success ? "" : String.format("4자리:%s, 5자리:%s",
                        test4Char ? "✓" : "✗", test5Char ? "✓" : "✗"));
    }

    /**
     * 테스트 8: 이름 규칙 상세 검증
     * 규칙: 2자 이상, 공백 제거(trim)
     */
    private static void testNameValidationRules() {
        String testName = "이름 공백 처리";
        totalTests++;

        try {
            // 앞뒤 공백이 있는 이름
            User user = userService.register("name01", "pass1234", "  홍길동  ");

            // trim이 적용되어 "홍길동"이 되어야 함
            boolean success = "홍길동".equals(user.getName());
            printTestResult(testName, success,
                    success ? "" : "trim 미적용: '" + user.getName() + "'");

        } catch (Exception e) {
            printTestResult(testName, false, e.getMessage());
        }
    }

    // ============== SECTION 3: 예외 처리 테스트 ==============

    /**
     * 테스트 9: 예외 메시지 정확성
     * 목적: 사용자 친화적인 에러 메시지 확인
     */
    private static void testExceptionMessages() {
        String testName = "예외 메시지 정확성";
        totalTests++;

        boolean allCorrect = true;
        StringBuilder errors = new StringBuilder();

        // 중복 ID 메시지
        try {
            userService.register("msg01", "pass1234", "테스트");
            userService.register("msg01", "pass1234", "테스트");
        } catch (IllegalStateException e) {  // ✅ 변경: 비즈니스 규칙 위반
            if (!e.getMessage().contains("이미 존재하는 ID")) {
                allCorrect = false;
                errors.append("중복ID메시지오류 ");
            }
        } catch (Exception e) {
            allCorrect = false;
            errors.append("예외타입오류 ");
        }

        // 패스워드 메시지
        try {
            userService.register("msg02", "12", "테스트");
        } catch (IllegalArgumentException e) {  // ✅ 변경: 입력값 오류
            if (!e.getMessage().contains("4자리 이상")) {
                allCorrect = false;
                errors.append("패스워드메시지오류 ");
            }
        } catch (Exception e) {
            allCorrect = false;
            errors.append("예외타입오류 ");
        }

        printTestResult(testName, allCorrect, errors.toString());
    }

    /**
     * 테스트 10: Null 처리 종합
     * 목적: NullPointerException 방지 확인
     */
    private static void testNullHandling() {
        String testName = "Null 안정성";
        totalTests++;

        boolean allSafe = true;
        String errorDetail = "";

        // 모든 파라미터가 null
        try {
            userService.register(null, null, null);
            allSafe = false; // 예외가 발생해야 함
            errorDetail = "예외가 발생하지 않음";
        } catch (NullPointerException e) {
            // NPE가 발생하면 안됨 - 실패!
            allSafe = false;
            errorDetail = "NPE 발생: " + e.getMessage();
        } catch (IllegalArgumentException e) {  // ✅ 변경: 입력값 검증 예외
            // IllegalArgumentException 발생 = 정상
            if (e.getMessage() == null || e.getMessage().isEmpty()) {
                allSafe = false;
                errorDetail = "예외 메시지가 없음";
            } else {
                // 정상적인 검증 예외 = 성공
                allSafe = true;
            }
        } catch (Exception e) {
            allSafe = false;
            errorDetail = "예상치 못한 예외: " + e.getClass().getSimpleName();
        }

        printTestResult(testName, allSafe, errorDetail);
    }

    // ============== 유틸리티 메서드 ==============

    /**
     * 테스트 결과 출력
     */
    private static void printTestResult(String testName, boolean success, String detail) {
        if (success) {
            passedTests++;
            System.out.printf("  ✅ %-20s : PASS\n", testName);
        } else {
            failedTests++;
            System.out.printf("  ❌ %-20s : FAIL", testName);
            if (!detail.isEmpty()) {
                System.out.printf(" (%s)", detail);
            }
            System.out.println();
        }
    }

    /**
     * 전체 테스트 결과 요약
     */
    private static void printTestResults() {
        System.out.println("\n=========================================");
        System.out.println("           테스트 결과 요약");
        System.out.println("=========================================");
        System.out.printf("총 테스트: %d개\n", totalTests);
        System.out.printf("✅ 성공: %d개\n", passedTests);
        System.out.printf("❌ 실패: %d개\n", failedTests);

        double successRate = totalTests > 0 ? (passedTests * 100.0) / totalTests : 0;
        System.out.printf("성공률: %.1f%%\n", successRate);

        if (failedTests == 0) {
            System.out.println("\n🎉 모든 테스트 통과! Service 레이어 안정적!");
        } else {
            System.out.println("\n⚠️  일부 테스트 실패. 비즈니스 로직 확인 필요!");
        }
    }

    /**
     * 테스트 데이터 정리
     */
    private static void clearTestData() {
        try {
            File file = new File("data/users.dat");
            if (file.exists()) {
                file.delete();
                System.out.println("[초기화] 기존 데이터 삭제");
            }
        } catch (Exception e) {
            System.err.println("[경고] 데이터 초기화 실패: " + e.getMessage());
        }
    }
}

/**
 * 💡 Service 테스트가 중요한 이유:
 *
 * 1. 비즈니스 로직의 핵심
 *    - 실제 업무 규칙이 구현된 곳
 *    - 버그가 가장 치명적인 곳
 *
 * 2. 테스트하기 좋은 구조
 *    - UI나 DB에 의존하지 않음
 *    - 순수 Java 로직
 *    - Mock 객체 사용 가능
 *
 * 3. 리팩토링 안정성
 *    - 코드 수정 시 테스트로 검증
 *    - 기존 기능 보장
 *
 * 4. 문서화 효과
 *    - 테스트 코드 = 사용 예제
 *    - 비즈니스 규칙 명시
 */