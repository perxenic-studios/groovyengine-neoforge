package io.github.luckymcdev.groovyengine.threads.core.scripting.error;

import io.github.luckymcdev.groovyengine.threads.client.screen.ThreadsErrorScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(value = Dist.CLIENT)
public class ScriptErrors {

    private static final List<ErrorEntry> errors = new ArrayList<>();

    /**
     * Adds an error to the list of errors. This error will be displayed on the errors screen.
     * @param scriptName the name of the script that caused the error
     * @param message a brief message describing the error
     * @param description a detailed description of the error
     */
    public static void addError(String scriptName, String message, String description) {
        errors.add(new ErrorEntry(scriptName, message, description));
    }

    /**
     * Returns a list of errors stored in the error list.
     * @return a copy of the error list.
     */
    public static List<ErrorEntry> getErrors() {
        return new ArrayList<>(errors);
    }

    /**
     * Checks if there are any errors stored in the error list.
     * @return true if there are any errors, false otherwise.
     */
    public static boolean hasErrors() {
        return !errors.isEmpty();
    }

    /**
     * Clears the list of script errors.
     * This method is used to remove all previously stored errors.
     */
    public static void clear() {
        errors.clear();
    }


    /**
     * Handles the client tick event by checking if there are any script errors stored in the error list.
     * If there are errors, it sets the current screen to the errors screen.
     * @param event The client tick event to handle.
     */
    @SubscribeEvent
    private static void handleErrors(ClientTickEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        if (hasErrors()) {
            mc.setScreen(new ThreadsErrorScreen());
        }
    }

    /**
     * Generates a human-readable description of an exception.
     * This method takes an exception and returns a string describing the error in a way that is easy to understand.
     * If the exception is a known type, it will return a specific error message.
     * If the exception is unknown, it will return a generic error message.
     * @param ex the exception to generate a description for
     * @return a human-readable description of the error
     */
    public static String generateErrorDescription(Exception ex) {
        if (ex instanceof groovy.lang.MissingPropertyException) {
            return "A variable or property used in the script is missing. Check spelling and imports.";
        } else if (ex instanceof groovy.lang.MissingMethodException) {
            return "A function or method is not defined. Verify function names and parameters.";
        } else if (ex instanceof java.lang.NullPointerException) {
            return "A null value was accessed. Ensure objects are initialized before use.";
        } else if (ex instanceof java.lang.ClassCastException) {
            return "An object was cast to an incompatible type. Check your type conversions.";
        } else if (ex instanceof java.lang.ArithmeticException) {
            return "Illegal arithmetic operation (like division by zero). Check calculations.";
        }

        return ex.getMessage();
    }

    /**
     * A record to keep track of a script error.
     *
     * @param scriptName the name of the script where the error happened
     * @param message the message of the error
     * @param description the definition of the error
     */
    public record ErrorEntry(String scriptName, String message, String description) {
    }
}
