package orm.sql;

import orm.classparser.MethodCaller;
import orm.classparser.PropertyChecker;
import orm.classparser.PropertyParser;
import orm.exceptions.OrmException;
import orm.exceptions.PrimaryKeyException;
import orm.exceptions.ScriptWriterException;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

public class InsertWriter {
    public static String getInsertSQL(Object obj) throws OrmException {
        PropertyParser<?> parser = new PropertyParser<>(obj.getClass());
        String rez = "INSERT INTO \"" + parser.getName() + "\" (";
        for(Field field: parser.getFields()){
            if(!PropertyChecker.isAutoInc(field)){
                rez += '"'+field.getName()+ "\",";
            }
        }
        rez = rez.substring(0, rez.length()-1) + ")" + " VALUES (";
        for(Field field: parser.getFields()){
            if(!PropertyChecker.isAutoInc(field)){
                if(!PropertyChecker.isFK(field)) {
                    Object value = MethodCaller.callGetter(obj, field.getName());
                    rez += JavaSQLMapper.toSQLValue(value) + ",";
                }
                else {
                    Object entity = MethodCaller.callGetter(obj, field.getName());
                    PropertyParser<?> entityParser = new PropertyParser<>(entity.getClass());
                    List<Field> pks = entityParser.getPKs();
                    if(pks.size()!=1)
                        throw new PrimaryKeyException("Foreign key must have exactly 1 PK!");
                    Field pk = pks.get(0);
                    Object value = MethodCaller.callGetter(entity, pk.getName());
                    rez += JavaSQLMapper.toSQLValue(value) + ",";
                }
            }
        }
        rez = rez.substring(0, rez.length()-1) + ")";
        return rez;
    }
}
