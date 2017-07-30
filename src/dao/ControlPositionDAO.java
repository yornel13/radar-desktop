package dao;

import java.util.List;
import java.util.Set;

import model.ControlPosition;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.criterion.Example;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A data access object (DAO) providing persistence and search support for
 * ControlPosition entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see model.ControlPosition
 * @author MyEclipse Persistence Tools
 */
public class ControlPositionDAO extends BaseHibernateDAO {
	private static final Logger log = LoggerFactory
			.getLogger(ControlPositionDAO.class);
	// property constants
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String PLACE_NAME = "placeName";

	public void save(ControlPosition transientInstance) {
		log.debug("saving ControlPosition instance");
		try {
			getSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(ControlPosition persistentInstance) {
		log.debug("deleting ControlPosition instance");
		try {
			getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public ControlPosition findById(java.lang.Long id) {
		log.debug("getting ControlPosition instance with id: " + id);
		try {
			ControlPosition instance = (ControlPosition) getSession().get(
					"model.ControlPosition", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public ControlPosition findByLatitudeLongitude(Double latitude, Double longitude) {
		Query query = getSession().createSQLQuery("SELECT * FROM control_position WHERE " +
				"latitude = :latitude AND " +
				"longitude = :longitude")
				.addEntity(ControlPosition.class)
				.setParameter("latitude", latitude)
				.setParameter("longitude", longitude);

		Object result = query.uniqueResult();
		return (ControlPosition) result;
	}

	public List findByExample(ControlPosition instance) {
		log.debug("finding ControlPosition instance by example");
		try {
			List results = getSession()
					.createCriteria("model.ControlPosition")
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
		log.debug("finding ControlPosition instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from ControlPosition as model where model."
					+ propertyName + "= ?";
			Query queryObject = getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByLatitude(Object latitude) {
		return findByProperty(LATITUDE, latitude);
	}

	public List findByLongitude(Object longitude) {
		return findByProperty(LONGITUDE, longitude);
	}

	public List findByPlaceName(Object placeName) {
		return findByProperty(PLACE_NAME, placeName);
	}

	public List findAll() {
		log.debug("finding all ControlPosition instances");
		try {
			String queryString = "from ControlPosition";
			Query queryObject = getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public List<ControlPosition> findAllActive() {
		Query query = getSession().createSQLQuery("SELECT * FROM control_position WHERE " +
				"active = true")
				.addEntity(ControlPosition.class);

		Object result = query.list();
		return (List<ControlPosition>) result;
	}

	public List<ControlPosition> findAllOrder() {
		Query query = getSession().createSQLQuery("SELECT * FROM control_position ORDER " +
				"by active DESC")
				.addEntity(ControlPosition.class);

		Object result = query.list();
		return (List<ControlPosition>) result;
	}

	public ControlPosition merge(ControlPosition detachedInstance) {
		log.debug("merging ControlPosition instance");
		try {
			ControlPosition result = (ControlPosition) getSession().merge(
					detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(ControlPosition instance) {
		log.debug("attaching dirty ControlPosition instance");
		try {
			getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(ControlPosition instance) {
		log.debug("attaching clean ControlPosition instance");
		try {
			getSession().buildLockRequest(LockOptions.NONE).lock(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
}