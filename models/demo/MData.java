package models.demo;

import orm.annotations.AutoInc;
import orm.annotations.DBEntity;
import orm.annotations.PK;

@DBEntity
public class MData {
    @PK @AutoInc
    int id;
    String data;

    public MData() {}

    public MData(int id, String data) {
        this.id = id;
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
