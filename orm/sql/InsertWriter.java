package orm.sql;

import orm.classparser.MethodCaller;
import orm.classparser.PropertyChecker;
import orm.classparser.PropertyParser;
import orm.exceptions.OrmException;
import orm.exceptions.ScriptWriterException;

import java.lang.reflect.Field;

public class InsertWriter {
    private final PropertyParser<?> parser;
    public InsertWriter(Class<?> the_class){
        parser = new PropertyParser<>(the_class);
    }
    public String getInsertSQL(Object obj) throws OrmException {
        if(obj.getClass() != parser.getClass())
            throw new ScriptWriterException("InsertWriter: object is not of type class");
        String rez = "INSERT INTO " + parser.getName() + " (";
        for(Field field: parser.getFields()){
            if(!PropertyChecker.isAutoInc(field)){
                rez += field.getName() + ",";
            }
        }
        rez = rez.substring(0, rez.length()-1) + ")" + " VALUES (";
        for(Field field: parser.getFields()){
            if(!PropertyChecker.isAutoInc(field)){
                rez += MethodCaller.callGetter(obj, field.getName()) + ",";
            }
        }
        return rez;
    }
}
