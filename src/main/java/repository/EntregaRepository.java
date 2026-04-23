package repository;

import model.Entrega;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio de entregas persistido en data/entregas.dat.
 * Actualiza en lugar de duplicar (mismo estudiante + misma tarea).
 */
public class EntregaRepository implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String FILE = "data/entregas.dat";

    private List<Entrega> entregas = new ArrayList<>();

    public EntregaRepository() {
        cargar();
    }

    public void guardar(Entrega e) {
        for (int i = 0; i < entregas.size(); i++) {
            if (entregas.get(i).equals(e)) { // equals por estudiante+tarea
                entregas.set(i, e);
                guardarArchivo();
                return;
            }
        }
        entregas.add(e);
        guardarArchivo();
    }

    public List<Entrega> getAll() {
        return new ArrayList<>(entregas);
    }

    // ── Persistencia ──────────────────────────────────────────────────────────

    private void guardarArchivo() {
        try {
            File file = new File(FILE);
            file.getParentFile().mkdirs();
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(entregas);
            }
        } catch (Exception e) {
            System.err.println("Error guardando entregas: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void cargar() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE))) {
            entregas = (List<Entrega>) ois.readObject();
        } catch (Exception e) {
            System.out.println("Repositorio de entregas nuevo.");
        }
    }
}
