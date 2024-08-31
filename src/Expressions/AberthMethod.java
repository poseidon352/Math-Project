package Expressions;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

public class AberthMethod {
    // public static final MathContext mathContext = MathContext.DECIMAL128;
    public static final MathContext mathContext = new MathContext(50, RoundingMode.HALF_EVEN);
    private static final BigDecimal EPSILON = new BigDecimal("1e-20", mathContext);

    /**
     * Compute the upper and lower bounds for the roots of a polynomial.
     *
     * @param f a Function object representing the polynomial.
     * @return an array with upper and lower bounds.
     */
    public static BigDecimal[] getUpperLowerBounds(Polynomial f) {
        List<BigDecimal> coef = f.getCoef();

        BigDecimal upper = BigDecimal.ONE.add(maxAbs(coef).divide(coef.get(coef.size() - 1).abs(), mathContext), mathContext);
        BigDecimal lower = (coef.get(0).abs()).divide((coef.get(0).abs()), mathContext);
        if (coef.size() > 1) {
            lower = lower.add((maxAbs(coef.subList(1, coef.size() - 1))).abs(), mathContext);
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
     * @param f a Polynomial object.
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
            BigDecimal radius = lower.add((upper.subtract(lower)).multiply(new BigDecimal(Double.toString(random.nextDouble()), mathContext)));
            double angle = 2 * Math.PI * random.nextDouble();
            ComplexNumber root = new ComplexNumber(radius.multiply(new BigDecimal(Double.toString(Math.cos(angle)), mathContext)),
                                                    radius.multiply(new BigDecimal(Double.toString(Math.sin(angle)), mathContext)));
            roots.add(root);
        }
        // for (int i = 0; i < degree; i++) {
        //     roots.add(new ComplexNumber(new BigDecimal(i+1)));
        // }

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
        int numberOfRoots = roots.size();
        Set<Integer> computedRootsIndex = new HashSet<>();

        int numOfThreads = 4;
        int extra = 0;
        int rootsPerThread = numberOfRoots / numOfThreads;

        boolean extraThread = false;
        int remainder = numberOfRoots % numOfThreads;
        
        if (remainder != 0) {
            extraThread = true;
            extra++;
        }

        while (true) {
            CountDownLatch latch = new CountDownLatch(numOfThreads + extra);
            for (int i = 0; i < numOfThreads; i++) {
                Multithreading thread = new AberthMethod.Multithreading(roots, f, derivative, computedRootsIndex, rootsPerThread*i, rootsPerThread*(i+1) - 1, latch);
                thread.start();
            }

            if (extraThread) {
                Multithreading thread = new AberthMethod.Multithreading(roots, f, derivative, computedRootsIndex, numberOfRoots - remainder - 1, numberOfRoots - 1, latch);
                thread.start();
            }

            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            iteration++;
            if (computedRootsIndex.size() >= numberOfRoots) {
                break;
            }
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

            
            roundedRoots.add(new ComplexNumber(root.getReal().setScale(3, RoundingMode.HALF_UP),
                        root.getImaginary().setScale(3, RoundingMode.HALF_UP)));
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

    // An implementation of insertion sort to sort the rounded roots
    static void sort(ArrayList<ComplexNumber> roots) {
        int n = roots.size();
        for (int i = 1; i < n; i++) {
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

    private static class Multithreading extends Thread {
        private List<ComplexNumber> roots;
        private Polynomial function;
        private Polynomial derivative;
        private Set<Integer> computedRootsIndex;
        private int startIndex;
        private int stopIndex;
        private CountDownLatch latch;

        public Multithreading(List<ComplexNumber> roots, Polynomial function, Polynomial derivative, Set<Integer> computedRootsIndex, int startIndex, int stopIndex, CountDownLatch latch) {
            this.roots = roots;
            this.function = function;
            this.derivative = derivative;
            this.computedRootsIndex = computedRootsIndex;
            this.startIndex = startIndex;
            this.stopIndex = stopIndex;
            this.latch = latch;
        }

        @Override
        public void run()
        {
            try {
                for (int k = startIndex; k <= stopIndex; k++) {
                    if (!computedRootsIndex.contains(k)) {
                        ComplexNumber r = roots.get(k);
                        //System.out.println("Root " + k + ": " + r);
                        ComplexNumber ratio = function.image(r).divide(derivative.image(r));
                        ComplexNumber offset = ratio.divide((new ComplexNumber(BigDecimal.ONE).subtract(
                            ratio.multiply(sumInverse(roots, r)))));
                        
                        ComplexNumber roundedOffset = new ComplexNumber(offset.getReal().setScale(5, RoundingMode.HALF_DOWN), offset.getImaginary().setScale(5, RoundingMode.HALF_DOWN));

                        
                        if (roundedOffset.getReal().abs().compareTo(EPSILON) < 0 && roundedOffset.getImaginary().abs().compareTo(EPSILON) < 0) {
                            computedRootsIndex.add(k);
                        }

                        ComplexNumber newRoot = r.subtract(offset);
                        //ComplexNumber roundedRoot = new ComplexNumber(newRoot.getReal().setScale(2, RoundingMode.HALF_DOWN), newRoot.getImaginary().setScale(2, RoundingMode.HALF_DOWN));
                        System.out.println("Root " + k + ": " + newRoot);
                        roots.set(k, newRoot);
                    }
                }
            } catch (Exception e) {
                // Throwing an exception
                System.out.println("Exception is caught");
            } finally {
                this.latch.countDown();
            }
        }
    }
}



