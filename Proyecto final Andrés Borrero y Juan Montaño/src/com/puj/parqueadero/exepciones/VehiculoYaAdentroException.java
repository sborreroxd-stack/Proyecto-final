package com.puj.parqueadero.excepciones;

public class VehiculoYaAdentroException extends ParqueaderoException{

    private static final long serialVersionUID = 1L;

    public VehiculoYaAdentroException(String mensaje){
        super(mensaje);
    }
}
