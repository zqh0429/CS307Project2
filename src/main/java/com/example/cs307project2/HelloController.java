package com.example.cs307project2;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ModuleLayer.Controller;
import java.net.Socket;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.mail.MessagingException;

public class HelloController implements Initializable {

    public Label label1;
    public Label label2;
    public Button loginButton;
    public TextField usernameText;
    public TextField passwordText;
    public static String username;
    public static int identify;
    public static String email;

    private static Connection con;

    static {
        try {
            con = C3P0Util.getConnection();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static boolean isManager = false;

    private static PreparedStatement stmt = null;
    //sql语句编译上提高效率
    //一次性编译sql语句，每一次执行的时候直接编译
//    static Properties prop = loadDBUser();


    @FXML
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
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        //            openDB();
        try {
//            con = C3P0Util.getConnection();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void login(){
        if (usernameText.getText() != null && passwordText.getText() != null){
            PreparedStatement s1;
            username = usernameText.getText();
            String password = passwordText.getText();
            try {
                stmt = con.prepareStatement("select * from author_info where authorName = ? and password = ?");
                s1 = con.prepareStatement("select email from author_info where authorname = ? ");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if (con != null) {
                try {
                    stmt.setString(1,username);
                    stmt.setString(2,password);
                    ResultSet rs = stmt.executeQuery();
                    if(rs.next()){
                        System.out.println("success!");
                        s1.setString(1,username);
                        ResultSet set = s1.executeQuery();
                        while (set.next()){
                            email = set.getString("email");
                        }
                        isManager = false;
                        Stage stage = new Stage();
                        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("main.fxml"));
                        Scene scene = new Scene(fxmlLoader.load());
                        stage.setScene(scene);
                        stage.show();
                    }else{
                        System.out.println("fail!");
                    }
                } catch (SQLException | IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            System.out.println(stmt.toString());
        }
    }
    public void register(){
        Random random = new Random();
        identify = random.nextInt(1000,9999);
        System.out.println("A");
        Stage stage = new Stage();
        Label label1 = new Label("Username: ");
        Label label2 = new Label("Password: ");
        Label label3 = new Label("Phone Number: ");
        Label label4 = new Label("Email: ");
        Label label5 = new Label("Identify Code: ");
        TextField username_text = new TextField();
        TextField password_text = new TextField();
        TextField phone_text = new TextField();
        TextField email_text = new TextField();
        TextField code = new TextField();
        Button buttonSend = new Button("Send");
        HBox hBox1 = new HBox(label1,username_text);
        HBox hBox2 = new HBox(label2,password_text);
        HBox hBox3 = new HBox(label3,phone_text);
        HBox hBox4 = new HBox(label4,email_text,buttonSend);
        HBox hBox5 = new HBox(label5,code);
        buttonSend.setOnAction(event -> {
            try {
                email = email_text.getText();
                System.out.println(email);
                MailTest.send();
            } catch (MessagingException | IOException e) {
                throw new RuntimeException(e);
            }
        });
        Button button = new Button("Register");
        VBox vBox = new VBox(hBox1,hBox2,hBox3,hBox4,hBox5,button);
        stage.setScene(new Scene(vBox));
        button.setOnAction(event -> {
            if (code.getText().equals(String.valueOf(identify)) && password_text.getText().length() != 11){
                try {
                    createNewAuthor(username_text.getText(),phone_text.getText(),password_text.getText());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }else {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Alert");
                alert.setHeaderText("Wrong");
                alert.showAndWait();
            }
        });
        stage.show();
    }
    public static void createNewAuthor(String name,String phone,String password) throws SQLException{
        stmt = con.prepareStatement("INSERT INTO author (authorName, authorRegistrationTime, authorID, authorPhone) " +
            "VALUES (?,?,?,?);");
        if (con != null) {
            try {
                stmt.setString(1,name);
                SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
                Date date = new Date(System.currentTimeMillis());
                stmt.setDate(2,date);
                Random random = new Random();
                String stringBuilder = String.valueOf(random.nextInt(99999999, 999999999))
                    + random.nextInt(99999999, 999999999);
                stmt.setString(3, stringBuilder);
                stmt.setString(4,phone);
                stmt.executeUpdate();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
        stmt = con.prepareStatement("INSERT INTO author_info (authorName,password,isManager,email) " +
            "VALUES (?,?,?,?);");
        if (con != null) {
            try {
                stmt.setString(1,name);
                stmt.setString(2,password);
                stmt.setBoolean(3,false);
                stmt.setString(4,email);
                stmt.executeUpdate();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    public void loginManager(){
        if (usernameText.getText() != null && passwordText.getText() != null){
            username = usernameText.getText();
            String password = passwordText.getText();
            try {
                stmt = con.prepareStatement("select * from author_info where authorName = ? "
                    + "and password = ? and isManager = ?");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            if (con != null) {
                try {
                    stmt.setString(1,username);
                    stmt.setString(2,password);
                    stmt.setBoolean(3,true);
                    ResultSet rs = stmt.executeQuery();
                    if(rs.next()){
                        System.out.println("success!");
                        isManager = true;
                        Stage stage = new Stage();
                        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("main.fxml"));
                        Scene scene = new Scene(fxmlLoader.load());
                        stage.setScene(scene);
                        stage.show();
//                        closeDB();
                    }else{
                        System.out.println("fail!");
                    }
                } catch (SQLException | IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            System.out.println(stmt.toString());
        }
    }

//    public void openDB() throws IOException {
//        try {
//            Class.forName("org.postgresql.Driver");
//        } catch (Exception e) {
//            System.err.println("Cannot find the Postgres driver. Check CLASSPATH.");
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