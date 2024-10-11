package ru.cod331n;

import ru.cod331n.annotation.reflect.JavaClassesReflection;
import ru.cod331n.annotation.starter.AnnotationProcessingBootstrap;

public class Main {

    public static void main(String[] args) {
        AnnotationProcessingBootstrap.run("ru.cod331n");

        JavaClassesReflection reflection = new JavaClassesReflection("ru.cod331n", null);
    }


}
