package com.example.cs307project2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class PostWindow implements Initializable {

    public TextField title;
    public TextField category;
    public TextField country;
    public TextField city;
    public TextArea content;

    private static Connection con = null;
    static {
        try {
            con = C3P0Util.getConnection();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private static PreparedStatement stmt = null;
    public HBox hbox;
    //    static Properties prop = loadDBUser();
    String username = HelloController.username;
    String path;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
    public void post(){
        PreparedStatement statement,s2,s3;
        try {
            statement = con.prepareStatement("select max(id) from post;");
            stmt = con.prepareStatement("insert into post(id, title, content, postingtime, city, country,anonymous,image) VALUES (?,?,?,?,?,?,?,?)");
            s2 = con.prepareStatement("insert into authorpost (postid, authorname) values(?,?);");
            s3 = con.prepareStatement("insert into category(postid, category) values(?,?);");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (con != null) {
            try {
                ResultSet resultSet = statement.executeQuery();
                int max = 0;
                if (resultSet.next()){
                    max = resultSet.getInt("max");
                }
                FileInputStream in = ImageUtil.readImage(path);
                stmt.setInt(1,max+1);
                stmt.setString(2, title.getText());
                stmt.setString(3, content.getText());
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                stmt.setTimestamp(4,timestamp);
                stmt.setString(5, city.getText());
                stmt.setString(6, country.getText());
                stmt.setBoolean(7,false);
                stmt.setBinaryStream(8,in,in.available());
                stmt.executeUpdate();
                s2.setInt(1,max+1);
                s2.setString(2,username);
                s2.executeUpdate();
                String[] arr = category.getText().split(",");
                for (int i = 0; i < arr.length; i++) {
                    s3.setInt(1,max+1);
                    s3.setString(2,arr[i]);
                    s3.executeUpdate();
                }
            } catch (SQLException | IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    public void postAnonymously(){
        PreparedStatement statement,s2,s3;
        try {
            statement = con.prepareStatement("select max(id) from post;");
            stmt = con.prepareStatement("insert into post(id, title, content, postingtime, city, country,anonymous,image) VALUES (?,?,?,?,?,?,?,?)");
            s2 = con.prepareStatement("insert into authorpost (postid, authorname) values(?,?);");
            s3 = con.prepareStatement("insert into category(postid, category) values(?,?);");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (con != null) {
            try {
                FileInputStream in = ImageUtil.readImage(path);
                ResultSet resultSet = statement.executeQuery();
                int max = 0;
                if (resultSet.next()){
                    max = resultSet.getInt("max");
                }
                stmt.setInt(1,max+1);
                stmt.setString(2, title.getText());
                stmt.setString(3, content.getText());
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                stmt.setTimestamp(4,timestamp);
                stmt.setString(5, city.getText());
                stmt.setString(6, country.getText());
                stmt.setBoolean(7,true);
                stmt.setBinaryStream(8,in,in.available());
                stmt.executeUpdate();
                s2.setInt(1,max+1);
                s2.setString(2,username);
                s2.executeUpdate();
                String[] arr = category.getText().split(",");
                for (int i = 0; i < arr.length; i++) {
                    s3.setInt(1,max+1);
                    s3.setString(2,arr[i]);
                    s3.executeUpdate();
                }
            } catch (SQLException | IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    public void addImage(){
        FileChooser fileChooser = new FileChooser();
        File file =  fileChooser.showOpenDialog(new Stage());
        //避免空指针异常
        if(file == null){
            return;
        }
        path = file.getPath();
    }
//    public void openDB() throws IOException {
//        try {
//            Class.forName("org.postgresql.Driver");
//        } catch (Exception e) {
//            System.err.println("Cannot find the postgres driver. Check CLASSPATH.");
//            System.exit(1);
//        }
//        String url = "jdbc:postgresql://" + prop.getProperty("host") + "/" + prop.getProperty("database");
//        try {
//            con = DriverManager.getConnection(url, prop);
//            if (con != null) {
//                System.out.println("Successfully connected to the database "
//                    + prop.getProperty("database") + " as " + prop.getProperty("user"));
//            }
//        } catch (SQLException e) {
//            System.err.println("Database connection failed");
//            System.err.println(e.getMessage());
//            System.exit(1);
//        }
//    }
//    private static Properties loadDBUser() {
//        Properties properties = new Properties();
//        try {
//            properties.load(new InputStreamReader(new FileInputStream("dbUser.properties")));
//            return properties;
//        } catch (IOException e) {
//            System.err.println("can not find db user file");
//            throw new RuntimeException(e);
//        }
//    }
//    private static void closeDB() {
//        if (con != null) {
//            try {
//                if (stmt != null) {
//                    stmt.close();
//                }
//                con.close();
//                con = null;
//            } catch (Exception ignored) {
//            }
//        }
//    }
}
