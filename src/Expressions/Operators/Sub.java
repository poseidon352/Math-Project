package Expressions.Operators;

import Expressions.Constant;
import Expressions.Expression;
import Expressions.Function;

public class Sub extends Operator implements Function {
    
    public Sub(Expression lhs, Expression rhs) {
        super(lhs, rhs);
    }

    @Override
    public Expression simplify() {
        lhs = lhs.simplify();
        rhs = rhs.simplify();
        // If the lhs and rhs are both constants then subtract them to produce a new constant
        if (lhs instanceof Constant && rhs instanceof Constant) {
            double difference = ((Constant) lhs).getValue() - ((Constant) rhs).getValue();
            return new Constant(difference);
        } else if (this.lhs instanceof Constant && this.rhs instanceof Operator) {
            return rhsOperator();
        // If the lhs is an Operator and the rhs is a Constant
        } else if (this.lhs instanceof Operator && this.rhs instanceof Constant) {
            return lhsOperator();
        } else if (this.lhs instanceof Operator && this.rhs instanceof Operator) {
            return lhsOperatorRhsOperator();
        }
        return doubleNegativeSimplification();
    }

    private Expression doubleNegativeSimplification() {
        if (rhs instanceof Constant) {
            Constant rhs = (Constant) this.rhs;
            if (rhs.getValue() < 0) {
                Double newRHSValue = rhs.getValue() * (-1);
                return new Add(lhs, new Constant(newRHSValue));
            }
        }
        return this;
    }

    private Expression rhsOperator() {
        if (this.rhs instanceof Add) {
            return simplifyAdd(rhs, lhs).simplify();
        } else if (this.rhs instanceof Sub) {
            // return rhsSub().simplify();
        } else if (this.lhs instanceof Constant && ((Constant) this.lhs).getValue() == 0) {
            return this.rhs;
        }
        return this;
    }

    private Expression simplifyAdd(Expression firstExpr, Expression secondExpr) {
        return this;
    }

    private Expression lhsOperator() {
        return this;
    }

    private Expression lhsOperatorRhsOperator() {
        return this;
    }

    @Override
    public Expression derivative() {
        throw new UnsupportedOperationException("Method not yet implemented");
    }

    @Override
    public Expression image(double x) {
        throw new UnsupportedOperationException("Method not yet implemented");
    }


    @Override
    public String toString() {
        String lhsString = this.lhs.toString();
        String rhsString = this.rhs.toString();

        if (rhs instanceof Add || rhs instanceof Sub) {
            rhsString = "(" + this.rhs.toString() + ")";
            }
        return lhsString + " - " + rhsString;
    }
}