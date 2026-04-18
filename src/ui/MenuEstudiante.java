package ui;

import service.*;
import model.*;
import java.util.*;

public class MenuEstudiante {

    private Scanner sc = new Scanner(System.in);
    private MateriaService ms = new MateriaService();
    private TareaService ts = new TareaService();

    public void mostrar(String est) {
        while (true) {
            System.out.println("1.Materias 2.Inscribir 3.Tareas 4.Entregar 5.Salir");
            int op = sc.nextInt(); sc.nextLine();

            if(op==1){
                for(Materia m: ms.obtenerDeEstudiante(est))
                    System.out.println(m.getNombre());
            }

            if(op==2){
                System.out.print("Codigo: ");
                ms.inscribir(sc.nextLine(), est);
            }

            if(op==3){
                for(Tarea t: ts.obtener(est))
                    System.out.println(t.getTitulo()+"-"+t.getEstado());
            }

            if(op==4){
                System.out.print("Titulo: ");
                ts.entregar(sc.nextLine(), est);
            }

            if(op==5) return;
        }
    }
}
