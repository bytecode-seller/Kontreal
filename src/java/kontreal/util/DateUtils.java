/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kontreal.util;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Dell
 */
public class DateUtils {
    
    final static String ENERO = "01";
    final static String FEBRERO = "02";
    final static String MARZO = "03";
    final static String ABRIL = "04";
    final static String MAYO = "05";
    final static String JUNIO = "06";
    final static String JULIO = "07";
    final static String AGOSTO = "08";
    final static String SEPTIEMBRE = "09";
    final static String OCTUBRE = "10";
    final static String NOVIEMBRE = "11";
    final static String DICIEMBRE = "12"; 
    
    public static String inicialesMesPorNumero(String iniciales){
        String mes = "";
        switch(iniciales){
            case "Ene":
                mes = ENERO;
                break;
            case "Feb":
                mes = FEBRERO;
                break;
            case "Mar":
                mes = MARZO;
                break;
            case "Abr":
                mes = ABRIL;
                break;
            case "May":
                mes = MAYO;
                break;
            case "Jun":
                mes = JUNIO;
                break;
            case "Jul":
                mes = JULIO;
                break;
            case "Ago":
                mes = AGOSTO;
                break;
            case "Sep":
                mes = SEPTIEMBRE;
                break;
            case "Oct":
                mes = OCTUBRE;
                break;
            case "Nov":
                mes = NOVIEMBRE;
                break;
            case "Dic":
                mes = DICIEMBRE;
                break;
            default:
                mes = ENERO;
                break;
        }
        return mes;
    }
    
    public static boolean validarUltimoDiaMes(int anio, int mes, int dia) {

        Calendar calendario = Calendar.getInstance();
        calendario.set(anio, mes-1 , 1);
        
        return  calendario.getActualMaximum(Calendar.DAY_OF_MONTH) == dia;
    }
}
