#!/usr/bin/python

import MySQLdb
import os

HOST = "localhost"
USER = "root"
PASSWORD = "password"
DATABASE = "kaggle_fb_t1"

db = MySQLdb.connect(HOST,USER,PASSWORD,DATABASE)
cursor = db.cursor()

def extract():
	tags = set()
	sql = "SELECT tags from train"
	tags_string = ""
	try:
		cursor.execute(sql)
		results = cursor.fetchall()
		for row in results:
			tags_string = row[0].lower()
			if tags_string is None:
				continue
			tags_set = set(tags_string.split())
			tags.update(tags_set)
	except:
		print tags_string
	sorted_tags = sorted(tags)
	return sorted_tags

def load(sorted_tags,load_in_db):
	i = 1;
	tag=""
	cursor.execute("truncate table tags")
	try:
		for tag in sorted_tags:
			sql = """insert into tags(name) values (%s)"""
			row = MySQLdb.escape_string(tag)
			if(load_in_db):
				cursor.execute(sql,tag)
			if(i % 1000 == 0):
				print "Committing till " + str(i)
				db.commit()
			i =i+1
		cursor.execute("insert into tags(id,name) values(0,'<BLANK>')")
		cursor.execute("update tags set id=0 where name = '<BLANK>'")
	except Exception, e:
		print "ERROR: on %s, %s" % tag % e
	db.commit()

def transform():
	tags_dict=dict()
        sql = "SELECT id,name from tags"
        try:
                cursor.execute(sql)
                results = cursor.fetchall()
                for row in results:
                        tag_id = row[0]
                        tag_name = row[1]
                        tags_dict[tag_name] = tag_id
	except Exception, e:
		print "ERROR: %s" % e

        i = 1;
        sql = "insert into train_tag_mapping(train_id,tag_id) values (%s,%s)"
        try:
        	cursor.execute("truncate table train_tag_mapping")
	        cursor.execute("select id,tags from train")
        	results = cursor.fetchall()
	        print "DONE fetching training data"
        	for row in results:
                	train_id = row[0]
	                tags_string = row[1]
        	        if tags_string is None:
                	        continue
	                tags_string = tags_string.lower()
        	        tags_list = tags_string.split()
                	undefined_tag = False
	                for tag in tags_list:
        	                if(tags_dict.has_key(tag)):
                	                tag_id = tags_dict[tag]
                        	else:
                                	if undefined_tag:
                                        	continue
	                                undefined_tag = True
        	                        tag_id = 0
                	        cursor.execute(sql,(train_id,tag_id))
                        	if(i % 10000 == 0):
                                	print "Committing till " + str(i)
	                                db.commit()
        	                i = i+1
	except Exception, e:
		print "ERROR: %s" % e
        db.commit()
	sql = "insert into mv_count_tags select g.name, count(*) as count  from tags g, train_tag_mapping m where g.id = m.tag_id group by g.name order by count(*) desc;"
	cursor.execute(sql)

load(extract(),True)
transform()
db.close()
