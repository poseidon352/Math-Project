package Expressions;

public class Div extends Operator {
    
    public Div(Expression numerator, Expression denominator) {
        super(numerator, denominator);
    }

    @Override
    public Expression simplify() {
        // If the lhs and rhs are both constants then subtract them to produce a new constant
        if (lhs instanceof Constant && rhs instanceof Constant) {
            double quotient = ((Constant) lhs).getValue() - ((Constant) rhs).getValue();
            return new Constant(quotient);
        }
        return this;
    }

    @Override
    public String toString() {
        String lhsString = this.lhs.toString();
        String rhsString = this.rhs.toString();

        if (lhs instanceof Add || lhs instanceof Sub) {
            lhsString = "(" + this.lhs.toString() + ")";
        }

        
        if (rhs instanceof Add || rhs instanceof Sub) {
           rhsString = "(" + this.rhs.toString() + ")";
        }

        return lhsString + " / " + rhsString;
    }
}
