package models.demo;

import orm.annotations.AutoInc;
import orm.annotations.Entity;
import orm.annotations.PK;
import orm.annotations.rules.Cascade;

@Entity
public class Persoana {
    @PK @AutoInc
    private int id;
    @Cascade
    MData data;
    private String nume;
}
