package kontreal.entities;
// Generated 16-may-2015 17:28:00 by Hibernate Tools 4.3.1


import java.util.Date;

/**
 * Llminicatalogo generated by hbm2java
 */
public class Lminicatalogo  implements java.io.Serializable {
     private Integer idLminicatalogo;
     private Minicatalogo minicatalogo;
     private String cuenta;
     private String nombre;
     private String tipo;
     private String regexpstr;
     private Date updated;

    public Lminicatalogo() {
    }

    public Lminicatalogo(Minicatalogo minicatalogo, String cuenta, String nombre, String tipo, String regexpstr, Date updated) {
       this.minicatalogo = minicatalogo;
       this.cuenta = cuenta;
       this.nombre = nombre;
       this.tipo = tipo;
       this.regexpstr = regexpstr;
       this.updated = updated;
    }
   
    public Integer getIdLminicatalogo() {
        return this.idLminicatalogo;
    }
    
    public void setIdLminicatalogo(Integer idLminicatalogo) {
        this.idLminicatalogo = idLminicatalogo;
    }
    public Minicatalogo getMinicatalogo() {
        return this.minicatalogo;
    }
    
    public void setMinicatalogo(Minicatalogo minicatalogo) {
        this.minicatalogo = minicatalogo;
    }
    public String getCuenta() {
        return this.cuenta;
    }
    
    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }
    public String getRegexpstr() {
        return regexpstr;
    }

    public void setRegexpstr(String regexpstr) {
        this.regexpstr = regexpstr;
    }
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    public Date getUpdated() {
        return this.updated;
    }
    
    public void setUpdated(Date updated) {
        this.updated = updated;
    }
}


