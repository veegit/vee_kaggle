package com.vee.multilabel.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.solr.analysis.LowerCaseFilterFactory;
import org.apache.solr.analysis.RemoveDuplicatesTokenFilterFactory;
import org.apache.solr.analysis.StandardFilterFactory;
import org.apache.solr.analysis.StandardTokenizerFactory;
import org.apache.solr.analysis.StopFilterFactory;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.AnalyzerDef;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Parameter;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.annotations.TokenFilterDef;
import org.hibernate.search.annotations.TokenizerDef;

/**
 * @author
 *
 */
@Entity
@Table(name = "tags")
@Indexed
@AnalyzerDef(name = "tag_analyzer",
	tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),
	filters = {
		@TokenFilterDef(factory = StandardFilterFactory.class),
		@TokenFilterDef(factory = LowerCaseFilterFactory.class),
		@TokenFilterDef(factory = StopFilterFactory.class,
		params = { @Parameter(name = "words", value = "com/vee/multilabel/entity/stopwords.txt") }),
		@TokenFilterDef(factory = RemoveDuplicatesTokenFilterFactory.class)
})
@Analyzer(definition = "tag_analyzer")
public class Tag {
	@Id
	@GeneratedValue
	@DocumentId
	private Long id;

	@Column
	private String name;

	public Tag() { 	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Field(store = Store.YES)
	public String getName() {
		return name;
	}
}
