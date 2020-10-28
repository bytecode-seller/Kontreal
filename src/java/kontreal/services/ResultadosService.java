package kontreal.services;

import java.util.List;
import javax.ejb.Remote;
import kontreal.dto.ResultadosDTO;
import kontreal.dto.ResultadosTotalesDTO;
import kontreal.dto.UtilidadPerdidaDTO;
import kontreal.entities.Empresa;

/**
 *
 * @author Martin Tepostillo
 */
@Remote
public interface ResultadosService {
    List<Empresa> getAllEmpresas();
    
    List<ResultadosDTO> getResultados(Empresa empresa, int ejercicio);
    
    List<ResultadosDTO> getResultados(Empresa empresa, int ejercicio, String ctaSup);
    
    List<ResultadosTotalesDTO> getResultadosTotales(Empresa empresa, int ejercicio);
    
    List<ResultadosTotalesDTO> getResultadosTotales(Empresa empresa, int ejercicio, String ctaSup);
    
    List<UtilidadPerdidaDTO> getUtilidadPerdida(Empresa empresa, int ejercicio);
}
