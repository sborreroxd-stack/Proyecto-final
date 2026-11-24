package com.puj.parqueadero.modelo;

public class Carro extends Vehiculo{

    private static final long TARIFA_MINUTO = 120;

    public Carro(String placa) {
        super(placa);
    }

    @Override
    public String getTipo() {
        return "CARRO";
    }

    @Override
    public boolean placaValida() {
        String placa = this.getPlaca();
        if (placa == null) {
            return false;
        }
        if (placa.length() != 6) {
            return false;
        }
        for (int i = 0; i < 3; i++) {
            char c = placa.charAt(i);
            if (c < 'A' || c > 'Z') {
                return false;
            }
        }
        for (int i = 3; i < 6; i++) {
            char c = placa.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    @Override
    public long calcularTarifa(long minutos) {
        return minutos * TARIFA_MINUTO;
    }
}
