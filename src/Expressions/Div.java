package Expressions;

public class Div extends Operator {
    
    public Div(Expression numerator, Expression denominator) {
        super(numerator, denominator);
    }

    @Override
    public Expression simplify() {
        lhs = lhs.simplify();
        rhs = rhs.simplify();
        // If the lhs and rhs are both constants then subtract them to produce a new constant
        if (lhs instanceof Constant && rhs instanceof Constant) {
            double quotient = ((Constant) lhs).getValue() / ((Constant) rhs).getValue();
            return new Constant(quotient);
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
        double denominatorValue = ((Constant) rhs).getValue();
        if (denominatorValue == 0) {
            //TODO: throw divideByZeroException
        } else if (denominatorValue == 1) {
            return lhs;
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
