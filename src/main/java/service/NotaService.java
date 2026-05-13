package service;

import exception.AccesoDenegadoException;
import model.Entrega;
import model.EstadoTarea;
import model.Materia;
import model.NotaFinal;
import model.Periodo;
import model.Rol;
import model.Usuario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotaService {

    private Map<String, List<NotaFinal>> notasFinales = new HashMap<>();

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
        notasFinales
            .computeIfAbsent(estudiante.getEmail(), k -> new ArrayList<>())
            .add(notaFinal);
        return notaFinal;
    }

    public List<NotaFinal> getHistorialEstudiante(Usuario estudiante) {
        return notasFinales.getOrDefault(estudiante.getEmail(), new ArrayList<>());
    }

    public List<NotaFinal> getNotasPorMateriaPeriodo(Materia materia, Periodo periodo) {
        List<NotaFinal> resultado = new ArrayList<>();
        for (List<NotaFinal> lista : notasFinales.values()) {
            for (NotaFinal n : lista) {
                if (n.getMateria().equals(materia) && n.getPeriodo().equals(periodo)) {
                    resultado.add(n);
                }
            }
        }
        return resultado;
    }

    public List<NotaFinal> getNotasFinales() {
        List<NotaFinal> todas = new ArrayList<>();
        notasFinales.values().forEach(todas::addAll);
        return todas;
    }
}
