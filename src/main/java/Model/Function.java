package Model;

import lombok.Getter;
import org.matheclipse.core.eval.ExprEvaluator;

import java.util.EmptyStackException;

/**
 * A function
 *
 * @author Alex
 */
public class Function {
    @Getter
    private String exprStr;
    private Token[] postfix;

    @Getter
    private String derivativeStr;

    private Function derivative;

    private boolean isValid;

    private static final PostfixEvaluator evaluator = new PostfixEvaluator();
    public static ExprEvaluator derivEvaluator;


    public Function(String exprStr) {
        this.exprStr = exprStr;
        this.postfix = null;
        this.isValid = false;
    }

    // testing purposes
    public Function(String exprStr, boolean ignoreValidity) {
        this.exprStr = exprStr;
        this.postfix = evaluator.convertInfixToPost(exprStr);
        this.isValid = true;
    }

    /**
     * Evaluates f(x)
     *
     * @param x the x value
     * @return the evaluation of f(x)
     */
    public double valueAt(double x) {
        if (postfix == null) {
            return Double.NaN;
        }
        return evaluator.evaluatePostfix(this.postfix, x);
    }

    /**
     * Checks if the expression string creates a valid function or not
     *
     * @return whether the function is valid
     */
    public boolean isValid() {
        if (exprStr == null || exprStr.isBlank()) {
            this.isValid = false;
            return false;
        }

        try {
            if (postfix == null) {
                try {
                    postfix = evaluator.convertInfixToPost(exprStr);
                } catch (EmptyStackException e) {
//                    System.out.println("bad parenthesis");
                    this.isValid = false;
                    return false;
                } catch (IllegalArgumentException e) {
//                    System.out.println("bad symbol");
                    this.isValid = false;
                    return false;
                }
            }

            valueAt(0);
            this.isValid = true;
            return true;
        } catch (ArithmeticException e) {
            this.isValid = true;
            return true;
        } catch (Exception e) {
            this.isValid = false;
            return false;
        }
    }

    /**
     * Evaluates the derivative of f(x) and sets it
     */
    public void evaluateDerivative() {
        if (this.isValid) {

            derivativeStr = derivEvaluator.eval(String.format("D(%s, x)", exprStr)).toString().toLowerCase();
            derivativeStr = derivativeStr.replace("log", "ln");
            derivative = new Function(derivativeStr);
        }
    }

    public Token[] getPostfix() {
        if (postfix == null) {
            isValid();
        }

        return postfix;
    }

    public Function getDerivative() {
        if (this.derivative == null) {
            this.evaluateDerivative();
        }
        return derivative;
    }
}
