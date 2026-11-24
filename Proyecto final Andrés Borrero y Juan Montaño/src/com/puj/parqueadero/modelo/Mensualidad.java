package com.puj.parqueadero.modelo;

import java.io.Serializable;
import java.time.LocalDate;

public class Mensualidad implements Serializable {

    private static final long serialVersionUID = 1L;

    private String placa;
    private LocalDate inicio;
    private LocalDate fin;

    public Mensualidad(String placa, LocalDate inicio,LocalDate fin){
        this.placa = placa;
        this.inicio = inicio;
        this.fin = fin;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public LocalDate getInicio() {
        return inicio;
    }

    public void setInicio(LocalDate inicio) {
        this.inicio = inicio;
    }

    public LocalDate getFin() {
        return fin;
    }

    public void setFin(LocalDate fin) {
        this.fin = fin;
    }

    public boolean cubre(LocalDate fecha){
        return !fecha.isBefore(this.inicio) && !fecha.isAfter(this.fin);
    }

    @Override
    public String toString() {
        return "Mensualidad " + this.placa + " | " + this.inicio + " a " + this.fin;
    }
}
