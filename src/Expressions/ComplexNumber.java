package Expressions;

import java.math.BigDecimal;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.util.FastMath;
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

        return createComplexNumber(real.add(addend.getReal()),
                             imaginary.add(addend.getImaginary()));
    }

    public ComplexNumber subtract(ComplexNumber subtrahend)
        throws NullArgumentException {
        MathUtils.checkNotNull(subtrahend);

        return createComplexNumber(real.subtract(subtrahend.getReal()),
                             imaginary.subtract(subtrahend.getImaginary()));
    }

    public ComplexNumber multiply(ComplexNumber factor)
        throws NullArgumentException {
        MathUtils.checkNotNull(factor);

        return createComplexNumber(real.multiply(factor.getReal()).subtract(imaginary.multiply(factor.getImaginary())),
                             real.multiply(factor.getImaginary()).subtract(imaginary.multiply(factor.getReal())));
    }

    public ComplexNumber divide(ComplexNumber divisor)
        throws NullArgumentException {
        MathUtils.checkNotNull(divisor);

        final BigDecimal c = divisor.getReal();
        final BigDecimal d = divisor.getImaginary();

        if (c.abs().compareTo(d.abs()) < 0) {
            BigDecimal q = c.divide(d);
            BigDecimal denominator = (c.multiply(q)).add(d);
            return createComplexNumber(((real.multiply(q)).add(imaginary)).divide(denominator),
                ((imaginary.multiply(q)).subtract(real)).divide(denominator));
        } else {
            BigDecimal q = d.divide(c);
            BigDecimal denominator = (d.multiply(q)).add(c);
            return createComplexNumber(((imaginary.multiply(q)).add(real)).divide(denominator),
                (imaginary.subtract(real).multiply(q)).divide(denominator));
        }
    }

    protected ComplexNumber createComplexNumber(BigDecimal realPart,
                                    BigDecimal imaginaryPart) {
        return new ComplexNumber(realPart, imaginaryPart);
    }
    
}
