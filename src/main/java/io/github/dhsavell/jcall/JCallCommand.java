package io.github.dhsavell.jcall;

import picocli.CommandLine;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "jcall")
public class JCallCommand implements Callable<Integer> {
    @CommandLine.Parameters(index = "0")
    private String fullyQualifiedClassName;

    @CommandLine.Parameters(index = "1")
    private String methodName;

    @CommandLine.Parameters(index = "2")
    private MethodType methodType;

    @CommandLine.Parameters(index = "3")
    private List<String> methodArgs;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new JCallCommand())
                .registerConverter(MethodType.class, MethodTypeConverter.forSystemClassLoader())
                .execute(args);

        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        if (methodType.parameterCount() != methodArgs.size()) {
            System.err.printf(
                    "Arity mismatch: received %d arguments but method descriptor %s specifies %d%n",
                    methodArgs.size(),
                    methodType.toMethodDescriptorString(),
                    methodType.parameterCount());
            return 1;
        }

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        Class<?> target = Class.forName(fullyQualifiedClassName);
        MethodHandle handle = lookup.findStatic(target, methodName, methodType);

        List<Object> argValues = new ArrayList<>();
        List<String> argSources = new ArrayList<>();

        for (int i = 0; i < methodType.parameterCount(); i++) {
            Class<?> paramType = methodType.parameterType(i);
            String argText = methodArgs.get(i);

            if (paramType.equals(String.class)) {
                argValues.add(argText);
                argSources.add("\"" + argText + "\"");
            } else {
                throw new IllegalArgumentException(String.format("Can't figure out how to derive %s from a String", paramType.getName()));
            }
        }

        System.out.printf("%s.%s(%s);%n", fullyQualifiedClassName, methodName, String.join(", ", argSources));
        System.out.println();

        try {
            System.out.println(handle.invokeWithArguments(argValues));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        return 0;
    }
}
