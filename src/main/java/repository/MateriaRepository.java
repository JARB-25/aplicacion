package repository;

import model.Materia;
import model.Tarea;
import model.Usuario;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio de materias persistido en data/materias.dat.
 * Guarda/actualiza por nombre+email del profesor (clave natural).
 */
public class MateriaRepository implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String FILE = "data/materias.dat";

    private List<Materia> materias = new ArrayList<>();

    public MateriaRepository() {
        cargar();
    }

    /** Guarda o actualiza (misma clave nombre+profesor) */
    public void guardar(Materia m) {
        for (int i = 0; i < materias.size(); i++) {
            Materia ex = materias.get(i);
            if (ex.getNombre().equals(m.getNombre())
                    && ex.getProfesor().getEmail().equals(m.getProfesor().getEmail())) {
                materias.set(i, m);
                guardarArchivo();
                return;
            }
        }
        materias.add(m);
        guardarArchivo();
    }

    /** Elimina por nombre + email del profesor */
    public boolean eliminar(String nombre, String emailProfesor) {
        boolean removed = materias.removeIf(m ->
            m.getNombre().equals(nombre)
            && m.getProfesor().getEmail().equals(emailProfesor));
        if (removed) guardarArchivo();
        return removed;
    }

    /** Materias de un profesor específico */
    public List<Materia> getPorProfesor(String emailProfesor) {
        List<Materia> res = new ArrayList<>();
        for (Materia m : materias)
            if (m.getProfesor().getEmail().equals(emailProfesor)) res.add(m);
        return res;
    }

    /** Materias en las que está inscrito un estudiante */
    public List<Materia> getPorEstudiante(String emailEstudiante) {
        List<Materia> res = new ArrayList<>();
        for (Materia m : materias)
            for (Usuario u : m.getEstudiantes())
                if (u.getEmail().equals(emailEstudiante)) { res.add(m); break; }
        return res;
    }

    public List<Materia> getAll() {
        return new ArrayList<>(materias);
    }

    public Materia buscar(String nombre, String emailProfesor) {
        for (Materia m : materias)
            if (m.getNombre().equals(nombre)
                    && m.getProfesor().getEmail().equals(emailProfesor)) return m;
        return null;
    }

    // ── Persistencia ──────────────────────────────────────────────────────────

    private void guardarArchivo() {
        try {
            File file = new File(FILE);
            file.getParentFile().mkdirs();
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(materias);
            }
        } catch (Exception e) {
            System.err.println("Error guardando materias: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void cargar() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE))) {
            materias = (List<Materia>) ois.readObject();
        } catch (Exception e) {
            System.out.println("Repositorio de materias nuevo.");
        }
    }
}