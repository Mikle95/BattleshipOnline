package battleShip;

public class Submarine extends Ship {
    public Submarine(){
        length = 1;
        hit[0] = false;
        hit[1] = hit[2] = hit[3] = true;
    }

    @Override
    public String getShipType() {
        return "Submarine";
    }

    @Override
    protected Object clone(){
        Ship a = new Submarine();
        a.setBowColumn(getBowColumn());
        a.setBowRow(getBowRow());
        a.setHorizontal(isHorizontal());
        return a;
    }
}
