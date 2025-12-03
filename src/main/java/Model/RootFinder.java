package Model;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.solvers.BrentSolver;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Finds intersections
 * @author Alex
 */
public class RootFinder {

    private static final PostfixEvaluator evaluator = new PostfixEvaluator();

    /**
     * Finds all roots of a function in [min, max] using a scanning and BrentSolver approach.
     *
     * @param f        the first fct
     * @param g        the second fct
     * @param min      minimum x value of the search interval
     * @param max      maximum x value of the search interval
     * @param step     scanning step size (smaller = more precise)
     * @param decimals how many decimals to round the roots
     * @return a list of all roots found
     */
    public static ArrayList<Double> findAllRoots(Function f, Function g, double min, double max, double step, int decimals) {
        Function hFct = subtractFcts(f, g);

        ArrayList<Double> roots = new ArrayList<>();
        UnivariateFunction h = hFct::valueAt;
        BrentSolver solver = new BrentSolver(1e-10, 1e-14);

        // because of rounding, we need to define a small tolerance for "effectively zero"
        double tolerance = 1e-9;

        for (double a = min; a < max; a += step) {
            double b = Math.min(a + step, max);

            double ha = hFct.valueAt(a);
            double hb = hFct.valueAt(b);

            if (Double.isInfinite(ha) || Double.isInfinite(hb) || Double.isNaN(ha) || Double.isNaN(hb)) {
                continue;
            }

            // 1) explicitly check if 'a' is a root (accounts for tangential roots, eg x^2 and 0)
            if (Math.abs(ha) < tolerance) {
                double rootClean = Math.round(a * Math.pow(10, decimals)) / Math.pow(10, decimals);
                if (!roots.contains(rootClean)) {
                    roots.add(rootClean);
                }
                // If 'a' is a root, we don't need to solve the interval [a, b]
                // unless the function oscillates wildly, but for safety, we can continue.
            }

            // 2) standard bracketing checks
            // only check for sign change to avoid duplicate work (from above) or solver errors
            try {
                if (ha * hb < 0) { // Strictly less than 0 implies a crossing
                    double root = solver.solve(1000, h, a, b);

                    double rootClean = Math.round(root * Math.pow(10, decimals)) / Math.pow(10, decimals);

                    // check if root is valid
                    double hRoot = hFct.valueAt(rootClean);
                    if (!Double.isInfinite(hRoot) && !Double.isNaN(hRoot) && Math.abs(hRoot) <= 1e-3) {
                        if (!roots.contains(rootClean)) {
                            roots.add(rootClean);
                        }
                    }
                }
            } catch (Exception e) {
                // Ignore intervals that solver fails on
            }
        }

        Collections.sort(roots);
        return roots;
    }

    /**
     * Subtracts 2 functions and returns that function
     *
     * @param f the first fct
     * @param g the second fct
     * @return the subtraction fct
     */
    private static Function subtractFcts(Function f, Function g) {
        if (f == null || g == null) {
            return null;
        }

        return new Function(String.format("%s-(%s)", f.getExprStr(), g.getExprStr()), true);
    }
}
