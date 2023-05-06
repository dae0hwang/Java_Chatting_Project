# Java N:N 채팅 서비스
Runnable Thread와 Java ServerSocket 클래스를 사용해서, 여러명이 접속할 수 있는 채팅 서버

Thread, ThreadLocal, ReentrantLock을 사용해서 하나의 Server 프로세스에 여러 Client 동작이 동시에 진행되어도 동시성 문제 없이 잘 돌아가는 채팅 서비스를 제공한다.

### 채팅 서버가 제공하는 동작
1. 여러 Client가 접속할 수 있는 채팅 서버 생성
2. Client 입장 시 이름을 먼저 등록해야 채팅 메세지 작성 가능하도록 설정
3. 프로토콜([Header : [길이][패킷종류]] - [Body])을 사용하여 Client 데이터 구분하기 (이름 등록, 메세지, 이미지파일)
4. Client 메세지 채팅 서버 참여자에게 전부 보내기
5. Client 종료 시 보낸 메세지, 받은 메세지 수 남아있는 참여자에게 보내기

# 블로그 포스트


# 채팅 서버 구성
### 여러 Client가 접속할 수 있는 채팅 서버 생성
Java Thread를 사용해 여러 명이 동시에 사용할 수 있는 채팅 서버를 만든다.

Server Socket을 열어 놓고, 해당 포트로 들어온 Client Socket을 Thread로 등록하여 개별 작동할 수 있도록 로직을 작성했다.

<img width="70%" src="https://blog.kakaocdn.net/dn/mWDS6/btsd0o1yGu4/n1GHVjJDY5irPoBftWuR2K/img.gif"/>

[동작 로직 설명](https://coding-business.tistory.com/111#%EC%97%AC%EB%9F%AC-client%EA%B0%80-%EC%A0%91%EC%86%8D%ED%95%A0-%EC%88%98-%EC%9E%88%EB%8A%94-%EC%84%9C%EB%B2%84-%EC%83%9D%EC%84%B1)




