import java.math.BigDecimal;

import Exceptions.InputTypoException;
import Exceptions.UnknownSymbolException;
import Expressions.ExpressionParser;

public class Main {
    public static void main(String[] args) throws UnknownSymbolException, InputTypoException
    {
        long start = System.currentTimeMillis();

        new ExpressionParser("(x-1)*(x-2)*(x-3)*(x-4)*(x-5)*(x-6)*(x-7)*(x-8)*(x-9)*(x-10)*(x-11)*(x-12)=0");
        // new ExpressionParser("2*3*4*5*6*7*8*9*10*11*12*13");

        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "ms");
    }
}
