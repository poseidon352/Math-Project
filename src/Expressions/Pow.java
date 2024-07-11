package Expressions;

public class Pow extends Operator{

    public Pow(Expression base, Expression exponent) {
        super(base, exponent);
    }

    @Override
    public Expression simplify() {
        // If the lhs and rhs are both constants then subtract them to produce a new constant
        if (lhs instanceof Constant && rhs instanceof Constant) {
            double result = ((Constant) lhs).getValue() - ((Constant) rhs).getValue();
            return new Constant(result);
        }
        return this;
    }

    @Override
    public String toString() {
        String lhsString = this.lhs.toString();
        String rhsString = this.rhs.toString();

        if (!(lhs instanceof Constant || lhs instanceof Variable)) {
            lhsString = "(" + this.lhs.toString() + ")";
        }

        
        if (!(rhs instanceof Constant || rhs instanceof Variable)) {
            rhsString = "(" + this.rhs.toString() + ")";
        }

        return lhsString + " ^ " + rhsString;
    }
}
