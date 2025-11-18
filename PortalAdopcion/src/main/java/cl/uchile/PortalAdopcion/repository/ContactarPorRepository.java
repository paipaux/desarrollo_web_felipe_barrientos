package cl.uchile.PortalAdopcion.repository;

import cl.uchile.PortalAdopcion.entity.ContactarPor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactarPorRepository extends JpaRepository<ContactarPor, Integer> {
    
}