package dao;

import model.User;
import util.HibernateSessionFactory;

public class TestDB {

    public static void main(String[] args) {
        User user = new UserDAO().findById(8l);
        user.setLastname("Cardenas");
        HibernateSessionFactory.getSession().flush();

    }
}
