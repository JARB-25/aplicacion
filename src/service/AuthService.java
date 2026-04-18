package service;

import model.Usuario;
import java.util.*;

public class AuthService {

    private List<Usuario> usuarios;

    public AuthService() {
        usuarios = FileService.cargarUsuarios();
    }

    public void registrar(String n, String p, String r) {
        usuarios.add(new Usuario(n, p, r));
        FileService.guardarUsuarios(usuarios);
    }

    public Usuario login(String n, String p) {
        for (Usuario u : usuarios) {
            if (u.getNombre().equals(n) && u.getPassword().equals(p)) {
                return u;
            }
        }
        return null;
    }
}
