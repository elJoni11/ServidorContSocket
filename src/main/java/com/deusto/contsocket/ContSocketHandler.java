package com.deusto.contsocket;

import java.io.*;
import java.net.Socket;

public class ContSocketHandler extends Thread {
    private Socket clientSocket;

    public ContSocketHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (
            // 1. Abrir un lector para recibir datos de texto del socket
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            String notificationMessage;
            
            // 2. Leer la línea completa enviada por el cliente (Servidor Ecoembes)
            if ((notificationMessage = in.readLine()) != null) {
                System.out.println("📨 Notificación recibida: " + notificationMessage);
                
                // 3. Procesar la notificación (número de contenedores y envases)
                processAssignment(notificationMessage);
            }

        } catch (IOException e) {
            System.err.println("❌ ERROR al manejar la comunicación con el cliente: " + e.getMessage());
        } finally {
            try {
                // 4. Asegurarse de cerrar el socket de comunicación con el cliente
                clientSocket.close(); 
                System.out.println("✔️ Conexión con cliente cerrada.");
            } catch (IOException e) {
                System.err.println("Error al cerrar el socket: " + e.getMessage());
            }
        }
    }

    /**
     * Lógica para procesar la notificación de asignación de contenedores.
     * Esta información le permite a la planta "planificar su jornada de trabajo"[cite: 35].
     */
    private void processAssignment(String data) {
        // En un escenario real, aquí se actualizaría la base de datos de PlasSB_DB.
        
        // Asumiendo un formato simple: "CONTENEDORES: X; ENVASES: Y"
        // Aquí podrías parsear la cadena para extraer X e Y.
        
        System.out.println("⭐ ContSocket Ltd.: Asignación recibida y lista para planificación. ⭐");
    }
}