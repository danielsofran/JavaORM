package orm;

import orm.classparser.PropertyParser;
import orm.exceptions.OrmException;
import orm.sql.DDLWriter;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ORM {
    ConnectionManager connectionManager;

    public ORM(ConnectionManager connectionManager)
    {
        this.connectionManager = connectionManager;
    }

    private static int sortClasses(Class<?> c1, Class<?> c2){
        PropertyParser<?> parser1 = new PropertyParser<>(c1);
        PropertyParser<?> parser2 = new PropertyParser<>(c2);
        return parser1.getNrAllFK() - parser2.getNrAllFK();
    }

    public void createTables(Class<?>... classes) throws OrmException, SQLException {
        List<DDLWriter> writers = Arrays.stream(classes).sorted(ORM::sortClasses).map(DDLWriter::new).collect(Collectors.toList());
        if(writers.size() == 0)
            return;
        PropertyParser<?> first = writers.get(0).getParser();
        if(first.getNrAllFK() != 1)
        {
            String fk = first.getFKs().stream()
                    .map(f -> "Table: "+f.getDeclaringClass().getSimpleName()+", Type: "+f.getType().getSimpleName()+", Field:"+f.getName()+"\n")
                    .reduce((c1, c2) -> c1 + " " + c2 + " ").orElse("");
            throw new OrmException("Cannot create model because references for \n"+fk+" are missing");
        }
        for(DDLWriter sqlScript : writers)
        {
            //System.out.println(sqlScript.getSQL()+"\n\n");
            connectionManager.executeUpdateSql(sqlScript.getCreateSQL());
        }
    }

    public void dropTables(Class<?>... classes) throws SQLException {
        List<DDLWriter> writers = Arrays.stream(classes).sorted(ORM::sortClasses).map(DDLWriter::new).collect(Collectors.toList());
        Collections.reverse(writers);
        for(DDLWriter sqlScript : writers)
        {
            connectionManager.executeUpdateSql(sqlScript.getDropSQL());
        }
    }
}
