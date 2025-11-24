package Function;

import Model.Function;
import Model.PostfixEvaluator;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ValidityTest {
    @Before
    public void setUp() {
        PostfixEvaluator evaluator = new PostfixEvaluator();
    }

    @Test
    public void testValidFunction_ArithmeticExceptionsAllowed() {
        Function f1 = new Function("1/x");
        assertTrue("1/x", f1.isValid());

        Function f2 = new Function("sqrt(-1)");
        assertTrue(f2.isValid());

        Function f3 = new Function("ln(0)");
        assertTrue(f3.isValid());

        Function f4 = new Function("tan(pi/2)");
        assertTrue(f4.isValid());
    }

    @Test
    public void testValidFunction_PiWithLetter() {
        Function f1 = new Function("pi3");
        assertTrue(f1.isValid());

        Function f2 = new Function("3pi");
        assertTrue(f2.isValid());

        Function f3 = new Function("pisin(x)");
        assertTrue(f3.isValid());

        Function f4 = new Function("pie");
        assertTrue("pi*e", f4.isValid());

//        Function f5 = new Function("xpi");
//        assertTrue("x*pi", f5.isValid());
    }

    @Test
    public void testValidFunction_EulerWithLetter() {
        Function f1 = new Function("e3");
        assertTrue(f1.isValid());

        Function f2 = new Function("3e");
        assertTrue(f2.isValid());

        Function f3 = new Function("esin(x)");
        assertTrue(f3.isValid());

        Function f4 = new Function("epi");
        assertTrue("e*pi", f4.isValid());

        Function f5 = new Function("ey");
        assertTrue("e*y", f5.isValid());
    }

    @Test
    public void testInvalidFunction_Empty() {
        Function f = new Function("");
        assertFalse(f.isValid());
    }

    @Test
    public void testInvalidFunction_OnlySpaces() {
        Function f = new Function("   ");
        assertFalse(f.isValid());
    }

    @Test
    public void testInvalidFunction_UnbalancedParentheses() {
//        assertFalse(new Function("(x+2").isValid());
        assertFalse(new Function("x+2)").isValid());
        assertFalse(new Function("((3x(())").isValid());
    }

    @Test
    public void testInvalidFunction_BadOperators() {
        assertFalse("Unsupported operator sequence", new Function("x**2").isValid());
        assertFalse("Unsupported operator sequence", new Function("x//2").isValid());
        assertFalse("Operator misuse", new Function("x+*2").isValid());
    }

    @Test
    public void testInvalidFunction_MissingOperands() {
        assertFalse(new Function("-").isValid());
        assertFalse(new Function("*x").isValid());
    }

    @Test
    public void testInvalidFunction_UnknownFunctions() {
        assertFalse(new Function("sqtr(x)").isValid());
        assertFalse(new Function("sinn(x)").isValid());
        assertFalse(new Function("logg(x)").isValid());
    }

    @Test
    public void testInvalidFunction_InvalidCharacters() {
        assertFalse(new Function("x$2").isValid());
        assertFalse(new Function("x#y").isValid());
        assertFalse(new Function("x@3").isValid());
        assertFalse(new Function("3!").isValid());
        assertFalse(new Function("3%").isValid());
        assertFalse(new Function("3_").isValid());
    }

    @Test
    public void testInvalidFunction_EmptyFunctionCall() {
        assertFalse("Empty function call", new Function("sin()").isValid());
        assertFalse("Empty function call", new Function("log()").isValid());
    }

    @Test
    public void testInvalidFunction_OperatorAtEnd() {
        assertFalse(new Function("x+").isValid());
        assertFalse(new Function("x*").isValid());
        assertFalse(new Function("x/").isValid());
    }

    @Test
    public void testInvalidFunction_OnlyOperator() {
        assertFalse(new Function("+").isValid());
        assertFalse(new Function("*").isValid());
    }

    @Test
    public void testInvalidFunction_DotMisuse() {
        assertFalse("Double dot in number", new Function("3..5").isValid());
        assertFalse("Double dot in number", new Function("..3").isValid());
//        assertFalse("Dot at end", new Function("3.").isValid());
    }


}
