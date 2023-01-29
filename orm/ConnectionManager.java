package orm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ConnectionManager {
    private String url; // jdbc:postgresql://localhost:5432/TestORM
    private final String username;
    private final String password;
    private final String port;

    public ConnectionManager(){
        username = "postgres";
        password = "parola";
        port = "5432";
        setDatabase("TestORM");
    }

    public ConnectionManager(String database){
        username = "postgres";
        password = "parola";
        port = "5432";
        setDatabase(database);
    }

    public ConnectionManager(String database, String username, String password){
        this.username = username;
        this.password = password;
        port = "5432";
        setDatabase(database);
    }

    public ConnectionManager(String database, String username, String password, String port){
        this.username = username;
        this.password = password;
        this.port = port;
        setDatabase(database);
    }

    public void setDatabase(String database)
    {
        url = "jdbc:postgresql://localhost:"+port+"/"+database;
    }

    public void executeUpdateSql(String sql) throws SQLException {
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.executeUpdate();
        }
    }
}
