package com.vee.multilabel.runner;

import org.hibernate.search.FullTextSession;
import org.hibernate.search.impl.SimpleIndexingProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vee.multilabel.entity.Tag;
import com.vee.multilabel.entity.Train;
import com.vee.multilabel.lucene.LuceneUtils;

public class Indexer {

	private static final Logger logger = LoggerFactory.getLogger(Indexer.class);

	private FullTextSession fullTextSession = LuceneUtils.getSession();

	public static void main(String args[]) {
		Indexer indexer = new Indexer();
		Querier querier = new Querier();
		try {
			indexer.index(false);
			querier.sampleQuery(indexer.getFullTextSession());
			LuceneUtils.closeSession();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void index(boolean reIndex) throws Exception {
		if (reIndex || !LuceneUtils.indexExists()) {
			System.out.println("Creating Index!!!!");
			SimpleIndexingProgressMonitor monitor = new SimpleIndexingProgressMonitor(1000);
			fullTextSession.createIndexer(Tag.class, Train.class).progressMonitor(monitor).startAndWait();
		}
	}

	FullTextSession getFullTextSession() {
		return fullTextSession;
	}
}