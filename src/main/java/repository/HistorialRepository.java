package repository;

import model.HistorialEntrada;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio de historial implementado como lista enlazada simple.
 * Los nodos se agregan al frente (O(1)) y se recorren del más reciente al más antiguo.
 * Se persiste en data/historial.dat.
 */
public class HistorialRepository implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String FILE = "data/historial.dat";

    private HistorialEntrada cabeza; // nodo más reciente
    private int tamaño;

    public HistorialRepository() {
        cargar();
    }

    /** Inserta al frente de la lista (O(1)) */
    public void registrar(HistorialEntrada entrada) {
        entrada.setSiguiente(cabeza);
        cabeza = entrada;
        tamaño++;
        guardarArchivo();
    }

    /** Registra un evento directamente desde sus datos */
    public void registrar(HistorialEntrada.TipoEvento tipo, String email, String detalle) {
        registrar(new HistorialEntrada(tipo, email, detalle));
    }

    /**
     * Retorna los últimos N eventos (recorrido desde cabeza).
     * Si n <= 0, retorna todos.
     */
    public List<HistorialEntrada> obtenerRecientes(int n) {
        List<HistorialEntrada> lista = new ArrayList<>();
        HistorialEntrada actual = cabeza;
        int count = 0;
        while (actual != null && (n <= 0 || count < n)) {
            lista.add(actual);
            actual = actual.getSiguiente();
            count++;
        }
        return lista;
    }

    /** Filtra eventos de un usuario específico */
    public List<HistorialEntrada> obtenerDeUsuario(String email) {
        List<HistorialEntrada> lista = new ArrayList<>();
        HistorialEntrada actual = cabeza;
        while (actual != null) {
            if (email.equals(actual.getEmailUsuario())) {
                lista.add(actual);
            }
            actual = actual.getSiguiente();
        }
        return lista;
    }

    public int getTamaño() { return tamaño; }

    // ── Persistencia ──────────────────────────────────────────────────────────

    private void guardarArchivo() {
        try {
            File file = new File(FILE);
            file.getParentFile().mkdirs();
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(this);
            }
        } catch (Exception e) {
            System.err.println("Error guardando historial: " + e.getMessage());
        }
    }

    private void cargar() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE))) {
            HistorialRepository cargado = (HistorialRepository) ois.readObject();
            this.cabeza = cargado.cabeza;
            this.tamaño = cargado.tamaño;
        } catch (Exception e) {
            // Primera ejecución, sin historial previo
            this.cabeza = null;
            this.tamaño = 0;
        }
    }
}