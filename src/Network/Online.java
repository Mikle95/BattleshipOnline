package Network;

import GUIelements.Controller;
import GUIelements.Main;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.*;
import java.net.Socket;

public abstract class Online extends Thread {
    OnlineController controller;
//    ObjectInputStream input;
//    ObjectOutputStream output;
    Socket clientSocket;
    String partnerName;

    public Online(OnlineController controller){
        this.controller = controller;
    }

    abstract public void meeting();
    abstract public void placement(boolean flag);

    public void game() {
        System.out.println(this.getClass() + " " + "METHOD GAME");
        var msg = new Message(controller.name);
        msg.code = 1;

        try {
            while (!Controller.controller.ocean.isGameOver() || !msg.isGameOver) {
                msg = recieveMessage();
                if(msg.code != 1) continue;

                if(controller.isMyStep ) {
                    Controller.controller.shotResult(msg);
                }
                else
                {
                    msg = Controller.controller.MakeShotMy(msg.x, msg.y);
                    sendMessage(msg);
                }
                if(msg.isGameOver) cancel(false);
            }
        }catch (Exception ex){
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Main.main.finish(true);
                }
            });
        }
    }

    public void cancel(boolean flag){
        try {
//            input.close();
//            output.close();
            clientSocket.close();
            interrupt();
        }catch (Exception ex){}
    }

    public Message sendShot(int x, int y) {
        Message msg = new Message(controller.name);
        msg.x = x;
        msg.y = y;
        msg.code = 1;
        try {
            sendMessage(msg);
            //msg = recieveMessage();
            return msg;
        }catch (IOException ex){}
        return null;
    }

    public void printLog(String message){
        Controller.controller.printLog(message);
    }

    public void sendMessage(Message msg) throws IOException{
        new ObjectOutputStream(clientSocket.getOutputStream()).writeObject(msg);
    }

    public Message recieveMessage() throws IOException, ClassNotFoundException{
        Message msg = (Message) new ObjectInputStream(clientSocket.getInputStream()).readObject();
        if(msg.code != -99) return msg;
        else
            throw new IOException();

//        return msg;
    }
}
