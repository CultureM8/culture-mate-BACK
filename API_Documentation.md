# CultureMate API 명세서

> **인증**: 대부분의 API는 JWT 토큰 기반 인증이 필요합니다. `Authorization: Bearer {token}` 헤더를 포함해야 합니다.

---

## 1. 인증 (Auth)

### `POST /api/v1/auth/login`

- **역할**: 사용자의 로그인 ID와 비밀번호를 받아 인증을 수행하고, 성공 시 사용자 정보를 반환합니다.
- **요청 (Request)**
  - **Content-Type**: `application/json`
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
      "loginId": "user123",
      "role": "MEMBER"
    }
    ```
  - **Error (401 Unauthorized)**:
    ```json
    {
      "status": "error",
      "message": "아이디 또는 비밀번호가 일치하지 않습니다."
    }
    ```

---

## 2. 회원 (Member)

### `POST /api/v1/member`

- **역할**: 새로운 회원을 등록(가입)합니다.
- **요청 (Request)**
  - **Content-Type**: `application/json`
  - **Body**:
    ```json
    {
      "loginId": "user123",
      "password": "password123",
      "nickname": "닉네임",
      "intro": "자기소개",
      "mbti": "ISTJ",
      "email": "user@example.com"
    }
    ```
- **응답 (Response)**
  - **Success (201 Created)**:
    ```json
    {
      "id": 1,
      "loginId": "user123",
      "role": "MEMBER",
      "status": "ACTIVE",
      "createdAt": "2024-01-01T12:00:00",
      "updatedAt": null
    }
    ```

### `GET /api/v1/member`

- **역할**: 회원 조회 (쿼리 파라미터로 다양한 조회 방식 지원)
- **쿼리 파라미터**:
  - `id` (Long): 특정 ID 회원 조회
  - `loginId` (String): 로그인 ID로 회원 조회  
  - `status` (MemberStatus): 상태별 회원 목록 조회
- **응답**: 단일 회원 또는 회원 목록

### `DELETE /api/v1/member/{memberId}`

- **역할**: 회원을 삭제합니다.
- **응답**: `204 No Content`

### `PATCH /api/v1/member/{id}/status`

- **역할**: 회원 상태를 변경합니다 (관리자용)
- **쿼리 파라미터**: `status` (ACTIVE, DORMANT, SUSPENDED, BANNED)

### `PATCH /api/v1/member/{id}/password`

- **역할**: 비밀번호를 변경합니다
- **쿼리 파라미터**: `newPassword` (String)

### `PATCH /api/v1/member/{id}/role`

- **역할**: 권한을 변경합니다 (관리자용)
- **쿼리 파라미터**: `role` (ADMIN, MEMBER)

---

## 3. 회원 상세 정보 (Member Detail)

### `GET /api/v1/member-detail/{memberId}`

- **역할**: 특정 회원의 상세 프로필 정보를 조회합니다.
- **응답 (Response)**
  - **Success (200 OK)**:
    ```json
    {
      "id": 1,
      "nickname": "닉네임",
      "profileImagePath": "/images/member/profile/20241201_abc123.jpg",
      "backgroundImagePath": "/images/member/background/20241201_def456.jpg",
      "intro": "자기소개입니다",
      "mbti": "ISTJ",
      "togetherScore": 95,
      "email": "user@example.com",
      "visibility": "PUBLIC",
      "createdAt": "2024-01-01T12:00:00",
      "updatedAt": "2024-01-15T10:30:00"
    }
    ```

### `POST /api/v1/member-detail/{memberId}`

- **역할**: 회원 상세 정보를 생성합니다.
- **인증**: 필수 (본인만 가능)
- **요청 (Request)**:
  ```json
  {
    "nickname": "닉네임",
    "intro": "자기소개",
    "mbti": "ISTJ",
    "email": "user@example.com",
    "visibility": "PUBLIC"
  }
  ```
- **응답**: `201 Created` + 생성된 상세 정보

### `PUT /api/v1/member-detail/{memberId}`

- **역할**: 특정 회원의 상세 프로필 정보를 수정합니다.
- **인증**: 필수 (본인만 가능)
- **요청 (Request)**:
  ```json
  {
    "nickname": "새닉네임",
    "intro": "수정된 자기소개",
    "mbti": "ENFP",
    "email": "newemail@example.com",
    "visibility": "PRIVATE"
  }
  ```
- **응답**: `200 OK` + 수정된 상세 정보

### `DELETE /api/v1/member-detail/{memberId}`

- **역할**: 회원 상세 정보를 삭제합니다.
- **인증**: 필수 (본인만 가능)
- **응답**: `204 No Content`

### `PATCH /api/v1/member-detail/{memberId}/image`

- **역할**: 프로필/배경 이미지를 업로드/수정합니다.
- **Content-Type**: `multipart/form-data`
- **Parameters**:
  - `image` (MultipartFile): 이미지 파일
  - `type` (String): "profile" 또는 "background"

### `DELETE /api/v1/member-detail/{memberId}/image`

- **역할**: 프로필/배경 이미지를 삭제합니다.
- **Parameters**: `type` (String): "profile" 또는 "background"

### `POST /api/v1/member-detail/{memberId}/gallery`

- **역할**: 갤러리 이미지들을 업로드합니다.
- **Content-Type**: `multipart/form-data`
- **Parameters**: `images` (List<MultipartFile>)

### `GET /api/v1/member-detail/{memberId}/gallery`

- **역할**: 갤러리 이미지 목록을 조회합니다.
- **응답**:
  ```json
  [
    "/images/member/gallery/20241201_img1.jpg",
    "/images/member/gallery/20241201_img2.jpg"
  ]
  ```

### `DELETE /api/v1/member-detail/{memberId}/gallery`

- **역할**: 특정 갤러리 이미지를 삭제합니다.
- **Parameters**: `imagePath` (String)

### `DELETE /api/v1/member-detail/{memberId}/gallery/all`

- **역할**: 모든 갤러리 이미지를 삭제합니다.

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
        "title": "2024 서울 재즈 페스티벌",
        "region": {
          "id": 101,
          "country": "대한민국",
          "city": "서울특별시",
          "district": "강남구"
        },
        "eventLocation": "올림픽공원 체조경기장",
        "startDate": "2024-05-15",
        "endDate": "2024-05-17",
        "description": "최고의 재즈 뮤지션들이 한자리에!",
        "thumbnailImagePath": "/images/event/thumbnail/20241201_jazz.jpg",
        "avgRating": 4.5,
        "reviewCount": 128,
        "interestCount": 1250
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
      "title": "2024 서울 재즈 페스티벌",
      "regionDto": {
        "id": 101,
        "country": "대한민국",
        "city": "서울특별시",
        "district": "강남구"
      },
      "eventLocation": "올림픽공원 체조경기장",
      "address": "서울특별시 송파구 올림픽로 424",
      "addressDetail": "체조경기장 1층",
      "startDate": "2024-05-15",
      "endDate": "2024-05-17",
      "durationMin": 180,
      "minAge": 8,
      "description": "최고의 재즈 뮤지션들이 한자리에 모이는 특별한 축제입니다.",
      "ticketPrices": [
        {
          "seatGrade": "VIP",
          "price": 250000
        },
        {
          "seatGrade": "R석",
          "price": 150000
        },
        {
          "seatGrade": "S석", 
          "price": 100000
        }
      ],
      "thumbnailImagePath": "/images/event/thumbnail/20241201_jazz.jpg",
      "mainImagePath": "/images/event/main/20241201_jazz_main.jpg",
      "contentImages": [
        "/images/event/content/20241201_jazz_1.jpg",
        "/images/event/content/20241201_jazz_2.jpg"
      ],
      "avgRating": 4.5,
      "reviewCount": 128,
      "interestCount": 1250,
      "createdAt": "2024-01-01T12:00:00",
      "updatedAt": "2024-01-15T10:30:00"
    }
    ```

### `GET /api/v1/events/search`

- **역할**: 이벤트를 검색합니다.
- **쿼리 파라미터**:
  - `keyword` (String): 제목 검색
  - `eventType` (EventType): 이벤트 타입
  - `region` (RegionDto): 지역 정보
- **응답**: 검색된 이벤트 목록 (GET /events와 동일한 구조)

### `POST /api/v1/events`

- **역할**: 새로운 문화 행사를 등록합니다.
- **Content-Type**: `multipart/form-data`
- **요청 (Request)**:
  - **Form Parameters**:
    - `eventRequestDto` (JSON String):
      ```json
      {
        "eventType": "CONCERT",
        "title": "2024 서울 재즈 페스티벌",
        "regionDto": {
          "country": "대한민국",
          "city": "서울특별시", 
          "district": "강남구"
        },
        "eventLocation": "올림픽공원 체조경기장",
        "address": "서울특별시 송파구 올림픽로 424",
        "addressDetail": "체조경기장 1층",
        "startDate": "2024-05-15",
        "endDate": "2024-05-17",
        "durationMin": 180,
        "minAge": 8,
        "description": "최고의 재즈 뮤지션들이 한자리에!",
        "ticketPriceDto": [
          {
            "seatGrade": "VIP",
            "price": 250000
          }
        ]
      }
      ```
    - `mainImage` (MultipartFile): 메인 이미지 파일 (선택사항)
    - `imagesToAdd` (List<MultipartFile>): 컨텐츠 이미지 파일들 (선택사항)
- **응답 (Response)**:
  - **Success (201 Created)**: 생성된 상세 정보 (GET /{id}와 동일한 구조)

### `PUT /api/v1/events/{id}`

- **역할**: 이벤트 정보를 수정합니다.
- **인증**: 필수 (작성자 또는 관리자만 가능)
- **Content-Type**: `multipart/form-data`
- **Parameters**: POST와 동일 + `imagesToDelete` (삭제할 이미지 경로들)

### `DELETE /api/v1/events/{id}`

- **역할**: 이벤트를 삭제합니다.
- **인증**: 필수 (작성자 또는 관리자만 가능)

### `POST /api/v1/events/{eventId}/interest`

- **역할**: 이벤트 관심 등록/취소를 토글합니다.
- **Parameters**: `memberId` (Long)
- **응답**: "관심 등록" 또는 "관심 취소"

### `GET /api/v1/events/{eventId}/content-images`

- **역할**: 이벤트의 컨텐츠 이미지 목록을 조회합니다.
- **응답**:
  ```json
  [
    "/images/event/content/20241201_jazz_1.jpg",
    "/images/event/content/20241201_jazz_2.jpg"
  ]
  ```

---

## 5. 행사 리뷰 (Event Review)

### `GET /api/v1/event-reviews/{eventId}`
- **역할**: 특정 행사에 달린 모든 리뷰를 조회합니다.
- **Path Parameter**: `eventId` (Long)
- **응답 (Response)**
  - **Success (200 OK)**:
    ```json
    [
      {
        "id": 1,
        "eventId": 1,
        "author": {
          "id": 1,
          "nickname": "재즈러버",
          "thumbnailImagePath": "/images/member/profile/thumb_20241201.jpg",
          "intro": "음악을 사랑하는 사람"
        },
        "rating": 5,
        "content": "정말 환상적인 공연이었습니다! 다음에도 꼭 가고 싶어요.",
        "createdAt": "2024-12-01T19:30:00",
        "updatedAt": null
      }
    ]
    ```

### `GET /api/v1/event-reviews/my`
- **역할**: 현재 사용자가 작성한 리뷰 목록을 조회합니다.
- **인증**: 필수
- **응답 (Response)**:
  ```json
  [
    {
      "id": 1,
      "event": {
        "id": 1,
        "eventType": "CONCERT",
        "title": "2024 서울 재즈 페스티벌",
        "description": "최고의 재즈 뮤지션들이 한자리에!",
        "thumbnailImagePath": "/images/event/thumbnail/20241201_jazz.jpg",
        "avgRating": 4.5,
        "reviewCount": 128,
        "interestCount": 1250
      },
      "author": {
        "id": 1,
        "nickname": "재즈러버",
        "thumbnailImagePath": "/images/member/profile/thumb_20241201.jpg",
        "intro": "음악을 사랑하는 사람"
      },
      "rating": 5,
      "content": "정말 환상적인 공연이었습니다!",
      "createdAt": "2024-12-01T19:30:00",
      "updatedAt": null
    }
  ]
  ```

### `POST /api/v1/event-reviews`
- **역할**: 특정 행사에 리뷰를 작성합니다.
- **인증**: 필수
- **요청 (Request)**
  - **Body**:
    ```json
    {
      "eventId": 1,
      "rating": 5,
      "content": "정말 환상적인 공연이었습니다! 다음에도 꼭 가고 싶어요."
    }
    ```
- **응답 (Response)**
  - **Success (201 Created)**: 생성된 리뷰 정보 (GET과 동일한 구조)

### `PUT /api/v1/event-reviews/{id}`
- **역할**: 리뷰를 수정합니다.
- **인증**: 필수 (작성자 본인만)
- **요청**: POST와 동일한 구조 (eventId 제외)
- **응답**: 수정된 리뷰 정보

### `DELETE /api/v1/event-reviews/{id}`
- **역할**: 리뷰를 삭제합니다.
- **인증**: 필수 (작성자 본인만)
- **응답**: `204 No Content`

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
        "title": "재즈 페스티벌 후기 공유해요!",
        "content": "어제 다녀온 재즈 페스티벌 정말 좋았어요. 다들 어떠셨나요?",
        "author": {
          "id": 1,
          "nickname": "재즈러버",
          "thumbnailImagePath": "/images/member/profile/thumb_20241201.jpg",
          "intro": "음악을 사랑하는 사람"
        },
        "eventCard": {
          "id": 1,
          "eventType": "CONCERT",
          "title": "2024 서울 재즈 페스티벌",
          "description": "최고의 재즈 뮤지션들이 한자리에!",
          "thumbnailImagePath": "/images/event/thumbnail/20241201_jazz.jpg",
          "avgRating": 4.5,
          "reviewCount": 128,
          "interestCount": 1250
        },
        "likeCount": 15,
        "createdAt": "2024-12-02T10:30:00",
        "updatedAt": null
      }
    ]
    ```

### `GET /api/v1/board/{boardId}`

- **역할**: 특정 게시글을 조회합니다.
- **응답**: GET /board와 동일한 단일 객체 구조

### `GET /api/v1/board/author/{memberId}`

- **역할**: 특정 작성자의 게시글 목록을 조회합니다.
- **응답**: 해당 작성자의 게시글 목록

### `GET /api/v1/board/search`

- **역할**: 게시글을 검색합니다.
- **쿼리 파라미터**:
  - `keyword` (String): 제목/내용 검색
  - `eventType` (EventType): 이벤트 타입별 필터
  - `authorId` (Long): 작성자별 필터

### `POST /api/v1/board`

- **역할**: 새로운 게시글을 작성합니다.
- **요청 (Request)**
  - **Body**:
    ```json
    {
      "title": "재즈 페스티벌 후기 공유해요!",
      "content": "어제 다녀온 재즈 페스티벌 정말 좋았어요. 다들 어떠셨나요?",
      "authorId": 1,
      "eventType": "CONCERT",
      "eventId": 1
    }
    ```
- **응답 (Response)**
  - **Success (200 OK)**: 생성된 게시글 정보 (GET과 동일한 구조)

### `PUT /api/v1/board/{boardId}`

- **역할**: 게시글을 수정합니다.
- **인증**: 필수 (작성자 본인만)
- **요청**: POST와 유사한 구조 (authorId 제외)

### `DELETE /api/v1/board/{boardId}`

- **역할**: 게시글을 삭제합니다.
- **인증**: 필수 (작성자 본인만)
- **응답**: `204 No Content`

### `POST /api/v1/board/{boardId}/like`

- **역할**: 게시글 좋아요를 토글합니다.
- **Parameters**: `memberId` (Long)
- **응답**: "좋아요 성공" 또는 "좋아요 취소"

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
        "author": {
          "id": 1,
          "nickname": "댓글러",
          "thumbnailImagePath": "/images/member/profile/thumb_20241202.jpg",
          "intro": "댓글 좋아하는 사람"
        },
        "content": "저도 그 공연 정말 좋았어요! 특히 마지막 곡이 인상깊었습니다.",
        "createdAt": "2024-12-02",
        "updatedAt": null,
        "likeCount": 5,
        "replyCount": 2
      }
    ]
    ```

### `GET /api/v1/board/{boardId}/comments/{parentId}/replies`

- **역할**: 특정 댓글의 대댓글 목록을 조회합니다.
- **응답**: 부모 댓글 조회와 동일한 구조 (단, replyCount는 항상 0)

### `POST /api/v1/board/{boardId}/comments`

- **역할**: 특정 게시글에 댓글 또는 대댓글을 작성합니다.
- **요청 (Request)**
  - **Body**:
    ```json
    {
      "boardId": 1,
      "authorId": 1,
      "comment": "저도 그 공연 정말 좋았어요! 특히 마지막 곡이 인상깊었습니다.",
      "parentId": null
    }
    ```
  - **대댓글 작성 시**: `parentId`에 부모 댓글 ID 포함
- **응답 (Response)**
  - **Success (201 Created)**: 생성된 댓글 정보 (GET과 동일한 구조)

### `PUT /api/v1/board/{boardId}/comments/{commentId}`

- **역할**: 댓글을 수정합니다.
- **Parameters**: `requesterId` (Long) - 요청자 ID
- **요청**: POST와 유사한 구조 (boardId, authorId 제외)
- **응답**: 수정된 댓글 정보

### `DELETE /api/v1/board/{boardId}/comments/{commentId}`

- **역할**: 댓글을 삭제합니다.
- **Parameters**: `requesterId` (Long) - 요청자 ID
- **응답**: `204 No Content`

### `POST /api/v1/board/{boardId}/comments/{commentId}/like`

- **역할**: 댓글 좋아요를 토글합니다.
- **Parameters**: `memberId` (Long)
- **응답**: "댓글 좋아요 성공" 또는 "댓글 좋아요 취소"

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
        "event": {
          "id": 1,
          "eventType": "CONCERT",
          "title": "2024 서울 재즈 페스티벌",
          "description": "최고의 재즈 뮤지션들이 한자리에!",
          "thumbnailImagePath": "/images/event/thumbnail/20241201_jazz.jpg",
          "avgRating": 4.5,
          "reviewCount": 128,
          "interestCount": 1250
        },
        "host": {
          "id": 1,
          "nickname": "재즈호스트",
          "thumbnailImagePath": "/images/member/profile/thumb_20241203.jpg",
          "intro": "재즈를 사랑하는 호스트"
        },
        "title": "재즈 페스티벌 같이 가실 분!",
        "content": "혼자 가기 아쉬워서 같이 가실 분을 찾습니다. 재즈 좋아하시는 분들 환영!",
        "region": {
          "id": 101,
          "country": "대한민국",
          "city": "서울특별시",
          "district": "강남구"
        },
        "address": "서울특별시 송파구 올림픽로 424",
        "addressDetail": "올림픽공원 정문 앞 집결",
        "meetingDate": "2024-05-15",
        "maxParticipants": 8,
        "currentParticipants": 3,
        "active": true,
        "createdAt": "2024-12-01T14:30:00",
        "updatedAt": null
      }
    ]
    ```

### `GET /api/v1/together/{id}`
- **역할**: 특정 모임의 상세 정보를 조회합니다.
- **응답**: GET /together와 동일한 단일 객체 구조

### `GET /api/v1/together/hosted-by/{hostId}`
- **역할**: 특정 회원이 호스트인 모임 목록을 조회합니다.
- **응답**: 해당 호스트의 모임 목록

### `GET /api/v1/together/with/{memberId}`
- **역할**: 특정 회원이 참여 중인 모임 목록을 조회합니다 (승인된 것만).
- **응답**: 해당 회원이 참여 중인 모임 목록

### `GET /api/v1/together/search`
- **역할**: 모임을 검색합니다.
- **쿼리 파라미터**:
  - `keyword` (String): 제목 검색
  - `eventType` (EventType): 이벤트 타입
  - `region` (RegionDto): 지역 정보
  - `meetingDate` (LocalDate): 모임 날짜

### `GET /api/v1/together/my-applications`
- **역할**: 내가 신청한 모임 목록을 조회합니다.
- **인증**: 필수
- **쿼리 파라미터**: `status` (PENDING, APPROVED, REJECTED) - 선택사항
- **응답**: 신청한 모임 목록

### `POST /api/v1/together`
- **역할**: 새로운 '같이해요' 모임을 생성합니다.
- **요청 (Request)**
  - **Body**:
    ```json
    {
      "eventId": 1,
      "hostId": 1,
      "title": "재즈 페스티벌 같이 가실 분!",
      "regionDto": {
        "country": "대한민국",
        "city": "서울특별시",
        "district": "강남구"
      },
      "address": "서울특별시 송파구 올림픽로 424",
      "addressDetail": "올림픽공원 정문 앞 집결",
      "meetingDate": "2024-05-15",
      "maxParticipants": 8,
      "content": "혼자 가기 아쉬워서 같이 가실 분을 찾습니다. 재즈 좋아하시는 분들 환영!"
    }
    ```
- **응답 (Response)**
  - **Success (200 OK)**: 생성된 모임 정보 (GET과 동일한 구조)

### `PUT /api/v1/together/{id}`
- **역할**: 모임 정보를 수정합니다.
- **인증**: 필수 (호스트만 가능)
- **요청**: POST와 유사한 구조 (hostId 제외)
- **응답**: 수정된 모임 정보

### `DELETE /api/v1/together/{id}`
- **역할**: 모임을 삭제합니다.
- **인증**: 필수 (호스트만 가능)
- **응답**: `204 No Content`

### `POST /api/v1/together/{id}/apply`
- **역할**: 모임에 참여 신청을 합니다.
- **인증**: 필수
- **응답**: `200 OK`

### `GET /api/v1/together/{togetherId}/participants`
- **역할**: 모임의 참여자 목록을 조회합니다.
- **쿼리 파라미터**: `status` (PENDING, APPROVED, REJECTED) - 선택사항
- **응답**:
  ```json
  [
    {
      "id": 2,
      "loginId": "member2",
      "role": "MEMBER",
      "status": "ACTIVE",
      "createdAt": "2024-01-02T12:00:00",
      "updatedAt": null
    }
  ]
  ```

### `POST /api/v1/together/{togetherId}/participants/{participantId}/approve`
- **역할**: 참여 신청을 승인합니다.
- **인증**: 필수 (호스트만 가능)
- **응답**: `200 OK`

### `POST /api/v1/together/{togetherId}/participants/{participantId}/reject`
- **역할**: 참여 신청을 거절합니다.
- **인증**: 필수 (호스트만 가능)
- **응답**: `200 OK`

### `DELETE /api/v1/together/{togetherId}/participants/cancel`
- **역할**: 내 참여를 취소합니다.
- **인증**: 필수 (본인만 가능)
- **응답**: `200 OK`

### `DELETE /api/v1/together/{togetherId}/participants/{participantId}`
- **역할**: 참여자를 강제 퇴출합니다.
- **인증**: 필수 (호스트만 가능)
- **응답**: `200 OK`

### `PATCH /api/v1/together/{togetherId}/recruiting/{status}`
- **역할**: 모집 상태를 변경합니다.
- **인증**: 필수 (호스트만 가능)
- **Path Parameter**: `status` - "close" 또는 "reopen"
- **응답**: `200 OK`

---

## 9. 채팅 (Chat & ChatRoom)

### `GET /api/v1/chatroom`
- **역할**: 모든 채팅방 목록을 조회합니다 (관리자용).
- **응답 (Response)**:
  ```json
  [
    {
      "id": 1,
      "roomName": "재즈 페스티벌 채팅방",
      "chatMemberCount": 5,
      "createdAt": "2024-12-01T14:30:00"
    }
  ]
  ```

### `POST /api/v1/chatroom/create`
- **역할**: 새로운 채팅방을 생성합니다.
- **Parameters**: `name` (String) - 채팅방 이름
- **응답**: 생성된 채팅방 정보

### `GET /api/v1/chatroom/my`
- **역할**: 현재 로그인한 사용자가 참여 중인 모든 채팅방 목록을 조회합니다.
- **인증**: 필수
- **응답 (Response)**
  - **Success (200 OK)**:
    ```json
    [
      {
        "id": 1,
        "roomName": "재즈 페스티벌 채팅방",
        "chatMemberCount": 5,
        "createdAt": "2024-12-01T14:30:00"
      }
    ]
    ```

### `GET /api/v1/chatroom/{roomId}`
- **역할**: 특정 채팅방의 상세 정보를 조회합니다.
- **응답**:
  ```json
  {
    "id": 1,
    "roomName": "재즈 페스티벌 채팅방",
    "chatMemberCount": 5,
    "participants": [
      {
        "id": 1,
        "nickname": "재즈러버",
        "thumbnailImagePath": "/images/member/profile/thumb_20241201.jpg",
        "intro": "음악을 사랑하는 사람"
      }
    ],
    "createdAt": "2024-12-01T14:30:00"
  }
  ```

### `POST /api/v1/chatroom/{roomId}/join`
- **역할**: 채팅방에 참가합니다.
- **인증**: 필수
- **응답**: `204 No Content`

### `DELETE /api/v1/chatroom/{roomId}/leave`
- **역할**: 채팅방을 나갑니다.
- **인증**: 필수
- **응답**: `204 No Content`

### `GET /api/v1/chatroom/{roomId}/messages`
- **역할**: 특정 채팅방의 이전 대화 내역을 불러옵니다.
- **응답 (Response)**
  - **Success (200 OK)**:
    ```json
    [
      {
        "id": 1,
        "roomId": 1,
        "senderId": 1,
        "content": "안녕하세요! 재즈 페스티벌 정말 기대됩니다.",
        "createdAt": "2024-12-01T15:30:00"
      }
    ]
    ```

### WebSocket: `Pub /chat.sendMessage`
- **역할**: 실시간으로 채팅 메시지를 서버에 전송합니다.
- **Connection**: WebSocket 연결 필요
- **요청 (Payload)**:
  ```json
  {
    "roomId": 1,
    "senderId": 1,
    "content": "안녕하세요! 재즈 페스티벌 정말 기대됩니다."
  }
  ```
- **브로드캐스트 (Sub /topic/chatroom/{roomId})**:
  ```json
  {
    "id": 1,
    "roomId": 1,
    "senderId": 1,
    "content": "안녕하세요! 재즈 페스티벌 정말 기대됩니다.",
    "createdAt": "2024-12-01T15:30:00"
  }
  ```

---

## 10. 데이터 타입 정의

### EventType (Enum)
- `CONCERT`: 콘서트
- `EXHIBITION`: 전시회
- `PLAY`: 연극
- `MUSICAL`: 뮤지컬
- `FESTIVAL`: 페스티벌
- `CLASSIC`: 클래식

### MemberStatus (Enum)  
- `ACTIVE`: 활성
- `DORMANT`: 휴면
- `SUSPENDED`: 정지
- `BANNED`: 차단

### Role (Enum)
- `ADMIN`: 관리자
- `MEMBER`: 일반 회원

### VisibleType (Enum)
- `PUBLIC`: 공개
- `PRIVATE`: 비공개

### ParticipationStatus (Enum)
- `PENDING`: 승인 대기
- `APPROVED`: 승인됨
- `REJECTED`: 거절됨

---

## 11. 공통 응답 구조

### 성공 응답
- **200 OK**: 조회/수정 성공
- **201 Created**: 생성 성공  
- **204 No Content**: 삭제/작업 완료

### 오류 응답
```json
{
  "status": "error",
  "message": "구체적인 오류 메시지",
  "timestamp": "2024-12-01T15:30:00",
  "path": "/api/v1/endpoint"
}
```

### HTTP 상태 코드
- **400 Bad Request**: 잘못된 요청
- **401 Unauthorized**: 인증 실패
- **403 Forbidden**: 권한 없음
- **404 Not Found**: 리소스 없음
- **409 Conflict**: 리소스 충돌
- **500 Internal Server Error**: 서버 오류