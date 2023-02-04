package orm.sql;

import orm.classparser.MethodCaller;
import orm.classparser.PropertyChecker;
import orm.classparser.PropertyParser;
import orm.exceptions.OrmException;
import orm.exceptions.PrimaryKeyException;
import orm.sql.utils.JavaSQLMapper;
import orm.sql.utils.Utils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

public class DMLWriter {
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
                if(!PropertyChecker.isFKEntity(field)) {
                    Object value = MethodCaller.callGetter(obj, field.getName());
                    rez += JavaSQLMapper.toSQLValue(value) + ",";
                }
                else {
                    Object entity = MethodCaller.callGetter(obj, field.getName());
                    Field pk = Utils.getFirstPK(entity.getClass());
                    Object value = MethodCaller.callGetter(entity, pk.getName());
                    rez += JavaSQLMapper.toSQLValue(value) + ",";
                }
            }
        }
        rez = rez.substring(0, rez.length()-1) + ")";
        return rez;
    }

    public static String getDeleteSQL(Class<?> the_class, Object... pkValues) throws OrmException
    {
        PropertyParser<?> parser = new PropertyParser<>(the_class);
        List<Field> fields = parser.getPKs();
        if(fields.size() != pkValues.length)
            throw new PrimaryKeyException("Invalid number of pks in DELETE "+pkValues.length+", instead of "+fields.size());

        String rez = "DELETE FROM \""+parser.getName()+"\" WHERE ";
        rez += Utils.createConditionSequence(fields, pkValues);
        return rez;
    }

    public static String getUpdateSQL(Object updated, Object... pkValues) throws OrmException
    {
        PropertyParser<?> parser = new PropertyParser<>(updated.getClass());
        List<Field> pkFields = parser.getPKs();
        List<Field> fields = parser.getFields().stream().filter(f->!PropertyChecker.isPK(f)).collect(Collectors.toList());
        if(pkFields.size() != pkValues.length)
            throw new PrimaryKeyException("Invalid number of pks in UPDATE "+pkValues.length+", instead of "+pkFields.size());

        return "UPDATE \""+parser.getName()+"\" SET "+Utils.createSetSequence(updated, fields) + " WHERE "+Utils.createConditionSequence(pkFields, pkValues);
    }
}
