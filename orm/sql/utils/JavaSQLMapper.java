package orm.sql.utils;

import orm.exceptions.OrmException;
import orm.exceptions.TypeConversionFailedException;

import java.lang.reflect.Method;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class JavaSQLMapper {
    // Java     SQL
    static Map<String, String> dict;
    static {
        dict = new HashMap<>();
        dict.put("int", "INT");
        dict.put("Integer", "INT");
        dict.put("long", "BIGINT");
        dict.put("Long", "BIGINT");
        dict.put("String", "VARCHAR");
        dict.put("boolean", "BOOLEAN");
        dict.put("Boolean", "BOOLEAN"); // TODO
        dict.put("double", "REAL");
        dict.put("float", "REAL");
        dict.put("Double", "REAL");
        dict.put("Float", "REAL");
        dict.put("LocalDateTime", "TIMESTAMP");
        dict.put("LocalDate", "DATE");
        dict.put("LocalTime", "TIME");
    }
    public static String getSQLType(Class<?> the_class) throws TypeConversionFailedException {
        try {
            String sqlType =  dict.get(the_class.getSimpleName());
            if (sqlType != null)
                return sqlType;
            if(the_class.isEnum()){
                return dict.get("String");
            }
            throw new Exception();
        }
        catch (Exception ex){
            throw new TypeConversionFailedException("Could not convert "+the_class.getSimpleName()+" to SQL");
        }
    }

    public static String toSQLValue(Object value){
        if(value.getClass().getSimpleName().equals("String"))
            return "'"+ value +"'";
        if(value.getClass().getSimpleName().equals("LocalDateTime"))
            return "'"+ ((LocalDateTime) value) +"'";
        if(value.getClass().getSimpleName().equals("LocalDate"))
            return "'"+ ((LocalDate) value) +"'";
        if(value.getClass().getSimpleName().equals("LocalTime"))
            return "'"+ ((LocalTime) value) +"'";

        if(value.getClass().isEnum())
            return "'"+ value +"'";

        if(value.getClass().getSimpleName().endsWith("oolean"))
            return Boolean.valueOf((boolean)value).toString().toUpperCase();
        return value.toString();
    }

    public static Object toJavaValue(Object value){
        if(Objects.equals(value.getClass(), Timestamp.class))
            return Timestamp.class.cast(value).toLocalDateTime();
        if(Objects.equals(value.getClass(), Date.class))
            return Date.class.cast(value).toLocalDate();
        if(Objects.equals(value.getClass(), Time.class))
            return Time.class.cast(value).toLocalTime();
        return value;
    }

    public static Object toJavaEnum(Object value, Class<?> the_enum) throws OrmException {
        try {
            Method valueOf = the_enum.getMethod("valueOf", String.class);
            String str_val = (String)value;
            return valueOf.invoke(null, str_val);
        }
        catch (Exception ex) {
            throw new OrmException("Could not convert " + value + " to the Enum " + the_enum.getSimpleName());
        }
    }
}
