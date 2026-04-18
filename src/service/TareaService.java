package service;

import model.Tarea;
import java.util.*;

public class TareaService {

    private List<Tarea> tareas;

    public TareaService() {
        tareas = FileService.cargarTareas();
    }

    public void crearParaMateria(String titulo, String desc, String cod, List<String> estudiantes) {
        for (String e : estudiantes) {
            tareas.add(new Tarea(titulo, desc, cod, e, "pendiente"));
        }
        FileService.guardarTareas(tareas);
    }

    public List<Tarea> obtener(String est) {
        List<Tarea> res = new ArrayList<>();
        for (Tarea t : tareas) {
            if (t.getEstudiante().equals(est)) res.add(t);
        }
        return res;
    }

    public void entregar(String titulo, String est) {
        for (Tarea t : tareas) {
            if (t.getTitulo().equals(titulo) && t.getEstudiante().equals(est)) {
                t.setEstado("entregada");
            }
        }
        FileService.guardarTareas(tareas);
    }
}