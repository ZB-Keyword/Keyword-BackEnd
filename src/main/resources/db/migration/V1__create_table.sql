-- Member Table
CREATE TABLE Member (
                        `member_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
                        `image_url` VARCHAR(100) NULL,
                        `name` VARCHAR(30) NOT NULL,
                        `phone` VARCHAR(15) NOT NULL,
                        `email` VARCHAR(50) NOT NULL,
                        `password` VARCHAR(100) NOT NULL,
                        `status` CHAR(15) NOT NULL,
                        `created_at` TIMESTAMP NOT NULL,
                        `updated_at` TIMESTAMP NOT NULL
);
-- Schedule Table
CREATE TABLE Schedule (
                          `schedule_id` BIGINT AUTO_INCREMENT NOT NULL,
                          `member_id` BIGINT NOT NULL,
                          `title` TEXT NOT NULL,
                          `contents` TEXT NULL,
                          `schedule_at` TIMESTAMP NOT NULL,
                          `location_explanation` VARCHAR(100) NOT NULL,
                          `latitude` DOUBLE NOT NULL,
                          `longitude` DOUBLE NOT NULL,
                          `status` CHAR(15) NOT NULL,
                          `remind_at` TIMESTAMP NOT NULL,
                          `created_at` TIMESTAMP NOT NULL,
                          `updated_at` TIMESTAMP NOT NULL,
                          PRIMARY KEY (`schedule_id`, `member_id`),
                          CONSTRAINT `FK_Member_TO_Schedule_1` FOREIGN KEY (`member_id`) REFERENCES `Member` (`member_id`)
);
-- Friend Table
CREATE TABLE Friend (
                        `id` BIGINT AUTO_INCREMENT NOT NULL,
                        `member_id` BIGINT NOT NULL,
                        `member_req_id` BIGINT NOT NULL,
                        `status` CHAR(15) NOT NULL,
                        `created_at` TIMESTAMP NOT NULL,
                        `updated_at` TIMESTAMP NOT NULL,
                        PRIMARY KEY (`id`, `member_id`),
                        CONSTRAINT `FK_Member_TO_Friend_1` FOREIGN KEY (`member_id`) REFERENCES `Member` (`member_id`)
);
-- ScheduleFriend Table
CREATE TABLE ScheduleFriend (
                                `schedule_friend_id` BIGINT AUTO_INCREMENT NOT NULL,
                                `schedule_id` BIGINT NOT NULL,
                                `member_id` BIGINT NOT NULL,
                                PRIMARY KEY (`schedule_friend_id`, `schedule_id`, `member_id`),
                                CONSTRAINT `FK_Schedule_TO_ScheduleFriend_1` FOREIGN KEY (`schedule_id`) REFERENCES `Schedule` (`schedule_id`),
                                CONSTRAINT `FK_Member_TO_ScheduleFriend_1` FOREIGN KEY (`member_id`) REFERENCES `Member` (`member_id`)
);
-- ChatRoom Table
CREATE TABLE ChatRoom (
                          `chat_room_id` BIGINT AUTO_INCREMENT NOT NULL,
                          `schedule_id` BIGINT NOT NULL,
                          `member_id` BIGINT NOT NULL,
                          `status` CHAR(15) NOT NULL,
                          `created_at` TIMESTAMP NOT NULL,
                          `updated_at` TIMESTAMP NOT NULL,
                          PRIMARY KEY (`chat_room_id`, `schedule_id`, `member_id`),
                          CONSTRAINT `FK_Schedule_TO_ChatRoom_1` FOREIGN KEY (`schedule_id`) REFERENCES `Schedule` (`schedule_id`),
                          CONSTRAINT `FK_Member_TO_ChatRoom_1` FOREIGN KEY (`member_id`) REFERENCES `Member` (`member_id`)
);
-- Notice Table
CREATE TABLE Notice (
                        `notice_id` BIGINT AUTO_INCREMENT NOT NULL,
                        `member_id` BIGINT NOT NULL,
                        `type` CHAR(15) NOT NULL,
                        `information_id` BIGINT NULL,
                        `is_read` BOOLEAN NOT NULL,
                        `created_at` TIMESTAMP NOT NULL,
                        `updated_at` TIMESTAMP NOT NULL,
                        PRIMARY KEY (`notice_id`, `member_id`),
                        CONSTRAINT `FK_Member_TO_Notice_1` FOREIGN KEY (`member_id`) REFERENCES `Member` (`member_id`)
);
-- Chat Table
CREATE TABLE Chat (
                      `chat_room_message_id` BIGINT AUTO_INCREMENT NOT NULL,
                      `chat_room_id` BIGINT NOT NULL,
                      `member_id` BIGINT NOT NULL,
                      `created_at` TIMESTAMP NOT NULL,
                      `updated_at` TIMESTAMP NOT NULL,
                      `content` VARCHAR(255) NOT NULL,
                      PRIMARY KEY (`chat_room_message_id`, `chat_room_id`, `member_id`),
                      CONSTRAINT `FK_ChatRoom_TO_Chat_1` FOREIGN KEY (`chat_room_id`) REFERENCES `ChatRoom` (`chat_room_id`),
                      CONSTRAINT `FK_Member_TO_Chat_1` FOREIGN KEY (`member_id`) REFERENCES `Member` (`member_id`)
);
