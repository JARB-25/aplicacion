package util;

import model.Usuario;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Árbol AVL de usuarios ordenado por email (lexicográfico).
 *
 * Uso real dentro del LMS:
 *  - Búsqueda O(log n) por email (más rápido que HashMap para búsquedas por rango)
 *  - Listar usuarios en orden alfabético sin sort extra (inorden)
 *  - Base para búsqueda por prefijo de email (autocompletar)
 *
 * El UsuarioRepository usa este árbol internamente además del HashMap para
 * soportar esas operaciones adicionales.
 */
public class AvlUsuarios implements Serializable {

    private static final long serialVersionUID = 1L;

    // ── Nodo interno ──────────────────────────────────────────────────────────

    private static class Nodo implements Serializable {
        Usuario usuario;
        Nodo izq, der;
        int altura;

        Nodo(Usuario u) {
            this.usuario = u;
            this.altura = 1;
        }
    }

    // ── Estado ────────────────────────────────────────────────────────────────

    private Nodo raiz;
    private int tamaño;

    // ── API pública ───────────────────────────────────────────────────────────

    public void insertar(Usuario usuario) {
        raiz = insertar(raiz, usuario);
        tamaño++;
    }

    public Usuario buscar(String email) {
        Nodo nodo = buscar(raiz, email);
        return nodo == null ? null : nodo.usuario;
    }

    /** Usuarios ordenados alfabéticamente por email (recorrido inorden) */
    public List<Usuario> enOrden() {
        List<Usuario> lista = new ArrayList<>();
        inorden(raiz, lista);
        return lista;
    }

    /**
     * Búsqueda por prefijo: todos los usuarios cuyo email empieza con el prefijo dado.
     * Útil para autocompletar en UI.
     */
    public List<Usuario> buscarPorPrefijo(String prefijo) {
        List<Usuario> resultado = new ArrayList<>();
        buscarPrefijo(raiz, prefijo.toLowerCase(), resultado);
        return resultado;
    }

    public int getTamaño() { return tamaño; }

    // ── Inserción AVL ─────────────────────────────────────────────────────────

    private Nodo insertar(Nodo nodo, Usuario usuario) {
        if (nodo == null) return new Nodo(usuario);

        int cmp = usuario.getEmail().compareTo(nodo.usuario.getEmail());
        if (cmp < 0) {
            nodo.izq = insertar(nodo.izq, usuario);
        } else if (cmp > 0) {
            nodo.der = insertar(nodo.der, usuario);
        } else {
            // Actualizar usuario existente (mismo email)
            nodo.usuario = usuario;
            tamaño--; // no incrementar tamaño en actualización
            return nodo;
        }

        actualizarAltura(nodo);
        return balancear(nodo);
    }

    // ── Búsqueda ──────────────────────────────────────────────────────────────

    private Nodo buscar(Nodo nodo, String email) {
        if (nodo == null) return null;
        int cmp = email.compareTo(nodo.usuario.getEmail());
        if (cmp < 0) return buscar(nodo.izq, email);
        if (cmp > 0) return buscar(nodo.der, email);
        return nodo;
    }

    // ── Recorridos ────────────────────────────────────────────────────────────

    private void inorden(Nodo nodo, List<Usuario> lista) {
        if (nodo == null) return;
        inorden(nodo.izq, lista);
        lista.add(nodo.usuario);
        inorden(nodo.der, lista);
    }

    private void buscarPrefijo(Nodo nodo, String prefijo, List<Usuario> result) {
        if (nodo == null) return;
        String email = nodo.usuario.getEmail().toLowerCase();
        if (email.startsWith(prefijo)) {
            result.add(nodo.usuario);
        }
        // El AVL está ordenado, así que podemos optimizar la búsqueda
        if (prefijo.compareTo(email) <= 0) buscarPrefijo(nodo.izq, prefijo, result);
        if (prefijo.compareTo(email) >= 0) buscarPrefijo(nodo.der, prefijo, result);
    }

    // ── Balanceo AVL ──────────────────────────────────────────────────────────

    private Nodo balancear(Nodo nodo) {
        int factor = factorBalance(nodo);

        // Izquierda-Izquierda
        if (factor > 1 && factorBalance(nodo.izq) >= 0)
            return rotarDerecha(nodo);

        // Izquierda-Derecha
        if (factor > 1 && factorBalance(nodo.izq) < 0) {
            nodo.izq = rotarIzquierda(nodo.izq);
            return rotarDerecha(nodo);
        }

        // Derecha-Derecha
        if (factor < -1 && factorBalance(nodo.der) <= 0)
            return rotarIzquierda(nodo);

        // Derecha-Izquierda
        if (factor < -1 && factorBalance(nodo.der) > 0) {
            nodo.der = rotarDerecha(nodo.der);
            return rotarIzquierda(nodo);
        }

        return nodo;
    }

    private Nodo rotarDerecha(Nodo y) {
        Nodo x = y.izq;
        Nodo T2 = x.der;
        x.der = y;
        y.izq = T2;
        actualizarAltura(y);
        actualizarAltura(x);
        return x;
    }

    private Nodo rotarIzquierda(Nodo x) {
        Nodo y = x.der;
        Nodo T2 = y.izq;
        y.izq = x;
        x.der = T2;
        actualizarAltura(x);
        actualizarAltura(y);
        return y;
    }

    private void actualizarAltura(Nodo n) {
        n.altura = 1 + Math.max(altura(n.izq), altura(n.der));
    }

    private int factorBalance(Nodo n) {
        return n == null ? 0 : altura(n.izq) - altura(n.der);
    }

    private int altura(Nodo n) {
        return n == null ? 0 : n.altura;
    }
}