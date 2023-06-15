package com.example.cs307project2;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.stream.Collectors;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javax.mail.MessagingException;

public class Main implements Initializable {

    public ListView<Post> postList;
    public Label postIDL;
    public Label postID;
    public Label titleL;
    public TextArea title;
    public Label categoryL;
    public TextArea category;
    public Label authorL;
    public TextField author;
    public Label postingTime;
    public Label postingTimeL;
    public Label postingCityL;
    public Label postingCity;
    public Label contentL;
    public TextArea content;
    private static Connection con = null;
    //    static {
//        try {
//            con = C3P0Util.getConnection();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
    private static PreparedStatement stmt = null;
    //sql语句编译上提高效率
    //一次性编译sql语句，每一次执行的时候直接编译
//    static Properties prop = loadDBUser();
    public static int id = 0;
    public TextField search;
    public ChoiceBox<String> choiceBox;
    public Button deleteButton;
    public ImageView imageView;
    public Label hitLabel;
    public Label hitContentLabel;
    String currentAuthor;
    String username = HelloController.username;
    String searchType;
    boolean isManager = HelloController.isManager;
    static int identify;
    static String email = HelloController.email;
    public static List<String> authorBlocked = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        hitLabel.setVisible(false);
        hitContentLabel.setVisible(false);
        if (isManager) {
            try {
                con = C3P0UtilManager.getConnection();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                con = C3P0Util.getConnection();
//                deleteButton.setVisible(false);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        try {
            blockList();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        postList.setCellFactory(new PostCellFactory());
        PreparedStatement s1, s2;
        try {
            stmt = con.prepareStatement("select * from post_info_view");
            s1 = con.prepareStatement("select * from category where postID = ?");
//            s2 = con.prepareStatement("select * from authorpost where postID = ?");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (con != null) {
            try {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    int postID = rs.getInt("id");
                    String title = rs.getString("title");
                    String content = rs.getString("content");
                    String postingTime = rs.getString("postingtime");
                    String city = rs.getString("city");
                    String country = rs.getString("country");
                    String author = rs.getString("authorname");
                    boolean anonymous = rs.getBoolean("anonymous");
                    s1.setInt(1, postID);
                    ResultSet ca = s1.executeQuery();
                    List<String> category = new ArrayList<>();
                    while (ca.next()) {
                        category.add(ca.getString("category"));
                    }
                    InputStream in = rs.getBinaryStream("image");
                    Post post = new Post(postID, title, category, content, postingTime,
                        city + "," + country, author, anonymous,in);
                    if (!authorBlocked.contains(author)){
                        postList.getItems().add(post);
                    }
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
        postList.getSelectionModel().selectedItemProperty().addListener(
            (ObservableValue<? extends Post> observable, Post oldValue, Post newValue) -> {
                id = observable.getValue().getPostID();
                postID.setText(String.valueOf(observable.getValue().getPostID()));
                title.setText(observable.getValue().getTitle());
                category.setText(observable.getValue().getCategory().toString());
                content.setText(observable.getValue().getContent());
                postingCity.setText(observable.getValue().getPostingCity());
                postingTime.setText(observable.getValue().getPostingTime());
                if (observable.getValue().isAnonymous()) {
                    author.setText("anonymous");
                    currentAuthor = null;
                } else {
                    author.setText(observable.getValue().getAuthor());
                    currentAuthor = observable.getValue().getAuthor();
                }
                if (observable.getValue().getImage() != null){
                    System.out.println("A");
                    Image image = new Image(observable.getValue().getImage());
                    imageView.setImage(image);
                }
                else {
                    imageView.setImage(null);
                }
                hitContentLabel.setText(String.valueOf(observable.getValue().getHit()));
            });
    }
    public void blockList() throws SQLException {
        stmt = con.prepareStatement("select * from author_block where authorname = ?");
        if (con != null){
            stmt.setString(1,username);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()){
                authorBlocked.add(resultSet.getString("authorblock"));
            }
        }
    }

    public void viewReply() throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("reply.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }

    public void reply() throws IOException {
        Stage stage = new Stage();
        TextArea textArea = new TextArea();
        Button button = new Button("Reply");
        Button button2 = new Button("Reply Anonymously");
        VBox vBox = new VBox(textArea, button);
        stage.setScene(new Scene(vBox));
        stage.setWidth(250);
        stage.setHeight(250);
        stage.setTitle("Reply");
        stage.show();
        button.setOnAction(event -> {
            String content = textArea.getText();
            PreparedStatement statement;
            try {
                statement = con.prepareStatement("select max(id) from reply;");
                stmt = con.prepareStatement(
                    "insert into reply (id,postid, replycontent,replystars,replyauthor,anonymous) values (?,?,?,?,?,?)");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if (con != null) {
                try {
                    ResultSet resultSet = statement.executeQuery();
                    int max = 0;
                    if (resultSet.next()) {
                        max = resultSet.getInt("max");
                    }
                    stmt.setInt(1, max + 1);
                    stmt.setInt(2, id);
                    stmt.setString(3, content);
                    stmt.setInt(4, 0);
                    stmt.setString(5, username);
                    stmt.setBoolean(6, false);
                    stmt.executeUpdate();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        button2.setOnAction(event -> {
            String content = textArea.getText();
            PreparedStatement statement;
            try {
                statement = con.prepareStatement("select max(id) from reply;");
                stmt = con.prepareStatement(
                    "insert into reply (id,postid, replycontent,replystars,replyauthor,anonymous) values (?,?,?,?,?,?)");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if (con != null) {
                try {
                    ResultSet resultSet = statement.executeQuery();
                    int max = 0;
                    if (resultSet.next()) {
                        max = resultSet.getInt("max");
                    }
                    stmt.setInt(1, max + 1);
                    stmt.setInt(2, id);
                    stmt.setString(3, content);
                    stmt.setInt(4, 0);
                    stmt.setString(5, username);
                    stmt.setBoolean(6, true);
                    stmt.executeUpdate();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    public void like() {
        try {
            stmt = con.prepareStatement(
                "insert into authorliked (postid, authorliked) values (?,?)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (con != null) {
            try {
                stmt.setInt(1, id);
                stmt.setString(2, username);
                stmt.executeUpdate();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public void favorite() {
        try {
            stmt = con.prepareStatement(
                "insert into authorfavorite (postid, authorfavorite) values (?,?)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (con != null) {
            try {
                stmt.setInt(1, id);
                stmt.setString(2, username);
                stmt.executeUpdate();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public void share() {
        try {
            stmt = con.prepareStatement(
                "insert into authorshared (postid, authorshared) values (?,?)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (con != null) {
            try {
                stmt.setInt(1, id);
                stmt.setString(2, username);
                stmt.executeUpdate();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public void followAuthor() {
        if (currentAuthor != null) {
            try {
                stmt = con.prepareStatement(
                    "insert into authorfollowedby (authorname, authorfollowedby) values (?,?)");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if (con != null) {
                try {
                    stmt.setString(1, username);
                    stmt.setString(2, currentAuthor);
                    stmt.executeUpdate();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

    }

    public void cancelFollowing() {
        if (currentAuthor != null) {
            try {
                stmt = con.prepareStatement(
                    "delete from authorfollowedby where authorname = ? and authorfollowedby = ?;");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if (con != null) {
                try {
                    stmt.setString(1, username);
                    stmt.setString(2, currentAuthor);
                    stmt.executeUpdate();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

    }

    public void openUserCenter() throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("user.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }

    public void post() throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("post.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }

    public void refresh() throws SQLException {
        hitContentLabel.setVisible(false);
        hitLabel.setVisible(false);
        postList.getItems().clear();
        authorBlocked.clear();
        blockList();
        PreparedStatement s1, s2;
        try {
            stmt = con.prepareStatement("select * from post_info_view");
            s1 = con.prepareStatement("select * from category where postID = ?");
            s2 = con.prepareStatement("call update_view()");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (con != null) {
            try {
                s2.executeUpdate();
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    int postID = rs.getInt("id");
                    String title = rs.getString("title");
                    String content = rs.getString("content");
                    String postingTime = rs.getString("postingtime");
                    String city = rs.getString("city");
                    String country = rs.getString("country");
                    String author = rs.getString("authorname");
                    boolean anonymous = rs.getBoolean("anonymous");
                    s1.setInt(1, postID);
                    ResultSet ca = s1.executeQuery();
                    List<String> category = new ArrayList<>();
                    while (ca.next()) {
                        category.add(ca.getString("category"));
                    }
                    InputStream in = rs.getBinaryStream("image");
                    Post post = new Post(postID, title, category, content, postingTime,
                        city + "," + country, author, anonymous,in);
                    if (!authorBlocked.contains(author)){
                        postList.getItems().add(post);
                    }
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
        postList.getSelectionModel().selectedItemProperty().addListener(
            (ObservableValue<? extends Post> observable, Post oldValue, Post newValue) -> {
                id = observable.getValue().getPostID();
                postID.setText(String.valueOf(observable.getValue().getPostID()));
                title.setText(observable.getValue().getTitle());
                category.setText(observable.getValue().getCategory().toString());
                content.setText(observable.getValue().getContent());
                postingCity.setText(observable.getValue().getPostingCity());
                postingTime.setText(observable.getValue().getPostingTime());
                if (observable.getValue().isAnonymous()) {
                    author.setText("anonymous");
                    currentAuthor = null;
                } else {
                    author.setText(observable.getValue().getAuthor());
                    currentAuthor = observable.getValue().getAuthor();
                }

            });
    }

    public void delete() {
        PreparedStatement s1, s2;
        try {
            stmt = con.prepareStatement("call delete(?);");
//            s1 = con.prepareStatement("delete from authorpost where postid = ?");
//            s2 = con.prepareStatement("delete from category where postid = ?");
            s2 = con.prepareStatement("delete from post where id = ?");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (con != null) {
            try {
                stmt.setInt(1, id);
                stmt.executeUpdate();
//                s1.setInt(1, id);
                s2.setInt(1, id);
//                s1.executeUpdate();
                s2.executeUpdate();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public void searchCity() {
        searchType = "city";
    }

    public void searchContent() {
        searchType = "content";
    }

    public void searchTitle() {
        searchType = "title";
    }

    public void searchAuthor() {
        searchType = "author";
    }

    public void searchCountry() {
        searchType = "country";
    }

    public void searchCategory() {
        searchType = "category";
    }
    public void searchTime() {
        searchType = "postingtime";
    }
    private class PostCellFactory implements Callback<ListView<Post>, ListCell<Post>> {

        @Override
        public ListCell<Post> call(ListView<Post> param) {
            return new ListCell<Post>() {

                @Override
                public void updateItem(Post post, boolean empty) {
                    super.updateItem(post, empty);
                    if (empty || Objects.isNull(post)) {
                        setText(null);
                        setGraphic(null);
                        return;
                    }

                    HBox wrapper = new HBox();
                    Label postIDLabel = new Label("" + post.getPostID());
                    Label titleLabel = new Label(post.getTitle());

                    titleLabel.setPrefSize(161, 20);
                    titleLabel.setWrapText(true);
                    titleLabel.setStyle("-fx-border-color: black; -fx-border-width: 1px;");

                    postIDLabel.setPrefSize(20, 20);
                    postIDLabel.setWrapText(true);
                    postIDLabel.setStyle("-fx-border-color: black; -fx-border-width: 1px;");

                    wrapper.setAlignment(Pos.TOP_RIGHT);
                    wrapper.getChildren().addAll(postIDLabel, titleLabel);
                    titleLabel.setPadding(new Insets(0, 20, 0, 0));

                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    setGraphic(wrapper);
                }
            };
        }

        @Override
        public String toString() {
            return postList.getAccessibleText();
        }
    }

    public void search() throws SQLException {
        System.out.println(searchType);
        String s = search.getText();
        PreparedStatement s1, s2, s3;
        postList.getItems().clear();
        if (searchType.equals("author")) { //search for author
            stmt = con.prepareStatement("select * from authorpost where authorname like ?");
            s1 = con.prepareStatement("select * from post where id = ?;");
            s2 = con.prepareStatement("select * from category where postID = ?");
            if (con != null) {
                try {
                    stmt.setString(1, "%" + s + "%");
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        int postID = rs.getInt("postid");
                        String author = rs.getString("authorname");
                        s1.setInt(1, postID);
                        ResultSet rs2 = s1.executeQuery();
                        while (rs2.next()) {
                            String title = rs2.getString("title");
                            String content = rs2.getString("content");
                            String postingTime = rs2.getString("postingtime");
                            String city = rs2.getString("city");
                            String country = rs2.getString("country");
                            boolean anonymous = rs2.getBoolean("anonymous");
                            s2.setInt(1, postID);
                            ResultSet ca = s2.executeQuery();
                            List<String> category = new ArrayList<>();
                            while (ca.next()) {
                                category.add(ca.getString("category"));
                            }
                            InputStream in = rs2.getBinaryStream("image");
                            Post post = new Post(postID, title, category, content, postingTime,
                                city + "," + country, author, anonymous,in);
                            if (!authorBlocked.contains(author)){
                                postList.getItems().add(post);
                            }
                        }
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        } else if (searchType.equals("category")) {
            stmt = con.prepareStatement("select * from category where category like ?");
            s1 = con.prepareStatement("select * from post where id = ?;");
            s2 = con.prepareStatement("select * from authorpost where postID = ?");
            s3 = con.prepareStatement("select * from category where postID = ?");
            if (con != null) {
                try {
                    stmt.setString(1, "%" + s + "%");
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        int postID = rs.getInt("postid");
                        s1.setInt(1, postID);
                        ResultSet rs2 = s1.executeQuery();
                        while (rs2.next()) {
                            String title = rs2.getString("title");
                            String content = rs2.getString("content");
                            String postingTime = rs2.getString("postingtime");
                            String city = rs2.getString("city");
                            String country = rs2.getString("country");
                            boolean anonymous = rs2.getBoolean("anonymous");
                            InputStream in = rs2.getBinaryStream("image");
                            s2.setInt(1, postID);
                            ResultSet au = s2.executeQuery();
                            String author = null;
                            while (au.next()) {
                                author = au.getString("authorname");
                            }
                            s3.setInt(1, postID);
                            ResultSet ca = s3.executeQuery();
                            List<String> category = new ArrayList<>();
                            while (ca.next()) {
                                category.add(ca.getString("category"));
                            }
                            Post post = new Post(postID, title, category, content, postingTime,
                                city + "," + country, author, anonymous,in);
                            if (!authorBlocked.contains(author)){
                                postList.getItems().add(post);
                            }
                        }

                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        } else if (searchType.equals("postingtime")){
            stmt = con.prepareStatement("select * from post where (TO_CHAR(postingtime, 'yyyy-mm-dd hh24:mi:ss') LIKE ?)");
            s1 = con.prepareStatement("select * from category where postID = ?");
            s2 = con.prepareStatement("select * from authorpost where postID = ?");
            if (con != null){
                try {
                    stmt.setString(1, "%" + s + "%");
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        int postID = rs.getInt("id");
                        String title = rs.getString("title");
                        String content = rs.getString("content");
                        String postingTime = rs.getString("postingtime");
                        String city = rs.getString("city");
                        String country = rs.getString("country");
                        boolean anonymous = rs.getBoolean("anonymous");
                        InputStream in = rs.getBinaryStream("image");
                        s1.setInt(1, postID);
                        ResultSet ca = s1.executeQuery();
                        List<String> category = new ArrayList<>();
                        while (ca.next()) {
                            category.add(ca.getString("category"));
                        }
                        s2.setInt(1, postID);
                        ResultSet au = s2.executeQuery();
                        String author = null;
                        while (au.next()) {
                            author = au.getString("authorname");
                        }
                        Post post = new Post(postID, title, category, content, postingTime,
                            city + "," + country, author, anonymous,in);
                        if (!authorBlocked.contains(author)){
                            postList.getItems().add(post);
                        }
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        else {
            try {
                stmt = con.prepareStatement("select * from post where " + searchType + " like ?;");
                s1 = con.prepareStatement("select * from category where postID = ?");
                s2 = con.prepareStatement("select * from authorpost where postID = ?");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if (con != null) {
                try {
                    stmt.setString(1, "%" + s + "%");
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        int postID = rs.getInt("id");
                        String title = rs.getString("title");
                        String content = rs.getString("content");
                        String postingTime = rs.getString("postingtime");
                        String city = rs.getString("city");
                        String country = rs.getString("country");
                        boolean anonymous = rs.getBoolean("anonymous");
                        InputStream in = rs.getBinaryStream("image");
                        s1.setInt(1, postID);
                        ResultSet ca = s1.executeQuery();
                        List<String> category = new ArrayList<>();
                        while (ca.next()) {
                            category.add(ca.getString("category"));
                        }
                        s2.setInt(1, postID);
                        ResultSet au = s2.executeQuery();
                        String author = null;
                        while (au.next()) {
                            author = au.getString("authorname");
                        }
                        Post post = new Post(postID, title, category, content, postingTime,
                            city + "," + country, author, anonymous,in);
                        if (!authorBlocked.contains(author)){
                            postList.getItems().add(post);
                        }
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

        postList.getSelectionModel().selectedItemProperty().addListener(
            (ObservableValue<? extends Post> observable, Post oldValue, Post newValue) -> {
                id = observable.getValue().getPostID();
                postID.setText(String.valueOf(observable.getValue().getPostID()));
                title.setText(observable.getValue().getTitle());
                category.setText(observable.getValue().getCategory().toString());
                content.setText(observable.getValue().getContent());
                postingCity.setText(observable.getValue().getPostingCity());
                postingTime.setText(observable.getValue().getPostingTime());
                if (observable.getValue().isAnonymous()) {
                    author.setText("anonymous");
                    currentAuthor = null;
                } else {
                    author.setText(observable.getValue().getAuthor());
                    currentAuthor = observable.getValue().getAuthor();
                }
            });
        search.clear();
    }

    public void rankAll() throws SQLException {
        hitContentLabel.setVisible(true);
        hitLabel.setVisible(true);
        postList.getItems().clear();
        PreparedStatement s1,s2,s3;
        stmt = con.prepareStatement("select count(*) cnt,postid from post p join authorliked a on p.id = a.postid\n"
            + "group by postid\n"
            + "order by cnt desc");
        s1 = con.prepareStatement("select * from post where id = ?;");
        s2 = con.prepareStatement("select * from category where postID = ?");
        s3 = con.prepareStatement("select * from authorpost where postID = ?");
        if (con != null){
            ResultSet resultSet = stmt.executeQuery();
            int j = 0;
            while (resultSet.next() && j < 10){
                j++;
                int cnt = resultSet.getInt("cnt");
                System.out.println(cnt);
                int postID = resultSet.getInt("postid");
                s1.setInt(1,postID);
                ResultSet rs = s1.executeQuery();
                while (rs.next()){
                    String title = rs.getString("title");
                    String content = rs.getString("content");
                    String postingTime = rs.getString("postingtime");
                    String city = rs.getString("city");
                    String country = rs.getString("country");
                    boolean anonymous = rs.getBoolean("anonymous");
                    InputStream in = rs.getBinaryStream("image");
                    s2.setInt(1, postID);
                    ResultSet ca = s2.executeQuery();
                    List<String> category = new ArrayList<>();
                    while (ca.next()) {
                        category.add(ca.getString("category"));
                    }
                    s3.setInt(1, postID);
                    ResultSet au = s3.executeQuery();
                    String author = null;
                    while (au.next()) {
                        author = au.getString("authorname");
                    }
                    Post post = new Post(postID, title, category, content, postingTime,
                        city + "," + country, author, anonymous,in);
                    post.setHit(cnt);
                    postList.getItems().add(post);
                }
            }
        }
    }
    public void rankCategory() throws SQLException {
        hitContentLabel.setVisible(true);
        hitLabel.setVisible(true);
        postList.getItems().clear();
        PreparedStatement s1,s2,s3,s4;
        String str = search.getText();
        stmt = con.prepareStatement("select count(*) cnt,postid from post p join authorliked a on p.id = a.postid\n"
            + "where postid = ?\n"
            + "group by postid;");
        s1 = con.prepareStatement("select * from post where id = ?;");
        s2 = con.prepareStatement("select * from category where postID = ?");
        s3 = con.prepareStatement("select * from authorpost where postID = ?");
        s4 = con.prepareStatement("select * from category where category like ?");
        if (con != null){
            s4.setString(1,"%"+str+"%");
            ResultSet rs4 = s4.executeQuery();
            Map<Integer,Integer> map = new HashMap<>();
            while (rs4.next()){
                int postID = rs4.getInt("postid");
                stmt.setInt(1,postID);
                ResultSet resultSet = stmt.executeQuery();
                while (resultSet.next()){
                    int cnt = resultSet.getInt("cnt");
                    map.put(postID,cnt);
                }
            }
            List<Integer> list = new ArrayList<>();
            List<Integer> list2 = new ArrayList<>();
            map.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEach(entry -> {
                list.add(entry.getKey());
                list2.add(entry.getValue());
            });
            Collections.reverse(list);
            Collections.reverse(list2);
            int j = 0;
            while (j < list.size() && j < 10){
                j++;
                int cnt = list2.get(j);
                System.out.println(cnt);
                int postID = list.get(j);
                s1.setInt(1,postID);
                ResultSet rs = s1.executeQuery();
                while (rs.next()){
                    String title = rs.getString("title");
                    String content = rs.getString("content");
                    String postingTime = rs.getString("postingtime");
                    String city = rs.getString("city");
                    String country = rs.getString("country");
                    boolean anonymous = rs.getBoolean("anonymous");
                    InputStream in = rs.getBinaryStream("image");
                    s2.setInt(1, postID);
                    ResultSet ca = s2.executeQuery();
                    List<String> category = new ArrayList<>();
                    while (ca.next()) {
                        category.add(ca.getString("category"));
                    }
                    s3.setInt(1, postID);
                    ResultSet au = s3.executeQuery();
                    String author = null;
                    while (au.next()) {
                        author = au.getString("authorname");
                    }
                    Post post = new Post(postID, title, category, content, postingTime,
                        city + "," + country, author, anonymous,in);
                    post.setHit(cnt);
                    postList.getItems().add(post);
                }
            }
        }
    }

    public void changePassword(){
        Random random = new Random();
        identify = random.nextInt(1000,9999);
        System.out.println("A");
        Stage stage = new Stage();
        Label label4 = new Label("Email: ");
        Label label5 = new Label("Identify Code: ");
        Label label2 = new Label("New Password: ");
        TextField password_text = new TextField();
        Label email_text = new Label(email);
        TextField code = new TextField();
        Button buttonSend = new Button("Send");
        HBox hBox2 = new HBox(label2,password_text);
        HBox hBox4 = new HBox(label4,email_text,buttonSend);
        HBox hBox5 = new HBox(label5,code);
        buttonSend.setOnAction(event -> {
            try {
                email = email_text.getText();
                System.out.println(email);
                MailTest.send2();
            } catch (MessagingException | IOException e) {
                throw new RuntimeException(e);
            }
        });
        Button button = new Button("Change");
        VBox vBox = new VBox(hBox4,hBox5,hBox2,button);
        stage.setScene(new Scene(vBox));
        button.setOnAction(event -> {
            if (code.getText().equals(String.valueOf(identify))){
                try {
                    stmt = con.prepareStatement("update author_info set password = ? where authorname = ?");
                    stmt.setString(1,password_text.getText());
                    stmt.setString(2,username);
                    stmt.executeUpdate();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }else {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Alert");
                alert.setHeaderText("Wrong verification code");
                alert.showAndWait();
            }
        });
        stage.show();
    }

    public void block() throws SQLException {
        stmt = con.prepareStatement("insert into author_block (authorname, authorblock) "
            + "VALUES (?,?)");
        if (con != null){
            stmt.setString(1,username);
            stmt.setString(2,currentAuthor);
            stmt.executeUpdate();
        }
        authorBlocked.add(currentAuthor);
    }
}

class Post {

    private int postID;
    private String title;
    private List<String> category;
    private String content;
    private String postingTime;
    private String postingCity;
    private String Author;
    private boolean anonymous;
    private InputStream image;
    private int hit;

    public Post(int postID, String title, List<String> category, String content, String postingTime,
        String postingCity, String author, boolean anonymous, InputStream image) {
        this.postID = postID;
        this.title = title;
        this.category = category;
        this.content = content;
        this.postingTime = postingTime;
        this.postingCity = postingCity;
        Author = author;
        this.anonymous = anonymous;
        this.image = image;
    }

    public int getHit() {
        return hit;
    }

    public void setHit(int hit) {
        this.hit = hit;
    }

    public int getPostID() {
        return postID;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getCategory() {
        return category;
    }

    public String getContent() {
        return content;
    }

    public String getPostingTime() {
        return postingTime;
    }

    public String getPostingCity() {
        return postingCity;
    }

    public String getAuthor() {
        return Author;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public InputStream getImage() {
        return image;
    }
}