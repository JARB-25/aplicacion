package repository;

import model.Tarea;

import java.io.*;
import java.util.*;

public class TareaRepository implements Serializable {

    private static final long serialVersionUID = 1L;
    // Directorio base — mismo que antes
    private static final String DIR = "data/";

    /** Devuelve la ruta del archivo para una materia concreta */
    private String archivo(String nombreMateria) {
        // Sanitiza el nombre para que sea un nombre de archivo válido
        String nombre = nombreMateria.replaceAll("[^a-zA-Z0-9áéíóúÁÉÍÓÚñÑ_\\-]", "_");
        return DIR + "tareas_" + nombre + ".dat";
    }

    // ── Guardar / actualizar ───────────────────────────────────────────────────

    /** Guarda o actualiza la tarea dentro del archivo de su materia */
    public void guardar(Tarea tarea, String nombreMateria) {
        List<Tarea> lista = cargar(nombreMateria);
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getTitulo().equals(tarea.getTitulo())) {
                lista.set(i, tarea);
                guardarArchivo(lista, nombreMateria);
                return;
            }
        }
        lista.add(tarea);
        guardarArchivo(lista, nombreMateria);
    }

    // ── Leer ──────────────────────────────────────────────────────────────────

    /** Devuelve todas las tareas de UNA materia */
    public List<Tarea> getAll(String nombreMateria) {
        return cargar(nombreMateria);
    }

    public boolean eliminar(String titulo, String nombreMateria) {
        List<Tarea> lista = cargar(nombreMateria);
        boolean removed = lista.removeIf(t -> t.getTitulo().equals(titulo));
        if (removed) {
            guardarArchivo(lista, nombreMateria);
        }
        return removed;
    }

    // ── Persistencia interna ──────────────────────────────────────────────────

    private void guardarArchivo(List<Tarea> lista, String nombreMateria) {
        try {
            File file = new File(archivo(nombreMateria));
            file.getParentFile().mkdirs();
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(lista);
            }
        } catch (Exception e) {
            System.err.println("Error guardando tareas de '" + nombreMateria + "': " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private List<Tarea> cargar(String nombreMateria) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo(nombreMateria)))) {
            return (List<Tarea>) ois.readObject();
        } catch (Exception e) {
            return new ArrayList<>();  // archivo nuevo o primera vez
        }
    }
}
