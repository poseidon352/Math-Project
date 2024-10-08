package Expressions.Operators;

import Expressions.*;

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

    public Expression solve() {
        Equal simplified = (Equal) simplify();
        this.lhs = simplified.getLHS();
        this.rhs = simplified.getRHS();

        /*if (lhs.isVariable() && rhs instanceof Constant) {
            return this;
        } else*/ if (((AbstractFunction) lhs).isPolynomial() && ((AbstractFunction) lhs).isPolynomial()) {
            return polynomialBothSides();
        } /*else if (lhs.hasVariable() && rhs.hasVariable()) {
            return variableBothSides();
        } else if (lhs.hasVariable()) {
            return ((Equal) variableOneSide(lhs, rhs)).solve();
        } else if (rhs.hasVariable()) {
            return ((Equal) variableOneSide(rhs, lhs)).solve();
        }/* */
        //throw an Exception if this line is read
        return this;
    }

    private Expression polynomialBothSides() {
        Polynomial polynomial = new Polynomial((AbstractFunction) (new Sub(this.lhs, this.rhs)).simplify());
        AberthMethod.aberthMethod(polynomial);
        return this;
    }

    private Expression variableBothSides() {
        return this;
    }

    private Expression variableOneSide(Expression varExpr, Expression constExpr) {
        if (varExpr instanceof Add) {
            return oneSideAdd((Add) varExpr, constExpr);
        } else if (varExpr instanceof Mul) {
            return oneSideMul((Mul) varExpr, constExpr);
        } else if (varExpr instanceof Pow) {
            return oneSidePow((Pow) varExpr, constExpr);
        }
        return this;
    }

    private Expression oneSideAdd(Add varExprAdd, Expression constExpr) {
        if (varExprAdd.getLHS().hasVariable()) {
            return new Equal(varExprAdd.getLHS(), new Sub(constExpr, varExprAdd.getRHS()).simplify());
        } else if (varExprAdd.getRHS().hasVariable()) {
            return new Equal(varExprAdd.getRHS(), new Sub(constExpr, varExprAdd.getLHS()).simplify());
        }
        return this;
    }

    private Expression oneSideMul(Mul varExprMul, Expression constExpr) {
        if (varExprMul.getLHS().hasVariable()) {
            return new Equal(varExprMul.getLHS(), new Div(constExpr, varExprMul.getRHS()).simplify());
        } else if (varExprMul.getRHS().hasVariable()) {
            return new Equal(varExprMul.getRHS(), new Div(constExpr, varExprMul.getLHS()).simplify());
        }
        return this;
    }

    //TODO: Fix this method
    private Expression oneSidePow(Pow varExprPow, Expression constExpr) {
        if (varExprPow.getLHS().hasVariable()) {
            return new Equal(varExprPow.getLHS(), new Pow(constExpr, new Pow(varExprPow.getRHS(), -1)).simplify());
        }
        return this;
    }

    @Override
    public boolean containsSameExpression(Expression expression) {
        return (this.lhs.containsSameExpression(expression) || this.rhs.containsSameExpression(expression));
    }

    @Override
    public String toString() {
        return lhs.toString() + " = " + rhs.toString();
    }    
    
}
