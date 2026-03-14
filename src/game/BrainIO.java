package game;

import java.io.*;

/**
 * Utility for saving/loading NeuralNetwork weights from a simple text file.
 */
public class BrainIO {

    public static void save(NeuralNetwork brain, File file) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
            // w1
            for (double[] row : brain.w1) {
                for (int j = 0; j < row.length; j++) {
                    if (j > 0) out.print(" ");
                    out.print(row[j]);
                }
                out.println();
            }
            out.println("#b1");
            // b1
            for (int i = 0; i < brain.b1.length; i++) {
                if (i > 0) out.print(" ");
                out.print(brain.b1[i]);
            }
            out.println();
            out.println("#w2");
            // w2
            for (int i = 0; i < brain.w2.length; i++) {
                if (i > 0) out.print(" ");
                out.print(brain.w2[i]);
            }
            out.println();
            out.println("#b2");
            out.println(brain.b2);
        }
    }

    public static void loadInto(NeuralNetwork brain, File file) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int row = 0;
            // w1 rows until "#b1"
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#b1")) break;
                String[] parts = line.trim().split("\\s+");
                for (int j = 0; j < parts.length && j < brain.w1[row].length; j++) {
                    brain.w1[row][j] = Double.parseDouble(parts[j]);
                }
                row++;
            }

            // b1
            line = br.readLine();
            if (line != null) {
                String[] parts = line.trim().split("\\s+");
                for (int i = 0; i < parts.length && i < brain.b1.length; i++) {
                    brain.b1[i] = Double.parseDouble(parts[i]);
                }
            }

            // "#w2"
            while ((line = br.readLine()) != null && !line.startsWith("#w2")) {
                // skip
            }
            // w2
            line = br.readLine();
            if (line != null) {
                String[] parts = line.trim().split("\\s+");
                for (int i = 0; i < parts.length && i < brain.w2.length; i++) {
                    brain.w2[i] = Double.parseDouble(parts[i]);
                }
            }

            // "#b2"
            while ((line = br.readLine()) != null && !line.startsWith("#b2")) {
                // skip
            }
            // b2
            line = br.readLine();
            if (line != null) {
                brain.b2 = Double.parseDouble(line.trim());
            }
        }
    }
}

