package dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import model.Watch;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.criterion.Example;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A data access object (DAO) providing persistence and search support for Watch
 * entities. Transaction control of the save(), update() and delete() operations
 * can directly support Spring container-managed transactions or they can be
 * augmented to handle user-managed Spring transactions. Each of these methods
 * provides additional information for how to configure it for the desired type
 * of transaction control.
 * 
 * @see model.Watch
 * @author MyEclipse Persistence Tools
 */
public class WatchDAO extends BaseHibernateDAO {
	private static final Logger log = LoggerFactory.getLogger(WatchDAO.class);
	// property constants
	public static final String START_TIME = "startTime";
	public static final String END_TIME = "endTime";

	public void save(Watch transientInstance) {
		log.debug("saving Watch instance");
		try {
			getSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(Watch persistentInstance) {
		log.debug("deleting Watch instance");
		try {
			getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Watch findById(java.lang.Long id) {
		log.debug("getting Watch instance with id: " + id);
		try {
			Watch instance = (Watch) getSession().get("modelo.Watch", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public Watch findByTime(Long startTime, Long endTime) {
		Query query = getSession().createSQLQuery("SELECT * FROM watch WHERE " +
				"start_time = :startTime AND " +
				"end_time = :endTime")
				.addEntity(Watch.class)
				.setParameter("startTime", startTime)
				.setParameter("endTime", endTime);

		Object result = query.uniqueResult();
		return (Watch) result;
	}

	public List<Watch> findAllByUserId(Long userId) {
		Query query = getSession().createSQLQuery("SELECT * FROM watch WHERE " +
				"user_id = :user_id")
				.addEntity(Watch.class)
				.setParameter("user_id", userId);

		Object result = query.list();
		return (List<Watch>) result;
	}

	public List findByExample(Watch instance) {
		log.debug("finding Watch instance by example");
		try {
			List results = getSession().createCriteria("modelo.Watch")
					.add(Example.create(instance)).list();
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding Watch instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from Watch as model where model."
					+ propertyName + "= ?";
			Query queryObject = getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByStartTime(Object startTime) {
		return findByProperty(START_TIME, startTime);
	}

	public List findByEndTime(Object endTime) {
		return findByProperty(END_TIME, endTime);
	}

	public List findAll() {
		log.debug("finding all Watch instances");
		try {
			String queryString = "from Watch";
			Query queryObject = getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public Watch merge(Watch detachedInstance) {
		log.debug("merging Watch instance");
		try {
			Watch result = (Watch) getSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(Watch instance) {
		log.debug("attaching dirty Watch instance");
		try {
			getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Watch instance) {
		log.debug("attaching clean Watch instance");
		try {
			getSession().buildLockRequest(LockOptions.NONE).lock(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
}