package com.inflexionlabs.goparken;

/**
 * Created by odalysmarronsanchez on 28/08/17.
 */

class CheckInUtilities {
    private static final CheckInUtilities ourInstance = new CheckInUtilities();

    private int id;
    private int parking_id;
    private int marker_id;
    private String in;
    private String out;
    private int comision;
    private String exit_code;
    private boolean promo;

    static CheckInUtilities getInstance() {
        return ourInstance;
    }

    private CheckInUtilities() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParking_id() {
        return parking_id;
    }

    public void setParking_id(int parking_id) {
        this.parking_id = parking_id;
    }

    public int getMarker_id() {
        return marker_id;
    }

    public void setMarker_id(int marker_id) {
        this.marker_id = marker_id;
    }

    public String getIn() {
        return in;
    }

    public void setIn(String in) {
        this.in = in;
    }

    public String getOut() {
        return out;
    }

    public void setOut(String out) {
        this.out = out;
    }

    public int getComision() {
        return comision;
    }

    public void setComision(int comision) {
        this.comision = comision;
    }

    public String getExit_code() {
        return exit_code;
    }

    public void setExit_code(String exit_code) {
        this.exit_code = exit_code;
    }

    public boolean isPromo() {
        return promo;
    }

    public void setPromo(boolean promo) {
        this.promo = promo;
    }
}
