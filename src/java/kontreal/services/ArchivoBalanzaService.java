package kontreal.services;

import javax.ejb.Remote;
import kontreal.dto.ArchivoBalanzaDto;

/**
 *
 * @author Martin Tepostillo
 */
@Remote
public interface ArchivoBalanzaService {
    public ArchivoBalanzaDto filterDataFromHtml(String html);
}
