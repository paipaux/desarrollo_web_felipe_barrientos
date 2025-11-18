package cl.uchile.PortalAdopcion.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comentario")
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(nullable = false, length = 300)
    private String texto;

    @Column(nullable = false)
    private LocalDateTime fecha;

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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
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