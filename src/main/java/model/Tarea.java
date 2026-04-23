package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Representa una tarea académica publicada por un profesor en una materia.
 * Incluye prioridad según la matriz de Eisenhower (urgente/importante),
 * descripción enriquecida y estado de ciclo de vida completo.
 */
public class Tarea implements Serializable {

    private static final long serialVersionUID = 1L;

    private String titulo;
    private String descripcion;       // Punto 2: descripción enriquecida
    private LocalDate fechaLimite;
    private LocalDate fechaCreacion;
    private EstadoTarea estado;       // Punto 6: estado completo
    private boolean urgente;          // Punto 4: Eisenhower
    private boolean importante;       // Punto 4: Eisenhower

    public Tarea(String titulo, LocalDate fechaLimite) {
        this.titulo = titulo;
        this.descripcion = "";
        this.fechaLimite = fechaLimite;
        this.fechaCreacion = LocalDate.now();
        this.estado = EstadoTarea.PENDIENTE;
        // Clasificación automática: urgente si vence en <= 3 días
        this.urgente = diasRestantes() <= 3;
        this.importante = true; // toda tarea académica es importante por defecto
    }

    public Tarea(String titulo, String descripcion, LocalDate fechaLimite) {
        this(titulo, fechaLimite);
        this.descripcion = descripcion != null ? descripcion : "";
    }

    // ── Lógica de fecha ────────────────────────────────────────────────────────

    public boolean estaFueraDeFecha() {
        return LocalDate.now().isAfter(fechaLimite);
    }

    /** Días que faltan para el vencimiento (negativo si ya venció) */
    public long diasRestantes() {
        return ChronoUnit.DAYS.between(LocalDate.now(), fechaLimite);
    }

    /**
     * Clasificación visual para la UI según Eisenhower + fecha.
     * VENCIDO → PROXIMO (<=3d) → PENDIENTE → ENTREGADO → CALIFICADO
     */
    public String etiquetaEstado() {
        if (estado == EstadoTarea.CALIFICADO) return "✅ Calificado";
        if (estado == EstadoTarea.ENTREGADO)  return "📤 Entregado";
        if (estaFueraDeFecha())               return "🔴 Vencido";
        if (diasRestantes() <= 3)             return "🟡 Próximo";
        return "🔵 Pendiente";
    }

    /** Cuadrante de Eisenhower: UI / Q1-Q4 */
    public String cuadranteEisenhower() {
        if (urgente && importante)   return "Q1 - Hacer ya";
        if (!urgente && importante)  return "Q2 - Planificar";
        if (urgente && !importante)  return "Q3 - Delegar";
        return                              "Q4 - Eliminar";
    }

    // ── Getters / Setters ──────────────────────────────────────────────────────

    public String getTitulo()              { return titulo; }
    public String getDescripcion()         { return descripcion; }
    public LocalDate getFechaLimite()      { return fechaLimite; }
    public LocalDate getFechaCreacion()    { return fechaCreacion; }
    public EstadoTarea getEstado()         { return estado; }
    public boolean isUrgente()             { return urgente; }
    public boolean isImportante()          { return importante; }

    public void setDescripcion(String d)   { this.descripcion = d != null ? d : ""; }
    public void setTitulo(String t)        { this.titulo = t; }
    public void setEstado(EstadoTarea e)   { this.estado = e; }
    public void setUrgente(boolean u)      { this.urgente = u; }
    public void setImportante(boolean i)   { this.importante = i; }
    public void setFechaLimite(LocalDate f){ this.fechaLimite = f; }

    // ── Igualdad por título (único dentro de una materia) ──────────────────────

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tarea)) return false;
        return Objects.equals(titulo, ((Tarea) o).titulo);
    }

    @Override
    public int hashCode() { return Objects.hash(titulo); }

    @Override
    public String toString() { return titulo; }
}
