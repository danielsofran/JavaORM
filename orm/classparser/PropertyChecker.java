package orm.classparser;

import orm.annotations.*;
import orm.annotations.rules.Cascade;
import orm.annotations.rules.NoAction;
import orm.annotations.rules.SetNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PropertyChecker {
    public static boolean isEntity(Class<?> the_class){
        Annotation annotation = the_class.getAnnotation(DBEntity.class);
        return !(annotation == null);
    }

    public static boolean isPK(Field the_class){
        Annotation annotation = the_class.getAnnotation(PK.class);
        return !(annotation == null);
    }

    public static boolean isAutoInc(Field the_class){
        Annotation annotation = the_class.getAnnotation(AutoInc.class);
        return !(annotation == null);
    }

    public static boolean isFKEntity(Field the_class){
        return isEntity(the_class.getType());
    }

    public static boolean isFKAnnotation(Field the_class){
        Annotation annotation = the_class.getAnnotation(FK.class);
        return !(annotation == null);
    }

    public static boolean isCascade(Field the_class){
        Annotation annotation = the_class.getAnnotation(Cascade.class);
        return !(annotation == null) && isFKEntity(the_class);
    }

    public static boolean isNoAction(Field the_class){
        Annotation annotation = the_class.getAnnotation(NoAction.class);
        return !(annotation == null) && isFKEntity(the_class);
    }

    public static boolean isSetNull(Field the_class){
        Annotation annotation = the_class.getAnnotation(SetNull.class);
        return !(annotation == null) && isFKEntity(the_class);
    }

    public static boolean isNotNull(Field the_class){
        Annotation annotation = the_class.getAnnotation(DBNotNull.class);
        return !(annotation == null);
    }

    public static List<Field> getFields(Class<?> the_class)
    {
        return Arrays.stream(the_class.getDeclaredFields()).collect(Collectors.toList());
    }
}
