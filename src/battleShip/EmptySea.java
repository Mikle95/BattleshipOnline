package battleShip;

public class EmptySea extends Ship {

    public EmptySea(){
        length = 1;
        hit[0] = false;
    }

    @Override
    boolean shootAt(int row, int column){
        hit[0] = true;
        return false;
    }

    @Override
    public boolean isSunk(){
        return false;
    }

    @Override
    public String toString() {
        //if(hit[0])
        return "-";
        //else return ".";
    }
}
