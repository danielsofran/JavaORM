package orm.exceptions;

public class ForeignKeyException extends OrmException{
    public ForeignKeyException(){super();}
    public ForeignKeyException(String msg){super(msg);}
}
