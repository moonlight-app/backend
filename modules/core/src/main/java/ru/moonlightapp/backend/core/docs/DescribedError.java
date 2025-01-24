package ru.moonlightapp.backend.core.docs;

import org.springframework.web.method.HandlerMethod;
import ru.moonlightapp.backend.core.docs.annotation.DescribeError;
import ru.moonlightapp.backend.core.docs.annotation.DescribeErrors;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public record DescribedError(String code, String userMessage, boolean system, String payload) {

    public static Map<String, DescribedError> findDescribes(HandlerMethod method) {
        Map<String, DescribedError> result = new HashMap<>();

        Consumer<Annotation[]> annotationsConsumer = annotations -> {
            for (Annotation annotation : annotations) {
                if (annotation instanceof DescribeErrors errors) {
                    for (DescribeError error : errors.value()) {
                        addDescribe(result, error);
                    }
                }

                if (annotation instanceof DescribeError error) {
                    addDescribe(result, error);
                }
            }
        };

        annotationsConsumer.accept(method.getMethod().getDeclaringClass().getAnnotations());
        annotationsConsumer.accept(method.getMethod().getAnnotations());
        return result;
    }

    private static void addDescribe(Map<String, DescribedError> map, DescribeError annotation) {
        DescribedError object = new DescribedError(
                annotation.code(),
                annotation.message(),
                annotation.system(),
                annotation.payload()
        );

        map.compute(annotation.code(), (code, existing) -> {
            if (existing != null) {
                String userMessage = !existing.userMessage().isEmpty() ? existing.userMessage() : object.userMessage();
                return new DescribedError(code, userMessage, object.system(), object.payload());
            } else {
                return object;
            }
        });
    }

}