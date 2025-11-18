package cl.uchile.PortalAdopcion.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "foto")
public class Foto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ruta_archivo", nullable = false, length = 300)
    private String rutaArchivo;

    @Column(name = "nombre_archivo", nullable = false, length = 300)
    private String nombreArchivo;

    @Column(name = "aviso_id", nullable = false, insertable = false, updatable = false)
    private Integer avisoId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aviso_id")
    private AvisoAdopcion aviso;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public Integer getAvisoId() {
        return avisoId;
    }

    public void setAvisoId(Integer avisoId) {
        this.avisoId = avisoId;
    }

    public AvisoAdopcion getAviso() {
        return aviso;
    }

    public void setAviso(AvisoAdopcion aviso) {
        this.aviso = aviso;
    }
}