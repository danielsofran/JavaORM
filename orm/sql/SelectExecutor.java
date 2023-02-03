package orm.sql;

import orm.ConnectionManager;
import orm.classparser.MethodCaller;
import orm.classparser.PropertyChecker;
import orm.classparser.PropertyParser;
import orm.exceptions.OrmException;
import orm.exceptions.PrimaryKeyException;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class SelectExecutor {
    ConnectionManager connectionManager;
    //private final PropertyParser<?> parser; // TABLE

    public SelectExecutor(ConnectionManager connectionManager){
        this.connectionManager = connectionManager; }

    public <T> T findByPK(Class<T> the_class, Object pkValue) throws OrmException, SQLException {
        PropertyParser<Class<T>> parser = new PropertyParser<>(the_class);
        List<Field> pks = parser.getPKs();
        if(pks.size()!=1)
            throw new PrimaryKeyException("The table "+the_class.getSimpleName()+" must have exactly 1 PK!");
        Field pk = pks.get(0);
        String SQL = "SELECT * FROM \""+parser.getName()+"\" WHERE \""+pk.getName()+"\" = "+JavaSQLMapper.toSQLValue(pkValue)+" LIMIT 1";

        T rez = null;
        try(Connection connection = connectionManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(SQL);
            ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            rez = createEntityFromResultSet(resultSet, the_class);
        }
        return rez;
    }

    public <T> List<T> executeSelectAll(Class<T> the_class) throws SQLException, OrmException {
        String SQL = "SELECT * FROM \""+the_class.getSimpleName()+"\"";
        List<T> rez = new LinkedList<>();

        try(Connection connection=connectionManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(SQL);
            ResultSet resultSet = statement.executeQuery())
        {
            while (resultSet.next())
            {
                T elem = createEntityFromResultSet(resultSet, the_class);
                rez.add(elem);
            }
        }
        return rez;
    }

    private <T> T createEntityFromResultSet(ResultSet resultSet, Class<T> the_class) throws OrmException, SQLException {
        PropertyParser<Class<T>> parser = new PropertyParser<>(the_class);
        List<Field> fields = parser.getFields();
        Object instance = MethodCaller.ctor(the_class);

        for (Field field : fields){
            Object obj = null;
            if(!PropertyChecker.isFK(field))
                obj = resultSet.getObject(field.getName());
            else {
                Object idObj = resultSet.getObject(field.getName());
                obj = findByPK(field.getType(), idObj);
            }

            if(!field.getType().isEnum())
                obj = JavaSQLMapper.toJavaValue(obj);
            else
                obj = JavaSQLMapper.toJavaEnum(obj, field.getType());
            MethodCaller.callSetter(instance, field.getName(), obj);
        }
        return the_class.cast(instance);
    }

}
