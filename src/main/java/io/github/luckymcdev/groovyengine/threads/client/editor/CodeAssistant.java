package io.github.luckymcdev.groovyengine.threads.client.editor;

import java.util.*;

public class CodeAssistant {

    private final Map<String, List<String>> javaMethods = new HashMap<>();
    private final Map<String, List<String>> groovyMethods = new HashMap<>();
    private final Map<String, String> javaDocumentation = new HashMap<>();
    private final Map<String, String> groovyDocumentation = new HashMap<>();

    public CodeAssistant() {
        initializeJavaSupport();
        initializeGroovySupport();
    }

    private void initializeJavaSupport() {
        // System class methods
        javaMethods.put("System", Arrays.asList(
                "out.println()", "out.print()", "err.println()", "exit()", "gc()",
                "currentTimeMillis()", "nanoTime()", "getProperty()", "setProperty()"
        ));

        // String methods
        javaMethods.put("String", Arrays.asList(
                "length()", "charAt()", "substring()", "indexOf()", "lastIndexOf()",
                "toLowerCase()", "toUpperCase()", "trim()", "replace()", "split()",
                "startsWith()", "endsWith()", "contains()", "equals()", "equalsIgnoreCase()",
                "isEmpty()", "isBlank()", "valueOf()", "format()"
        ));

        // List methods
        javaMethods.put("List", Arrays.asList(
                "add()", "remove()", "get()", "set()", "size()", "isEmpty()",
                "clear()", "contains()", "indexOf()", "lastIndexOf()", "toArray()",
                "addAll()", "removeAll()", "retainAll()", "subList()", "iterator()"
        ));

        // ArrayList methods (inherits List + specific ones)
        List<String> arrayListMethods = new ArrayList<>(javaMethods.get("List"));
        arrayListMethods.addAll(Arrays.asList(
                "ensureCapacity()", "trimToSize()", "clone()"
        ));
        javaMethods.put("ArrayList", arrayListMethods);

        // Map methods
        javaMethods.put("Map", Arrays.asList(
                "put()", "get()", "remove()", "containsKey()", "containsValue()",
                "size()", "isEmpty()", "clear()", "keySet()", "values()",
                "entrySet()", "putAll()", "getOrDefault()", "putIfAbsent()"
        ));

        // HashMap methods
        List<String> hashMapMethods = new ArrayList<>(javaMethods.get("Map"));
        hashMapMethods.addAll(Arrays.asList("clone()"));
        javaMethods.put("HashMap", hashMapMethods);

        // Object methods (available on all objects)
        javaMethods.put("Object", Arrays.asList(
                "toString()", "equals()", "hashCode()", "getClass()", "clone()",
                "notify()", "notifyAll()", "wait()"
        ));

        // Thread methods
        javaMethods.put("Thread", Arrays.asList(
                "start()", "run()", "stop()", "suspend()", "resume()", "interrupt()",
                "isAlive()", "isDaemon()", "setDaemon()", "getName()", "setName()",
                "getPriority()", "setPriority()", "sleep()", "yield()", "join()"
        ));

        // Documentation for hover tooltips
        javaDocumentation.put("System", "The System class contains several useful class fields and methods. It cannot be instantiated.");
        javaDocumentation.put("String", "The String class represents character strings. All string literals are instances of this class.");
        javaDocumentation.put("List", "An ordered collection (also known as a sequence). Lists can contain duplicate elements.");
        javaDocumentation.put("ArrayList", "Resizable-array implementation of the List interface. Permits null elements.");
        javaDocumentation.put("Map", "An object that maps keys to values. A map cannot contain duplicate keys.");
        javaDocumentation.put("HashMap", "Hash table based implementation of the Map interface. Permits null values and null key.");
        javaDocumentation.put("Thread", "A thread is a thread of execution in a program. Allows concurrent execution of code.");
        javaDocumentation.put("Object", "Class Object is the root of the class hierarchy. Every class has Object as a superclass.");

        // Method documentation
        javaDocumentation.put("println", "Prints text to the standard output stream followed by a newline character.");
        javaDocumentation.put("print", "Prints text to the standard output stream without a newline character.");
        javaDocumentation.put("length", "Returns the number of characters in this string or elements in array.");
        javaDocumentation.put("size", "Returns the number of elements in this collection.");
        javaDocumentation.put("add", "Appends the specified element to the end of this list.");
        javaDocumentation.put("remove", "Removes the first occurrence of the specified element from this list.");
        javaDocumentation.put("get", "Returns the element at the specified position in this list.");
        javaDocumentation.put("set", "Replaces the element at the specified position with the specified element.");
        javaDocumentation.put("contains", "Returns true if this collection contains the specified element.");
        javaDocumentation.put("indexOf", "Returns the index of the first occurrence of the specified element.");
        javaDocumentation.put("substring", "Returns a string that is a substring of this string.");
        javaDocumentation.put("toLowerCase", "Converts all characters in this String to lower case.");
        javaDocumentation.put("toUpperCase", "Converts all characters in this String to upper case.");
        javaDocumentation.put("trim", "Returns a string whose value is this string, with leading and trailing whitespace omitted.");
        javaDocumentation.put("equals", "Compares this string to the specified object for equality.");
        javaDocumentation.put("toString", "Returns a string representation of the object.");
        javaDocumentation.put("isEmpty", "Returns true if this collection contains no elements or string has length 0.");
        javaDocumentation.put("clear", "Removes all elements from this collection.");
        javaDocumentation.put("put", "Associates the specified value with the specified key in this map.");
        javaDocumentation.put("containsKey", "Returns true if this map contains a mapping for the specified key.");
        javaDocumentation.put("keySet", "Returns a Set view of the keys contained in this map.");
        javaDocumentation.put("values", "Returns a Collection view of the values contained in this map.");
    }

    private void initializeGroovySupport() {
        // Groovy includes all Java methods plus Groovy-specific ones

        // Copy Java methods
        groovyMethods.putAll(javaMethods);
        groovyDocumentation.putAll(javaDocumentation);

        // Add Groovy-specific methods for collections
        groovyMethods.put("Collection", Arrays.asList(
                "each{}", "eachWithIndex{}", "collect{}", "findAll{}", "find{}",
                "any{}", "every{}", "sum()", "max()", "min()", "sort()", "reverse()",
                "unique()", "flatten()", "join()", "grep{}", "inject{}", "groupBy{}"
        ));

        // String extensions in Groovy
        List<String> groovyStringMethods = new ArrayList<>(javaMethods.get("String"));
        groovyStringMethods.addAll(Arrays.asList(
                "center()", "padLeft()", "padRight()", "multiply()", "reverse()",
                "tokenize()", "eachLine{}", "readLines()", "splitEachLine{}",
                "findAll{}", "replaceAll{}", "capitalize()", "uncapitalize()",
                "isNumber()", "toBigDecimal()", "toBigInteger()", "toInteger()",
                "toDouble()", "toFloat()", "toLong()", "toURL()", "toURI()"
        ));
        groovyMethods.put("String", groovyStringMethods);

        // List extensions in Groovy
        List<String> groovyListMethods = new ArrayList<>(javaMethods.get("List"));
        groovyListMethods.addAll(Arrays.asList(
                "each{}", "eachWithIndex{}", "collect{}", "findAll{}", "find{}",
                "any{}", "every{}", "sum()", "max{}", "min{}", "sort{}", "reverse()",
                "unique{}", "flatten()", "join()", "grep{}", "inject{}", "groupBy{}",
                "head()", "tail()", "init()", "last()", "first()", "take()", "drop()",
                "plus()", "minus()", "intersect()", "disjoint()", "transpose()"
        ));
        groovyMethods.put("List", groovyListMethods);

        // Map extensions in Groovy
        List<String> groovyMapMethods = new ArrayList<>(javaMethods.get("Map"));
        groovyMapMethods.addAll(Arrays.asList(
                "each{}", "eachWithIndex{}", "collect{}", "findAll{}", "find{}",
                "any{}", "every{}", "groupBy{}", "sort{}", "plus()", "minus()",
                "subMap()", "withDefault{}"
        ));
        groovyMethods.put("Map", groovyMapMethods);

        // Number extensions
        groovyMethods.put("Number", Arrays.asList(
                "times{}", "upto{}", "downto{}", "step{}", "abs()", "round()",
                "floor()", "ceil()", "trunc()", "power()", "sqrt()", "plus()",
                "minus()", "multiply()", "div()", "mod()", "negative()", "positive()",
                "intValue()", "longValue()", "floatValue()", "doubleValue()"
        ));

        // File extensions (common in Groovy scripts)
        groovyMethods.put("File", Arrays.asList(
                "text", "bytes", "readLines()", "eachLine{}", "eachByte{}",
                "eachFile{}", "eachDir{}", "eachFileRecurse{}", "eachDirRecurse{}",
                "withReader{}", "withWriter{}", "withInputStream{}", "withOutputStream{}",
                "append()", "write()", "leftShift()", "newReader()", "newWriter()",
                "newInputStream()", "newOutputStream()", "size()", "directorySize()",
                "traverse{}", "filterLine{}", "splitEachLine{}"
        ));

        // Add Groovy-specific documentation
        groovyDocumentation.put("each", "Iterates through each element in the collection, calling the closure for each item.");
        groovyDocumentation.put("collect", "Transforms each element in the collection using the closure and returns a new list.");
        groovyDocumentation.put("findAll", "Finds all elements in the collection that match the closure condition.");
        groovyDocumentation.put("find", "Finds the first element in the collection that matches the closure condition.");
        groovyDocumentation.put("any", "Returns true if any element in the collection matches the closure condition.");
        groovyDocumentation.put("every", "Returns true if every element in the collection matches the closure condition.");
        groovyDocumentation.put("sum", "Returns the sum of all elements in the collection.");
        groovyDocumentation.put("max", "Returns the maximum element in the collection.");
        groovyDocumentation.put("min", "Returns the minimum element in the collection.");
        groovyDocumentation.put("sort", "Sorts the collection and returns a new sorted list.");
        groovyDocumentation.put("reverse", "Reverses the order of elements in the collection.");
        groovyDocumentation.put("unique", "Returns a collection with duplicate elements removed.");
        groovyDocumentation.put("flatten", "Flattens nested collections into a single flat collection.");
        groovyDocumentation.put("join", "Joins all elements in the collection into a string with optional separator.");
        groovyDocumentation.put("inject", "Iteratively applies a closure to collection elements with an accumulator.");
        groovyDocumentation.put("groupBy", "Groups collection elements by the result of a closure.");
        groovyDocumentation.put("times", "Executes a closure n times, passing the current index.");
        groovyDocumentation.put("upto", "Executes a closure from this number up to the target number.");
        groovyDocumentation.put("downto", "Executes a closure from this number down to the target number.");
        groovyDocumentation.put("step", "Executes a closure stepping by the specified increment.");
        groovyDocumentation.put("def", "Dynamic type definition - variable can hold any type of object.");
        groovyDocumentation.put("it", "Default parameter name in closures when no explicit parameter is defined.");
    }

    public List<String> getSuggestions(String objectName, String language) {
        Map<String, List<String>> methodMap = language.equals("Java") ? javaMethods : groovyMethods;

        // Direct object type match
        if (methodMap.containsKey(objectName)) {
            return new ArrayList<>(methodMap.get(objectName));
        }

        // Common object patterns
        List<String> suggestions = new ArrayList<>();

        // If it looks like a string variable
        if (objectName.toLowerCase().contains("string") || objectName.toLowerCase().contains("str") ||
                objectName.toLowerCase().contains("text") || objectName.toLowerCase().contains("name")) {
            suggestions.addAll(methodMap.getOrDefault("String", Collections.emptyList()));
        }

        // If it looks like a list variable
        else if (objectName.toLowerCase().contains("list") || objectName.toLowerCase().contains("array") ||
                objectName.toLowerCase().contains("collection") || objectName.endsWith("s")) {
            suggestions.addAll(methodMap.getOrDefault("List", Collections.emptyList()));
            if (language.equals("Groovy")) {
                suggestions.addAll(methodMap.getOrDefault("Collection", Collections.emptyList()));
            }
        }

        // If it looks like a map variable
        else if (objectName.toLowerCase().contains("map") || objectName.toLowerCase().contains("dict")) {
            suggestions.addAll(methodMap.getOrDefault("Map", Collections.emptyList()));
        }

        // If it looks like a number variable
        else if (objectName.toLowerCase().contains("num") || objectName.toLowerCase().contains("count") ||
                objectName.toLowerCase().contains("size") || objectName.toLowerCase().contains("index")) {
            if (language.equals("Groovy")) {
                suggestions.addAll(methodMap.getOrDefault("Number", Collections.emptyList()));
            }
        }

        // Default to Object methods for unknown types
        if (suggestions.isEmpty()) {
            suggestions.addAll(methodMap.getOrDefault("Object", Collections.emptyList()));
        }

        // Remove duplicates and sort
        return suggestions.stream().distinct().sorted().limit(10).toList();
    }

    public List<String> getGeneralSuggestions(String language) {
        List<String> generalSuggestions = new ArrayList<>();

        if (language.equals("Java")) {
            generalSuggestions.addAll(Arrays.asList(
                    "System.out.println()", "String", "List", "ArrayList", "HashMap",
                    "public", "private", "protected", "static", "final", "class",
                    "interface", "extends", "implements", "import", "package",
                    "if", "else", "for", "while", "do", "switch", "case", "break",
                    "continue", "return", "try", "catch", "finally", "throw", "throws",
                    "new", "this", "super", "null", "true", "false"
            ));
        } else if (language.equals("Groovy")) {
            generalSuggestions.addAll(Arrays.asList(
                    "println", "def", "String", "List", "Map", "each", "collect",
                    "findAll", "find", "any", "every", "sum", "max", "min",
                    "public", "private", "protected", "static", "final", "class",
                    "interface", "extends", "implements", "import", "package",
                    "if", "else", "for", "while", "do", "switch", "case", "break",
                    "continue", "return", "try", "catch", "finally", "throw", "throws",
                    "new", "this", "super", "null", "true", "false", "it", "as", "in"
            ));
        }

        return generalSuggestions;
    }

    public String getHoverInfo(String identifier, String language) {
        Map<String, String> docMap = language.equals("Java") ? javaDocumentation : groovyDocumentation;

        // Direct match
        if (docMap.containsKey(identifier)) {
            return docMap.get(identifier);
        }

        // Method name match (remove parentheses)
        String methodName = identifier.replaceAll("\\(.*\\)", "");
        if (docMap.containsKey(methodName)) {
            return docMap.get(methodName);
        }

        // Common keywords
        switch (identifier.toLowerCase()) {
            case "public":
                return "Access modifier: visible to all classes";
            case "private":
                return "Access modifier: visible only within the same class";
            case "protected":
                return "Access modifier: visible to package and subclasses";
            case "static":
                return "Belongs to the class rather than instance";
            case "final":
                return "Cannot be changed, overridden, or extended";
            case "class":
                return "Defines a new class type";
            case "interface":
                return "Defines a contract that classes can implement";
            case "extends":
                return "Inherits from another class";
            case "implements":
                return "Provides implementation for an interface";
            case "import":
                return "Makes classes from other packages available";
            case "package":
                return "Defines the namespace for classes";
            case "if":
                return "Conditional statement for branching logic";
            case "else":
                return "Alternative branch in conditional statement";
            case "for":
                return "Loop statement for iteration";
            case "while":
                return "Loop statement with pre-condition";
            case "do":
                return "Loop statement with post-condition";
            case "switch":
                return "Multi-way branch statement";
            case "case":
                return "Branch option in switch statement";
            case "break":
                return "Exits loop or switch statement";
            case "continue":
                return "Skips to next iteration of loop";
            case "return":
                return "Returns value from method";
            case "try":
                return "Begins exception handling block";
            case "catch":
                return "Handles specific exception types";
            case "finally":
                return "Code that always executes after try/catch";
            case "throw":
                return "Throws an exception";
            case "throws":
                return "Declares exceptions that method might throw";
            case "new":
                return "Creates new instance of a class";
            case "this":
                return "Reference to current object instance";
            case "super":
                return "Reference to parent class";
            case "null":
                return "Represents absence of value";
            case "true":
            case "false":
                return "Boolean literal value";
            default:
                return ""; // No documentation available
        }
    }
}