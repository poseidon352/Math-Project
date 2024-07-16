package Expressions;

public class Pow extends Operator{

    public Pow(Expression base, Expression exponent) {
        super(base, exponent);
    }

    @Override
    public Expression simplify() {
        lhs = lhs.simplify();
        rhs = rhs.simplify();
        // If the lhs and rhs are both constants then subtract them to produce a new constant
        if (lhs instanceof Constant && rhs instanceof Constant) {
            double result = Math.pow(((Constant) lhs).getValue(), ((Constant) rhs).getValue());
            return new Constant(result);
        } else if (lhs instanceof Constant) {
            return rhsOperator();
        } else if (rhs instanceof Constant) {
            return lhsOperator();
        }
        return this;
    }

    private Expression rhsOperator() {
        if (((Constant) lhs).getValue() == 0) {
            return new Constant(0);
        }
        return this;
    }

    private Expression lhsOperator() {
        if (((Constant) rhs).getValue() == 0) {
            return new Constant(1);
        } else if (lhs instanceof Pow) {
            return simplifyLhsPow().simplify();
        } else if (lhs instanceof Mul) {
            return simplifyLhsMul().simplify();
        }
        return this;
    }

    private Expression simplifyLhsPow() {
        Pow lhs = (Pow) this.lhs;
        return new Pow(lhs.getLHS(), new Mul(lhs.getRHS(), rhs));
    }

    private Expression simplifyLhsMul() {
        Mul lhs = (Mul) this.lhs;
        return new Mul(new Pow(lhs.getLHS(), rhs), new Pow(lhs.getRHS(), rhs));
    }

    @Override
    public String toString() {
        if (this.rhs instanceof Constant && ((Constant) this.rhs).getValue() == 1){
            return this.lhs.toString();
        }
        String lhsString = this.lhs.toString();
        String rhsString = this.rhs.toString();

        if (!(lhs instanceof Constant || lhs instanceof Variable)) {
            lhsString = "(" + this.lhs.toString() + ")";
        }

        
        if (!(rhs instanceof Constant || rhs instanceof Variable)) {
            rhsString = "(" + this.rhs.toString() + ")";
        }

        return lhsString + "^" + rhsString;
    }    
}
