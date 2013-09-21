TRUNCATE TABLE `kaggle_fb`.`train`;
LOAD DATA LOCAL INFILE '$KAGGLE_FB/Train.csv' INTO TABLE `kaggle_fb`.`train` FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\r\n' (`id`, `title`, `body`, `tags`);
