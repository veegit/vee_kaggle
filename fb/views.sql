insert into mv_count_tags
select g.id, g.name, count(*) as count  from tags g, train_tag_mapping m where g.id = m.tag_id group by g.id, g.name order by count(*) desc;


insert into mv_x2_count_tags
SELECT m.tag_id as id1,m2.tag_id as id2,count(*) as count
FROM tags g, train_tag_mapping m, train_tag_mapping m2
WHERE g.id = m.tag_id 
AND m2.train_id = m.train_id
AND m2.tag_id <> m.tag_id
group by m.tag_id,m2.tag_id
order by count(*) desc
