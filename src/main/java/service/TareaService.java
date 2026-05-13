package service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exception.AccesoDenegadoException;
import exception.TareaFueraDeFechaException;
import model.Entrega;
import model.EstadoTarea;
import model.Materia;
import model.Rol;
import model.Tarea;
import model.Usuario;
import rb.lms.AppState;
import repository.EntregaRepository;
import repository.HistorialRepository;
import repository.TareaRepository;

public class TareaService {

    private Map<String, Entrega> entregas = new HashMap<>();
    private final EntregaRepository entregaRepo = new EntregaRepository();
    private final TareaRepository tareaRepo = new TareaRepository();
    private HistorialRepository historialRepo;

    private String claveEntrega(Usuario estudiante, Tarea tarea) {
        return estudiante.getEmail() + ":" + tarea.getTitulo();
    }

    public TareaService() {
        try {
            for (Entrega e : entregaRepo.getAll()) {
                entregas.put(claveEntrega(e.getEstudiante(), e.getTarea()), e);
            }
        } catch (Exception e) {
        }
    }

    public void setHistorialRepo(HistorialRepository repo) {
        this.historialRepo = repo;
    }

    public Tarea crearTarea(String titulo, String descripcion, Materia materia,
                            Usuario profesor, LocalDate fechaLimite) {
        if (profesor.getRol() != Rol.PROFESOR) {
            throw new AccesoDenegadoException("Solo profesores pueden crear tareas");
        }
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El titulo no puede estar vacio");
        }

        Tarea tarea = new Tarea(titulo, descripcion, fechaLimite);
        materia.agregarTarea(tarea);
        tareaRepo.guardar(tarea, materia.getNombre());
        AppState.materiaService.guardar(materia);

        registrarHistorial(model.HistorialEntrada.TipoEvento.TAREA_CREADA,
            profesor.getEmail(), "Tarea creada: " + titulo);

        return tarea;
    }

    public Tarea crearTarea(String titulo, Materia materia, Usuario profesor, LocalDate fechaLimite) {
        return crearTarea(titulo, "", materia, profesor, fechaLimite);
    }

    public void actualizarTarea(Tarea tarea, String titulo, String descripcion, Usuario profesor, Materia materia) {
        if (profesor.getRol() != Rol.PROFESOR) {
            throw new AccesoDenegadoException("Solo profesores pueden editar tareas");
        }
        if (!materia.getProfesor().equals(profesor)) {
            throw new AccesoDenegadoException("No eres el profesor de esta materia");
        }
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El titulo no puede estar vacio");
        }

        String tituloAnterior = tarea.getTitulo();
        if (!tituloAnterior.equals(titulo.trim())) {
            tareaRepo.eliminar(tituloAnterior, materia.getNombre());
            tarea.setTitulo(titulo.trim());
        }
        tarea.setDescripcion(descripcion);
        tareaRepo.guardar(tarea, materia.getNombre());
        AppState.materiaService.guardar(materia);
    }

    public void eliminarTarea(Tarea tarea, Usuario profesor, Materia materia) {
        if (profesor.getRol() != Rol.PROFESOR) {
            throw new AccesoDenegadoException("Solo profesores pueden eliminar tareas");
        }
        if (!materia.getProfesor().equals(profesor)) {
            throw new AccesoDenegadoException("No eres el profesor de esta materia");
        }

        materia.eliminarTarea(tarea.getTitulo());
        tareaRepo.eliminar(tarea.getTitulo(), materia.getNombre());
        AppState.materiaService.guardar(materia);
    }

    public Entrega entregarTarea(Usuario estudiante, Materia materia, Tarea tarea,
                                 String contenido, String nombreArchivo) {
        if (estudiante.getRol() != Rol.ESTUDIANTE) {
            throw new AccesoDenegadoException("Solo estudiantes pueden entregar tareas");
        }
        if (!materia.getEstudiantes().contains(estudiante)) {
            throw new AccesoDenegadoException("El estudiante no esta inscrito en la materia");
        }
        if (tarea.estaFueraDeFecha()) {
            throw new TareaFueraDeFechaException("La tarea vencio el " + tarea.getFechaLimite());
        }
        if (entregas.containsKey(claveEntrega(estudiante, tarea))) {
            throw new RuntimeException("Ya entregaste esta tarea");
        }

        Entrega entrega = new Entrega(estudiante, tarea, contenido);
        if (nombreArchivo != null && !nombreArchivo.isBlank()) {
            entrega.setNombreArchivo(nombreArchivo);
        }
        entregas.put(claveEntrega(estudiante, tarea), entrega);
        entregaRepo.guardar(entrega);

        registrarHistorial(model.HistorialEntrada.TipoEvento.ENTREGA,
            estudiante.getEmail(), "Entrego: " + tarea.getTitulo());

        return entrega;
    }

    public Entrega entregarTarea(Usuario estudiante, Materia materia, Tarea tarea, String contenido) {
        return entregarTarea(estudiante, materia, tarea, contenido, "");
    }

    public void calificarTarea(Usuario profesor, Materia materia, Entrega entrega,
                               double nota, String comentario) {
        if (profesor.getRol() != Rol.PROFESOR) {
            throw new AccesoDenegadoException("Solo profesores pueden calificar");
        }
        if (!materia.getProfesor().equals(profesor)) {
            throw new AccesoDenegadoException("No eres el profesor de esta materia");
        }
        if (nota < 0 || nota > 5) {
            throw new IllegalArgumentException("Nota invalida: debe estar entre 0.0 y 5.0");
        }

        entrega.setNota(nota);
        entrega.setEstado(EstadoTarea.CALIFICADO);
        if (comentario != null && !comentario.isBlank()) {
            entrega.setComentarioProfesor(comentario);
        }
        entregas.put(claveEntrega(entrega.getEstudiante(), entrega.getTarea()), entrega);
        entregaRepo.guardar(entrega);

        registrarHistorial(model.HistorialEntrada.TipoEvento.CALIFICACION,
            profesor.getEmail(),
            String.format("Califico '%s' de %s -> %.1f",
                entrega.getTarea().getTitulo(),
                entrega.getEstudiante().getEmail(),
                nota));
    }

    public void calificarTarea(Usuario profesor, Materia materia, Entrega entrega, double nota) {
        calificarTarea(profesor, materia, entrega, nota, "");
    }

    public void clasificarEisenhower(Tarea tarea, boolean urgente, boolean importante,
                                     Usuario profesor, Materia materia) {
        if (profesor.getRol() != Rol.PROFESOR) {
            throw new AccesoDenegadoException("Solo el profesor puede reclasificar tareas");
        }
        tarea.setUrgente(urgente);
        tarea.setImportante(importante);
        tareaRepo.guardar(tarea, materia.getNombre());
    }

    public Collection<Entrega> getEntregas() {
        return entregas.values();
    }

    public List<Tarea> obtenerTareasGuardadas(String nombreMateria) {
        return tareaRepo.getAll(nombreMateria);
    }

    public List<Tarea> obtenerTareasGuardadas() {
        return new ArrayList<>();
    }

    public List<Entrega> obtenerEntregasCalificadas(Usuario estudiante) {
        List<Entrega> resultado = new ArrayList<>();
        for (Entrega e : entregas.values()) {
            if (e.getEstudiante().equals(estudiante) && e.getEstado() == EstadoTarea.CALIFICADO) {
                resultado.add(e);
            }
        }
        return resultado;
    }

    public List<Entrega> obtenerEntregasPorEstudiante(Usuario estudiante) {
        List<Entrega> resultado = new ArrayList<>();
        for (Entrega e : entregas.values()) {
            if (e.getEstudiante().equals(estudiante)) {
                resultado.add(e);
            }
        }
        return resultado;
    }

    public List<Tarea> obtenerTareasPendientes(Usuario estudiante, Materia materia) {
        List<Tarea> pendientes = new ArrayList<>();
        for (Tarea tarea : materia.getTareas()) {
            boolean entregada = entregas.containsKey(claveEntrega(estudiante, tarea));
            if (!entregada) {
                pendientes.add(tarea);
            }
        }
        return pendientes;
    }

    public List<Tarea> obtenerTareasOrdenadasEisenhower(Materia materia) {
        List<Tarea> lista = new ArrayList<>(materia.getTareas());
        lista.sort((a, b) -> {
            int pa = prioridadEisenhower(a);
            int pb = prioridadEisenhower(b);
            if (pa != pb) {
                return Integer.compare(pa, pb);
            }
            return Long.compare(a.diasRestantes(), b.diasRestantes());
        });
        return lista;
    }

    private int prioridadEisenhower(Tarea t) {
        if (t.isUrgente() && t.isImportante()) {
            return 1;
        }
        if (!t.isUrgente() && t.isImportante()) {
            return 2;
        }
        if (t.isUrgente() && !t.isImportante()) {
            return 3;
        }
        return 4;
    }

    public void clasificarEisenhower(Tarea tarea, boolean urgente, boolean importante, Usuario profesor) {
        Materia materia = AppState.getMateriaActual();
        if (materia == null) {
            throw new IllegalStateException("No hay materia activa");
        }
        clasificarEisenhower(tarea, urgente, importante, profesor, materia);
    }

    private void registrarHistorial(model.HistorialEntrada.TipoEvento tipo, String email, String detalle) {
        if (historialRepo != null) {
            historialRepo.registrar(tipo, email, detalle);
        }
    }
}
