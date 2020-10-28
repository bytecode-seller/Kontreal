package kontreal.util;

import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import kontreal.dto.ResultadosConsultaDTO;

/**
 *
 * @author Martin Tepostillo
 */
public class ConverterResultados {

    public static List<ResultadosConsultaDTO> getListResultados(List<Object[]> resultados){
        
        Function<Object[],? extends Object> a = new Function<Object[], ResultadosConsultaDTO>(){
            @Override
            public ResultadosConsultaDTO apply(Object[] t) {
                ResultadosConsultaDTO rdto = new ResultadosConsultaDTO((String)t[0], (String)t[1], (String) t[2],(Date) t[3], (double) t[4], (double) t[5],(double) t[5] );
                return rdto;
            }
        };
        
        List<ResultadosConsultaDTO> l = (List<ResultadosConsultaDTO>)(List<?>)  resultados.stream().map(a).collect(Collectors.toList());
        
        return l; 
    }
}
