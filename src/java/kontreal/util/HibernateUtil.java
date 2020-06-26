/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kontreal.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.hibernate.stat.Statistics;

/**
 * Hibernate Utility class with a convenient method to get Session Factory
 * object.
 *
 * @author modima65
 */
public class HibernateUtil {

    private static final SessionFactory sessionFactory;
    private static final ServiceRegistry serviceRegistry;
    private static final ThreadLocal threadSession = new ThreadLocal();
    private static final ThreadLocal threadTransaction = new ThreadLocal();
    private static final Statistics statistics;

    static {
        try {
            // Create the SessionFactory from standard (hibernate.cfg.xml)
            // config file.
            Configuration configuration = new Configuration();
            configuration.configure();
            serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
            //            Deprecated **
            //            sessionFactory = new Configuration().configure().buildSessionFactory();
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            statistics = sessionFactory.getStatistics();
            statistics.setStatisticsEnabled(true);
        } catch (HibernateException e) {
            // Log the exception.
            System.out.println(":--- Initial SessionFactory creation failed.");
            Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, null, e);
            throw new ExceptionInInitializerError(e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static Session getSession() {
        Session session = (Session)threadSession.get();
        try {
            if (session == null) {
                session = sessionFactory.openSession();
                threadSession.set(session);
            }
        } catch (HibernateException e) {
            System.out.println(":--- From HibernateUtil.getSession Method.");
            Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, null, e);
        }
        return session;
    }

    public static void closeSession() {
        try {
            Session session = (Session)threadSession.get();
            threadSession.set(null);
            if (session != null && session.isOpen()) {
                System.out.println(":--- Closing... --- :");
                session.close();
            }
        } catch (HibernateException e) {
            System.out.println(":--- From HibernateUtil.closeSession Method.");
            Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public static void beginTransaction() {
        Transaction tx = (Transaction)threadTransaction.get();
        try {
            if (tx == null) {
                tx = getSession().beginTransaction();
                threadTransaction.set(tx);
            }
        } catch (HibernateException e) {
            System.out.println(":--- From HibernateUtil.beginTransaction Method.");
            Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, null, e);
        }

    }

    public static void commitTransaction() {
        Transaction tx = (Transaction)threadTransaction.get();
        try {
            if (tx != null && !tx.wasCommitted() && !tx.wasRolledBack()) {
                System.out.println(":--- Commiting... --- :");
                tx.commit();
                threadTransaction.set(null);
                System.out.println(":--- Commited --- :");
            }
        } catch (HibernateException e) {
            rollbackTransaction();
            System.out.println(":--- Rolling back... --- :");
            System.out.println(":--- From HibernateUtil.commitTransaction Method.");
            Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public static void rollbackTransaction() {
        Transaction tx = (Transaction)threadTransaction.get();
        try {
            threadTransaction.set(null);
            if (tx != null && !tx.wasCommitted() && !tx.wasRolledBack()) {
                tx.rollback();
                System.out.println(":--- Rolledback --- :");
            }
        } catch (HibernateException e) {
            System.out.println(":--- From HibernateUtil.rollbackTransaction Method.");
            Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            closeSession();
        }
    }

    public Statistics getStatistics() {
        return statistics;
    }
}
