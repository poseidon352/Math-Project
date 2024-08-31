package Expressions;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import Expressions.Operators.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

public class Polynomial extends ConcreteFunction {
    private List<BigDecimal> coef;
    private int degree;

    public Polynomial(AbstractFunction function) {
        super(function);
        coef = findCoefs();
    }

    public List<BigDecimal> getCoef() {
        return this.coef;
    }

    public int getDegree() {
        return this.degree;
    }

    // The function must be in the form ax^n + bx^(n-1) + ... (The powers need not be in order)
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List<BigDecimal> findCoefs() {
        Map<Integer,BigDecimal> unorderedCoef = getLeafFunctions();
        List<Integer> orderedExponents = new ArrayList(new TreeSet(unorderedCoef.keySet()));
        List<BigDecimal> orderedCoef = new ArrayList<>();
        this.degree = orderedExponents.get(orderedExponents.size() - 1);
        for (int exponent : orderedExponents) {
            orderedCoef.add(unorderedCoef.get(exponent));
        }
        return orderedCoef;
    }

    private Map<Integer,BigDecimal> getLeafFunctions() {
        Map<Integer,BigDecimal> coef = new HashMap<>();
        collectLeafNodes(this.function, coef);
        return coef;
    }

    private static void collectLeafNodes(AbstractFunction function, Map<Integer,BigDecimal> coef) {
        if (function == null) {
            return;
        }

        if (function instanceof Constant) {
            coef.put(0, ((Constant) function).getBigDecimalValue());
        } else if (function instanceof Variable) {
            coef.put(1, BigDecimal.ONE);
        } else if (function instanceof Mul) {
            findCoefForMul((Mul) function, coef);
        } else if (function instanceof Pow) {
            coef.put((int) ((Constant) ((Pow) function).getLHS()).getValue(), BigDecimal.ONE);
        } else if (function instanceof Add) {
            Add functionAdd = (Add) function;
            collectLeafNodes((AbstractFunction) functionAdd.getLHS(), coef);
            collectLeafNodes((AbstractFunction) functionAdd.getRHS(), coef);
        }
    }

    private static void findCoefForMul(Mul function, Map<Integer,BigDecimal> coef) {
        BigDecimal coefficient;
        if (function.getLHS() instanceof Constant) {
            coefficient = ((Constant) function.getLHS()).getBigDecimalValue();
            Expression rhs = function.getRHS();
            if (rhs instanceof Pow) {
                coef.put((int) ((Constant) ((Pow) rhs).getRHS()).getValue(), coefficient);
            } else if (rhs instanceof Variable) {
                coef.put(1, coefficient);
            }
        } else if (function.getRHS() instanceof Constant) {
            coefficient = ((Constant) function.getRHS()).getBigDecimalValue();
            Expression lhs = function.getLHS();
            if (lhs instanceof Pow) {
                coef.put((int) ((Constant) ((Pow) lhs).getRHS()).getValue(), coefficient);
            } else if (lhs instanceof Variable) {
                coef.put(1, coefficient);
            }
        }
    }

    public ComplexNumber image(ComplexNumber x) {
        ComplexNumber result = new ComplexNumber(BigDecimal.ZERO);
        for (int i = 0; i < coef.size(); i++) {
            result = result.add((new ComplexNumber(coef.get(i))).multiply(x.pow(i)));
        }
        return result;
    }
}
