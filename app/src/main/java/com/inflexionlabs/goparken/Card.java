package com.inflexionlabs.goparken;

/**
 * Created by odalysmarronsanchez on 24/08/17.
 */

public class Card {

    private int id;
    private String openpayMask;
    private String status;
    private int def;

    public Card(int id, String openpayMask, String status, int def) {
        this.id = id;
        this.openpayMask = openpayMask;
        this.status = status;
        this.def = def;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOpenpayMask() {
        return openpayMask;
    }

    public void setOpenpayMask(String openpayMask) {
        this.openpayMask = openpayMask;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getDef() {
        return def;
    }

    public void setDef(int def) {
        this.def = def;
    }
}
