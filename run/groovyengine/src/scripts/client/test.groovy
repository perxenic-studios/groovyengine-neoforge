import io.github.luckymcdev.groovyengine.GE
import java.io.File

// List of closures, each triggers one error
[
        { def obj = null; obj.toString() },                        // NullPointerException
        //{ int x = 10 / 0 },                                       // ArithmeticException
        { def arr = [1,2,3]; GE.LOG.info(arr[5].toString()) },    // ArrayIndexOutOfBoundsException
        { Object s = "string"; Integer i = (Integer) s },         // ClassCastException
        { Thread.sleep(-10) },                                    // IllegalArgumentException
        { new File("nonexistent.txt").text },                     // FileNotFoundException
        { assert false }                                          // AssertionError
].each { it() }  // execute each closure
