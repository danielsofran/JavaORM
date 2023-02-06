package orm;

import orm.classparser.MethodCaller;
import orm.classparser.PropertyChecker;
import orm.classparser.PropertyParser;
import orm.exceptions.DuplicateDataException;
import orm.exceptions.OrmException;
import orm.exceptions.PrimaryKeyException;
import orm.sql.DDLWriter;
import orm.sql.DMLWriter;
import orm.sql.utils.JavaSQLMapper;
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
        if(ais.size()>1)
            throw new PrimaryKeyException("Entity "+the_class.getSimpleName()+" must have at most 1 Auto Increment field, bat it has "+ais.size()+"!");
        String insertSQL = DMLWriter.getInsertSQL(obj);

        Connection connection=connectionManager.getConnection();
        Statement s = connection.createStatement();
        try {
            s.executeUpdate(insertSQL);
        }
        catch (SQLException sqlException)
        {
            if(sqlException.getMessage().contains("duplicate key value violates unique constraint"))
                throw new DuplicateDataException("Value "+obj+" already exists in the table "+the_class.getSimpleName());
            else throw sqlException;
        }

        if(ais.size() == 1) {
            Field ai = ais.get(0);
            String sqlType = ai!=null?JavaSQLMapper.getSQLType(ai.getType()):"";
            String selectSQL = "SELECT CAST(LASTVAL() AS "+sqlType+")";
            String fname = "";
            try{fname=ai.getName();}
            catch (NullPointerException ex){
                throw new OrmException("AutoIncrement field getName() for INSERT in table "+the_class.getSimpleName()+" failed!");
            }

            ResultSet rs = s.executeQuery(selectSQL);
            rs.next();

            Object value = rs.getObject(1);
            MethodCaller.callSetter(obj, fname, value);
            rs.close();
        }
        connection.close();
        return the_class.cast(obj);
    }

    public <T> List<T> select(Class<T> table) throws SQLException, OrmException {
        SelectExecutor se = new SelectExecutor(connectionManager);
        return se.executeSelectAll(table);
    }

    public <T> T select(Class<T> table, Object... pkValues) throws OrmException, SQLException {
        SelectExecutor se = new SelectExecutor(connectionManager);
        return se.findByPK(table, pkValues);
    }

    public <T> void delete(Class<T> table, Object... pkValues) throws OrmException, SQLException {
        String SQL = DMLWriter.getDeleteSQL(table, pkValues);
        connectionManager.executeUpdateSql(SQL);
    }

    public <T> void update(T updated, Object... pkValues) throws OrmException, SQLException {
        String SQL = DMLWriter.getUpdateSQL(updated, pkValues);
        connectionManager.executeUpdateSql(SQL);
    }

}
