# Java N:N 채팅 서비스
Runnable Thread와 Java ServerSocket 클래스를 사용해서, 여러 명이 접속할 수 있는 채팅 서버를 구현했습니다.

Thread, ThreadLocal, ReentrantLock을 사용해서 하나의 채팅서버에서 여러 Client 동작이 동시에 진행되어도 동시성 문제 없이 작동하는 채팅 서버입니다.

### 채팅 서버가 제공하는 동작
1. 여러 Client가 접속할 수 있는 채팅 서버 생성
2. Client 입장 시 이름을 먼저 등록해야 채팅 메세지 작성 가능하도록 설정
3. Header와 Body로 구성된 형태의 프로토콜을 구현하여 다양한 종류와 길이의 데이터를 통신
4. Client 메세지 채팅 서버 참여자에게 전부 전송
5. Client 종료 시 보낸 메세지, 받은 메세지 수 남아있는 참여자에게 전송

# 채팅 서버 구성
### 1. 여러 Client가 접속할 수 있는 채팅 서버 생성
Java Thread를 사용해 여러 명이 동시에 접속 이용할 수 있는 서버를 구성했습니다.

Server Socket을 열어 놓고, 해당 포트로 들어온 Client Socket을 Thread로 등록하였습니다.

<img width="80%" src="https://blog.kakaocdn.net/dn/mWDS6/btsd0o1yGu4/n1GHVjJDY5irPoBftWuR2K/img.gif"/>

[동작 로직 설명](https://coding-business.tistory.com/111#%EC%97%AC%EB%9F%AC-client%EA%B0%80-%EC%A0%91%EC%86%8D%ED%95%A0-%EC%88%98-%EC%9E%88%EB%8A%94-%EC%84%9C%EB%B2%84-%EC%83%9D%EC%84%B1)

### 2. Client 입장 시 이름을 먼저 등록해야 채팅 메세지 작성 가능하도록 설정
Client는 채팅 서버에 입장할 대 먼저 이름을 등록해야 채팅에 참여할 수 있습니다.

이름을 등록한 후에 채팅을 작성하면 등록한 이름과 작성한 메세지가 자신과 참여자에게 전달됩니다.

<img width="80%" src="https://blog.kakaocdn.net/dn/cojlcs/btsd5X2KDBe/6z4b5VahQluPNyxvotSI01/img.gif">

[동작 로직 설명](https://coding-business.tistory.com/111#client-%EC%9E%85%EC%9E%A5-%EC%8B%9C-%EC%9D%B4%EB%A6%84%EC%9D%84-%EB%A8%BC%EC%A0%80-%EB%93%B1%EB%A1%9D%ED%95%B4%EC%95%BC-%EC%B1%84%ED%8C%85-%EB%A9%94%EC%84%B8%EC%A7%80-%EC%9E%91%EC%84%B1-%EA%B0%80%EB%8A%A5%ED%95%98%EB%8F%84%EB%A1%9D-%EC%84%A4%EC%A0%95)

### 3. Header와 Body로 구성된 형태의 프로토콜을 구현하여 다양한 종류와 길이의 데이터를 통신
채팅 서버에 사용한 프로토콜 형식은 [Header : [길이(4바이트)][메세지 종류(4바이트)]] - [Body] 입니다. 먼저 고정된 8바이트 크기의 헤더 데이터를 교환합니다. 헤더에는 실제 데이터가 담긴 바디의 길이와 메세지 종류 정보가 담겨 있습니다. 헤더에 명시된 길이만큼 데이터를 받아오고, 메시지 종류에 맞게 로직을 처리할 수 있습니다.

<img width="80%" src="https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2Fnd37J%2FbtsdZz3HSSe%2FHOJuNtSsCge6BQGskfcTyK%2Fimg.png">

<img width="80%" src="https://blog.kakaocdn.net/dn/qXDQL/btsd0RWHIfB/jkqFNcc4NZHF8fewDJXnT1/img.gif">

[동작 로직 설명](https://coding-business.tistory.com/111#header%EC%99%80-body%EB%A1%9C-%EA%B5%AC%EC%84%B1%EB%90%9C-%ED%98%95%ED%83%9C%EC%9D%98-%ED%94%84%EB%A1%9C%ED%86%A0%EC%BD%9C%EC%9D%84-%EA%B5%AC%ED%98%84%ED%95%98%EC%97%AC-%EB%8B%A4%EC%96%91%ED%95%9C-%EC%A2%85%EB%A5%98%EC%99%80-%EA%B8%B8%EC%9D%B4%EC%9D%98-%EB%8D%B0%EC%9D%B4%ED%84%B0%EB%A5%BC-%ED%86%B5%EC%8B%A0-%EC%9D%B4%EB%A6%84-%EB%93%B1%EB%A1%9D,-%EB%AC%B8%EC%9E%90-%EB%A9%94%EC%84%B8%EC%A7%80,-%EC%9D%B4%EB%AF%B8%EC%A7%80%ED%8C%8C%EC%9D%BC)

### 4. Client 메세지 채팅 서버 참여자에게 전송
Client가 작성한 메세지를 Server 채팅 방에서 참여한 모든 Client에게 전송합니다.

Client에서 넘어온 메세지를 가공해 Server의 Client 해시 목록을 사용하여 전체에 보낼 수 있습니다.

<img width="80%" src="https://blog.kakaocdn.net/dn/dIQI3j/btsdZ00XYvP/khIu1k6w9vmDXWJDmZz5ek/img.gif">

[동작 로직 설명](https://coding-business.tistory.com/111#client-%EB%A9%94%EC%84%B8%EC%A7%80-%EC%B1%84%ED%8C%85-%EC%84%9C%EB%B2%84-%EC%B0%B8%EC%97%AC%EC%9E%90%EC%97%90%EA%B2%8C-%EC%A0%84%EB%B6%80-%EB%B3%B4%EB%82%B4%EA%B8%B0)

### 5. Client 종료 시 보낸 메세지, 받은 메세지 수 남아있는 참여자에게 전송
서버에서 Client가 보낸 메세지 수와 받은 메세지 수 정보를 저장하고 있습니다.

Client가 나가게 되면 계속 접속해 있는 유저들에게 Client가 나갔다는 메세지를 보냅니다.

종료 메세지와 함께 나간 Client가 보낸 메세지 수와 받은 메세지 수를 함께 전송합니다.

받은 메세지 수를 여러 스레드 상황에서 안전하게 보장하기 위해서 ReetrantLock과 HashMap을 사용하고 보낸 메세지 수를 안전하게 보장하기 위해서 ThreadLocal을 설정합니다.

<img width="80%" src="https://blog.kakaocdn.net/dn/bkeofQ/btsgDKaAx9Y/DXNrIFmRlR8vpJJ098PleK/img.gif">

[동작 로직 설명](https://coding-business.tistory.com/111#client-%EC%A2%85%EB%A3%8C-%EC%8B%9C-%EB%B3%B4%EB%82%B8-%EB%A9%94%EC%84%B8%EC%A7%80,-%EB%B0%9B%EC%9D%80-%EB%A9%94%EC%84%B8%EC%A7%80-%EC%88%98-%EB%82%A8%EC%95%84%EC%9E%88%EB%8A%94-%EC%B0%B8%EC%97%AC%EC%9E%90%EC%97%90%EA%B2%8C-%EB%B3%B4%EB%82%B4%EA%B8%B0)
