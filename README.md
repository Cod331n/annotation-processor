# Annotation Processor
Annotation Processor — это библиотека для обработки аннотаций в рантайме, разработанная на Java. Она предназначена для упрощения работы с кодом на основе аннотаций в проекте.

## Установка
Для того чтобы использовать этот процессор аннотаций в своем проекте, необходимо добавить его зависимость в `build.gradle.kts`.

```kotlin
dependencies {
    annotationProcessor("com.github.Cod331n:annotation-processor:<version>")
}
```

## Использование
1. Создайте аннотацию, обязательно с ```@Retention(RetentionPolicy.RUNTIME)```;
2. Создайте обработчик аннотации, наследуемый от класса ```AbstractAnnotationProcessor```, указав аннотацию, которую вы обрабатываете;
3. Отметьте созданный обработчик аннотации аннотацией ```@AnnotationProcessor```;
4. В главном классе проекта укажите обрабатываемые пакеты (или пакет), в котором находится аннотация ```@AnnotationProcessor```.

## Пример использования
Создадим планировщик, который будет выполнять повторяющиеся действия. Чтобы его не регистрировать где-то, создадим гибкую структуру, через аннотацию. 
В её процессоре будем создавать новый объект класса и выполнять с ним действия.
```kotlin
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExampleScheduler {
}

@Getter
@ExampleScheduler
public class Scheduler {

    private final int delay;
    private final int period;

    public void schedule() { // logic };
}

@AnnotationProcessor
public class ExampleSchedulerAnnotationProcessor extends AbstractAnnotationProcessor<ExampleScheduler> {
    @Override
    public void process(@Nullable Class<?> clazz, @NotNull AnnotatedElement element, @NotNull SimpleGameScheduler annotation) {
        try {
            Scheduler scheduler = (Scheduler) clazz.newInstance();
            // example
            Tasks.every(gameScheduler.getDelay(), gameScheduler.getPeriod(), task -> gameScheduler.schedule());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}

public class Main {
    public static void main(String[] args) {
        AnnotationProcessBootstrap.run("ru.cod331n", null); // если не работает, то classLoader необходимо передать в параметр (например this.getClassLoader())
    }
}
```
