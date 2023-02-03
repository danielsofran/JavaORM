package orm.classparser;

import models.demo.Angajat;
import orm.exceptions.MethodIncorectlyDefinedException;
import orm.exceptions.MethodNotFoundException;
import orm.exceptions.OrmException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MethodCaller {
    public static <T> T ctor(Class<T> clazz) throws MethodNotFoundException {
        PropertyParser<Class<T>> parser = new PropertyParser<>(clazz);
        Constructor<?>[] ctors = clazz.getConstructors();
        Constructor<?> ctor = null;
        for(Constructor<?> constructor : ctors)
            if(constructor.getParameterCount() == 0)
                ctor = constructor;
        if(ctor == null)
            throw new MethodNotFoundException("No appropriate constructor found for class "+parser.getName());
        Object returned = null;
        try {
            returned = ctor.newInstance();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        if (returned == null)
            throw new MethodNotFoundException("No appropriate constructor found for class "+parser.getName());
        return clazz.cast(returned);
    }

    public static <T> T ctor(Class<T> clazz, Object[] params_value) throws MethodNotFoundException {
        PropertyParser<Class<T>> parser = new PropertyParser<>(clazz);
        Object[] oparams = parser.getFields().stream().map(f -> f.getType()).toArray();
        List<Class<?>> lparams = Arrays.stream(oparams).map(o -> (Class<?>)o).collect(Collectors.toList());
        Constructor<?>[] ctors = clazz.getConstructors();
        Constructor<?> ctor = null;
        for(Constructor<?> constructor : ctors)
            if(constructor.getParameterCount() == lparams.size())
                if(Arrays.equals(constructor.getParameterTypes(), oparams))
                    ctor = constructor;
        if(ctor == null)
            throw new MethodNotFoundException("No appropriate constructor found for class "+parser.getName());
        Object returned = null;
        try {
            returned = ctor.newInstance(params_value);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        if (returned == null)
            throw new MethodNotFoundException("No appropriate constructor found for class "+parser.getName());
        return clazz.cast(returned);
    }

    public static <T> T callGetter(Object instance, String sufix) throws OrmException {
        Class<?> clazz = instance.getClass();
        Method method = null;
        do{
            try {
                method = clazz.getMethod("get" + sufix);
                break;
            }
            catch (NoSuchMethodException ne){
                clazz = clazz.getSuperclass();
            }
        } while (!clazz.getSimpleName().equals("Object"));
        if(method == null)
            throw new MethodNotFoundException("Getter get"+sufix+" of class "+clazz.getSimpleName()+" not found");
        Object returned;
        Class<T> returned_type;
        try{
            returned = method.invoke(instance);
            returned_type = (Class<T>) method.getReturnType();
        }
        catch (Exception ex){
            throw new OrmException(ex.getMessage());
        }
        return returned_type.cast(returned);
    }

    public static void callSetter(Object instance, String sufix, Object value) throws OrmException {
        Class<?> clazz = instance.getClass();
        Method method = null;
        do{
            method = Arrays.stream(clazz.getMethods())
                    .filter(m -> m.getParameterCount() == 1)
                    .filter(m -> m.getName().equals("set"+sufix))
                    .findFirst().orElse(null);
            if(method != null)
                break;
            clazz = clazz.getSuperclass();
        } while (!clazz.getSimpleName().equals("Object"));
        if(method==null)
            throw new MethodNotFoundException("Setter set"+sufix+" of class "+clazz.getSimpleName()+" not found");
        try{
            method.invoke(instance, value);
        }
        catch (Exception ex){
            // wrapper class cast problem
            try{
                Class<?> reqired_type = Arrays.stream(method.getParameterTypes()).findFirst().orElse(null);
                if(reqired_type == null)
                    throw new MethodIncorectlyDefinedException("Setter set"+sufix+" of class "+clazz.getSimpleName()+" has wrong parameter!");
                Object casted = reqired_type.cast(value);
                method.invoke(instance, casted);
            }
            catch (Exception ex2)
            {
                throw new OrmException(ex2.getMessage());
            }
        }
    }

    private static Object castToType(Object value, Class<?> type)
    {
        if(Objects.equals(type, double.class))
            return (double) value;
        if(Objects.equals(type, float.class))
            return (float) value;
        return value;
    }
}
