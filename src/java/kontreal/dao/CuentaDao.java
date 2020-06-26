/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kontreal.dao;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import kontreal.entities.Cuenta;
import kontreal.entities.Empresa;
import kontreal.entities.Minicatalogo;
import kontreal.util.HibernateUtil;
import org.hibernate.Session;

/**
 *
 * @author modima65
 */
public class CuentaDao {
    
    public static void insert(Minicatalogo miniCatalogo) {
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();
        
        session.save(miniCatalogo);
    }
    
    public static void update(Minicatalogo miniCatalogo) {
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();
        
        session.update(miniCatalogo);
    }
    
    public static void delete(Minicatalogo miniCatalogo) {
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();
        
        session.delete(miniCatalogo);
    }

    public static Cuenta searchById(Integer id) {
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();

        return (Cuenta) session.load(Cuenta.class, id);
    }

    public static void refresh(Cuenta cuenta) {
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();

        session.refresh(cuenta);
    }

    public static List<Cuenta> searchAll(Empresa empresa) {
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();

        List<Cuenta> cuentas = new ArrayList<>();
        if (empresa != null) {
            cuentas = session.createQuery("from Cuenta c where c.empresa = :emp order by c.cuenta").setEntity("emp", empresa).list();
        }
        
        return cuentas;
    }

    public static List<Cuenta> searchDistinct(Empresa empresa) {
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();

        List<Cuenta> cuentas;
        if (empresa != null) {
            cuentas = session.createQuery("select distinct c from Cuenta c where c.nivel = 3 and c.empresa = :emp group by c.cuenta order by c.cuenta")
                    .setEntity("emp", empresa).list();
        } else {
            cuentas = session.createQuery("select distinct c from Cuenta c where c.nivel = 3 group by c.cuenta order by c.cuenta").list();
        }
        
        return cuentas;
    }

    public static List<Minicatalogo> searchMiniCatalogs() {
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();

        List<Minicatalogo> previousList = session.createQuery("from Minicatalogo m left join fetch m.lminicatalogos mm order by m.nombre").list();
        Set<Minicatalogo> noDups = new LinkedHashSet<>(previousList);
        
        return new ArrayList<>(noDups);
    }

    public static List<Cuenta> searchGastos(Empresa empresa) {
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();
        return session.createQuery("from Cuenta c where c.empresa = :emp and tipo like 'G R%' and afecta = 'AFECTABLE' "
                + "order by c.cuenta").setEntity("emp", empresa).list();
    }

}
