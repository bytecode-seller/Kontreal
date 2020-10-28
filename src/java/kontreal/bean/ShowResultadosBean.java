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
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import kontreal.dao.BalanzaDao;
import kontreal.dto.ResultadosDTO;
import kontreal.dto.ResultadosTotalesDTO;
import kontreal.dto.UtilidadPerdidaDTO;
import kontreal.entities.Empresa;
import kontreal.services.ResultadosService;
import org.joda.time.DateTime;
import org.primefaces.context.RequestContext;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;

/**
 *
 * @author modima65
 */
@ManagedBean
@ViewScoped
public class ShowResultadosBean implements Serializable {

    private final String[] titMeses = {"Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic",};
    private Map<String, ChartSeries> chartSerieMensuales;
    private Map<String, ChartSeries> chartSerieAcumulado;
    private Map<String, ChartSeries> chartSerieIngresoMes;
    private Map<String, ChartSeries> chartSerieIngresoAcu;
    private Map<String, ChartSeries> chartSerieEgresoMes;
    private Map<String, ChartSeries> chartSerieEgresoAcu;
    private double sumaMes;
    private double sumaIngresoMes;
    private double sumEgresoMes;
    private String promedioChart;
    private String tituloChart;
    private List<ResultadosDTO> resultadosData;
    private List<ResultadosTotalesDTO> resultadosTotalData;
    private List<UtilidadPerdidaDTO> utilidadesData;
    private final List<Integer> ejercicios;
    private String[] grupos;
    private Date[] meses;
    private int ejercicio;
    private String cuentaSup;
    private String cuentaNom;
    private boolean resumen;
    private List<Empresa> empresas;
    private Empresa selectedEmpresa;
    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;
    @EJB
    private ResultadosService resultadosService;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    /**
     * Creates a new instance of ShowResultados
     */
    public ShowResultadosBean() {
        System.out.println(": ----- " + this.getClass().getSimpleName() + " --- :");
        ejercicios = BalanzaDao.getEjerciciosBalanza();
        ejercicio = ejercicios.get(ejercicios.size() - 1);
        resumen = true;
    }

    @PostConstruct
    private void initData() {
        initEmpresas();
        showDialog();
    }

    private void getResultados() {
        resultadosData = resultadosService.getResultados(selectedEmpresa, ejercicio);
        resultadosTotalData = resultadosService.getResultadosTotales(this.selectedEmpresa, ejercicio);
        utilidadesData = resultadosService.getUtilidadPerdida(this.selectedEmpresa, ejercicio);

        meses = new Date[resultadosData.get(0).getResultadosAc().size()];
        for (int k = 0; k < meses.length; k++) {
            meses[k] = new DateTime().withMonthOfYear(k + 1).dayOfMonth().withMinimumValue().toDate();
        }
        grupos = new String[]{"Ingresos", "Egresos"};
    }
    
    /**
     * Gets empresas availables and initializes them.
     */
    private void initEmpresas(){
        this.empresas = resultadosService.getAllEmpresas();
    }
    
    private void showDialog(){
        RequestContext.getCurrentInstance().execute("PF('empresaDialog').show();");
    }
    
    public void selectEmpresaDialogListener(){
        FacesMessage errorMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Empresa no seleccionada", "Debe seleccionar una empresa antes de continuar");
        if(this.selectedEmpresa == null){
            FacesContext.getCurrentInstance().addMessage("selectEmpresa", errorMessage);
        }else {
            System.out.println("La empresa es: " + this.selectedEmpresa.getNombre());
            RequestContext.getCurrentInstance().execute("PF('empresaDialog').hide();");
            getResultados();
        }
            
    }

    private void updateData2() {
        resultadosData = resultadosService.getResultados(this.selectedEmpresa, ejercicio, cuentaSup);
        System.out.println("update 2");
        for (ResultadosDTO resultadosDTO : resultadosData) {
            System.out.println(resultadosDTO.getNombre());
        }
        resultadosTotalData = resultadosService.getResultadosTotales(this.selectedEmpresa, ejercicio, cuentaSup);
    }

    public void detalleListener(String elNombre, String laCuenta) {
        resumen = false;
        cuentaNom = elNombre;
        cuentaSup = laCuenta;
        updateData2();
    }

    public void resumenListener() {
        resumen = true;
        getResultados();
    }

    // Grafica Utilidad o Pérdida
    public void chart1Listener() {
        calcUtilidadesChart();
        promedioChart = String.format("$ %(,.2f", sumaMes / utilidadesData.size());
    }

    // Grafica Cuentas
    public void chart2Listener(String cuenta) {
        calcResultadosChart(cuenta);
    }

    // Grafica Total Ingresos Egresos
    public void chart3Listener(String tipo) {
        calcTotalResultadosChart(tipo, new String[]{"Mensual", "Acumulado"});
        promedioChart = String.format("$ %(,.2f", sumaMes / resultadosTotalData.get(0).getResultadosAcreedora().size());

        if (resumen) {
            tituloChart = tipo.equals("H RESULTADOS ACREEDORA") ? "Ingresos" : "Egresos";
        } else {
            tituloChart = cuentaNom;
        }
    }

    // Grafica Ingresos VS Egresos
    public void chart4Listener() {
        calcTotalResultadosChart("H RESULTADOS ACREEDORA", new String[]{"Ingreso", "Ingresos"});
        chartSerieIngresoMes = chartSerieMensuales;
        chartSerieIngresoAcu = chartSerieAcumulado;
        sumaIngresoMes = sumaMes;
        calcTotalResultadosChart("G RESULTADOS DEUDORA", new String[]{"Egreso", "Egresos"});
        chartSerieEgresoMes = chartSerieMensuales;
        chartSerieEgresoAcu = chartSerieAcumulado;
        sumEgresoMes = sumaMes;
        promedioChart = String.format("%.2f%% / %.2f%%", (sumaIngresoMes - sumEgresoMes) / sumaIngresoMes * 100,
                sumEgresoMes / sumaIngresoMes * 100);

        tituloChart = "Ingresos Vs Egresos";
    }

    private void calcResultadosChart(String cuenta) {
        chartSerieMensuales = new TreeMap<>();
        chartSerieAcumulado = new TreeMap<>();
        sumaMes = 0.0;

        ResultadosDTO resultados = new ResultadosDTO();
        for (ResultadosDTO obj : resultadosData) {
            if (obj.getCuenta().equals(cuenta)) {
                resultados = obj;
                break;
            }
        }

        tituloChart = resultados.getNombre();

        String titMensual = "Mensual";
        String titAcumulado = "Acumulada";
        for (int k = 0; k < resultados.getResultadosAc().size(); k++) {
            String mes = titMeses[k];
            double elMes = (double) resultados.getResultadosAc().get(k);
            double elAcu = (double) resultados.getResultadosFin().get(k);
            sumaMes += elMes;
            
            System.out.println("Mes: " + mes);

            if (chartSerieMensuales.containsKey(titMensual)) {
                System.out.println("Entro en suma mensual: ");
                ChartSeries chartSeries = chartSerieMensuales.get(titMensual);
                Map<Object, Number> data = chartSeries.getData();
                if (data.containsKey(mes)) {
                    double impMes = (double) data.get(mes) + elMes;
                    data.put(mes, impMes);
                    chartSeries.setData(data);
                    System.out.println("Suma para este mes: " + impMes);
                } else {
                    chartSeries.set(mes, elMes);
                    System.out.println("Suma para este mes: " + elMes);
                }
                chartSerieMensuales.put(titMensual, chartSeries);
            } else {
                ChartSeries chartSeries = new ChartSeries();
                chartSeries.setLabel(titMensual);
                chartSeries.set(mes, elMes);
                chartSerieMensuales.put(titMensual, chartSeries);
            }

            if (chartSerieAcumulado.containsKey(titAcumulado)) {
                ChartSeries chartSeries = chartSerieAcumulado.get(titAcumulado);
                Map<Object, Number> data = chartSeries.getData();
                if (data.containsKey(mes)) {
                    double manMes = (double) data.get(mes) + elAcu;
                    data.put(mes, manMes);
                    chartSeries.setData(data);
                } else {
                    chartSeries.set(mes, elAcu);
                }
                chartSerieAcumulado.put(titAcumulado, chartSeries);
            } else {
                ChartSeries chartSeries = new ChartSeries();
                chartSeries.setLabel(titAcumulado);
                chartSeries.set(mes, elAcu);
                chartSerieAcumulado.put(titAcumulado, chartSeries);
            }
        }
        sumaMes /= resultados.getResultadosAc().size();
        promedioChart = String.format("$ %(,.2f", sumaMes);
    }

    private void calcTotalResultadosChart(String tipo, String[] series) {
        chartSerieMensuales = new TreeMap<>();
        chartSerieAcumulado = new TreeMap<>();
        sumaMes = 0.0;

        ResultadosTotalesDTO resultados = new ResultadosTotalesDTO();
        for (ResultadosTotalesDTO obj : resultadosTotalData) {
            if (obj.getTipo().equals(tipo)) {
                resultados = obj;
                break;
            }
        }

        for (int k = 0; k < resultados.getResultadosAcreedora().size(); k++) {
            String mes = titMeses[k];
            double elMes = resultados.getResultadosAcreedora().get(k);;
            double elAcu = resultados.getResultadosDeudora().get(k);
            sumaMes += elMes;

            if (chartSerieMensuales.containsKey(series[0])) {
                ChartSeries chartSeries = chartSerieMensuales.get(series[0]);
                Map<Object, Number> data = chartSeries.getData();
                if (data.containsKey(mes)) {
                    double impMes = (double) data.get(mes) + elMes;
                    data.put(mes, impMes);
                    chartSeries.setData(data);
                } else {
                    chartSeries.set(mes, elMes);
                }
                chartSerieMensuales.put(series[0], chartSeries);
            } else {
                ChartSeries chartSeries = new ChartSeries();
                chartSeries.setLabel(series[0]);
                chartSeries.set(mes, elMes);
                chartSerieMensuales.put(series[0], chartSeries);
            }

            if (chartSerieAcumulado.containsKey(series[1])) {
                ChartSeries chartSeries = chartSerieAcumulado.get(series[1]);
                Map<Object, Number> data = chartSeries.getData();
                if (data.containsKey(mes)) {
                    double manMes = (double) data.get(mes) + elAcu;
                    data.put(mes, manMes);
                    chartSeries.setData(data);
                } else {
                    chartSeries.set(mes, elAcu);
                }
                chartSerieAcumulado.put(series[1], chartSeries);
            } else {
                ChartSeries chartSeries = new ChartSeries();
                chartSeries.setLabel(series[1]);
                chartSeries.set(mes, elAcu);
                chartSerieAcumulado.put(series[1], chartSeries);
            }
        }
    }

    private void calcUtilidadesChart() {
        chartSerieMensuales = new TreeMap<>();
        chartSerieAcumulado = new TreeMap<>();
        sumaMes = 0.0;
        tituloChart = "Utilidad o Pérdida";

        String titMensual = "Mensual";
        String titAcumulado = "Acumulada";
        for (UtilidadPerdidaDTO obj : utilidadesData) {
            String mes = titMeses[new DateTime(obj.getFecha()).getMonthOfYear() - 1];
            double elMes = obj.getCargosAbonos();
            double elAcu = obj.getSaldofin();
            sumaMes += elMes;

            if (chartSerieMensuales.containsKey(titMensual)) {
                ChartSeries chartSeries = chartSerieMensuales.get(titMensual);
                Map<Object, Number> data = chartSeries.getData();
                if (data.containsKey(mes)) {
                    double impMes = (double) data.get(mes) + elMes;
                    data.put(mes, impMes);
                    chartSeries.setData(data);
                } else {
                    chartSeries.set(mes, elMes);
                }
                chartSerieMensuales.put(titMensual, chartSeries);
            } else {
                ChartSeries chartSeries = new ChartSeries();
                chartSeries.setLabel(titMensual);
                chartSeries.set(mes, elMes);
                chartSerieMensuales.put(titMensual, chartSeries);
            }

            if (chartSerieAcumulado.containsKey(titAcumulado)) {
                ChartSeries chartSeries = chartSerieAcumulado.get(titAcumulado);
                Map<Object, Number> data = chartSeries.getData();
                if (data.containsKey(mes)) {
                    double manMes = (Long) data.get(mes) + elAcu;
                    data.put(mes, manMes);
                    chartSeries.setData(data);
                } else {
                    chartSeries.set(mes, elAcu);
                }
                chartSerieAcumulado.put(titAcumulado, chartSeries);
            } else {
                ChartSeries chartSeries = new ChartSeries();
                chartSeries.setLabel(titAcumulado);
                chartSeries.set(mes, elAcu);
                chartSerieAcumulado.put(titAcumulado, chartSeries);
            }
        }
    }

    public BarChartModel getChart1() {
        BarChartModel chartModel = new BarChartModel();

        for (Map.Entry entry : chartSerieMensuales.entrySet()) {
            chartModel.addSeries((ChartSeries) entry.getValue());
        }

        for (Map.Entry entry : chartSerieAcumulado.entrySet()) {
            chartModel.addSeries((ChartSeries) entry.getValue());
        }

        chartModel.setLegendPosition("nw");
        chartModel.setTitle("Ejercicio " + ejercicio);
        Axis xAxis = chartModel.getAxis(AxisType.X);
        xAxis.setLabel("Promedio: " + this.promedioChart);
        
        return chartModel;
    }

    public CartesianChartModel getChart2() {
        LineChartModel chartModel = new LineChartModel();
        LineChartSeries a = new LineChartSeries();
        LineChartSeries b = new LineChartSeries();

        for (Map.Entry entry : chartSerieAcumulado.entrySet()) {
            a.setData(((ChartSeries)entry.getValue()).getData());
        }

        
        
        for (Map.Entry entry : chartSerieMensuales.entrySet()) {
            b.setData(((ChartSeries)entry.getValue()).getData());
        }
        
        chartModel.addSeries(a);
        chartModel.addSeries(b);

        chartModel.setLegendPosition("ne");
        chartModel.setTitle("Ejercicio " + ejercicio);
        Axis xAxis = chartModel.getAxis(AxisType.X);
        xAxis.setLabel("Promedio: " + this.promedioChart);
        chartModel.setStacked(true);
        chartModel.setShowPointLabels(true);
        
        return chartModel;
    }

    public CartesianChartModel getChart3() {
        CartesianChartModel chartModel = new CartesianChartModel();

        for (Map.Entry entry : chartSerieEgresoMes.entrySet()) {
            chartModel.addSeries((ChartSeries) entry.getValue());
        }

        for (Map.Entry entry : chartSerieIngresoMes.entrySet()) {
            chartModel.addSeries((ChartSeries) entry.getValue());
        }

        return chartModel;
    }

    public CartesianChartModel getChart4() {
        CartesianChartModel chartModel = new CartesianChartModel();

        for (Map.Entry entry : chartSerieIngresoAcu.entrySet()) {
            chartModel.addSeries((ChartSeries) entry.getValue());
        }

        for (Map.Entry entry : chartSerieEgresoAcu.entrySet()) {
            chartModel.addSeries((ChartSeries) entry.getValue());
        }

        return chartModel;
    }

    public List<ResultadosDTO> getResultadosData() {
        return resultadosData;
    }

    public Date[] getMeses() {
        return meses;
    }

    public int getEjercicio() {
        return ejercicio;
    }

    public void setEjercicio(int ejercicio) {
        this.ejercicio = ejercicio;
    }

    public List<UtilidadPerdidaDTO> getUtilidadesData() {
        return utilidadesData;
    }

    public String getPromedioChart() {
        return promedioChart;
    }

    public String getTituloChart() {
        return tituloChart;
    }

    public List<ResultadosTotalesDTO> getResultadosTotalData() {
        return resultadosTotalData;
    }

    public List<Integer> getEjercicios() {
        return ejercicios;
    }

    public String[] getGrupos() {
        return grupos;
    }

    public boolean isResumen() {
        return resumen;
    }

    public String getCuentaNom() {
        return cuentaNom;
    }

    public List<Empresa> getEmpresas() {
        return empresas;
    }

    public void setEmpresas(List<Empresa> empresas) {
        this.empresas = empresas;
    }

    public Empresa getSelectedEmpresa() {
        return selectedEmpresa;
    }

    public void setSelectedEmpresa(Empresa selectedEmpresa) {
        this.selectedEmpresa = selectedEmpresa;
    }
    
}
