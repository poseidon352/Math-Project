package Expressions.Test;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.complex.Complex;

public class Function1 {
    private Map<Integer, Double> coef;

    /**
     * Constructor to initialize the polynomial with given coefficients.
     *
     * @param coef a map representing the coefficients of the polynomial, with exponent as key and coefficient as value.
     */
    public Function1(Map<Integer, Double> coef) {
        this.coef = new HashMap<>();
        for (Map.Entry<Integer, Double> entry : coef.entrySet()) {
            if (entry.getValue() != 0) {
                this.coef.put(entry.getKey(), entry.getValue());
            }
        }
        System.out.println(this.coef);
    }

    /**
     * Get the degree of the polynomial.
     *
     * @return the degree of the polynomial.
     */
    public int getDegree() {
        return coef.keySet().stream().max(Integer::compare).orElse(0);
    }

    /**
     * Get the coefficients of the polynomial as an array.
     *
     * @return an array of coefficients, starting from the coefficient for x^0.
     */
    public double[] getCoef() {
        int minIndex = coef.keySet().stream().min(Integer::compare).orElse(0);
        int size = getDegree() - minIndex + 1;
        double[] coefArray = new double[size];
        for (int i = 0; i < size; i++) {
            coefArray[i] = coef.getOrDefault(minIndex + i, 0.0);
        }
        return coefArray;
    }

    /**
     * Get the coefficients of the derivative polynomial as an array.
     *
     * @return an array of derivative coefficients.
     */
    public double[] getDcoef() {
        double[] coefArray = getCoef();
        double[] dcoefArray = new double[coefArray.length - 1];
        for (int i = 1; i < coefArray.length; i++) {
            dcoefArray[i - 1] = coefArray[i] * i;
        }
        return dcoefArray;
    }

    /**
     * Evaluate the polynomial at a given x value.
     *
     * @param x the value to evaluate the polynomial at.
     * @return the polynomial value at x.
     */
    public Complex image(Complex x) {
        Complex result = new Complex(0);
        double[] coefArray = getCoef();
        for (int i = 0; i < coefArray.length; i++) {
            result = result.add((new Complex(coefArray[i])).multiply(x.pow(i)));
        }
        return result;
    }

    /**
     * Compute the derivative of the polynomial at a given x value.
     *
     * @param x the value to compute the derivative at.
     * @return the derivative of the polynomial at x.
     */
    public Complex derivative(Complex x) {
        Complex delta = new Complex(1.e-12);
        return (image(x.add(delta)).subtract(image(x))).divide(delta);
    }
}
