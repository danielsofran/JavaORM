package models.demo;

import orm.annotations.DBEntity;
import orm.annotations.PK;

@DBEntity
public class E_FkData {
    @PK
    int id;
    MData data;
}
