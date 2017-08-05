package dao;

import java.util.List;

import model.Route;
import model.RoutePosition;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.criterion.Example;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A data access object (DAO) providing persistence and search support for
 * RoutePosition entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see model.RoutePosition
 * @author MyEclipse Persistence Tools
 */
public class RoutePositionDAO extends BaseHibernateDAO {
	private static final Logger log = LoggerFactory
			.getLogger(RoutePositionDAO.class);
	// property constants
	public static final String CREATE_DATE = "createDate";

	public void save(RoutePosition transientInstance) {
		log.debug("saving RoutePosition instance");
		try {
			getSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(RoutePosition persistentInstance) {
		log.debug("deleting RoutePosition instance");
		try {
			getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public RoutePosition findById(Long id) {
		log.debug("getting RoutePosition instance with id: " + id);
		try {
			RoutePosition instance = (RoutePosition) getSession().get(
					".RoutePosition", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByExample(RoutePosition instance) {
		log.debug("finding RoutePosition instance by example");
		try {
			List results = getSession().createCriteria("model.RoutePosition")
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
		log.debug("finding RoutePosition instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from RoutePosition as model where model."
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
		log.debug("finding all RoutePosition instances");
		try {
			String queryString = "from RoutePosition";
			Query queryObject = getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public List<RoutePosition> findAllByRouteId(Long routeId) {
		Query query = getSession().createSQLQuery("SELECT * FROM route_position WHERE " +
				"route_id = :route_id ORDER BY create_date DESC")
				.addEntity(RoutePosition.class)
				.setParameter("route_id", routeId);

		Object result = query.list();
		return (List<RoutePosition>) result;
	}


	public RoutePosition merge(RoutePosition detachedInstance) {
		log.debug("merging RoutePosition instance");
		try {
			RoutePosition result = (RoutePosition) getSession().merge(
					detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(RoutePosition instance) {
		log.debug("attaching dirty RoutePosition instance");
		try {
			getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(RoutePosition instance) {
		log.debug("attaching clean RoutePosition instance");
		try {
			getSession().buildLockRequest(LockOptions.NONE).lock(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
}