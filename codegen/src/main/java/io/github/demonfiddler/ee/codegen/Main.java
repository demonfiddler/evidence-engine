package io.github.demonfiddler.ee.codegen;

import java.util.Collections;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;

public class Main {

    private static final ParseOptions OPTIONS;

    static {
        OPTIONS = new ParseOptions();
    }

    public static void main(String... args) {
        SwaggerParseResult result = new OpenAPIParser().readLocation(args[0], Collections.emptyList(), OPTIONS);
        if (!result.getMessages().isEmpty()) {
            for (String msg : result.getMessages())
                System.err.println(msg);
        }
    }

}
