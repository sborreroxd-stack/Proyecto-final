package com.puj.parqueadero.excepciones;

public class PlacaInvalidaException extends ParqueaderoException{

    private static final long serialVersionUID = 1L;

    public PlacaInvalidaException(String mensaje){
        super(mensaje);
    }
}
