package repository;

import model.Usuario;
import util.AvlUsuarios;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repositorio de usuarios.
 * - HashMap para búsqueda exacta O(1) por email.
 * - AvlUsuarios para listado ordenado y búsqueda por prefijo O(log n).
 * Ambas estructuras se mantienen sincronizadas.
 */
public class UsuarioRepository implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String FILE = "data/usuarios.dat";

    private Map<String, Usuario> usuarios = new HashMap<>();
    private AvlUsuarios avl = new AvlUsuarios();

    public UsuarioRepository() {
        cargar();
    }

    /** Guarda o actualiza un usuario en ambas estructuras */
    public void guardar(Usuario usuario) {
        usuarios.put(usuario.getEmail(), usuario);
        avl.insertar(usuario);
        guardarArchivo();
    }

    /** Búsqueda O(1) por email exacto */
    public Usuario buscar(String email) {
        return usuarios.get(email);
    }

    /** Todos los usuarios ordenados alfabéticamente por email */
    public List<Usuario> listarOrdenados() {
        return avl.enOrden();
    }

    /** Búsqueda por prefijo de email (para autocompletar) */
    public List<Usuario> buscarPorPrefijo(String prefijo) {
        return avl.buscarPorPrefijo(prefijo);
    }

    public int getTotalUsuarios() {
        return usuarios.size();
    }

    // ── Persistencia ──────────────────────────────────────────────────────────

    private void guardarArchivo() {
        try {
            File file = new File(FILE);
            file.getParentFile().mkdirs();
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(this);
            }
        } catch (Exception e) {
            System.err.println("Error guardando usuarios: " + e.getMessage());
        }
    }

    private void cargar() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE))) {
            UsuarioRepository cargado = (UsuarioRepository) ois.readObject();
            this.usuarios = cargado.usuarios;
            // Reconstruir AVL desde el mapa (el AVL no necesita persistirse por separado)
            this.avl = new AvlUsuarios();
            for (Usuario u : this.usuarios.values()) {
                this.avl.insertar(u);
            }
        } catch (Exception e) {
            System.out.println("Repositorio de usuarios nuevo.");
        }
    }
}