package repository;

import model.Tarea;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TareaRepository implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String DIR = "data/";

    private String archivo(String nombreMateria) {
        String nombre = nombreMateria.replaceAll("[^a-zA-Z0-9áéíóúÁÉÍÓÚñÑ_\\-]", "_");
        return DIR + "tareas_" + nombre + ".dat";
    }

    public void guardar(Tarea tarea, String nombreMateria) {
        Map<String, Tarea> mapa = cargar(nombreMateria);
        mapa.put(tarea.getTitulo(), tarea);
        guardarArchivo(mapa, nombreMateria);
    }

    public List<Tarea> getAll(String nombreMateria) {
        return new ArrayList<>(cargar(nombreMateria).values());
    }

    public boolean eliminar(String titulo, String nombreMateria) {
        Map<String, Tarea> mapa = cargar(nombreMateria);
        boolean removed = mapa.remove(titulo) != null;
        if (removed) {
            guardarArchivo(mapa, nombreMateria);
        }
        return removed;
    }

    private void guardarArchivo(Map<String, Tarea> mapa, String nombreMateria) {
        try {
            File file = new File(archivo(nombreMateria));
            file.getParentFile().mkdirs();
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(new ArrayList<>(mapa.values()));
            }
        } catch (Exception e) {
            System.err.println("Error guardando tareas de '" + nombreMateria + "': " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Tarea> cargar(String nombreMateria) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo(nombreMateria)))) {
            List<Tarea> lista = (List<Tarea>) ois.readObject();
            Map<String, Tarea> mapa = new LinkedHashMap<>();
            for (Tarea tarea : lista) {
                mapa.put(tarea.getTitulo(), tarea);
            }
            return mapa;
        } catch (Exception e) {
            return new LinkedHashMap<>();
        }
    }
}
