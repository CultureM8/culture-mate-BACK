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
  - **Error (401 Unauthorized)**: 아이디 또는 비밀번호가 일치하지 않을 경우 반환됩니다.

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

### `GET /api/v1/member`

- **역할**: 쿼리 파라미터에 따라 특정 조건의 회원을 조회합니다. 파라미터가 없으면 전체 회원을 조회합니다.
- **요청 (Request)**
  - **Query Parameters**:
    - `id` (Long, optional): 회원 ID로 조회
    - `loginId` (String, optional): 로그인 ID로 조회
    - `status` (String, optional): `ACTIVE`, `DORMANT`, `SUSPENDED`, `DELETED` 등 상태로 조회
- **응답 (Response)**
  - **Success (200 OK)**: 단일 조회 시 `회원 정보 객체`, 목록 조회 시 `List<회원 정보 객체>`
    ```json
    // 단일 조회 예시
    {
      "id": 1,
      "loginId": "string",
      "email": "user@example.com",
      "role": "MEMBER",
      "status": "ACTIVE",
      "createdAt": "yyyy-MM-dd'T'HH:mm:ss"
    }
    ```

### `DELETE /api/v1/member/{memberId}`
- **역할**: 특정 회원을 탈퇴 처리합니다.
- **요청 (Request)**
  - **Path Parameter**: `memberId` (Long)
- **응답 (Response)**
  - **Success (204 No Content)**

---

## 3. 회원 상세 정보 (Member Detail)

### `GET /api/v1/member-detail/{memberId}`

- **역할**: 특정 회원의 상세 프로필 정보를 조회합니다.
- **요청 (Request)**
  - **Path Parameter**: `memberId` (Long)
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
  - **Path Parameter**: `memberId` (Long)
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
  - **Success (200 OK)**: 수정된 `회원 상세 정보 객체`

### `PATCH /api/v1/member-detail/{memberId}/image`
- **역할**: 회원의 프로필 또는 배경 이미지를 업로드/수정합니다.
- **요청 (Request)**
  - **Path Parameter**: `memberId` (Long)
  - **Query Parameters**:
    - `image` (MultipartFile): 이미지 파일
    - `type` (String): "profile" 또는 "background"
- **응답 (Response)**
  - **Success (200 OK)**

### `POST /api/v1/member-detail/{memberId}/gallery`
- **역할**: 특정 회원의 갤러리에 여러 이미지를 업로드합니다.
- **요청 (Request)**
  - **Path Parameter**: `memberId` (Long)
  - **Query Parameter**: `images` (List<MultipartFile>)
- **응답 (Response)**
  - **Success (201 Created)**

---

## 4. 문화 행사 (Event)

### `GET /api/v1/events`

- **역할**: 전체 문화 행사 목록을 조회합니다.
- **요청 (Request)**: 없음
- **응답 (Response)**
  - **Success (200 OK)**: `List<행사 목록 정보 객체>`
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
- **요청 (Request)**
  - **Path Parameter**: `id` (Long)
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
      "contentImageUrls": ["string (URL)", "string (URL)"],
      "ticketPrices": [
        { "seatGrade": "R", "price": 150000 },
        { "seatGrade": "S", "price": 120000 }
      ],
      "viewCount": 100,
      "interestCount": 10,
      "createdAt": "yyyy-MM-dd'T'HH:mm:ss"
    }
    ```

### `POST /api/v1/events`

- **역할**: 새로운 문화 행사를 등록합니다. (multipart/form-data 형식)
- **요청 (Request)**
  - **Form Data**:
    - `eventRequestDto` (JSON Part):
      ```json
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
    - `mainImage` (File Part, optional): 메인 이미지 파일
    - `imagesToAdd` (File Part, optional): 설명 이미지 파일 목록
- **응답 (Response)**
  - **Success (201 Created)**: 생성된 `행사 상세 정보 객체`

### `POST /api/v1/events/{eventId}/interest`
- **역할**: 특정 행사에 대한 관심 등록/취소를 토글합니다.
- **요청 (Request)**
  - **Path Parameter**: `eventId` (Long)
  - **Query Parameter**: `memberId` (Long)
- **응답 (Response)**
  - **Success (200 OK)**: "관심 등록" 또는 "관심 취소" 메시지

---

## 5. 행사 리뷰 (Event Review)

### `GET /api/v1/event-reviews`
- **역할**: 특정 행사에 달린 모든 리뷰를 조회합니다.
- **요청 (Request)**
  - **Query Parameter**: `eventId` (Long)
- **응답 (Response)**
  - **Success (200 OK)**: `List<리뷰 정보 객체>`
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
  - **Success (201 Created)**: 생성된 `리뷰 정보 객체`

---

## 6. 게시판 (Board)

### `GET /api/v1/board`

- **역할**: 전체 게시글 목록을 조회합니다.
- **요청 (Request)**: 없음
- **응답 (Response)**
  - **Success (200 OK)**: `List<게시글 정보 객체>`
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
  - **Success (200 OK)**: 생성된 `게시글 정보 객체`

### `POST /api/v1/board/{boardId}/like`
- **역할**: 특정 게시글의 좋아요/좋아요 취소를 토글합니다.
- **요청 (Request)**
  - **Path Parameter**: `boardId` (Long)
  - **Query Parameter**: `memberId` (Long)
- **응답 (Response)**
  - **Success (200 OK)**: "좋아요 성공" 또는 "좋아요 취소" 메시지

---

## 7. 댓글 (Comment)

### `GET /api/v1/board/{boardId}/comments`

- **역할**: 특정 게시글의 부모 댓글 목록을 조회합니다. (대댓글 제외)
- **요청 (Request)**
  - **Path Parameter**: `boardId` (Long)
- **응답 (Response)**
  - **Success (200 OK)**: `List<댓글 정보 객체>`
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
  - **Path Parameter**: `boardId` (Long)
  - **Body**:
    ```json
    {
      "authorId": 1,
      "comment": "string",
      "parentId": null
    }
    ```
- **응답 (Response)**
  - **Success (201 Created)**: 생성된 `댓글 정보 객체`

### `GET /api/v1/board/{boardId}/comments/{parentId}/replies`
- **역할**: 특정 댓글에 달린 대댓글 목록을 조회합니다.
- **요청 (Request)**
  - **Path Parameters**: `boardId` (Long), `parentId` (Long)
- **응답 (Response)**
  - **Success (200 OK)**: `List<대댓글 정보 객체>` (구조는 일반 댓글과 동일)

---

## 8. 같이해요 (Together)

### `GET /api/v1/together`
- **역할**: 전체 '같이해요' 모임 목록을 조회합니다.
- **요청 (Request)**: 없음
- **응답 (Response)**
  - **Success (200 OK)**: `List<모임 정보 객체>`
    ```json
    [
      {
        "id": 1,
        "event": { "id": 1, "title": "string", ... },
        "host": { "id": 1, "nickname": "string", ... },
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
  - **Success (200 OK)**: 생성된 `모임 정보 객체`

### `POST /api/v1/together/{id}/apply`
- **역할**: 특정 모임에 참여를 신청합니다. (로그인 필요)
- **요청 (Request)**
  - **Path Parameter**: `id` (Long)
- **응답 (Response)**
  - **Success (200 OK)**

### `GET /api/v1/together/{togetherId}/participants`
- **역할**: 특정 모임의 참여자 목록을 조회합니다.
- **요청 (Request)**
  - **Path Parameter**: `togetherId` (Long)
  - **Query Parameter**: `status` (String, optional) - `PENDING`, `APPROVED`, `REJECTED`
- **응답 (Response)**
  - **Success (200 OK)**: `List<회원 정보 객체>`

---

## 9. 채팅 (Chat)

### `GET /api/v1/chatroom/my`
- **역할**: 현재 로그인한 사용자가 참여중인 모든 채팅방 목록을 조회합니다.
- **요청 (Request)**: 없음 (인증 쿠키/토큰 필요)
- **응답 (Response)**
  - **Success (200 OK)**: `List<채팅방 정보 객체>`
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
- **요청 (Request)**
  - **Path Parameter**: `roomId` (Long)
- **응답 (Response)**
  - **Success (200 OK)**: `List<채팅 메시지 객체>`
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
- **요청 (Payload)**
  ```json
  {
    "roomId": "uuid-string",
    "senderId": 1,
    "content": "string"
  }
  ```
- **브로드캐스트 (Sub /topic/chatroom/{roomId})**: 해당 채팅방을 구독중인 클라이언트에게 메시지가 전송됩니다.
  ```json
  {
    "roomId": "uuid-string",
    "senderId": 1,
    "senderNickname": "string",
    "content": "string",
    "sentAt": "yyyy-MM-dd'T'HH:mm:ss"
  }
  ```

---

## 10. 데이터 초기화 (개발용)

### `POST /api/v1/init/{dataType}`
- **역할**: 서버의 초기 데이터를 설정합니다. (개발 및 테스트 용도)
- **요청 (Request)**
  - **Path Parameter**: `dataType` (String) - `regions`, `admin`, `members`
  - **Query Parameter** (for `members`): `count` (int, optional, default: 20)
- **응답 (Response)**
  - **Success (200 OK)**:
    ```json
    {
      "message": "string (결과 메시지)"
    }
    ```
