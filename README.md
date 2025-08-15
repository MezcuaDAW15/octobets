# Octobets (Backend · Spring Boot)

**Octobets** es una plataforma web para organizar **apuestas informales** entre amigos o comunidades, con **autenticación JWT**, **gestión de fichas** y **pagos simulados vía Stripe**. Permite crear apuestas personalizadas, participar con fichas virtuales y registrar el historial económico del usuario. El foco es abrir la puerta a apostar en contextos cotidianos (amistosos, locales), no competir con casas de apuestas tradicionales. :contentReference[oaicite:0]{index=0}

> Este repositorio contiene el **backend (Spring Boot, JDK 21)**.  
> El **frontend (Angular 19 + Tailwind)** está en: **https://github.com/MezcuaDAW15/octobets-front**.

---

## 📎 Memoria del PFC

La memoria completa del Proyecto Final de Ciclo está aquí:

- `docs/Memoria-Octobets-DavidMezcuaDiago.pdf`

En la memoria encontrarás requisitos, diseño (ERD, clases, despliegue), decisiones de arquitectura, planificación real, pruebas y futuras ampliaciones como cierre automático, notificaciones en tiempo real o nuevas pasarelas de pago. :contentReference[oaicite:1]{index=1}

---

## ✨ Características principales

- **Apuestas personalizadas**: creación, publicación y resolución manual por el creador. :contentReference[oaicite:2]{index=2}
- **Sistema de fichas** con **Stripe** para comprar fichas y registro de transacciones. :contentReference[oaicite:3]{index=3}
- **Autenticación segura** con **JWT + Spring Security**. :contentReference[oaicite:4]{index=4}
- **Historial económico** del usuario (ingresos, apuestas, beneficios/pérdidas). :contentReference[oaicite:5]{index=5}
- **Arquitectura por capas** y paquetes: `model.dto`, `repository.dao`, `repository.entity`, `service`, `rest.controller`. :contentReference[oaicite:6]{index=6}
- **Despliegue** con **Docker** y **CI/CD en GitHub Actions**. :contentReference[oaicite:7]{index=7}

---

## 🧱 Stack

- **Backend**: Java 21, Spring Boot, Spring Security, JWT, Maven
- **BD**: MySQL 8
- **Pagos**: Stripe (modo test)
- **Infra**: Docker, GitHub Actions

---

## 🚀 Puesta en marcha

### Opción A) Local con Maven

Requisitos: JDK 21, Maven, MySQL 8.

1. Crea una BD vacía (por ejemplo, `octobets`).
2. Configura `application.properties` (usuario/clave de MySQL, JWT secret, Stripe keys).
3. Lanza:
   ```bash
   mvn spring-boot:run
   ```
****
