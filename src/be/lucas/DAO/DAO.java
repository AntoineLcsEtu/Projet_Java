package be.lucas.DAO;

import be.lucas.util.DBConnection;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class DAO<T> {

    protected Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    public abstract boolean create(T obj);
    public abstract boolean delete(T obj);
    public abstract boolean update(T obj);
    public abstract T find(int id);
}