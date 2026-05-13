package repository;

import model.Entrega;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntregaRepository implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String FILE = "data/entregas.dat";

    private Map<String, Entrega> entregas = new HashMap<>();

    public EntregaRepository() {
        cargar();
    }

    private String clave(Entrega e) {
        return e.getEstudiante().getEmail() + ":" + e.getTarea().getTitulo();
    }

    public void guardar(Entrega e) {
        entregas.put(clave(e), e);
        guardarArchivo();
    }

    public List<Entrega> getAll() {
        return new ArrayList<>(entregas.values());
    }

    private void guardarArchivo() {
        try {
            File file = new File(FILE);
            file.getParentFile().mkdirs();
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(new ArrayList<>(entregas.values()));
            }
        } catch (Exception e) {
            System.err.println("Error guardando entregas: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void cargar() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE))) {
            List<Entrega> lista = (List<Entrega>) ois.readObject();
            for (Entrega e : lista) {
                entregas.put(clave(e), e);
            }
        } catch (Exception e) {
            System.out.println("Repositorio de entregas nuevo.");
        }
    }
}
