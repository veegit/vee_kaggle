package com.vee.multilabel.runner;

import java.io.File;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.impl.SimpleIndexingProgressMonitor;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vee.multilabel.entity.Tag;
import com.vee.multilabel.entity.Train;

public class Indexer {

	private static final Logger logger = LoggerFactory.getLogger(Indexer.class);

	private SessionFactory hibernateSessionFactory;
	private Session hibernateSession;
	private FullTextSession fullTextSession;

	public static void main(String args[]) {
		Indexer demo = new Indexer();
		Querier querier = new Querier();
		try {
			demo.index(false);
			querier.sampleQuery(demo.getFullTextSession());
			demo.tearDown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void index(boolean reIndex) throws Exception {
		Configuration configuration = new Configuration();
		configuration.configure();
		ServiceRegistry serviceRegistry = new ServiceRegistryBuilder()
					.applySettings(configuration.getProperties())
					.buildServiceRegistry();
		hibernateSessionFactory = configuration.buildSessionFactory(serviceRegistry);
		hibernateSession = hibernateSessionFactory.openSession();
		fullTextSession = Search.getFullTextSession(hibernateSession);

		File indexDirectory = new File(configuration.getProperty("hibernate.search.default.indexBase") + "/" + Tag.class.getName());
		boolean indexExists = indexDirectory != null && indexDirectory.exists();
		if (reIndex || !indexExists) {
			System.out.println("Creating Index!!!!");
			SimpleIndexingProgressMonitor monitor = new SimpleIndexingProgressMonitor(1000);
			fullTextSession.createIndexer(Tag.class, Train.class).progressMonitor(monitor).startAndWait();
		}
	}

	public void tearDown() throws Exception {
		hibernateSession.close();
		hibernateSessionFactory.close();
		logger.info("Closing");
	}

	public FullTextSession getFullTextSession() {
		return fullTextSession;
	}
}