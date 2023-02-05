package models.demo;

import orm.annotations.AutoInc;
import orm.annotations.DBEntity;
import orm.annotations.FK;
import orm.annotations.PK;
import orm.annotations.rules.Cascade;

@DBEntity
public class Persoana2 {
    @PK @AutoInc
    private int id;
    @FK(Table = MData.class, RefCol = "id") @Cascade
    int mdata_id;
    private String nume;

    public Persoana2(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMdata_id() {
        return mdata_id;
    }

    public void setMdata_id(int mdata_id) {
        this.mdata_id = mdata_id;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }
}
