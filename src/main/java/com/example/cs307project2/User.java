package com.example.cs307project2;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

public class User implements Initializable {
    private static Connection con = null;
    static {
        try {
            con = C3P0Util.getConnection();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private static PreparedStatement stmt = null;
//    static Properties prop = loadDBUser();
    public ListView<String> infoList;
    String username = HelloController.username;
    String currentAuthor;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        infoList.getSelectionModel().selectedItemProperty().addListener(
            (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                currentAuthor = observable.getValue();
        });
    }
    public void viewLike(){
        infoList.getItems().clear();
        PreparedStatement s1;
        try {
            stmt = con.prepareStatement("select * from authorliked where authorliked = ?");
            s1 = con.prepareStatement("select * from post where id = ?");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (con != null) {
            try {
                stmt.setString(1,username);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()){
                    int postID = rs.getInt("postid");
                    s1.setInt(1,postID);
                    ResultSet rs2 = s1.executeQuery();
                    if (rs2.next()){
                        String s = rs2.getString("title");
                        infoList.getItems().add("Post Id: "+postID + "\nTitle:"+s);
                    }
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    public void viewFavorite(){
        infoList.getItems().clear();
        PreparedStatement s1;
        try {
            stmt = con.prepareStatement("select * from authorfavorite where authorfavorite = ?");
            s1 = con.prepareStatement("select * from post where id = ?");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (con != null) {
            try {
                stmt.setString(1,username);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()){
                    int postID = rs.getInt("postID");
                    s1.setInt(1,postID);
                    ResultSet rs2 = s1.executeQuery();
                    if (rs2.next()){
                        String s = rs2.getString("title");
                        infoList.getItems().add("Post Id: "+postID + "\nTitle:"+s);
                    }
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    public void viewShared(){
        infoList.getItems().clear();
        PreparedStatement s1;
        try {
            stmt = con.prepareStatement("select * from authorshared where authorshared = ?");
            s1 = con.prepareStatement("select * from post where id = ?");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (con != null) {
            try {
                stmt.setString(1,username);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()){
                    int postID = rs.getInt("postID");
                    s1.setInt(1,postID);
                    ResultSet rs2 = s1.executeQuery();
                    if (rs2.next()){
                        String s = rs2.getString("title");
                        infoList.getItems().add("Post Id: "+postID + "\nTitle:"+s);
                    }
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    public void viewPost(){
        infoList.getItems().clear();
        PreparedStatement s1;
        try {
            stmt = con.prepareStatement("select * from authorpost where authorname = ?");
            s1 = con.prepareStatement("select * from post where id = ?");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (con != null) {
            try {
                stmt.setString(1,username);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()){
                    int postID = rs.getInt("postid");
                    s1.setInt(1,postID);
                    ResultSet rs2 = s1.executeQuery();
                    while (rs2.next()){
                        String s = rs2.getString("title");
                        infoList.getItems().add("Post Id: "+postID + "\nTitle:"+s);
                    }
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    public void viewFollow(){
        infoList.getItems().clear();
        try {
            stmt = con.prepareStatement("select * from authorfollowedby where authorname = ?");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (con != null) {
            try {
                stmt.setString(1,username);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()){
                    infoList.getItems().add(rs.getString("authorfollowedby"));
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    public void viewFollower(){
        infoList.getItems().clear();
        try {
            stmt = con.prepareStatement("select * from authorfollowedby where authorfollowedby = ?");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (con != null) {
            try {
                stmt.setString(1,username);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()){
                    infoList.getItems().add(rs.getString("authorname"));
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    public void viewReply(){
        infoList.getItems().clear();
        try {
            stmt = con.prepareStatement("select * from reply where replyauthor = ?");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (con != null) {
            try {
                stmt.setString(1,username);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()){
                    int postID = rs.getInt("postid");
                    String s = rs.getString("replycontent");
                    infoList.getItems().add("Post ID: "+postID + "\nContent: "+s);
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public void viewBlocked() throws SQLException {
        infoList.getItems().clear();
        stmt = con.prepareStatement("select * from author_block where authorname = ?");
        if (con != null){
            stmt.setString(1,username);
            ResultSet set = stmt.executeQuery();
            while (set.next()){
                infoList.getItems().add(set.getString("authorblock"));
            }
        }
    }
    public void blockCancel() throws SQLException {
        stmt = con.prepareStatement("delete from author_block where authorname = ? "
            + "and authorblock = ?");
        if (con != null){
            stmt.setString(1,username);
            stmt.setString(2,currentAuthor);
            stmt.executeUpdate();
        }
    }
}