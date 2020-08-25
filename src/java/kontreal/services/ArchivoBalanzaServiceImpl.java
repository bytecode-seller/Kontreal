package kontreal.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.Stateless;
import kontreal.dao.BalanzaDao;
import kontreal.dao.CuentaDao;
import kontreal.dao.EmpresaDao;
import kontreal.dto.ArchivoBalanzaDto;
import kontreal.entities.Balanza;
import kontreal.entities.Cuenta;
import kontreal.entities.Empresa;
import kontreal.exceptions.CustomException;
import kontreal.util.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import kontreal.errors.Error;

/**
 *
 * @author Martin Tepostillo
 */
@Stateless
public class ArchivoBalanzaServiceImpl implements ArchivoBalanzaService{
     
    final static int CUENTA = 0;
    final static int NOMBRE_CUENTA = 1;
    final static int SALDO_INI_DEUDOR = 2;
    final static int SALDO_INI_ACREEDOR = 3;
    final static int CARGOS = 4;
    final static int ABONOS = 5;
    final static int SALDO_FIN_DEUDOR = 6;
    final static int SALDO_FIN_ACREEDOR = 7;
    
    @Override
    public ArchivoBalanzaDto filterDataFromHtml(String html){        
        
        Document doc = Jsoup.parse(html);
        Elements elements = doc.getElementsByTag("tr");
        
        ArrayList<String> erroresArchivo = new ArrayList<>();
        ArchivoBalanzaDto datosArchivo = new ArchivoBalanzaDto();
        
        datosArchivo.setNombreArchivo(doc.getElementById("nombreArchivo").text());
        
        //El primer elemento <tr> es el nombre de la empresa
        String nombreEmpresa = elements.get(0).child(1).text();
        System.out.println("nombre de la empresa: " + nombreEmpresa);
        
        if(!empresaExists(nombreEmpresa)){
            erroresArchivo.add(kontreal.errors.Error.EMPRESA_NOT_FOUND_ERROR);
            datosArchivo.setValid(false);
            datosArchivo.setErrores(erroresArchivo);
            return datosArchivo;
        }
        
        datosArchivo.setNombreEmpresa(nombreEmpresa);
        
        //El segundo elemento <tr> contiene la fecha en la que se hizo la balanza
        String fechaBalanza = elements.get(1).child(0).text();
        
        Date fechaParseada = null;
        
        try {
            fechaParseada = parseFecha(fechaBalanza);
        } catch (CustomException ex) {
            erroresArchivo.add(ex.getValue() + " " + ex.getMensaje());
            datosArchivo.setErrores(erroresArchivo);
            datosArchivo.setValid(false);
            return datosArchivo;
        }
        
        Boolean fechaValida = DateUtils.validarUltimoDiaMes(fechaParseada.getYear(), fechaParseada.getMonth(), fechaParseada.getDate());
        
        //Si la fecha de la balanza es invalida se lanza una Excepcion
        if (!fechaValida){
            erroresArchivo.add(Error.INVALID_DATE_ERROR);
            datosArchivo.setValid(false);
            datosArchivo.setErrores(erroresArchivo);
            return datosArchivo;
        }
            
        
        //Si hay registros de balanza con la misma fecha se lanza excepcion
        if(balanzaIsLoaded(fechaParseada, nombreEmpresa)) 
                datosArchivo.setToUpdate(true);
        
        datosArchivo.setFechaBalanza(fechaParseada);
        
        Balanza b = null;
        Cuenta c  = null;
        Map<String, String> cuentasNoRegistradas = new HashMap<String,String>();
        ArrayList<Balanza> registrosBalanza = new ArrayList<>();
        
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
                b.setFecha(fechaParseada);
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
            for (Map.Entry<String, String> entry : cuentasNoRegistradas.entrySet()) {
                erroresArchivo.add(kontreal.errors.Error.CUENTA_NOT_FOUND_ERROR + ": " + entry.getKey() + " " + entry.getValue());
            }
            datosArchivo.setValid(false);
            datosArchivo.setErrores(erroresArchivo);
            return datosArchivo;
        }
        
        datosArchivo.setRegistrosBalanza(registrosBalanza);
        datosArchivo.setNumeroRegistros(registrosBalanza.size());
        
        return datosArchivo;
    }
    
    public boolean empresaExists(String nombreEmpresa){
        Empresa e = EmpresaDao.searchByName(nombreEmpresa);
        System.out.println("nombre de la empresa: " + nombreEmpresa);
        
        return e!=null;
    }
    
    public boolean balanzaIsLoaded(Date date, String empresa){
        
        boolean uploaded = false;
        
        if (BalanzaDao.isUpload(date, empresa))
            uploaded = true;
        
        return uploaded;
    }
    
    public Date parseFecha(String fecha) throws CustomException{
         //Filtramos la cadena del tercer tag <td> para obtener la fecha
        String fechaBalanza = fecha.substring(27);
        
        //Cambiamos las iniciales del mes por su representacion numerica
        String inicialesMes = fechaBalanza.substring(3, 6);
        
        fechaBalanza = fechaBalanza.replace(inicialesMes.subSequence(0, inicialesMes.length()), DateUtils.inicialesMesPorNumero(inicialesMes).subSequence(0, 2));
        
        //Se hace parse de tipo String a tipo Date
        Date date = null;  
        try {
            date = new SimpleDateFormat("dd/MM/yyyy").parse(fechaBalanza);
        } catch (ParseException ex) {
            System.out.println("Parsing failed");
            throw new CustomException(kontreal.errors.Error.PARSE_DATE_ERROR)
                    .entityName(Date.class.getSimpleName())
                    .value(fechaBalanza);
        }
        System.out.println(fechaBalanza+"\t"+date);  
        System.out.println("fecha balanza: " + fechaBalanza);
        
        return date;
    }
}
