package Expressions.Operators;

import Expressions.Expression;

public class Sub extends Add{

    public Sub(Expression lhs, Expression rhs) {
        super(lhs, new Mul(rhs, -1));
    }

    public Sub(Expression lhs, double rhs) {
        super(lhs, new Mul(rhs, -1));
    }

    public Sub(double lhs, Expression rhs) {
        super(lhs, new Mul(rhs, -1));
    }

    public Sub(double lhs, double rhs) {
        super(lhs, new Mul(rhs, -1));
    }
}
