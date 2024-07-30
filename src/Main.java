import Exceptions.InputTypoException;
import Exceptions.UnknownSymbolException;
import Expressions.*;

public class Main {
    public static void main(String[] args) throws UnknownSymbolException, InputTypoException
    {   
        long start = System.currentTimeMillis();

        new ExpressionParser("(x+1)(x-1)");

        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "ms");
    }
}
