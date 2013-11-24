package com.vee.multilabel.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.apache.solr.analysis.LowerCaseFilterFactory;
import org.apache.solr.analysis.RemoveDuplicatesTokenFilterFactory;
import org.apache.solr.analysis.StandardFilterFactory;
import org.apache.solr.analysis.StandardTokenizerFactory;
import org.apache.solr.analysis.StopFilterFactory;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.AnalyzerDef;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Parameter;
import org.hibernate.search.annotations.TokenFilterDef;
import org.hibernate.search.annotations.TokenizerDef;

/**
 * @author
 *
 */
@Entity
@Table(name = "train")
@Indexed
@AnalyzerDef(name = "train_analyzer",
	tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),
	filters = {
		@TokenFilterDef(factory = StandardFilterFactory.class),
		@TokenFilterDef(factory = LowerCaseFilterFactory.class),
		@TokenFilterDef(factory = StopFilterFactory.class,
		params = { @Parameter(name = "words", value = "com/vee/multilabel/entity/stopwords.txt") }),
		@TokenFilterDef(factory = RemoveDuplicatesTokenFilterFactory.class)
})
@Analyzer(definition = "train_analyzer")
public class Train {
	@Id
	@GeneratedValue
	@DocumentId
	private Integer id;

	@Column
	private String title;

	@Column(length=16777215)
	@Type(type="text")
	private String body;

	@ManyToMany
	@JoinTable(name = "train_tag_mapping", joinColumns = @JoinColumn(name = "train_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
	private List<Tag> tagList = new ArrayList<Tag>();

	public Train() { 	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void setTagList(List<Tag> tagList) {
		this.tagList = tagList;
	}

	public Integer getId() {
		return id;
	}

	@Field
	public String getTitle() {
		return title;
	}

	@Field
	public String getBody() {
		return body;
	}

	public List<Tag> getTagList() {
		return tagList;
	}
}
