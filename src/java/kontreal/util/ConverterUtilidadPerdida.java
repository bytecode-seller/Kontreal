package kontreal.util;

import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import kontreal.dto.UtilidadPerdidaDTO;
import org.joda.time.DateTime;

/**
 *
 * @author Martin Tepostillo
 */
public class ConverterUtilidadPerdida {
    
    public static List<UtilidadPerdidaDTO> getListUtilidadPerdida(List<Object[]> resultados){
        
        Function<Object[],? extends Object> a = new Function<Object[], UtilidadPerdidaDTO>(){
            @Override
            public UtilidadPerdidaDTO apply(Object[] t) {
                UtilidadPerdidaDTO rdto = new UtilidadPerdidaDTO((Date) t[0], (double) t[1], (double) t[2] );
                System.out.println("fecha: " + new DateTime(rdto.getFecha()).toString("dd/MMMM/yy"));
                return rdto;
            }
        };
        
        List<UtilidadPerdidaDTO> l = (List<UtilidadPerdidaDTO>)(List<?>)  resultados.stream().map(a).collect(Collectors.toList());
        
        return l; 
    }
}
