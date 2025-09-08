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
  - **Success (200 OK)**: `회원 상세 정보 객체`

### `PUT /api/v1/member-detail/{memberId}`

- **역할**: 특정 회원의 상세 프로필 정보를 수정합니다.
- **요청 (Request)**
  - **Path Parameter**: `memberId` (Long)
  - **Body**: `회원 상세 정보 DTO`
- **응답 (Response)**
  - **Success (200 OK)**: 수정된 `회원 상세 정보 객체`

### `PATCH /api/v1/member-detail/{memberId}/image`
- **역할**: 회원의 프로필 또는 배경 이미지를 업로드/수정합니다.
- **요청 (Request)**
  - **Path Parameter**: `memberId` (Long)
  - **Form Data**:
    - `image` (MultipartFile): 이미지 파일
    - `type` (String): "profile" 또는 "background"
- **응답 (Response)**
  - **Success (200 OK)**

### `POST /api/v1/member-detail/{memberId}/gallery`
- **역할**: 특정 회원의 갤러리에 여러 이미지를 업로드합니다.
- **요청 (Request)**
  - **Path Parameter**: `memberId` (Long)
  - **Form Data**: `images` (List<MultipartFile>)
- **응답 (Response)**
  - **Success (201 Created)**

---

## 4. 문화 행사 (Event)

### `GET /api/v1/events`

- **역할**: 전체 문화 행사 목록을 조회합니다.
- **요청 (Request)**: 없음
- **응답 (Response)**
  - **Success (200 OK)**: `List<행사 목록 정보 객체>`

### `GET /api/v1/events/{id}`

- **역할**: 특정 문화 행사의 상세 정보를 조회합니다.
- **요청 (Request)**
  - **Path Parameter**: `id` (Long)
- **응답 (Response)**
  - **Success (200 OK)**: `행사 상세 정보 객체`

### `GET /api/v1/events/search`
- **역할**: 키워드, 지역, 기간 등으로 행사를 검색합니다.
- **요청 (Request)**
  - **Query Parameters**: `keyword`, `region`, `eventType`, `startDate`, `endDate`
- **응답 (Response)**
  - **Success (200 OK)**: `List<행사 목록 정보 객체>`

### `POST /api/v1/events`

- **역할**: 새로운 문화 행사를 등록합니다. (multipart/form-data 형식)
- **요청 (Request)**
  - **Form Data**: `eventRequestDto` (JSON), `mainImage` (File), `imagesToAdd` (List<File>)
- **응답 (Response)**
  - **Success (201 Created)**: 생성된 `행사 상세 정보 객체`

### `PUT /api/v1/events/{id}`
- **역할**: 기존 문화 행사를 수정합니다. (multipart/form-data 형식)
- **요청 (Request)**
  - **Path Parameter**: `id` (Long)
  - **Form Data**: `eventRequestDto` (JSON), `mainImage` (File), `imagesToAdd` (List<File>)
- **응답 (Response)**
  - **Success (200 OK)**: 수정된 `행사 정보 객체`

### `DELETE /api/v1/events/{id}`
- **역할**: 특정 문화 행사를 삭제합니다.
- **요청 (Request)**
  - **Path Parameter**: `id` (Long)
- **응답 (Response)**
  - **Success (204 No Content)**

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

### `POST /api/v1/event-reviews`
- **역할**: 특정 행사에 리뷰를 작성합니다.
- **요청 (Request)**
  - **Body**: `EventReviewRequestDto`
- **응답 (Response)**
  - **Success (201 Created)**: 생성된 `리뷰 정보 객체`

### `PUT /api/v1/event-reviews/{id}`
- **역할**: 리뷰를 수정합니다.
- **요청 (Request)**
  - **Path Parameter**: `id` (Long)
  - **Body**: `EventReviewRequestDto`
- **응답 (Response)**
  - **Success (200 OK)**: 수정된 `리뷰 정보 객체`

### `DELETE /api/v1/event-reviews/{id}`
- **역할**: 리뷰를 삭제합니다.
- **요청 (Request)**
  - **Path Parameter**: `id` (Long)
- **응답 (Response)**
  - **Success (204 No Content)**

---

## 6. 게시판 (Board)

### `GET /api/v1/board`

- **역할**: 전체 게시글 목록을 조회합니다.
- **요청 (Request)**: 없음
- **응답 (Response)**
  - **Success (200 OK)**: `List<게시글 정보 객체>`

### `GET /api/v1/board/{boardId}`
- **역할**: 특정 게시글을 조회합니다.
- **요청 (Request)**
  - **Path Parameter**: `boardId` (Long)
- **응답 (Response)**
  - **Success (200 OK)**: `게시글 정보 객체`

### `GET /api/v1/board/search`
- **역할**: 게시글을 검색합니다.
- **요청 (Request)**
  - **Query Parameters**: `keyword`, `author`, `category` 등
- **응답 (Response)**
  - **Success (200 OK)**: `List<게시글 정보 객체>`

### `POST /api/v1/board`

- **역할**: 새로운 게시글을 작성합니다.
- **요청 (Request)**
  - **Body**: `BoardDto.Request`
- **응답 (Response)**
  - **Success (200 OK)**: 생성된 `게시글 정보 객체`

### `PUT /api/v1/board/{boardId}`
- **역할**: 게시글을 수정합니다.
- **요청 (Request)**
  - **Path Parameter**: `boardId` (Long)
  - **Body**: `BoardDto.Request`
- **응답 (Response)**
  - **Success (200 OK)**: 수정된 `게시글 정보 객체`

### `DELETE /api/v1/board/{boardId}`
- **역할**: 게시글을 삭제합니다.
- **요청 (Request)**
  - **Path Parameter**: `boardId` (Long)
- **응답 (Response)**
  - **Success (204 No Content)**

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

- **역할**: 특정 게시글의 부모 댓글 목록을 조회합니다.
- **요청 (Request)**
  - **Path Parameter**: `boardId` (Long)
- **응답 (Response)**
  - **Success (200 OK)**: `List<댓글 정보 객체>`

### `POST /api/v1/board/{boardId}/comments`

- **역할**: 특정 게시글에 댓글 또는 대댓글을 작성합니다.
- **요청 (Request)**
  - **Path Parameter**: `boardId` (Long)
  - **Body**: `CommentRequestDto`
- **응답 (Response)**
  - **Success (201 Created)**: 생성된 `댓글 정보 객체`

### `PUT /api/v1/board/{boardId}/comments/{commentId}`
- **역할**: 댓글을 수정합니다.
- **요청 (Request)**
  - **Path Parameters**: `boardId` (Long), `commentId` (Long)
  - **Body**: `CommentRequestDto`
- **응답 (Response)**
  - **Success (200 OK)**: 수정된 `댓글 정보 객체`

### `DELETE /api/v1/board/{boardId}/comments/{commentId}`
- **역할**: 댓글을 삭제합니다.
- **요청 (Request)**
  - **Path Parameters**: `boardId` (Long), `commentId` (Long)
  - **Query Parameter**: `requesterId` (Long)
- **응답 (Response)**
  - **Success (204 No Content)**

### `GET /api/v1/board/{boardId}/comments/{parentId}/replies`
- **역할**: 특정 댓글에 달린 대댓글 목록을 조회합니다.
- **요청 (Request)**
  - **Path Parameters**: `boardId` (Long), `parentId` (Long)
- **응답 (Response)**
  - **Success (200 OK)**: `List<대댓글 정보 객체>`

### `POST /api/v1/board/{boardId}/comments/{commentId}/like`
- **역할**: 댓글의 좋아요/좋아요 취소를 토글합니다.
- **요청 (Request)**
  - **Path Parameters**: `boardId` (Long), `commentId` (Long)
  - **Query Parameter**: `memberId` (Long)
- **응답 (Response)**
  - **Success (200 OK)**: "댓글 좋아요 성공" 또는 "댓글 좋아요 취소" 메시지

---

## 8. 같이해요 (Together)

### `GET /api/v1/together`
- **역할**: 전체 '같이해요' 모임 목록을 조회합니다.
- **요청 (Request)**: 없음
- **응답 (Response)**
  - **Success (200 OK)**: `List<모임 정보 객체>`

### `GET /api/v1/together/{id}`
- **역할**: 특정 모임의 상세 정보를 조회합니다.
- **요청 (Request)**
  - **Path Parameter**: `id` (Long)
- **응답 (Response)**
  - **Success (200 OK)**: `모임 정보 객체`

### `GET /api/v1/together/search`
- **역할**: '같이해요' 모임을 검색합니다.
- **요청 (Request)**
  - **Query Parameters**: `keyword`, `region`, `date` 등
- **응답 (Response)**
  - **Success (200 OK)**: `List<모임 정보 객체>`

### `POST /api/v1/together`
- **역할**: 새로운 '같이해요' 모임을 생성합니다.
- **요청 (Request)**
  - **Body**: `TogetherDto.Request`
- **응답 (Response)**
  - **Success (200 OK)**: 생성된 `모임 정보 객체`

### `PUT /api/v1/together/{id}`
- **역할**: 모임 정보를 수정합니다.
- **요청 (Request)**
  - **Path Parameter**: `id` (Long)
  - **Body**: `TogetherDto.Request`
- **응답 (Response)**
  - **Success (200 OK)**: 수정된 `모임 정보 객체`

### `DELETE /api/v1/together/{id}`
- **역할**: 모임을 삭제합니다.
- **요청 (Request)**
  - **Path Parameter**: `id` (Long)
- **응답 (Response)**
  - **Success (204 No Content)**

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
  - **Query Parameter**: `status` (String, optional)
- **응답 (Response)**
  - **Success (200 OK)**: `List<회원 정보 객체>`

### `POST /api/v1/together/{togetherId}/participants/{participantId}/approve`
- **역할**: 모임 참여를 승인합니다. (호스트만 가능)
- **요청 (Request)**
  - **Path Parameters**: `togetherId` (Long), `participantId` (Long)
- **응답 (Response)**
  - **Success (200 OK)**

### `POST /api/v1/together/{togetherId}/participants/{participantId}/reject`
- **역할**: 모임 참여를 거절합니다. (호스트만 가능)
- **요청 (Request)**
  - **Path Parameters**: `togetherId` (Long), `participantId` (Long)
- **응답 (Response)**
  - **Success (200 OK)**

### `DELETE /api/v1/together/{togetherId}/participants/cancel`
- **역할**: 모임 참여 신청을 취소합니다. (신청자 본인)
- **요청 (Request)**
  - **Path Parameter**: `togetherId` (Long)
- **응답 (Response)**
  - **Success (200 OK)**

### `DELETE /api/v1/together/{togetherId}/participants/{participantId}`
- **역할**: 모임에서 참여자를 강제 퇴출합니다. (호스트만 가능)
- **요청 (Request)**
  - **Path Parameters**: `togetherId` (Long), `participantId` (Long)
- **응답 (Response)**
  - **Success (200 OK)**

### `PATCH /api/v1/together/{togetherId}/recruiting/{status}`
- **역할**: 모임의 모집 상태를 변경합니다. (호스트만 가능)
- **요청 (Request)**
  - **Path Parameters**: `togetherId` (Long), `status` (String: "close" 또는 "reopen")
- **응답 (Response)**
  - **Success (200 OK)**

---

## 9. 채팅 (Chat)

### `GET /api/v1/chatroom`
- **역할**: 전체 채팅방 목록을 조회합니다. (관리자용)
- **요청 (Request)**: 없음
- **응답 (Response)**
  - **Success (200 OK)**: `List<채팅방 정보 객체>`

### `GET /api/v1/chatroom/my`
- **역할**: 현재 로그인한 사용자가 참여중인 모든 채팅방 목록을 조회합니다.
- **요청 (Request)**: 없음 (인증 필요)
- **응답 (Response)**
  - **Success (200 OK)**: `List<채팅방 정보 객체>`

### `GET /api/v1/chatroom/{roomId}`
- **역할**: 특정 채팅방의 상세 정보를 조회합니다.
- **요청 (Request)**
  - **Path Parameter**: `roomId` (Long)
- **응답 (Response)**
  - **Success (200 OK)**: `채팅방 상세 정보 객체`

### `GET /api/v1/chatroom/{roomId}/messages`
- **역할**: 특정 채팅방의 이전 대화 내역을 불러옵니다.
- **요청 (Request)**
  - **Path Parameter**: `roomId` (Long)
- **응답 (Response)**
  - **Success (200 OK)**: `List<채팅 메시지 객체>`

### `POST /api/v1/chatroom/create`
- **역할**: 새로운 채팅방을 생성합니다.
- **요청 (Request)**
  - **Query Parameter**: `name` (String)
- **응답 (Response)**
  - **Success (200 OK)**: 생성된 `채팅방 정보 객체`

### `DELETE /api/v1/chatroom/{roomId}/leave`
- **역할**: 채팅방에서 나갑니다.
- **요청 (Request)**
  - **Path Parameter**: `roomId` (Long)
- **응답 (Response)**
  - **Success (204 No Content)**

### WebSocket: `Pub /chat.sendMessage`
- **역할**: 실시간으로 채팅 메시지를 서버에 전송합니다.
- **요청 (Payload)**: `ChatMessageDto`
- **브로드캐스트**: `/topic/chatroom/{roomId}`로 `ChatMessageDto` 전송

---

## 10. 데이터 초기화 (개발용)

### `POST /api/v1/init/regions`
- **역할**: 지역 데이터를 초기화합니다.
- **요청 (Request)**: 없음
- **응답 (Response)**
  - **Success (200 OK)**: `{ "message": "지역 데이터 초기화 완료" }`

### `POST /api/v1/init/admin`
- **역할**: 관리자 계정을 초기화합니다.
- **요청 (Request)**: 없음
- **응답 (Response)**
  - **Success (200 OK)**: `{ "message": "관리자 데이터 초기화 완료" }`

### `POST /api/v1/init/members`
- **역할**: 더미 회원 데이터를 생성합니다.
- **요청 (Request)**
  - **Query Parameter**: `count` (int, optional, default: 20)
- **응답 (Response)**
  - **Success (200 OK)**: `{ "message": "더미 회원 데이터 초기화 완료" }`
