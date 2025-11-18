package cl.uchile.PortalAdopcion.repository;

import cl.uchile.PortalAdopcion.entity.AvisoAdopcion;
import cl.uchile.PortalAdopcion.dto.AvisoEvaluacionDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

@Repository
public interface AvisoAdopcionRepository extends JpaRepository<AvisoAdopcion, Integer> {
    
    @Query("SELECT new cl.uchile.PortalAdopcion.dto.AvisoEvaluacionDTO(" +
           "a.id, a.fechaIngreso, a.sector, a.cantidad, a.tipo, a.edad, c.nombre, AVG(n.nota)) " +
           "FROM AvisoAdopcion a " +
           "JOIN a.comuna c " +
           "LEFT JOIN Nota n ON a.id = n.avisoId " +
           "GROUP BY a.id, a.fechaIngreso, a.sector, a.cantidad, a.tipo, a.edad, c.nombre " +
           "ORDER BY a.fechaIngreso DESC")
    List<AvisoEvaluacionDTO> findAllAvisosWithPromedio();

    @Query("SELECT DISTINCT a FROM AvisoAdopcion a " +
           "JOIN FETCH a.comuna c " +
           "JOIN FETCH c.region " +
           "LEFT JOIN FETCH a.fotos f " +
           "ORDER BY a.fechaIngreso DESC")
    List<AvisoAdopcion> findRecentWithDetails(Pageable pageable);

    @Query("SELECT a FROM AvisoAdopcion a " +
           "JOIN FETCH a.comuna c " +
           "JOIN FETCH c.region " +
           "WHERE a.id = :avisoId")
    Optional<AvisoAdopcion> findAvisoWithDetailsById(@Param("avisoId") Integer avisoId);

    @Query(value = "SELECT DATE_FORMAT(fecha_ingreso, '%Y-%m-%d') as dia, COUNT(*) as total " +
                   "FROM aviso_adopcion " +
                   "GROUP BY dia " +
                   "ORDER BY dia ASC", nativeQuery = true)
    List<Map<String, Object>> getStatsAvisosPorDia();

    @Query(value = "SELECT tipo, COUNT(*) as total " +
                   "FROM aviso_adopcion " +
                   "GROUP BY tipo", nativeQuery = true)
    List<Map<String, Object>> getStatsAvisosPorTipo();

    @Query(value = "SELECT DATE_FORMAT(fecha_ingreso, '%Y-%m') as mes, tipo, COUNT(*) as total " +
                   "FROM aviso_adopcion " +
                   "GROUP BY mes, tipo " +
                   "ORDER BY mes ASC, tipo", nativeQuery = true)
    List<Map<String, Object>> getStatsAvisosPorMes();
}