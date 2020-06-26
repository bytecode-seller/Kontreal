/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kontreal.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import kontreal.entities.Gasto;
import kontreal.util.HibernateUtil;
import org.hibernate.Session;

/**
 *
 * @author modima65
 */
public class GastoDao {

    public static Gasto searchById(Integer id) {
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();

        Gasto gasto = (Gasto) session.load(Gasto.class, id);
        return gasto;
    }

    public static void refresh(Gasto gasto) {
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();

        session.refresh(gasto);
    }

    public static List<Gasto> searchAll(Date desde, Date hasta) {
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();

        if (desde == null || hasta == null) {
            return new ArrayList<>();
        }

        return session.createQuery("from Gasto g where g.fecha between :desde and :hasta order by g.fecha, g.concepto")
                .setDate("desde", desde).setDate("hasta", hasta).list();
    }

    public static List<Object[]> sumByMonth(Date desde, Date hasta) {
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();

        if (desde == null || hasta == null) {
            return new ArrayList<>();
        }

        return session.createQuery("select g.empresa, g.fecha, g.concepto, count(*), sum(g.importe) from Gasto g "
                + "where g.factura.fecha between :desde and :hasta group by 1, 2, 3 order by 1, 2, 3")
                .setDate("desde", desde).setDate("hasta", hasta).list();
    }

    public static List<Object[]> sumTransfers(Date desde, Date hasta) {
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();

        if (desde == null || hasta == null) {
            return new ArrayList<>();
        }

        return session.createQuery("select g.factura.empresa, g.concepto.cuenta.empresa, g.fecha, count(*), sum(g.importe) from Gasto g "
                + "where g.factura.fecha between :desde and :hasta group by 1, 2, 3 order by 1, 2, 3")
                .setDate("desde", desde).setDate("hasta", hasta).list();
    }

}
