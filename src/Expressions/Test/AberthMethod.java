package Expressions.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

import org.apache.commons.math3.complex.Complex;

public class AberthMethod {
    private static final double EPSILON = 1e-12;

    /**
     * Compute the upper and lower bounds for the roots of a polynomial.
     *
     * @param f a Function object representing the polynomial.
     * @return an array with upper and lower bounds.
     */
    public static double[] getUpperLowerBounds(Function1 f) {
        int degree = f.getDegree();
        double[] coef = f.getCoef();

        double upper = 1 + 1 / Math.abs(coef[coef.length - 1]) * maxAbs(coef);
        double lower = Math.abs(coef[0]) / (Math.abs(coef[0]) + maxAbs(sliceArray(coef, 1, degree + 1)));

        return new double[]{upper, lower};
    }

    /**
     * Find the maximum absolute value in an array.
     *
     * @param array an array of double values.
     * @return the maximum absolute value.
     */
    private static double maxAbs(double[] array) {
        double max = 0;
        for (double v : array) {
            max = Math.max(max, Math.abs(v));
        }
        return max;
    }

    /**
     * Slice an array from a start index to an end index.
     *
     * @param array the original array.
     * @param start the start index (inclusive).
     * @param end   the end index (exclusive).
     * @return a sliced array.
     */
    private static double[] sliceArray(double[] array, int start, int end) {
        double[] sliced = new double[end - start];
        System.arraycopy(array, start, sliced, 0, end - start);
        return sliced;
    }

    /**
     * Initialize the roots of a polynomial using random values within the bounds.
     *
     * @param f a Function object representing the polynomial.
     * @return a list of initialized roots as Complex numbers.
     */
    public static List<Complex> initRoots(Function1 f) {
        int degree = f.getDegree();
        double[] bounds = getUpperLowerBounds(f);
        double upper = bounds[0];
        double lower = bounds[1];

        List<Complex> roots = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < degree; i++) {
            double radius = lower + (upper - lower) * random.nextDouble();
            double angle = 2 * Math.PI * random.nextDouble();
            Complex root = new Complex(radius * Math.cos(angle), radius * Math.sin(angle));
            roots.add(root);
        }

        return roots;
    }

    /**
     * Compute the roots of a given polynomial using the Aberth Method.
     *
     * @param f a Function object representing the polynomial.
     * @return an array with the number of iterations and the list of roots.
     */
    public static Object[] aberthMethod(Function1 f) {
        List<Complex> roots = initRoots(f);
        int iteration = 0;

        while (true) {
            int valid = 0;

            for (int k = 0; k < roots.size(); k++) {
                Complex r = roots.get(k);
                Complex ratio = f.image(r).divide(f.derivative(r));
                Complex offset = ratio.divide( (new Complex(1).subtract(
                    ratio.multiply(sumInverse(roots, r))
                )));
                if (Math.abs(offset.getReal()) < EPSILON && Math.abs(offset.getImaginary()) < EPSILON) {
                    valid++;
                }
                roots.set(k, r.subtract(offset));
            }

            if (valid == roots.size()) {
                break;
            }
            iteration++;
        }

        // Round roots to 12 decimal places
        List<Complex> roundedRoots = new ArrayList<>();
        for (Complex root : roots) {
            roundedRoots.add(new Complex(round(root.getReal(), 12), round(root.getImaginary(), 12)));
        }

        return new Object[]{iteration, roundedRoots};
    }

    
    /**
     * Compute the sum of the inverses of the differences between a root and other roots.
     *
     * @param roots a list of roots.
     * @param r     the root for which the sum is computed.
     * @return the sum of inverses.
     */
    private static Complex sumInverse(List<Complex> roots, Complex r) {
        Complex sum = new Complex(0);
        for (Complex x : roots) {
            if (!x.equals(r)) {
                sum = sum.add(new Complex(1).divide(r.subtract(x)));
            }
        }
        return sum;
    }

    /**
     * Round a double value to a specified number of decimal places.
     *
     * @param value the value to round.
     * @param places the number of decimal places.
     * @return the rounded value.
     */
    private static double round(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

    public static void main(String[] args) {
        Map<Integer, Double> coef = new HashMap<>();
        coef.put(0, 4.0);
        coef.put(1, 4.0);
        coef.put(2, 1.0);
        Function1 function = new Function1(coef);
        Object[] result = aberthMethod(function);
        int iteration = (int) result[0];
        List<Complex> roots = (List<Complex>) result[1];
        String toPrint = "Iterations: " + iteration;
        for (Object i : roots) {
            toPrint += " " + (Complex) i;
        }
        System.out.println(toPrint);
    }
}



