package com.puj.parqueadero.presentacion;

import com.puj.parqueadero.excepciones.ParqueaderoException;
import com.puj.parqueadero.modelo.Carro;
import com.puj.parqueadero.modelo.CarroElectrico;
import com.puj.parqueadero.modelo.Convenio;
import com.puj.parqueadero.modelo.Moto;
import com.puj.parqueadero.modelo.Tiquete;
import com.puj.parqueadero.modelo.Vehiculo;
import com.puj.parqueadero.negocio.IParqueadero;
import com.puj.parqueadero.negocio.Parqueadero;
import com.puj.parqueadero.persistencia.ManejoArchivos;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Consola {

    private IParqueadero sistema;
    private Scanner lector;

    public Consola() {
        this.sistema = new Parqueadero();
        this.lector = new Scanner(System.in);
    }

    public static void main(String[] args) {

        Consola consola = new Consola();
        ManejoArchivos archivos = new ManejoArchivos();
        DateTimeFormatter formatoFechaHora = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        int opcion = -1;
        while (opcion != 0) {

            System.out.println();
            System.out.println("===== PARQUEADERO PLAZA CENTRAL =====");
            System.out.println("1. Cargar mensualidades");
            System.out.println("2. Procesar movimientos");
            System.out.println("3. Registrar una entrada");
            System.out.println("4. Registrar una salida");
            System.out.println("5. Ver los vehiculos adentro");
            System.out.println("6. Cierre de caja de un dia");
            System.out.println("7. Placas frecuentes");
            System.out.println("8. Guardar el sistema");
            System.out.println("9. Cargar el sistema");
            System.out.println("0. Salir");
            System.out.print("Opcion: ");

            String texto = consola.lector.nextLine().trim();

            try {
                opcion = Integer.parseInt(texto);

                if (opcion == 1) {
                    System.out.print("Carpeta de los archivos: ");
                    String carpeta = consola.lector.nextLine().trim();
                    List<String> lineas = archivos.cargarMensualidades(carpeta + "/Mensualidades.txt", consola.sistema);
                    for (String linea : lineas) {
                        System.out.println(linea);
                    }
                    System.out.println("Lineas procesadas: " + lineas.size());

                } else if (opcion == 2) {
                    System.out.print("Carpeta de los archivos: ");
                    String carpeta = consola.lector.nextLine().trim();
                    List<String> lineas = archivos.procesarMovimientos(carpeta + "/Movimientos.txt", consola.sistema);
                    for (String linea : lineas) {
                        System.out.println(linea);
                    }
                    System.out.println("Lineas procesadas: " + lineas.size());

                } else if (opcion == 3) {
                    System.out.print("Placa: ");
                    String placa = consola.lector.nextLine().trim();
                    System.out.print("Tipo (CARRO, MOTO o ELECTRICO): ");
                    String tipo = consola.lector.nextLine().trim().toUpperCase();
                    System.out.print("Fecha y hora de entrada (aaaa-mm-dd hh:mm): ");
                    LocalDateTime entrada = LocalDateTime.parse(consola.lector.nextLine().trim(), formatoFechaHora);

                    Vehiculo vehiculo;
                    if (tipo.equals("CARRO")) {
                        vehiculo = new Carro(placa);
                    } else if (tipo.equals("MOTO")) {
                        vehiculo = new Moto(placa);
                    } else if (tipo.equals("ELECTRICO")) {
                        vehiculo = new CarroElectrico(placa);
                    } else {
                        throw new ParqueaderoException("Tipo de vehiculo desconocido: '" + tipo + "'.");
                    }

                    Tiquete tiquete = consola.sistema.registrarEntrada(vehiculo, entrada);
                    System.out.println(tiquete);

                } else if (opcion == 4) {
                    System.out.print("Placa: ");
                    String placa = consola.lector.nextLine().trim();
                    System.out.print("Fecha y hora de salida (aaaa-mm-dd hh:mm): ");
                    LocalDateTime salida = LocalDateTime.parse(consola.lector.nextLine().trim(), formatoFechaHora);
                    System.out.print("Convenio (NINGUNO, ALMACEN, RESTAURANTE o CINE): ");
                    String textoConvenio = consola.lector.nextLine().trim().toUpperCase();

                    Convenio convenio = Convenio.NINGUNO;
                    if (textoConvenio.length() > 0) {
                        try {
                            convenio = Convenio.valueOf(textoConvenio);
                        } catch (IllegalArgumentException e) {
                            throw new ParqueaderoException("Convenio desconocido: '" + textoConvenio + "'.");
                        }
                    }

                    Tiquete tiquete = consola.sistema.registrarSalida(placa, salida, convenio);
                    System.out.println(tiquete);

                } else if (opcion == 5) {
                    ArrayList<String> lineas = consola.sistema.reporteVehiculosAdentro();
                    if (lineas.isEmpty()) {
                        System.out.println("No hay vehiculos adentro.");
                    }
                    for (String linea : lineas) {
                        System.out.println(linea);
                    }

                } else if (opcion == 6) {
                    System.out.print("Fecha del cierre (aaaa-mm-dd): ");
                    LocalDate fecha = LocalDate.parse(consola.lector.nextLine().trim());

                    ArrayList<String> lineas = consola.sistema.reporteCierre(fecha);
                    for (String linea : lineas) {
                        System.out.println(linea);
                    }
                    String ruta = "Cierre_" + fecha + ".txt";
                    archivos.exportarReporte(lineas, ruta);
                    System.out.println("Cierre exportado a " + ruta);

                } else if (opcion == 7) {
                    ArrayList<String> lineas = consola.sistema.reportePlacasFrecuentes();
                    if (lineas.isEmpty()) {
                        System.out.println("Todavia no hay tiquetes cerrados.");
                    }
                    for (String linea : lineas) {
                        System.out.println(linea);
                    }

                } else if (opcion == 8) {
                    archivos.guardarSistema(consola.sistema, "sistema.dat");
                    System.out.println("Sistema guardado en sistema.dat");

                } else if (opcion == 9) {
                    consola.sistema = archivos.cargarSistema("sistema.dat");  
                    System.out.println("Sistema recuperado de sistema.dat");

                } else if (opcion == 0) {
                    System.out.println("Hasta luego.");

                } else {
                    System.out.println("Opcion invalida: escoja un numero del 0 al 9.");
                }

            } catch (NumberFormatException e) {
                opcion = -1;
                System.out.println("Opcion invalida: escriba un numero del 0 al 9.");
            } catch (DateTimeParseException e) {
                System.out.println("Fecha invalida. Use aaaa-mm-dd (y hh:mm para la hora). " + e.getMessage());
            } catch (ParqueaderoException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error con el archivo: " + e.getMessage());
            }
        }
    }
}
