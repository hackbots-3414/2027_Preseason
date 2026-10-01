
public class App {
    
    public static void main(String[] args) throws Exception {
        gameOfChance();
    }

    public static void gameOfChance() {
    double x = Math.random();
    if (x < 0.16666) {
        System.out.println("You win!");
    } else {
        System.out.println("You lose!");
    }
    }
}
