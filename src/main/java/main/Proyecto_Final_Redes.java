/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package main;

import Cliente.TCPCliente;
import Cliente.UDPCliente;
import Servidor.TCPServer;
import Servidor.UDPServer;
import java.util.Scanner;

/**
 *
 * @author Luis
 */
public class Proyecto_Final_Redes {

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        
        
        while(true){ 
            System.out.println("Ingrese el tipo de protocolo de comunicacion TCP/UDP o S para salir");
            String protocolo = sc.nextLine();
        
            
            switch (protocolo.toUpperCase()) {
            case "TCP":
                new TCPServer();
                new TCPCliente();
                break;
            case "UDP":
                new UDPServer();
                new UDPCliente();
                break;
            case "S":
                System.out.println("Saliendo del sistema");
                return;
            default:
                System.out.println("Debe ingresar TCP o UDP");
            }
        }
        
        
    }
}
