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
import kontreal.bean.ConceptosBean;
import kontreal.entities.Cuenta;

/**
 *
 * @author modima65
 */
@FacesConverter(value = "converterCuentaConcepto")
public class ConverterCuentaConcepto implements Converter, Serializable {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null) {
            return null;
        }

        ConceptosBean bean = context.getApplication().evaluateExpressionGet(context, "#{conceptosBean}", ConceptosBean.class);

        Cuenta returnedCuenta = bean.getCuentasConverter().get(value);
        return returnedCuenta;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return (value instanceof Cuenta) ? ((Cuenta) value).getCuenta() + " - " + ((Cuenta) value).getNombre() : "";
    }

}
