package kontreal.entities;
// Generated 16-may-2015 17:28:00 by Hibernate Tools 4.3.1


import java.util.Date;

/**
 * Cueresultados generated by hbm2java
 */
public class Cueresultados  implements java.io.Serializable {


     private Integer idCueresultados;
     private Cuenta cuenta;
     private Date updated;

    public Cueresultados() {
    }

    public Cueresultados(Cuenta cuenta, Date updated) {
       this.cuenta = cuenta;
       this.updated = updated;
    }
   
    public Integer getIdCueresultados() {
        return this.idCueresultados;
    }
    
    public void setIdCueresultados(Integer idCueresultados) {
        this.idCueresultados = idCueresultados;
    }
    public Cuenta getCuenta() {
        return this.cuenta;
    }
    
    public void setCuenta(Cuenta cuenta) {
        this.cuenta = cuenta;
    }
    public Date getUpdated() {
        return this.updated;
    }
    
    public void setUpdated(Date updated) {
        this.updated = updated;
    }




}

