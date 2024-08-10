package Expressions;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import Expressions.Operators.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Polynomial extends ConcreteFunction {
    private List<Double> coef;
    private int degree;

    public Polynomial(AbstractFunction function) {
        super(function);
        coef = findCoefs();
    }

    public List<Double> getCoef() {
        return this.coef;
    }

    public int getDegree() {
        return this.degree;
    }

    // The function must be in the for ax^n + bx^(n-1) + ... (The powers need not be in order)
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List<Double> findCoefs() {
        Map<Integer,Double> unorderedCoef = getLeafFunctions();
        List<Integer> orderedExponents = new ArrayList(new TreeSet(unorderedCoef.keySet()));
        List<Double> orderedCoef = new ArrayList<>();
        this.degree = orderedExponents.get(orderedExponents.size() - 1);
        for (int exponent : orderedExponents) {
            orderedCoef.add(unorderedCoef.get(exponent));
        }
        return orderedCoef;
    }

    public Map<Integer,Double> getLeafFunctions() {
        Map<Integer,Double> coef = new HashMap<>();
        collectLeafNodes(this.function, coef);
        return coef;
    }

    private static void collectLeafNodes(AbstractFunction function, Map<Integer,Double> coef) {
        if (function == null) {
            return;
        }

        if (function instanceof Constant) {
            coef.put(0, ((Constant) function).getValue());
        } else if (function instanceof Variable) {
            coef.put(1, 1.0);
        } else if (function instanceof Mul) {
            findCoefForMul((Mul) function, coef);
        } else if (function instanceof Pow) {
            coef.put((int) ((Constant) ((Pow) function).getLHS()).getValue(), 1.0);
        } else if (function instanceof Add) {
            Add functionAdd = (Add) function;
            collectLeafNodes((AbstractFunction) functionAdd.getLHS(), coef);
            collectLeafNodes((AbstractFunction) functionAdd.getRHS(), coef);
        }
    }

    private static void findCoefForMul(Mul function, Map<Integer,Double> coef) {
        double coefficient;
        if (function.getLHS() instanceof Constant) {
            coefficient = ((Constant) function.getLHS()).getValue();
            Expression rhs = function.getRHS();
            if (rhs instanceof Pow) {
                coef.put((int) ((Constant) ((Pow) rhs).getRHS()).getValue(), coefficient);
            } else if (rhs instanceof Variable) {
                coef.put(1, coefficient);
            }
        } else if (function.getRHS() instanceof Constant) {
            coefficient = ((Constant) function.getRHS()).getValue();
            Expression lhs = function.getLHS();
            if (lhs instanceof Pow) {
                coef.put((int) ((Constant) ((Pow) lhs).getRHS()).getValue(), coefficient);
            } else if (lhs instanceof Variable) {
                coef.put(1, coefficient);
            }
        }
    }

    /* Stopping condition: if the function is not an Add, otherwise continue to call recursively */
}
