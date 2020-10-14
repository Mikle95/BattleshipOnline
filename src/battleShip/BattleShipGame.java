package battleShip;
import java.util.Scanner;


public class BattleShipGame {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        do {
            Ocean ocean = new Ocean();
            ocean.placeAllShipsRandomly();
            ocean.print();

            do {
                makeShot(scan, ocean);
                ocean.print();
            } while (!ocean.isGameOver());

            statistic(ocean);
            System.out.println("Enter \"restart\" to play again");
        } while (scan.next().toLowerCase().equals("restart"));
    }

    static void makeShot(Scanner scan, Ocean ocean){
        int x = -1, y = -1;
        do{
            try {
                x = scan.nextInt();
                y = scan.nextInt();

                if (x < 0 || x > 9 || y < 0 || y > 9)
                    System.out.println("Out of field");
            }
            catch (Exception ex) {
             System.out.println("Not a number");
             scan.nextLine();
            }
        }while (x < 0 || x > 9 || y < 0 || y > 9);
        ocean.shootAt(x,y);
    }

    static String statistic(Ocean ocean){
        return "Statistic:\n" + "Shots Fired: " + ocean.getShotsFired() + "\n" +
                                "Hit Count: " + ocean.getHitCount() + "\n" +
                                    "Ships Sunk: " + ocean.getShipsSunk();
    }

    static void ShootAll(){
        Ocean oc = new Ocean();
        oc.placeAllShipsRandomly();

        for (int i = -2; i<12; i++)
            for (int j =-5; j<13; j++)
                oc.shootAt(i,j);

        oc.print();
        statistic(oc);
    }
}
