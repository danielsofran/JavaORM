package orm.sql;

import orm.exceptions.TypeConversionFailedException;

import java.util.HashMap;
import java.util.Map;

public class JavaSQLMapper {
    // Java     SQL
    static Map<String, String> dict = null;
    private static void init(){
        dict = new HashMap<>();
        dict.put("int", "INT");
        dict.put("Integer", "INT");
        dict.put("String", "VARCHAR");
        dict.put("boolean", "BOOLEAN");
        dict.put("double", "DECIMAL");
        dict.put("float", "DECIMAL");
        dict.put("Double", "DECIMAL");
        dict.put("Float", "DECIMAL");
        dict.put("LocalDateTime", "TIMESTAMP");
    }
    public static String getSQLType(String javaType) throws TypeConversionFailedException {
        if(dict == null)
            init();
        try {
            return dict.get(javaType);
        }
        catch (Exception ex){
            throw new TypeConversionFailedException("Could not convert "+javaType+" to SQL");
        }
    }
}
