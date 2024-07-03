package Expressions;

import java.util.Arrays;
import java.util.List;

public class Mul implements Expression{
    List<Expression> operands;

    public Mul(Expression... ops) {
        operands = Arrays.asList(ops);
    }

    public void neg(Expression argument) {
        argument = new Mul(new Constant(-1) , argument);
    }
}
