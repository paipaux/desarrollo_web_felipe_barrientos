package cl.uchile.PortalAdopcion.repository;

import cl.uchile.PortalAdopcion.entity.Foto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FotoRepository extends JpaRepository<Foto, Integer> {
    
    List<Foto> findByAvisoId(Integer avisoId);
}