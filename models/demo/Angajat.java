package models.demo;

import orm.annotations.Entity;
import orm.annotations.PK;

@Entity
public class Angajat extends Persoana{
    private double salariu;
    private Persoana sef;

    public Angajat() {
    }

    public double getSalariu() {
        return salariu;
    }

    public void setSalariu(double salariu) {
        this.salariu = salariu;
    }

    public Persoana getSef() {
        return sef;
    }

    public void setSef(Persoana sef) {
        this.sef = sef;
    }
}
