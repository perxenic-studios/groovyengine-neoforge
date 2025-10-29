# Scripts

A script is a Groovy file (`.groovy`) that contains code executed by the engine. For a general overview, see the [HomePage](HomePage.md).

Scripts can be written in an object-oriented style or as a simple sequence of method calls.

## Basic Example

Here is a fundamental example of a script:

````groovy
package client

import io.github.luckymcdev.groovyengine.GE

GE.LOG.info('Hello from a script!')
````

This script begins with a package declaration, `package client`. This corresponds to its location in the project's folder structure, meaning the file resides at `workspace/src/main/groovy/client/`. For more details, see [Folder Structure](FolderStructure.md).

While it's not mandatory to declare a package, it is highly recommended for better organization.

## Dependencies and Load Order

Scripts can import and use code from other scripts, creating a dependency. To manage this, you must control the order in which scripts are loaded.

This is handled using a `priority` value, which is defined as a comment in the script file. See [ScriptMetaData](ScriptMetaData.md) for more details.

**Key Rule:** A script with a higher priority number loads **earlier**. If no priority is set, it defaults to `0`.

### Example

Imagine you have two scripts. The first is a utility script that provides a function:

**File: `workspace/src/main/groovy/client/test/MyUtil.groovy`**
```groovy
//priority=10
package client.test

static String getMessage() {
    return "HELLO!"
}
```

And a second script that uses the first one:

**File: `workspace/src/main/groovy/client/Main.groovy`**
```groovy
//priority=0
package client

import client.test.MyUtil
import io.github.luckymcdev.groovyengine.GE

GE.LOG.info("Message from util: " + MyUtil.getMessage())
```

When the scripts are loaded, the output will be:
`Message from util: HELLO!`

This works because `MyUtil.groovy` has a higher priority (`10`) than `Main.groovy` (`0`), ensuring it is loaded first and is available to be imported. If the priorities were reversed or equal, the engine would throw an error because `MyUtil` would not have been loaded when `Main.groovy` needed it.
