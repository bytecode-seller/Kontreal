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
import kontreal.bean.ShowSaldosBean;
import kontreal.entities.Cuenta;

/**
 *
 * @author modima65
 */
@FacesConverter(value = "converterCuentaSaldos")
public class ConverterCuentaSaldos implements Converter, Serializable {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null) {
            return null;
        }

        ShowSaldosBean bean = context.getApplication().evaluateExpressionGet(context, "#{showSaldosBean}", ShowSaldosBean.class);

        Cuenta returnedCuenta = bean.getCuentasConverter().get(value);
        return returnedCuenta;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return (value instanceof Cuenta) ? ((Cuenta) value).getNumeroCuenta() + " - " + ((Cuenta) value).getNombre() : "";
    }

}
