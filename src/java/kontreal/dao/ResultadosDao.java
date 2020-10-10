/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kontreal.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import kontreal.dto.ResultadosDTO;
import kontreal.entities.Empresa;
import kontreal.util.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.joda.time.DateTime;

/**
 *
 * @author modima65
 */
public class ResultadosDao {

    public static List<Object[]> getResultados(Empresa empresa, int ejercicio) {
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();
        Map<String, Object[]> resultados = new LinkedHashMap<>();

        List<Object[]> objQuery = session.createQuery("Select b.cuenta.cuenta, b.cuenta.tipo, b.cuenta.nombre, b.fecha, "
                + "b.cargos, b.abonos, b.saldofin from Balanza b, Cueresultados c "
                + "where b.cuenta.empresa = :emp and b.cuenta = c.cuenta and YEAR(b.fecha) = :eje order by b.cuenta, b.fecha")
                .setEntity("emp", empresa).setInteger("eje", ejercicio).list();
        Date maxFecha = (Date) session.createQuery("select max(b.fecha) from Balanza b where b.cuenta.empresa = :emp and YEAR(b.fecha) = :eje")
                .setEntity("emp", empresa).setInteger("eje", ejercicio).uniqueResult();

        int maxIndices = new DateTime(maxFecha).getMonthOfYear();
        for (Object[] datarow : objQuery) {
            System.out.println("Registro...");
            System.out.println(datarow.toString());
            String cuenta = (String) datarow[0];
            int idxMes = new DateTime((Date) datarow[3]).getMonthOfYear() - 1;

            if (!resultados.containsKey(cuenta)) {
                Object[] resultado = new Object[maxIndices * 2 + 3];
                resultado[0] = cuenta;
                resultado[1] = (String) datarow[1];
                resultado[2] = (String) datarow[2];
                for (int k = 3; k < resultado.length; k++) {
                    resultado[k] = 0.0;
                }
                resultados.put(cuenta, resultado);
            }
            Object[] resultado = resultados.get(cuenta);
            int signed = resultado[1].equals("H RESULTADOS AC") ? -1 : 1;
            resultado[idxMes * 2 + 3] = (double) resultado[idxMes * 2 + 3] + ((double) datarow[4] + (double) datarow[5]) * signed;
            resultado[idxMes * 2 + 4] = (double) resultado[idxMes * 2 + 4] + ((double) datarow[6]) * signed;
            resultados.put(cuenta, resultado);
        }

        return new ArrayList<>(resultados.values());
    }
    
    public static List<ResultadosDTO> getResultadosByDTO(Empresa empresa, int ejercicio){
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();
        
        Query objQuery = session.createSQLQuery("select b.cuenta.cuenta as cuentaR, b.cuenta.tipo as tipoR, b.cuenta.nombre as nombreR, b.fecha as fechaR, "
                + "b.cargos as cargosR, b.abonos as abonosR, b.saldofin as saldofinR from Balanza b, Cueresultados c "
                + "where b.cuenta.empresa = :emp and b.cuenta = c.cuenta and YEAR(b.fecha) = :eje order by b.cuenta, b.fecha")
                .setEntity("emp", empresa)
                .setInteger("eje", ejercicio);
        
        Query q = objQuery.setResultTransformer(Transformers.aliasToBean(ResultadosDTO.class));
        
        List<ResultadosDTO> r = q.list();
        ResultadosDTO rd = r.get(0);
        System.out.println("Resultado: " + rd.getNombreR());
        
        return r;
    }
    
    public static Date getMaxfecha(Empresa empresa, int ejercicio){
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();
        
        Date maxFecha = (Date) session.createQuery("select max(b.fecha) from Balanza b where b.cuenta.empresa = :emp and YEAR(b.fecha) = :eje")
                .setEntity("emp", empresa).setInteger("eje", ejercicio).uniqueResult();
        
        return maxFecha;
    }

    public static List<Object[]> getResultados(Empresa empresa, int ejercicio, String ctaSup) {
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();
        Map<String, Object[]> resultados = new LinkedHashMap<>();

        int nivel = (int) session.createQuery("select c.nivel from Cuenta c where c.empresa = :emp and c.cuenta = :cta")
                .setEntity("emp", empresa).setParameter("cta", ctaSup).uniqueResult();
        String ctaSupLike = ctaSup.substring(0, nivel == 2 ? 3 : 8) + "%";

        List<Object[]> objQuery = session.createQuery("Select b.cuenta.cuenta, b.cuenta.tipo, b.cuenta.nombre, b.fecha, "
                + "b.cargos, b.abonos, b.saldofin from Balanza b where b.cuenta.empresa = :emp "
                + "and b.cuenta.cuenta like :cta and b.cuenta.nivel = :niv and YEAR(b.fecha) = :eje order by b.cuenta, b.fecha")
                .setEntity("emp", empresa).setParameter("cta", ctaSupLike).setInteger("niv", nivel + 1).setInteger("eje", ejercicio).list();
        Date maxFecha = (Date) session.createQuery("select max(b.fecha) from Balanza b where b.cuenta.empresa = :emp and YEAR(b.fecha) = :eje")
                .setEntity("emp", empresa).setInteger("eje", ejercicio).uniqueResult();

        int maxIndices = new DateTime(maxFecha).getMonthOfYear();
        for (Object[] datarow : objQuery) {
            String cuenta = (String) datarow[0];
            int idxMes = new DateTime((Date) datarow[3]).getMonthOfYear() - 1;

            if (!resultados.containsKey(cuenta)) {
                Object[] resultado = new Object[maxIndices * 2 + 3];
                resultado[0] = cuenta;
                resultado[1] = (String) datarow[1];
                resultado[2] = (String) datarow[2];
                for (int k = 3; k < resultado.length; k++) {
                    resultado[k] = 0.0;
                }
                resultados.put(cuenta, resultado);
            }
            Object[] resultado = resultados.get(cuenta);
            int signed = resultado[1].equals("H RESULTADOS AC") ? -1 : 1;
            resultado[idxMes * 2 + 3] = (double) resultado[idxMes * 2 + 3] + ((double) datarow[4] + (double) datarow[5]) * signed;
            resultado[idxMes * 2 + 4] = (double) resultado[idxMes * 2 + 4] + ((double) datarow[6]) * signed;
            resultados.put(cuenta, resultado);
        }

        return new ArrayList<>(resultados.values());
    }

    public static List<Object[]> getTotalesResultados(Empresa empresa, int ejercicio) {
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();
        Map<String, Object[]> resultados = new LinkedHashMap<>();

        List<Object[]> objQuery = session.createQuery("Select b.cuenta.tipo, b.fecha, sum(b.cargos), sum(b.abonos), sum(b.saldofin) "
                + "from Balanza b, Cueresultados c where b.cuenta.empresa = :emp and b.cuenta = c.cuenta and YEAR(b.fecha) = :eje "
                + "group by b.cuenta.tipo, b.fecha order by b.cuenta.tipo, b.fecha")
                .setEntity("emp", empresa).setInteger("eje", ejercicio).list();
        Date maxFecha = (Date) session.createQuery("select max(b.fecha) from Balanza b where b.cuenta.empresa = :emp and YEAR(b.fecha) = :eje")
                .setEntity("emp", empresa).setInteger("eje", ejercicio).uniqueResult();

        int maxIndices = new DateTime(maxFecha).getMonthOfYear();
        for (Object[] datarow : objQuery) {
            String tipo = (String) datarow[0];
            int idxMes = new DateTime((Date) datarow[1]).getMonthOfYear() - 1;

            if (!resultados.containsKey(tipo)) {
                Object[] resultado = new Object[maxIndices * 2 + 1];
                resultado[0] = tipo;
                for (int k = 1; k < resultado.length; k++) {
                    resultado[k] = 0.0;
                }
                resultados.put(tipo, resultado);
            }
            Object[] resultado = resultados.get(tipo);
            int signed = resultado[0].equals("H RESULTADOS AC") ? -1 : 1;
            resultado[idxMes * 2 + 1] = (double) resultado[idxMes * 2 + 1] + ((double) datarow[2] + (double) datarow[3]) * signed;
            resultado[idxMes * 2 + 2] = (double) resultado[idxMes * 2 + 2] + ((double) datarow[4]) * signed;
            resultados.put(tipo, resultado);
        }

        return new ArrayList<>(resultados.values());
    }

    public static List<Object[]> getTotalesResultados(Empresa empresa, int ejercicio, String ctaSup) {
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();
        Map<String, Object[]> resultados = new LinkedHashMap<>();

        int nivel = (int) session.createQuery("select c.nivel from Cuenta c where c.empresa = :emp and c.cuenta = :cta")
                .setEntity("emp", empresa).setParameter("cta", ctaSup).uniqueResult();
        String ctaSupLike = ctaSup.substring(0, nivel == 2 ? 3 : 8) + "%";

        List<Object[]> objQuery = session.createQuery("Select b.cuenta.tipo, b.fecha, sum(b.cargos), sum(b.abonos), sum(b.saldofin) "
                + "from Balanza b where b.cuenta.empresa = :emp and b.cuenta.cuenta like :cta and b.cuenta.nivel = :niv and YEAR(b.fecha) = :eje"
                + "group by b.cuenta.tipo, b.fecha order by b.cuenta.tipo, b.fecha")
                .setEntity("emp", empresa).setParameter("cta", ctaSupLike).setInteger("niv", nivel + 1).setInteger("eje", ejercicio).list();
        Date maxFecha = (Date) session.createQuery("select max(b.fecha) from Balanza b where b.cuenta.empresa = :emp and YEAR(b.fecha) = :eje")
                .setEntity("emp", empresa).setInteger("eje", ejercicio).uniqueResult();

        int maxIndices = new DateTime(maxFecha).getMonthOfYear();
        for (Object[] datarow : objQuery) {
            String tipo = (String) datarow[0];
            int idxMes = new DateTime((Date) datarow[1]).getMonthOfYear() - 1;

            if (!resultados.containsKey(tipo)) {
                Object[] resultado = new Object[maxIndices * 2 + 1];
                resultado[0] = tipo;
                for (int k = 1; k < resultado.length; k++) {
                    resultado[k] = 0.0;
                }
                resultados.put(tipo, resultado);
            }
            Object[] resultado = resultados.get(tipo);
            int signed = resultado[0].equals("H RESULTADOS AC") ? -1 : 1;
            resultado[idxMes * 2 + 1] = (double) resultado[idxMes * 2 + 1] + ((double) datarow[2] + (double) datarow[3]) * signed;
            resultado[idxMes * 2 + 2] = (double) resultado[idxMes * 2 + 2] + ((double) datarow[4]) * signed;
            resultados.put(tipo, resultado);
        }

        return new ArrayList<>(resultados.values());
    }

    public static List<Object[]> getUtilidadPerdida(Empresa empresa, int ejercicio) {
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();
        List<Object[]> returnQuery = new LinkedList();

        List<Object[]> objQuery = session.createQuery("Select b.fecha, sum(0 - b.cargos - b.abonos), sum(0 - b.saldofin) "
                + "from Balanza b, Cueresultados c where b.cuenta.empresa = :emp and b.cuenta = c.cuenta and YEAR(b.fecha) = :eje "
                + "group by b.fecha order by b.fecha asc").setEntity("emp", empresa).setInteger("eje", ejercicio).list();
        Date maxFecha = (Date) session.createQuery("select max(b.fecha) from Balanza b where b.cuenta.empresa = :emp and YEAR(b.fecha) = :eje")
                .setEntity("emp", empresa).setInteger("eje", ejercicio).uniqueResult();

        int maxIndices = new DateTime(maxFecha).getMonthOfYear();

        for (int k = 1; k < maxIndices + 1; k++) {
            Date lastDate = new DateTime().withMonthOfYear(k).dayOfMonth().withMaximumValue().toDate();
            Object[] newDatarow = new Object[]{lastDate, 0.0, 0.0};

            for (Object[] datarow : objQuery) {
                DateTime fecha = new DateTime((Date) datarow[0]);
                if (fecha.getMonthOfYear() == k) {
                    newDatarow = datarow;
                    break;
                }
            }
            returnQuery.add(newDatarow);
        }

        return returnQuery;
    }
}
