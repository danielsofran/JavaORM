package models.demo;

import orm.annotations.DBEntity;
import orm.annotations.PK;

@DBEntity
public class NonAIData {
    @PK
    Long id;
    String someData;

    public NonAIData(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSomeData() {
        return someData;
    }

    public void setSomeData(String someData) {
        this.someData = someData;
    }
}
