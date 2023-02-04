package orm.sql.utils;

import orm.classparser.MethodCaller;
import orm.classparser.PropertyChecker;
import orm.classparser.PropertyParser;
import orm.exceptions.OrmException;
import orm.exceptions.PrimaryKeyException;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public class Utils {
    private static final Double eps = 1e-5;
    private static final List<Class<?>> floatingPointTypes = new LinkedList<>();
    static{
        floatingPointTypes.add(double.class);
        floatingPointTypes.add(float.class);
        floatingPointTypes.add(Double.class);
        floatingPointTypes.add(Float.class);
    }

    public static Field getFirstPK(Class<?> the_class) throws PrimaryKeyException {
        PropertyParser<?> entityParser = new PropertyParser<>(the_class);
        List<Field> pks = entityParser.getPKs();
        if(pks.size()!=1)
            throw new PrimaryKeyException("Foreign key must have exactly 1 PK!");
        return pks.get(0);
    }

    public static String createEqualCondition(Field field, Object value) // Java value
    {
        Class<?> the_class = field.getType();
        if(floatingPointTypes.stream().anyMatch(c -> c.equals(the_class)))
            return "ABS(\""+field.getName() + "\" - "+ JavaSQLMapper.toSQLValue(value)+") < " + eps;
        if(LocalDateTime.class.equals(the_class))
            return '"'+field.getName()+"\"::timestamp(0) = "+JavaSQLMapper.toSQLValue(value)+"::timestamp(0)";
        if(LocalDate.class.equals(the_class))
            return '"'+field.getName()+"\"::time(0) = "+JavaSQLMapper.toSQLValue(value)+"::time(0)";
        return '"'+field.getName()+"\" = "+JavaSQLMapper.toSQLValue(value);
    }

    public static String createConditionSequence(List<Field> fields, Object... values) throws OrmException {
        String rez = "", separator = "AND";
        int index = 0;
        for(Field field : fields)
        {
            Object value = values[index++];
            if(PropertyChecker.isFKEntity(field))
            {
                Field pk = Utils.getFirstPK(value.getClass());
                value = MethodCaller.callGetter(value, pk.getName());
            }
            String current_seq = Utils.createEqualCondition(field, value);
            if(rez.equals(""))
                rez = current_seq;
            else
                rez += " " + separator + " " + current_seq;
        }
        return rez;
    }

    public static String createSetSequence(Object obj, List<Field> fields) throws OrmException {
        String rez = "", separator = ",";
        for(Field field : fields)
        {
            Object value = MethodCaller.callGetter(obj, field.getName());
            if(PropertyChecker.isFKEntity(field))
            {
                Field pk = Utils.getFirstPK(value.getClass());
                value = MethodCaller.callGetter(value, pk.getName());
            }
            String current_seq = '"'+field.getName()+"\" = " + JavaSQLMapper.toSQLValue(value);
            if(rez.equals(""))
                rez = current_seq;
            else
                rez += " " + separator + " " + current_seq;
        }
        return rez;
    }
}
