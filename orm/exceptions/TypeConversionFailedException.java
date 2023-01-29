package orm.exceptions;

public class TypeConversionFailedException extends OrmException {
    public TypeConversionFailedException(){
        super();
    }
    public TypeConversionFailedException(String msg)
    {
        super(msg);
    }
}
