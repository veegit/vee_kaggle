package com.vee.multilabel.runner;

import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author
 *
 */
public class Querier {

	private static final Logger logger = LoggerFactory.getLogger(Querier.class);

	public static void main(String[] args) {

	}

	void sampleQuery(FullTextSession fullTextSession) throws ParseException {
		QueryParser parser = new QueryParser(Version.LUCENE_CURRENT, Indexer.FIELD, new StandardAnalyzer(Version.LUCENE_CURRENT));
		String searchString = String.format("%s:%s OR %s:%s", Indexer.FIELD, Indexer.queries[0],Indexer.FIELD,Indexer.queries[1]);
		Query luceneQuery = parser.parse(searchString);
		FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery(luceneQuery);
		fullTextQuery.setProjection("id");
		List<Object[]> searchResults = fullTextQuery.list();

		for (Object[] result : searchResults) {
			logger.info("Result found: " + result[0]);
		}
	}

}
