package kontreal.services.log;

import java.util.Date;
import java.util.List;
import kontreal.entities.LogArchivoBalanza;

/**
 *
 * @author Martin Tepostillo
 */
public interface LogService {
    public List<LogArchivoBalanza> getTwoMontsAgo();
    public List<LogArchivoBalanza> getByDateWithCritera(Date bCarga, Date eCarga, Date bConsulta, Date eConsulta);
}
