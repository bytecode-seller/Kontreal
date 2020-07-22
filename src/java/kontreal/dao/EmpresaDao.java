/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kontreal.dao;

import java.util.List;
import kontreal.entities.Empresa;
import kontreal.util.HibernateUtil;
import org.hibernate.Session;

/**
 *
 * @author modima65
 */
public class EmpresaDao {
    public static List<Empresa> searchAll() {
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();
        List<Empresa> empresa = session.createQuery("from Empresa e order by e.nombre").list();
        return empresa;
    }

    public static Empresa searchByName(String nombreEmpresa){
        HibernateUtil.beginTransaction();
        Session session = HibernateUtil.getSession();
        
        return (Empresa) session.createQuery("from Empresa e where e.nombre = :name").setString("name", nombreEmpresa).uniqueResult();
    }
}
