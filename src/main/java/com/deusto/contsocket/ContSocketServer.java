package com.deusto.contsocket;

import java.io.*;
import java.net.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 
 * Servidor TCP Socket para ContSocket Ltd. - Prototipo 2
 
 **/
public class ContSocketServer {
    // Puerto según especificación del Prototipo 2 [cambio: antes era 9000]
    private static final int PORT = 9000;
    
    // Persistencia en memoria (HashMap) - Prototipo 2
    // Almacena capacidades disponibles por fecha
    private static Map<LocalDate, Integer> capacidades = new HashMap<>();
    
    public static void main(String[] args) {
        // Inicializar datos en memoria
        inicializarCapacidades();
        
        try (
            // 1. Crear el ServerSocket: abre un puerto para escuchar
            ServerSocket serverSocket = new ServerSocket(PORT)
        ) {
            System.out.println("═══════════════════════════════════════════════");
            System.out.println("✅ Servidor ContSocket Ltd. - Prototipo 2");
            System.out.println("📡 Puerto: " + PORT);
            System.out.println("💾 Persistencia: En memoria (HashMap)");
            System.out.println("═══════════════════════════════════════════════");
            
            // Bucle principal para aceptar conexiones indefinidamente
            while (true) {
                // 2. serverSocket.accept(): Bloquea hasta que llega una conexión
                Socket clientSocket = serverSocket.accept();
                System.out.println("\n🌐 Conexión desde: " + clientSocket.getInetAddress().getHostAddress());
                
                // 3. Manejar la conexión en un hilo separado
                new ContSocketHandler(clientSocket, capacidades).start();
            }
        } catch (IOException e) {
            System.err.println("❌ ERROR del Servidor ContSocket: " + e.getMessage());
        }
    }
    
    /**
     * Inicializa capacidades para los próximos 30 días
     * Prototipo 2: Persistencia en memoria
     */
    private static void inicializarCapacidades() {
        LocalDate hoy = LocalDate.now();
        
        System.out.println("\n📦 Inicializando capacidades en memoria...");
        
        // Generar 30 días de capacidades (250-350 toneladas)
        for (int i = 0; i < 30; i++) {
            LocalDate fecha = hoy.plusDays(i);
            int capacidad = 250 + (i * 3) % 100;
            capacidades.put(fecha, capacidad);
        }
        
        System.out.println("✅ " + capacidades.size() + " días de capacidad inicializados\n");
    }
}
