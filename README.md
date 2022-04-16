# SPCM

# 핵심 기능 일람
* 데스크톱 WOL 부팅, 종료, 볼륨 조절 및 URL 실행 등 제어 기능
* 트위터 계정 사용, API 접근 및 트윗 청소기
* 저녁밥 추천기 (중요)
* Firebase Cloud Messaging(안드로이드 푸쉬 알림) 및 E-mail 송신 시스템
* 작업 스케줄러 (cron표현식 및 eval로 DB에 저장된 작업들 실행)

# 설치 방법

서버 및 앱 설정에서 사용하는 token값은 임의의 문자열을 사용하면 됨. (비밀번호라고 생각하세요)

## 서버 설정
1. cd console-server
2. npm install
3. .env 파일 내용 작성
4. node index.js (sudo 권장)

## 데이터베이스 설정
1. console-server/database/init.sql 실행
2. charset 관련 오류 발생시 utf8mb4로 설정할 것

## PC 서버 설정
1. cd remote-server
2. npm install
3. .env 파일 내용 작성
4. node index.js (관리자 권한 필요:시스템 온도 측정 코드에서 권한 사용)

## 안드로이드 앱 설정
1. android/app/src/main/java/com/sasarinomari/spcmconsole/APIInterface.kt 파일을 열고 토큰 값 및 서버 호스트 주소 입력
2. (서버가 http일 경우) android/app/src/main/res/xml/network_security_config.xml 파일을 열고 서버 도메인 입력
