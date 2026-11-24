package com.puj.parqueadero.excepciones;

public class FechaInvalidaException extends ParqueaderoException{

    private static final long serialVersionUID = 1L;

    public FechaInvalidaException(String mensaje){
        super(mensaje);
    }
}
