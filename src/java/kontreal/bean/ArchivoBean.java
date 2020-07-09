package kontreal.bean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import kontreal.util.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Martin Tepostillo
 */
@Named
@ViewScoped
public class ArchivoBean implements Serializable{
    
    private UploadedFile file;
    
    public ArchivoBean() {
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }
    
    public void handleFileUpload(FileUploadEvent event) {
        
        FacesContext context = FacesContext.getCurrentInstance();
        file = event.getFile();
        String name = file.getFileName();
        System.out.println("File name: " + name);

        String type = file.getContentType();
        System.out.println("File type: " + type);

        long size = file.getSize();
        System.out.println("File size: " + size);
        if(file!=null)
            System.out.println("hay archivo");
         
        context.addMessage("msg", new FacesMessage("Successful",  "Archivo subido") );
        context.addMessage("myGrowl", new FacesMessage("Second Message", "Additional Message Detail"));
        
        
        System.out.println("Iniicio de archivo");
        String htmlString = "";
        try {
            htmlString = convertFileToString();
        } catch (IOException ex) {
            Logger.getLogger(ArchivoBean.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error al convertir archivo a texto");
        }
        filterDataFromHtml(htmlString);
        System.out.println("fin archivo.");
        file = null;
    }
    
    private String convertFileToString() throws IOException{
      String html = new BufferedReader(new InputStreamReader(file.getInputstream(), StandardCharsets.UTF_8))
              .lines()
              .collect(Collectors.joining("\n"));
      
      return html;
    }
    
    private String filterDataFromHtml(String html){        
        
        Document doc = Jsoup.parse(html);
        Elements elements = doc.getElementsByTag("td");
        boolean dataFound = false;
        
        //El segundo elemento <td> es el nombre de la empresa
        String nombreCuenta = elements.get(1).text();
        System.out.println("nombre de la empresa: " + nombreCuenta);
        
        //El tercer elemento <td> contiene la fecha en la que se hizo la balanza
        String fechaBalanza = elements.get(2).text();
        
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
        
        if (fechaValida) {
            System.out.println("Fecha valida");
        }else{
            System.out.println("Fecha invalida");
        }
        
        // Saltamos etiquetas <td> esta llegar a una con el texto "ACTIVO" pues es el primer registro
        ArrayList<String> data = new ArrayList<>();
        for(Element e: elements){
            if(e.text().equals("Total cuentas no impresas")){
                data.remove(data.size()-1);
                data.remove(data.size()-1);
                dataFound = false;
                break;
            }
            if(e.text().equals("ACTIVO")){
                data.add(e.previousElementSibling().text());
                dataFound = true;
            }
            if(dataFound){
                data.add(e.text());
            }
        }
    
        for(String dato: data){
            System.out.println(dato);
        }
        return "pendiente";
    }
}
