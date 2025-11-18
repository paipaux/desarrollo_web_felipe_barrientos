package cl.uchile.PortalAdopcion.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "contactar_por")
public class ContactarPor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, columnDefinition = "ENUM('whatsapp', 'telegram', 'X', 'instagram', 'tiktok', 'otra')")
    private String nombre;

    @Column(nullable = false, length = 150)
    private String identificador;

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

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
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