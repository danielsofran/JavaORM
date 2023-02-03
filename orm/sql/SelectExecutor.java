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
import java.util.List;

public class SelectExecutor {
    ConnectionManager connectionManager;
    //private final PropertyParser<?> parser; // TABLE

    public SelectExecutor(ConnectionManager connectionManager){
        this.connectionManager = connectionManager; }

    // TODO: first PK
    public <T> T findByPK(Class<T> the_class, Object pkValue) throws OrmException, SQLException {
        PropertyParser<Class<T>> parser = new PropertyParser<>(the_class);
        List<Field> pks = parser.getPKs();
        if(pks.size()!=1)
            throw new PrimaryKeyException("The table "+the_class.getSimpleName()+" must have exactly 1 PK!");
        Field pk = pks.get(0);
        String SQL = "SELECT * FROM \""+parser.getName()+"\" WHERE \""+pk.getName()+"\" = "+JavaSQLMapper.toSQLValue(pkValue)+" LIMIT 1";

        List<Field> fields = parser.getFields();
        Object instance = MethodCaller.ctor(the_class);
        try(Connection connection = connectionManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(SQL);
            ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
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
        }
        return the_class.cast(instance);
    }

}
