package com.puj.parqueadero.modelo;

public enum Convenio {

    NINGUNO(0),
    ALMACEN(30),
    RESTAURANTE(60),
    CINE(120);

    private final int minutosGratis;

    private Convenio(int minutosGratis) {
        this.minutosGratis = minutosGratis;
    }

    public int getMinutosGratis() {
        return minutosGratis;
    }
}
