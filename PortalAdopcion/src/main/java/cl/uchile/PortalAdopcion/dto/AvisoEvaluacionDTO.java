package cl.uchile.PortalAdopcion.dto;

import cl.uchile.PortalAdopcion.entity.TipoMascota;
import java.time.LocalDateTime;

public class AvisoEvaluacionDTO {
    
    private Integer id;
    private LocalDateTime fechaPublicacion;
    private String sector;
    private String cantidadTipoEdad; 
    private String comuna;
    private String notaPromedio;

    public AvisoEvaluacionDTO(Integer id, LocalDateTime fechaPublicacion, String sector, Integer cantidad, TipoMascota tipo, Integer edad, String comuna, Double notaPromedio) {
        this.id = id;
        this.fechaPublicacion = fechaPublicacion;
        this.sector = sector;
        this.comuna = comuna;
        
        this.cantidadTipoEdad = cantidad + " " + tipo.name() + " " + edad + " " + (edad > 1 ? "años" : "año");
        
        this.notaPromedio = (notaPromedio != null) ? String.format("%.1f", notaPromedio) : "-";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDateTime fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getCantidadTipoEdad() {
        return cantidadTipoEdad;
    }

    public void setCantidadTipoEdad(String cantidadTipoEdad) {
        this.cantidadTipoEdad = cantidadTipoEdad;
    }

    public String getComuna() {
        return comuna;
    }

    public void setComuna(String comuna) {
        this.comuna = comuna;
    }

    public String getNotaPromedio() {
        return notaPromedio;
    }

    public void setNotaPromedio(String notaPromedio) {
        this.notaPromedio = notaPromedio;
    }
}