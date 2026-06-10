package io.flavien.demo.api.configuration

import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar

/**
 * Registers the api classpath resources that GraalVM native images must embed.
 *
 * The bundled OpenAPI specification is read by [OpenAPIConfiguration] through the
 * swagger parser, a mechanism invisible to the Spring AOT engine.
 */
class ApiRuntimeHints : RuntimeHintsRegistrar {
    override fun registerHints(
        hints: RuntimeHints,
        classLoader: ClassLoader?,
    ) {
        hints.resources().registerPattern("openapi.json")
    }
}
