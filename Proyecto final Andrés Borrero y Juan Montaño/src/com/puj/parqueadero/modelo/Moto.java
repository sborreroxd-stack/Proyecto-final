package com.puj.parqueadero.modelo;

public class Moto extends Vehiculo{

    private static final long TARIFA_MINUTO = 80;

    public Moto(String placa) {
        super(placa);
    }
    @Override
    public String getTipo(){
        return "MOTO";
    }
    @Override
    public boolean placaValida(){
        String placa = this.getPlaca();
        if (placa == null) {
            return false;
        }
        if (placa.length() != 6) {
            return false;
        }
        for (int i = 0; i < 6; i++) {
            char c = placa.charAt(i);
            if (i == 3 || i == 4) {
                if (c < '0' || c > '9') {
                    return false;
                }
            } else {
                if (c < 'A' || c > 'Z') {
                    return false;
                }
            }
        }
        return true;
    }
    @Override
    public long calcularTarifa(long minutos){
        return minutos * TARIFA_MINUTO;
    }


}
