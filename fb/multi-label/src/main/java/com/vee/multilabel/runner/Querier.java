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

import com.vee.multilabel.lucene.LuceneUtils;

/**
 * @author
 *
 */
public class Querier {

	private static final Logger logger = LoggerFactory.getLogger(Querier.class);
	public static final String queries[] = { "lisp", "erlang" };
	public static final String SEARCH_FIELD = "body";

	public static void main(String[] args) {
		try {
			new Querier().sampleQuery(LuceneUtils.getSession());
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	void sampleQuery(FullTextSession fullTextSession) throws ParseException {
		QueryParser parser = new QueryParser(Version.LUCENE_CURRENT, SEARCH_FIELD, new StandardAnalyzer(Version.LUCENE_CURRENT));
		String searchString = String.format("%s:%s OR %s:%s", SEARCH_FIELD, queries[0], SEARCH_FIELD, queries[1]);
		Query luceneQuery = parser.parse(searchString);
		FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery(luceneQuery);
		fullTextQuery.setProjection("id");
		List<Object[]> searchResults = fullTextQuery.list();

		for (Object[] result : searchResults) {
			System.out.println("Result found: " + result[0]);
		}
	}

}
