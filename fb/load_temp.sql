TRUNCATE TABLE `kaggle_fb`.`temp`;
LOAD DATA LOCAL INFILE '$KAGGLE_FB/temp.csv' INTO TABLE `kaggle_fb`.`temp` FIELDS TERMINATED BY ',' ENCLOSED BY '"' LINES TERMINATED BY '\r\n' (`id`, `title`, `body`, `tags`);
