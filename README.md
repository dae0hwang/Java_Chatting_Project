## 프로젝트 목표
Java Sokcet과 Thread를 사용하여 여러 명이 참여할 수 있는 단체 채팅 서비스를 구현했습니다.

다수의 참여자들이 동시에 동작해도 동시성 문제 없이 작동하는 채팅 서버를 구현하기 위해 ThreadLocal, ReentrantLock를 사용합니다.

## 프로젝트 동작
### 1. 여러명이 접속할 수 있는 채팅 서버 생성
Server Socker을 열어두고, 해당 서버로 들어오는 Client Socket을 Thread로 등록하여 여러 명이 접속할 수 있습니다. [동작 로직 설명](https://coding-business.tistory.com/111#%EC%97%AC%EB%9F%AC-client%EA%B0%80-%EC%A0%91%EC%86%8D%ED%95%A0-%EC%88%98-%EC%9E%88%EB%8A%94-%EC%84%9C%EB%B2%84-%EC%83%9D%EC%84%B1)

<img width="50%" src="https://blog.kakaocdn.net/dn/mWDS6/btsd0o1yGu4/n1GHVjJDY5irPoBftWuR2K/img.gif"/>


### 2. Client 입장 시 이름을 먼저 등록하도록 설정
채팅 서버에 입장하면 먼저 이름을 등록해야 채팅에 참여할 수 있습니다.  
그 이후 채팅을 작성하면 등록한 이름과 메세지가 다른 참여자에게 전달됩니다.
[동작 로직 설명](https://coding-business.tistory.com/111#client-%EC%9E%85%EC%9E%A5-%EC%8B%9C-%EC%9D%B4%EB%A6%84%EC%9D%84-%EB%A8%BC%EC%A0%80-%EB%93%B1%EB%A1%9D%ED%95%B4%EC%95%BC-%EC%B1%84%ED%8C%85-%EB%A9%94%EC%84%B8%EC%A7%80-%EC%9E%91%EC%84%B1-%EA%B0%80%EB%8A%A5%ED%95%98%EB%8F%84%EB%A1%9D-%EC%84%A4%EC%A0%95)
<img width="50%" src="https://blog.kakaocdn.net/dn/cojlcs/btsd5X2KDBe/6z4b5VahQluPNyxvotSI01/img.gif">

### 3. Header와 Body로 구성된 형태의 프로토콜을 구현하여 다양한 종류와 길이의 데이터를 통신
채팅 서버에 사용한 프로토콜 형식은 [Header : [길이(4바이트)][메세지 종류(4바이트)]] - [Body] 입니다. 먼저 고정된 8바이트 크기의 헤더 데이터를 교환합니다. 헤더에는 실제 데이터가 담긴 바디의 길이와 메세지 종류 정보가 담겨 있습니다. 헤더에 명시된 길이만큼 데이터를 받아오고, 메시지 종류에 맞게 로직을 처리할 수 있습니다. [동작 로직 설명](https://coding-business.tistory.com/111#header%EC%99%80-body%EB%A1%9C-%EA%B5%AC%EC%84%B1%EB%90%9C-%ED%98%95%ED%83%9C%EC%9D%98-%ED%94%84%EB%A1%9C%ED%86%A0%EC%BD%9C%EC%9D%84-%EA%B5%AC%ED%98%84%ED%95%98%EC%97%AC-%EB%8B%A4%EC%96%91%ED%95%9C-%EC%A2%85%EB%A5%98%EC%99%80-%EA%B8%B8%EC%9D%B4%EC%9D%98-%EB%8D%B0%EC%9D%B4%ED%84%B0%EB%A5%BC-%ED%86%B5%EC%8B%A0-%EC%9D%B4%EB%A6%84-%EB%93%B1%EB%A1%9D,-%EB%AC%B8%EC%9E%90-%EB%A9%94%EC%84%B8%EC%A7%80,-%EC%9D%B4%EB%AF%B8%EC%A7%80%ED%8C%8C%EC%9D%BC)

<img width="50%" src="https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2Fnd37J%2FbtsdZz3HSSe%2FHOJuNtSsCge6BQGskfcTyK%2Fimg.png">

<img width="50%" src="https://blog.kakaocdn.net/dn/qXDQL/btsd0RWHIfB/jkqFNcc4NZHF8fewDJXnT1/img.gif">

### 4. 참여자가 나가면 해당 참여자의 받고, 보낸 메세지 수를 전송
채팅 서버는 각 클라이언트가 받고 보낸 메세지 수를 저장하고 있으며, 클라이언트가 종료될 때 해당 클라이언트의 받고 보낸 메세지 수를 나머지 참여자들에게 전달합니다. [동작 로직 설명](https://coding-business.tistory.com/111#client-%EC%A2%85%EB%A3%8C-%EC%8B%9C-%EB%B3%B4%EB%82%B8-%EB%A9%94%EC%84%B8%EC%A7%80,-%EB%B0%9B%EC%9D%80-%EB%A9%94%EC%84%B8%EC%A7%80-%EC%88%98-%EB%82%A8%EC%95%84%EC%9E%88%EB%8A%94-%EC%B0%B8%EC%97%AC%EC%9E%90%EC%97%90%EA%B2%8C-%EB%B3%B4%EB%82%B4%EA%B8%B0)

<img width="50%" src="https://blog.kakaocdn.net/dn/bkeofQ/btsgDKaAx9Y/DXNrIFmRlR8vpJJ098PleK/img.gif">
