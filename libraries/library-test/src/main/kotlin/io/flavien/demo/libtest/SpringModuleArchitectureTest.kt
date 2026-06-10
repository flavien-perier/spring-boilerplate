package io.flavien.demo.libtest

import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.library.GeneralCodingRules
import org.slf4j.Logger

@Suppress("ktlint:standard:property-naming")
abstract class SpringModuleArchitectureTest {
    @ArchTest
    val `logger fields should be private static final and named log`: ArchRule =
        fields()
            .that()
            .haveRawType(Logger::class.java)
            .should()
            .bePrivate()
            .andShould()
            .beStatic()
            .andShould()
            .beFinal()
            .andShould()
            .haveName("log")
            .because("Logger declarations must be uniform: `private val log` in a companion object")

    @ArchTest
    val `no classes should access standard streams`: ArchRule = GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS

    @ArchTest
    val `no classes should use field injection`: ArchRule = GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION

    @ArchTest
    val `no classes should use java util logging`: ArchRule = GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING

    @ArchTest
    val `classes named Configuration should be annotated with @Configuration`: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("Configuration")
            .and()
            .areNotInterfaces()
            .should()
            .beAnnotatedWith("org.springframework.context.annotation.Configuration")
            .because("Classes named *Configuration must be annotated with @Configuration")
            .allowEmptyShould(true)

    @ArchTest
    val `classes named Properties should be annotated with @ConfigurationProperties`: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("Properties")
            .and()
            .areNotInterfaces()
            .should()
            .beAnnotatedWith("org.springframework.boot.context.properties.ConfigurationProperties")
            .because("Classes named *Properties must be annotated with @ConfigurationProperties")
            .allowEmptyShould(true)

    @ArchTest
    val `@ConfigurationProperties classes should reside in the config properties package`: ArchRule =
        classes()
            .that()
            .areAnnotatedWith("org.springframework.boot.context.properties.ConfigurationProperties")
            .should()
            .resideInAPackage("..configuration.properties..")
            .because("@ConfigurationProperties classes must be organized in the config.properties sub-package")
            .allowEmptyShould(true)

    @ArchTest
    val `classes in exception packages should extend RuntimeException`: ArchRule =
        classes()
            .that()
            .resideInAPackage("..exception..")
            .and()
            .areNotInterfaces()
            .should()
            .beAssignableTo(RuntimeException::class.java)
            .because("All classes in exception packages must extend RuntimeException")
            .allowEmptyShould(true)

    @ArchTest
    val `classes in exception packages should have names ending with Exception`: ArchRule =
        classes()
            .that()
            .resideInAPackage("..exception..")
            .and()
            .areNotInterfaces()
            .should()
            .haveSimpleNameEndingWith("Exception")
            .because("Exception classes must follow the naming convention *Exception")
            .allowEmptyShould(true)

    @ArchTest
    val `configuration properties should be immutable`: ArchRule =
        fields()
            .that()
            .areDeclaredInClassesThat()
            .areAnnotatedWith("org.springframework.boot.context.properties.ConfigurationProperties")
            .should()
            .beFinal()
            .because("@ConfigurationProperties classes use constructor binding — mutable properties hide configuration errors")
            .allowEmptyShould(true)

    @ArchTest
    val `entities, models and exceptions should not depend on services or repositories`: ArchRule =
        noClasses()
            .that()
            .resideInAnyPackage("..entity..", "..model..", "..exception..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("..service..", "..repository..")
            .because("Entities, models and exceptions are the lowest layer of the domain")
            .allowEmptyShould(true)

    @ArchTest
    val `@Transactional should only be used in service classes`: ArchRule =
        methods()
            .that()
            .areAnnotatedWith("org.springframework.transaction.annotation.Transactional")
            .should()
            .beDeclaredInClassesThat()
            .areAnnotatedWith("org.springframework.stereotype.Service")
            .because("Transaction boundaries must only be declared in @Service classes, not in repositories, entities, or configurations")
            .allowEmptyShould(true)

    @ArchTest
    val `classes in model packages should not be annotated with Spring stereotypes`: ArchRule =
        noClasses()
            .that()
            .resideInAPackage("..model..")
            .should()
            .beAnnotatedWith("org.springframework.stereotype.Service")
            .orShould()
            .beAnnotatedWith("org.springframework.stereotype.Repository")
            .orShould()
            .beAnnotatedWith("org.springframework.stereotype.Component")
            .orShould()
            .beAnnotatedWith("org.springframework.stereotype.Controller")
            .because("Model classes are plain data holders and must not be Spring-managed beans")
            .allowEmptyShould(true)

    @ArchTest
    val `@Bean methods should only be declared in @Configuration classes`: ArchRule =
        methods()
            .that()
            .areAnnotatedWith("org.springframework.context.annotation.Bean")
            .should()
            .beDeclaredInClassesThat()
            .areAnnotatedWith("org.springframework.context.annotation.Configuration")
            .because("@Bean factory methods must only appear inside @Configuration classes")
            .allowEmptyShould(true)

    @ArchTest
    val `classes in entity packages should not be annotated with Spring stereotypes`: ArchRule =
        noClasses()
            .that()
            .resideInAPackage("..entity..")
            .should()
            .beAnnotatedWith("org.springframework.stereotype.Service")
            .orShould()
            .beAnnotatedWith("org.springframework.stereotype.Repository")
            .orShould()
            .beAnnotatedWith("org.springframework.stereotype.Component")
            .orShould()
            .beAnnotatedWith("org.springframework.stereotype.Controller")
            .because("Entity classes are plain persistence objects and must not be Spring-managed beans")
            .allowEmptyShould(true)

    @ArchTest
    val `classes annotated with @Transactional should be annotated with @Service`: ArchRule =
        classes()
            .that()
            .areAnnotatedWith("org.springframework.transaction.annotation.Transactional")
            .should()
            .beAnnotatedWith("org.springframework.stereotype.Service")
            .because("Class-level transaction boundaries must only be declared on @Service classes")
            .allowEmptyShould(true)

    @ArchTest
    val `@Configuration classes should have names ending with Config or Configuration`: ArchRule =
        classes()
            .that()
            .areAnnotatedWith("org.springframework.context.annotation.Configuration")
            .and()
            .areNotInterfaces()
            .should()
            .haveSimpleNameEndingWith("Config")
            .orShould()
            .haveSimpleNameEndingWith("Configuration")
            .because("@Configuration classes must follow the naming convention *Config or *Configuration")
            .allowEmptyShould(true)

    @ArchTest
    val `@ConfigurationProperties classes should have names ending with Properties`: ArchRule =
        classes()
            .that()
            .areAnnotatedWith("org.springframework.boot.context.properties.ConfigurationProperties")
            .and()
            .areNotInterfaces()
            .should()
            .haveSimpleNameEndingWith("Properties")
            .because("@ConfigurationProperties classes must follow the naming convention *Properties")
            .allowEmptyShould(true)
}
