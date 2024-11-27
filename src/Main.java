import Exceptions.InputTypoException;
import Exceptions.UnknownSymbolException;
import Expressions.ExpressionParser;

public class Main {
    public static void main(String[] args) throws UnknownSymbolException, InputTypoException
    {   
        long start = System.currentTimeMillis();

        new ExpressionParser("(x+1)*(x+1)=0");
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "ms");

        //TODO: upper and lower bounds are not being calculated properly
    }
}
