package models.demo;

import orm.annotations.AutoInc;
import orm.annotations.Entity;
import orm.annotations.PK;

@Entity
public class MData {
    @PK @AutoInc
    int id;
    String data;
}
