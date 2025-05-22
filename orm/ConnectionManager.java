package orm;

import java.sql.*;

public class ConnectionManager {
    private String url; // jdbc:postgresql://localhost:5432/TestORM
    private final String username;
    private final String password;
    private final String address;

    public ConnectionManager(){
        username = "postgres";
        password = "parola";
        address = "localhost:5432";
        setDatabase("TestORM");
    }

    public ConnectionManager(String database, String username, String password){
        this.username = username;
        this.password = password;
        address = "localhost:5432";
        setDatabase(database);
    }

    public ConnectionManager(String database, String username, String password, String address){
        this.username = username;
        this.password = password;
        this.address = address;
        setDatabase(database);
    }

    public void setDatabase(String database)
    {
        url = "jdbc:postgresql://"+ address +"/"+database;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    public void executeUpdateSql(String sql) throws SQLException {
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.executeUpdate();
        }
    }
}
