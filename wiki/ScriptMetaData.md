# Script Metadata

Script metadata is defined using comments at the top of your Groovy script files. This metadata allows you to control how your scripts are loaded and executed.

## Priority

The `priority` metadata determines the load order of your scripts. It is defined using a comment in the format `//priority=X`, where `X` is an integer.

**The higher the priority number, the earlier the script will be loaded.**

This is useful when you have scripts that depend on each other. For example, if `scriptA` depends on `scriptB`, you should give `scriptB` a higher priority to ensure it is loaded before `scriptA`.

### Example

**`scriptB.groovy`**
```groovy
//priority=10

class MyUtil {
    static String getMessage() {
        return "Hello from Script B!"
    }
}
```

**`scriptA.groovy`**
```groovy
//priority=5

println(MyUtil.getMessage())
```

In this example, `scriptB` will be loaded before `scriptA`, so the `MyUtil` class will be available when `scriptA` is executed.

## Disabled

To disable a script and prevent it from being loaded, add the `//disabled` comment to the top of the file.

### Example

```groovy
//disabled

// This script will not be loaded by GroovyEngine.
println("This message will never be printed.")
```
