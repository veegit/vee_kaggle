TRUNCATE TABLE `kaggle_fb`.`train`;
LOAD DATA LOCAL INFILE '/home/narayanan/workspace/data/kaggle-fb/Train.csv' INTO TABLE `kaggle_fb`.`train` FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '"' LINES TERMINATED BY '\r\n' IGNORE 1 LINES (`id`, `title`, `body`, `tags`);
