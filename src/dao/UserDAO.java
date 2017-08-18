package dao;

import java.util.List;
import java.util.Set;

import javafx.collections.ObservableList;
import model.Group;
import model.User;
import model.Watch;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.criterion.Example;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.soap.SOAPBinding;

/**
 * A data access object (DAO) providing persistence and search support for User
 * entities. Transaction control of the save(), update() and delete() operations
 * can directly support Spring container-managed transactions or they can be
 * augmented to handle user-managed Spring transactions. Each of these methods
 * provides additional information for how to configure it for the desired type
 * of transaction control.
 * 
 * @see model.User
 * @author MyEclipse Persistence Tools
 */
public class UserDAO extends BaseHibernateDAO {
	private static final Logger log = LoggerFactory.getLogger(UserDAO.class);
	// property constants
	public static final String DNI = "dni";
	public static final String NAME = "name";
	public static final String LASTNAME = "lastname";
	public static final String PASSWORD = "password";
	public static final String CREATE_DATE = "create_date";
	public static final String LAST_UPDATE = "last_update";
	public static final String ACTIVE = "active";

	public void save(User transientInstance) {
		log.debug("saving User instance");
		try {
			getSession().save(transientInstance);
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}
	}

	public void delete(User persistentInstance) {
		log.debug("deleting User instance");
		try {
			getSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public User findById(java.lang.Long id) {
		log.debug("getting User instance with id: " + id);
		try {
			User instance = (User) getSession().get("model.User", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public User findByDni(String dni) {
		Query query = getSession().createSQLQuery("SELECT * FROM user WHERE " +
				"dni = :dni")
				.addEntity(User.class)
				.setParameter("dni", dni);

		Object result = query.uniqueResult();
		return (User) result;
	}

	public List findUserByGroupId(Long id) {
		Query query = getSession().createSQLQuery("SELECT * FROM user WHERE " +
				"group_id = :group_id")
				.addEntity(User.class)
				.setParameter("group_id", id);

		Object result = query.list();
		return (List) result;
	}

	public List findByExample(User instance) {
		log.debug("finding User instance by example");
		try {
			List results = getSession().createCriteria("model.User")
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
		log.debug("finding User instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from User as model where model."
					+ propertyName + "= ?";
			Query queryObject = getSession().createQuery(queryString);
			queryObject.setParameter(0, value);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByDni(Object dni) {
		return findByProperty(DNI, dni);
	}

	public List findByName(Object name) {
		return findByProperty(NAME, name);
	}

	public List findByLastname(Object lastname) {
		return findByProperty(LASTNAME, lastname);
	}

	public List findByPassword(Object password) {
		return findByProperty(PASSWORD, password);
	}

	public List findByCreate(Object create) {
		return findByProperty(CREATE_DATE, create);
	}

	public List findByUpdate(Object update) {
		return findByProperty(LAST_UPDATE, update);
	}

	public List findByActive(Object active) {
		return findByProperty(ACTIVE, active);
	}

	public List findAll() {
		log.debug("finding all User instances");
		try {
			String queryString = "from User";
			Query queryObject = getSession().createQuery(queryString);
			return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public List<User> findAllOrder() {
		Query query = getSession().
				createSQLQuery("SELECT * FROM user ORDER BY active DESC ")
				.addEntity(User.class);
		Object result = query.list();
		return (List<User>) result;
	}

	public List<User> findAllOrderByCompanyId(Long companyId) {
		Query query = getSession().
				createSQLQuery("SELECT * FROM user WHERE company_id = :company_id ORDER BY active DESC")
				.addEntity(User.class)
				.setParameter("company_id", companyId);
		Object result = query.list();
		return (List<User>) result;
	}

	public List<User> findAllByCompanyIdActive(Long companyId) {
		Query query = getSession().
				createSQLQuery("SELECT * FROM user WHERE company_id = :company_id AND active = true")
				.addEntity(User.class)
				.setParameter("company_id", companyId);
		Object result = query.list();
		return (List<User>) result;
	}

	public List findAllOrderByGroup() {
		Query query = getSession().createSQLQuery("SELECT * FROM user ORDER BY group_id ")
				.addEntity(User.class);
		Object result = query.list();
		return (List) result;
	}

	public List findAllByCompanyIdOrderByGroup(Long companyId) {
		Query query = getSession().createSQLQuery("SELECT * FROM user WHERE company_id = :company_id ORDER BY group_id")
				.addEntity(User.class)
				.setParameter("company_id", companyId);
		Object result = query.list();
		return (List) result;
	}

	public List findUserByCompanyIdByGroupId(Long id, Long companyId) {
		Query query = getSession().createSQLQuery("SELECT * FROM user WHERE " +
				"group_id = :group_id and company_id = :company_id")
				.addEntity(User.class)
				.setParameter("group_id", id)
				.setParameter("company_id", companyId);
		Object result = query.list();
		return (List) result;
	}

	public List<User> findAllActive() {
		Query query = getSession().
				createSQLQuery("SELECT * FROM user where active = true")
				.addEntity(User.class);
		Object result = query.list();
		return (List<User>) result;
	}

	public User merge(User detachedInstance) {
		log.debug("merging User instance");
		try {
			User result = (User) getSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(User instance) {
		log.debug("attaching dirty User instance");
		try {
			getSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(User instance) {
		log.debug("attaching clean User instance");
		try {
			getSession().buildLockRequest(LockOptions.NONE).lock(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
}