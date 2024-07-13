package Expressions;

public class Equal extends Operator {

    public Equal(Expression lhs, Expression rhs) {
        super(lhs, rhs);
    }

    @Override
    public Expression simplify() {
        lhs = lhs.simplify();
        rhs = rhs.simplify();
        return this;
    }

    @Override
    public String toString() {
        return lhs.toString() + " = " + rhs.toString();
    }    
    
}
