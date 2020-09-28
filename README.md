# jcall

Call arbitrary JVM methods from the command line.

It was helpful for a niche reverse-engineering project that never went
anywhere, and probably won't ever be useful for much else. Pretty fun to
write, though! 

## Usage

Note: You'll probably want to pair this with some JVM configuration, like
setting the classpath.

### Call Static Method

```sh
$ jcall <fq class name> <method name> <method descriptor> <args>
```

```sh
$ jcall java.lang.Integer parseInt (Ljava/lang/String;)I 12345
java.lang.Integer.parseInt("12345");

12345
```
