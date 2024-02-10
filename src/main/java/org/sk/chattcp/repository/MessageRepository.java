package org.sk.chattcp.repository;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.sk.chattcp.entity.Message;
import org.sk.chattcp.functionality.hibernate.HibernateUtil;

public class MessageRepository {
    public Message save(Message message) {
        Session session = HibernateUtil.openSession();
        Transaction tx = session.beginTransaction();
        session.save(message);
        tx.commit();
        session.close();
        return message;
    }

    public Message findById(Long id) {
        Session session = HibernateUtil.openSession();
        Message message = session.get(Message.class, id);
        session.close();
        return message;
    }

    public void delete(Message message) {
        Session session = HibernateUtil.openSession();
        Transaction tx = session.beginTransaction();
        session.delete(message);
        tx.commit();
        session.close();
    }

}