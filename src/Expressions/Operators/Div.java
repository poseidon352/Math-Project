package Expressions.Operators;

import Expressions.Expression;

public class Div extends Mul{

    public Div(Expression lhs, Expression rhs) {
        super(lhs, new Pow(rhs, -1));
    }

    public Div(Expression lhs, double rhs) {
        super(lhs, new Pow(rhs, -1));
    }

    public Div(double lhs, Expression rhs) {
        super(lhs, new Pow(rhs, -1));
    }

    public Div(double lhs, double rhs) {
        super(lhs, new Pow(rhs, -1));
    }
}
