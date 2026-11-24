package com.puj.parqueadero.modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Tiquete implements Serializable {

    private static final long serialVersionUID= 1L;

    private final int numero;
    private final LocalDateTime entrada;
    private LocalDateTime salida;
    private long valorParqueo;
    private long valorRecarga;

    private final Vehiculo vehiculo;
    private Convenio convenio;


    public Tiquete(Vehiculo vehiculo, int numero, LocalDateTime entrada) {
        this.vehiculo = vehiculo;
        this.numero = numero;
        this.entrada = entrada;

        this.salida = null;
        this.convenio = null;
        this.valorParqueo = 0;
        this.valorRecarga = 0;
    }

    public boolean estaAbierto(){
        return this.salida == null;
    }

    public long getMinutos(){
        if( this.salida == null){
            return 0;
        }
        return ChronoUnit.MINUTES.between(this.entrada,this.salida);
    }

   public void cerrar( LocalDateTime salida,Convenio convenio,boolean mensualidad){
        this.salida= salida;
        this.convenio= convenio;

        long minutos = this.getMinutos();
        long cobrables = minutos - convenio.getMinutosGratis();
        if (cobrables < 0) {
            cobrables = 0;
        }

        if (mensualidad) {
            this.valorParqueo = 0;
        } else {
            this.valorParqueo = this.vehiculo.calcularTarifa(cobrables);
        }
        this.valorRecarga = this.vehiculo.cargoAdicional(minutos);
    }

    public long getTotal(){
        return this.valorParqueo + this.valorRecarga;
    }

    public int getNumero() {
        return numero;
    }

    public LocalDateTime getEntrada() {
        return entrada;
    }

    public LocalDateTime getSalida() {
        return salida;
    }

    public void setSalida(LocalDateTime salida) {
        this.salida = salida;
    }

    public long getValorParqueo() {
        return valorParqueo;
    }

    public void setValorParqueo(long valorParqueo) {
        this.valorParqueo = valorParqueo;
    }

    public long getValorRecarga() {
        return valorRecarga;
    }

    public void setValorRecarga(long valorRecarga) {
        this.valorRecarga = valorRecarga;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public Convenio getConvenio() {
        return convenio;
    }

    public void setConvenio(Convenio convenio) {
        this.convenio = convenio;
    }

    @Override
    public String toString() {

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        String texto = "Tiquete " + this.numero
                + " | " + this.vehiculo.getTipo() + " " + this.vehiculo.getPlaca()
                + " | Entrada: " + this.entrada.format(formato);

        if (this.estaAbierto()) {
            return texto + " | ABIERTO";
        }
        return texto + " | Salida: " + this.salida.format(formato)
                + " | Minutos: " + this.getMinutos()
                + " | Parqueo: " + this.valorParqueo
                + " | Recarga: " + this.valorRecarga
                + " | Total: " + this.getTotal();
    }
}
