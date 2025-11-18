package cl.uchile.PortalAdopcion.repository;

import cl.uchile.PortalAdopcion.entity.Comuna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ComunaRepository extends JpaRepository<Comuna, Integer> {
    
    List<Comuna> findByRegionId(Integer regionId);
}