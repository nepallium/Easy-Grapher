package Function;

import Model.Function;
import Model.RootFinder;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class InterceptTest {

    @Test
    public void testIntersection_1Root() {
        Function f = new Function("3x-5", true);
        Function g = new Function("x+2", true);

        List<Double> expected = List.of(3.5);
        List<Double> actual = RootFinder.findAllRoots(f, g, -10, 10, 0.5, 1);

        assertEquals("linear intersection", expected, actual);
    }

    @Test
    public void testIntersection_2Root() {
        Function f = new Function("x^2", true);
        Function g = new Function("x+2", true);

        List<Double> expected = List.of(-1.0, 2.0);
        List<Double> actual = RootFinder.findAllRoots(f, g, -10, 10, 0.5, 1);

        assertEquals("quadratic, linear", expected, actual);
    }

    @Test
    public void testIntersection_3Root() {
        Function f = new Function("x^3", true);
        Function g = new Function("4x", true);

        List<Double> expected = List.of(-2.0, 0.0, 2.0);
        List<Double> actual = RootFinder.findAllRoots(f, g, -10, 10, 0.5, 1);

        assertEquals("cubic vs linear", expected, actual);
    }

    @Test
    public void testIntersection_NoRoot() {
        Function f = new Function("x", true);
        Function g = new Function("x+2", true);

        List<Double> expected = List.of();
        List<Double> actual = RootFinder.findAllRoots(f, g, -10, 10, 0.5, 1);

        assertEquals("parallel lines, no intersection", expected, actual);
    }

    // --- Edge cases below ---

    @Test
    public void testIntersection_Tangent() {
        Function f = new Function("x^2", true);
        Function g = new Function("0", true);

        List<Double> expected = List.of(0.0);
        List<Double> actual = RootFinder.findAllRoots(f, g, -10, 10, 0.5, 1);

        assertEquals("tangent / double root", expected, actual);
    }

    @Test
    public void testIntersection_Tangent2() {
        Function f = new Function("x^2", true);
        Function g = new Function("x^3", true);

        List<Double> expected = List.of(0.0, 1.0);
        List<Double> actual = RootFinder.findAllRoots(f, g, -20, 20, 0.1, 1);

        assertEquals(expected, actual);
    }

    @Test
    public void testIntersection_SteepSlope() {
        Function f = new Function("x^3", true);
        Function g = new Function("0", true);

        List<Double> expected = List.of(0.0);
        List<Double> actual = RootFinder.findAllRoots(f, g, -1, 1, 0.1, 1);

        assertEquals("steep slope cubic", expected, actual);
    }

    @Test
    public void testIntersection_Discontinuity() {
        // 1/x discontinuity at x = 0
        Function f1 = new Function("1/x", true);
        Function g1 = new Function("0", true);
        List<Double> actual1 = RootFinder.findAllRoots(f1, g1, -10, 10, 0.5, 1);
        assertEquals("1/x should have no root", List.of(), actual1);

        // 1/(x-3) discontinuity at x = 3
        Function f2 = new Function("1/(x-3)", true);
        Function g2 = new Function("0", true);
        List<Double> actual2 = RootFinder.findAllRoots(f2, g2, 0, 6, 0.5, 1);
        assertEquals("1/(x-3) should have no root", List.of(), actual2);

        //        // tan(x) discontinuity at pi/2
//        Function f3 = new Function("tan(x)");
//        Function g3 = new Function("0");
//        List<Double> actual3 = RootFinder.findAllRoots(f3, g3, -3.5, 3.5, 0.001, 2);
//        List<Double> expected3 = List.of(-3.14, 0.0, 3.14);
//        assertEquals("tan(x) roots", expected3, actual3);
    }

    @Test
    public void testIntersection_CloseRoots() {
        Function f = new Function("sin(100*x)", true);
        Function g = new Function("0", true);

        // Only checking that some roots are detected in [-0.1, 0.1]
        List<Double> actual = RootFinder.findAllRoots(f, g, -0.1, 0.1, 0.001, 5);
        assertTrue("multiple close roots", actual.size() > 0);
    }

    @Test
    public void testIntersection_LargeRoots() {
        Function f = new Function("x^2 - 1000000", true);
        Function g = new Function("0", true);

        List<Double> expected = List.of(-1000.0, 1000.0);
        List<Double> actual = RootFinder.findAllRoots(f, g, -2000, 2000, 10, 1);

        assertEquals("large magnitude roots", expected, actual);
    }
}
