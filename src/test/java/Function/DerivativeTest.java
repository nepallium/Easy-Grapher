package Function;

import org.junit.Test;
import Model.Function;
import org.matheclipse.core.eval.ExprEvaluator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * @author Alex
 */
public class DerivativeTest {
    @Test
    public void testSymjaInitialization() {
        ExprEvaluator eval = new ExprEvaluator();
        String out = eval.eval("D(abs(x), x)").toString();
        System.out.println("OUT = " + out);
    }

    @Test
    public void testDerivative_regularCases() {
        Function f1 = new Function("x^2", true);
        f1.evaluateDerivative();
        assertEquals("2*x", f1.getDerivativeStr());

        Function f2 = new Function("sin(x)", true);
        f2.evaluateDerivative();
        assertEquals("cos(x)", f2.getDerivativeStr());

        Function f3 = new Function("x^3 + 2*x", true);
        f3.evaluateDerivative();
        assertEquals("2+3*x^2", f3.getDerivativeStr());

        Function f4 = new Function("e^x", true);
        f4.evaluateDerivative();
        assertEquals("e^x*ln(e)", f4.getDerivativeStr());
    }

    @Test
    public void testDerivative_edgeCases() {
        Function f1 = new Function("5", true);
        f1.evaluateDerivative();
        assertEquals("0", f1.getDerivativeStr());

        Function f2 = new Function("-x", true);
        f2.evaluateDerivative();
        assertEquals("-1", f2.getDerivativeStr());

        Function f3 = new Function("abs(x)", true);
        f3.evaluateDerivative();
        Function f3Derivative = f3.getDerivative();
        assertFalse(f3Derivative.isValid());

        Function f4 = new Function("1/x", true);
        f4.evaluateDerivative();
        assertEquals("-1/x^2", f4.getDerivativeStr());
    }

    @Test
    public void testDerivative_composition() {
        Function f1 = new Function("sin(x^2)", true);
        f1.evaluateDerivative();
        assertEquals("2*x*cos(x^2)", f1.getDerivativeStr());

        Function f2 = new Function("ln(sin(x))", true);
        f2.evaluateDerivative();
        assertEquals("cot(x)", f2.getDerivativeStr());

//        Function f3 = new Function("ln(sec(x) + tan(x))", true);
//        f3.evaluateDerivative();
//        assertEquals("sec(x)", f3.getDerivativeStr());
    }

    @Test
    public void testDerivative_higherOrder() {
        Function f1 = new Function("sin(x)", true);
        f1.evaluateDerivative();
        Function firstOrder = f1.getDerivative();
        assertEquals("cos(x)", firstOrder.getExprStr());
        firstOrder.isValid();

        firstOrder.evaluateDerivative();
        Function secondOrder = firstOrder.getDerivative();
        assertEquals("-sin(x)", secondOrder.getExprStr());
        secondOrder.isValid();

        secondOrder.evaluateDerivative();
        Function thirdOrder = secondOrder.getDerivative();
        assertEquals("-cos(x)", thirdOrder.getExprStr());
    }
}
