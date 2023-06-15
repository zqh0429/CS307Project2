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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

public class ReplyWindow implements Initializable {
    private static Connection con = null;
    static {
        try {
            con = C3P0Util.getConnection();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private static PreparedStatement stmt = null;
    //sql语句编译上提高效率
    //一次性编译sql语句，每一次执行的时候直接编译
//    static Properties prop = loadDBUser();
    public ListView<reply> replyList;
    public TextField replyAuthor;
    public TextField replyStars;
    public ListView<reply> secondaryReplyList;
    public TextArea replyContent;
    public Label idLabel;
    public TextField replyid;

    int id = Main.id;
    String username = HelloController.username;
    String currentAuthor;
    int currentReplyID;
    boolean replyFlag = false;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        try {
//            openDB();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        replyList.setCellFactory(new replyCellFactory());
        try {
            stmt = con.prepareStatement("select * from reply where postid = ?");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (con != null) {
            try {
                stmt.setInt(1,id);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()){
                    int id = rs.getInt("id");
                    int replyID = rs.getInt("postID");
                    String content = rs.getString("replycontent");
                    int star = rs.getInt("replystars");
                    String author = rs.getString("replyauthor");
                    boolean anonymous = rs.getBoolean("anonymous");
                    reply reply = new reply(id,replyID,content,star,author,anonymous);
                    replyList.getItems().add(reply);
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }

        replyList.getSelectionModel().selectedItemProperty().addListener(
            (ObservableValue<? extends reply> observable,reply oldValue,reply newValue) ->{
                idLabel.setText("Post ID:");
                replyid.setText(String.valueOf(observable.getValue().getReplyID()));
                if (observable.getValue().anonymous){
                    replyAuthor.setText("anonymous");
                    currentAuthor = null;
                }else {
                    replyAuthor.setText(observable.getValue().getAuthor());
                    currentAuthor = observable.getValue().getAuthor();
                }
                replyStars.setText(String.valueOf(observable.getValue().getStar()));
                replyContent.setText(observable.getValue().content);
                secondaryReplyList.getItems().clear();
                secondaryReplyList.setCellFactory(new replyCellFactory());

                currentReplyID = observable.getValue().getId();
                replyFlag = true;
                System.out.println(currentAuthor);
                try {
                    stmt = con.prepareStatement("select * from second_reply where replyid = ?");
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                if (con != null) {
                    try {
                        stmt.setInt(1,observable.getValue().id);
                        ResultSet rs = stmt.executeQuery();
                        while (rs.next()){
                            int id = rs.getInt("id");
                            int replyID = rs.getInt("replyid");
                            String second_replyauthor = rs.getString("second_replyauthor");
                            int star = rs.getInt("second_replystars");
                            String second_replycontent = rs.getString("second_replycontent");
                            boolean anonymous = rs.getBoolean("anonymous");
                            reply reply = new reply(id,replyID,second_replycontent,star,second_replyauthor,anonymous);
                            secondaryReplyList.getItems().add(reply);
                        }
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
        secondaryReplyList.getSelectionModel().selectedItemProperty().addListener(
            (ObservableValue<? extends reply> observable,reply oldValue,reply newValue) ->{
                idLabel.setText("Reply ID:");
                replyid.setText(String.valueOf(observable.getValue().getReplyID()));
                if (observable.getValue().anonymous){
                    replyAuthor.setText("anonymous");
                    currentAuthor = null;
                }else {
                    replyAuthor.setText(observable.getValue().getAuthor());
                    currentAuthor = observable.getValue().getAuthor();
                }
                replyStars.setText(String.valueOf(observable.getValue().getStar()));
                replyContent.setText(observable.getValue().content);
                currentReplyID = observable.getValue().getId();
                System.out.println(currentAuthor);
                replyFlag = false;
            });

    }
    public void like(){
        if (replyFlag){
            try {
                stmt = con.prepareStatement("update reply set replystars = replystars +1 where id = ?;");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if (con != null) {
                try {
                    stmt.setInt(1,currentReplyID);
                    stmt.executeUpdate();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        else {
            try {
                stmt = con.prepareStatement("update second_reply set "
                    + "second_replystars = second_replystars +1 where id = ?");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if (con != null) {
                try {
                    System.out.println(stmt.toString());
                    stmt.setInt(1,currentReplyID);
                    stmt.executeUpdate();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }
    public void reply(){
        if (replyFlag){
            Stage stage = new Stage();
            TextArea textArea = new TextArea();
            Button button = new Button("Reply");
            VBox vBox = new VBox(textArea,button);
            stage.setScene(new Scene(vBox));
            stage.setWidth(250);
            stage.setHeight(250);
            stage.setTitle("Reply");
            stage.show();
            button.setOnAction(event -> {
                String content = textArea.getText();
                PreparedStatement statement;
                try {
                    statement = con.prepareStatement("select max(id) from second_reply;");
                    stmt = con.prepareStatement("insert into second_reply (id,replyid, second_replycontent,"
                        + "second_replystars,second_replyauthor) values (?,?,?,?,?)");
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
                        stmt.setInt(1,max+1);
                        stmt.setInt(2,currentReplyID);
                        stmt.setString(3,content);
                        stmt.setInt(4,0);
                        stmt.setString(5,username);
                        System.out.println(stmt.toString());
                        stmt.executeUpdate();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
        }

    }

    private class replyCellFactory implements Callback<ListView<reply>, ListCell<reply>> {

        @Override
        public ListCell<reply> call(ListView<reply> param) {
            return new ListCell<reply>() {

                @Override
                public void updateItem(reply reply, boolean empty) {
                    super.updateItem(reply, empty);
                    if (empty || Objects.isNull(reply)) {
                        setText(null);
                        setGraphic(null);
                        return;
                    }

                    HBox wrapper = new HBox();
                    Label replyIDLabel = new Label("" + reply.getReplyID());
                    Label titleLabel = new Label(reply.getContent());

                    titleLabel.setPrefSize(161, 20);
                    titleLabel.setWrapText(true);
                    titleLabel.setStyle("-fx-border-color: black; -fx-border-width: 1px;");

                    replyIDLabel.setPrefSize(20, 20);
                    replyIDLabel.setWrapText(true);
                    replyIDLabel.setStyle("-fx-border-color: black; -fx-border-width: 1px;");

                    wrapper.setAlignment(Pos.TOP_RIGHT);
                    wrapper.getChildren().addAll(replyIDLabel,titleLabel);
                    titleLabel.setPadding(new Insets(0, 20, 0, 0));

                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    setGraphic(wrapper);
                }
            };
        }

        @Override
        public String toString() {
            return replyList.getAccessibleText();
        }
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
class reply{
    int id;
    int replyID;//reply-postID;secondary_reply-relyID
    String content;
    int star;
    String author;
    boolean anonymous;

    public reply(int id,int replyID, String content, int star, String author,boolean anonymous) {
        this.id = id;
        this.replyID = replyID;
        this.content = content;
        this.star = star;
        this.author = author;
        this.anonymous = anonymous;
    }

    public int getId() {
        return id;
    }

    public int getReplyID() {
        return replyID;
    }

    public String getContent() {
        return content;
    }

    public int getStar() {
        return star;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isAnonymous() {
        return anonymous;
    }
}