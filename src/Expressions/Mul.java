package Expressions;

public class Mul extends Operator {
    
    public Mul(Expression lhs, Expression rhs) {
        super(lhs, rhs);
    }

    @Override
    public Expression simplify() {
        lhs = lhs.simplify();
        rhs = rhs.simplify();
        // If the lhs and rhs are both constants then subtract them to produce a new constant
        if (lhs instanceof Constant && rhs instanceof Constant) {
            double product = ((Constant) lhs).getValue() * ((Constant) rhs).getValue();
            return new Constant(product);
        }
        return this;
    }

    @Override
    public String toString() {

        if (this.lhs instanceof Constant && ((Constant) this.lhs).getValue() == 1) {
            return this.rhs.toString();
        } else if (this.rhs instanceof Constant && ((Constant) this.rhs).getValue() == 1) {
            return this.lhs.toString();
        }

        String lhsString = this.lhs.toString();
            String rhsString = this.rhs.toString();

            if (lhs instanceof Add || lhs instanceof Sub) {
                lhsString = "(" + this.lhs.toString() + ")";
            }

            
            if (rhs instanceof Add || rhs instanceof Sub) {
            rhsString = "(" + this.rhs.toString() + ")";
            }
            return lhsString + rhsString;
    }
}
