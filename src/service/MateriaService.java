package service;

import model.Materia;
import java.util.*;

public class MateriaService {

    private List<Materia> materias;

    public MateriaService() {
        materias = FileService.cargarMaterias();
    }

    public void crearMateria(String n, String c, String prof) {
        materias.add(new Materia(n, c, prof, new ArrayList<>()));
        FileService.guardarMaterias(materias);
    }

    public void inscribir(String cod, String est) {
        for (Materia m : materias) {
            if (m.getCodigo().equals(cod)) {
                m.agregarEstudiante(est);
                FileService.guardarMaterias(materias);
                return;
            }
        }
    }

    public List<Materia> obtenerTodas() {
        return materias;
    }

    public List<Materia> obtenerDeEstudiante(String est) {
        List<Materia> res = new ArrayList<>();
        for (Materia m : materias) {
            if (m.getEstudiantes().contains(est)) res.add(m);
        }
        return res;
    }
}