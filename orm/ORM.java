package orm;

import orm.classparser.MethodCaller;
import orm.classparser.PropertyChecker;
import orm.classparser.PropertyParser;
import orm.exceptions.OrmException;
import orm.exceptions.PrimaryKeyException;
import orm.sql.DDLWriter;
import orm.sql.InsertWriter;
import orm.sql.JavaSQLMapper;
import orm.sql.SelectExecutor;

import java.lang.reflect.Field;
import java.sql.*;
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

    public <T> T insert(Object obj) throws OrmException, SQLException {
        Class<T> the_class = (Class<T>) obj.getClass();
        if(!PropertyChecker.isEntity(the_class)){
            throw new OrmException("Insert: Object class should be with entity annotation");
        }

        PropertyParser<Class<T>> parser = new PropertyParser<>(the_class);
        List<Field> ais = parser.getAutoIncs();
        if(ais.size()!=1)
            throw new PrimaryKeyException("Entity "+the_class.getSimpleName()+" must have exactly 1 Auto Increment field, bat it has "+ais.size()+"!");
        Field ai = ais.get(0);
        String sqlType = JavaSQLMapper.getSQLType(ai.getType());

        String insertSQL = InsertWriter.getInsertSQL(obj);
        String selectSQL = "SELECT CAST(LASTVAL() AS "+sqlType+")";

        Connection connection=connectionManager.getConnection();
        Statement s = connection.createStatement();
        s.executeUpdate(insertSQL);

        ResultSet rs = s.executeQuery(selectSQL);
        rs.next();
        String fname = ai.getName();
        Object value = rs.getObject(1);
        MethodCaller.callSetter(obj, fname, value);
        rs.close();
        connection.close();
        return the_class.cast(obj);
    }

    public <T> List<T> select(Class<T> table) throws SQLException, OrmException {
        SelectExecutor se = new SelectExecutor(connectionManager);
        return se.executeSelectAll(table);
    }

    public <T> T select(Class<T> table, Object pkValue) throws OrmException, SQLException {
        SelectExecutor se = new SelectExecutor(connectionManager);
        return se.findByPK(table, pkValue);
    }

}
