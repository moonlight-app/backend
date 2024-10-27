package ru.moonlightapp.backend.docs;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.method.HandlerMethod;
import ru.moonlightapp.backend.docs.annotation.GenericErrorResponse;
import ru.moonlightapp.backend.docs.annotation.Note;
import ru.moonlightapp.backend.docs.annotation.SuccessResponse;
import ru.moonlightapp.backend.model.ErrorModel;
import ru.moonlightapp.backend.model.FieldValidationErrorModel;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public final class DocsOperationCustomizer implements OperationCustomizer {

    @Override
    public Operation customize(Operation operation, HandlerMethod method) {
        remove418Response(operation);

        List<String> errorDescriptions = new ArrayList<>();
        List<String> notes = new ArrayList<>();

        SuccessResponse successResponse = method.getMethodAnnotation(SuccessResponse.class);
        if (successResponse != null)
            addSuccessResponse(operation, successResponse.value(), successResponse.canBeEmpty());

        boolean hasValidationError = Arrays.stream(method.getMethodParameters()).anyMatch(p -> p.hasParameterAnnotation(Valid.class));

        GenericErrorResponse genericErrorResponse = method.getMethodAnnotation(GenericErrorResponse.class);
        String[] genericErrorCodes = genericErrorResponse != null ? genericErrorResponse.value() : null;
        boolean hasGenericError = genericErrorCodes != null && genericErrorCodes.length != 0;

        if (hasValidationError || hasGenericError)
            addBadRequestResponse(operation, hasValidationError, hasGenericError);

        Note note = method.getMethodAnnotation(Note.class);
        if (note != null)
            notes.addAll(List.of(note.value()));

        if (hasGenericError) {
            List<String> codes = new ArrayList<>(Arrays.asList(genericErrorCodes));
            codes.sort(String::compareToIgnoreCase);

            Map<String, DescribedError> describes = DescribedError.findDescribes(method);
            for (String code : codes) {
                DescribedError describe = describes.get(code);
                if (describe != null) {
                    errorDescriptions.add("| %s | %s | %s | %s |".formatted(
                            describe.system() ? "**`%s`**".formatted(code) : "`%s`".formatted(code),
                            describe.userMessage(),
                            describe.system() ? "✓" : "",
                            describe.payload()
                    ));
                } else {
                    errorDescriptions.add("| `%s` | — |  | — |".formatted(code));
                }
            }
        }

        PostMapping postMapping = method.getMethodAnnotation(PostMapping.class);
        if (postMapping != null && ArrayUtils.contains(postMapping.consumes(), "multipart/form-data")) {
            Set<String> formDataParams = new HashSet<>();

            for (MethodParameter parameter : method.getMethodParameters())
                if (parameter.getParameterAnnotations() == null || parameter.getParameterAnnotations().length == 0)
                    formDataParams.add(parameter.getParameter().getName());

            if (formDataParams != null) {
                List<Parameter> parameters = operation.getParameters();
                if (parameters != null) {
                    for (Parameter parameter : parameters) {
                        if (parameter.getName() != null && formDataParams.contains(parameter.getName())) {
                            appendDescription(parameter, "Часть тела запроса `multipart/form-data`");
                        }
                    }
                }
            }
        }

        if (!errorDescriptions.isEmpty()) {
            String text = String.join("\n", errorDescriptions);
            appendDescription(operation, """
                    ## Возможные ошибки
                    | Код | Описание | Системная? | `payload` |
                    | --- | -------- | :--------: | --------- |
                    %s
                    """.formatted(text)
            );
        }

        if (!notes.isEmpty()) {
            String text = notes.stream().map("- %s"::formatted).collect(Collectors.joining("\n"));
            appendDescription(operation, """
                    ## Заметки
                    %s""".formatted(text)
            );
        }

        return operation;
    }

    public void addSuccessResponse(Operation operation, String description, boolean canBeEmpty) {
        apiResponse(operation, "200", description);

        if (canBeEmpty) {
            apiResponse(operation, "204", "Содержимое отсутствует");
        }
    }

    public void addBadRequestResponse(Operation operation, boolean hasValidationError, boolean hasGenericError) {
        ApiResponse apiResponse = apiResponse(operation, "400", "Неверный запрос");

        Content content = apiResponse.getContent();
        if (content == null) {
            content = new Content();
            apiResponse.setContent(content);
        }

        MediaType mediaType = content.get("application/json");
        if (mediaType == null) {
            mediaType = new MediaType();
            content.addMediaType("application/json", mediaType);
        }

        if (hasValidationError) {
            mediaType.addExamples("Ошибка валидации", new Example().value(new FieldValidationErrorModel("...", "...")));
        }

        if (hasGenericError) {
            mediaType.addExamples("Ошибка с кодом", new Example().value(new ErrorModel("<код ошибки>", "...")));
        }
    }

    private static void remove418Response(Operation operation) {
        ApiResponses responses = operation.getResponses();
        if (responses != null) {
            responses.remove("418");
        }
    }

    private static void appendDescription(Operation operation, String appended) {
        appendDescription(operation::getDescription, operation::setDescription, appended);
    }

    private static void appendDescription(Parameter parameter, String appended) {
        appendDescription(parameter::getDescription, parameter::setDescription, appended);
    }

    private static void appendDescription(Supplier<String> getter, Consumer<String> setter, String appended) {
        String description = getter.get();

        if (description != null) {
            description += "\n" + appended;
        } else {
            description = appended;
        }

        setter.accept(description);
    }

    private static ApiResponse apiResponse(Operation operation, String name, String description) {
        ApiResponse response = apiResponses(operation).get(name);
        if (response != null)
            return response;

        response = new ApiResponse().description(description);
        apiResponses(operation).addApiResponse(name, response);
        return response;
    }

    private static ApiResponses apiResponses(Operation operation) {
        ApiResponses responses = operation.getResponses();
        if (responses != null)
            return responses;

        responses = new ApiResponses();
        operation.setResponses(responses);
        return responses;
    }

}