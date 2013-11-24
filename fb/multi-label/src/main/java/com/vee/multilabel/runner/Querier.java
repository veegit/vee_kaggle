package com.vee.multilabel.runner;

import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionException;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.transform.ResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vee.multilabel.entity.Tag;
import com.vee.multilabel.entity.Train;
import com.vee.multilabel.lucene.LuceneUtils;

/**
 * @author
 *
 */
public class Querier {

	private static final Logger logger = LoggerFactory.getLogger(Querier.class);
	public static final String queries[] = { "extract description", "select option" };
	public static final String queries1[] = { "\"html pars\"" };
	public static final String SEARCH_FIELD = "title";

	private Session session;

	public Querier(Session session) {
		this.session = session;
	}

	public static void main(String[] args) {
		try {
			new Querier(LuceneUtils.getSession()).searchQuery();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	void searchQuery() throws ParseException, SessionException {
		FullTextSession fullTextSession = null;
		if (session instanceof FullTextSession) {
			fullTextSession = (FullTextSession) session;
		}
		else {
			throw new SessionException("No Text session acive");
		}
		QueryParser parser = new QueryParser(Version.LUCENE_CURRENT, SEARCH_FIELD, new StandardAnalyzer(Version.LUCENE_CURRENT));
		String searchString = String.format("%s:%s", SEARCH_FIELD, queries1[0]);
		Query luceneQuery = parser.parse(searchString);
		FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery(luceneQuery);
		fullTextQuery.setProjection("id");
		@SuppressWarnings("unchecked")
		List<Object[]> searchResults = fullTextQuery.list();

		for (Object[] result : searchResults) {
			System.out.println("Result found: " + result[0] + " " + ((Train) findById(Train.class,(Integer) result[0])).getTitle() );
		}
	}

	void databaseQuery() {
		try {
			DetachedCriteria criteria = DetachedCriteria.forClass(Train.class)
					.createCriteria("tagList")
						.add(Restrictions.in("name", queries));
			List<Train> trains = doFindByCriteria(criteria, CriteriaSpecification.DISTINCT_ROOT_ENTITY, -1, -1);
			for (Train train: trains) {
				String str="";
				for(Tag tag: train.getTagList()) {
					str += tag.getName() + ",";
				}
				System.out.println(String.format("%d\t%s\t%s",train.getId(), train.getTitle(), str));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	private List doFindByCriteria(final DetachedCriteria criteria, final ResultTransformer resultTransformer, final int firstResult, final int maxResults) {
		if (resultTransformer != null) {
			criteria.setResultTransformer(resultTransformer);
		}
		Criteria executableCriteria = criteria.getExecutableCriteria(session);
		if (firstResult >= 0) {
			executableCriteria.setFirstResult(firstResult);
		}
		if (maxResults > 0) {
			executableCriteria.setMaxResults(maxResults);
		}
		@SuppressWarnings("unchecked")
		List result = executableCriteria.list();
		return result;
	}

	public Object findById(Class type, int id) {
		Object result = session.load(type, id);
		return result;
	}



}
