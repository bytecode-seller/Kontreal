/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kontreal.dao;

import java.util.Date;
import java.util.List;
import kontreal.entities.Concepto;
import kontreal.entities.Empresa;
import kontreal.util.HibernateUtil;
import org.hibernate.Session;

/**
 *
 * @author modima65
 */
public class ConceptoDao {

    public static Concepto searchById(Integer id) {
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();

        Concepto concepto = (Concepto) session.load(Concepto.class, id);
        return concepto;
    }

    public static List<Concepto> searchAll(Empresa empresa, int tipo) {
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();
        List<Concepto> conceptos = session.createQuery("from Concepto c where c.cuenta.empresa = :emp and c.codigo = :cod "
                + "order by c.codigo, c.nombre").setEntity("emp", empresa).setInteger("cod", tipo).list();
        return conceptos;
    }

    public static List<Concepto> searchAll() {
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();
        List<Concepto> conceptos = session.createQuery("from Concepto c order by c.codigo, c.cuenta.empresa").list();
        return conceptos;
    }

    public static void refresh(Concepto concepto) {
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();

        session.refresh(concepto);
    }

    public static void insert(Concepto concepto) {
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();

        concepto.setUpdated(new Date());
        session.save(concepto);
    }

    public static void edit(Concepto concepto) {
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();

        concepto.setUpdated(new Date());
        session.update(concepto);
    }

    public static boolean delete(Concepto concepto) {
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();
        session.refresh(concepto);

        if (concepto.getFacturas().isEmpty() && concepto.getGastos().isEmpty()) {
            concepto.setUpdated(new Date());
            session.delete(concepto);
            return true;
        } else {
            return false;
        }
    }
}
