TRUNCATE TABLE mv_count_tags;
INSERT INTO mv_count_tags
SELECT g.id, g.name, COUNT(*) AS COUNT
FROM tags g, train_tag_mapping m
WHERE g.id = m.tag_id
GROUP BY g.id, g.name
ORDER BY COUNT(*) DESC;

SELECT 'DONE 1 DIM' as '';

TRUNCATE TABLE mv_x2_count_tags;
INSERT INTO mv_x2_count_tags(id1,id2,count)
SELECT m1.tag_id AS id1,m2.tag_id AS id2, COUNT(*) AS COUNT
FROM tags g, train_tag_mapping m1, train_tag_mapping m2
WHERE g.id = m1.tag_id 
AND m1.train_id = m2.train_id 
AND m1.tag_id <> m2.tag_id
GROUP BY m1.tag_id,m2.tag_id
ORDER BY COUNT(*) DESC;

SELECT 'DONE 2 DIM' as '';

