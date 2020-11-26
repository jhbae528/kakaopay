# kakaopay
subject - 배재호


# GitHub
* https://github.com/jhbae528/kakaopay
	

# 개발 프레임워크

* Spring Boot 2.4

* RestAPI

* JDK 11

* 데이터베이스 
	H2 embedded mem

* 테이블 설계: 
    create table payment_info (
        manage_id varchar(20) not null,	-- 관리번호
        pay_type varchar(10),			-- 결제 유형
        installment integer,			-- 할부
        amount integer,					-- 금액
        vat integer,					-- 부가가치세
        pay_statement varchar(450),		-- string 명세
        origin_manage_id varchar(255),	-- 원거래 관리번호
        primary key (manage_id)
    )


# 문제해결 전략
* 통신 데이터 
    RequestDto, ResponseDto 를 정의하여 json 매핑으로 사용

* 관리번호
    P0000000...00 || sequence 번호로 생성 [20자리] 
    
* 암호화 
    AES/CBC/PKCS5Padding

* Multi Thread
    결제 요청시 : 결제 카드번호를 기준으로 syncronized block 설정
    
    결제 취소시 : 원거래 관리번호를 기준으로 syncronized block 설정
    
* Exception
    CustomException 및 에러코드 정의
    
* 금액 데이터
    금액을 토대로 부가가치세를 자동 계산 후 거래 진행
    
* 단위테스트
    MockMvc 사용


# 빌드 및 실행 방법
* 빌드 
    Gradle

* 구조
    - controller 
        [PaymentController.java]
    - domain
        - entity
        - repository
            [PaymentInfoRepository.java]
    - dto
    - exception
    - security
    - service 
        [PaymentServiceImpl.java]
    - util

* 실행방법

    결제 API : PaymentController.requestPayment() -> paymentService.processPayment()
    
    결제 취소 API : PaymentController.requestCancel() -> paymentService.processCancel()
    
    데이터 조회 API : PaymentController.requestPayment()-> paymentService.processReadPayment()


# 단위 테스트
* Test Class
    MainControllerTest.java
    
* 필수 구현 API 기능
    1. 결제 API
        reqPay() 테스트 실행
    
    2. 결제 취소 API
        reqCancel() 테스트 실행

    3. 데이터 조회 API
        reqReadData() 테스트 실행
        
* 선택문제
    Test Case 1
        case1Test() 테스트 실행
        
    Test Case 2
        case2Test() 테스트 실행
        
    Test Case 3
        case3Test() 테스트 실행
        
    Multi Thread - 결제 - 하나의 카드번호로 동시에 결제를 할 수 없음
        multiThreadCasePaymentTest() 테스트 실행
        
    Multi Thread - 전체취소 - 결제 한 건에 대한 전체취소를 동시에 할 수 없음
        multiThreadCaseTotalCancelTest() 테스트 실행
        
    Multi Thread - 부분취소 - 결제 한 건에 대한 부분취소를 동시에 할 수 없음
        multiThreadCasePartCancelTest() 테스트 실행