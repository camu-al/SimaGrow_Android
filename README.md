# 🌱 SimaGrow – Sistema de Gestión de Incidencias

![Status](https://img.shields.io/badge/Status-Completado-green)
![Category](https://img.shields.io/badge/Category-Productividad_/_Gestión-purple)
![Stack](https://img.shields.io/badge/Stack-Kotlin_|_Room_|_Coroutines-blue)

**SimaGrow** es una aplicación móvil nativa diseñada para centralizar la gestión de incidencias, usuarios, cursos y actividades. El proyecto se enfoca en ofrecer una experiencia de usuario fluida y un sistema de persistencia robusto, garantizando la integridad de los datos en entornos de gestión educativa o corporativa.

---

## 🎯 Propósito del Proyecto

El objetivo de SimaGrow fue construir una herramienta de gestión interna que destaque por su eficiencia técnica y facilidad de uso. Se puso especial énfasis en el manejo de datos locales y el ciclo de vida de la aplicación.

* **Arquitectura de Datos:** Implementación de un esquema relacional local complejo mediante Room.
* **Seguridad de Sesión:** Gestión de credenciales y estados de usuario persistentes.
* **Estándar Visual:** Adopción de las guías de Material Design para una interfaz moderna y coherente.

---

## 📐 Arquitectura del Sistema

He aplicado una estructura de capas para separar la interfaz de usuario de la lógica de acceso a datos, utilizando herramientas oficiales de Jetpack para asegurar la estabilidad del sistema.

```mermaid
graph TD
    A[UI - Activities/Fragments] --> B[ViewBinding Layer]
    B --> C[Business Logic & Coroutines]
    C --> D[(Room Database)]
    C --> E[SharedPreferences]
    
    style A fill:#3DDC84,stroke:#333,color:#fff
    style D fill:#336791,stroke:#333,color:#fff
    style E fill:#FF9900,stroke:#333,color:#000
```
## 💻 Implementación Técnica

### 🛠️ Capa de Persistencia (Data Layer)
* **Room Database:** Implementación de una base de datos relacional local sobre SQLite para la gestión de entidades (Usuarios, Incidencias, Cursos y Actividades) con integridad referencial.
* **Kotlin Coroutines:** Gestión de la concurrencia mediante programación asíncrona, asegurando que las operaciones pesadas de base de datos se ejecuten fuera del hilo principal (Main Thread).
* **SharedPreferences:** Uso de almacenamiento clave-valor para la persistencia de la sesión del usuario y flags de estado de la aplicación.

### 🎨 Interfaz de Usuario (UI Layer)
* **Material Design Components:** Adopción de estándares modernos de Google para componentes como *Cards*, *Floating Action Buttons* y *Input Layouts*, garantizando una UX intuitiva.
* **ViewBinding:** Sustitución de `findViewById` por vinculación de vistas para mejorar la seguridad del código, evitando errores de punteros nulos (`NullPointerException`) y optimizando el rendimiento.
* **Navegación Estructurada:** Control de flujos condicionales para el acceso seguro a la aplicación mediante validaciones en tiempo real en los formularios de Login y Registro.

## 🛠️ Stack Tecnológico

| Categoría | Herramientas / Tecnologías |
|-----------|-----------------------------|
| **Lenguaje** | Kotlin |
| **Persistencia** | Room (SQLite), SharedPreferences |
| **Asincronía** | Kotlin Coroutines |
| **UI Framework** | Material Design 3, XML |
| **Arquitectura** | Layered Architecture (Capa de datos y UI) |
| **Herramientas** | Android Studio, ViewBinding |

## 📸 Galería de la Aplicación

A continuación se muestran capturas reales de la interfaz de usuario, destacando el flujo de navegación y el diseño basado en Material Design.

<table align="center">
  <tr>
    <td valign="top" align="center">
      <img src="https://github.com/user-attachments/assets/7fc168f0-2fb9-46e2-875c-f88fd834cfd5" width="200" /><br>
      <sub>Login</sub><br><br>
      <img src="https://github.com/user-attachments/assets/e513dd63-fad6-42f8-9f67-24976e8e2959" width="200" /><br>
      <sub>Registro</sub>
    </td>
    <td valign="top" align="center">
      <img src="https://github.com/user-attachments/assets/eebb6680-1cf7-4af1-a220-15fa7197d883" width="200" /><br>
      <sub>Home</sub><br><br>
      <img src="https://github.com/user-attachments/assets/9f8dbfef-ecd4-4654-bae9-3b1ca903899a" width="200" /><br>
      <sub>Actividades</sub>
    </td>
    <td valign="top" align="center">
      <img src="https://github.com/user-attachments/assets/87500d25-b462-45ca-bb5c-eaa42162947f" width="200" /><br>
      <sub>Listado de mensajes al professorado</sub><br><br>
      <img src="https://github.com/user-attachments/assets/87fba2f6-ba23-4cf6-98cc-080de1a08262" width="200" /><br>
      <sub>Detalle de Curso</sub>
    </td>
  </tr>
  <tr>
    <td valign="top" align="center">
      <img src="https://github.com/user-attachments/assets/6d7438ad-e80b-4219-9b9e-8c8ba183ddb2" width="200" /><br>
      <sub>Listado de Incidencias</sub>
    </td>
    <td valign="top" align="center">
      <img src="https://github.com/user-attachments/assets/27b92eea-2ebe-4c88-b78e-dbe96e1407b3" width="200" /><br>
      <sub>Creación de Datos</sub>
    </td>
    <td valign="top" align="center">
      <img src="https://github.com/user-attachments/assets/eebb6680-1cf7-4af1-a220-15fa7197d883" width="200" style="opacity: 0.5;" /><br>
      <sub>UX Flow</sub>
    </td>
  </tr>
</table>

## 👤 Contacto
<div align="left">
  <a href="https://www.linkedin.com/in/camu-al/" target="_blank">
    <img src="https://img.shields.io/static/v1?message=LinkedIn&logo=linkedin&label=&color=0077B5&logoColor=white&style=flat" height="40" />
  </a>

  <a href="mailto:camudeveloper@gmail.com" target="_blank">
    <img src="https://img.shields.io/static/v1?message=Gmail&logo=gmail&label=&color=D14836&logoColor=white&style=flat" height="40" />
  </a>
</div>

