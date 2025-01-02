import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileReader;
import java.math.BigInteger;
import java.util.*;

public class ShamirSecretSharing {

    public static void main(String[] args) {
        try {
            // Read the JSON input
            JSONObject testcase1 = new JSONObject(new JSONTokener(new FileReader("s1.json")));
            JSONObject testcase2 = new JSONObject(new JSONTokener(new FileReader("s2.json")));

            // Process both test cases
            long c1 = solveForConstant(testcase1);
            long c2 = solveForConstant(testcase2);

            // Output results
            System.out.println("Secret for Test Case 1: " + c1);
            System.out.println("Secret for Test Case 2: " + c2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static long solveForConstant(JSONObject testcase) {
        // Parse n and k
        JSONObject keys = testcase.getJSONObject("keys");
        int n = keys.getInt("n");
        int k = keys.getInt("k");

        // Extract and decode the points (x, y)
        List<int[]> points = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            if (testcase.has(String.valueOf(i))) {
                JSONObject root = testcase.getJSONObject(String.valueOf(i));
                int x = i;
                int base = root.getInt("base");
                String value = root.getString("value");
                int y = decodeValue(value, base);
                points.add(new int[]{x, y});
            }
        }

        // Use only the first k points for the polynomial
        points = points.subList(0, k);

        // Perform Lagrange Interpolation to find the constant term
        return findConstantTerm(points, k);
    }

    private static int decodeValue(String value, int base) {
        // Decode the value from the specified base to an integer
        return new BigInteger(value, base).intValue();
    }

    private static long findConstantTerm(List<int[]> points, int k) {
        double c = 0;

        for (int i = 0; i < k; i++) {
            double term = points.get(i)[1]; // Start with y_i
            for (int j = 0; j < k; j++) {
                if (i != j) {
                    term *= (0 - points.get(j)[0]) / (double) (points.get(i)[0] - points.get(j)[0]);
                }
            }
            c += term;
        }

        return Math.round(c);
    }
}