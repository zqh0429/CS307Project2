package com.example.cs307project2;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.mchange.v2.ser.Indirector;

import javax.sql.DataSource;

public class C3P0Util {
    private static ComboPooledDataSource dataSource;
    static {
        dataSource = new ComboPooledDataSource();
        try {
            dataSource.setDriverClass("org.postgresql.Driver");
            dataSource.setJdbcUrl("jdbc:postgresql://localhost:5432/project2");
            dataSource.setUser("visitor");
            dataSource.setPassword("123456");
            dataSource.setInitialPoolSize(10);
            dataSource.setMaxPoolSize(100);
        } catch (PropertyVetoException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取Connection
     * @return
     * @throws Exception
     */
    public static Connection getConnection() throws Exception{
        //从数据源中获取连接
        return dataSource.getConnection();
    }

    /**
     * 关闭三个资源，dao层有多个方法会用到关闭资源的操作，所以进行封装
     * @param conn
     * @param statement
     * @param resultSet
     */
    public static void closeAll(Connection conn, Statement statement, ResultSet resultSet){
        if(resultSet != null){
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if(statement != null){
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if(conn != null){
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

