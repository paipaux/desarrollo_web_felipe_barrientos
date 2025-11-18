package cl.uchile.PortalAdopcion.controller;

import cl.uchile.PortalAdopcion.entity.*;
import cl.uchile.PortalAdopcion.dto.AvisoEvaluacionDTO;
import cl.uchile.PortalAdopcion.service.AvisoAdopcionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/") 
public class AvisoAdopcionController {

    private final AvisoAdopcionService avisoAdopcionService;

    @Autowired
    public AvisoAdopcionController(AvisoAdopcionService avisoAdopcionService) {
        this.avisoAdopcionService = avisoAdopcionService;
    }

    @GetMapping("/")
    public String mostrarPortada(Model model) {
        List<AvisoAdopcion> avisos = avisoAdopcionService.findRecentWithDetails();
        model.addAttribute("avisos", avisos);
        return "Portada";
    }

    @GetMapping("/listado")
    public String mostrarListadoEvaluacion(Model model) {
        List<AvisoEvaluacionDTO> avisos = avisoAdopcionService.findAllAvisosWithPromedio();
        model.addAttribute("avisos", avisos); 
        return "Listado";
    }

    @GetMapping("/estadisticas")
    public String estadisticas() {
        return "Estadísticas";
    }

    @GetMapping("/formulario")
    public String mostrarFormulario(Model model) {
        List<Region> regiones = avisoAdopcionService.findAllRegiones();
        model.addAttribute("regiones", regiones);
        model.addAttribute("aviso", new AvisoAdopcion()); 
        return "Formulario";
    }
    
    @PostMapping("/crear-aviso")
    public String crearAviso(
            @RequestParam("select-comuna") Integer comunaId,
            @RequestParam(name = "sector", required = false) String sector,
            @RequestParam("nombre") String nombre,
            @RequestParam("email") String email,
            @RequestParam("numero") String celular,
            @RequestParam("select-tipo") String tipo,
            @RequestParam("cantidad") Integer cantidad,
            @RequestParam("edad") Integer edad,
            @RequestParam("select-medida") String unidadMedida,
            @RequestParam("fechaEntrega") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaEntrega,
            @RequestParam(name = "desc", required = false) String descripcion,
            @RequestParam("fotos") MultipartFile[] fotos,
            @RequestParam(name = "contact_network", required = false) List<String> contactNetworks,
            @RequestParam(name = "contact_identifier", required = false) List<String> contactIdentifiers) throws IOException {
        
        avisoAdopcionService.crearAvisoCompleto(comunaId, sector, nombre, email, celular, tipo, cantidad, edad, unidadMedida, fechaEntrega, descripcion, fotos, contactNetworks, contactIdentifiers);
        
        return "redirect:/";
    }


    @GetMapping("/aviso/{avisoId}")
    public String verAviso(@PathVariable Integer avisoId, Model model) {
        Optional<AvisoAdopcion> avisoOpt = avisoAdopcionService.findAvisoById(avisoId);
        
        if (avisoOpt.isEmpty()) {
            return "redirect:/"; 
        }
        
        List<Foto> fotos = avisoAdopcionService.findFotosByAvisoId(avisoId);
        model.addAttribute("aviso", avisoOpt.get());
        model.addAttribute("fotos", fotos);
        return "aviso_detalle";
    }

    @PostMapping("/api/notas/evaluar/{avisoId}")
    public ResponseEntity<?> evaluarAviso(@PathVariable Integer avisoId, @RequestBody Nota nota) {
        if (nota.getNota() == null) {
            return ResponseEntity.badRequest().body("{\"error\": \"La nota es obligatoria.\"}");
        }
        Double nuevoPromedio = avisoAdopcionService.guardarNotaYRecalcularPromedio(avisoId, nota.getNota()); 
        return ResponseEntity.ok(nuevoPromedio); 
    }

    @GetMapping("/api/comunas/{regionId}")
    @ResponseBody
    public List<Comuna> apiComunas(@PathVariable Integer regionId) {
        return avisoAdopcionService.findComunasByRegionId(regionId);
    }

    @GetMapping("/api/aviso/{avisoId}/comentarios")
    @ResponseBody
    public List<Comentario> apiGetComentarios(@PathVariable Integer avisoId) {
        return avisoAdopcionService.findComentariosByAvisoId(avisoId);
    }

    @PostMapping("/api/aviso/{avisoId}/comentario")
    @ResponseBody
    public ResponseEntity<?> apiAddComentario(@PathVariable Integer avisoId, @RequestBody Comentario comentario) {
        Comentario nuevoComentario = avisoAdopcionService.saveComentario(avisoId, comentario);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoComentario);
    }

    @GetMapping("/api/stats/avisos-por-dia")
    @ResponseBody
    public ResponseEntity<?> apiStatsPorDia() {
        List<Map<String, Object>> data = avisoAdopcionService.getStatsAvisosPorDia();
        List<String> categorias = data.stream().map(item -> item.get("dia").toString()).collect(Collectors.toList());
        List<Long> valores = data.stream().map(item -> (Long) item.get("total")).collect(Collectors.toList());
        
        return ResponseEntity.ok(Map.of("categorias", categorias, "valores", valores));
    }

    @GetMapping("/api/stats/avisos-por-tipo")
    @ResponseBody
    public ResponseEntity<?> apiStatsPorTipo() {
        List<Map<String, Object>> data = avisoAdopcionService.getStatsAvisosPorTipo();
        List<Map<String, Object>> responseData = data.stream().map(item -> 
            Map.of("name", item.get("tipo").toString().substring(0, 1).toUpperCase() + item.get("tipo").toString().substring(1), 
                   "y", item.get("total"))
        ).collect(Collectors.toList());
        return ResponseEntity.ok(responseData);
    }

    @GetMapping("/api/stats/avisos-por-mes")
    @ResponseBody
    public ResponseEntity<?> apiStatsPorMes() {
        List<Map<String, Object>> data = avisoAdopcionService.getStatsAvisosPorMes();
        
        List<String> meses = data.stream().map(item -> item.get("mes").toString()).distinct().sorted().collect(Collectors.toList());
        
        Map<String, Long> datosGatos = data.stream()
            .filter(item -> "gato".equals(item.get("tipo")))
            .collect(Collectors.toMap(item -> item.get("mes").toString(), item -> (Long) item.get("total")));
            
        Map<String, Long> datosPerros = data.stream()
            .filter(item -> "perro".equals(item.get("tipo")))
            .collect(Collectors.toMap(item -> item.get("mes").toString(), item -> (Long) item.get("total")));

        List<Long> gatoDataList = meses.stream().map(mes -> datosGatos.getOrDefault(mes, 0L)).collect(Collectors.toList());
        List<Long> perroDataList = meses.stream().map(mes -> datosPerros.getOrDefault(mes, 0L)).collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
            "categorias", meses,
            "series", List.of(
                Map.of("name", "Gato", "data", gatoDataList),
                Map.of("name", "Perro", "data", perroDataList)
            )
        ));
    }
}