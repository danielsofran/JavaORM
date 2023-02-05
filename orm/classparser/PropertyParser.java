package orm.classparser;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class PropertyParser<T extends Class<?>> {
    private final T currentClass;
    private List<Field> fields = null; // all fields from hierarchy of entities
    private List<Field> PKs = null; // primary keys
    private List<Field> EntityFKs = null; // foreign keys by aggregation
    private List<Field> AnnotationFKs = null; // foreign keys by annotation
    private List<Field> autoIncs = null; // auto increment fields

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

    private List<Field> getEntityFKs(Class<?> the_class)
    {
        if(checkClassRecursion(the_class)) {
            List<Field> fields = PropertyChecker.getFields(the_class);
            List<Field> fks = fields.stream().filter(PropertyChecker::isFKEntity).collect(Collectors.toList());
            List<Field> next = getEntityFKs(the_class.getSuperclass());
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
    }

//    public Class<?> getContainedClass(){
//        return currentClass;
//    }

    public String getName(){
        return currentClass.getSimpleName();
    }

    public List<Field> getFields() {
        if(fields != null)
            return fields;
        fields = getFieldsRec(currentClass);
        return fields;
    }
    public List<Field> getPKs() {
        if(PKs != null)
            return PKs;
        PKs = getPKs(currentClass);
        return PKs;
    }
    public List<Field> getEntityFKs() {
        if(EntityFKs!=null)
            return EntityFKs;
        EntityFKs = getEntityFKs(currentClass);
        return EntityFKs;
    }
    public List<Field> getAnnotationFKs(){
        if(AnnotationFKs!=null)
            return AnnotationFKs;
        if(fields==null)
            fields=getFieldsRec(currentClass);
        AnnotationFKs = fields.stream().filter(PropertyChecker::isFKAnnotation).collect(Collectors.toList());
        return AnnotationFKs;
    }
    public List<Field> getAutoIncs() {
        if(autoIncs!=null)
            return autoIncs;
        if(PKs==null)
            PKs = getPKs(currentClass);
        autoIncs = PKs.stream().filter(PropertyChecker::isAutoInc).collect(Collectors.toList());
        return autoIncs;
    }

//    private List<Class<?>> getAllClasses(Class<?> the_class)
//    {
//        if(checkClassRecursion(the_class))
//        {
//            List<Class<?>> rez = new LinkedList<>();
//            rez.add(the_class);
//            List<Field> fieldList = getFieldsRec(the_class);
//            for(Field field : fieldList)
//                if(PropertyChecker.isFKEntity(field))
//                    rez.addAll(getAllClasses(field.getType()));
//                else if(PropertyChecker.isFKAnnotation(field))
//                    rez.add(field.getType());
//            return rez.stream().distinct().collect(Collectors.toList());
//        }
//        return new LinkedList<>();
//    }

    private int getNrAllFK(Class<?> the_class)
    {
        if(checkClassRecursion(the_class))
        {
            int rez = 1;
            List<Field> fieldList = getFieldsRec(the_class);
            for(Field field : fieldList)
                if(PropertyChecker.isFKEntity(field))
                    rez += getNrAllFK(field.getType());
                else if(PropertyChecker.isFKAnnotation(field))
                    rez += 1;
            return rez;
        }
        return 0;
    }

    public int getNrAllFK() {
        return getNrAllFK(currentClass);
    }

    @Override
    public String toString() {
        return Integer.toString(PKs.size());
    }
}
