package kontreal.services.archivo;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import kontreal.dao.BalanzaDao;
import kontreal.dao.CuentaDao;
import kontreal.dao.EmpresaDao;
import kontreal.dto.Archivo;
import kontreal.entities.Balanza;
import kontreal.entities.Cuenta;
import kontreal.entities.Empresa;
import kontreal.exceptions.CustomException;
import kontreal.util.DateUtils;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 *
 * @author Martin Tepostillo
 */
@Named
@ViewScoped
public class ArchivoService implements Serializable{
    
    public Archivo convertirHTMLAArchivo(String html, Class<?> tipo){
        
        Archivo archivo = new Archivo(tipo);
        // 1. Parsear String HTML y asignar nombre de Archivo
        Document document =  parseHTML(html);
        archivo.setNombre(document.getElementById("nombreArchivo").text());
        
        // 2. Traer las etiquetas con informacion importante <tr>
        Elements elements = document.getElementsByTag("tr");
        
        // 3. Obtener la empresa
        String nombreEmpresa = elements.get(0).child(1).text();
        Empresa empresa = getEmpresaByName(nombreEmpresa);
        if(empresa == null){
            return archivoConErrores(archivo, kontreal.errors.Error.EMPRESA_NOT_FOUND_ERROR);
        }else{
            archivo.setEmpresa(empresa);
        }
        
        // 4. Obtener la fecha en que se recupero el Archivo
        String dateQueryString = elements.get(1).child(0).text();
        Date parsedDate;
        
        // 5. Convertir fecha de tipo String a Date
        try {
            parsedDate = parseFecha(dateQueryString);
        } catch (CustomException ex) {
            return archivoConErrores(archivo, ex.getValue() + " " + ex.getMensaje());
        }
        
        // 6. Validar que la fecha corresponde al ultimo dia del mes
        DateTime dTemp = new DateTime(parsedDate);
        Boolean dateValidation = DateUtils.validarUltimoDiaMes(dTemp.getYear(), dTemp.getMonthOfYear(), dTemp.getDayOfMonth());
        
        if(!dateValidation){
            return archivoConErrores(archivo, kontreal.errors.Error.INVALID_DATE_ERROR);
        }else{
            archivo.setConsulta(parsedDate);
        }
        
        // 7. Hacer lo mismo con la fecha de descarga
        
        String dateDownloadString = elements.get(1).child(1).text();
        Date parsedDownloadDate;
        
        try {
            parsedDownloadDate = parseFecha(dateDownloadString);
        } catch (CustomException ex) {
            return archivoConErrores(archivo, ex.getValue() + " " + ex.getMensaje());
        }
        
        archivo.setDescarga(parsedDownloadDate);
        
        // 8. Validar si existen registros con el mes al que corresponde el archivo
        if(archivoIsLoaded(parsedDate, nombreEmpresa, archivo.getClass())){
            archivo.setActualizable(true);
        }
        
        
        return fillFile(archivo, getRowsFromFileBalanza(elements, nombreEmpresa, parsedDate));
    }
    
    public Document parseHTML(String html){
        return Jsoup.parse(html);
    }
    
    public Empresa getEmpresaByName(String nombreEmpresa){
        return EmpresaDao.findByName(nombreEmpresa);
    }
    
    public Date parseFecha(String fecha) throws CustomException{
         //Filtramos la cadena del tercer tag <td> para obtener la fecha
        String fechaConsulta = fecha.substring(27);
        
        //Cambiamos las iniciales del mes por su representacion numerica
        String inicialesMes = fechaConsulta.substring(3, 6);
        
        fechaConsulta = fechaConsulta.replace(inicialesMes.subSequence(0, inicialesMes.length()), DateUtils.inicialesMesPorNumero(inicialesMes).subSequence(0, 2));
        
        //Se hace parse de tipo String a tipo Date
        Date date = null;  
        try {
            date = new SimpleDateFormat("dd/MM/yyyy").parse(fechaConsulta);
        } catch (ParseException ex) {
            System.out.println("Parsing failed");
            throw new CustomException(kontreal.errors.Error.PARSE_DATE_ERROR)
                    .entityName(Date.class.getSimpleName())
                    .value(fechaConsulta);
        }
        System.out.println(fechaConsulta+"\t"+date);  
        System.out.println("Fecha de consulta del archivo: " + fechaConsulta);
        
        return date;
    }
    
    public Archivo archivoConErrores(Archivo archivo, String error){
        archivo.getErrores().add(error);
        archivo.setValido(Boolean.FALSE);
        return archivo;
    }
    
    public boolean archivoIsLoaded(Date date, String nombreEmpresa, Class<?> tipo){
        return tipo.equals(Balanza.class) ? BalanzaDao.isUpload(date, nombreEmpresa) : CuentaDao.isUpload(date, nombreEmpresa);
    }
    
    public List getRowsFromFileByType(Elements elements, String nombreEmpresa, Class<?> tipo, Date date){
        
        return tipo == Balanza.class ? 
                getRowsFromFileBalanza(elements, nombreEmpresa, date) :
                getRowsFromFileCuenta(elements, nombreEmpresa, date);
    }
    
    public List<Object> getRowsFromFileBalanza(Elements elements, String nombreEmpresa, Date date){
        Balanza b;
        Cuenta c;
        List<Balanza> registrosBalanza = new ArrayList<>();
        Map<String, String> cuentasNoRegistradas = new HashMap<>();
        for(int i = 7; i< elements.size()-8; i++){
            b = new Balanza();
            c = CuentaDao.searchByCuentaAndEmpresa(elements.get(i).child(ArchivoConstants.NUMERO_CUENTA_BALANZA).text(), nombreEmpresa);
            if(c != null){
                b.setCuenta(c);
                String saldoIniDeudor =elements.get(i).child(ArchivoConstants.SALDO_INI_DEUDOR).text(); 
                double saldoIniD = saldoIniDeudor.isEmpty() ? 0d : Double.parseDouble(saldoIniDeudor.replace(",", ""));
                String saldoIniAcreedor =elements.get(i).child(ArchivoConstants.SALDO_INI_ACREEDOR).text(); 
                double saldoIniA = saldoIniAcreedor.isEmpty() ? 0d : Double.parseDouble(saldoIniAcreedor.replace(",", ""));
                
                b.setSaldoini(saldoIniD + (-1 * saldoIniA));
                b.setCargos(Double.parseDouble(elements.get(i).child(ArchivoConstants.CARGOS).text().replace(",", "")));
                b.setAbonos(-1*Double.parseDouble(elements.get(i).child(ArchivoConstants.ABONOS).text().replace(",", "")));
                
                String saldoFinDeudor =elements.get(i).child(ArchivoConstants.SALDO_FIN_DEUDOR).text(); 
                double saldoFinD = saldoFinDeudor.isEmpty() ? 0d : Double.parseDouble(saldoFinDeudor.replace(",", ""));
                String saldoFinAcreedor =elements.get(i).child(ArchivoConstants.SALDO_FIN_ACREEDOR).text(); 
                double saldoFinA = saldoFinAcreedor.isEmpty() ? 0d : Double.parseDouble(saldoFinAcreedor.replace(",", ""));
                
                b.setSaldofin(saldoFinD + (-1 * saldoFinA));
                b.setFecha(date);
                b.setUpdated(new Date());
                registrosBalanza.add(b);
            }
            else{
                //Guardamos los datos de las cuentas que aparecen en el archivo de la balanza pero no estan registradas en la base de datos
                cuentasNoRegistradas.put(elements.get(i).child(ArchivoConstants.NUMERO_CUENTA_BALANZA).text(), elements.get(i).child(ArchivoConstants.NOMBRE_CUENTA).text());
            }
            c = null;
        }
        List<Object> data = new ArrayList<>();
        data.add(registrosBalanza);
        data.add(cuentasNoRegistradas);
        
        return data;
    }
    
    public List getRowsFromFileCuenta(Elements elements, String nombreEmpresa, Date date){
        ArrayList<Cuenta> registrosCatalogo = new ArrayList<>();
        Empresa e = EmpresaDao.searchByName(nombreEmpresa);
        Cuenta c;
        Integer actualizados = 0;
        // Comenzamos a leer <tr> a partir del sexto que donde es empiezan los registros y hasta 6 antes del ultimo ya que no tienen datos de interes
        for (int i = 6; i < elements.size() - 6; i++) {
            c = CuentaDao.searchByCuentaAndEmpresa(elements.get(i).child(ArchivoConstants.NUMERO_CUENTA_CUENTA).text(), nombreEmpresa);

            if (c == null) {
                c = new Cuenta();
            } else {
                actualizados++; 
            }

            c.setNivel(Integer.parseInt(elements.get(i).child(ArchivoConstants.NIVEL).text()));
            c.setNumeroCuenta(elements.get(i).child(ArchivoConstants.NUMERO_CUENTA_CUENTA).text());
            c.setNombre(elements.get(i).child(ArchivoConstants.NOMBRE_CUENTA).text().toUpperCase());
            c.setTipo(elements.get(i).child(ArchivoConstants.TIPO).text().toUpperCase());
            c.setAfecta(elements.get(i).child(ArchivoConstants.AFECTA).text().toUpperCase());
            c.setDig(elements.get(i).child(ArchivoConstants.DIG).text().toUpperCase());
            c.setDivisa(elements.get(i).child(ArchivoConstants.DIVISA).text().toUpperCase());
            c.setEdo(elements.get(i).child(ArchivoConstants.EDO).text().toUpperCase());
            c.setUpdated(new Date());
            c.setFecha(date);
            c.setEmpresa(e);

            registrosCatalogo.add(c);
            c = null;
        }
        
        List<Object> data = new ArrayList<>();
        data.add(registrosCatalogo);
        data.add(actualizados);
        
        return data;
    }
    
    public Archivo fillFile(Archivo archivo, List data){
        
        if(archivo.getTipo() == Balanza.class){
            List<String> erroresBalanza = getErroresFromBalanza((Map<String, String>) data.get(1));
            
            if(!erroresBalanza.isEmpty()){
                archivo.setErrores(erroresBalanza);
                archivo.setValido(Boolean.FALSE);
            }else{
                archivo.setRegistros((List) data.get(0));
            }
        }else{
            archivo.setRegistros((List) data.get(0));
            archivo.setActualizados((Integer) data.get(1));
        }
        return archivo;
    }
    
    public List<String> getErroresFromBalanza(Map<String, String> cuentasNoRegistradas){
        List<String> errores = new ArrayList<>();
        if(!cuentasNoRegistradas.isEmpty()){
            for (Map.Entry<String, String> entry : cuentasNoRegistradas.entrySet()) {
                errores.add(kontreal.errors.Error.CUENTA_NOT_FOUND_ERROR + ": " + entry.getKey() + " " + entry.getValue());
            }
        }
        return errores;
    }
}
