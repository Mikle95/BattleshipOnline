package battleShip;

public class Ship implements Cloneable{
    protected int bowRow;
    protected int bowColumn;
    protected int length;
    protected boolean horizontal;
    protected boolean[] hit = new boolean[4];

    public int getLength(){
        return length;
    }

    public int getBowRow(){
        return  bowRow;
    }

    public int getBowColumn(){
        return bowColumn;
    }

    public boolean isHorizontal(){
        return horizontal;
    }

    void setBowRow(int row){
        bowRow = row;
    }

    void setBowColumn(int column){
        bowColumn = column;
    }

    void setHorizontal(boolean horizontal){
        this.horizontal = horizontal;
    }

    public String getShipType(){
        return "no Ship Type";
    }

    boolean okToPlaceShipAt(int row, int column, boolean horizontal, Ocean ocean){
        boolean check = true;

        if(row > 9 || row < 0 || column > 9 || column < 0) return false;
        if(horizontal && column + length > 10 || !horizontal && row + length > 10) return false;

        for (int i = -1; i < length + 1; i++)
            for (int j = -1; j < 2; j++)
                if(horizontal)
                    check = check && !ocean.isOccupied(row + j, column + i);
                else
                    check = check && !ocean.isOccupied(row + i, column + j);

        return check;
    }

    void placeShipAt(int row, int column, boolean horizontal, Ocean ocean) {
        if(!okToPlaceShipAt(row, column, horizontal, ocean)) return;

        Ship ship = (Ship)this.clone();
        ship.setBowRow(row);
        ship.setBowColumn(column);
        ship.setHorizontal(horizontal);

        Ship[][] ships = ocean.getShipArray();

        for (int i = 0; i < length; i++) {
            if (horizontal)
                ships[row][column + i] = ship;
            else
                ships[row + i][column] = ship;
        }

    }

    public String toString() {
        if(isSunk()) return "X";
        else return "S";
    }

    public boolean isSunk(){
        return hit[0] && hit[1] && hit[2] && hit[3];
    }

    boolean shootAt(int row, int column){
        int part = -1;

        if(row == bowRow && horizontal)
            part = column - bowColumn;
        else if (column == bowColumn && !horizontal)
            part = row - bowRow;
        else return false;

        if(part >= length || part < 0 || hit[part])
            return false;
        else
            return hit[part] = true;
    }

    @Override
    protected Object clone() {
        return new Ship();
    }

    boolean isHited(int row, int column){
        int part = -1;

        if(row == bowRow && horizontal)
            part = column - bowColumn;
        else if (column == bowColumn && !horizontal)
            part = row - bowRow;
        else return false;

        if(part >= length || part < 0)
            return false;
        else
            return hit[part];
    }
}
