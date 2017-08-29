package com.inflexionlabs.goparken;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by odalysmarronsanchez on 29/08/17.
 */

public class Auto {

    public String uidA;
    public String nombreA;
    public String placa;
    public String marca;
    public String submarca;
    public String anio;

    public Auto() {
    }

    public Auto(String uidA, String nombreA, String placa, String marca, String submarca, String anio) {
        this.uidA = uidA;
        this.nombreA = nombreA;
        this.placa = placa;
        this.marca = marca;
        this.submarca = submarca;
        this.anio = anio;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uidA);
        result.put("nombre", nombreA);
        result.put("placa", placa);
        result.put("marca", marca);
        result.put("submarca", submarca);
        result.put("año", anio);

        return result;
    }
}
