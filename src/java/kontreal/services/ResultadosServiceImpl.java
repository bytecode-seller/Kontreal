package kontreal.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import kontreal.dao.EmpresaDao;
import kontreal.dao.ResultadosDao;
import kontreal.dto.ResultadosConsultaDTO;
import kontreal.dto.ResultadosDTO;
import kontreal.dto.ResultadosTotalesConsultaDTO;
import kontreal.dto.ResultadosTotalesDTO;
import kontreal.dto.UtilidadPerdidaDTO;
import kontreal.entities.Empresa;
import kontreal.util.ConverterResultados;
import kontreal.util.ConverterResultadosTotales;
import kontreal.util.ConverterUtilidadPerdida;
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
    public ArrayList<ResultadosDTO> getResultados(Empresa empresa, int ejercicio) {
        
        Date maxFecha = ResultadosDao.getMaxfecha(empresa, ejercicio);
        
        List<Object[]> resultadosParciales = ResultadosDao.getResultadosByDTO(empresa, ejercicio);
        
        List<ResultadosConsultaDTO> resultados = ConverterResultados.getListResultados(resultadosParciales);
         
        Map<String, ResultadosDTO> res = new LinkedHashMap<>();
        
        System.out.println("Primera cuenta: " + resultados.get(0).getNombre());
        
        int maxIndices = new DateTime(maxFecha).getMonthOfYear();
        for (ResultadosConsultaDTO datarow : resultados) {
            System.out.println(datarow.getNombre());
            String cuenta = datarow.getNumeroCuenta();
            int idxMes = new DateTime(datarow.getFecha()).getMonthOfYear() - 1;

            if (!res.containsKey(cuenta)) {
                ResultadosDTO resultado = new ResultadosDTO();
                resultado.setCuenta(cuenta);
                resultado.setTipo(datarow.getTipo());
                resultado.setNombre(datarow.getNombre());
                for (int k = 0; k < maxIndices; k++) {
                    resultado.addResultadosAc(0.0);
                    resultado.addResultadosFin(0.0);
                }
                res.put(cuenta, resultado);
            }
            ResultadosDTO resultado = res.get(cuenta);
            int signed = resultado.getNombre().equals("H RESULTADOS ACREEDORA") ? -1 : 1;
            double sumaCargosAbonos = resultado.getResultadosAc().get(idxMes) + datarow.getCargos() + datarow.getAbonos();
            double sumaSaldoFin = resultado.getResultadosFin().get(idxMes) +  datarow.getSaldofin();
            
            System.out.println("Suma cargos abonos para el mes " + idxMes + ": " + (sumaCargosAbonos * signed));
            System.out.println("Suma saldoFin para el mes " + idxMes + ": " + (sumaSaldoFin * signed));
            resultado
                    .getResultadosAc()
                    .set(idxMes, sumaCargosAbonos * signed);
            resultado
                    .getResultadosFin()
                    .set(idxMes, sumaSaldoFin * signed);
            res.put(cuenta, resultado);
        }
        
        return new ArrayList<>(res.values());
    }
    
    @Override
    public List<ResultadosDTO> getResultados(Empresa empresa, int ejercicio, String ctaSup) {
        
        Date maxFecha = ResultadosDao.getMaxfecha(empresa, ejercicio);
        
        List<Object[]> resultadosParciales = ResultadosDao.getResultadosByDTO(empresa, ejercicio, ctaSup);
        
        List<ResultadosConsultaDTO> resultados = ConverterResultados.getListResultados(resultadosParciales);
         
        Map<String, ResultadosDTO> res = new LinkedHashMap<>();
        
        
        int maxIndices = new DateTime(maxFecha).getMonthOfYear();
        for (ResultadosConsultaDTO datarow : resultados) {
            System.out.println(datarow.getNombre());
            String cuenta = datarow.getNumeroCuenta();
            int idxMes = new DateTime(datarow.getFecha()).getMonthOfYear() - 1;

            if (!res.containsKey(cuenta)) {
                ResultadosDTO resultado = new ResultadosDTO();
                resultado.setCuenta(cuenta);
                resultado.setTipo(datarow.getTipo());
                resultado.setNombre(datarow.getNombre());
                for (int k = 0; k < maxIndices; k++) {
                    resultado.addResultadosAc(0.0);
                    resultado.addResultadosFin(0.0);
                }
                res.put(cuenta, resultado);
            }
            ResultadosDTO resultado = res.get(cuenta);
            int signed = resultado.getNombre().equals("H RESULTADOS ACREEDORA") ? -1 : 1;
            double sumaCargosAbonos = resultado.getResultadosAc().get(idxMes) + datarow.getCargos() + datarow.getAbonos();
            double sumaSaldoFin = resultado.getResultadosFin().get(idxMes) +  datarow.getSaldofin();
            
            System.out.println("Suma cargos abonos para el mes " + idxMes + ": " + (sumaCargosAbonos * signed));
            System.out.println("Suma saldoFin para el mes " + idxMes + ": " + (sumaSaldoFin * signed));
            
            resultado
                    .getResultadosAc()
                    .set(idxMes, sumaCargosAbonos * signed);
            resultado
                    .getResultadosFin()
                    .set(idxMes, sumaSaldoFin * signed);
            res.put(cuenta, resultado);
        }
        
        return new ArrayList<>(res.values());
    }

    @Override
    public List<ResultadosTotalesDTO> getResultadosTotales(Empresa empresa, int ejercicio) {
        
        List<Object[]> resultadosParciales = ResultadosDao.getTotalesResultadosByDTO(empresa, ejercicio);
        Date maxFecha = ResultadosDao.getMaxfecha(empresa, ejercicio);
        List<ResultadosTotalesConsultaDTO> resultados = ConverterResultadosTotales.getListResultados(resultadosParciales);
        Map<String, ResultadosTotalesDTO> res = new LinkedHashMap<>();
        
        int maxIndices = new DateTime(maxFecha).getMonthOfYear();
        for (ResultadosTotalesConsultaDTO r : resultados) {
            String tipo = r.getTipo();
            int idxMes = new DateTime( r.getFecha()).getMonthOfYear() - 1;

            if (!res.containsKey(tipo)) {
                ResultadosTotalesDTO resultado = new ResultadosTotalesDTO();
                resultado.setTipo(tipo);
                for (int k = 0; k < maxIndices; k++) {
                    resultado.addResultadosTotalesAcreedora(0.0);
                    resultado.addResultadosTotalesDeudora(0.0);
                }
                res.put(tipo, resultado);
            }
            ResultadosTotalesDTO resultado = res.get(tipo);
            int signed = resultado.getTipo().equals("H RESULTADOS AC") ? -1 : 1;
            resultado.getResultadosAcreedora().set(idxMes, resultado.getResultadosAcreedora().get(idxMes) + (r.getSumatoriaCargos() + r.getSumatoriaAbonos()) * signed);
            resultado.getResultadosDeudora().set(idxMes, resultado.getResultadosDeudora().get(idxMes) + (r.getSumatoriaSaldofin() * signed));
            res.put(tipo, resultado);
        }
        
        return new ArrayList<>(res.values());
    }
    
    @Override
    public List<ResultadosTotalesDTO> getResultadosTotales(Empresa empresa, int ejercicio, String ctaSup) {
        
        List<Object[]> resultadosParciales = ResultadosDao.getTotalesResultadosByDTO(empresa, ejercicio, ctaSup);
        Date maxFecha = ResultadosDao.getMaxfecha(empresa, ejercicio);
        List<ResultadosTotalesConsultaDTO> resultados = ConverterResultadosTotales.getListResultados(resultadosParciales);
        Map<String, ResultadosTotalesDTO> res = new LinkedHashMap<>();
        
        int maxIndices = new DateTime(maxFecha).getMonthOfYear();
        for (ResultadosTotalesConsultaDTO r : resultados) {
            String tipo = r.getTipo();
            int idxMes = new DateTime( r.getFecha()).getMonthOfYear() - 1;

            if (!res.containsKey(tipo)) {
                ResultadosTotalesDTO resultado = new ResultadosTotalesDTO();
                resultado.setTipo(tipo);
                for (int k = 0; k < maxIndices; k++) {
                    resultado.addResultadosTotalesAcreedora(0.0);
                    resultado.addResultadosTotalesDeudora(0.0);
                }
                res.put(tipo, resultado);
            }
            ResultadosTotalesDTO resultado = res.get(tipo);
            int signed = resultado.getTipo().equals("H RESULTADOS AC") ? -1 : 1;
            resultado.getResultadosAcreedora().set(idxMes, resultado.getResultadosAcreedora().get(idxMes) + (r.getSumatoriaCargos() + r.getSumatoriaAbonos()) * signed);
            resultado.getResultadosDeudora().set(idxMes, resultado.getResultadosDeudora().get(idxMes) + (r.getSumatoriaSaldofin() * signed));
            res.put(tipo, resultado);
        }
        
        return new ArrayList<>(res.values());
    }

    @Override
    public List<UtilidadPerdidaDTO> getUtilidadPerdida(Empresa empresa, int ejercicio) {
        
        List<Object[]> resultadosParciales = ResultadosDao.getUtilidadPerdidabyDTO(empresa, ejercicio);
        List<UtilidadPerdidaDTO> resultados = ConverterUtilidadPerdida.getListUtilidadPerdida(resultadosParciales);
        
        return resultados;
    }

}
