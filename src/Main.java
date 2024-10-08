import Exceptions.InputTypoException;
import Exceptions.UnknownSymbolException;
import Expressions.ExpressionParser;

public class Main {
    public static void main(String[] args) throws UnknownSymbolException, InputTypoException
    {   
        long start = System.currentTimeMillis();

        new ExpressionParser("(x-1)*(x-2)*(x-3)*(x-4)*(x-5)*(x-6)*(x-7)*(x-8)*(x-9)*(x-10)*(x-11)*(x-12)" +
                                "*(x-13)*(x-14)*(x-15)*(x-16)*(x-17)*(x-18)*(x-19)*(x-20)*(x-21)*(x-22)*(x-23)*(x-24)" +
                                    "*(x-25)*(x-26)*(x-27)*(x-28)*(x-29)*(x-30)*(x-31)*(x-32)*(x-33)*(x-34)*(x-35)*(x-36)" +
                                        "*(x-37)*(x-38)*(x-39)*(x-40)=0");
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "ms");
    }
}
