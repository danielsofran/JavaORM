package orm.sql;

import orm.annotations.FK;
import orm.classparser.PropertyChecker;
import orm.classparser.PropertyParser;
import orm.exceptions.ForeignKeyException;
import orm.exceptions.PrimaryKeyException;
import orm.exceptions.TypeConversionFailedException;
import orm.sql.utils.JavaSQLMapper;

import java.lang.reflect.Field;
import java.util.List;

public class DDLWriter {

    private final PropertyParser<?> parser;

    public DDLWriter(Class<?> the_class){
        parser = new PropertyParser<>(the_class);
    }

    public PropertyParser<?> getParser(){
        return parser;
    }

    private String createLine(Field field) throws TypeConversionFailedException, ForeignKeyException {
        String rez = "\""+field.getName()+"\" ";
        String type = JavaSQLMapper.getSQLType(field.getType());
        if(type != null){
            rez += type;
        }
        else { // FK
            // get FK type
            PropertyParser<?> fkParser = new PropertyParser<>(field.getType());
            List<Field> pks = fkParser.getPKs();
            if(pks.size() != 1)
                throw new ForeignKeyException("Cannot create Foreign Key on table "+fkParser.getName()+" because it does not have exactly 1 Primary Key, but "+pks.size());
            Class<?> pktype = pks.get(0).getType();
            rez += JavaSQLMapper.getSQLType(pktype);
        }
        if(PropertyChecker.isNotNull(field))
            rez += " NOT NULL";
        if(PropertyChecker.isAutoInc(field))
            rez += " GENERATED ALWAYS AS IDENTITY";
        return rez;
    }

    private String createPKs() throws PrimaryKeyException {
        List<Field> pks = parser.getPKs();
        if(pks.size() < 1) throw new PrimaryKeyException("Please provide at least 1 Primary Key fro table "+parser.getName());
        String pk_enum = "";
        for(Field field : pks)
            pk_enum += '"'+field.getName()+"\", ";
        if(!pk_enum.equals(""))
            return "PRIMARY KEY("+pk_enum.substring(0, pk_enum.length()-2)+")";
        else return null;
    }

    private String createFKs(){
        String rez = "";
        List<Field> entityFKs = parser.getEntityFKs();
        List<Field> annotationFKs = parser.getAnnotationFKs();
        //List<PropertyParser<?>> refs = fks.stream().map(f -> new PropertyParser<>(f.getType())).collect(Collectors.toList());
        for(Field field : entityFKs){
            PropertyParser<?> fkParser = new PropertyParser<>(field.getType());
            String pkName = '"'+fkParser.getPKs().get(0).getName()+'"';
            String constraint = "CONSTRAINT fk_"+parser.getName()+"_"+fkParser.getName()+"\n\t"
                    +"FOREIGN KEY(\""+field.getName()+"\")\n\t"
                    +"REFERENCES \""+fkParser.getName()+"\"(\""+pkName+"\") ";
            if(PropertyChecker.isCascade(field))
                constraint+=
                    "ON DELETE CASCADE "+
                    "ON UPDATE CASCADE";
            if(PropertyChecker.isSetNull(field))
                constraint+=
                    "ON DELETE SET NULL "+
                    "ON UPDATE SET NULL";
            if(PropertyChecker.isNoAction(field))
                constraint+=
                        "ON DELETE NO ACTION "+
                                "ON UPDATE NO ACTION";
            rez += constraint + ",\n";
        }
        for(Field field : annotationFKs){
            //PropertyParser<?> fkParser = new PropertyParser<>(field.getType());
            FK FKa = field.getAnnotation(FK.class);
            String constraint = "CONSTRAINT fk_"+parser.getName()+"_"+FKa.Table()+"\n\t"
                    +"FOREIGN KEY(\""+field.getName()+"\")\n\t"
                    +"REFERENCES \""+FKa.Table()+"\"(\""+FKa.RefCol()+"\") ";
            if(PropertyChecker.isCascade(field))
                constraint+=
                        "ON DELETE CASCADE "+
                                "ON UPDATE CASCADE";
            if(PropertyChecker.isSetNull(field))
                constraint+=
                        "ON DELETE SET NULL "+
                                "ON UPDATE SET NULL";
            if(PropertyChecker.isNoAction(field))
                constraint+=
                        "ON DELETE NO ACTION "+
                                "ON UPDATE NO ACTION";
            rez += constraint + ",\n";
        }
        if(!rez.equals(""))
            rez = rez.substring(0, rez.length()-2);
        return rez;
    }

    public String getCreateSQL() throws TypeConversionFailedException, ForeignKeyException, PrimaryKeyException {
        String rez = "CREATE TABLE \""+parser.getName()+"\"(\n";
        for(Field field : parser.getFields())
            rez += createLine(field)+",\n";
        //rez = rez.substring(0, rez.length()-2);
        rez += "\n"+createPKs();
        String fks = createFKs();
        if(!fks.equals("")){
            rez += ",\n"+fks;
        }
        rez += "\n);";
        return rez;
    }

    public String getDropSQL(){
        return "DROP TABLE \""+parser.getName()+"\";";
    }
}
