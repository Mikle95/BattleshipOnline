package Network;

import GUIelements.Controller;

import java.io.IOException;

public class OnlineController {
    boolean isServer;
    public String name;
    String adress;
    String port;
    Online connection;
    public boolean isMyStep = false;
    public boolean ready = false;
    public String partnerName;

    public OnlineController(boolean isServer){
        this.isServer = isServer;
    }

    public void setName(String name){
        this.name = name.replace(' ', '_');
    }

    public void setConnection(String adress, String port) throws IOException {
        connection = isServer ? new Server(Integer.parseInt(port), this) : new Client(adress, Integer.parseInt(port), this);
        connection.start();
        Controller.controller.printLog("Connected by adress: " + adress + ":" + port);
    }

    public Message sendShot(int x, int y){
        if(isMyStep)
            return connection.sendShot(x,y);
        else
            return null;
    }

    public void interrupt(){
        if(connection != null) {
            Message msg = new Message(name);
            msg.code = -99;
            try {
                connection.sendMessage(msg);
            }catch (Exception ex){}
            connection.cancel(true);
            connection.interrupt();
        }
    }
}
