package Expressions;

public abstract class BasicOperation implements Expression {
    protected Expression lhs;
    protected Expression rhs;

    public BasicOperation(Expression lhs, Expression rhs) {
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