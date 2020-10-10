package kontreal.services;

import java.util.List;
import javax.ejb.Remote;
import kontreal.dto.ResultadosDTO;
import kontreal.entities.Empresa;

/**
 *
 * @author Martin Tepostillo
 */
@Remote
public interface ResultadosService {
    List<Empresa> getAllEmpresas();
    
    List<Object[]> getResultados(Empresa empresa, int ejercicio);
}
