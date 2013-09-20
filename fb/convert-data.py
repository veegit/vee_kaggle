#!/usr/bin/python

import MySQLdb

db = MySQLdb.connect("localhost","root","password","kaggle_fb")
cursor = db.cursor()

def extract():
	tags = set()
	sql = "SELECT tags from train"
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
	myfile = open("tags.txt","w")
	tag=""
	s=""
	cursor.execute("truncate table tags")
	try:
		for tag in sorted_tags:
			sql = """insert into tags(name) values (%s)"""
			row = MySQLdb.escape_string(tag)
			if(load_in_db):
				cursor.execute(sql,tag)
			s = s + tag + "\n"
			if(i % 1000 == 0):
				print "Committing till " + str(i)
				db.commit()
				print>>myfile,s
				s=""
			i =i+1
	except:
		print "ERROR "+tag
	print>>myfile,s
	db.commit()	

load(extract(),True)
db.close()
