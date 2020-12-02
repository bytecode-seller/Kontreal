package kontreal.services;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import kontreal.dao.LogArchivoBalanzaDao;
import kontreal.entities.LogArchivoBalanza;

/**
 *
 * @author Martin Tepostillo
 */
@Named
@ViewScoped
public class LogServiceImpl implements LogService, Serializable{

    @Override
    public List<LogArchivoBalanza> getByDateRangeCarga(Date desde, Date hasta) {
        if(desde != null)
            System.out.println("desde: " + desde);
        else
            System.out.println("desde es null");
        if(hasta != null)
            System.out.println("hasta: " + hasta);
        else
            System.out.println("hasta es null");
        
        Date twoMonthsAgo = Date.from(ZonedDateTime.now().minusMonths(2).toInstant());
        
        if(desde==null && hasta==null)
            return LogArchivoBalanzaDao.getByBeginDateConsulta(twoMonthsAgo);
        
        if(desde == null && hasta != null)
            return LogArchivoBalanzaDao.getByDateLimitCarga(hasta);
        
        if(desde != null && hasta == null)
            return LogArchivoBalanzaDao.getByBeginDateCarga(desde);
        
        if(desde.compareTo(hasta) > 0)
            System.out.println("Error fecha: fecha inicial es mayor a fecha final");
        
        return LogArchivoBalanzaDao.getBetweenDateCarga(desde, hasta);
    }
    
    @Override
    public List<LogArchivoBalanza> getByDateRangeConsulta(Date desde, Date hasta) {
        if(desde != null)
            System.out.println("desde: " + desde);
        else
            System.out.println("desde es null");
        if(hasta != null)
            System.out.println("hasta: " + hasta);
        else
            System.out.println("hasta es null");
        
        Date twoMonthsAgo = Date.from(ZonedDateTime.now().minusMonths(2).toInstant());
        
        if(desde==null && hasta==null)
            return LogArchivoBalanzaDao.getByBeginDateConsulta(twoMonthsAgo);
        
        if(desde == null && hasta != null)
            return LogArchivoBalanzaDao.getByDateLimitConsulta(hasta);
        
        if(desde != null && hasta == null)
            return LogArchivoBalanzaDao.getByBeginDateConsulta(desde);
        
        if(desde.compareTo(hasta) > 0)
            System.out.println("Error fecha: fecha inicial es mayor a fecha final");
        
        return LogArchivoBalanzaDao.getBetweenDateConsulta(desde, hasta);
    }
}
