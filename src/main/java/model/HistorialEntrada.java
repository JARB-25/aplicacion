package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Nodo de una lista enlazada simple que registra un evento de actividad.
 * El historial completo se almacena como cadena de nodos en {@link repository.HistorialRepository}.
 *
 * Tipos de evento:
 *  - ENTREGA      → estudiante entregó una tarea
 *  - CALIFICACION → profesor calificó una entrega
 *  - LOGIN        → usuario inició sesión
 *  - REGISTRO     → usuario se registró
 *  - TAREA_CREADA → profesor creó una tarea
 */
public class HistorialEntrada implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public enum TipoEvento {
        ENTREGA, CALIFICACION, LOGIN, REGISTRO, TAREA_CREADA
    }

    private final TipoEvento tipo;
    private final String emailUsuario;
    private final String detalle;
    private final LocalDateTime momento;
    private HistorialEntrada siguiente; // lista enlazada

    public HistorialEntrada(TipoEvento tipo, String emailUsuario, String detalle) {
        this.tipo = tipo;
        this.emailUsuario = emailUsuario;
        this.detalle = detalle;
        this.momento = LocalDateTime.now();
        this.siguiente = null;
    }

    // ── Getters ────────────────────────────────────────────────────────────────

    public TipoEvento getTipo()             { return tipo; }
    public String getEmailUsuario()         { return emailUsuario; }
    public String getDetalle()              { return detalle; }
    public LocalDateTime getMomento()       { return momento; }
    public HistorialEntrada getSiguiente()  { return siguiente; }

    public void setSiguiente(HistorialEntrada siguiente) {
        this.siguiente = siguiente;
    }

    // ── Display ───────────────────────────────────────────────────────────────

    public String iconoTipo() {
        switch (tipo) {
            case ENTREGA:      return "📤";
            case CALIFICACION: return "✅";
            case LOGIN:        return "🔑";
            case REGISTRO:     return "👤";
            case TAREA_CREADA: return "➕";
            default:           return "•";
        }
    }

    @Override
    public String toString() {
        return String.format("[%s] %s %s — %s",
            momento.format(FMT), iconoTipo(), emailUsuario, detalle);
    }
}