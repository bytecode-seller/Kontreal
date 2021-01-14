package kontreal.dao;

import java.util.List;
import kontreal.entities.Impresora;
import kontreal.util.HibernateUtil;
import org.hibernate.Session;

/**
 *
 * @author Martin Tepostillo
 */
public class ImpresoraDao {
    
    public static void guardarImpresora(Impresora impresora){
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();
        session.saveOrUpdate(impresora);
    }
    
    public static boolean exists(Impresora impresora){
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();
        return session.createQuery("SELECT 1 FROM Impresora WHERE nombre = :nombre AND service = :service AND characteristic = :characteristic")
                .setString("nombre", impresora.getNombre())
                .setString("service", impresora.getPrintService())
                .setString("characteristic", impresora.getPrintCharacteristic())
                .uniqueResult() != null;
    }
    
    public static List<Impresora> getImpresoras(){
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();
        
        return session.createQuery("FROM Impresora").list();
    }
}
