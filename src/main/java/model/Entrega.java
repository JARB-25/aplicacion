package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa la entrega de una tarea por parte de un estudiante.
 * Soporta contenido de texto enriquecido y simulación de archivo adjunto.
 */
public class Entrega implements Serializable {

    private static final long serialVersionUID = 1L;

    private Usuario estudiante;
    private Tarea tarea;
    private String contenido;          // Punto 2: texto enriquecido (textarea)
    private String nombreArchivo;      // Punto 2: simulación de adjunto
    private LocalDateTime fechaEntrega;
    private EstadoTarea estado;
    private double nota;
    private String comentarioProfesor; // feedback al calificar

    public Entrega(Usuario estudiante, Tarea tarea, String contenido) {
        this.estudiante = estudiante;
        this.tarea = tarea;
        this.contenido = contenido != null ? contenido : "";
        this.nombreArchivo = "";
        this.fechaEntrega = LocalDateTime.now();
        this.estado = EstadoTarea.ENTREGADO;
        this.nota = -1; // sin calificar
    }

    // ── Getters ────────────────────────────────────────────────────────────────

    public Usuario getEstudiante()          { return estudiante; }
    public Tarea getTarea()                 { return tarea; }
    public String getContenido()            { return contenido; }
    public String getNombreArchivo()        { return nombreArchivo; }
    public LocalDateTime getFechaEntrega()  { return fechaEntrega; }
    public EstadoTarea getEstado()          { return estado; }
    public double getNota()                 { return nota; }
    public String getComentarioProfesor()   { return comentarioProfesor; }

    // ── Setters ────────────────────────────────────────────────────────────────

    public void setEstado(EstadoTarea estado)           { this.estado = estado; }
    public void setNota(double nota)                    { this.nota = nota; }
    public void setNombreArchivo(String nombreArchivo)  { this.nombreArchivo = nombreArchivo; }
    public void setComentarioProfesor(String c)         { this.comentarioProfesor = c; }

    // ── Helpers UI ────────────────────────────────────────────────────────────

    public boolean tieneArchivo() {
        return nombreArchivo != null && !nombreArchivo.isBlank();
    }

    public String badgeEstado() {
        switch (estado) {
            case CALIFICADO: return "✅ " + String.format("%.1f", nota);
            case ENTREGADO:  return "📤 Entregado";
            default:         return "⏳ Pendiente";
        }
    }

    // ── Igualdad por estudiante + tarea ───────────────────────────────────────

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entrega)) return false;
        Entrega e = (Entrega) o;
        return Objects.equals(estudiante, e.estudiante) && Objects.equals(tarea, e.tarea);
    }

    @Override
    public int hashCode() { return Objects.hash(estudiante, tarea); }
}