package kontreal.dao;

import java.util.Date;
import java.util.List;
import kontreal.entities.LogArchivoBalanza;
import kontreal.util.HibernateUtil;
import org.hibernate.Session;

/**
 *
 * @author Martin Tepostillo
 */
public class LogArchivoBalanzaDao {
    
    public static List<LogArchivoBalanza> findAll(){
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();
        
        return session.createQuery("from LogArchivoBalanza").list();
    }
    
    public static void insert(LogArchivoBalanza log){
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();
        
        session.save(log);
    }
    
    public static List<LogArchivoBalanza> getByDateLimitCarga(Date limit){
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();
        
        return session.createQuery("from LogArchivoBalanza l where l.fechaCarga <= :limit")
                .setDate("limit", limit).list();
    }
    
    public static List<LogArchivoBalanza> getByBeginDateCarga(Date begin){
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();
        
        return session.createQuery("from LogArchivoBalanza l where l.fechaCarga >= :begin")
                .setDate("begin", begin).list();
    }
    
    public static List<LogArchivoBalanza> getBetweenDateCarga(Date begin, Date limit){
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();
        
        return session.createQuery("from LogArchivoBalanza l where l.fechaCarga >= :begin and l.fechaCarga <= :limit " )
                .setDate("begin", begin).setDate("limit", limit).list();
    }
    
    public static List<LogArchivoBalanza> getByDateLimitConsulta(Date limit){
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();
        
        return session.createQuery("from LogArchivoBalanza l where l.fechaArchivo <= :limit")
                .setDate("limit", limit).list();
    }
    
    public static List<LogArchivoBalanza> getByBeginDateConsulta(Date begin){
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();
        
        return session.createQuery("from LogArchivoBalanza l where l.fechaArchivo >= :begin")
                .setDate("begin", begin).list();
    }
    
    public static List<LogArchivoBalanza> getBetweenDateConsulta(Date begin, Date limit){
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();
        
        return session.createQuery("from LogArchivoBalanza l where l.fechaArchivo >= :begin and l.fechaArchivo <= :limit " )
                .setDate("begin", begin).setDate("limit", limit).list();
    }
}
