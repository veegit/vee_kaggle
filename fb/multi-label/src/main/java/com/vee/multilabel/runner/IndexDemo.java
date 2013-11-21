package com.vee.multilabel.runner;

import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.Version;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.impl.SimpleIndexingProgressMonitor;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vee.multilabel.entity.New_Train;
import com.vee.multilabel.entity.Tag;

public class IndexDemo {

	private static final Logger logger = LoggerFactory
			.getLogger(IndexDemo.class);
	private static final String queries[] = { "script", "erlang" };
	private static final String FIELD = "body";

	private SessionFactory hibernateSessionFactory;
	private Session hibernateSession;

	public static void main(String args[]) {
		IndexDemo demo = new IndexDemo();
		try {
			demo.setUp();
			demo.testUsingLuceneQueryParserWithProjection();
			//demo.testUsingHibernateSearchQueryBuilderReturningFullEntity();
			//demo.testUsingLuceneBooleanQueryReturningFullEntity();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setUp() throws Exception {
		BasicConfigurator.configure();
		Configuration configuration = new Configuration();
		configuration.configure("hibernate.cfg.xml");
		ServiceRegistry serviceRegistry = new ServiceRegistryBuilder()
				.applySettings(configuration.getProperties())
				.buildServiceRegistry();
		hibernateSessionFactory = configuration
				.buildSessionFactory(serviceRegistry);
		hibernateSession = hibernateSessionFactory.openSession();
		populateDBWithTestData();
	}

	public void testUsingLuceneQueryParserWithProjection() throws Exception {
		FullTextSession fullTestSession = Search
				.getFullTextSession(hibernateSession);
		SimpleIndexingProgressMonitor monitor = new SimpleIndexingProgressMonitor(1000);
		fullTestSession
			.createIndexer(New_Train.class)
			.batchSizeToLoadObjects( 25 )
			.threadsToLoadObjects( 5 )
			.idFetchSize( 150 )
			.threadsForSubsequentFetching( 20 )
			.progressMonitor(monitor)
			.startAndWait();

		QueryParser parser = new QueryParser(Version.LUCENE_CURRENT, FIELD, new StandardAnalyzer(Version.LUCENE_CURRENT));
		String searchString = String.format("%s:%s OR %s:%s", FIELD,queries[0],FIELD,queries[1]);
		Query luceneQuery = parser.parse(searchString);
		FullTextQuery fullTextQuery = fullTestSession.createFullTextQuery(luceneQuery);
		/*
		 * Setting a projection avoids hitting the database and retrieving data
		 * not required by your search use case. This can save overhead when you
		 * only need to return a "read-only" list of items. If no project is
		 * set, Hibernate Search will fetch all matching Hibernate-managed
		 * entities from the database.
		 */
		//fullTextQuery.setProjection("id", "title");
		fullTextQuery.setProjection("id");
		List<Object[]> searchResults = fullTextQuery.list();

		for (Object[] result : searchResults) {
			logger.info("Result found: " + result[0]);
		}
	}

	public void testUsingHibernateSearchQueryBuilderReturningFullEntity() {
		FullTextSession fullTextSession = Search.getFullTextSession(hibernateSession);
		QueryBuilder queryBuilder = fullTextSession.getSearchFactory()
				.buildQueryBuilder().forEntity(Tag.class).get();
		org.apache.lucene.search.Query luceneQuery = queryBuilder
				.bool()
				.should(queryBuilder.keyword().onField("name")
						.matching(queries[0]).createQuery())
				.should(queryBuilder.keyword().onField("name")
						.matching(queries[1]).createQuery()).createQuery();
		org.hibernate.Query hibernateQuery = fullTextSession
				.createFullTextQuery(luceneQuery, Tag.class);
		List<Tag> searchResults = hibernateQuery.list();
		for (Tag tag : searchResults) {
			logger.info("Tag found, id=" + tag.getId() + ", name="
					+ tag.getName());
			if (tag.getName().equals(queries[0])) {
				logger.info("Found {0}", tag.getName());
			} else if (tag.getName().equals(queries[1])) {
				logger.info("Found {0}", tag.getName());
			}
		}
		logger.info(searchResults.size() + "");
	}

	public void testUsingLuceneBooleanQueryReturningFullEntity()
			throws Exception {
		FullTextSession fullTextSession = Search
				.getFullTextSession(hibernateSession);

		BooleanQuery bq = new BooleanQuery();
		TermQuery gt350TermQuery = new TermQuery(new Term("name", queries[0]));
		TermQuery belAirTermQuery = new TermQuery(new Term("name", queries[1]));
		bq.add(gt350TermQuery, BooleanClause.Occur.SHOULD);
		bq.add(belAirTermQuery, BooleanClause.Occur.SHOULD);
		Query q = new QueryParser(Version.LUCENE_CURRENT, "cs-method",
				new StandardAnalyzer(Version.LUCENE_CURRENT)).parse(bq
				.toString());

		org.hibernate.Query hibernateQuery = fullTextSession
				.createFullTextQuery(q, Tag.class);
		List<Tag> searchResults = hibernateQuery.list();

		for (Tag tag : searchResults) {
			logger.info("Tag found, id=" + tag.getId() + ", name="
					+ tag.getName());
			if (tag.getName().equals(queries[0])) {
				logger.info("Found {0}", tag.getName());
			} else if (tag.getName().equals(queries[1])) {
				logger.info("Found {0}", tag.getName());
			}
		}
		logger.info(searchResults.size() + "");
	}

	public void tearDown() throws Exception {
		Transaction tx = hibernateSession.beginTransaction();
		tx.commit();
		hibernateSession.close();
		hibernateSessionFactory.close();
	}

	private void populateDBWithTestData() {
		Transaction tx = hibernateSession.beginTransaction();
		tx.commit();
	}
}
