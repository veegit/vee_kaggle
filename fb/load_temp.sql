TRUNCATE TABLE `kaggle_fb`.`temp`;
LOAD DATA LOCAL INFILE '/home/narayanan/workspace/data/kaggle-fb/temp.csv' INTO TABLE `kaggle_fb`.`temp` FIELDS TERMINATED BY ',' ENCLOSED BY '"' LINES TERMINATED BY '\r\n' IGNORE 1 LINES (`id`, `title`, `body`, `tags`);
