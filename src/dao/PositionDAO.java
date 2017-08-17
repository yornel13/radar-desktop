package dao;

import java.util.List;

import model.Position;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.criterion.Example;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A data access object (DAO) providing persistence and search support for
 * Position entities. Transaction control of the save(), update() and delete()
 * operations can directly support Spring container-managed transactions or they
 * can be augmented to handle user-managed Spring transactions. Each of these
 * methods provides additional information for how to configure it for the
 * desired type of transaction control.
 * 
 * @see model.Position
 * @author MyEclipse Persistence Tools
 */
public class PositionDAO extends BaseHibernateDAO {
	private static final Logger log = LoggerFactory
			.getLogger(PositionDAO.class);
	// property constants
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String TIME = "time";
	public static final String UPDATE_TIME = "updateTime";

	public void save(Position transientInstance) {
		log.debug("saving Position instance");
		try {
			getSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(Position persistentInstance) {
		log.debug("deleting Position instance");
		try {
			getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Position findById(java.lang.Long id) {
		log.debug("getting Position instance with id: " + id);
		try {
			Position instance = (Position) getSession().get("modelo.Position",
					id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<Position> findAllByWatchId(Long watchId) {
		Query query = getSession().createSQLQuery("SELECT * FROM position WHERE " +
				"watch_id = :watch_id ORDER BY time DESC")
				.addEntity(Position.class)
				.setParameter("watch_id", watchId);

		Object result = query.list();
		return (List<Position>) result;
	}

	public List<Position> findAllByControlId(Long controlId) {
		Query query = getSession().createSQLQuery("SELECT * FROM position WHERE " +
				"control_id = :control_id ORDER BY time DESC")
				.addEntity(Position.class)
				.setParameter("control_id", controlId);

		Object result = query.list();
		return (List<Position>) result;
	}

	public List findByExample(Position instance) {
		log.debug("finding Position instance by example");
		try {
			List results = getSession().createCriteria("modelo.Position")
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
		log.debug("finding Position instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from Position as model where model."
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

	public List findByTime(Object time) {
		return findByProperty(TIME, time);
	}

	public List findByUpdateTime(Object updateTime) {
		return findByProperty(UPDATE_TIME, updateTime);
	}

	public List findAll() {
		log.debug("finding all Position instances");
		try {
			String queryString = "from Position";
			Query queryObject = getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public Position merge(Position detachedInstance) {
		log.debug("merging Position instance");
		try {
			Position result = (Position) getSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(Position instance) {
		log.debug("attaching dirty Position instance");
		try {
			getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Position instance) {
		log.debug("attaching clean Position instance");
		try {
			getSession().buildLockRequest(LockOptions.NONE).lock(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
}