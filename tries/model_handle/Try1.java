package tries.model_handle;

import models.demo.Ore;
import orm.ConnectionManager;
import orm.classparser.PropertyChecker;
import orm.exceptions.OrmException;
import models.demo.Angajat;
import orm.classparser.PropertyParser;
import models.demo.MData;
import models.demo.Persoana;
import orm.ORM;
import orm.sql.DDLWriter;

public class Try1 {
    public static void main(String[] args) {
        PropertyParser<?> parser = new PropertyParser<>(Angajat.class);
        //Field[] fields = Angajat.class.getSuperclass().getDeclaredFields();
        //System.out.println(parser.getNrAllFK());
        //System.out.println(int.class.getTypeName().substring(0, 1));
        ORM orm = new ORM(new ConnectionManager());

        try {
            //orm.dropTables(Persoana.class, MData.class, Angajat.class);
            //orm.createTables(Persoana.class, MData.class, Angajat.class);
            orm.dropTables(Ore.class);
            orm.createTables(Ore.class);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

    }
}
