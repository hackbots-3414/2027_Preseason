public class App {
    public static void main(String[] args) throws Exception {
        double x = Math.random();
        for (int y=0; y<5; y++) {
        System.out.println("You rolled a: "+x);
         if (x == 0 || x==1){
                System.out.println("Wow, you win!");
            } else {
             System.out.println("You lose, as expected.");
         }
        }   
    }
}