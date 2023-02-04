package orm.sql;

import orm.classparser.MethodCaller;
import orm.classparser.PropertyChecker;
import orm.classparser.PropertyParser;
import orm.exceptions.OrmException;
import orm.sql.utils.JavaSQLMapper;
import orm.sql.utils.SequenceType;
import orm.sql.utils.Utils;

import java.lang.reflect.Field;
import java.util.List;

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
                if(!PropertyChecker.isFK(field)) {
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

    private static String createSequence(Object obj, List<Field> fields, SequenceType type) throws OrmException {
        String rez = "", separator = "";
        if(type == SequenceType.CONDITION)
            separator = "AND";
        else if(type == SequenceType.SET)
            separator = ",";
        for(Field field : fields)
        {
            Object value = MethodCaller.callGetter(obj, field.getName());
            if(PropertyChecker.isFK(field))
            {
                Field pk = Utils.getFirstPK(value.getClass());
                value = MethodCaller.callGetter(value, pk.getName());
            }
            String current_seq = "";
            if(type == SequenceType.CONDITION) {
                current_seq = Utils.createEqualCondition(field, value);
            }
            else if (type == SequenceType.SET) {
                current_seq = '"'+field.getName()+"\" = " + JavaSQLMapper.toSQLValue(value);
            }
            if(rez.equals(""))
                rez = current_seq;
            else
                rez += " " + separator + " " + current_seq;
        }
        return rez;
    }
}
