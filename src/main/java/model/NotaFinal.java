package model;
import java.io.Serializable;

public class NotaFinal implements Serializable{

    private Usuario estudiante;
    private Materia materia;
    private Periodo periodo;
    private double promedio;
    private boolean aprobado;

    // Umbral de aprobación: 3.0 (escala colombiana 0-5)
    private static final double UMBRAL = 3.0;

    public NotaFinal(Usuario estudiante, Materia materia, Periodo periodo, double promedio) {
        this.estudiante = estudiante;
        this.materia = materia;
        this.periodo = periodo;
        this.promedio = Math.round(promedio * 100.0) / 100.0; // 2 decimales
        this.aprobado = this.promedio >= UMBRAL;
    }

    public Usuario getEstudiante()  { return estudiante; }
    public Materia getMateria()     { return materia; }
    public Periodo getPeriodo()     { return periodo; }
    public double getPromedio()     { return promedio; }
    public boolean isAprobado()     { return aprobado; }

    @Override
    public String toString() {
        return String.format("%s | %s | %.2f | %s",
            estudiante.getEmail(),
            materia.getNombre(),
            promedio,
            aprobado ? "APROBADO" : "REPROBADO"
        );
    }
}