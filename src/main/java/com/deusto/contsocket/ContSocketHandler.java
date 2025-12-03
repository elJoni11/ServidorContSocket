package com.deusto.contsocket;

import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.util.Map;

/**
 * Maneja la comunicación con un cliente usando el protocolo del Prototipo 2
 * 
 * PROTOCOLO:
 * - GET_CAPACITY:YYYY-MM-DD → OK:<toneladas> o ERR:NO_DATA
 * - NOTIFICAR_ASIGNACION:id:contenedores:fecha:cantidad → OK o ERR
 * - CONSULTAR_ESTADO → OK:OPERATIVA:<capacidadActual>
 * - PING → PONG
 */
public class ContSocketHandler extends Thread {
    private Socket clientSocket;
    private Map<LocalDate, Integer> capacidades; // Referencia a las capacidades en memoria
    
    public ContSocketHandler(Socket socket, Map<LocalDate, Integer> capacidades) {
        this.clientSocket = socket;
        this.capacidades = capacidades;
    }
    
    @Override
    public void run() {
        try (
            // Abrir lector y escritor para comunicación bidireccional
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String comando;
            
            // Leer comando del cliente
            if ((comando = in.readLine()) != null) {
                System.out.println("📨 Comando recibido: " + comando);
                
                // Procesar comando y obtener respuesta
                String respuesta = procesarComando(comando);
                
                // Enviar respuesta al cliente
                out.println(respuesta);
                System.out.println("📤 Respuesta enviada: " + respuesta);
            }
            
        } catch (IOException e) {
            System.err.println("❌ ERROR al manejar comunicación: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                System.out.println("✔️ Conexión cerrada.\n");
            } catch (IOException e) {
                System.err.println("Error al cerrar socket: " + e.getMessage());
            }
        }
    }
    
    /**
     * Procesa un comando según el protocolo del Prototipo 2
     * Formato: COMANDO:PARAMETRO1:PARAMETRO2:...
     */
    private String procesarComando(String comando) {
        if (comando == null || comando.isEmpty()) {
            return "ERR:COMANDO_VACIO";
        }
        
        String[] partes = comando.split(":");
        String accion = partes[0].toUpperCase();
        
        try {
            switch (accion) {
                case "PING":
                    return "PONG";
                    
                case "GET_CAPACITY":
                    return procesarGetCapacity(partes);
                    
                case "NOTIFICAR_ASIGNACION":
                    return procesarNotificacion(partes);
                    
                case "CONSULTAR_ESTADO":
                    return procesarEstado();
                    
                default:
                    return "ERR:COMANDO_DESCONOCIDO";
            }
        } catch (Exception e) {
            return "ERR:ERROR_PROCESANDO";
        }
    }
    
    /**
     * GET_CAPACITY:YYYY-MM-DD
     * Ejemplo: GET_CAPACITY:2024-12-05
     */
    private String procesarGetCapacity(String[] partes) {
        if (partes.length < 2) {
            return "ERR:FORMATO_INVALIDO";
        }
        
        try {
            LocalDate fecha = LocalDate.parse(partes[1]);
            Integer capacidad = capacidades.get(fecha);
            
            if (capacidad == null) {
                return "ERR:NO_DATA";
            }
            
            return "OK:" + capacidad;
            
        } catch (Exception e) {
            return "ERR:FECHA_INVALIDA";
        }
    }
    
    /**
     * NOTIFICAR_ASIGNACION:asignacionID:C001,C002:YYYY-MM-DD:cantidad
     * Ejemplo: NOTIFICAR_ASIGNACION:ASG001:C001,C002:2024-12-05:50
     * 
     * Actualiza la capacidad disponible restando la cantidad asignada
     */
    private String procesarNotificacion(String[] partes) {
        if (partes.length < 5) {
            return "ERR:PARAMETROS_INSUFICIENTES";
        }
        
        try {
            String asignacionID = partes[1];
            String contenedores = partes[2];
            LocalDate fecha = LocalDate.parse(partes[3]);
            int cantidad = Integer.parseInt(partes[4]);
            
            // Verificar que existe capacidad para esa fecha
            Integer capacidadActual = capacidades.get(fecha);
            if (capacidadActual == null) {
                return "ERR:NO_DATA";
            }
            
            // Verificar que hay suficiente capacidad
            if (capacidadActual < cantidad) {
                return "ERR:CAPACIDAD_INSUFICIENTE";
            }
            
            // Actualizar capacidad disponible
            capacidades.put(fecha, capacidadActual - cantidad);
            
            System.out.println("⭐ Asignación " + asignacionID + " registrada");
            System.out.println("   Contenedores: " + contenedores);
            System.out.println("   Fecha: " + fecha);
            System.out.println("   Cantidad: " + cantidad + " toneladas");
            System.out.println("   Nueva capacidad: " + capacidades.get(fecha) + " toneladas");
            
            return "OK:Asignación registrada";
            
        } catch (Exception e) {
            return "ERR:DATOS_INVALIDOS";
        }
    }
    
    /**
     * CONSULTAR_ESTADO
     * Devuelve el estado operativo de la planta
     */
    private String procesarEstado() {
        LocalDate hoy = LocalDate.now();
        Integer capacidadHoy = capacidades.get(hoy);
        int capacidad = (capacidadHoy != null) ? capacidadHoy : 0;
        
        return "OK:OPERATIVA:" + capacidad;
    }
}
