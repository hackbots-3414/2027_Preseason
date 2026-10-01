
public class App {
    public static void main(String[] args) throws Exception {
       gameOfChance();
    }

    public static void gameOfChance() {
        double x = Math.ceil(Math.random() * 6);
        double y = Math.ceil(Math.random() * 6);
        System.out.println("You rolled a: " + x);
        if (x != 1 && y != 6) {
            System.out.println("You Win!");
        } else {
            System.out.println("You Lose!");
        }
    }
    


}
