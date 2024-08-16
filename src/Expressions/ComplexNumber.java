package Expressions;

import java.math.BigDecimal;
import java.math.MathContext;

import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.util.MathUtils;

public class ComplexNumber {
    private final BigDecimal real;
    private final BigDecimal imaginary;

    public BigDecimal getReal() {
        return real;
    }

    public BigDecimal getImaginary() {
        return imaginary;
    }


    public ComplexNumber(BigDecimal real) {
        this(real, BigDecimal.ZERO);
    }

    public ComplexNumber(BigDecimal real, BigDecimal imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }


    public ComplexNumber add(ComplexNumber addend) throws NullArgumentException {
        MathUtils.checkNotNull(addend);

        return createComplexNumber(real.add(addend.getReal(), MathContext.DECIMAL128),
                             imaginary.add(addend.getImaginary(), MathContext.DECIMAL128));
    }

    public ComplexNumber subtract(ComplexNumber subtrahend)
        throws NullArgumentException {
        MathUtils.checkNotNull(subtrahend);

        return createComplexNumber(real.subtract(subtrahend.getReal(), MathContext.DECIMAL128),
                             imaginary.subtract(subtrahend.getImaginary(), MathContext.DECIMAL128));
    }

    public ComplexNumber multiply(ComplexNumber factor)
        throws NullArgumentException {
        MathUtils.checkNotNull(factor);

        return createComplexNumber((real.multiply(factor.getReal(), MathContext.DECIMAL128)).subtract(imaginary.multiply(factor.getImaginary(), MathContext.DECIMAL128), MathContext.DECIMAL128),
                             (real.multiply(factor.getImaginary(), MathContext.DECIMAL128)).add(imaginary.multiply(factor.getReal(), MathContext.DECIMAL128), MathContext.DECIMAL128));
    }

    public ComplexNumber divide(ComplexNumber divisor)
        throws NullArgumentException {
        MathUtils.checkNotNull(divisor);

        final BigDecimal c = divisor.getReal();
        final BigDecimal d = divisor.getImaginary();

        if ((c.abs()).compareTo(d.abs()) < 0) {
            BigDecimal q = c.divide(d, MathContext.DECIMAL128);
            BigDecimal denominator = (c.multiply(q)).add(d);
            return createComplexNumber(((real.multiply(q, MathContext.DECIMAL128)).add(imaginary, MathContext.DECIMAL128)).divide(denominator, MathContext.DECIMAL128),
                ((imaginary.multiply(q, MathContext.DECIMAL128)).subtract(real, MathContext.DECIMAL128)).divide(denominator, MathContext.DECIMAL128));
        } else {
            BigDecimal q = d.divide(c, MathContext.DECIMAL128);
            BigDecimal denominator = (d.multiply(q, MathContext.DECIMAL128)).add(c, MathContext.DECIMAL128);
            return createComplexNumber(((imaginary.multiply(q, MathContext.DECIMAL128)).add(real, MathContext.DECIMAL128)).divide(denominator, MathContext.DECIMAL128),
                (imaginary.subtract(real.multiply(q, MathContext.DECIMAL128), MathContext.DECIMAL128)).divide(denominator, MathContext.DECIMAL128));
        }
    }

    public ComplexNumber pow(int power) {
        ComplexNumber value = new ComplexNumber(BigDecimal.ONE);
        for (int i = 1; i <= power; i++) {
            value = multiply(value);
        }
        return value;
    }

    // returns -1, 0, or 1 as this is numerically
    // less than, equal to, or greater than val.
    public int compareTo(ComplexNumber val) {

        BigDecimal valReal = val.getReal();
        BigDecimal valImaginary = val.getImaginary();

        // If the real values are equal
        if (real.compareTo(valReal) == 0) {
            if (imaginary.compareTo(valImaginary) < 0) {
                return -1;
            } else if (imaginary.compareTo(valImaginary) != 0) {
                return 1;
            } else {
                return 0;
            }
        } else if (real.compareTo(valReal) < 0) {
            return -1;
        }

        return 1;

    }

    public ComplexNumber createComplexNumber(BigDecimal realPart,
                                    BigDecimal imaginaryPart) {
        return new ComplexNumber(realPart, imaginaryPart);
    }

    @Override
    public String toString() {
        return "(" + real.stripTrailingZeros().toPlainString() + ", " + imaginary.stripTrailingZeros().toPlainString() + ")";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((real == null) ? 0 : real.hashCode());
        result = prime * result + ((imaginary == null) ? 0 : imaginary.hashCode());
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
        ComplexNumber other = (ComplexNumber) obj;
        if (real == null) {
            if (other.real != null)
                return false;
        } else if (!real.equals(other.real))
            return false;
        if (imaginary == null) {
            if (other.imaginary != null)
                return false;
        } else if (!imaginary.equals(other.imaginary))
            return false;
        return true;
    }

    
    
}
