package com.puj.parqueadero.negocio;
import com.puj.parqueadero.excepciones.FechaInvalidaException;
import com.puj.parqueadero.excepciones.PlacaInvalidaException;
import com.puj.parqueadero.excepciones.VehiculoNoEncontradoException;
import com.puj.parqueadero.excepciones.VehiculoYaAdentroException;
import com.puj.parqueadero.modelo.Mensualidad;
import com.puj.parqueadero.modelo.Tiquete;
import com.puj.parqueadero.modelo.Convenio;
import com.puj.parqueadero.modelo.Vehiculo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;

public class Parqueadero implements IParqueadero, Serializable {

    private static final long serialVersionUID = 1L;

    private int siguienteNumero = 1;
    private ArrayList<Tiquete> tiquetes = new ArrayList<Tiquete>();
    private ArrayList<Mensualidad> mensualidades = new ArrayList<Mensualidad>();

    @Override
    public void agregarMensualidad(Mensualidad m) throws FechaInvalidaException {
        if(m.getFin().isBefore(m.getInicio())){
            throw new FechaInvalidaException("La mensualidad de la placa " + m.getPlaca()
                    + " termina (" + m.getFin() + ") antes de empezar (" + m.getInicio() + ").");
        }
        this.mensualidades.add(m);
    }

    private Tiquete buscarAbierto(String placa){
        for(Tiquete t: this.tiquetes){
            if(t.estaAbierto()&& t.getVehiculo().getPlaca().equalsIgnoreCase(placa)){
                return t;
            }
        }
        return null;
    }

    private boolean tieneMensualidad(String placa, LocalDate fecha){
        for(Mensualidad m: this.mensualidades){
            if(m.getPlaca().equalsIgnoreCase(placa)&& m.cubre(fecha)){
                return true;
            }
        }
        return false;
    }
    @Override
    public Tiquete registrarEntrada(Vehiculo v, LocalDateTime entrada)
            throws PlacaInvalidaException,VehiculoYaAdentroException{
        if(!v.placaValida()){
            throw new PlacaInvalidaException("La placa " + v.getPlaca() +
                    " no corresponde a un vehiculo tipo " + v.getTipo() + ".");
        }
        if(this.buscarAbierto(v.getPlaca()) != null){
            throw new VehiculoYaAdentroException("El vehiculo " + v.getPlaca() + " ya tiene un tiquete abierto.");

        }

        Tiquete tiquete = new Tiquete(v, this.siguienteNumero, entrada);
        this.siguienteNumero++;
        this.tiquetes.add(tiquete);
        return tiquete;

    }

    @Override
    public Tiquete registrarSalida(String placa, LocalDateTime salida, Convenio convenio)
            throws VehiculoNoEncontradoException, FechaInvalidaException{
        Tiquete tiquete = this.buscarAbierto(placa);
        if (tiquete == null){
            throw new VehiculoNoEncontradoException("No hay un tiquete abierto con la placa " + placa + ".");

        }

        if(salida.isBefore(tiquete.getEntrada())){
            throw new FechaInvalidaException("la salida (" + salida + ") " +
                    "es anterior a la entrada (" + tiquete.getEntrada() + ").");
        }

        Convenio convenioFinal = convenio;
        if (convenioFinal == null){
            convenioFinal = Convenio.NINGUNO;
        }

        boolean hayMensualidad= this.tieneMensualidad(placa, tiquete.getEntrada().toLocalDate());

        tiquete.cerrar(salida,convenioFinal, hayMensualidad);
        return tiquete;
    }

    @Override
    public ArrayList<Tiquete> getTiquetes() {
        return new ArrayList<Tiquete>(this.tiquetes);
    }

    @Override
    public ArrayList<String> reporteVehiculosAdentro() {
        ArrayList<Tiquete> abiertos = new ArrayList<Tiquete>();
        for(Tiquete t: this.tiquetes){
            if(t.estaAbierto()){
                abiertos.add(t);
            }
        }

        abiertos.sort(new Comparator<Tiquete>() {
            @Override
            public int compare(Tiquete a, Tiquete b) {
                int comparacion = a.getEntrada().compareTo(b.getEntrada());
                if (comparacion != 0) {
                    return comparacion;
                }
                return a.getVehiculo().getPlaca().compareTo(b.getVehiculo().getPlaca());
            }
        });

        ArrayList<String> lineas = new ArrayList<String>();
        for (Tiquete t : abiertos) {
            lineas.add(t.toString());
        }
        return lineas;
    }

    @Override
    public ArrayList<String> reporteCierre(LocalDate fecha) {
        ArrayList<Tiquete> delDial = new ArrayList<Tiquete>();
        for (Tiquete t: this.tiquetes){
            if (!t.estaAbierto() && t.getSalida().toLocalDate().equals(fecha)){
                delDial.add(t);
            }
        }

        long parqueo = 0;
        long recarga = 0;
        for (Tiquete t: delDial){
            parqueo += t.getValorParqueo();
            recarga += t.getValorRecarga();
        }

        ArrayList<String> lineas = new ArrayList<String>();
        lineas.add("CIERRE " + fecha);
        lineas.add("tiquetes=" + delDial.size());
        lineas.add("parqueo=" + parqueo);
        lineas.add("recarga=" + recarga);
        lineas.add("total=" + (recarga + parqueo));

        String[] tipos = {"CARRO","MOTO","ELECTRICO"};
        for (String tipo : tipos){
            int cantidad = 0;
            long total = 0;
            for (Tiquete t : delDial){
                if (t.getVehiculo().getTipo().equals(tipo)){
                    cantidad ++;
                    total += t.getTotal();
                }
            }

            lineas.add(tipo + " tiquetes=" + cantidad + " total=" + total);
        }

        return lineas;
    }


    private int contarCerrados(String placa) {
        int cantidad = 0;
        for (Tiquete t : this.tiquetes) {
            if (!t.estaAbierto() && t.getVehiculo().getPlaca().equalsIgnoreCase(placa)) {
                cantidad++;
            }
        }
        return cantidad;
    }

    private long totalPagado(String placa) {
        long total = 0;
        for (Tiquete t : this.tiquetes) {
            if (!t.estaAbierto() && t.getVehiculo().getPlaca().equalsIgnoreCase(placa)) {
                total += t.getTotal();
            }
        }
        return total;
    }

    @Override
    public ArrayList<String> reportePlacasFrecuentes() {
        ArrayList<String> placas = new ArrayList<String>();
        for (Tiquete t: tiquetes){
            if (!t.estaAbierto()){
                String placa = t.getVehiculo().getPlaca().toUpperCase();
                if (!placas.contains(placa)){
                    placas.add(placa);
                }
            }
        }

        placas.sort(new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                int comparacion = Integer.compare(
                        Parqueadero.this.contarCerrados(b), Parqueadero.this.contarCerrados(a));
                if (comparacion != 0) {
                    return comparacion;
                }
                comparacion = Long.compare(
                        Parqueadero.this.totalPagado(b), Parqueadero.this.totalPagado(a));
                if (comparacion != 0) {
                    return comparacion;
                }
                return a.compareTo(b);
            }
        });

        ArrayList<String> lineas = new ArrayList<String>();
       int limite = 3;
        if (placas.size() < limite) {
            limite = placas.size();
        }

        for (int i =0; i<limite; i++){
            String placa = placas.get(i);

            lineas.add((i + 1) + ". " + placa + " tiquetes=" + this.contarCerrados(placa)
                    + " total=" + this.totalPagado(placa));
        }
        return lineas;
    }
}