package Expressions;

public abstract class Operator implements Expression {
    protected Expression lhs;
    protected Expression rhs;

    public Operator(Expression lhs, Expression rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public Expression getLHS() {
        return this.lhs;
    }

    public Expression getRHS() {
        return this.rhs;
    }
}