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
import kontreal.dao.ResultadosDao;
import org.joda.time.DateTime;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;

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
    private List<Object[]> resultadosData;
    private List<Object[]> resultadosTotalData;
    private List<Object[]> utilidadesData;
    private final List<Integer> ejercicios;
    private String[] grupos;
    private Date[] meses;
    private int ejercicio;
    private String cuentaSup;
    private String cuentaNom;
    private boolean resumen;
    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

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
        updateData1();
    }

    private void updateData1() {
        System.out.println("Selected empresa: " + sessionBean.getSelectedEmpresa());
        resultadosData = ResultadosDao.getResultados(sessionBean.getSelectedEmpresa(), ejercicio);
        resultadosTotalData = ResultadosDao.getTotalesResultados(sessionBean.getSelectedEmpresa(), ejercicio);
        utilidadesData = ResultadosDao.getUtilidadPerdida(sessionBean.getSelectedEmpresa(), ejercicio);

        meses = new Date[(resultadosData.get(0).length - 3) / 2];
        for (int k = 0; k < meses.length; k++) {
            meses[k] = new DateTime().withMonthOfYear(k + 1).dayOfMonth().withMinimumValue().toDate();
        }

        grupos = new String[]{"Ingresos", "Egresos"};
    }

    private void updateData2() {
        resultadosData = ResultadosDao.getResultados(sessionBean.getSelectedEmpresa(), ejercicio, cuentaSup);
        resultadosTotalData = ResultadosDao.getTotalesResultados(sessionBean.getSelectedEmpresa(), ejercicio, cuentaSup);
    }

    public void detalleListener(String elNombre, String laCuenta) {
        resumen = false;
        cuentaNom = elNombre;
        cuentaSup = laCuenta;
        updateData2();
    }

    public void resumenListener() {
        resumen = true;
        updateData1();
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
        promedioChart = String.format("$ %(,.2f", sumaMes / ((resultadosTotalData.get(0).length - 1) / 2));

        if (resumen) {
            tituloChart = tipo.equals("H RESULTADOS AC") ? "Ingresos" : "Egresos";
        } else {
            tituloChart = cuentaNom;
        }
    }

    // Grafica Ingresos VS Egresos
    public void chart4Listener() {
        calcTotalResultadosChart("H RESULTADOS AC", new String[]{"Ingreso", "Ingresos"});
        chartSerieIngresoMes = chartSerieMensuales;
        chartSerieIngresoAcu = chartSerieAcumulado;
        sumaIngresoMes = sumaMes;
        calcTotalResultadosChart("G RESULTADOS DE", new String[]{"Egreso", "Egresos"});
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

        Object[] resultados = new Object[resultadosData.get(0).length];
        for (Object[] obj : resultadosData) {
            if (((String) obj[0]).equals(cuenta)) {
                resultados = obj;
                break;
            }
        }

        tituloChart = (String) resultados[2];

        String titMensual = "Mensual";
        String titAcumulado = "Acumulada";
        for (int k = 0; k < (resultados.length - 3) / 2; k++) {
            String mes = titMeses[k];
            double elMes = (double) resultados[k * 2 + 3];
            double elAcu = (double) resultados[k * 2 + 4];
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
        sumaMes /= ((resultados.length - 3) / 2);
        promedioChart = String.format("$ %(,.2f", sumaMes);
    }

    private void calcTotalResultadosChart(String tipo, String[] series) {
        chartSerieMensuales = new TreeMap<>();
        chartSerieAcumulado = new TreeMap<>();
        sumaMes = 0.0;

        Object[] resultados = new Object[resultadosTotalData.get(0).length];
        for (Object[] obj : resultadosTotalData) {
            if (((String) obj[0]).equals(tipo)) {
                resultados = obj;
                break;
            }
        }

        for (int k = 0; k < (resultados.length - 1) / 2; k++) {
            String mes = titMeses[k];
            double elMes = (double) resultados[k * 2 + 1];
            double elAcu = (double) resultados[k * 2 + 2];
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
        for (Object[] obj : utilidadesData) {
            String mes = titMeses[new DateTime((Date) obj[0]).getMonthOfYear() - 1];
            double elMes = (double) obj[1];
            double elAcu = (double) obj[2];
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

    public CartesianChartModel getChart1() {
        CartesianChartModel chartModel = new CartesianChartModel();

        for (Map.Entry entry : chartSerieMensuales.entrySet()) {
            chartModel.addSeries((ChartSeries) entry.getValue());
        }

        for (Map.Entry entry : chartSerieAcumulado.entrySet()) {
            chartModel.addSeries((ChartSeries) entry.getValue());
        }

        return chartModel;
    }

    public CartesianChartModel getChart2() {
        CartesianChartModel chartModel = new CartesianChartModel();

        for (Map.Entry entry : chartSerieAcumulado.entrySet()) {
            chartModel.addSeries((ChartSeries) entry.getValue());
        }

        for (Map.Entry entry : chartSerieMensuales.entrySet()) {
            chartModel.addSeries((ChartSeries) entry.getValue());
        }

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

    public List<Object[]> getResultadosData() {
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

    public List<Object[]> getUtilidadesData() {
        return utilidadesData;
    }

    public String getPromedioChart() {
        return promedioChart;
    }

    public String getTituloChart() {
        return tituloChart;
    }

    public List<Object[]> getResultadosTotalData() {
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
}
