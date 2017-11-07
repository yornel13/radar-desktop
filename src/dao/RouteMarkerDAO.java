package dao;

import java.util.List;

import model.RouteMarker;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.criterion.Example;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A data access object (DAO) providing persistence and search support for
 * RouteMarker entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see model.RouteMarker
 * @author MyEclipse Persistence Tools
 */
public class RouteMarkerDAO extends BaseHibernateDAO {
	private static final Logger log = LoggerFactory
			.getLogger(RouteMarkerDAO.class);
	// property constants
	public static final String CREATE_DATE = "createDate";

	public void save(RouteMarker transientInstance) {
		log.debug("saving RouteMarker instance");
		try {
			getSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(RouteMarker persistentInstance) {
		log.debug("deleting RouteMarker instance");
		try {
			getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public RouteMarker findById(Long id) {
		log.debug("getting RouteMarker instance with id: " + id);
		try {
			RouteMarker instance = (RouteMarker) getSession().get(
					"model.RouteMarker", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<RouteMarker> findAllByControlId(Long controlId) {
		Query query = getSession().createSQLQuery("SELECT * FROM route_marker WHERE " +
				"control_id = :control_id")
				.addEntity(RouteMarker.class)
				.setParameter("control_id", controlId);

		Object result = query.list();
		return (List<RouteMarker>) result;
	}

	public List findByExample(RouteMarker instance) {
		log.debug("finding RouteMarker instance by example");
		try {
			List results = getSession().createCriteria("model.RouteMarker")
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
		log.debug("finding RouteMarker instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from RouteMarker as model where model."
					+ propertyName + "= ?";
			Query queryObject = getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByCreateDate(Object createDate) {
		return findByProperty(CREATE_DATE, createDate);
	}

	public List findAll() {
		log.debug("finding all RouteMarker instances");
		try {
			String queryString = "from RouteMarker";
			Query queryObject = getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public RouteMarker merge(RouteMarker detachedInstance) {
		log.debug("merging RouteMarker instance");
		try {
			RouteMarker result = (RouteMarker) getSession().merge(
					detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(RouteMarker instance) {
		log.debug("attaching dirty RouteMarker instance");
		try {
			getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(RouteMarker instance) {
		log.debug("attaching clean RouteMarker instance");
		try {
			getSession().buildLockRequest(LockOptions.NONE).lock(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
}