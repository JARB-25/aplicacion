package service;

import model.*;
import exception.AccesoDenegadoException;
import repository.MateriaRepository;

import java.util.List;

/**
 * Servicio de materias con CRUD completo y persistencia.
 */
public class MateriaService {

    private final MateriaRepository repo = new MateriaRepository();

    // ── Crear ─────────────────────────────────────────────────────────────────

    public Materia crearMateria(String nombre, Usuario profesor) {
        if (profesor.getRol() != Rol.PROFESOR)
            throw new AccesoDenegadoException("Solo profesores pueden crear materias");
        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("El nombre de la materia no puede estar vacío");
        if (repo.buscar(nombre.trim(), profesor.getEmail()) != null)
            throw new RuntimeException("Ya existe una materia con ese nombre para este profesor");

        Materia materia = new Materia(nombre.trim(), profesor);
        repo.guardar(materia);
        return materia;
    }

    // ── Leer ──────────────────────────────────────────────────────────────────

    public List<Materia> getMateriasPorProfesor(Usuario profesor) {
        return repo.getPorProfesor(profesor.getEmail());
    }

    public List<Materia> getMateriasDeEstudiante(Usuario estudiante) {
        return repo.getPorEstudiante(estudiante.getEmail());
    }

    public List<Materia> getMaterias() {
        return repo.getAll();
    }

    public Materia buscar(String nombre, String emailProfesor) {
        return repo.buscar(nombre, emailProfesor);
    }

    // ── Renombrar ─────────────────────────────────────────────────────────────

    public void renombrarMateria(Materia materia, String nuevoNombre, Usuario profesor) {
        if (profesor.getRol() != Rol.PROFESOR)
            throw new AccesoDenegadoException("Solo profesores pueden editar materias");
        if (!materia.getProfesor().equals(profesor))
            throw new AccesoDenegadoException("Solo el profesor de la materia puede editarla");
        if (nuevoNombre == null || nuevoNombre.isBlank())
            throw new IllegalArgumentException("El nombre no puede estar vacío");

        repo.eliminar(materia.getNombre(), profesor.getEmail());
        materia.setNombre(nuevoNombre.trim());
        repo.guardar(materia);
    }

    // ── Eliminar ──────────────────────────────────────────────────────────────

    public void eliminarMateria(Materia materia, Usuario profesor) {
        if (profesor.getRol() != Rol.PROFESOR)
            throw new AccesoDenegadoException("Solo profesores pueden eliminar materias");
        if (!materia.getProfesor().equals(profesor))
            throw new AccesoDenegadoException("Solo el profesor de la materia puede eliminarla");

        repo.eliminar(materia.getNombre(), profesor.getEmail());
    }

    // ── Inscribir estudiante ──────────────────────────────────────────────────

    public void inscribirEstudiante(Materia materia, Usuario estudiante) {
        if (estudiante.getRol() != Rol.ESTUDIANTE)
            throw new AccesoDenegadoException("Solo estudiantes pueden inscribirse");
        if (materia.getEstudiantes().contains(estudiante))
            throw new RuntimeException("El estudiante ya está inscrito");

        materia.inscribirEstudiante(estudiante);
        repo.guardar(materia);
    }

    public void guardar(Materia materia) {
        repo.guardar(materia);
    }
}