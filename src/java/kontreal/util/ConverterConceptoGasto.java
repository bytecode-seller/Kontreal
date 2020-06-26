/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kontreal.util;

import java.io.Serializable;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import kontreal.bean.GastosBean;
import kontreal.entities.Concepto;

/**
 *
 * @author modima65
 */
@FacesConverter(value = "converterConceptoGasto")
public class ConverterConceptoGasto implements Converter, Serializable {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null) {
            return null;
        }

        GastosBean bean = context.getApplication().evaluateExpressionGet(context, "#{gastosBean}", GastosBean.class);

        Concepto returnedConcepto = bean.getConceptosConverter().get(value);
        return returnedConcepto;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return (value instanceof Concepto) ? ((Concepto) value).getNombre() : "";
    }

}
