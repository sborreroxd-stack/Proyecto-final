package com.puj.parqueadero.negocio;

import com.puj.parqueadero.modelo.Convenio;
import com.puj.parqueadero.modelo.Mensualidad;
import com.puj.parqueadero.modelo.Tiquete;
import com.puj.parqueadero.modelo.Vehiculo;
import com.puj.parqueadero.excepciones.VehiculoYaAdentroException;
import com.puj.parqueadero.excepciones.VehiculoNoEncontradoException;
import com.puj.parqueadero.excepciones.PlacaInvalidaException;
import com.puj.parqueadero.excepciones.FechaInvalidaException;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public interface IParqueadero {

    public abstract void agregarMensualidad(Mensualidad m) throws FechaInvalidaException;
    public abstract Tiquete registrarEntrada(Vehiculo v, LocalDateTime entrada) throws PlacaInvalidaException,
            VehiculoYaAdentroException;
    public abstract Tiquete registrarSalida(String placa,LocalDateTime salida, Convenio convenio) throws VehiculoNoEncontradoException,
            FechaInvalidaException;
    public abstract ArrayList<String> reporteVehiculosAdentro();
    public abstract ArrayList<String> reporteCierre(LocalDate fecha);
    public abstract ArrayList<String> reportePlacasFrecuentes();
    public abstract ArrayList<Tiquete> getTiquetes();

    }

