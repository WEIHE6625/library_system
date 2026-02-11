package com.mycompany.library_system.utility;
import java.io.File;

public class MessageUI {
    public static void displayInvalidChoiceMessage() {
        System.out.println("\nInvalid choice");
    }
    public static void displayExitMessage() {
        System.out.println("\nExiting system");
    }
    public static boolean fileExists(String path) {
        return new File(path).exists();
    }
}
