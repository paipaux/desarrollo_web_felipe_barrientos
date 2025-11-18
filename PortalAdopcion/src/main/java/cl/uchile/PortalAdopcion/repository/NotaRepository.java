package cl.uchile.PortalAdopcion.repository;

import cl.uchile.PortalAdopcion.entity.Nota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotaRepository extends JpaRepository<Nota, Integer> {
    
    List<Nota> findByAvisoId(Integer avisoId);
}