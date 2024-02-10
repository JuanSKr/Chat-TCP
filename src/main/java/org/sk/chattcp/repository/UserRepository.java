package org.sk.chattcp.repository;


import org.hibernate.Session;
import org.hibernate.Transaction;
import org.sk.chattcp.entity.User;
import org.sk.chattcp.functionality.hibernate.HibernateUtil;

public class UserRepository {
    public User save(User user) {
        Session session = HibernateUtil.openSession();
        Transaction tx = session.beginTransaction();
        session.save(user);
        tx.commit();
        session.close();
        return user;
    }

    public User findByUsername(String username) {
        Session session = HibernateUtil.openSession();
        User user = session.createQuery("from User where username = :username", User.class)
                .setParameter("username", username)
                .uniqueResult();
        session.close();
        return user;
    }

    public User findById(Long id) {
        Session session = HibernateUtil.openSession();
        User user = session.get(User.class, id);
        session.close();
        return user;
    }

    public void delete(User user) {
        Session session = HibernateUtil.openSession();
        Transaction tx = session.beginTransaction();
        session.delete(user);
        tx.commit();
        session.close();
    }
}
