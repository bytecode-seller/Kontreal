package kontreal.dao;

import java.util.Date;
import java.util.List;
import kontreal.entities.LogArchivoBalanza;
import kontreal.util.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

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
    
    public static List<LogArchivoBalanza> findByStartDate(Date startDate){
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();
        Criteria cr = session.createCriteria(LogArchivoBalanza.class);
        cr.add(Restrictions.ge("fechaArchivo", startDate));
        return cr.list();
    }
    
    public static List<LogArchivoBalanza> getByDataWithCriteria(Date bCarga, Date eCarga, Date bConsulta, Date eConsulta){
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();
        
        Criteria criteria = session.createCriteria(LogArchivoBalanza.class);
        
        if(bCarga != null){
            criteria.add(Restrictions.ge("fechaCarga", bCarga));
            System.out.println("Entro a fecha carga inicio: "+bCarga);
        }
        if(eCarga != null){
            criteria.add(Restrictions.le("fechaCarga", eCarga));
            System.out.println("Entro a fecha carga limite: "+eCarga);
        }
        if(bConsulta != null){
            criteria.add(Restrictions.ge("fechaArchivo", bConsulta));
            System.out.println("Entro a fecha archivo inicio: "+bConsulta);
        }
        if(eConsulta != null){
            criteria.add(Restrictions.le("fechaArchivo", eConsulta));
            System.out.println("Entro a fecha archivo limite: "+eConsulta);
        }
        
        return criteria.list();
    }
}
