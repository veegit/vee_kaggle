package com.vee.multilabel.lucene;

import java.io.File;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import com.vee.multilabel.entity.Tag;

/**
 * @author
 *
 */
public class LuceneUtils {

	private static SessionFactory hibernateSessionFactory;
	private static Session hibernateSession;
	private static FullTextSession fullTextSession;
	private static Configuration configuration;

	private LuceneUtils() {
		//Dont new me
	}

	public static FullTextSession getSession() {
		if(fullTextSession == null) {
			configuration = new Configuration();
			configuration.configure();
			ServiceRegistry serviceRegistry = new ServiceRegistryBuilder()
						.applySettings(configuration.getProperties())
						.buildServiceRegistry();
			hibernateSessionFactory = configuration.buildSessionFactory(serviceRegistry);
			hibernateSession = hibernateSessionFactory.openSession();
			fullTextSession = Search.getFullTextSession(hibernateSession);
		}
		return fullTextSession;
	}

	public static void closeSession() {
		if(fullTextSession != null) {
			if(hibernateSession.isOpen()) {
				hibernateSession.close();
			}
			if(!hibernateSessionFactory.isClosed()) {
				hibernateSessionFactory.close();
			}
		}
	}

	public static boolean indexExists() {
		File indexDirectory = new File(configuration.getProperty("hibernate.search.default.indexBase") + "/" + Tag.class.getName());
		return (indexDirectory != null && indexDirectory.exists());
	}

}
