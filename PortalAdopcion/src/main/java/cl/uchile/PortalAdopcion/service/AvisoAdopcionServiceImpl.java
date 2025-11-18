package cl.uchile.PortalAdopcion.service;

import cl.uchile.PortalAdopcion.entity.*;
import cl.uchile.PortalAdopcion.repository.*;
import cl.uchile.PortalAdopcion.dto.AvisoEvaluacionDTO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class AvisoAdopcionServiceImpl implements AvisoAdopcionService {

    private final AvisoAdopcionRepository avisoAdopcionRepository;
    private final NotaRepository notaRepository;
    private final RegionRepository regionRepository;
    private final ComunaRepository comunaRepository;
    private final FotoRepository fotoRepository;
    private final ComentarioRepository comentarioRepository;
    private final ContactarPorRepository contactarPorRepository;

    private static final String UPLOAD_PATH = "src/main/resources/static/uploads/";

    @Autowired
    public AvisoAdopcionServiceImpl(
            AvisoAdopcionRepository avisoAdopcionRepository, 
            NotaRepository notaRepository,
            RegionRepository regionRepository,
            ComunaRepository comunaRepository,
            FotoRepository fotoRepository,
            ComentarioRepository comentarioRepository,
            ContactarPorRepository contactarPorRepository) {
        
        this.avisoAdopcionRepository = avisoAdopcionRepository;
        this.notaRepository = notaRepository;
        this.regionRepository = regionRepository;
        this.comunaRepository = comunaRepository;
        this.fotoRepository = fotoRepository;
        this.comentarioRepository = comentarioRepository;
        this.contactarPorRepository = contactarPorRepository;
    }

    @Override
    public List<AvisoEvaluacionDTO> findAllAvisosWithPromedio() {
        return avisoAdopcionRepository.findAllAvisosWithPromedio();
    }
    
    @Override
    @Transactional
    public Double guardarNotaYRecalcularPromedio(Integer avisoId, Integer notaValor) {
        
        if (notaValor == null || notaValor < 1 || notaValor > 7) {
            throw new IllegalArgumentException("La nota debe estar entre 1 y 7.");
        }
        
        AvisoAdopcion aviso = avisoAdopcionRepository.findById(avisoId)
                .orElseThrow(() -> new IllegalArgumentException("Aviso no encontrado."));
        
        Nota nuevaNota = new Nota();
        nuevaNota.setAviso(aviso);
        nuevaNota.setNota(notaValor);
        
        notaRepository.save(nuevaNota);

        return calcularPromedio(avisoId);
    }
    
    private Double calcularPromedio(Integer avisoId) {
        List<Nota> notas = notaRepository.findByAvisoId(avisoId);
        
        if (notas.isEmpty()) {
            return null;
        }
        
        double suma = notas.stream().mapToInt(Nota::getNota).sum();
        
        return Math.round((suma / notas.size()) * 10.0) / 10.0;
    }

    @Override
    public AvisoAdopcion save(AvisoAdopcion aviso) {
        return avisoAdopcionRepository.save(aviso);
    }

    @Override
    public List<AvisoAdopcion> findRecentWithDetails() {
        Pageable limit = PageRequest.of(0, 5);
        return avisoAdopcionRepository.findRecentWithDetails(limit);
    }

    @Override
    public List<Region> findAllRegiones() {
        return regionRepository.findAll();
    }

    @Override
    public List<Comuna> findComunasByRegionId(Integer regionId) {
        return comunaRepository.findByRegionId(regionId);
    }

    @Override
    public Optional<AvisoAdopcion> findAvisoById(Integer avisoId) {
        return avisoAdopcionRepository.findAvisoWithDetailsById(avisoId);
    }

    @Override
    public List<Foto> findFotosByAvisoId(Integer avisoId) {
        return fotoRepository.findByAvisoId(avisoId);
    }

    @Override
    public List<Comentario> findComentariosByAvisoId(Integer avisoId) {
        return comentarioRepository.findByAvisoIdOrderByFechaDesc(avisoId);
    }

    @Override
    @Transactional
    public Comentario saveComentario(Integer avisoId, Comentario comentario) {
        String nombre = comentario.getNombre();
        String texto = comentario.getTexto();
        if (nombre == null || nombre.trim().length() < 3 || nombre.trim().length() > 80) {
            throw new IllegalArgumentException("El nombre es obligatorio (entre 3 y 80 caracteres).");
        }
        if (texto == null || texto.trim().length() < 5) {
            throw new IllegalArgumentException("El comentario es obligatorio (mínimo 5 caracteres).");
        }

        AvisoAdopcion aviso = avisoAdopcionRepository.findById(avisoId)
                .orElseThrow(() -> new IllegalArgumentException("Aviso no encontrado: " + avisoId));
        
        comentario.setAviso(aviso);
        comentario.setFecha(LocalDateTime.now());
        
        return comentarioRepository.save(comentario);
    }
    
    @Override
    @Transactional
    public void crearAvisoCompleto(
            Integer comunaId, String sector, String nombre, String email, 
            String celular, String tipo, Integer cantidad, Integer edad, 
            String unidadMedida, LocalDateTime fechaEntrega, String descripcion, 
            MultipartFile[] fotos, List<String> contactNetworks, List<String> contactIdentifiers) throws IOException {

        validarAviso(nombre, email, celular, tipo, cantidad, edad, unidadMedida, fechaEntrega, comunaId);

        Comuna comuna = comunaRepository.findById(comunaId)
                .orElseThrow(() -> new IllegalArgumentException("Comuna no válida: " + comunaId));

        AvisoAdopcion aviso = new AvisoAdopcion();
        aviso.setComuna(comuna);
        aviso.setSector(sector);
        aviso.setNombre(nombre);
        aviso.setEmail(email);
        aviso.setCelular(celular);
        
        aviso.setTipo(TipoMascota.valueOf(tipo)); 
        aviso.setUnidadMedida(UnidadMedida.valueOf(unidadMedida));
        
        aviso.setCantidad(cantidad);
        aviso.setEdad(edad);
        aviso.setFechaEntrega(fechaEntrega);
        aviso.setDescripcion(descripcion);
        aviso.setFechaIngreso(LocalDateTime.now());
        
        AvisoAdopcion savedAviso = avisoAdopcionRepository.save(aviso);

        List<Foto> fotosGuardadas = new ArrayList<>();
        for (MultipartFile file : fotos) {
            if (file.isEmpty() || !allowedFile(file.getOriginalFilename())) {
                continue;
            }

            String originalFilename = file.getOriginalFilename();
            String uniqueFilename = System.currentTimeMillis() + "_" + originalFilename;
            Path destinationFile = Paths.get(UPLOAD_PATH).resolve(uniqueFilename).toAbsolutePath();

            Files.createDirectories(Paths.get(UPLOAD_PATH));
            Files.copy(file.getInputStream(), destinationFile);

            Foto foto = new Foto();
            foto.setAviso(savedAviso);
            foto.setNombreArchivo(uniqueFilename);
            foto.setRutaArchivo("/uploads/" + uniqueFilename); 
            fotosGuardadas.add(foto);
        }
        fotoRepository.saveAll(fotosGuardadas);

        if (contactNetworks != null && contactIdentifiers != null) {
            List<ContactarPor> contactos = new ArrayList<>();
            for (int i = 0; i < contactNetworks.size(); i++) {
                String network = contactNetworks.get(i);
                String identifier = contactIdentifiers.get(i);
                if (network != null && !network.isEmpty() && identifier != null && !identifier.isEmpty()) {
                    ContactarPor contacto = new ContactarPor();
                    contacto.setAviso(savedAviso);
                    contacto.setNombre(network.equals("x") ? "X" : network);
                    contacto.setIdentificador(identifier);
                    contactos.add(contacto);
                }
            }
            contactarPorRepository.saveAll(contactos);
        }
    }

    private void validarAviso(String nombre, String email, String celular, String tipo, Integer cantidad, Integer edad, String unidadMedida, LocalDateTime fechaEntrega, Integer comunaId) {
        if (nombre == null || nombre.trim().length() < 3 || nombre.trim().length() > 200) {
            throw new IllegalArgumentException("Nombre inválido");
        }
        if (email == null || !Pattern.compile("^[\\w.]+@[a-zA-Z_]+?\\.[a-zA-Z]{2,3}$").matcher(email).matches()) {
            throw new IllegalArgumentException("Email inválido");
        }
        if (celular == null || !Pattern.compile("^\\+\\d{3}\\.\\d{8}$").matcher(celular).matches()) {
            throw new IllegalArgumentException("Número inválido");
        }
        if (tipo == null || tipo.trim().isEmpty()) {
            throw new IllegalArgumentException("Tipo inválido");
        }
        if (cantidad == null || cantidad < 1) {
            throw new IllegalArgumentException("Cantidad inválida");
        }
        if (edad == null || edad < 1) {
            throw new IllegalArgumentException("Edad inválida");
        }
        if (unidadMedida == null || unidadMedida.trim().isEmpty()) {
            throw new IllegalArgumentException("Unidad de medida inválida");
        }
        if (fechaEntrega == null || fechaEntrega.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Fecha de entrega inválida");
        }
        if (comunaId == null) {
            throw new IllegalArgumentException("Comuna inválida");
        }
    }

    private boolean allowedFile(String filename) {
        if (filename == null || !filename.contains(".")) {
            return false;
        }
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        return List.of("png", "jpg", "jpeg", "gif").contains(extension);
    }

    @Override
    public List<Map<String, Object>> getStatsAvisosPorDia() {
        return avisoAdopcionRepository.getStatsAvisosPorDia();
    }

    @Override
    public List<Map<String, Object>> getStatsAvisosPorTipo() {
        return avisoAdopcionRepository.getStatsAvisosPorTipo();
    }

    @Override
    public List<Map<String, Object>> getStatsAvisosPorMes() {
        return avisoAdopcionRepository.getStatsAvisosPorMes();
    }
}