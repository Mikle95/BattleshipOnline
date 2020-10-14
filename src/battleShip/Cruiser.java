package battleShip;

public class Cruiser extends Ship {

    public Cruiser(){
        length = 3;
        hit[0] = hit[1] = hit[2] = false;
        hit[3] = true;
    }

    @Override
    public String getShipType() {
        return "cruiser";
    }

    @Override
    protected Object clone(){
        Ship a = new Cruiser();
        a.setBowColumn(getBowColumn());
        a.setBowRow(getBowRow());
        a.setHorizontal(isHorizontal());
        return a;
    }
}
