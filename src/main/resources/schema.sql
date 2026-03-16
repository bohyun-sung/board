-- 외래 키 체크 일시 중지 (생성 순서 꼬임 방지)
SET
FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `uploads`;
DROP TABLE IF EXISTS `comments`;
DROP TABLE IF EXISTS `post`;
DROP TABLE IF EXISTS `member`;
DROP TABLE IF EXISTS `admin`;

-- 1. 관리자 테이블
CREATE TABLE `admin`
(
    `admin_idx` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'idx',
    `name`      varchar(20)  NOT NULL COMMENT '이름',
    `user_id`   varchar(100) NOT NULL COMMENT 'id',
    `password`  varchar(100) NOT NULL COMMENT '비밀번호',
    `email`     varchar(100) NOT NULL COMMENT '이메일',
    `phone`     varchar(20)  NOT NULL COMMENT '핸드폰',
    `rgdt`      timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성시간',
    `updt`      timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정시간',
    `role_type` tinyint(4) NOT NULL COMMENT '0: 관리자, 1: 유저',
    PRIMARY KEY (`admin_idx`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. 멤버 테이블
CREATE TABLE `member`
(
    `member_idx` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `email`      varchar(100) NOT NULL COMMENT '이메일',
    `nickname`   varchar(20)  NOT NULL COMMENT '닉네임(이름)',
    `phone`      varchar(100)          DEFAULT NULL COMMENT '전화번호',
    `password`   varchar(100)          DEFAULT NULL COMMENT '비밀번호',
    `role_type`  tinyint(3) unsigned NOT NULL COMMENT '0:관리자 1:이용자',
    `rgdt`       timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성시간',
    `updt`       timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정시간',
    `provider`   tinyint(4) NOT NULL COMMENT '0:로컬 1:구글',
    PRIMARY KEY (`member_idx`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. 게시글 테이블
CREATE TABLE `post`
(
    `post_idx`   bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `title`      varchar(100) NOT NULL COMMENT '제목',
    `content`    text         NOT NULL COMMENT '본문',
    `view_count` int(10) unsigned NOT NULL DEFAULT 0 COMMENT '조회수',
    `board_type` tinyint(4) NOT NULL COMMENT '0: 공지, 1: 이벤트, 2: 뉴스, 3: 자유게시판',
    `role_type`  tinyint(4) NOT NULL COMMENT '0: 관리자, 1: 이용자',
    `admin_idx`  bigint(20) unsigned DEFAULT NULL COMMENT '관리자 idx',
    `rgdt`       timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성시간',
    `updt`       timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정시간',
    `member_idx` bigint(20) unsigned DEFAULT NULL COMMENT '멤버 idx',
    PRIMARY KEY (`post_idx`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. 댓글 테이블
CREATE TABLE `comments`
(
    `comment_idx`        bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `content`            text         NOT NULL COMMENT '댓글 내용',
    `parent_comment_idx` bigint(20) unsigned DEFAULT NULL COMMENT '부모 댓글 root는 null',
    `member_idx`         bigint(20) unsigned NOT NULL COMMENT '작성자 멤버 idx',
    `is_deleted`         tinyint(1) NOT NULL DEFAULT 0 COMMENT '0: 유지 1: 삭제',
    `post_idx`           bigint(20) unsigned NOT NULL COMMENT '게시물 idx',
    `root_comment_idx`   bigint(20) unsigned DEFAULT NULL COMMENT '최상위 부모 idx',
    `path`               varchar(255) NOT NULL COMMENT '계층 경로',
    `rgdt`               timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성시간',
    `updt`               timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정시간',
    PRIMARY KEY (`comment_idx`),
    KEY                  `comments_FK_1` (`member_idx`),
    KEY                  `comments_FK_2` (`parent_comment_idx`),
    KEY                  `idx_comments_hierarchy` (`post_idx`,`root_comment_idx`,`path`),
    CONSTRAINT `comments_FK` FOREIGN KEY (`post_idx`) REFERENCES `post` (`post_idx`) ON DELETE CASCADE,
    CONSTRAINT `comments_FK_1` FOREIGN KEY (`member_idx`) REFERENCES `member` (`member_idx`),
    CONSTRAINT `comments_FK_2` FOREIGN KEY (`parent_comment_idx`) REFERENCES `comments` (`comment_idx`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. 업로드 테이블
CREATE TABLE `uploads`
(
    `upload_idx`         bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `upload_url`         varchar(200) NOT NULL COMMENT 's3_url',
    `thumbnail_url`      varchar(200)          DEFAULT NULL COMMENT 's3_썸네일_url',
    `upload_type`        smallint(5) unsigned NOT NULL COMMENT '0:게시판',
    `upload_mapping_idx` bigint(20) unsigned DEFAULT NULL COMMENT '업로드 매핑 idx',
    `file_size`          int(10) unsigned NOT NULL COMMENT '파일사이즈',
    `extension`          varchar(10)  NOT NULL COMMENT '확장자',
    `rgdt`               timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성시간',
    `updt`               timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정시간',
    `sort_order`         smallint(5) unsigned NOT NULL COMMENT '업로드순서',
    PRIMARY KEY (`upload_idx`),
    KEY                  `uploads_upload_type_IDX` (`upload_type`,`upload_mapping_idx`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 외래 키 체크 다시 활성화
SET
FOREIGN_KEY_CHECKS = 1;