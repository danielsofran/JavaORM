package tries.model_handle;

import models.demo.*;
import orm.ConnectionManager;
import orm.classparser.PropertyChecker;
import orm.exceptions.ForeignKeyException;
import orm.exceptions.OrmException;
import orm.classparser.PropertyParser;
import orm.ORM;
import orm.exceptions.PrimaryKeyException;
import orm.exceptions.TypeConversionFailedException;
import orm.sql.DDLWriter;

public class Try1 {
    public static void main(String[] args) throws TypeConversionFailedException, PrimaryKeyException, ForeignKeyException {
        PropertyParser<?> parser = new PropertyParser<>(Angajat.class);
        //Field[] fields = Angajat.class.getSuperclass().getDeclaredFields();
        //System.out.println(parser.getNrAllFK());
        //System.out.println(int.class.getTypeName().substring(0, 1));
        ORM orm = new ORM(new ConnectionManager());
        DDLWriter writer = new DDLWriter(Persoana2.class);
        System.out.println(writer.getCreateSQL());

        try {
            //orm.dropTables(Persoana.class, MData.class, Angajat.class);
            //orm.createTables(Persoana.class, MData.class, Angajat.class);
            orm.dropTables(Persoana2.class);
            orm.createTables(Persoana2.class);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

    }
}
