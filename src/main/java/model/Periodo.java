package model;

import java.time.LocalDate;
import java.io.Serializable;

public class Periodo implements Serializable{

    private String nombre;          // Ej: "2024-1"
    private LocalDate inicio;
    private LocalDate fin;
    private boolean activo;

    public Periodo(String nombre, LocalDate inicio, LocalDate fin) {
        this.nombre = nombre;
        this.inicio = inicio;
        this.fin = fin;
        this.activo = true;
    }

    public boolean estaActivo() {
        LocalDate hoy = LocalDate.now();
        return activo && !hoy.isBefore(inicio) && !hoy.isAfter(fin);
    }

    public String getNombre()     { return nombre; }
    public LocalDate getInicio()  { return inicio; }
    public LocalDate getFin()     { return fin; }
    public boolean isActivo()     { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
