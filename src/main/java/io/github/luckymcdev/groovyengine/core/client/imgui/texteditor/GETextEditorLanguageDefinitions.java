package io.github.luckymcdev.groovyengine.core.client.imgui.texteditor;

import imgui.extension.texteditor.TextEditorLanguageDefinition;
import imgui.extension.texteditor.flag.TextEditorPaletteIndex;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Map;
import java.util.HashMap;

@OnlyIn(Dist.CLIENT)
public class GETextEditorLanguageDefinitions {
    private static final TextEditorLanguageDefinition javaDef = new TextEditorLanguageDefinition();
    private static final TextEditorLanguageDefinition groovyDef = new TextEditorLanguageDefinition();

    static {
        setupJavaDefinition();
        setupGroovyDefinition();
    }

    private static void setupJavaDefinition() {
        javaDef.setName("Java");

        // Comprehensive Java keywords
        javaDef.setKeywords(new String[]{
                "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char",
                "class", "const", "continue", "default", "do", "double", "else", "enum",
                "extends", "final", "finally", "float", "for", "goto", "if", "implements",
                "import", "instanceof", "int", "interface", "long", "native", "new",
                "package", "private", "protected", "public", "return", "short", "static",
                "strictfp", "super", "switch", "synchronized", "this", "throw", "throws",
                "transient", "try", "void", "volatile", "while", "true", "false", "null"
        });

        // Enhanced identifiers with documentation
        Map<String, String> identifiers = new HashMap<>();

        // Java standard library
        identifiers.put("System", "Provides access to system resources and standard I/O");
        identifiers.put("String", "Immutable sequence of characters");
        identifiers.put("Object", "The root class of the Java class hierarchy");
        identifiers.put("List", "Ordered collection interface");
        identifiers.put("ArrayList", "Resizable array implementation of List");
        identifiers.put("HashMap", "Hash table based implementation of Map");
        identifiers.put("Scanner", "Simple text scanner for parsing primitives and strings");
        identifiers.put("File", "Abstract representation of file and directory pathnames");
        identifiers.put("Thread", "Execution thread in a program");
        identifiers.put("Exception", "Base class for all exceptions");
        identifiers.put("RuntimeException", "Base class for unchecked exceptions");
        identifiers.put("IOException", "Exception thrown when I/O operations fail");

        // Common methods
        identifiers.put("println", "Prints text followed by a newline");
        identifiers.put("print", "Prints text without a newline");
        identifiers.put("length", "Returns the length of a string or array");
        identifiers.put("size", "Returns the number of elements in a collection");
        identifiers.put("get", "Retrieves an element at specified index/key");
        identifiers.put("set", "Sets an element at specified index/key");
        identifiers.put("add", "Adds an element to a collection");
        identifiers.put("remove", "Removes an element from a collection");
        identifiers.put("equals", "Compares objects for equality");
        identifiers.put("toString", "Returns string representation of object");
        identifiers.put("valueOf", "Converts primitive to wrapper object");
        identifiers.put("parseInt", "Parses string to integer");
        identifiers.put("parseDouble", "Parses string to double");

        javaDef.setIdentifiers(identifiers);

        // Comments
        javaDef.setSingleLineComment("//");
        javaDef.setCommentStart("/*");
        javaDef.setCommentEnd("*/");
        javaDef.setPreprocChar('@');
        javaDef.setAutoIndentation(true);

        // Enhanced regex patterns for syntax highlighting
        Map<String, Integer> tokenRegex = new HashMap<>();
        tokenRegex.put("\\b\\d+\\.\\d+[fFdD]?\\b", TextEditorPaletteIndex.Number); // Floating point
        tokenRegex.put("\\b\\d+[lLfFdD]?\\b", TextEditorPaletteIndex.Number); // Integers with suffixes
        tokenRegex.put("\\b0[xX][0-9a-fA-F]+\\b", TextEditorPaletteIndex.Number); // Hexadecimal
        tokenRegex.put("\\b0[bB][01]+\\b", TextEditorPaletteIndex.Number); // Binary
        tokenRegex.put("\"([^\"\\\\]|\\\\.)*\"", TextEditorPaletteIndex.String); // String literals
        tokenRegex.put("'([^'\\\\]|\\\\.)*'", TextEditorPaletteIndex.CharLiteral); // Char literals
        tokenRegex.put("@\\w+", TextEditorPaletteIndex.Preprocessor); // Annotations
        tokenRegex.put("\\b[A-Z][a-zA-Z0-9_]*\\b", TextEditorPaletteIndex.KnownIdentifier); // Class names

        javaDef.setTokenRegexStrings(tokenRegex);
    }

    private static void setupGroovyDefinition() {
        groovyDef.setName("Groovy");

        // Groovy keywords (includes Java + Groovy specific)
        groovyDef.setKeywords(new String[]{
                "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char",
                "class", "const", "continue", "def", "default", "do", "double", "else",
                "enum", "extends", "final", "finally", "float", "for", "goto", "if",
                "implements", "import", "in", "instanceof", "int", "interface", "long",
                "native", "new", "package", "private", "protected", "public", "return",
                "short", "static", "strictfp", "super", "switch", "synchronized", "this",
                "throw", "throws", "trait", "transient", "try", "void", "volatile",
                "while", "true", "false", "null", "it", "as"
        });

        // Groovy-specific identifiers
        Map<String, String> groovyIdentifiers = new HashMap<>();
        groovyIdentifiers.put("def", "Dynamic type definition - can be any type");
        groovyIdentifiers.put("it", "Default parameter in closures");
        groovyIdentifiers.put("this", "Reference to current object");
        groovyIdentifiers.put("super", "Reference to parent class");
        groovyIdentifiers.put("println", "Prints text followed by newline (Groovy built-in)");
        groovyIdentifiers.put("print", "Prints text without newline");
        groovyIdentifiers.put("each", "Iterates over collection elements");
        groovyIdentifiers.put("collect", "Transforms collection elements");
        groovyIdentifiers.put("findAll", "Filters collection elements");
        groovyIdentifiers.put("find", "Finds first matching element");
        groovyIdentifiers.put("size", "Returns collection size");
        groovyIdentifiers.put("length", "Returns string/array length");

        groovyDef.setIdentifiers(groovyIdentifiers);

        groovyDef.setSingleLineComment("//");
        groovyDef.setCommentStart("/*");
        groovyDef.setCommentEnd("*/");
        groovyDef.setPreprocChar('@');
        groovyDef.setAutoIndentation(true);

        // Groovy-specific regex patterns
        Map<String, Integer> groovyTokenRegex = new HashMap<>();
        groovyTokenRegex.put("\\b\\d+\\.\\d+[fFdDgG]?\\b", TextEditorPaletteIndex.Number);
        groovyTokenRegex.put("\\b\\d+[lLfFdDgG]?\\b", TextEditorPaletteIndex.Number);
        groovyTokenRegex.put("\"([^\"\\\\]|\\\\.)*\"", TextEditorPaletteIndex.String);
        groovyTokenRegex.put("'([^'\\\\]|\\\\.)*'", TextEditorPaletteIndex.String); // Single quotes are strings in Groovy
        groovyTokenRegex.put("/([^/\\\\]|\\\\.)*/.+", TextEditorPaletteIndex.String); // Regex literals
        groovyTokenRegex.put("\\$\\{[^}]*\\}", TextEditorPaletteIndex.String); // String interpolation
        groovyTokenRegex.put("@\\w+", TextEditorPaletteIndex.Preprocessor);

        groovyDef.setTokenRegexStrings(groovyTokenRegex);
    }

    public static TextEditorLanguageDefinition getJavaDef() {
        return javaDef;
    }

    public static TextEditorLanguageDefinition getGroovyDef() {
        return groovyDef;
    }
}