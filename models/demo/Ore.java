package models.demo;

import orm.annotations.AutoInc;
import orm.annotations.Entity;
import orm.annotations.PK;

import java.time.LocalDateTime;

@Entity
public class Ore {
    @PK @AutoInc
    int id;
    LocalDateTime ora;
    MyEnum tip;

    public MyEnum getTip() {
        return tip;
    }

    public void setTip(MyEnum tip) {
        this.tip = tip;
    }

    public Ore(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getOra() {
        return ora;
    }

    public void setOra(LocalDateTime ora) {
        this.ora = ora;
    }
}
