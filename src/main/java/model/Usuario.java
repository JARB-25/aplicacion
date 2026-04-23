// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package model;

import java.io.Serializable;
import java.util.Objects;

public class Usuario implements Serializable {
   private String email;
   private String passwordHash;
   private Rol rol;

   public Usuario(String email, String passwordHash, Rol rol) {
      this.email = email;
      this.passwordHash = passwordHash;
      this.rol = rol;
   }

   public String getEmail() {
      return this.email;
   }

   public String getPasswordHash() {
      return this.passwordHash;
   }

   public Rol getRol() {
      return this.rol;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof Usuario)) {
         return false;
      } else {
         Usuario u = (Usuario)o;
         return Objects.equals(this.email, u.email);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.email});
   }
}
