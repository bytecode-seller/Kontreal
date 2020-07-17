package kontreal.bean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import kontreal.dao.BalanzaDao;
import kontreal.dao.CuentaDao;
import kontreal.entities.Balanza;
import kontreal.entities.Cuenta;
import kontreal.util.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.primefaces.component.growl.Growl;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Martin Tepostillo
 */
@Named
@ViewScoped
public class ArchivoBean implements Serializable{
    
    final static int CUENTA = 0;
    final static int NOMBRE_CUENTA = 1;
    final static int SALDO_INI_DEUDOR = 2;
    final static int SALDO_INI_ACREEDOR = 3;
    final static int CARGOS = 4;
    final static int ABONOS = 5;
    final static int SALDO_FIN_DEUDOR = 6;
    final static int SALDO_FIN_ACREEDOR = 7;

    public ArchivoBean(UploadedFile file) {
        this.file = file;
    }
    
    private UploadedFile file;
    private Growl growl;
    private Date fechaBalanza;
    private String empresa;
    private Integer numRegistros;
    private boolean fileUploaded;

    public boolean isFileUploaded() {
        return fileUploaded;
    }

    public void setFileUploaded(boolean fileUploaded) {
        this.fileUploaded = fileUploaded;
    }
    
    public ArchivoBean() {
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }
    
    public Growl getGrowl(){
        return this.growl;
    }
    
    public void setGrowl(Growl growl){
        this.growl = growl;
    }

    public Date getFechaBalanza() {
        return fechaBalanza;
    }

    public String getEmpresa() {
        return empresa;
    }

    public Integer getNumRegistros() {
        return numRegistros;
    }
    
    public void setNumRegistros(Integer numRegistros) {
        this.numRegistros = numRegistros;
    }
    
    public void handleFileUpload(FileUploadEvent event) throws IOException {
        
        FacesContext context = FacesContext.getCurrentInstance();
        file = event.getFile();
        String name = file.getFileName();
        System.out.println("File name: " + name);

        String type = file.getContentType();
        System.out.println("File type: " + type);

        long size = file.getSize();
        System.out.println("File size: " + size);
        
        String htmlString = "";
        //Se convierte el archivo de texto a un solo String para procesarlo con Jsoup
        try {
            htmlString = convertFileToString();
        } catch (IOException ex) {
            Logger.getLogger(ArchivoBean.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error al convertir archivo a texto");
        }
        //Se  filtran y validan los datos
        ArrayList<Balanza> registrosBalanza = new ArrayList<>();
        try {
            registrosBalanza.addAll(filterDataFromHtml(htmlString));
            ///Guardar datos
            save(registrosBalanza);
            context.addMessage("documento", new FacesMessage(FacesMessage.SEVERITY_INFO, "Carga exitosa!", "Tu archivo fue cargado con exito") );
            this.setFileUploaded(true);
            //FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "/infoArchivo.xhtml?faces-redirect=true");
        }catch(DateTimeException dte){
            context.addMessage("documento", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error en la validacion de datos", dte.getMessage()));
        }catch (Exception ex) {
            context.addMessage("documento", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error en la validacion de datos", ex.getMessage()));
        }
        System.out.println("fin archivo.");
        file = null;
    }
    
    private String convertFileToString() throws IOException{
      String html = new BufferedReader(new InputStreamReader(file.getInputstream(), StandardCharsets.UTF_8))
              .lines()
              .collect(Collectors.joining("\n"));
      
      return html;
    }
    
    private List<Balanza> filterDataFromHtml(String html) throws DateTimeException, Exception{        
        
        Document doc = Jsoup.parse(html);
        Elements elements = doc.getElementsByTag("tr");
        
        //El primer elemento <tr> es el nombre de la empresa
        String nombreEmpresa = elements.get(0).child(1).text();
        this.empresa = nombreEmpresa;
        System.out.println("nombre de la empresa: " + nombreEmpresa);
        
        //El segundo elemento <tr> contiene la fecha en la que se hizo la balanza
        String fechaBalanza = elements.get(1).child(0).text();
        
        //Filtramos la cadena del tercer tag <td> para obtener la fecha
        fechaBalanza = fechaBalanza.substring(27);
        
        //Cambiamos las iniciales del mes por su representacion numerica
        String inicialesMes = fechaBalanza.substring(3, 6);
        
        fechaBalanza = fechaBalanza.replace(inicialesMes.subSequence(0, inicialesMes.length()), DateUtils.inicialesMesPorNumero(inicialesMes).subSequence(0, 2));
        Date date = null;  
        try {
            date = new SimpleDateFormat("dd/MM/yyyy").parse(fechaBalanza);
        } catch (ParseException ex) {
            Logger.getLogger(ArchivoBean.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Parsing failed");
        }
        System.out.println(fechaBalanza+"\t"+date);  
        System.out.println("fecha balanza: " + fechaBalanza);
        
        Boolean fechaValida = DateUtils.validarUltimoDiaMes(date.getYear(), date.getMonth(), date.getDate());
        
        //Si la fecha de la balanza es invalida se lanza una Excepcion
        if (!fechaValida)
            throw new DateTimeException("La fecha proporcionada es invalida, fecha de balanza no coincide con fin de mes");
        else
            this.fechaBalanza = date;
        
        Balanza b = null;
        ArrayList<Balanza> registrosBalanza = new ArrayList<>();
        Cuenta c  = null;
        Map<String, String> cuentasNoRegistradas = new HashMap<String,String>();
        
        // Comenzamos a leer <tr> a partir del septimo que donde es empiezan los registros y hasta 8 antes del ultimo ya que no tienen datos de interes
        for(int i = 7; i< elements.size()-8; i++){
            b = new Balanza();
            c = CuentaDao.searchByCuentaAndEmpresa(elements.get(i).child(CUENTA).text(), nombreEmpresa);
            if(c != null){
                b.setCuenta(c);
                String saldoIniDeudor =elements.get(i).child(SALDO_INI_DEUDOR).text(); 
                double saldoIniD = saldoIniDeudor.isEmpty() ? 0d : Double.parseDouble(saldoIniDeudor.replace(",", ""));
                String saldoIniAcreedor =elements.get(i).child(SALDO_INI_ACREEDOR).text(); 
                double saldoIniA = saldoIniAcreedor.isEmpty() ? 0d : Double.parseDouble(saldoIniAcreedor.replace(",", ""));
                
                b.setSaldoini(saldoIniD + (-1 * saldoIniA));
                b.setCargos(Double.parseDouble(elements.get(i).child(CARGOS).text().replace(",", "")));
                b.setAbonos(-1*Double.parseDouble(elements.get(i).child(ABONOS).text().replace(",", "")));
                
                String saldoFinDeudor =elements.get(i).child(SALDO_FIN_DEUDOR).text(); 
                double saldoFinD = saldoFinDeudor.isEmpty() ? 0d : Double.parseDouble(saldoFinDeudor.replace(",", ""));
                String saldoFinAcreedor =elements.get(i).child(SALDO_FIN_ACREEDOR).text(); 
                double saldoFinA = saldoFinAcreedor.isEmpty() ? 0d : Double.parseDouble(saldoFinAcreedor.replace(",", ""));
                
                b.setSaldofin(saldoFinD + (-1 * saldoFinA));
                b.setFecha(date);
                b.setUpdated(new Date());
                registrosBalanza.add(b);
            }
            else{
                //Guardamos los datos de las cuentas que aparecen en el archivo de la balanza pero no estan registradas en la base de datos
                cuentasNoRegistradas.put(elements.get(i).child(CUENTA).text(), elements.get(i).child(NOMBRE_CUENTA).text());
            }
            c = null;
        }
        
        // Si hay cuentas no registradas en la balnza de comprobacion lanzar un error
        if(!cuentasNoRegistradas.isEmpty()){
            throw new Exception("Hay cuentas en la balanza de comprobacion que no han sido registradas para esta empresa. Actualizar antes el catalogo de cuentas.");
        }
        this.setNumRegistros(registrosBalanza.size());
        return registrosBalanza;
    }
    
    private void save(List<Balanza> toSave){
        for (Balanza balanza : toSave) {
            BalanzaDao.insert(balanza);
        }
    }
}
