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

### Client 입장 시 이름을 먼저 등록해야 채팅 메세지 작성 가능하도록 설정
Client는 채팅 서버에 입장할 대 먼저 이름을 등록해야 한다.

이름을 등록한 후에 채팅 서버에 참여할 수 있도록 설정한다.

<img width="70%" src="https://blog.kakaocdn.net/dn/cojlcs/btsd5X2KDBe/6z4b5VahQluPNyxvotSI01/img.gif">

[동작 로직 설명](https://coding-business.tistory.com/111#client-%EC%9E%85%EC%9E%A5-%EC%8B%9C-%EC%9D%B4%EB%A6%84%EC%9D%84-%EB%A8%BC%EC%A0%80-%EB%93%B1%EB%A1%9D%ED%95%B4%EC%95%BC-%EC%B1%84%ED%8C%85-%EB%A9%94%EC%84%B8%EC%A7%80-%EC%9E%91%EC%84%B1-%EA%B0%80%EB%8A%A5%ED%95%98%EB%8F%84%EB%A1%9D-%EC%84%A4%EC%A0%95)

### 프로토콜([Header : [길이][패킷종류]] - [Body])을 사용하여 Client 데이터 구분하기 (이름 등록, 메세지, 이미지파일)
<img width="80%" src="https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2F31lXI%2FbtsdZNtPlda%2FgkbfCBhi0xNTk7CWIY10aK%2Fimg.png">

위 바이트 배열은 이름을 등록을 위한 Client Data이고,

아래 바이트 배열은 Client의 메세지 데이터 이다.

단순 바이트 배열로는 데이터가 메세지인지 사진인지 이름인지 파악할 수 없다.

<img width="80%" src="https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2Fnd37J%2FbtsdZz3HSSe%2FHOJuNtSsCge6BQGskfcTyK%2Fimg.png">


그래서 Client와 Server가 상호 협의한 프로토콜을 이용해서 해당 데이터가 어떤 데이터인지 파악할 수 있도록 해보자.

사용한 프로토콜은\[Header : \[길이(4바이트)\]\[패킷 종류(4바이트)\]\] \[Body\]이다.

먼저 고정된 길이의 8바이트 헤더를 통해 다음에 오는 Body 데이터의 종류(메세지,이름,사진)와 길이를 파악해 데이터를 잘 저장하고 그에 맞게 처리할 수 있다.

<img width="70%" src="https://blog.kakaocdn.net/dn/qXDQL/btsd0RWHIfB/jkqFNcc4NZHF8fewDJXnT1/img.gif">

[동작 로직 설명](https://coding-business.tistory.com/111#%ED%94%84%EB%A1%9C%ED%86%A0%EC%BD%9C[header--[%EA%B8%B8%EC%9D%B4][%ED%8C%A8%ED%82%B7%EC%A2%85%EB%A5%98]]---[body]%EC%9D%84-%EC%82%AC%EC%9A%A9%ED%95%98%EC%97%AC-client-%EB%8D%B0%EC%9D%B4%ED%84%B0-%EA%B5%AC%EB%B6%84%ED%95%98%EA%B8%B0-%EC%9D%B4%EB%A6%84-%EB%93%B1%EB%A1%9D,-%EB%A9%94%EC%84%B8%EC%A7%80,-%EC%9D%B4%EB%AF%B8%EC%A7%80%ED%8C%8C%EC%9D%BC)

### Client 메세지 채팅 서버 참여자에게 전부 보내기
Client가 작성한 메세지를 Server 채팅 방에서 참여한 모든 Client에게 보낸다.

Client에서 넘어온 메세지를 가공해 Server의 Client 해시 목록을 사용하여 전체에 보낼 수 있다.

<img width="70%" src="https://blog.kakaocdn.net/dn/dIQI3j/btsdZ00XYvP/khIu1k6w9vmDXWJDmZz5ek/img.gif">

[동작 로직 설명](https://coding-business.tistory.com/111#client-%EB%A9%94%EC%84%B8%EC%A7%80-%EC%B1%84%ED%8C%85-%EC%84%9C%EB%B2%84-%EC%B0%B8%EC%97%AC%EC%9E%90%EC%97%90%EA%B2%8C-%EC%A0%84%EB%B6%80-%EB%B3%B4%EB%82%B4%EA%B8%B0)

### Client 종료 시 보낸 메세지, 받은 메세지 수 남아있는 참여자에게 보내기
서버에서 Client가 보낸 메세지 수와 받은 메세지 수 정보를 가지고 있다.

Client가 나가게 되면 계속 접속해 있는 유저들에게 Client가 나갔다는 메세지를 보낸다.

종료 메세지와 함께 나간 Client가 보낸 메세지 수와 받은 메세지 수를 함께 전송한다.

받은 메세지 수를 여러 스레드 상황에서 안전하게 보장하기 위해서 ReetrantLock과 HashMap을 사용하고 보낸 메세지 수를 안전하게 보장하기 위해서 ThreadLocal을 설정한다.

<img width="70%" src="https://blog.kakaocdn.net/dn/J68Xr/btsdZ0s8nDH/fef7VSd0im8sH0FOiP45Uk/img.gif">

[동작 로직 설명](https://coding-business.tistory.com/111#client-%EC%A2%85%EB%A3%8C-%EC%8B%9C-%EB%B3%B4%EB%82%B8-%EB%A9%94%EC%84%B8%EC%A7%80,-%EB%B0%9B%EC%9D%80-%EB%A9%94%EC%84%B8%EC%A7%80-%EC%88%98-%EB%82%A8%EC%95%84%EC%9E%88%EB%8A%94-%EC%B0%B8%EC%97%AC%EC%9E%90%EC%97%90%EA%B2%8C-%EB%B3%B4%EB%82%B4%EA%B8%B0)
