package Expressions.Operators;

import Expressions.Constant;
import Expressions.Expression;

import java.math.BigDecimal;
import java.math.MathContext;

import Expressions.AberthMethod;
import Expressions.AbstractFunction;

public class Pow extends Operator implements AbstractFunction {

    public Pow(Expression base, Expression exponent) {
        super(base, exponent);
    }

    public Pow(Expression lhs, double rhs) {
        super(lhs, rhs);
    }

    public Pow(double lhs, Expression rhs) {
        super(lhs, rhs);
    }

    public Pow(double lhs, double rhs) {
        super(lhs, rhs);
    }

    @Override
    public Expression simplify() {
        lhs = lhs.simplify();
        rhs = rhs.simplify();
        // If the lhs and rhs are both constants then subtract them to produce a new constant
        if (lhs instanceof Constant && rhs instanceof Constant) {
            // double result = Math.pow(((Constant) lhs).getValue(), ((Constant) rhs).getValue());
            BigDecimal result = ((Constant) lhs).getBigDecimalValue().pow((int)((Constant) rhs).getValue(), AberthMethod.mathContext);
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
    public AbstractFunction derivative() {
        if (rhs instanceof Constant) {
            return new Mul(rhs, new Pow(lhs, ((Constant) rhs).getValue() - 1));
        }
        return this;
    }

    @Override
    public Expression image(double x) {
        return (new Pow(((AbstractFunction) lhs).image(x), ((AbstractFunction) rhs).image(x))).simplify();
    }

    @Override
    public boolean isPolynomial() {
        return (this.lhs.isVariable() && this.rhs instanceof Constant && ((Constant) this.rhs).getValue() >= 0);
    }

    @Override
    public boolean containsSameExpression(Expression expression) {
        return equals(expression);
    }

    @Override
    public String toString() {
        if (this.rhs instanceof Constant && ((Constant) this.rhs).getValue() == 1){
            return this.lhs.toString();
        }
        String lhsString = this.lhs.toString();
        String rhsString = this.rhs.toString();

        if (!lhs.isVariable() || (lhs instanceof Constant && ((Constant) lhs).getValue() < 0)) {
            lhsString = "(" + this.lhs.toString() + ")";
        }

        
        if (!lhs.isVariable() || (rhs instanceof Constant && ((Constant) rhs).getValue() < 0)) {
            rhsString = "(" + this.rhs.toString() + ")";
        }

        return lhsString + "^" + rhsString;
    }    
}
