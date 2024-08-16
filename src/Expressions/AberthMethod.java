package Expressions;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AberthMethod {
    private static final BigDecimal EPSILON = new BigDecimal("1e-20", MathContext.DECIMAL128);

    /**
     * Compute the upper and lower bounds for the roots of a polynomial.
     *
     * @param f a Function object representing the polynomial.
     * @return an array with upper and lower bounds.
     */
    public static BigDecimal[] getUpperLowerBounds(Polynomial f) {
        List<BigDecimal> coef = f.getCoef();

        BigDecimal upper = BigDecimal.ONE.add(maxAbs(coef).divide(coef.get(coef.size() - 1).abs(), MathContext.DECIMAL128), MathContext.DECIMAL128);
        BigDecimal lower = (coef.get(0).abs()).divide((coef.get(0).abs()), MathContext.DECIMAL128);
        if (coef.size() > 1) {
            lower = lower.add((maxAbs(coef.subList(1, coef.size() - 1))).abs(), MathContext.DECIMAL128);
        }

        return new BigDecimal[]{upper, lower};
    }

    /**
     * Find the maximum absolute value in an array.
     *
     * @param array an array of double values.
     * @return the maximum absolute value.
     */
    private static BigDecimal maxAbs(List<BigDecimal> array) {
        BigDecimal max = BigDecimal.ZERO;
        for (BigDecimal v : array) {
            // max = Math.max(max, Math.abs(v));
            if ((v.abs()).compareTo(max) > 0) {
                max = v;
            }
        }
        return max;
    }

    /**
     * Initialize the roots of a polynomial using random values within the bounds.
     *
     * @param f a Function object representing the polynomial.
     * @return a list of initialized roots as Complex numbers.
     */
    public static List<ComplexNumber> initRoots(Polynomial f) {
        int degree = f.getDegree();
        BigDecimal[] bounds = getUpperLowerBounds(f);
        BigDecimal upper = bounds[0];
        BigDecimal lower = bounds[1];

        List<ComplexNumber> roots = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < degree; i++) {
            BigDecimal radius = lower.add((upper.subtract(lower)).multiply(new BigDecimal(Double.toString(random.nextDouble()), MathContext.DECIMAL128)));
            double angle = 2 * Math.PI * random.nextDouble();
            ComplexNumber root = new ComplexNumber(radius.multiply(new BigDecimal(Double.toString(Math.cos(angle)), MathContext.DECIMAL128)),
                                                    radius.multiply(new BigDecimal(Double.toString(Math.sin(angle)), MathContext.DECIMAL128)));
            roots.add(root);
        }
        // roots.add(new ComplexNumber(new BigDecimal("3")));
        // roots.add(new ComplexNumber(new BigDecimal("4")));

        return roots;
    }

    /**
     * Compute the roots of a given polynomial using the Aberth Method.
     *
     * @param f a Function object representing the polynomial.
     * @return an array with the number of iterations and the list of roots.
     */
    public static Object[] aberthMethod(Polynomial f) {
        List<ComplexNumber> roots = initRoots(f);
        int iteration = 0;
        Polynomial derivative = new Polynomial(f.derivative());

        while (true) {
            int valid = 0;

            for (int k = 0; k < roots.size(); k++) {
                ComplexNumber r = roots.get(k);
                System.out.println("Root " + k + ": " + r);

                // ComplexNumber image = f.image(r);
                // System.out.println(image);
                // ComplexNumber derivativeImage = derivative.image(r);
                // System.out.println(derivativeImage);
                // ComplexNumber division = image.divide(derivativeImage);
                // System.out.println(division);

                if (r.getReal().abs().compareTo(EPSILON) < 0) {
                    r = new ComplexNumber(BigDecimal.ZERO, r.getImaginary());
                }
    
                if (r.getImaginary().abs().compareTo(EPSILON) < 0) {
                    r = new ComplexNumber(r.getReal(), BigDecimal.ZERO);
                } 
                
                ComplexNumber ratio = (f.image(r)).divide(derivative.image(r));
                ComplexNumber offset = ratio.divide( (new ComplexNumber(BigDecimal.ONE).subtract(
                    ratio.multiply(sumInverse(roots, r))
                )));
                
                // ComplexNumber rounded = new ComplexNumber(r.getReal().round(new MathContext(2, RoundingMode.HALF_UP)), r.getImaginary().round(new MathContext(2, RoundingMode.HALF_UP)));
                // ComplexNumber image = f.image(rounded);
                // ComplexNumber imageRounded = new ComplexNumber(image.getReal().round(new MathContext(1, RoundingMode.HALF_UP)),image.getImaginary().round(new MathContext(1, RoundingMode.HALF_UP)));
                // if (imageRounded.getReal().compareTo(BigDecimal.ZERO) == 0 && imageRounded.getImaginary().compareTo(BigDecimal.ZERO) == 0) {
                //     System.exit(0);
                //     valid++;
                //     roots.set(k, rounded);
                //     continue;
                // }
                
                if (offset.getReal().abs().compareTo(EPSILON) < 0 && offset.getImaginary().abs().compareTo(EPSILON) < 0) {
                    valid++;
                    roots.set(k, r);
                    System.out.println("This is a correct root");
                    continue;
                }
                roots.set(k, r.subtract(offset));
            }

            if (valid == roots.size()) {
                break;
            }
            iteration++;
        }

        // Round roots to 12 decimal places
        ArrayList<ComplexNumber> roundedRoots = new ArrayList<>();
        for (ComplexNumber root : roots) {

            if (root.getReal().abs().compareTo(EPSILON) < 0) {
                root = new ComplexNumber(BigDecimal.ZERO, root.getImaginary());
            }

            if (root.getImaginary().abs().compareTo(EPSILON) < 0) {
                root = new ComplexNumber(root.getReal(), BigDecimal.ZERO);
            } 


            roundedRoots.add(new ComplexNumber(root.getReal().round(new MathContext(2, RoundingMode.HALF_UP)).stripTrailingZeros(),
                            root.getImaginary().round(new MathContext(2, RoundingMode.HALF_UP)).stripTrailingZeros()));
        } 

        sort(roundedRoots);

        String toPrint = "Iterations: " + iteration + "\nRoots:";
        for (ComplexNumber root : roundedRoots) {
            toPrint += " " + root;
        }
        System.out.println(toPrint);

        return new Object[]{iteration, roundedRoots};
    }

    
    /**
     * Compute the sum of the inverses of the differences between a root and other roots.
     *
     * @param roots a list of roots.
     * @param r     the root for which the sum is computed.
     * @return the sum of inverses.
     */
    private static ComplexNumber sumInverse(List<ComplexNumber> roots, ComplexNumber r) {
        ComplexNumber sum = new ComplexNumber(BigDecimal.ZERO);
        for (ComplexNumber x : roots) {
            if (x.compareTo(r) != 0) {
                sum = sum.add(new ComplexNumber(BigDecimal.ONE).divide(r.subtract(x)));
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

    // An implementation of insertion sort to sort the rounded roots
    static void sort(ArrayList<ComplexNumber> roots) {
        int n = roots.size();
        for (int i = 1; i < n; ++i) {
            ComplexNumber key = roots.get(i);
            int j = i - 1;

            /* Move elements of arr[0..i-1], that are
               greater than key, to one position ahead
               of their current position */
            while (j >= 0 && roots.get(j).compareTo(key) > 0) {
                roots.set(j + 1, roots.get(j));
                j = j - 1;
            }
            roots.set(j+1, key);
        }
    }
}



