package models.demo;

import orm.annotations.AutoInc;
import orm.annotations.Entity;
import orm.annotations.PK;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
public class Ore {
    @PK @AutoInc
    int id;
    LocalDateTime ora;
    MyEnum tip;

    boolean flag;
    LocalDate date;
    LocalTime time;

    Long aLong;

    public boolean isFlag() {
        return flag;
    }

    public Long getALong() {
        return aLong;
    }

    public void setALong(Long aLong) {
        this.aLong = aLong;
    }

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

    public boolean getFlag() {

        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }
}
