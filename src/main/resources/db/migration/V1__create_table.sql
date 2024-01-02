CREATE TABLE `Member` (
                          `member_id`	BIGINT AUTO_INCREMENT PRIMARY KEY,
                          `image_url`	VARCHAR(100)	NULL,
                          `name`	VARCHAR(30)	NOT NULL,
                          `phone`	VARCHAR(15)	NOT NULL,
                          `email`	VARCHAR(50)	NOT NULL,
                          `password`	VARCHAR(100)	NOT NULL,
                          `status`	CHAR(15) NOT NULL,
                          `created_at`	TIMESTAMP	NOT NULL,
                          `updated_at`	TIMESTAMP	NOT NULL
);

CREATE TABLE `Schedule` (
                            `schedule_id`	BIGINT AUTO_INCREMENT PRIMARY KEY,
                            `member_id`	BIGINT	NOT NULL,
                            `title`	TEXT	NOT NULL,
                            `contents`	TEXT	NULL,
                            `schedule_at`	TIMESTAMP	NOT NULL,
                            `location_explanation`	VARCHAR(100)	NOT NULL,
                            `latitude`	DOUBLE	NOT NULL,
                            `longitude`	DOUBLE	NOT NULL,
                            `status`	CHAR(15)	NOT NULL,
                            `remind_at`	TIMESTAMP	NOT NULL,
                            `created_at`	TIMESTAMP	NOT NULL,
                            `updated_at`	TIMESTAMP	NOT NULL
);

CREATE TABLE `Friend` (
                          `id`	BIGINT AUTO_INCREMENT  PRIMARY KEY,
                          `member_id`	BIGINT	NOT NULL,
                          `member_req_id`	BIGINT	NOT NULL,
                          `status`	CHAR(15)	NOT NULL,
                          `created_at`	TIMESTAMP	NOT NULL,
                          `updated_at`	TIMESTAMP	NOT NULL
);

CREATE TABLE `ScheduleFriend` (
                                  `schedule_friend_id`	BIGINT AUTO_INCREMENT  PRIMARY KEY,
                                  `schedule_id`	BIGINT	NOT NULL,
                                  `member_id`	BIGINT	NOT NULL
);

CREATE TABLE `ChatRoom` (
                            `chat_room_id`	BIGINT AUTO_INCREMENT  PRIMARY KEY,
                            `schedule_id`	BIGINT	NOT NULL,
                            `member_id`	BIGINT	NOT NULL,
                            `status`	CHAR(15)	NOT NULL,
                            `created_at`	TIMESTAMP	NOT NULL,
                            `updated_at`	TIMESTAMP	NOT NULL
);

CREATE TABLE `Notice` (
                          `notice_id`	BIGINT AUTO_INCREMENT  PRIMARY KEY,
                          `member_id`	BIGINT	NOT NULL,
                          `type`	CHAR(15)	NOT NULL,
                          `information_id`	BIGINT	NULL,
                          `is_read`	BOOLEAN	NOT NULL,
                          `created_at`	TIMESTAMP	NOT NULL,
                          `updated_at`	TIMESTAMP	NOT NULL
);

CREATE TABLE `Chat` (
                        `chat_room_message_id`	BIGINT AUTO_INCREMENT  PRIMARY KEY,
                        `chat_room_id`	BIGINT	NOT NULL,
                        `member_id`	BIGINT	NOT NULL,
                        `created_at`	TIMESTAMP	NOT NULL,
                        `updated_at`	TIMESTAMP	NOT NULL,
                        `content`	VARCHAR(300)	NOT NULL
);


ALTER TABLE `Schedule` ADD CONSTRAINT `FK_Member_TO_Schedule_1` FOREIGN KEY (
                                                                             `member_id`
    )
    REFERENCES `Member` (
                         `member_id`
        );

ALTER TABLE `Friend` ADD CONSTRAINT `FK_Member_TO_Friend_1` FOREIGN KEY (
                                                                         `member_id`
    )
    REFERENCES `Member` (
                         `member_id`
        );

ALTER TABLE `ScheduleFriend` ADD CONSTRAINT `FK_Schedule_TO_ScheduleFriend_1` FOREIGN KEY (
                                                                                           `schedule_id`
    )
    REFERENCES `Schedule` (
                           `schedule_id`
        );

ALTER TABLE `ScheduleFriend` ADD CONSTRAINT `FK_Member_TO_ScheduleFriend_1` FOREIGN KEY (
                                                                                         `member_id`
    )
    REFERENCES `Member` (
                         `member_id`
        );

ALTER TABLE `ChatRoom` ADD CONSTRAINT `FK_Schedule_TO_ChatRoom_1` FOREIGN KEY (
                                                                               `schedule_id`
    )
    REFERENCES `Schedule` (
                           `schedule_id`
        );

ALTER TABLE `ChatRoom` ADD CONSTRAINT `FK_Member_TO_ChatRoom_1` FOREIGN KEY (
                                                                             `member_id`
    )
    REFERENCES `Member` (
                         `member_id`
        );

ALTER TABLE `Notice` ADD CONSTRAINT `FK_Member_TO_Notice_1` FOREIGN KEY (
                                                                         `member_id`
    )
    REFERENCES `Member` (
                         `member_id`
        );

ALTER TABLE `Chat` ADD CONSTRAINT `FK_ChatRoom_TO_Chat_1` FOREIGN KEY (
                                                                       `chat_room_id`
    )
    REFERENCES `ChatRoom` (
                           `chat_room_id`
        );

ALTER TABLE `Chat` ADD CONSTRAINT `FK_Member_TO_Chat_1` FOREIGN KEY (
                                                                     `member_id`
    )
    REFERENCES `Member` (
                         `member_id`
        );

