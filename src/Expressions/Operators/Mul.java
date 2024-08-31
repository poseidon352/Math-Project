package Expressions.Operators;

import Expressions.Constant;
import Expressions.Expression;

import java.math.BigDecimal;
import java.math.MathContext;

import Expressions.AberthMethod;
import Expressions.AbstractFunction;

public class Mul extends Operator implements AbstractFunction {
    
    public Mul(Expression lhs, Expression rhs) {
        super(lhs, rhs);
    }

    public Mul(Expression lhs, double rhs) {
        super(lhs, rhs);
    }

    public Mul(double lhs, Expression rhs) {
        super(lhs, rhs);
    }

    public Mul(double lhs, double rhs) {
        super(lhs, rhs);
    }

    @Override
    public Expression simplify() {
        lhs = lhs.simplify();
        rhs = rhs.simplify();
        // If the lhs and rhs are both constants then subtract them to produce a new constant
        if (lhs instanceof Constant && rhs instanceof Constant) {
            // double product = ((Constant) lhs).getValue() * ((Constant) rhs).getValue();
            BigDecimal product = ((Constant) lhs).getBigDecimalValue().multiply(((Constant) rhs).getBigDecimalValue(), AberthMethod.mathContext);
            return new Constant(product);
        } else if (lhs instanceof Constant) {
            return oneSideConstant(rhs, (Constant) lhs);
        } else if (rhs instanceof Constant) {
            return oneSideConstant(lhs, (Constant) rhs);
        } else if (lhs instanceof Operator && rhs instanceof Operator) {
            return lhsOperatorRhsOperator();
        }
        return this;
    }

    private Expression oneSideConstant(Expression expression, Constant constant) {
        if (constant.getBigDecimalValue().compareTo(BigDecimal.ZERO) == 0) {
            return new Constant(0);
        } else if (expression instanceof Mul) {
            return simplifyMul((Mul) expression, constant);
        }
        return expand();
    }

    private Expression simplifyMul(Mul firstExpr, Expression secondExpr) {
        if (secondExpr.getClass().equals(firstExpr.getRHS().getClass())) {
            return new Mul(new Mul(secondExpr, firstExpr.getRHS()), firstExpr.getLHS()).simplify();
        } else if (secondExpr.getClass().equals(firstExpr.getLHS().getClass())) {
            return new Mul(new Mul(secondExpr, firstExpr.getLHS()), firstExpr.getRHS()).simplify();
        }
        return this;
    }

    private Expression lhsOperatorRhsOperator() {
        if (lhs instanceof Mul && rhs instanceof Mul) {
            return lhsMulRhsMul((Mul) lhs, (Mul) rhs).simplify();
        } else if (rhs instanceof Pow && rhs.baseEquals(lhs)) {
            return powLhsEqualRhs((Pow) rhs, lhs).simplify();
        } else if (lhs instanceof Pow && lhs.baseEquals(rhs)) {
            return powLhsEqualRhs((Pow) lhs, rhs).simplify();
        }
        return expand();
    }

    private Expression lhsMulRhsMul(Mul firstExpr, Mul secondExpr) {
        BigDecimal multiplicationOfSecondMul = BigDecimal.ONE;
        double powerOfSecondMul = 1;
        Pow secondExprPow = null;

        if (secondExpr.getLHS() instanceof Constant) {
            multiplicationOfSecondMul = ((Constant) secondExpr.getLHS()).getBigDecimalValue();
            secondExprPow = (Pow) secondExpr.getRHS();
            powerOfSecondMul = ((Constant) secondExprPow.getRHS()).getValue();
        } else if (secondExpr.getRHS() instanceof Constant) {
            multiplicationOfSecondMul = ((Constant) secondExpr.getRHS()).getBigDecimalValue();
            secondExprPow = (Pow) secondExpr.getLHS();
            powerOfSecondMul = ((Constant) secondExprPow.getRHS()).getValue();
        }
        if (firstExpr.getRHS() instanceof Constant && firstExpr.getLHS() instanceof Pow) {
            Pow firstMulExprLhs = (Pow) firstExpr.getLHS();
            if (secondExprPow.getLHS().equals(firstMulExprLhs.getLHS())) {
                return new Mul(new Constant(((Constant) firstExpr.getRHS()).getBigDecimalValue().multiply(multiplicationOfSecondMul, AberthMethod.mathContext)), 
                new Pow(secondExprPow.getLHS(), new Constant(((Constant) firstMulExprLhs.getRHS()).getValue() + powerOfSecondMul)));
            }
        } else if (firstExpr.getRHS() instanceof Pow && firstExpr.getLHS() instanceof Constant) {
            Pow firstMulExprRhs = (Pow) firstExpr.getRHS();
            if (secondExprPow.getLHS().equals(firstMulExprRhs.getLHS())) {
                return new Mul(new Constant(((Constant) firstExpr.getLHS()).getBigDecimalValue().multiply(multiplicationOfSecondMul, AberthMethod.mathContext)), 
                new Pow(secondExprPow.getLHS(), new Constant(((Constant) firstMulExprRhs.getRHS()).getValue() + powerOfSecondMul)));
            }
        }
        return this;
    }

    // If there is a Pow and both expressions are equals (excluding powers they are raised to)
    private Expression powLhsEqualRhs(Pow powExpr, Expression expression) {
        double powerOfFirstPow = ((Constant) powExpr.getRHS()).getValue();
        double powerOfSecondExpr = 1;
        double newRhsValue = powerOfFirstPow + powerOfSecondExpr;
        if (expression instanceof Pow) {
            Pow secondPow = (Pow) expression;
            powerOfSecondExpr = ((Constant) secondPow.getRHS()).getValue();
            newRhsValue = powerOfFirstPow + powerOfSecondExpr;
            return new Pow(powExpr.getLHS(), new Constant(newRhsValue));
        }
        return new Pow(powExpr.getLHS(), new Constant(newRhsValue));
    }

    public Expression expand() {
        if (this.lhs instanceof Mul) {
            this.lhs = ((Mul)this.lhs).expand().simplify();
        }

        if (this.rhs instanceof Mul) {
            this.rhs = ((Mul)this.rhs).expand().simplify();
        }

        if (this.lhs instanceof Add && this.rhs instanceof Add) {
            return foil().simplify();
        } else if (this.lhs instanceof Add && (rhs instanceof Constant || rhs.isVariable())) {
            return foilSingleTerm(rhs, (Add) lhs).simplify();
        } else if ((lhs instanceof Constant || lhs.isVariable()) && this.rhs instanceof Add) {
            return foilSingleTerm(lhs, (Add) rhs).simplify();
        }
        return this;
    }

// (x+1)(x+2)
    private Expression foil() {
        Add lhs = (Add) this.lhs;
        Add rhs = (Add) this.rhs;
        Expression f = new Mul(lhs.getLHS(), rhs.getLHS());
        Expression o = new Mul(lhs.getLHS(), rhs.getRHS());
        Expression i = new Mul(lhs.getRHS(), rhs.getLHS());
        Expression l = new Mul(lhs.getRHS(), rhs.getRHS());
        return new Add(new Add(f, o), new Add(i, l));
    }

    private Expression foilSingleTerm(Expression constOrVar, Add addExpr) {
        Mul newLhs = new Mul(constOrVar, addExpr.getLHS());
        Mul newRhs = new Mul(constOrVar, addExpr.getRHS());
        return new Add(newLhs, newRhs);
    }

    @Override
    public String toString() {

        if (this.lhs instanceof Constant){
            double lhsValue = ((Constant) this.lhs).getValue();
            if (lhsValue == 1) {
                return this.rhs.toString();
            } else if (lhsValue == -1) {
                return "-" + this.rhs.toString();
            }
        } else if (this.rhs instanceof Constant){
            double rhsValue = ((Constant) this.rhs).getValue();
            if (rhsValue == 1) {
                return this.lhs.toString();
            } else if (rhsValue == -1) {
                return "-" + this.lhs.toString();
            }
        }

        String lhsString = this.lhs.toString();
        String rhsString = this.rhs.toString();

        if (lhs instanceof Add) {
            lhsString = "(" + this.lhs.toString() + ")";
        }

        
        if (rhs instanceof Add) {
            rhsString = "(" + this.rhs.toString() + ")";
        }
        return lhsString + rhsString;
    }

    // Product rule
    @Override
    public AbstractFunction derivative() {
        Expression lhsDerivative = (Expression) ((AbstractFunction) lhs).derivative();
        Expression rhsDerivative = (Expression) ((AbstractFunction) rhs).derivative();
        return (AbstractFunction) (new Add((Expression) new Mul(lhs, rhsDerivative), new Mul(lhsDerivative, rhs))).simplify();
    }

    @Override
    public Expression image(double x) {
        return (new Mul(((AbstractFunction) lhs).image(x), ((AbstractFunction) rhs).image(x))).simplify();
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
        if (expression instanceof Mul) {
            return variableHasSamePower(this, (Mul) expression);
        }
        return false;
    }


    private boolean variableHasSamePower(Mul firstExpr, Mul secondExpr) {
        Pow firstExprPow = getVariablePow(firstExpr);
        Pow secondExprPow = getVariablePow(secondExpr);

        if (firstExprPow.equals(secondExprPow)) {
            return true;
        }
        return false;
    }

    private Pow getVariablePow(Mul mulExpr) {
        if (mulExpr.getLHS() instanceof Pow) {
            return (Pow) mulExpr.getLHS();
        } else if (mulExpr.getRHS() instanceof Pow) {
            return (Pow) mulExpr.getRHS();
        }
        return null;
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
        Mul other = (Mul) obj;
        if (lhs == null || rhs == null) {
            return false;
        } else if ((!lhs.equals(other.lhs) || !rhs.equals(other.rhs)) &&
                    (!lhs.equals(other.rhs) || !rhs.equals(other.lhs))) {
            return false;
        }
        return true;
    }
}
