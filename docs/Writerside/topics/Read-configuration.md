# Read configuration

By default, plugin creates a class with name `BytemaskConfig`.
If you've configured the different name
from the [configuration](Configure.md "Configure custom file name and class name"), then you'll have to use that class.

## Example

If properties file is like:

```Generic
API_KEY=Hello1234
```

In code, you can access it like

<tabs>
<code-block lang="kotlin">
fun example() {
    val apiKey = BytemaskConfig.API_KEY
}
</code-block>
<code-block lang="java">
void example() {
    String apiKey = BytemaskConfig.apiKey()
}
</code-block>
</tabs>

![](ConfigAndGeneratedCode.png)