package io.flavien.demo.domain

import com.tngtech.archunit.base.DescribedPredicate
import com.tngtech.archunit.core.domain.JavaCall
import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.core.domain.JavaModifier
import com.tngtech.archunit.core.domain.properties.HasName
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchCondition
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.ConditionEvents
import com.tngtech.archunit.lang.SimpleConditionEvent
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import io.flavien.demo.domain.shared.exception.FioException
import io.flavien.demo.domain.tenant.TenantContext
import io.flavien.demo.libtest.SpringModuleArchitectureTest
import io.github.resilience4j.retry.annotation.Retry
import jakarta.persistence.Entity
import org.springframework.data.redis.core.RedisHash
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RestController
import java.io.Serializable

@AnalyzeClasses(
    packages = ["io.flavien.demo.domain"],
    importOptions = [ImportOption.DoNotIncludeTests::class],
)
@Suppress("ktlint:standard:property-naming")
class ArchitectureTest : SpringModuleArchitectureTest() {
    private val haveJavaMailSenderField =
        object : DescribedPredicate<JavaClass>("declare a JavaMailSender field") {
            override fun test(input: JavaClass) = input.fields.any { it.rawType.isEquivalentTo(JavaMailSender::class.java) }
        }

    private val haveAPasswordField =
        object : DescribedPredicate<JavaClass>("have a password field") {
            override fun test(input: JavaClass) = input.fields.any { it.name == "password" }
        }

    private val declareTheirOwnToString =
        object : ArchCondition<JavaClass>("declare their own toString()") {
            override fun check(
                item: JavaClass,
                events: ConditionEvents,
            ) {
                val declaresToString = item.methods.any { it.name == "toString" && it.rawParameterTypes.isEmpty() }
                events.add(
                    SimpleConditionEvent(
                        item,
                        declaresToString,
                        "${item.name} ${if (declaresToString) "declares" else "does not declare"} its own toString()",
                    ),
                )
            }
        }

    @ArchTest
    val `@Configuration classes should reside in a configuration package`: ArchRule =
        classes()
            .that()
            .areAnnotatedWith("org.springframework.context.annotation.Configuration")
            .should()
            .resideInAPackage("..configuration..")
            .because("@Configuration beans must be organized in a configuration sub-package")
            .allowEmptyShould(true)

    @ArchTest
    val `classes in entity packages should be annotated with @Entity or @RedisHash`: ArchRule =
        classes()
            .that()
            .resideInAPackage("..entity..")
            .and()
            .areNotInterfaces()
            .should()
            .beAnnotatedWith(Entity::class.java)
            .orShould()
            .beAnnotatedWith(RedisHash::class.java)
            .because(
                "Entity classes must be annotated with @Entity (JPA / PostgreSQL) " +
                    "or @RedisHash (Spring Data Redis / Valkey)",
            )

    @ArchTest
    val `@Entity and @RedisHash classes should reside in an entity package`: ArchRule =
        classes()
            .that()
            .areAnnotatedWith(Entity::class.java)
            .or()
            .areAnnotatedWith(RedisHash::class.java)
            .should()
            .resideInAPackage("..entity..")
            .because(
                "Classes annotated with @Entity (JPA) or @RedisHash (Redis) " +
                    "must reside in an entity sub-package",
            )

    @ArchTest
    val `@RedisHash classes should implement Serializable`: ArchRule =
        classes()
            .that()
            .areAnnotatedWith(RedisHash::class.java)
            .should()
            .implement(Serializable::class.java)
            .because(
                "Redis-persisted entities must implement Serializable " +
                    "for correct Java serialization by Spring Data Redis",
            )

    @ArchTest
    val `domain module should not contain @Controller beans`: ArchRule =
        noClasses()
            .that()
            .resideInAPackage("io.flavien.demo.domain..")
            .should()
            .beAnnotatedWith(Controller::class.java)
            .because("HTTP controllers must not reside in the domain module")

    @ArchTest
    val `domain module should not contain @RestController beans`: ArchRule =
        noClasses()
            .that()
            .resideInAPackage("io.flavien.demo.domain..")
            .should()
            .beAnnotatedWith(RestController::class.java)
            .because("HTTP controllers must not reside in the domain module")

    @ArchTest
    val `domain module should not depend on the api module`: ArchRule =
        noClasses()
            .that()
            .resideInAPackage("io.flavien.demo.domain..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("io.flavien.demo.api..")
            .because(
                "The domain module must not depend on the api module — " +
                    "the dependency direction is api → domain, never the reverse",
            )

    @ArchTest
    val `domain module should not depend on the batch module`: ArchRule =
        noClasses()
            .that()
            .resideInAPackage("io.flavien.demo.domain..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("io.flavien.demo.batch..")
            .because("The domain module must not depend on the batch module")

    @ArchTest
    val `domain module should not depend on the servlet API`: ArchRule =
        noClasses()
            .that()
            .resideInAPackage("io.flavien.demo.domain..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("jakarta.servlet..")
            .because(
                "The domain module must be HTTP-agnostic — " +
                    "servlet API concepts belong in the api module",
            )

    @ArchTest
    val `domain should only use spring-web for HTTP status on exceptions`: ArchRule =
        noClasses()
            .that()
            .resideOutsideOfPackage("..domain..exception..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("org.springframework.web..")
            .because(
                "Only domain exceptions may carry HTTP status via @ResponseStatus/HttpStatus; " +
                        "controllers and servlet API belong in the api module",
            )

    @ArchTest
    val `domain should only use spring-http for status on exceptions`: ArchRule =
        noClasses()
            .that()
            .resideOutsideOfPackage("..domain..exception..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("org.springframework.http..")
            .because("Only domain exceptions may reference HttpStatus")

    @ArchTest
    val `public methods of classes with a JavaMailSender field should be annotated with @Retry`: ArchRule =
        methods()
            .that()
            .areDeclaredInClassesThat(haveJavaMailSenderField)
            .and()
            .arePublic()
            .should()
            .beAnnotatedWith(Retry::class.java)
            .because(
                "Services that send email must protect their public methods with " +
                    "@Retry (Resilience4j) to retry on transient SMTP failures",
            )

    @ArchTest
    val `only tenant infrastructure should manage the TenantContext lifecycle`: ArchRule =
        noClasses()
            .that()
            .resideOutsideOfPackage("..domain.tenant..")
            .should()
            .callMethod(TenantContext::class.java, "set", String::class.java)
            .orShould()
            .callMethod(TenantContext::class.java, "clear")
            .because(
                "Only entry points (HTTP filter, batch runner) own the tenant lifecycle — " +
                    "domain services must only read the current tenant",
            )

    @ArchTest
    val `only MailService and tenant infrastructure should use JavaMailSender`: ArchRule =
        noClasses()
            .that()
            .resideOutsideOfPackages("..shared.service..", "..tenant..", "..configuration..")
            .should()
            .dependOnClassesThat()
            .areAssignableTo(JavaMailSender::class.java)
            .because("Email sending is centralized in MailService, which carries the @Retryable policy")

    @ArchTest
    val `random tokens should be generated with an explicit SecureRandom`: ArchRule =
        noClasses()
            .should()
            .callMethodWhere(JavaCall.Predicates.target(HasName.Predicates.name("randomString\$default")))
            .because(
                "RandomUtil.randomString must be called with SECURE_RANDOM — " +
                    "relying on the default Random parameter is not cryptographically secure",
            )

    @ArchTest
    val `only PasswordService should depend on spring-security-crypto`: ArchRule =
        noClasses()
            .that()
            .doNotHaveSimpleName("PasswordService")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("org.springframework.security.crypto..")
            .because("Password hashing algorithm choices must have a single owner")

    @ArchTest
    val `tenant models holding a password should mask it in toString`: ArchRule =
        classes()
            .that()
            .resideInAPackage("..tenant.model..")
            .and(haveAPasswordField)
            .should(declareTheirOwnToString)
            .because("Kotlin data classes leak all constructor properties in their generated toString — secrets must be masked")

    @ArchTest
    val `@Service classes should reside in a service package`: ArchRule =
        classes()
            .that()
            .areAnnotatedWith("org.springframework.stereotype.Service")
            .should()
            .resideInAPackage("..service..")
            .because("@Service classes must be organized in service sub-packages")

    @ArchTest
    val `service classes should be annotated with @Service`: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("Service")
            .and()
            .areNotInterfaces()
            .should()
            .beAnnotatedWith("org.springframework.stereotype.Service")
            .because("All service classes must be annotated with @Service")

    @ArchTest
    val `@Service classes should have names ending with Service`: ArchRule =
        classes()
            .that()
            .areAnnotatedWith("org.springframework.stereotype.Service")
            .should()
            .haveSimpleNameEndingWith("Service")
            .because("@Service classes must follow the naming convention *Service")

    @ArchTest
    val `@Repository interfaces should reside in a repository package`: ArchRule =
        classes()
            .that()
            .areAnnotatedWith("org.springframework.stereotype.Repository")
            .should()
            .resideInAPackage("..repository..")
            .because("@Repository interfaces must be organized in repository sub-packages")

    @ArchTest
    val `repository classes should be interfaces`: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("Repository")
            .should()
            .beInterfaces()
            .because("Repositories in Spring Data are interfaces delegated to runtime-generated proxies")

    @ArchTest
    val `repository interfaces should be annotated with @Repository`: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("Repository")
            .should()
            .beAnnotatedWith("org.springframework.stereotype.Repository")
            .because("All repository interfaces must be annotated with @Repository")

    @ArchTest
    val `@Repository interfaces should have names ending with Repository`: ArchRule =
        classes()
            .that()
            .areAnnotatedWith("org.springframework.stereotype.Repository")
            .should()
            .haveSimpleNameEndingWith("Repository")
            .because("@Repository interfaces must follow the naming convention *Repository")

    @ArchTest
    val `non-abstract classes in exception packages should be annotated with @ResponseStatus`: ArchRule =
        classes()
            .that()
            .resideInAPackage("..exception..")
            .and()
            .areNotInterfaces()
            .and()
            .doNotHaveModifier(JavaModifier.ABSTRACT)
            .should()
            .beAnnotatedWith("org.springframework.web.bind.annotation.ResponseStatus")
            .because(
                "Domain exceptions must be annotated with @ResponseStatus " +
                    "to declare the HTTP status code returned when the exception propagates",
            )

    @ArchTest
    val `abstract classes in exception packages should not be annotated with @ResponseStatus`: ArchRule =
        noClasses()
            .that()
            .resideInAPackage("..exception..")
            .and()
            .haveModifier(JavaModifier.ABSTRACT)
            .should()
            .beAnnotatedWith("org.springframework.web.bind.annotation.ResponseStatus")
            .because(
                "Abstract exception base classes must not carry @ResponseStatus — " +
                    "only concrete leaf exceptions declare an HTTP status",
            )

    @ArchTest
    val `domain exceptions should extend FioException`: ArchRule =
        classes()
            .that()
            .resideInAPackage("..exception..")
            .and()
            .areNotInterfaces()
            .should()
            .beAssignableTo(FioException::class.java)
            .because(
                "All domain exceptions must extend FioException " +
                    "to ensure consistent error handling and wrapping semantics",
            ).allowEmptyShould(true)
}
