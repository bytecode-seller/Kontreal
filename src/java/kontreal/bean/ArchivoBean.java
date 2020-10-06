package kontreal.bean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import kontreal.dao.BalanzaDao;
import kontreal.dto.ArchivoBalanzaDto;
import kontreal.entities.Balanza;
import kontreal.services.ArchivoBalanzaService;
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
    
    @EJB
    private ArchivoBalanzaService archivoBalanzaService;
    private UploadedFile file;
    private Date fechaBalanza;
    private String empresa;
    private Integer numRegistros;
    private boolean fileUploaded;
    private ArrayList<ArchivoBalanzaDto> archivosBalanza = new ArrayList<>();
    private ArrayList<FacesMessage> errores = new ArrayList<>();
    private AtomicInteger fileCounter = new AtomicInteger();

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

    public ArrayList<ArchivoBalanzaDto> getArchivosBalanza() {
        return archivosBalanza;
    }

    public void setArchivosBalanza(ArrayList<ArchivoBalanzaDto> archivosBalanza) {
        this.archivosBalanza = archivosBalanza;
    }
    
    
    
    public void handleFileUpload(FileUploadEvent event){
        
        file = event.getFile();
        String name = file.getFileName();
        System.out.println("File name: " + name);

        String type = file.getContentType();
        System.out.println("File type: " + type);

        long size = file.getSize();
        System.out.println("File size: " + size);
        
        RequestContext reqContext = RequestContext.getCurrentInstance();
        String htmlString = "<h1 id=\"nombreArchivo\">" + name + "</h1>";
        //Se convierte el archivo de texto a un solo String para procesarlo con Jsoup
        try {
            htmlString += convertFileToString();
        } catch (IOException ex) {
            System.out.println("Error al convertir archivo a texto");
        }
        //Se  filtran y validan los datos
        archivosBalanza.add(archivoBalanzaService.filterDataFromHtml(htmlString));
        
        if(fileCounter.decrementAndGet() == 0) {
            for (int i = 0; i< archivosBalanza.size();i++) {
                
                if(!archivosBalanza.get(i).isValid()){
                    for (String error : archivosBalanza.get(i).getErrores()) {
                        FacesContext.getCurrentInstance().addMessage("documento", new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error al leer documento",archivosBalanza.get(i).getNombreArchivo() + "\n" + error));
                        
                    }
                    archivosBalanza.remove(i);
                }
                
            }
            if(!archivosIsEmpty()){
                reqContext.execute("PF('confirmDialog').show();");
                FacesMessage msg = new FacesMessage("Successful",  " uploaded");
                FacesContext.getCurrentInstance().addMessage("documento", msg);
                
            }
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
    
    public void save(int index){
        if(archivosBalanza.get(index).isToUpdate())
                BalanzaDao.deleteByDate(archivosBalanza.get(index).getFechaBalanza());
        
        for (Balanza balanza : archivosBalanza.get(index).getRegistrosBalanza()) {
                BalanzaDao.insertOrUpdate(balanza);
        }
        FacesContext.getCurrentInstance().addMessage("documento", new FacesMessage(FacesMessage.SEVERITY_INFO, "Carga exitosa!", "Tu archivo fue cargado con exito") );
        this.setFileUploaded(true);
        archivosBalanza.remove(index);
        archivosBalanzaIsEmpty();
    }
    
    public void cancel(){
        RequestContext.getCurrentInstance().execute("PF('confirmDialog').hide();");
        this.archivosBalanza.clear();
    }
    
    public void disableUpload(int index){
        this.archivosBalanza.remove(index);
        archivosBalanzaIsEmpty();
    }
    
    public boolean archivosIsEmpty(){    
        return this.archivosBalanza.isEmpty();
    }
    
    public void setFileCounter(ActionEvent e){
        String length  = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("size").toString();
        System.out.println("Numero de archivos a subir: " + length );
        if(length != null) {
            fileCounter.set(Integer.parseInt(length));
        }
    }
    
    private void archivosBalanzaIsEmpty(){
        if(this.archivosBalanza.isEmpty()){
            RequestContext.getCurrentInstance().execute("PF('confirmDialog').hide();");
        }
    }
}
