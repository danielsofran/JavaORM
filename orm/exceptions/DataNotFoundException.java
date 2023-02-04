package orm.exceptions;

public class DataNotFoundException extends OrmException {
    public DataNotFoundException(){super();}
    public DataNotFoundException(String msg){super(msg);}
}
