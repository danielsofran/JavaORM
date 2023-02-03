package orm.classparser;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class PropertyParser<T extends Class<?>> {
    private final T currentClass;
    private List<Field> fields; // all fields from hierarchy of entities
    private List<Field> PKs; // primary keys
    private List<Field> FKs; // foreign keys
    private List<Field> autoIncs; // auto increment fields

    private List<Field> getPKs(Class<?> the_class){
        if(checkClassRecursion(the_class)) {
            List<Field> fields = PropertyChecker.getFields(the_class);
            List<Field> pkc = fields.stream().filter(PropertyChecker::isPK).collect(Collectors.toList());
            if (pkc.size() == 0) {
                return getPKs(the_class.getSuperclass());
            } else {
                return pkc;
            }
        }
        return new LinkedList<>();
    }

    private List<Field> getFKs(Class<?> the_class)
    {
        if(checkClassRecursion(the_class)) {
            List<Field> fields = PropertyChecker.getFields(the_class);
            List<Field> fks = fields.stream().filter(PropertyChecker::isFK).collect(Collectors.toList());
            List<Field> next = getFKs(the_class.getSuperclass());
            fks.addAll(next);
            return fks;
        }
        return new LinkedList<>();
    }

    private boolean checkClassRecursion(Class<?> the_class){
        return !(the_class == null || the_class.getSimpleName().equals("Object") || !PropertyChecker.isEntity(the_class));
    }

    private List<Field> getFieldsRec(Class<?> the_class)
    {
        if(checkClassRecursion(the_class)) {
            List<Field> current = (Arrays.stream(the_class.getDeclaredFields()).collect(Collectors.toList()));
            List<Field> next = getFieldsRec(the_class.getSuperclass());
            next.addAll(current);
            return next;
        }
        return new LinkedList<>();
    }

    public PropertyParser(T the_class){
        currentClass = the_class;
        fields = getFieldsRec(currentClass);
        PKs = getPKs(currentClass);
        FKs = getFKs(currentClass);
        autoIncs = PKs.stream().filter(PropertyChecker::isAutoInc).collect(Collectors.toList());
    }

    public Class<?> getContainedClass(){
        return currentClass;
    }

    public String getName(){
        return currentClass.getSimpleName();
    }

    public List<Field> getFields() { return fields; }
    public List<Field> getPKs() { return PKs; }
    public List<Field> getFKs() { return FKs; }
    public List<Field> getAutoIncs() { return autoIncs; }

    private List<Class<?>> getAllClasses(Class<?> the_class)
    {
        if(checkClassRecursion(the_class))
        {
            List<Class<?>> rez = new LinkedList<>();
            rez.add(the_class);
            List<Field> fieldList = getFieldsRec(the_class);
            for(Field field : fieldList)
            {
                rez.addAll(getAllClasses(field.getType()));
            }
            return rez.stream().distinct().collect(Collectors.toList());
        }
        return new LinkedList<>();
    }

    public int getNrAllFK() {
        List<Class<?>> classes = getAllClasses(currentClass);
        return classes.size();
    }

    @Override
    public String toString() {
        return Integer.toString(PKs.size());
    }
}
