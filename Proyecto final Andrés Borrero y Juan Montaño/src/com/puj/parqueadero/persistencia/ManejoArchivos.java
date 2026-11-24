package com.puj.parqueadero.persistencia;

import com.puj.parqueadero.excepciones.FechaInvalidaException;
import com.puj.parqueadero.excepciones.ParqueaderoException;
import com.puj.parqueadero.modelo.Carro;
import com.puj.parqueadero.modelo.CarroElectrico;
import com.puj.parqueadero.modelo.Convenio;
import com.puj.parqueadero.modelo.Mensualidad;
import com.puj.parqueadero.modelo.Moto;
import com.puj.parqueadero.modelo.Tiquete;
import com.puj.parqueadero.modelo.Vehiculo;
import com.puj.parqueadero.negocio.IParqueadero;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lee y escribe archivos. No imprime nada en pantalla: devuelve textos
 * y quien la usa (Consola) decide como mostrarlos.
 */
public class ManejoArchivos {

    // Formato de fecha y hora de Movimientos.txt
    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Lee Mensualidades.txt (placa*inicio*fin). Una linea con error no detiene
     * la carga: se informa con su numero de linea y se sigue con la siguiente.
     */
    public List<String> cargarMensualidades(String ruta, IParqueadero sistema) throws IOException {
        List<String> resultado = new ArrayList<String>();

        try (BufferedReader lector = new BufferedReader(new FileReader(ruta))) {
            String linea;
            int numeroLinea = 0;

            while ((linea = lector.readLine()) != null) {
                numeroLinea++;                       // cuenta TODAS las lineas, tambien blancos y comentarios
                String texto = linea.trim();
                if (texto.length() > 0 && !texto.startsWith("#")) {
                    try {
                        String[] campos = texto.split("\\*");
                        if (campos.length != 3) {
                            throw new ParqueaderoException("Se esperaban 3 campos (placa*inicio*fin) y llegaron "
                                    + campos.length + ".");
                        }
                        String placa = campos[0].trim();
                        LocalDate inicio = this.leerFecha(campos[1]);
                        LocalDate fin = this.leerFecha(campos[2]);

                        Mensualidad m = new Mensualidad(placa, inicio, fin);
                        sistema.agregarMensualidad(m);
                        resultado.add("Linea " + numeroLinea + ": OK - " + m);
                    } catch (ParqueaderoException e) {
                        resultado.add("Linea " + numeroLinea + ": " + e.getMessage());
                    }
                }
            }
        }
        return resultado;
    }

    /**
     * Lee Movimientos.txt y aplica en orden las entradas (E*placa*tipo*fecha hora)
     * y las salidas (S*placa*fecha hora*convenio).
     */
    public List<String> procesarMovimientos(String ruta, IParqueadero sistema) throws IOException {
        List<String> resultado = new ArrayList<String>();

        try (BufferedReader lector = new BufferedReader(new FileReader(ruta))) {
            String linea;
            int numeroLinea = 0;

            while ((linea = lector.readLine()) != null) {
                numeroLinea++;
                String texto = linea.trim();
                if (texto.length() > 0 && !texto.startsWith("#")) {
                    try {
                        String[] campos = texto.split("\\*");
                        String clase = campos[0].trim().toUpperCase();

                        if (clase.equals("E")) {
                            if (campos.length != 4) {
                                throw new ParqueaderoException("Una entrada necesita 4 campos "
                                        + "(E*placa*tipo*fecha hora) y llegaron " + campos.length + ".");
                            }
                            Vehiculo v = this.crearVehiculo(campos[2].trim(), campos[1].trim());
                            LocalDateTime entrada = this.leerFechaHora(campos[3]);
                            Tiquete t = sistema.registrarEntrada(v, entrada);
                            resultado.add("Linea " + numeroLinea + ": OK - " + t);

                        } else if (clase.equals("S")) {
                            if (campos.length != 4) {
                                throw new ParqueaderoException("Una salida necesita 4 campos "
                                        + "(S*placa*fecha hora*convenio) y llegaron " + campos.length + ".");
                            }
                            LocalDateTime salida = this.leerFechaHora(campos[2]);
                            Convenio convenio = this.leerConvenio(campos[3]);
                            Tiquete t = sistema.registrarSalida(campos[1].trim(), salida, convenio);
                            resultado.add("Linea " + numeroLinea + ": OK - " + t);

                        } else {
                            throw new ParqueaderoException("La linea debe empezar por E o S y empieza por '"
                                    + campos[0].trim() + "'.");
                        }
                    } catch (ParqueaderoException e) {
                        resultado.add("Linea " + numeroLinea + ": " + e.getMessage());
                    }
                }
            }
        }
        return resultado;
    }

    /** Escribe cada linea del reporte en el archivo (por ejemplo Cierre_2026-03-02.txt). */
    public void exportarReporte(List<String> lineas, String ruta) throws IOException {
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(ruta))) {
            for (String linea : lineas) {
                escritor.write(linea);
                escritor.newLine();
            }
        }
    }

    /** Guarda el sistema completo (tiquetes, mensualidades y consecutivo) con serializacion. */
    public void guardarSistema(IParqueadero sistema, String ruta) throws IOException {
        try (ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(ruta))) {
            salida.writeObject(sistema);
        }
    }

    /** Recupera el sistema guardado. Quien llama debe reemplazar su referencia por la devuelta. */
    public IParqueadero cargarSistema(String ruta) throws IOException, ClassNotFoundException {
        try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(ruta))) {
            return (IParqueadero) entrada.readObject();
        }
    }

    // ------------------------------------------------------------------
    // Conversion de texto a objetos. Un dato mal escrito se rechaza con
    // una excepcion propia para que la linea se informe y la carga siga.
    // ------------------------------------------------------------------

    private Vehiculo crearVehiculo(String tipo, String placa) throws ParqueaderoException {
        String t = tipo.toUpperCase();
        if (t.equals("CARRO")) {
            return new Carro(placa);
        }
        if (t.equals("MOTO")) {
            return new Moto(placa);
        }
        if (t.equals("ELECTRICO")) {
            return new CarroElectrico(placa);
        }
        throw new ParqueaderoException("Tipo de vehiculo desconocido: '" + tipo + "'.");
    }

    private Convenio leerConvenio(String texto) throws ParqueaderoException {
        try {
            return Convenio.valueOf(texto.trim());
        } catch (IllegalArgumentException e) {
            throw new ParqueaderoException("Convenio desconocido: '" + texto.trim() + "'.");
        }
    }

    private LocalDate leerFecha(String texto) throws FechaInvalidaException {
        try {
            return LocalDate.parse(texto.trim());
        } catch (DateTimeParseException e) {
            throw new FechaInvalidaException("La fecha '" + texto.trim() + "' no tiene el formato aaaa-mm-dd o no existe.");
        }
    }

    private LocalDateTime leerFechaHora(String texto) throws FechaInvalidaException {
        try {
            return LocalDateTime.parse(texto.trim(), FORMATO_FECHA_HORA);
        } catch (DateTimeParseException e) {
            throw new FechaInvalidaException("La fecha y hora '" + texto.trim()
                    + "' no tiene el formato aaaa-mm-dd hh:mm o no existe.");
        }
    }
}
