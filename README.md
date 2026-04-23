# LMS

Proyecto JavaFX con Maven Wrapper listo para clonar y compilar en otra computadora.

## Requisitos

- JDK 17 o superior instalado
- `JAVA_HOME` apuntando a esa instalacion del JDK

## Compilar

En Windows:

```powershell
.\mvnw.cmd clean package
```

En Linux o macOS:

```bash
./mvnw clean package
```

## Ejecutar

En Windows:

```powershell
.\mvnw.cmd javafx:run
```

En Linux o macOS:

```bash
./mvnw javafx:run
```

## Notas

- No hace falta instalar Maven globalmente: el proyecto ya incluye Maven Wrapper.
- La carpeta `data/` se genera automaticamente en tiempo de ejecucion y no se versiona.
- Los archivos generados de compilacion tampoco se versionan.
- Si en Windows el wrapper no encuentra Java, configura `JAVA_HOME` y vuelve a ejecutar el comando.
- Ejemplo en Windows PowerShell si ya tienes un JDK instalado:

```powershell
$env:JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-17"
$env:Path="$env:JAVA_HOME\bin;$env:Path"
.\mvnw.cmd clean package
```
