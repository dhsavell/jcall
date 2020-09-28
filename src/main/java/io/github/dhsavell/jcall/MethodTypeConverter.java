package io.github.dhsavell.jcall;

import picocli.CommandLine;

import java.lang.invoke.MethodType;

public class MethodTypeConverter implements CommandLine.ITypeConverter<MethodType> {
    private final ClassLoader classLoader;

    private MethodTypeConverter(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public static MethodTypeConverter forSystemClassLoader() {
        return new MethodTypeConverter(ClassLoader.getSystemClassLoader());
    }

    @Override
    public MethodType convert(String value) throws IllegalArgumentException, TypeNotPresentException {
        return MethodType.fromMethodDescriptorString(value, classLoader);
    }
}
