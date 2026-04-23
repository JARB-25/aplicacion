package service;

import model.*;
import exception.AccesoDenegadoException;
import java.util.ArrayList;
import java.util.List;

public class NotaService {

    private List<NotaFinal> notasFinales = new ArrayList<>();

    /**
     * Calcula el promedio de todas las entregas CALIFICADAS
     * de un estudiante en una materia y genera su NotaFinal.
     */
    public NotaFinal calcularNotaFinal(
            Usuario profesor,
            Usuario estudiante,
            Materia materia,
            Periodo periodo,
            List<Entrega> entregas) {

        if (profesor.getRol() != Rol.PROFESOR) {
            throw new AccesoDenegadoException("Solo profesores pueden cerrar notas");
        }
        if (!materia.getProfesor().equals(profesor)) {
            throw new AccesoDenegadoException("No eres el profesor de esta materia");
        }

        List<Double> notas = new ArrayList<>();
        for (Entrega e : entregas) {
            if (e.getEstudiante().equals(estudiante)
                    && e.getEstado() == EstadoTarea.CALIFICADO) {
                notas.add(e.getNota());
            }
        }

        if (notas.isEmpty()) {
            throw new RuntimeException(
                "El estudiante no tiene entregas calificadas en esta materia"
            );
        }

        double promedio = notas.stream()
                               .mapToDouble(Double::doubleValue)
                               .average()
                               .orElse(0.0);

        NotaFinal notaFinal = new NotaFinal(estudiante, materia, periodo, promedio);
        notasFinales.add(notaFinal);
        return notaFinal;
    }

    /** Retorna todas las notas finales de un estudiante en todos los períodos */
    public List<NotaFinal> getHistorialEstudiante(Usuario estudiante) {
        List<NotaFinal> historial = new ArrayList<>();
        for (NotaFinal n : notasFinales) {
            if (n.getEstudiante().equals(estudiante)) {
                historial.add(n);
            }
        }
        return historial;
    }

    /** Retorna todas las notas de una materia en un período específico */
    public List<NotaFinal> getNotasPorMateriaPeriodo(Materia materia, Periodo periodo) {
        List<NotaFinal> resultado = new ArrayList<>();
        for (NotaFinal n : notasFinales) {
            if (n.getMateria().equals(materia) && n.getPeriodo().equals(periodo)) {
                resultado.add(n);
            }
        }
        return resultado;
    }

    public List<NotaFinal> getNotasFinales() { return notasFinales; }
}
