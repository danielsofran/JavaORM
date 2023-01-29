package models.demo;

import orm.annotations.Entity;
import orm.annotations.PK;

@Entity
public class Angajat extends Persoana{
    private double salariu;
    private Persoana sef;
}
