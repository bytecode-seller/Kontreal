package kontreal.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import kontreal.dao.BalanzaDao;
import kontreal.dao.CuentaDao;
import kontreal.dao.EmpresaDao;
import kontreal.entities.Balanza;
import kontreal.entities.Cuenta;
import kontreal.entities.Empresa;
import kontreal.services.SaldosService;
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
    private List<Empresa> empresas;
    private List<Cuenta> cuentas;
    private Empresa selectedEmpresa;
    boolean changeEmpresa;
    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;
    @EJB
    private SaldosService saldosService;

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
        System.out.println("Init data");
        this.empresas = new ArrayList<>();
        this.cuentas = new ArrayList<>();
        this.saldosData = new ArrayList<>();
        this.empresas.addAll( EmpresaDao.searchAll());
        this.cuentas.addAll(CuentaDao.findAll());
    }

    private void updateData() {
//        if(cuenta != null){
//            saldosData.clear();
//            List<Balanza> lb = saldosService.getSaldos(selectedEmpresa, cuenta, ejercicio);
//            saldosData.addAll(lb);
//        }else{
//            System.out.println("Entro en else: ");
//            saldosData.clear();
//            List<Balanza> lb = saldosService.getSaldos(selectedEmpresa, ejercicio);
//            saldosData.addAll(lb);
//            
//            
//        }
        
        if(this.selectedEmpresa != null){
            cuentas.addAll(CuentaDao.searchAll(selectedEmpresa));
            saldosData.clear();
            List<Balanza> lb = saldosService.getSaldos(selectedEmpresa, ejercicio);
            saldosData.addAll(lb);
        }
        
        if(this.selectedEmpresa != null && this.cuenta != null){
            saldosData.clear();
            List<Balanza> lb = saldosService.getSaldos(selectedEmpresa, cuenta, ejercicio);
            saldosData.addAll(lb);
        }
        if(this.cuenta != null){
           saldosData.clear();
           List<Balanza> lb = saldosService.getSaldos(selectedEmpresa, cuenta, ejercicio);
           saldosData.addAll(lb);
        }
        if(!saldosData.isEmpty())
                System.out.println("Primer saldo: " + saldosData.get(0).getCargos());
        
    }
    
    public void listenerChangeEmpresa(){
        this.cuentas.clear();
    }

    public void listenerParams() {
        if(this.selectedEmpresa != null)
            System.out.println("Nombre empresa seleccionada saldos: " + getSelectedEmpresa().getNombre());
        //System.out.println("-------------- Cuenta: " + cuenta.getCuenta());
        //System.out.println("-------------- Ejercicio: " + ejercicio);
        updateData();
    }
//
//    private void cuentasConverter() {
//        cuentasConverter = new TreeMap<>();
//        cuentasConverter.put("- Selecciona -", null);
//
//        if(this.selectedEmpresa == null){
//            for (Cuenta cue : CuentaDao.findAll()) {
//                cuentasConverter.put(cue.getNumeroCuenta() + " - " + cue.getNombre(), cue);
//            }
//        }
//        else
//        for (Cuenta cue : CuentaDao.searchAll(this.selectedEmpresa)) {
//            cuentasConverter.put(cue.getNumeroCuenta() + " - " + cue.getNombre(), cue);
//        }
//    }

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

    public List<Empresa> getEmpresas(){
        return this.empresas;
    }
    
    public void setEmpresas( List<Empresa> empresas){
        this.empresas = empresas;
    }

    public Empresa getSelectedEmpresa() {
        return selectedEmpresa;
    }

    public void setSelectedEmpresa(Empresa selectedEmpresa) {
        this.selectedEmpresa = selectedEmpresa;
    }

    public List<Cuenta> getCuentas() {
        return cuentas;
    }

    public void setCuentas(List<Cuenta> cuentas) {
        this.cuentas = cuentas;
    }
    
}
