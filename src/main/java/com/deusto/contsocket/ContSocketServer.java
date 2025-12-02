package com.deusto.contsocket;

import java.io.*;
import java.net.*;

public class ContSocketServer {
    // Puerto definido para la comunicación por sockets con ContSocket Ltd. [cite: 66]
    private static final int PORT = 9000; 

    public static void main(String[] args) {
        try (
            // 1. Crear el ServerSocket: abre un puerto para escuchar
            ServerSocket serverSocket = new ServerSocket(PORT) 
        ) {
            System.out.println("✅ Servidor ContSocket Ltd. activo y escuchando en el puerto " + PORT);

            // Bucle principal para aceptar conexiones indefinidamente
            while (true) {
                // 2. serverSocket.accept(): Bloquea el programa hasta que llega una conexión del cliente (Ecoembes Server)
                Socket clientSocket = serverSocket.accept(); 
                System.out.println("\n🌐 Conexión aceptada desde: " + clientSocket.getInetAddress().getHostAddress());

                // 3. Manejar la conexión en un hilo (Thread) separado para no bloquear el bucle principal
                new ContSocketHandler(clientSocket).start();
            }
        } catch (IOException e) {
            System.err.println("❌ ERROR del Servidor ContSocket: " + e.getMessage());
        }
    }
}