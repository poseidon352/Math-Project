package Expressions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.complex.Complex;

public class AberthMethod {
    private static final double EPSILON = 1e-10;

    /**
     * Compute the upper and lower bounds for the roots of a polynomial.
     *
     * @param f a Function object representing the polynomial.
     * @return an array with upper and lower bounds.
     */
    public static double[] getUpperLowerBounds(Polynomial f) {
        List<Double> coef = f.getCoef();

        double upper = 1 + maxAbs(coef) / Math.abs(coef.get(coef.size() - 1));
        double lower = Math.abs(coef.get(0)) / (Math.abs(coef.get(0)));
        if (coef.size() > 1) {
            lower += Math.abs(maxAbs(coef.subList(1, coef.size() - 1)));
        }

        return new double[]{upper, lower};
    }

    /**
     * Find the maximum absolute value in an array.
     *
     * @param array an array of double values.
     * @return the maximum absolute value.
     */
    private static double maxAbs(List<Double> array) {
        double max = 0;
        for (double v : array) {
            max = Math.max(max, Math.abs(v));
        }
        return max;
    }

    /**
     * Initialize the roots of a polynomial using random values within the bounds.
     *
     * @param f a Function object representing the polynomial.
     * @return a list of initialized roots as Complex numbers.
     */
    public static List<Complex> initRoots(Polynomial f) {
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
    public static Object[] aberthMethod(Polynomial f) {
        List<Complex> roots = initRoots(f);
        int iteration = 0;
        Polynomial derivative = new Polynomial(f.derivative());

        while (true) {
            int valid = 0;

            for (int k = 0; k < roots.size(); k++) {
                Complex r = roots.get(k);
                // System.out.println("Root " + k + ": " + r);
                Complex ratio = (f.image(r)).divide(derivative.image(r));
                Complex offset = ratio.divide( (new Complex(1).subtract(
                    ratio.multiply(sumInverse(roots, r))
                )));
                Complex rounded = new Complex(round(r.getReal(), 3), round(r.getImaginary(), 3));
                Complex image = f.image(rounded);
                Complex imageRounded = new Complex(Math.round(image.getReal()), Math.round(image.getImaginary()));
                if (imageRounded.equals(new Complex(0, 0))) {
                    valid++;
                    roots.set(k, rounded);
                    continue;
                }
                
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

        // // Round roots to 12 decimal places
        // List<Complex> roundedRoots = new ArrayList<>();
        // for (Complex root : roots) {
        //     boolean isRounded = false;
        //     for (int i = 1; i < 12; i++) {
        //         Complex rounded = new Complex(round(root.getReal(), i), round(root.getImaginary(), i));
        //         if (f.image(rounded).equals(new Complex(0))) {
        //             roundedRoots.add(rounded);
        //             isRounded = true;
        //             break;
        //         }
        //     }
        //     if (!isRounded) {
        //         roundedRoots.add(new Complex(round(root.getReal(), 12), round(root.getImaginary(), 12)));
        //     }
        // }
        //TODO: sort roots
        // List<Complex> orderedRoots = new ArrayList<>();
        // Complex maxRoot = orderedRoots.get(0);
        // for (Complex root : roundedRoots) {

        // }

        String toPrint = "Iterations: " + iteration + "\nRoots:";
        for (Complex root : roots) {
            toPrint += " " + root;
        }
        System.out.println(toPrint);

        return new Object[]{iteration, roots};
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

    }
}



