/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.proyecto_final_redes;

/**
 *
 * @author Luis
 */
import Servidor.TCPServer;
import Servidor.UDPServer;
import Cliente.TCPCliente;
import Cliente.UDPCliente;
import java.util.Scanner;

public class Proyecto_Final_Redes {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== LAUNCHER DE CHAT ===");
        System.out.println();

        System.out.println("Elige el protocolo:");
        System.out.println("  1. TCP");
        System.out.println("  2. UDP");
        System.out.print("Opción: ");
        int proto = sc.nextInt();

        System.out.println();
        System.out.println("Elige el modo:");
        System.out.println("  1. Servidor");
        System.out.println("  2. Cliente");
        System.out.print("Opción: ");
        int modo = sc.nextInt();

        System.out.println();

        try {
            switch (proto) {
                case 1 -> {
                    if (modo == 1) {
                        System.out.println("Iniciando Servidor TCP...");
                        TCPServer server = new TCPServer();
                        server.start();
                    } else {
                        System.out.println("Iniciando Cliente TCP...");
                        TCPCliente cliente = new TCPCliente();
                        cliente.startReceiving();
                    }
                }
                case 2 -> {
                    if (modo == 1) {
                        System.out.println("Iniciando Servidor UDP...");
                        UDPServer server = new UDPServer();
                        server.start();
                    } else {
                        System.out.println("Iniciando Cliente UDP...");
                        new UDPCliente();
                    }
                }
                default -> System.out.println("Opción inválida.");
            }
        } catch (Exception e) {
            System.err.println("Error al iniciar: " + e.getMessage());
        }
    }
}