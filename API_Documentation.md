# CultureMate API 명세서

## 1. 인증 (Auth)

### `POST /api/v1/auth/login`

- **역할**: 사용자의 로그인 ID와 비밀번호를 받아 인증을 수행하고, 성공 시 사용자 정보를 반환합니다.
- **요청 (Request)**
  - **Body**:
    ```json
    {
      "loginId": "string",
      "password": "string"
    }
    ```
- **응답 (Response)**
  - **Success (200 OK)**:
    ```json
    {
      "id": 1,
      "loginId": "string",
      "role": "MEMBER"
    }
    ```

---

## 2. 회원 (Member)

### `POST /api/v1/member`

- **역할**: 새로운 회원을 등록(가입)합니다.
- **요청 (Request)**
  - **Body**:
    ```json
    {
      "loginId": "string",
      "password": "string",
      "email": "user@example.com",
      "nickname": "string",
      "intro": "string",
      "mbti": "ISTJ",
      "birthDate": "yyyy-MM-dd",
      "gender": "MALE",
      "visibility": "PUBLIC"
    }
    ```
- **응답 (Response)**
  - **Success (201 Created)**:
    ```json
    {
      "id": 1,
      "loginId": "string",
      "email": "user@example.com",
      "role": "MEMBER",
      "status": "ACTIVE",
      "createdAt": "yyyy-MM-dd'T'HH:mm:ss"
    }
    ```

---

## 3. 회원 상세 정보 (Member Detail)

### `GET /api/v1/member-detail/{memberId}`

- **역할**: 특정 회원의 상세 프로필 정보를 조회합니다.
- **응답 (Response)**
  - **Success (200 OK)**:
    ```json
    {
      "memberId": 1,
      "nickname": "string",
      "intro": "string",
      "mbti": "ISTJ",
      "birthDate": "yyyy-MM-dd",
      "gender": "MALE",
      "visibility": "PUBLIC",
      "profileImageUrl": "string (URL)",
      "backgroundImageUrl": "string (URL)"
    }
    ```

### `PUT /api/v1/member-detail/{memberId}`

- **역할**: 특정 회원의 상세 프로필 정보를 수정합니다.
- **요청 (Request)**
  - **Body**:
    ```json
    {
      "nickname": "string",
      "intro": "string",
      "mbti": "ISTJ",
      "birthDate": "yyyy-MM-dd",
      "gender": "MALE",
      "visibility": "PUBLIC"
    }
    ```
- **응답 (Response)**
  - **Success (200 OK)**: 수정된 `회원 상세 정보 객체` (구조는 GET과 동일)

---

## 4. 문화 행사 (Event)

### `GET /api/v1/events`

- **역할**: 전체 문화 행사 목록을 조회합니다.
- **응답 (Response)**
  - **Success (200 OK)**:
    ```json
    [
      {
        "id": 1,
        "eventType": "CONCERT",
        "title": "string",
        "region": { "country": "대한민국", "city": "서울특별시", "district": "강남구" },
        "eventLocation": "string",
        "startDate": "yyyy-MM-dd",
        "endDate": "yyyy-MM-dd",
        "mainImageUrl": "string (URL)",
        "viewCount": 100,
        "interestCount": 10
      }
    ]
    ```

### `GET /api/v1/events/{id}`

- **역할**: 특정 문화 행사의 상세 정보를 조회합니다.
- **응답 (Response)**
  - **Success (200 OK)**:
    ```json
    {
      "id": 1,
      "eventType": "CONCERT",
      "title": "string",
      "content": "string",
      "region": { "country": "대한민국", "city": "서울특별시", "district": "강남구" },
      "eventLocation": "string",
      "startDate": "yyyy-MM-dd",
      "endDate": "yyyy-MM-dd",
      "mainImageUrl": "string (URL)",
      "contentImageUrls": ["string (URL)"],
      "ticketPrices": [
        { "seatGrade": "R", "price": 150000 }
      ],
      "viewCount": 100,
      "interestCount": 10,
      "createdAt": "yyyy-MM-dd'T'HH:mm:ss"
    }
    ```

### `POST /api/v1/events`

- **역할**: 새로운 문화 행사를 등록합니다.
- **요청 (Request)**
  - **Form Data**: `eventRequestDto` (JSON), `mainImage` (File), `imagesToAdd` (List<File>)
    ```json
    // eventRequestDto
    {
      "eventType": "CONCERT",
      "title": "string",
      "content": "string",
      "region": { "country": "대한민국", "city": "서울특별시", "district": "강남구" },
      "eventLocation": "string",
      "startDate": "yyyy-MM-dd",
      "endDate": "yyyy-MM-dd",
      "ticketPrices": [
        { "seatGrade": "R", "price": 150000 }
      ]
    }
    ```
- **응답 (Response)**
  - **Success (201 Created)**: 생성된 `행사 상세 정보 객체` (구조는 GET 상세 조회와 동일)

---

## 5. 행사 리뷰 (Event Review)

### `GET /api/v1/event-reviews`
- **역할**: 특정 행사에 달린 모든 리뷰를 조회합니다.
- **응답 (Response)**
  - **Success (200 OK)**:
    ```json
    [
      {
        "id": 1,
        "eventId": 1,
        "author": { "id": 1, "nickname": "string", "profileImageUrl": "string (URL)" },
        "rating": 5,
        "content": "string",
        "createdAt": "yyyy-MM-dd'T'HH:mm:ss"
      }
    ]
    ```

### `POST /api/v1/event-reviews`
- **역할**: 특정 행사에 리뷰를 작성합니다.
- **요청 (Request)**
  - **Body**:
    ```json
    {
      "eventId": 1,
      "memberId": 1,
      "rating": 5,
      "content": "string"
    }
    ```
- **응답 (Response)**
  - **Success (201 Created)**: 생성된 `리뷰 정보 객체` (구조는 GET과 동일)

---

## 6. 게시판 (Board)

### `GET /api/v1/board`

- **역할**: 전체 게시글 목록을 조회합니다.
- **응답 (Response)**
  - **Success (200 OK)**:
    ```json
    [
      {
        "id": 1,
        "author": { "id": 1, "nickname": "string", "profileImageUrl": "string (URL)" },
        "title": "string",
        "content": "string",
        "category": "FREE",
        "viewCount": 100,
        "likeCount": 10,
        "commentCount": 5,
        "createdAt": "yyyy-MM-dd"
      }
    ]
    ```

### `POST /api/v1/board`

- **역할**: 새로운 게시글을 작성합니다.
- **요청 (Request)**
  - **Body**:
    ```json
    {
      "authorId": 1,
      "title": "string",
      "content": "string",
      "category": "FREE"
    }
    ```
- **응답 (Response)**
  - **Success (200 OK)**: 생성된 `게시글 정보 객체` (구조는 GET과 동일)

---

## 7. 댓글 (Comment)

### `GET /api/v1/board/{boardId}/comments`

- **역할**: 특정 게시글의 부모 댓글 목록을 조회합니다.
- **응답 (Response)**
  - **Success (200 OK)**:
    ```json
    [
      {
        "id": 1,
        "boardId": 1,
        "author": { "id": 1, "nickname": "string", "profileImageUrl": "string (URL)" },
        "content": "string",
        "createdAt": "yyyy-MM-dd",
        "likeCount": 5,
        "replyCount": 2
      }
    ]
    ```

### `POST /api/v1/board/{boardId}/comments`

- **역할**: 특정 게시글에 댓글 또는 대댓글을 작성합니다.
- **요청 (Request)**
  - **Body**:
    ```json
    {
      "authorId": 1,
      "comment": "string",
      "parentId": null
    }
    ```
- **응답 (Response)**
  - **Success (201 Created)**: 생성된 `댓글 정보 객체` (구조는 GET과 동일)

---

## 8. 같이해요 (Together)

### `GET /api/v1/together`
- **역할**: 전체 '같이해요' 모임 목록을 조회합니다.
- **응답 (Response)**
  - **Success (200 OK)**:
    ```json
    [
      {
        "id": 1,
        "event": { "id": 1, "title": "string", "mainImageUrl": "string (URL)" },
        "host": { "id": 1, "nickname": "string", "profileImageUrl": "string (URL)" },
        "title": "string",
        "content": "string",
        "region": { "country": "대한민국", "city": "서울특별시", "district": "강남구" },
        "meetingDate": "yyyy-MM-dd",
        "maxParticipants": 10,
        "currentParticipants": 5,
        "active": true
      }
    ]
    ```

### `POST /api/v1/together`
- **역할**: 새로운 '같이해요' 모임을 생성합니다.
- **요청 (Request)**
  - **Body**:
    ```json
    {
      "eventId": 1,
      "hostId": 1,
      "title": "string",
      "content": "string",
      "regionId": 101,
      "address": "string",
      "meetingDate": "yyyy-MM-dd",
      "maxParticipants": 10,
      "visible": "PUBLIC"
    }
    ```
- **응답 (Response)**
  - **Success (200 OK)**: 생성된 `모임 정보 객체` (구조는 GET과 동일)

---

## 9. 채팅 (Chat)

### `GET /api/v1/chatroom/my`
- **역할**: 현재 로그인한 사용자가 참여중인 모든 채팅방 목록을 조회합니다.
- **응답 (Response)**
  - **Success (200 OK)**:
    ```json
    [
      {
        "roomId": "uuid-string",
        "name": "string",
        "memberCount": 2
      }
    ]
    ```

### `GET /api/v1/chatroom/{roomId}/messages`
- **역할**: 특정 채팅방의 이전 대화 내역을 불러옵니다.
- **응답 (Response)**
  - **Success (200 OK)**:
    ```json
    [
      {
        "roomId": "uuid-string",
        "senderId": 1,
        "senderNickname": "string",
        "content": "string",
        "sentAt": "yyyy-MM-dd'T'HH:mm:ss"
      }
    ]
    ```

### WebSocket: `Pub /chat.sendMessage`
- **역할**: 실시간으로 채팅 메시지를 서버에 전송합니다.
- **요청 (Payload)**:
  ```json
  {
    "roomId": "uuid-string",
    "senderId": 1,
    "content": "string"
  }
  ```
- **브로드캐스트 (Sub /topic/chatroom/{roomId})**:
  ```json
  {
    "roomId": "uuid-string",
    "senderId": 1,
    "senderNickname": "string",
    "content": "string",
    "sentAt": "yyyy-MM-dd'T'HH:mm:ss"
  }
  ```