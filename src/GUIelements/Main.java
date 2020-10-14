package GUIelements;

import Network.OnlineController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Timer;

public class Main extends Application {

    public static Main main;
    static boolean isServer;

    Controller controller;

    public void finish(boolean flag){
        Alert al = new Alert(Alert.AlertType.WARNING);
        al.setContentText(controller.netController.name + "stopped game!Ok?");
        al.showAndWait();
        controller.netController.interrupt();
        Platform.exit();
        System.exit(0);
    }

    public void finish(){
        controller.netController.interrupt();
        Platform.exit();
        System.exit(0);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        main = this;

        Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
        controller = new Controller(primaryStage, root, new OnlineController(isServer));

        primaryStage.setTitle("BattleShip Game " + (isServer ? "Server" : "Client"));
        var a = Toolkit.getDefaultToolkit().getScreenSize();

        GridPane bf = (GridPane) root.lookup("#battleField");
        GridPane myBF = (GridPane) root.lookup("#battleField_MY");

        controller.fields = new ArrayList<>();
        Label lbl = new Label("\\");
        lbl.setMaxSize(a.width, a.height);
        lbl.setAlignment(Pos.CENTER);
        lbl.setFont(Font.font(20));
        bf.add(lbl, 0,0);
        lbl = new Label("\\");
        lbl.setMaxSize(a.width, a.height);
        lbl.setAlignment(Pos.CENTER);
        lbl.setFont(Font.font(20));
        myBF.add(lbl, 0,0);
//        bf.add(lbl, 0, 0);
        for (int i = 0; i < 10;++i){
            addLabels(bf, i);
            addLabels(myBF, i);
            controller.fields.add(new ArrayList<>());
            controller.Myfields.add(new ArrayList<>());
            for (int j = 0; j < 10; ++j){
                addButton(bf, i, j, true);
                addButton(myBF, i, j, false);
            }
        }


        primaryStage.addEventFilter(KeyEvent.KEY_RELEASED, event -> keyEvent(root, event));


        primaryStage.setScene(new Scene(root, 800, 500));
        primaryStage.setMinHeight(500);
        primaryStage.setMinWidth(800);
        primaryStage.setMaxHeight(a.height);
        primaryStage.setMaxWidth(a.width);

        Stage netStage = new Stage();
        Parent rootCon = FXMLLoader.load(getClass().getResource("connect.fxml"));
//        new Alert(Alert.AlertType.INFORMATION, "End").show();

        netStage.setTitle(isServer ? "Server" : "Client");
        netStage.setScene(new Scene(rootCon));
        if(isServer) {
            rootCon.lookup("#adress").setVisible(false);
            rootCon.lookup("#adresslbl").setVisible(false);
        }
        rootCon.lookup("#ok").setOnMouseClicked(event -> {
            String name = ((TextField) rootCon.lookup("#name")).getText();
            if (name.equals("")) return;
            String adress = ((TextField) rootCon.lookup("#adress")).getText();
            String port = ((TextField) rootCon.lookup("#port")).getText();
            try {
                controller.netController.setName(name);
                controller.netController.setConnection(adress, port);

                Button cancel = (Button)root.lookup("#connect");
                cancel.setText("Cancel");
                cancel.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//                        alert.setTitle("Title");
//                        alert.setHeaderText("Some Text");
                        alert.setContentText("Close game?.");

                        ButtonType buttonTypeOne = new ButtonType("Yes");
                        ButtonType buttonTypeCancel = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
                        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeCancel);
                        Optional<ButtonType> result = alert.showAndWait();
                        if(result.get() == buttonTypeCancel) return;

                        Main.main.finish();
                    }
                });

                netStage.close();
            } catch (Exception ex) {
                return;
            }
        });

        controller.GameMode();

        primaryStage.setOnCloseRequest(event -> finish());
        primaryStage.show();
        ((Button)root.lookup("#connect")).setOnAction(event -> {
            netStage.show();
            netStage.setMaxWidth(netStage.getWidth());
            netStage.setMaxHeight(netStage.getHeight());
            netStage.setMinWidth(netStage.getWidth());
            netStage.setMinHeight(netStage.getHeight());
        });
    }


    public static void main(String[] args) {
        isServer = args.length == 0 || args[0].equals("Server");
        launch(args);
    }

    public void addButton(GridPane bf, int x, int y, boolean flag){
        var a = Toolkit.getDefaultToolkit().getScreenSize();
        Button btn = new Button("");
        btn.setMaxSize(a.width, a.height);
        if(flag)
            controller.fields.get(x).add(btn);
        else
            controller.Myfields.get(x).add(btn);
        bf.add(btn, x + 1, y + 1);
        btn.setFocusTraversable(false);
    }

    void addLabels(GridPane bf, int i){
        var a = Toolkit.getDefaultToolkit().getScreenSize();
        var lbl = new Label(i + 1 + "");
        lbl.setMaxSize(a.width, a.height);
        lbl.setAlignment(Pos.CENTER);
        lbl.setFont(Font.font(20));
        bf.add(lbl, i + 1, 0);

        lbl = new Label(i + 1 + "");
        lbl.setMaxSize(a.width, a.height);
        lbl.setAlignment(Pos.CENTER);
        lbl.setFont(Font.font(20));
        bf.add(lbl, 0, i + 1);
    }

    void keyEvent(Parent root, KeyEvent event){
        TextField editX = (TextField) root.lookup("#editX");
        TextField editY = (TextField) root.lookup("#editY");

        if(!controller.restartMode && event.getCode() == KeyCode.ENTER){
            try{
                editX.setStyle("-fx-border-color: #008000");
                editY.setStyle("-fx-border-color: #008000");
                int x = Integer.parseInt(editX.getText());
                int y = Integer.parseInt(editY.getText());
                controller.fields.get(y - 1).get(x - 1).fire();
            } catch (Exception ex) {
                editX.setStyle("-fx-border-color: #8B0000");
                editY.setStyle("-fx-border-color: #8B0000");
            }
        }

        try {
            int x = Integer.parseInt(editX.getText());
            int y = Integer.parseInt(editY.getText());
            switch (event.getCode()) {
                case UP:
                case DOWN:
                case RIGHT:
                case LEFT: root.requestFocus();
            }
            switch (event.getCode()) {
                case UP: controller.fields.get(x - 2).get(y - 1); editX.setText(x - 1 + ""); break;
                case DOWN: controller.fields.get(x).get(y - 1); editX.setText(x + 1 + ""); break;
                case RIGHT: controller.fields.get(x - 1).get(y); editY.setText(y + 1 + ""); break;
                case LEFT: controller.fields.get(x - 1).get(y - 2); editY.setText(y - 1 + ""); break;
            }
        }catch (Exception ex){}
    }
}
