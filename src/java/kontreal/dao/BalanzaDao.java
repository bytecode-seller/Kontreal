/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kontreal.dao;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import kontreal.entities.Balanza;
import kontreal.entities.Cuenta;
import kontreal.entities.Empresa;
import kontreal.entities.Reporteesp;
import kontreal.util.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author modima65
 */
public class BalanzaDao {

    public static List<Balanza> searchBalanza(Empresa empresa, Date fecha, List<String> cuentas, int nivel) {
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();

        String nivelQry = (nivel > 0 ? "=" : "<>");

        StringBuilder regExp = new StringBuilder();
        if (cuentas.isEmpty()) {
            regExp.append("^.*-.*-.*");
        } else {
            Iterator iter = cuentas.listIterator();
            while (iter.hasNext()) {
                regExp.append(iter.next());

                if (iter.hasNext()) {
                    regExp.append("|");
                }
            }
        }

        Query query;
        if (empresa != null) {
            query = session.createQuery("from Balanza b left join fetch b.cuenta c left join fetch b.cuenta.empresa e "
                    + "where e = :emp and b.fecha = :fec and b.cuenta.nivel " + nivelQry + " :niv and b.cuenta.cuenta REGEXP :regx "
                    + "order by b.cuenta.tipo, b.cuenta.cuenta")
                    .setEntity("emp", empresa).setDate("fec", fecha).setInteger("niv", nivel).setString("regx", regExp.toString());
        } else {
            query = session.createQuery("from Balanza b left join fetch b.cuenta c left join fetch b.cuenta.empresa e "
                    + "where b.fecha = :fec and b.cuenta.nivel " + nivelQry + " :niv and b.cuenta.numeroCuenta REGEXP :regx "
                    + "order by b.cuenta.tipo, b.cuenta.numeroCuenta").setDate("fec", fecha).setInteger("niv", nivel).setString("regx", regExp.toString());
        }

        return query.list();
    }

    public static List<Balanza> searchSaldos(Empresa empresa, Cuenta cuenta, int ejercicio) {
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();

        if (empresa != null) {
            return session.createQuery("from Balanza b left join fetch b.cuenta c left join fetch b.cuenta.empresa e "
                    + "where e = :emp and c = :cue and YEAR(b.fecha) = :eje order by b.cuenta.tipo, b.cuenta.numeroCuenta")
                    .setEntity("emp", empresa).setEntity("cue", cuenta).setInteger("eje", ejercicio).list();
        } else {
            return session.createQuery("from Balanza b left join fetch b.cuenta c left join fetch b.cuenta.empresa e "
                    + "where c = :cue and YEAR(b.fecha) = :eje order by b.cuenta.tipo, b.cuenta.numeroCuenta")
                    .setEntity("cue", cuenta).setInteger("eje", ejercicio).list();
        }
    }

    public static List<Balanza> searchSaldos2(Empresa empresa, int id, Date fecha) {
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();

        Reporteesp reporte = (Reporteesp) session.load(Reporteesp.class, id);

        StringBuilder queryStr;
        queryStr = new StringBuilder("select b from Balanza b where ");
        if (empresa != null) {
            queryStr.append("this.cuenta.empresa = :emp and ");
        }
        queryStr.append("b.cuenta = this.cuenta and b.fecha = :fec order by b.cuenta.empresa, b.cuenta.tipo, b.cuenta.numeroCuenta");

        Query query = session.createFilter(reporte.getLcuentas(), queryStr.toString()).setDate("fec", fecha);
        if (empresa != null) {
            query.setEntity("emp", empresa);
        }

        return query.list();

//        List<Balanza> balanza = new ArrayList<>();
//        for (Lcuenta lc : reporte.getLcuentas()) {
//            balanza.addAll(session.createQuery("from Balanza b left join fetch b.cuenta c "
//                    + "where b.cuenta.empresa = :emp and b.cuenta = :cue and b.fecha = :fec order by b.cuenta.tipo, b.cuenta.cuenta")
//                    .setEntity("emp", empresa).setEntity("cue", lc.getCuenta()).setDate("fec", fecha).list());
//        }
//        return session.createFilter(reporte.getLcuentas(), "select b from Balanza b left join fetch b.cuenta c "
//                + "where b.cuenta.empresa = :emp and b.cuenta = this and b.fecha = :fec "
//                + "order by b.cuenta.empresa, b.cuenta.tipo, b.cuenta.cuenta").setEntity("emp", empresa).setDate("fec", fecha).list();
    }

    public static List<Date> getFechasBalanza() {
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();
        return session.createQuery("select distinct b.fecha from Balanza b order by b.fecha asc").list();
    }

    public static List<Integer> getEjerciciosBalanza() {
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();
        return session.createQuery("select distinct YEAR(b.fecha) from Balanza b order by 1 asc").list();
    }
    
    public static void insert(Balanza balanza) {
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();
        
        session.save(balanza);
    }
    
    public static Boolean isUpload(Date fecha, String empresa){
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();
        return (long)session.createQuery("select count(*) from Balanza b where b.fecha = :date and b.cuenta.empresa.nombre = :emp")
                .setDate("date", fecha)
                .setString("emp", empresa)
                .uniqueResult() > 0;
    }
    
    public static void insertOrUpdate(Balanza balanza){
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();
        
        session.saveOrUpdate(balanza);
    }
    
    public static void deleteByDate(Date date){
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();
        
        Balanza b = new Balanza();
        b.setFecha(date);
        session.delete(b);
    }
}
