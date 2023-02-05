package models.demo;

import orm.annotations.DBEntity;
import orm.annotations.PK;

@DBEntity
public class TData {
    @PK
    float id;
    double dbl;
    Short s;
    char c;
    Character ch;

    public TData(){}

    public Character getCh() {
        return ch;
    }

    public void setCh(Character ch) {
        this.ch = ch;
    }

    public float getId() {
        return id;
    }

    public void setId(float id) {
        this.id = id;
    }

    public Short getS() {
        return s;
    }

    public void setS(Short s) {
        this.s = s;
    }

    public char getC() {
        return c;
    }

    public void setC(char c) {
        this.c = c;
    }

    public double getDbl() {
        return dbl;
    }

    public void setDbl(double dbl) {
        this.dbl = dbl;
    }
}
