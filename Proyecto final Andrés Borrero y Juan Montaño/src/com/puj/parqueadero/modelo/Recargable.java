package com.puj.parqueadero.modelo;

public interface Recargable {

    public static final long COSTO_MINUTO = 50;

    public abstract long costoRecarga(long minutos);

}
