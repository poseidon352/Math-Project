package Expressions;

public class Mul extends BasicOperation {
    
    public Mul(Expression lhs, Expression rhs) {
        super(lhs, rhs);
    }

    public void neg(Expression expression) {
        expression = new Mul(new Constant(-1) , expression);
    }

    @Override
    public Expression simplify() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'simplify'");
    }
}
