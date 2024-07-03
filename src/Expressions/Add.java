package Expressions;

import java.util.Arrays;
import java.util.List;

public class Add implements Expression{
    List<Expression> operands;
    
    public Add(Expression... ops) {
        operands = Arrays.asList(ops);
    }
}
