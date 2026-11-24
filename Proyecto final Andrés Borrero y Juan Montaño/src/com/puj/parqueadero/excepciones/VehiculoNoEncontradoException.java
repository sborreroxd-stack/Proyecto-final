package com.puj.parqueadero.excepciones;

public class VehiculoNoEncontradoException extends ParqueaderoException{

    private static final long serialVersionUID = 1L;

    public VehiculoNoEncontradoException(String mensaje){

        super(mensaje);
    }

}
