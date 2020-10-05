package kontreal.services;

import java.util.Date;
import java.util.List;
import javax.ejb.Remote;
import kontreal.entities.LogArchivoBalanza;

/**
 *
 * @author Martin Tepostillo
 */
@Remote
public interface LogService {
    public List<LogArchivoBalanza> getByDateRangeCarga(Date desde, Date hasta);
    public List<LogArchivoBalanza> getByDateRangeConsulta(Date desde, Date hasta);
}
