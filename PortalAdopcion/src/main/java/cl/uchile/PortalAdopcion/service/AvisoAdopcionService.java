package cl.uchile.PortalAdopcion.service;

import cl.uchile.PortalAdopcion.entity.AvisoAdopcion;
import cl.uchile.PortalAdopcion.entity.Comentario;
import cl.uchile.PortalAdopcion.entity.Comuna;
import cl.uchile.PortalAdopcion.entity.Foto;
import cl.uchile.PortalAdopcion.entity.Region;
import cl.uchile.PortalAdopcion.dto.AvisoEvaluacionDTO;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AvisoAdopcionService {

    List<AvisoEvaluacionDTO> findAllAvisosWithPromedio();
    Double guardarNotaYRecalcularPromedio(Integer avisoId, Integer nota);

    List<AvisoAdopcion> findRecentWithDetails();

    List<Region> findAllRegiones();
    List<Comuna> findComunasByRegionId(Integer regionId);

    void crearAvisoCompleto(
            Integer comunaId, String sector, String nombre, String email, 
            String celular, String tipo, Integer cantidad, Integer edad, 
            String unidadMedida, LocalDateTime fechaEntrega, String descripcion, 
            MultipartFile[] fotos, List<String> contactNetworks, List<String> contactIdentifiers) throws IOException;
    
    AvisoAdopcion save(AvisoAdopcion aviso);


    Optional<AvisoAdopcion> findAvisoById(Integer avisoId);
    List<Foto> findFotosByAvisoId(Integer avisoId);

    List<Comentario> findComentariosByAvisoId(Integer avisoId);
    Comentario saveComentario(Integer avisoId, Comentario comentario);
    
    List<Map<String, Object>> getStatsAvisosPorDia();
    List<Map<String, Object>> getStatsAvisosPorTipo();
    List<Map<String, Object>> getStatsAvisosPorMes();
}