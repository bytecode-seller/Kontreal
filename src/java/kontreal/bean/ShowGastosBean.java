/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kontreal.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import kontreal.dao.FacturaDao;
import kontreal.dao.GastoDao;
import kontreal.entities.Empresa;
import org.joda.time.DateTime;

/**
 *
 * @author modima65
 */
@ManagedBean
@ViewScoped
public class ShowGastosBean implements Serializable {

    private List<Object[]> facturasData;
    private List<Object[]> gastosData;
    private List<Object[]> transferData;
    private Date desde;
    private Date hasta;
    private Double[] totalesFacturas;
    private Double[] totalesGastos;
    private Double[] totalesTransfer;
    private Double[] totalesEstadosCuenta;
    private Map<String, double[]> estadoCuenta;
    private final String[] meses;
    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    /**
     * Creates a new instance of ShowBalanza
     */
    public ShowGastosBean() {
        System.out.println(": ----- " + this.getClass().getSimpleName() + " --- :");
        meses = new String[]{"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre",};
        desde = DateTime.now().minusMonths(1).dayOfMonth().withMaximumValue().toDate();
        hasta = DateTime.now().hourOfDay().withMaximumValue().toDate();
    }

    @PostConstruct
    private void initData() {
        updateData();
    }

    private void updateData() {
        facturasData = FacturaDao.sumByMonth(desde, hasta);
        gastosData = GastoDao.sumByMonth(desde, hasta);
        transferData = GastoDao.sumTransfers(desde, hasta);
        estadoCuenta = new HashMap<>();

        totalesFacturas = new Double[] {0.0, 0.0, 0.0, 0.0};
        for (Object[] object : facturasData) {
            totalesFacturas[0] += (long) object[2];
            totalesFacturas[1] += (double) object[3];
            totalesFacturas[2] += (double) object[4];
            totalesFacturas[3] += (double) object[5];

            String empresaKey = ((Empresa) object[0]).getNombre();
            if (!estadoCuenta.containsKey(empresaKey)) {
                estadoCuenta.put(empresaKey, new double[] {0.0, 0.0, 0.0, 0.0});
            }
            double[] totalesEstados = estadoCuenta.get(empresaKey);
            totalesEstados[0] += 1;
            totalesEstados[1] += (double) object[3] * -1;
            totalesEstados[3] += (double) object[3] * -1;
            estadoCuenta.put(empresaKey, totalesEstados);
        }

        totalesGastos = new Double[] {0.0, 0.0};
        for (Object[] object : gastosData) {
            totalesGastos[0] += (long) object[3];
            totalesGastos[1] += (double) object[4];

            String empresaKey = ((Empresa) object[0]).getNombre();
            if (!estadoCuenta.containsKey(empresaKey)) {
                estadoCuenta.put(empresaKey, new double[] {0.0, 0.0, 0.0, 0.0});
            }
            double[] totalesEstados = estadoCuenta.get(empresaKey);
            totalesEstados[0] += 1;
            totalesEstados[2] += (double) object[4];
            totalesEstados[3] += (double) object[4];
            estadoCuenta.put(empresaKey, totalesEstados);
        }

        totalesEstadosCuenta = new Double[] {0.0, 0.0, 0.0, 0.0};
        for (double[] values : estadoCuenta.values()) {
            totalesEstadosCuenta[0] += values[0];
            totalesEstadosCuenta[1] += values[1];
            totalesEstadosCuenta[2] += values[2];
            totalesEstadosCuenta[3] += values[3];
        }

        totalesTransfer = new Double[] {0.0, 0.0};
        for (Object[] object : transferData) {
            totalesTransfer[0] += (long) object[3];
            totalesTransfer[1] += (double) object[4];
        }

    }

    public void fechasListener() {
        updateData();
    }

    public void dropFilters() {
        desde = DateTime.now().minusMonths(4).dayOfMonth().withMaximumValue().toDate();
        hasta = DateTime.now().hourOfDay().withMaximumValue().toDate();
        updateData();
    }

    public String convertMonth(Date fecha) {
        DateTime date = new DateTime(fecha);
        return meses[date.getMonthOfYear() - 1].substring(0, 3);
    }

    public List<Object[]> getGastosData() {
        return gastosData;
    }

    public List<Object[]> getFacturasData() {
        return facturasData;
    }

    public Double[] getTotalesGastos() {
        return totalesGastos;
    }

    public Double[] getTotalesFacturas() {
        return totalesFacturas;
    }

    public Double[] getTotalesTransfer() {
        return totalesTransfer;
    }

    public List<Object[]> getTransferData() {
        return transferData;
    }

    public Double[] getTotalesEstadosCuenta() {
        return totalesEstadosCuenta;
    }

    public Map<String, double[]> getEstadoCuenta() {
        return estadoCuenta;
    }

    public Date getDesde() {
        return desde;
    }

    public void setDesde(Date desde) {
        this.desde = desde;
    }

    public Date getHasta() {
        return hasta;
    }

    public void setHasta(Date hasta) {
        this.hasta = hasta;
    }

}
