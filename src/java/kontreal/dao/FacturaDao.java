/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kontreal.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import kontreal.entities.Empresa;
import kontreal.entities.Factura;
import kontreal.entities.Gasto;
import kontreal.util.HibernateUtil;
import org.hibernate.Session;

/**
 *
 * @author modima65
 */
public class FacturaDao {

    public static Factura searchById(Integer id) {
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();

        Factura factura = (Factura) session.load(Factura.class, id);
        return factura;
    }

    public static List<Factura> searchAll(Empresa empresa) {
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();

        if (empresa == null) {
            return session.createQuery("from Factura f order by f.fecha, f.folio").list();
        }

        return session.createQuery("from Factura f where f.empresa = :emp order by f.fecha, f.folio").setEntity("emp", empresa).list();
    }

    public static List<Factura> searchAll(Date desde, Date hasta) {
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();

        if (desde == null || hasta == null) {
            return new ArrayList<>();
        }

        return session.createQuery("from Factura f where f.fecha between :desde and :hasta order by f.fecha, f.folio")
                .setDate("desde", desde).setDate("hasta", hasta).list();
    }

    public static List<Factura> searchByDate(Date fecha) {
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();

        if (fecha == null) {
            return new ArrayList<>();
        }

        return session.createQuery("from Factura f where f.fecha = :fec order by f.fecha, f.folio").setDate("fec", fecha).list();
    }

    public static Object[] sumAll() {
        return sumAll(null);
    }

    public static Object[] sumAll(Empresa empresa) {
        List<Factura> facturas = searchAll(empresa);
        Object[] totales = new Object[]{facturas.size(), 0.00, 0.00, 0.00};

        for (Factura factura : facturas) {
            totales[1] = (double) totales[1] + factura.getImporte();
            totales[2] = (double) totales[2] + factura.getImporteutilizado();
        }
        totales[3] = (double) totales[1] - (double) totales[2];

        return totales;
    }

    public static List<Object[]> sumByMonth(Date desde, Date hasta) {
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();

        if (desde == null || hasta == null) {
            return new ArrayList<>();
        }

        return session.createQuery("select f.empresa, f.fecha, count(*), sum(f.importe), sum(f.importeutilizado),"
                + "sum(f.importe - f.importeutilizado) from Factura f where f.fecha between :desde and :hasta group by 1, 2 order by 1, 2")
                .setDate("desde", desde).setDate("hasta", hasta).list();
    }

    public static List<String> searchEmisores() {
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();

        return session.createQuery("select distinct f.rfc from Factura f order by 1").list();
    }

    public static void refresh(Factura factura) {
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();

        session.refresh(factura);
    }

    public static void insert(Factura factura) {
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();

        factura.setUpdated(new Date());
        session.save(factura);
    }

    public static void edit(Factura factura) {
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();

        factura.setUpdated(new Date());
        session.update(factura);
    }

    public static void delete(Factura factura) {
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();

        factura.setUpdated(new Date());
        session.delete(factura);
    }

    public static void insertGastos(Gasto gasto) {
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();

        List<Factura> facturas = session
                .createQuery("from Factura f where f.importe > f.importeutilizado order by (f.importe - f.importeutilizado) asc").list();

        double importeGasto = gasto.getImporte();

        while (importeGasto > 0) {
            Factura theRichestFactura = null;

            for (Factura factura : facturas) {
                double disponible = factura.getImporte() - factura.getImporteutilizado();

                if (disponible > 0) {
                    theRichestFactura = factura;

                    if (disponible >= importeGasto) {
                        gasto.setImporte(importeGasto);
                        factura.setImporteutilizado(factura.getImporteutilizado() + importeGasto);
                        gasto.setFactura(factura);
                        factura.getGastos().add(gasto);
                        edit(factura);
                        importeGasto = 0;
                        break;
                    }
                }
            }

            if (importeGasto > 0 && theRichestFactura != null) {
                double disponible = theRichestFactura.getImporte() - theRichestFactura.getImporteutilizado();
                Gasto sliceGasto = new Gasto(gasto.getConcepto(), gasto.getEmpresa(), theRichestFactura, disponible, new Date(),
                        gasto.getComentario(), new Date());

                theRichestFactura.setImporteutilizado(theRichestFactura.getImporte());
                theRichestFactura.getGastos().add(sliceGasto);
                edit(theRichestFactura);

                importeGasto = importeGasto - disponible;
            }
        }
    }

}
