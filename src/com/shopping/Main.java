package com.shopping;

import com.shopping.controller.MainController;

/**
 * 쇼핑몰 애플리케이션의 진입점
 * MVC 패턴 학습용 미니 프로젝트
 * 
 * @author Student
 * @version 1.0
 */
public class Main {
    
    /**
     * 프로그램 시작 메소드
     * @param args 명령행 인자 (사용하지 않음)
     */
    public static void main(String[] args) {
        // 메인 컨트롤러 인스턴스 생성
        MainController mainController = new MainController();
        
        // 애플리케이션 시작
        mainController.start();
    }
}