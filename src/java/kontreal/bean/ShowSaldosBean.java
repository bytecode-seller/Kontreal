/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kontreal.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import kontreal.dao.BalanzaDao;
import kontreal.dao.CuentaDao;
import kontreal.entities.Balanza;
import kontreal.entities.Cuenta;
import org.joda.time.DateTime;

/**
 *
 * @author modima65
 */
@ManagedBean
@ViewScoped
public class ShowSaldosBean implements Serializable {

    private List<Balanza> saldosData;
    private List<Balanza> saldosFilter;
    private final List<Integer> ejercicios;
    private Map<String, Cuenta> cuentasConverter;
    private Cuenta cuenta;
    private int ejercicio;
    private final String[] meses;
    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    /**
     * Creates a new instance of ShowBalanza
     */
    public ShowSaldosBean() {
        System.out.println(": ----- " + this.getClass().getSimpleName() + " --- :");
        meses = new String[] {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre", };
        ejercicios = BalanzaDao.getEjerciciosBalanza();
        ejercicio = ejercicios.get(ejercicios.size() - 1);
    }

    @PostConstruct
    private void initData() {
        cuentasConverter();
    }

    private void updateData() {
        saldosData = BalanzaDao.searchSaldos(sessionBean.getSelectedEmpresa(), cuenta, ejercicio);
    }

    public void listenerParams() {
        System.out.println("-------------- Cuenta: " + cuenta.getCuenta());
        System.out.println("-------------- Ejercicio: " + ejercicio);
        updateData();
    }

    private void cuentasConverter() {
        cuentasConverter = new TreeMap<>();
        cuentasConverter.put("- Selecciona -", null);

        for (Cuenta cue : CuentaDao.searchAll(sessionBean.getSelectedEmpresa())) {
            cuentasConverter.put(cue.getCuenta() + " - " + cue.getNombre(), cue);
        }
    }

    public String convertMonth(Date fecha) {
        DateTime date = new DateTime(fecha);
        return meses[date.getMonthOfYear() - 1].substring(0, 3) + "-" + date.getYear();
    }

    public List<Balanza> getSaldosData() {
        return saldosData;
    }

    public List<Balanza> getSaldosFilter() {
        return saldosFilter;
    }

    public void setSaldosFilter(List<Balanza> saldosFilter) {
        this.saldosFilter = saldosFilter;
    }

    public List<Integer> getEjercicios() {
        return ejercicios;
    }

    public int getEjercicio() {
        return ejercicio;
    }

    public void setEjercicio(int ejercicio) {
        this.ejercicio = ejercicio;
    }

    public Map<String, Cuenta> getCuentasConverter() {
        return cuentasConverter;
    }

    public Cuenta getCuenta() {
        return cuenta;
    }

    public void setCuenta(Cuenta cuenta) {
        this.cuenta = cuenta;
    }

}
