package io.github.luckymcdev.groovyengine.threads.core.scripting.error;

import io.github.luckymcdev.groovyengine.threads.client.screen.ThreadsErrorScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber
public class ScriptErrors {

    public static class ErrorEntry {
        public final String scriptName;
        public final String message;
        public final String description;

        public ErrorEntry(String scriptName, String message, String description) {
            this.scriptName = scriptName;
            this.message = message;
            this.description = description;
        }
    }

    private static final List<ErrorEntry> errors = new ArrayList<>();

    public static void addError(String scriptName, String message, String description) {
        errors.add(new ErrorEntry(scriptName, message, description));
    }

    public static List<ErrorEntry> getErrors() {
        return new ArrayList<>(errors);
    }

    public static boolean hasErrors() {
        return !errors.isEmpty();
    }

    public static void clear() {
        errors.clear();
    }

    @SubscribeEvent
    private static void handleErrors(ClientTickEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        if(hasErrors()) {
            mc.setScreen(new ThreadsErrorScreen());
        }
    }

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

        return "See logs for details.";
    }
}
