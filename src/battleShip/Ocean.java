package battleShip;

import java.util.Random;

public class Ocean {
    Ship[][] ships = new Ship[10][10];
    int shotsFired;
    int hitCount;
    int shipsSunk;
    public String logs = "";
    Ship[] types = {new BattleShip(), new Cruiser(), new Destroyer(), new Submarine()};

    public Ocean(){
        shotsFired = 0;
        hitCount = 0;
        shipsSunk = 0;

        for(int i = 0; i<10; i++)
            for (int j = 0; j<10; j++) {
                ships[i][j] = new EmptySea();
                ships[i][j].setBowRow(i);
                ships[i][j].setBowColumn(j);
            }
    }

    public boolean placeShip(int x, int y, boolean horizontal, int type){
        if(type > 3 || type < 0) return false;
        if(!types[type].okToPlaceShipAt(x, y, horizontal, this)) return false;

        types[type].placeShipAt(x, y, horizontal, this);

        return true;
    }

    public boolean deleteShip(int x, int y){
        if(!isOccupied(x, y)) return false;

        for (int i = ships[x][y].length - 1; i >= 0; --i){
            if(!ships[x][y].isHorizontal()) {
                ships[x + i][y] = new EmptySea();
                ships[x + i][y].setBowRow(x + i);
                ships[x + i][y].setBowColumn(y);
            }
            else {
                ships[x][y + i] = new EmptySea();
                ships[x][y + i].setBowRow(x);
                ships[x][y + i].setBowColumn(y + i);
            }
        }

        return true;
    }

    public boolean okToPlaceShip(int x, int y, boolean horizontal, int type){
        return types[type].okToPlaceShipAt(x, y, horizontal, this);
    }

    public void placeAllShipsRandomly() {
        Random rnd = new Random();
        int x,y;
        boolean horizontal;
        for (Ship ship: types)
            for(int i = 0; i <= 4 - ship.getLength(); i++){
                do {
                    x = rnd.nextInt(10);
                    y = rnd.nextInt(10);
                    horizontal = rnd.nextBoolean();
                }while (!ship.okToPlaceShipAt(x,y,horizontal, this));
                ship.placeShipAt(x,y,horizontal, this);
            }
    }

    public boolean isOccupied(int row, int column){
        if(row < 0 || row > 9 || column < 0 || column > 9) return false;
        return !ships[row][column].getClass().equals(EmptySea.class);
    }

    public int[] shootAt(int row, int column){
        shotsFired++;
        if(row < 0 || row > 9 || column < 0 || column > 9) return null;

        Ship ship = ships[row][column];

        if(ship.shootAt(row, column)) {
            hitCount++;
            logs = "hit";
            if(ship.isSunk()) {
                shipsSunk++;
                logs = "You just sank a " + ship.getShipType();
                int[] mas = new int[ship.getLength() * 2];


                for (int i = 0; i < ship.getLength(); ++i){
                    mas[i * 2] = ship.isHorizontal() ? ship.getBowRow() : ship.getBowRow() + i;
                    mas[i * 2 + 1] = ship.isHorizontal() ? ship.getBowColumn() + i: ship.getBowColumn();
                }
                return mas;
            }
            return null;
        }
        logs = "miss";
        return null;
    }

    public int getShotsFired(){
        return shotsFired;
    }

    public int getHitCount(){
        return hitCount;
    }

    public int getShipsSunk(){
        return shipsSunk;
    }

    public boolean isGameOver(){
        return getShipsSunk() >= 10;
    }

    public Ship[][] getShipArray(){
        return ships;
    }

    void print(){
        System.out.println("- | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9");
        for(int i = 0; i<10; i++){
            String s = "" + i;
            for(int j = 0; j < 10; j++){
                s+=" | ";
                if(ships[i][j].isHited(i,j))
                    s += ships[i][j].toString();
                else
                    s += ".";
            }
            System.out.println(s);
        }
    }


    public String statistic(){
        return "Statistic:\n" + "Shots Fired: " + getShotsFired() + "\n" +
                "Hit Count: " + getHitCount() + "\n" +
                "Ships Sunk: " + getShipsSunk();
    }
}
