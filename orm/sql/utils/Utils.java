package orm.sql.utils;

import orm.classparser.PropertyParser;
import orm.exceptions.PrimaryKeyException;
import orm.sql.utils.JavaSQLMapper;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

public class Utils {
    private static Double eps = 10e-5;
    private static List<Class<?>> floatingPointTypes = new LinkedList<>();
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
}
