import java.util.StringTokenizer;

public class Tokenizer extends StringTokenizer{

    public Tokenizer(String str) {
        super(str, "+-*/^()", true);
    }
    
    public static void main(String[] args) {
        Tokenizer st1 = new Tokenizer("1+2-3*4/5");
 
        // Condition holds true till there is single token
        // remaining using hasMoreTokens() method
        while (st1.hasMoreTokens())
            // Getting next tokens
            System.out.println(st1.nextToken());
    }
}
