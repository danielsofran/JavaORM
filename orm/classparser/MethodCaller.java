package orm.classparser;

import models.demo.Angajat;
import orm.exceptions.MethodIncorectlyDefinedException;
import orm.exceptions.MethodNotFoundException;
import orm.exceptions.OrmException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
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

    public static <T> T callGetter(Object instance, String name) throws OrmException {
        Class<?> clazz = instance.getClass();
        name=createSufix(name);
        Method method = null;
        do{
            try {
                method = clazz.getMethod("get" + name);
                break;
            }
            catch (NoSuchMethodException ne){
                clazz = clazz.getSuperclass();
            }
        } while (!clazz.getSimpleName().equals("Object"));
        if(method == null)
            throw new MethodNotFoundException("Getter get"+name+" of class "+clazz.getSimpleName()+" not found");
        Object returned;
        Class<T> returned_type;
        try{
            returned = method.invoke(instance);
            returned_type = (Class<T>) method.getReturnType();
        }
        catch (Exception ex){
            throw new OrmException(ex.getMessage());
        }
        if(!returned_type.isPrimitive())
            return returned_type.cast(returned);
        return (T) returned;
    }

    public static void callSetter(Object instance, String name, Object value) throws OrmException {
        Class<?> clazz = instance.getClass();
        name=createSufix(name);
        Method method = null;
        do{
            String finalName = name;
            method = Arrays.stream(clazz.getMethods())
                    .filter(m -> m.getParameterCount() == 1)
                    .filter(m -> m.getName().equals("set"+ finalName))
                    .findFirst().orElse(null);
            if(method != null)
                break;
            clazz = clazz.getSuperclass();
        } while (!clazz.getSimpleName().equals("Object"));
        if(method==null)
            throw new MethodNotFoundException("Setter set"+name+" of class "+clazz.getSimpleName()+" not found");
        try{
            method.invoke(instance, value);
        }
        catch (Exception ex){
            // wrapper class cast problem
            try{
                Class<?> required_type = Arrays.stream(method.getParameterTypes()).findFirst().orElse(null);
                if(required_type == null)
                    throw new MethodIncorectlyDefinedException("Setter set"+name+" of class "+clazz.getSimpleName()+" has wrong parameter!");
                Object casted = primitiveMap.get(required_type).cast(value);
                method.invoke(instance, casted);
            }
            catch (Exception ex2)
            {
                throw new OrmException("Setter wrapped class problem in MethodCaller\n"+ex2.getMessage());
            }
        }
    }

    private final static Map<Class<?>, Class<?>> primitiveMap = new HashMap<Class<?>, Class<?>>();
    static {
        primitiveMap.put(boolean.class, Boolean.class);
        primitiveMap.put(byte.class, Byte.class);
        primitiveMap.put(short.class, Short.class);
        primitiveMap.put(char.class, Character.class);
        primitiveMap.put(int.class, Integer.class);
        primitiveMap.put(long.class, Long.class);
        primitiveMap.put(float.class, Float.class);
        primitiveMap.put(double.class, Double.class);
    }

    private static String createSufix(String fieldName)
    {
        return fieldName.substring(0, 1).toUpperCase()+fieldName.substring(1);
    }
}
