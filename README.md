# 💬 Chat TCP/UDP en Java

> Proyecto Final · Redes  · ITSON

Aplicación de chat cliente-servidor desarrollada en Java que permite comunicación en tiempo real mediante sockets TCP y UDP, con soporte para mensajes grupales y privados entre múltiples clientes.

---

## 📋 Descripción

El sistema implementa una arquitectura cliente-servidor donde un servidor central gestiona las conexiones y retransmite mensajes entre los clientes conectados. Se utilizan hilos independientes para manejar cada cliente de forma concurrente, permitiendo que varios usuarios chatíen simultáneamente sin bloqueos.

---

## 👥 Integrantes

|          Nombre          |         Matrícula           |
|--------------------------|-----------------------------|
| Luis Alonso Ortiz        |         00000262702         |
| Angel Alberto Perez      |         00000269680         |

---

## 🛠️ Tecnologías utilizadas

- **Java 17+**
- **TCP Sockets** — conexión confiable orientada a flujo
- **UDP Sockets** — comunicación sin conexión de baja latencia
- **Multihilos** (`Thread` / `Runnable`) — manejo concurrente de clientes
- **Maven / IntelliJ IDEA / Netbeans

---

## 📁 Estructura del proyecto

```
chat-tcp-udp-java/
├── src/
│   ├── servidor/
│   │   ├── ServidorTCP.java        # Lógica del servidor TC
|   |   └── ServidorUDP.java        # Logica dek servidor UDP
│   ├── cliente/
|   |   ├── ClienteUDP.java        # Lógica del cliente UDP
│   │   ├── ClienteTCP.java        # Lógica del cliente TCP
|   |   └── Usuario.java           # Logica del usuario
│   └── main/
│       └── main.java          # clase encargada de la ejecucion del proyecto
├── screenshots/
│   ├── servidor_activo.png
│   ├── registro_usuario.png
│   ├── chat_grupal.png
│   └── mensaje_privado.png
├── .gitignore
└── README.md
```

---

## 🚀 Cómo ejecutar

### Requisitos previos

- Java JDK 17 o superior instalado
- Terminal / Git Bash

### Compilar el proyecto

```bash
javac -d bin src/**/*.java
```

### Iniciar el servidor

```bash
java -cp bin servidor.ServidorMain
```

El servidor escuchará en el puerto `7778` (TCP) y `7778` (UDP) por defecto.

### Conectar un cliente

```bash
java -cp bin cliente.ClienteMain
```

Se te pedirá ingresar la IP del servidor y tu nombre de usuario.

---

## ⚙️ Funcionalidades

- [x] Registro de usuario con nombre único
- [x] Mensajes grupales visibles para todos los conectados
- [x] Mensajes privados entre usuarios (`/msg <usuario> <mensaje>`)
- [x] Lista de usuarios conectados (`/usuarios`)
- [ ] Desconexión controlada *(en desarrollo)*
- [ ] Historial de mensajes persistente *(en desarrollo)*

---

## 📸 Capturas de pantalla

### Servidor activo
(<img width="1237" height="450" alt="image" src="https://github.com/user-attachments/assets/a5851fe0-5fa0-4031-81fc-15f62ce51796" />)

### Registro de usuario
(<img width="938" height="516" alt="Captura de pantalla 2026-05-17 235018" src="https://github.com/user-attachments/assets/b9cc478d-4ed6-4b01-af1d-9921c2134866" />)

### Chat grupal

(<img width="1062" height="518" alt="image" src="https://github.com/user-attachments/assets/6aef9304-6f48-4c41-8111-487463df9844" />)

### Mensaje privado
(<img width="1053" height="443" alt="image" src="https://github.com/user-attachments/assets/119b4b0b-e6f6-4143-a775-ffcf71ba9611" />
                    <img width="1132" height="522" alt="image" src="https://github.com/user-attachments/assets/56451d30-c8d1-44eb-8fed-0e3234170f36" />)

---

## 🌐 Protocolo de comunicación

Los mensajes siguen el formato:

```
TIPO|ORIGEN|DESTINO|CONTENIDO
```

| Tipo   | Descripción |
|--------|-------------|
| `MSG`  | Mensaje grupal |
| `PRIV` | Mensaje privado |
| `REG`  | Registro de nuevo usuario |
| `BYE`  | Desconexión de usuario |
| `LIST` | Solicitud de lista de usuarios |

---

## 📄 Licencia

Proyecto académico — ITSON · Redes · 2026
