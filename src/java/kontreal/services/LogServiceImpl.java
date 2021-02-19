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
    public List<LogArchivoBalanza> getTwoMontsAgo() {
        
        Date twoMonthsAgo = Date.from(ZonedDateTime.now().minusMonths(2).toInstant());
        
        return LogArchivoBalanzaDao.findByStartDate(twoMonthsAgo);
    }
    
    @Override
    public List<LogArchivoBalanza> getByDateWithCritera(Date bCarga, Date eCarga, Date bConsulta, Date eConsulta) {
        return LogArchivoBalanzaDao.getByDataWithCriteria(bCarga, eCarga, bConsulta, eConsulta);
    }
}
