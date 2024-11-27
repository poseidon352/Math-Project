package Expressions.Operators;

import Expressions.Constant;
import Expressions.Expression;

import java.math.BigDecimal;

import Expressions.AberthMethod;
import Expressions.AbstractFunction;

public class Add extends Operator implements AbstractFunction {
    
    public Add(Expression lhs, Expression rhs) {
        super(lhs, rhs);
    }

    public Add(Expression lhs, double rhs) {
        super(lhs, rhs);
    }

    public Add(double lhs, Expression rhs) {
        super(lhs, rhs);
    }

    public Add(double lhs, double rhs) {
        super(lhs, rhs);
    }

    @Override
    public Expression simplify() {
        // Simplify the lhs and rhs as much as possible
        lhs = this.lhs.simplify();
        rhs = this.rhs.simplify();
        // If the lhs and rhs are both constants then add them to produce a new constant
        if (this.lhs instanceof Constant && this.rhs instanceof Constant) {
            // double sum = ((Constant) this.lhs).getValue() + ((Constant) this.rhs).getValue();
            BigDecimal sum = ((Constant) this.lhs).getBigDecimalValue().add(((Constant) this.rhs).getBigDecimalValue(), AberthMethod.mathContext);
            return new Constant(sum);
        // If the lhs is a Constant and the rhs is an Operator
        } else if (this.lhs instanceof Constant && this.rhs instanceof Operator) {
            return rhsOperator();
        // If the lhs is an Operator and the rhs is a Constant
        } else if (this.lhs instanceof Operator && this.rhs instanceof Constant) {
            return lhsOperator();
        } else if (this.lhs instanceof Operator && this.rhs instanceof Operator) {
            return lhsOperatorRhsOperator();
        }
        return this;
    }

    // If the lhs is a Constant and the rhs is an Operator
    private Expression rhsOperator() {
        if (this.rhs instanceof Add) {
            return addExprSimplification((Add) rhs, lhs);
        // If the constant value is 0 then return rhs
        }
        return basicSimplification((Constant) lhs, rhs);
    }

    // If the lhs is an Operator and the rhs is a Constant
    private Expression lhsOperator() {
        if (this.lhs instanceof Add) {
            return addExprSimplification((Add) lhs, rhs);
        }
        return basicSimplification((Constant) rhs, lhs);
    }

    // If the constant value is 0 then return the other side of the Add
    private Expression basicSimplification(Constant constant, Expression expression) {
        BigDecimal value = constant.getBigDecimalValue();
        if (value.compareTo(BigDecimal.ZERO) == 0) {
            return expression;
        }
        return this;
    }

    private Expression lhsMulRhsMul(Mul firstExpr, Mul secondExpr) {
        BigDecimal multiplicationOfSecondMul = BigDecimal.ONE;
        Pow secondExprPow = null;

        if (secondExpr.getLHS() instanceof Constant) {
            multiplicationOfSecondMul = ((Constant) secondExpr.getLHS()).getBigDecimalValue();
            secondExprPow = (Pow) secondExpr.getRHS();
        } else if (secondExpr.getRHS() instanceof Constant) {
            multiplicationOfSecondMul = ((Constant) secondExpr.getRHS()).getBigDecimalValue();
            secondExprPow = (Pow) secondExpr.getLHS();
        }
        if (firstExpr.getRHS() instanceof Constant && firstExpr.getLHS() instanceof Pow) {
            if (secondExprPow.equals(firstExpr.getLHS())) {
                return new Mul(new Constant(((Constant) firstExpr.getRHS()).getBigDecimalValue().add(multiplicationOfSecondMul,
                                                                            AberthMethod.mathContext)), secondExprPow).simplify();
            }
        } else if (firstExpr.getRHS() instanceof Pow && firstExpr.getLHS() instanceof Constant) {
            if (secondExprPow.equals(firstExpr.getRHS())) {
                return new Mul(new Constant(((Constant) firstExpr.getLHS()).getBigDecimalValue().add(multiplicationOfSecondMul,
                                                                            AberthMethod.mathContext)), secondExprPow).simplify();
            }
        }
        return this;
    }


    // These methods will reorder the expression so that the above rules can be 
    // directly followed
    private Expression lhsOperatorRhsOperator() {
        if (this.lhs instanceof Add && this.rhs instanceof Add) {
            Add rhs = (Add) this.rhs;
            return new Add(new Add(this.lhs, rhs.getLHS()).simplify(), rhs.getRHS()).simplify();
        }else if (this.lhs instanceof Add) {
            return addExprSimplification((Add) lhs, rhs);
        } else if (this.rhs instanceof Add) {
            return addExprSimplification((Add) rhs, lhs);
        } else if (this.lhs instanceof Mul && this.rhs instanceof Mul) {
            return lhsMulRhsMul((Mul) lhs, (Mul) rhs);
        }
        return this;
    }

    private Expression addExprSimplification(Add addExpr, Expression secondExpr) {
        if (addExpr.getLHS().containsSameExpression(secondExpr)) {
            return new Add(new Add(addExpr.getLHS(), secondExpr).simplify(), addExpr.getRHS());
        } else if (addExpr.getRHS().containsSameExpression(secondExpr)) {
            return new Add(new Add(addExpr.getRHS(), secondExpr).simplify(), addExpr.getLHS());
        }
        return this;
    }

    @Override
    public AbstractFunction derivative() {
        return (AbstractFunction) (new Add((Expression) ((AbstractFunction) lhs).derivative(), 
                (Expression) ((AbstractFunction) rhs).derivative())).simplify();
    }

    @Override
    public Expression image(double x) {
        return (new Add(((AbstractFunction) lhs).image(x), ((AbstractFunction) rhs).image(x))).simplify();
    }

    @Override
    public boolean isPolynomial() {
        if (this.lhs instanceof AbstractFunction && this.rhs instanceof AbstractFunction) {
            return (((AbstractFunction) this.lhs).isPolynomial() && ((AbstractFunction) this.rhs).isPolynomial());
        }
        return false;
    }

    @Override
    public boolean containsSameExpression(Expression expression) {
        return (this.lhs.containsSameExpression(expression) || this.rhs.containsSameExpression(expression));
    }

    @Override
    public String toString() {
        if (this.rhs instanceof Constant && ((Constant) this.rhs).getValue() < 0) {
            Constant newRhs = new Constant(((Constant) this.rhs).getValue() * (-1));
            return lhs.toString() + " - " + newRhs.toString();
        } else if (this.lhs instanceof Constant && this.rhs instanceof Mul) {
            return (new Add(rhs, lhs)).toString();
        }
        return lhs.toString() + " + " + rhs.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((lhs == null) ? 0 : lhs.hashCode());
        result = prime * result + ((rhs == null) ? 0 : rhs.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Add other = (Add) obj;
        if (lhs == null || rhs == null) {
            return false;
        } else if ((!lhs.equals(other.lhs) || !rhs.equals(other.rhs)) &&
                    (!lhs.equals(other.rhs) || !rhs.equals(other.lhs))) {
            return false;
        }
        return true;
    }
}
