-- Create not entity tables
CREATE TABLE `news_type` (
  `id` varchar(100) NOT NULL,
  `nt_name` varchar(255) NOT NULL,
  `nt_description` varchar(255) NOT NULL,
  `del_flg` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
);

CREATE TABLE `notification_medium_type` (
  `id` varchar(50) NOT NULL,
  `mt_description` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
);

-- Alter tables to add columns not included in entities
ALTER TABLE symbol add del_flg tinyint(1) NOT NULL DEFAULT 0;


-- Insert initial data
INSERT INTO news_type (id, nt_name, nt_description, del_flg) VALUES('DIVDEC', 'Dividends Declaration', 'Company declaration of dividends payment', 0);
INSERT INTO symbol (id, company_name, del_flg) VALUES('SVL', 'SUPREME VENTURES LIMITED', 0);
INSERT INTO notification_medium_type (id, mt_description) VALUES('TELEGRAM', 'Telegram app user chat');
INSERT INTO `user` (id, username) VALUES('1','shadow');
INSERT INTO `user` (id, username) VALUES('3','shadow 3');

INSERT INTO notification_medium (medium_id, `user`, medium_type) VALUES ('927362871', 1, 'TELEGRAM');
INSERT INTO notification_subscription (symbol, notif_type, medium_id) VALUES ('SVL', 'DIVDEC', '927362871');