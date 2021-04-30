# kotlin-native-performance-experiment

This repo, reproduces the KorGE bunnymark update code that is slow on Kotlin/Native, except when marking all the methods as inline (that hurts performance on JVM and JS).

```
> Task :runReleaseExecutableNative
Executing INLINED...
Executed INLINED sixty frames in 796ms
Executing NON-INLINED-BUT-CONST...
Executed NON-INLINED-BUT-CONST sixty frames in 1.03s
Executing NON-INLINED...
Executed NON-INLINED sixty frames in 26.8s
```
