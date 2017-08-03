package dao;

import java.util.List;
import java.util.Set;

import model.Route;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.criterion.Example;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A data access object (DAO) providing persistence and search support for Route
 * entities. Transaction control of the save(), update() and delete() operations
 * can directly support Spring container-managed transactions or they can be
 * augmented to handle user-managed Spring transactions. Each of these methods
 * provides additional information for how to configure it for the desired type
 * of transaction control.
 * 
 * @see model.Route
 * @author MyEclipse Persistence Tools
 */
public class RouteDAO extends BaseHibernateDAO {
	private static final Logger log = LoggerFactory.getLogger(RouteDAO.class);
	// property constants
	public static final String NAME = "name";
	public static final String CREATE_DATE = "createDate";
	public static final String LAST_UPDATE = "lastUpdate";
	public static final String ACTIVE = "active";

	public void save(Route transientInstance) {
		log.debug("saving Route instance");
		try {
			getSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(Route persistentInstance) {
		log.debug("deleting Route instance");
		try {
			getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Route findById(Long id) {
		log.debug("getting Route instance with id: " + id);
		try {
			Route instance = (Route) getSession().get("model.Route", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByExample(Route instance) {
		log.debug("finding Route instance by example");
		try {
			List results = getSession().createCriteria("model.Route")
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
		log.debug("finding Route instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from Route as model where model."
					+ propertyName + "= ?";
			Query queryObject = getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByName(Object name) {
		return findByProperty(NAME, name);
	}

	public List findByCreateDate(Object createDate) {
		return findByProperty(CREATE_DATE, createDate);
	}

	public List findByLastUpdate(Object lastUpdate) {
		return findByProperty(LAST_UPDATE, lastUpdate);
	}

	public List findByActive(Object active) {
		return findByProperty(ACTIVE, active);
	}

	public List findAll() {
		log.debug("finding all Route instances");
		try {
			String queryString = "from Route";
			Query queryObject = getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public Route merge(Route detachedInstance) {
		log.debug("merging Route instance");
		try {
			Route result = (Route) getSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(Route instance) {
		log.debug("attaching dirty Route instance");
		try {
			getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Route instance) {
		log.debug("attaching clean Route instance");
		try {
			getSession().buildLockRequest(LockOptions.NONE).lock(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
}