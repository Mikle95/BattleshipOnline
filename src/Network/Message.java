package Network;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Message implements Serializable {
    public String name;
    public String message = "";
    public int x, y;
    public boolean isGameOver = false;
    public boolean isServerWinner;
    public boolean ready = false;
    public boolean miss = false;
    public int code = 0;
    public String statistics = "";
    public List<Integer> sunk = new ArrayList<>();
    public Message(String name){
        this.name = name;
    }
}
