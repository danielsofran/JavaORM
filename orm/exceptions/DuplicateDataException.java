package orm.exceptions;

public class DuplicateDataException extends OrmException {
    public DuplicateDataException(){super();}
    public DuplicateDataException(String msg){super(msg);}
}
