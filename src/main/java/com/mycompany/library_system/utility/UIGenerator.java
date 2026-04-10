package com.mycompany.library_system.utility;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.function.Supplier;

/**
 * 
 * @author all
 */
public class UIGenerator {
    public static final int TOTAL_WIDTH = 160;
    static Scanner scanner = new Scanner(System.in);

    // Used for build a title
    public static void titleBuilder(String title) {
        System.out.println("=".repeat(TOTAL_WIDTH));
        System.out.println(centerText(title, TOTAL_WIDTH));
        System.out.println("=".repeat(TOTAL_WIDTH));
    }

    // Clear the console screen in NetBeans
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void displayInvalidChoiceMessage() {
        System.out.println("\nInvalid input! Please try again.");
    }
    
    public static void displayExitMessage() {
        System.out.println("\nExiting system...");
    }

    public static void pressEnterToContinue() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public static boolean fileExists(String path) {
        return new File(path).exists();
    }

    /**  
     * General option menu
     * @author wh
     * @param options Strings list to display (Option 0 is for exit)
     * @param optionInputMessage Prompt message for user input
     * @param action Array of Runnable actions corresponding to each option
     * @return true if user chooses to exit (option 0), otherwise false
     */
    public static boolean optionMenu(String[] options, String optionInputMessage, Supplier<Boolean>[] actions) {
        /*
        -----------------
        | Sample result |
        -----------------

        -----------------------------
         [1] Add Book
         [2] Edit Book
         [3] Delete Book
         [0] Back to Main Menu
        -----------------------------
         Select an option >>  

        */

        System.out.println("-".repeat(50));
        for (String option : options) {
            System.out.println(option);
        }
        System.out.println("-".repeat(50));

        int input = UserPrompt.inputInteger(optionInputMessage, 0, options.length - 1);

        if (input == 0) {
            return true;
        } 
        else {
            return actions[input - 1].get();
        }
    }

    public static String reportTitleBuilder(String line1, String line2) {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(TOTAL_WIDTH)).append("\n");
        sb.append(centerText(line1, TOTAL_WIDTH)).append("\n");
        sb.append(centerText(line2, TOTAL_WIDTH)).append("\n");
        sb.append("=".repeat(TOTAL_WIDTH)).append("\n");

        return sb.toString();
    }

    public static String centeredTitleWithUnderline(String title) {
        StringBuilder sb = new StringBuilder();
        sb.append(centerText(title, TOTAL_WIDTH)).append("\n");
        sb.append(centerText("-".repeat(title.length() + 4), TOTAL_WIDTH)).append("\n");

        return sb.toString();
    }

    // REPORT UI Generator
    public static String centerText(String text, int totalWidth) {
        int paddingNeeded = totalWidth - text.length();
        int leftPad = paddingNeeded / 2;
        int rightPad = paddingNeeded - leftPad;

        return " ".repeat(Math.max(0, leftPad))
                + text
                + " ".repeat(Math.max(0, rightPad));
    }

    public static void appendHeader(StringBuilder report, String reportTopic, String title) {
        report.append("\n");
        report.append(reportTitleBuilder(
                "TAR UMT LIBRARY MANAGEMENT SYSTEM",
                reportTopic
        ));
        report.append("\n");
        report.append(centeredTitleWithUnderline(title));
        report.append("\n");
        report.append("Generated At : ")
                .append(LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .append("\n");
        report.append("*".repeat(160)).append("\n\n");
    }

    public static void appendFooter(StringBuilder report) {
        report.append("*".repeat(160)).append("\n");
        report.append(centerText("END OF THE REPORT", UIGenerator.TOTAL_WIDTH)).append("\n");
        report.append("=".repeat(160)).append("\n");
    }

    public static void appendCenteredLine(StringBuilder report, String text) {
        report.append(UIGenerator.centerText(text, UIGenerator.TOTAL_WIDTH)).append("\n");
    }

    public static String formatYAxisLabel(double value, boolean isDiscrete) {
        if (isDiscrete) {
            if (value >= 1_000_000) return String.format("%5.1fM", value / 1_000_000.0);
            if (value >= 1_000) return String.format("%5.1fK", value / 1_000.0);
            return String.format("%5d", Math.round(value)); 
        } else {
            if (value >= 1_000_000) return String.format("$%5.1fM", value / 1_000_000.0);
            if (value >= 1_000) return String.format("$%5.1fK", value / 1_000.0);
            return String.format("%5.0f", value);
        }
    }

    /**
     * Generates a vertical bar chart and appends it to a StringBuilder.
     *
     * @param report       The StringBuilder to append the generated chart to.
     * @param labels       An array of String labels for the X-axis.
     * @param values       An array of integers representing the height of each bar.
     * @param size         The number of data points to plot (number of columns).
     * @param column_width The character width of each column/bar in the chart.
     * @param maxValue     The maximum value in the dataset, used for scaling.
     * @param isDiscrete   A flag indicating whether the data is discrete.
        *                     If true, the Y-axis will be labeled with integer values.
        *                     If false, the Y-axis will be labeled with scaled values.
     */
    public static void appendVerticalBarChart(StringBuilder report, String[] labels, int[] values, int size, int chartHeight, int columnWidth, double maxValue, boolean isDiscrete) {
        int yAxisWidth = 8;
        int leftPrefixWidth = yAxisWidth + 2;

        for (int level = chartHeight; level >= 1; level--) {
            StringBuilder row = new StringBuilder();
            
            // Calculate the scaled value for this vertical level
            double scaledValue = (chartHeight == 0) ? 0 : (maxValue / chartHeight) * level;
            
            // Add the formatted Y-Axis Label
            row.append(String.format("%" + yAxisWidth + "s |", formatYAxisLabel(scaledValue, isDiscrete)));

            int topHalfStep = level * 2;
            int bottomHalfStep = topHalfStep - 1;

            // Draw the bars
            for (int i = 0; i < size; i++) {
                
                if (values[i] == topHalfStep) {
                    row.append(UIGenerator.centerText("--", columnWidth));
                } 
                else if (values[i] > topHalfStep) {
                    row.append(UIGenerator.centerText("||", columnWidth));
                } 
                else if (values[i] == bottomHalfStep) {
                    row.append(UIGenerator.centerText("__", columnWidth)); 
                } 
                else {
                    row.append(" ".repeat(columnWidth));
                }
                
                row.append(" ");
            }
            UIGenerator.appendCenteredLine(report, row.toString());
        }

        // X-axis
        StringBuilder axis = new StringBuilder();
        axis.append(" ".repeat(yAxisWidth + 1)).append("|");
        for (int i = 0; i < size; i++) {
            axis.append("-".repeat(columnWidth)).append("-");
        }
        UIGenerator.appendCenteredLine(report, axis.toString());

        // Labels
        StringBuilder labelRow = new StringBuilder();
        labelRow.append(" ".repeat(yAxisWidth));
        for (int i = 0; i < size; i++) {
            labelRow.append(" ").append(UIGenerator.centerText(labels[i], columnWidth));
        }
        UIGenerator.appendCenteredLine(report, labelRow.toString());
        report.append("\n");
    }
}
