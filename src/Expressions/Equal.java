package Expressions;

public class Equal extends Operator {

    public Equal(Expression lhs, Expression rhs) {
        super(lhs, rhs);
    }

    @Override
    public Expression simplify() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'simplify'");
    }

    @Override
    public String toString() {
        return lhs.toString() + " = " + rhs.toString();
    }
    
}
