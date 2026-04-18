package model;

public class Usuario {
    private String nombre;
    private String password;
    private String rol;

    public Usuario(String nombre, String password, String rol) {
        this.nombre = nombre;
        this.password = password;
        this.rol = rol;
    }

    public String getNombre() { return nombre; }
    public String getPassword() { return password; }
    public String getRol() { return rol; }

    public String toFileString() {
        return nombre + "," + password + "," + rol;
    }

    public static Usuario fromFileString(String linea) {
        String[] p = linea.split(",");
        return new Usuario(p[0], p[1], p[2]);
    }
}
