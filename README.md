# 🎮 Sudoku: Aventura de Tinta

<div align="center">
  <img src="src/main/resources/com/example/demosudoku/Images/Imagenes README/Imagen1.png" width="400" alt="Banner del Proyecto"/>
  
  [![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)](https://github.com/usuario/sudoku-aventura-tinta/releases/tag/v1.0.0)
  [![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
  [![JavaFX](https://img.shields.io/badge/JavaFX-17+-green.svg)](https://openjfx.io/)
</div>

---

## 📖 Descripción General

**Sudoku: Aventura de Tinta** es una implementación moderna y elegante del clásico juego de lógica Sudoku, desarrollada completamente en Java utilizando JavaFX para crear una interfaz gráfica atractiva e intuitiva. Este proyecto combina la esencia tradicional del Sudoku con una experiencia visual envolvente, ofreciendo y características que mejoran la jugabilidad.

El juego está diseñado con una arquitectura limpia y modular, facilitando su mantenimiento y futuras expansiones. Ideal tanto para jugadores casuales como para entusiastas del Sudoku que buscan un desafío mental.

---

## ✨ Características Principales

- 🎨 **Interfaz Gráfica Moderna**: Diseño intuitivo y atractivo desarrollado con JavaFX
- ✅ **Validación en Tiempo Real**: Comprueba automáticamente las jugadas y detecta errores
- 🔢 **Generador de Tableros**: Crea tableros aleatorios únicos y solucionables
- 💡 **Sistema de Pistas**: Ayuda al jugador cuando se encuentra atascado
- 🌙 **Modo Claro/Oscuro**: Personalización visual según preferencias del usuario

---

## 🛠️ Tecnologías y Herramientas

### Lenguajes y Frameworks
- ☕ **Java SE 17+ (Amazon Corretto)**: Lenguaje de programación principal y JDK utilizado
- 🖼️ **JavaFX 17+**: Framework para la interfaz gráfica de usuario
- 📄 **FXML**: Lenguaje de marcado para diseñar interfaces JavaFX

### Herramientas de Desarrollo
- **IntelliJ IDEA**: IDE principal para el desarrollo
- **Scene Builder**: Herramienta visual para diseñar interfaces FXML
- **Maven**: Sistema de gestión de dependencias y construcción del proyecto
- **Git**: Control de versiones distribuido
- **GitHub**: Plataforma de alojamiento y colaboración del código

### Documentación
- **Javadoc**: Generación automática de documentación del código
- **Markdown**: Formato de documentación del proyecto

---

## 📁 Estructura del Proyecto
```
demo-sudoku-master/
│
├── .idea/                              # Configuración de IntelliJ IDEA
├── .mvn/                               # Wrapper de Maven
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com.example.demosudoku/
│   │   │       ├── controller/
│   │   │       │   ├── SudokuGameController.java
│   │   │       │   ├── SudokuHelpController.java
│   │   │       │   ├── SudokuWelcomeController.java
│   │   │       │   └── SudokuWinController.java
│   │   │       │
│   │   │       ├── model/
│   │   │       │   ├── board/
│   │   │       │   │   ├── Board.java
│   │   │       │   │   └── IBoard.java
│   │   │       │   │
│   │   │       │   ├── game/
│   │   │       │   │   ├── Game.java
│   │   │       │   │   ├── GameAbstract.java
│   │   │       │   │   └── IGame.java
│   │   │       │   │
│   │   │       │   └── user/
│   │   │       │       ├── SessionManager.java
│   │   │       │       └── User.java
│   │   │       │
│   │   │       ├── utils/
│   │   │       │   ├── AlertBox.java
│   │   │       │   └── IAlertBox.java
│   │   │       │
│   │   │       ├── view/
│   │   │       │   ├── SudokuGameStage.java
│   │   │       │   ├── SudokuHelpStage.java
│   │   │       │   ├── SudokuWelcomeStage.java
│   │   │       │   └── SudokuWinStage.java
│   │   │       │
│   │   │       └── Main.java           # Clase principal de la aplicación
│   │   │
│   │   ├── resources/
│   │   │   └── com.example.demosudoku/
│   │   │       ├── CSS/
│   │   │       │   └── [estilos CSS]
│   │   │       │
│   │   │       ├── images/             # Recursos gráficos
│   │   │       │   └── favicon.png
│   │   │       │
│   │   │       ├── board-sudoku.fxml   # Interfaz del tablero de juego
│   │   │       ├── menu-sudoku.fxml    # Interfaz del menú principal
│   │   │       ├── rules-sudoku.fxml   # Interfaz de reglas/ayuda
│   │   │       └── win-sudoku.fxml     # Interfaz de pantalla de victoria
│   │   │
│   │   └── module-info.java            # Configuración de módulos Java
│   │
│   └── test/                           # Directorio para pruebas unitarias
│       └── java/
│
├── target/                             # Archivos compilados (generado por Maven)
│
├── .gitignore                          # Archivos ignorados por Git
├── pom.xml                             # Configuración de Maven
└── README.md                           # Documentación del proyecto
```

### 📦 Descripción de Paquetes

#### **Controller**
Controladores JavaFX que manejan la lógica de las vistas:
- `SudokuGameController`: Lógica del tablero de juego
- `SudokuHelpController`: Controlador de la pantalla de ayuda/reglas
- `SudokuWelcomeController`: Controlador del menú de bienvenida
- `SudokuWinController`: Controlador de la pantalla de victoria

#### **Model**
Capa de modelo con la lógica de negocio:
- **board/**: Gestión del tablero de Sudoku
  - `Board`: Implementación del tablero
  - `IBoard`: Interfaz del tablero
- **game/**: Lógica del juego
  - `Game`: Implementación del juego
  - `GameAbstract`: Clase abstracta base
  - `IGame`: Interfaz del juego
- **user/**: Gestión de usuarios y sesiones
  - `SessionManager`: Administrador de sesiones
  - `User`: Modelo de usuario

#### **Utils**
Utilidades y componentes auxiliares:
- `AlertBox`: Cuadros de diálogo personalizados
- `IAlertBox`: Interfaz para alertas

#### **View**
Clases de visualización y gestión de escenas:
- `SudokuGameStage`: Ventana del juego
- `SudokuHelpStage`: Ventana de ayuda
- `SudokuWelcomeStage`: Ventana de bienvenida
- `SudokuWinStage`: Ventana de victoria

#### **Resources**
Recursos de la aplicación:
- **CSS/**: Hojas de estilo para la interfaz
- **images/**: Imágenes e iconos
- **FXML**: Archivos de diseño de interfaz
  - `game-sudoku.fxml`: Diseño del tablero
  - `welcome-sudoku.fxml`: Diseño del menú
  - `help-sudoku.fxml`: Diseño de las reglas
  - `win-sudoku.fxml`: Diseño de la pantalla de victoria

## 🚀 Instalación y Ejecución

### Prerrequisitos

Antes de comenzar, asegúrate de tener instalado:

- ☕ **Java JDK 17 o superior** - [Descargar aquí](https://www.oracle.com/java/technologies/downloads/)
- 📦 **Maven 3.6+** - [Descargar aquí](https://maven.apache.org/download.cgi)
- 🔧 **Git** - [Descargar aquí](https://git-scm.com/downloads)
- 💻 **IntelliJ IDEA** (Recomendado) - [Descargar aquí](https://www.jetbrains.com/idea/download/)

### Verificar Instalaciones
```bash
# Verificar Java
java -version

# Verificar Maven
mvn -version

# Verificar Git
git --version
```

### Pasos de Instalación

#### 1️⃣ Clonar el Repositorio
```bash
git clone https://github.com/usuario/sudoku-aventura-tinta.git
cd sudoku-aventura-tinta
```

#### 2️⃣ Compilar el Proyecto con Maven
```bash
mvn clean install
```

#### 3️⃣ Ejecutar la Aplicación

**Opción A: Usando Maven**
```bash
mvn javafx:run
```

**Opción B: Usando IntelliJ IDEA**
1. Abre el proyecto en IntelliJ IDEA
2. Espera a que Maven descargue las dependencias
3. Busca la clase `Main.java` en `src/main/java/com/sudoku/`
4. Haz clic derecho y selecciona "Run 'Main.main()'"

**Opción C: Usando el JAR ejecutable**
```bash
java -jar target/sudoku-aventura-tinta-1.0.0.jar
```

---

## 🎮 Cómo Jugar

1. **Iniciar el Juego**: Al abrir la aplicación, selecciona el boton jugar
2. **Completar el Tablero**: Rellena las celdas vacías con números del 1 al 6
4. **Reglas del Sudoku**:
   - Cada fila debe contener los números del 1 al 6 sin repetir
   - Cada columna debe contener los números del 1 al 6 sin repetir
   - Cada cuadrícula 2x3 debe contener los números del 1 al 6 sin repetir
5. **Usar Pistas**: Si te atascas, puedes usar el botón de pistas (limitadas por partida)
6. **Validar Solución**: El juego te notificará automáticamente cuando completes el tablero correctamente(te saltara la pantalla ganar)

---

## 🔄 Uso de Git y Control de Versiones

### Flujo de Trabajo con Git

Este proyecto sigue un flujo de trabajo estructurado utilizando Git y GitHub para el control de versiones.

#### Configuración Inicial
```bash
# Configurar usuario de Git
git config --global user.name "Tu Nombre"
git config --global user.email "tu.email@ejemplo.com"

# Clonar el repositorio
git clone https://github.com/usuario/sudoku-aventura-tinta.git
cd sudoku-aventura-tinta
```

#### Estructura de Ramas

- **`main`**: Rama principal con el código estable y versiones lanzadas
- **`juan-dev`**: Rama de desarrollo del colaborador Juan Manuel Muñoz Delgado
- **`andres-dev`**: Ramas de desarrollo del colaborador de Andres Felipe Muñoz Moreno

### Historial de Versiones

| Versión | Fecha | Descripción |
|---------|-------|-------------|
| v1.0.0 | 15/10/2025 | Lanzamiento inicial con todas las características principales |

---

## 📸 Capturas de Pantalla

### Menú Principal
<img src="src/main/resources/com/example/demosudoku/Images/Imagenes%20README/Imagen2.png" width="400">

### Tablero de Juego
<img src="src/main/resources/com/example/demosudoku/Images/Imagenes%20README/Imagen3.png" width="400">

### Selección de Reglas
<img src="src/main/resources/com/example/demosudoku/Images/Imagenes README/imagen4.png" width="400">

### Juego Completado
<img src="src/main/resources/com/example/demosudoku/Images/Imagenes README/imagen5.png" width="400">




---



---

## 👥 Autores

Este proyecto fue desarrollado por:

- **Andres Felipe Muñoz Moreno** - Desarrollador
- **Juan Manuel Muñoz Delgado** - Desarrollador


## 📞 Contacto

- 📧 Email: andres.f.munoz.m@correounivalle.edu.co | juan.munoz.delgado@correounivalle.edu.co
- 🐙 GitHub: [@juanmunozdelgado-oss]([https://github.com/usuario1](https://github.com/juanmunozdelgado-oss)) | [@AndresMunozMoreno
](https://github.com/AndresMunozMoreno)
- 🌐 Website del Proyecto: [https://sudoku-aventura-tinta.github.io](https://sudoku-aventura-tinta.github.io)

---

<div align="center">
  <p>Hecho con ❤️ y ☕ por Andres Felipe Muñoz Moreno y Juan Manuel Muñoz Delgado</p>
  <p>© 2025 Sudoku: Aventura de Tinta - Versión 1.0.0</p>
  
</div>
