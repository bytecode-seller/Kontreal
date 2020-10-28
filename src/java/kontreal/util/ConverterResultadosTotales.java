package kontreal.util;

import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import kontreal.dto.ResultadosConsultaDTO;
import kontreal.dto.ResultadosTotalesConsultaDTO;

/**
 *
 * @author Martin Tepostillo
 */
public class ConverterResultadosTotales {

    public static List<ResultadosTotalesConsultaDTO> getListResultados(List<Object[]> resultados){
        
        Function<Object[],? extends Object> a = new Function<Object[], ResultadosTotalesConsultaDTO>(){
            @Override
            public ResultadosTotalesConsultaDTO apply(Object[] t) {
                ResultadosTotalesConsultaDTO rdto = new ResultadosTotalesConsultaDTO((String)t[0], (Date) t[1], (double) t[2], (double) t[3],(double) t[4] );
                return rdto;
            }
        };
        
        List<ResultadosTotalesConsultaDTO> l = (List<ResultadosTotalesConsultaDTO>)(List<?>)  resultados.stream().map(a).collect(Collectors.toList());
        
        return l; 
    }
}
