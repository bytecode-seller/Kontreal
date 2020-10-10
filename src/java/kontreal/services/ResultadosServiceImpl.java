package kontreal.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import kontreal.dao.EmpresaDao;
import kontreal.dao.ResultadosDao;
import kontreal.dto.ResultadosDTO;
import kontreal.entities.Empresa;
import org.joda.time.DateTime;

/**
 *
 * @author Martin Tepostillo
 */
@Stateless
public class ResultadosServiceImpl implements ResultadosService {

    @Override
    public List<Empresa> getAllEmpresas() {
        return EmpresaDao.searchAll();
    }

    @Override
    public ArrayList<Object[]> getResultados(Empresa empresa, int ejercicio) {
        
        Date maxFecha = ResultadosDao.getMaxfecha(empresa, ejercicio);
        
        List<ResultadosDTO> resultados = ResultadosDao.getResultadosByDTO(empresa, ejercicio);
        
        Map<String, Object[]> res = new LinkedHashMap<>();
        
        System.out.println("Primera cuenta: " + resultados.get(0).getNombreR());
        
        int maxIndices = new DateTime(maxFecha).getMonthOfYear();
        for (ResultadosDTO datarow : resultados) {
            System.out.println("Registro...");
            System.out.println(datarow.toString());
            String cuenta = datarow.getCuentaR();
            int idxMes = new DateTime(datarow.getFechaR()).getMonthOfYear() - 1;

            if (!res.containsKey(cuenta)) {
                Object[] resultado = new Object[maxIndices * 2 + 3];
                resultado[0] = cuenta;
                resultado[1] = datarow.getTipoR();
                resultado[2] = datarow.getNombreR();
                for (int k = 3; k < resultado.length; k++) {
                    resultado[k] = 0.0;
                }
                res.put(cuenta, resultado);
            }
            Object[] resultado = res.get(cuenta);
            int signed = resultado[1].equals("H RESULTADOS AC") ? -1 : 1;
            resultado[idxMes * 2 + 3] = (double) resultado[idxMes * 2 + 3] + ((double) datarow.getCargosR() + (double) datarow.getAbonosR()) * signed;
            resultado[idxMes * 2 + 4] = (double) resultado[idxMes * 2 + 4] + ((double) datarow.getSaldofinR()) * signed;
            res.put(cuenta, resultado);
        }

        return new ArrayList<>(res.values());
    }

}
