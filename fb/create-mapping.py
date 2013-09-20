#!/usr/bin/python

import MySQLdb

db = MySQLdb.connect("localhost","root","password","kaggle_fb")
tags_dict = dict()
cursor = db.cursor()

def load_tags():
	sql = "SELECT id,name from tags"
	try:
		cursor.execute(sql)
		results = cursor.fetchall()
		for row in results:
			tag_id = row[0]
			tag_name = row[1]
			tags_dict[tag_name] = tag_id
	except:
		print "ERROR"
	print "DONE Loading Tags"

def load_mapping():
	i = 1;
	sql = "insert into train_tag_mapping(train_id,tag_id) values (%s,%s)"
	#try:
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
			if(i % 1000 == 0):
				print "Committing till " + str(i)
				db.commit()
			i = i+1
	#except:
		#print "ERROR"
	db.commit()	

load_tags()
load_mapping()
db.close()
