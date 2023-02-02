package orm.classparser;

import models.demo.Angajat;
import orm.exceptions.MethodNotFoundException;
import orm.exceptions.OrmException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MethodCaller {
    private static Object ctor(Class<?> clazz, Object[] params_value) throws MethodNotFoundException {
        PropertyParser<?> parser = new PropertyParser<>(clazz);
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
        return returned;
    }

    public static Object callGetter(Object instance, String sufix) throws OrmException {
        Class<?> clazz = instance.getClass();
        Method method = null;
        try {
            method = clazz.getMethod("get" + sufix);
        }
        catch (NoSuchMethodException ne){throw new MethodNotFoundException("Getter get"+sufix+" of class "+clazz.getSimpleName()+" not found");}
        Object returned;
        try{
            returned = method.invoke(instance);
        }
        catch (Exception ex){
            throw new OrmException(ex.getMessage());
        }
        return returned;
    }

    public static void callSetter(Object instance, String sufix, Object value) throws OrmException {
        Class<?> clazz = instance.getClass();
        Method method = null;
        try {
            //method = clazz.getMethod("set" + sufix, value.getClass());
            method = Arrays.stream(clazz.getMethods())
                    .filter(m -> m.getParameterCount() == 1)
                    .filter(m -> m.getName().equals("set"+sufix))
                    .findFirst().orElse(null);
            if (method == null)
                throw new NoSuchMethodException();
        }
        catch (NoSuchMethodException ne){throw new MethodNotFoundException("Setter set"+sufix+" of class "+clazz.getSimpleName()+" not found");}
        try{
            method.invoke(instance, value);
        }
        catch (Exception ex){
            throw new OrmException(ex.getMessage());
        }
    }
}
