package io.flavien.demo.domain

import com.tngtech.archunit.base.DescribedPredicate
import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import jakarta.persistence.Entity
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.RedisHash
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@AnalyzeClasses(
    packages = ["io.flavien.demo.domain"],
    importOptions = [ImportOption.DoNotIncludeTests::class],
)
class ArchitectureTest {
    private val haveJavaMailSenderField =
        object : DescribedPredicate<JavaClass>("declare a JavaMailSender field") {
            override fun test(input: JavaClass) = input.fields.any { it.rawType.isEquivalentTo(JavaMailSender::class.java) }
        }

    @ArchTest
    val serviceClassesShouldBeAnnotatedWithService: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("Service")
            .and()
            .areNotInterfaces()
            .should()
            .beAnnotatedWith(Service::class.java)
            .because("All service classes must be annotated with @Service")

    @ArchTest
    val serviceClassesShouldResideInServicePackage: ArchRule =
        classes()
            .that()
            .areAnnotatedWith(Service::class.java)
            .should()
            .resideInAPackage("..service..")
            .because("@Service classes must be organized in service sub-packages")

    @ArchTest
    val repositoriesShouldBeInterfaces: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("Repository")
            .should()
            .beInterfaces()
            .because("Repositories in Spring Data are interfaces delegated to runtime-generated proxies")

    @ArchTest
    val repositoriesShouldBeAnnotatedWithRepository: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("Repository")
            .should()
            .beAnnotatedWith(Repository::class.java)
            .because("All repository interfaces must be annotated with @Repository")

    @ArchTest
    val repositoriesShouldResideInRepositoryPackage: ArchRule =
        classes()
            .that()
            .areAnnotatedWith(Repository::class.java)
            .should()
            .resideInAPackage("..repository..")
            .because("@Repository interfaces must be organized in repository sub-packages")

    @ArchTest
    val exceptionsInExceptionPackageShouldExtendRuntimeException: ArchRule =
        classes()
            .that()
            .resideInAPackage("..exception..")
            .and()
            .areNotInterfaces()
            .should()
            .beAssignableTo(RuntimeException::class.java)
            .because("All classes in exception packages must extend RuntimeException")

    @ArchTest
    val exceptionsInExceptionPackageShouldBeAnnotatedWithResponseStatus: ArchRule =
        classes()
            .that()
            .resideInAPackage("..exception..")
            .and()
            .areNotInterfaces()
            .should()
            .beAnnotatedWith(ResponseStatus::class.java)
            .because(
                "Domain exceptions must be annotated with @ResponseStatus " +
                    "to declare the HTTP status code returned when the exception propagates",
            )

    @ArchTest
    val exceptionNamesShouldEndWithException: ArchRule =
        classes()
            .that()
            .resideInAPackage("..exception..")
            .and()
            .areNotInterfaces()
            .should()
            .haveSimpleNameEndingWith("Exception")
            .because("Exception classes must follow the naming convention *Exception")

    @ArchTest
    val entitiesInEntityPackageShouldBeAnnotatedCorrectly: ArchRule =
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
    val domainShouldNotHaveControllerAnnotation: ArchRule =
        noClasses()
            .that()
            .resideInAPackage("io.flavien.demo.domain..")
            .should()
            .beAnnotatedWith(Controller::class.java)
            .because("HTTP controllers must not reside in the domain module")

    @ArchTest
    val domainShouldNotHaveRestControllerAnnotation: ArchRule =
        noClasses()
            .that()
            .resideInAPackage("io.flavien.demo.domain..")
            .should()
            .beAnnotatedWith(RestController::class.java)
            .because("HTTP controllers must not reside in the domain module")

    @ArchTest
    val domainShouldNotDependOnApiModule: ArchRule =
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
    val domainShouldNotDependOnBatchModule: ArchRule =
        noClasses()
            .that()
            .resideInAPackage("io.flavien.demo.domain..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("io.flavien.demo.batch..")
            .because("The domain module must not depend on the batch module")

    @ArchTest
    val domainShouldNotDependOnServletApi: ArchRule =
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
    val entityAnnotatedClassesShouldResideInEntityPackage: ArchRule =
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
    val configurationClassesShouldBeAnnotatedWithConfiguration: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("Configuration")
            .and()
            .areNotInterfaces()
            .should()
            .beAnnotatedWith(Configuration::class.java)
            .because("Classes named *Configuration must be annotated with @Configuration")

    @ArchTest
    val publicMethodsOfTokenServicesShouldBeAnnotatedWithCircuitBreaker: ArchRule =
        methods()
            .that()
            .areDeclaredInClassesThat()
            .haveSimpleNameEndingWith("TokenService")
            .and()
            .arePublic()
            .should()
            .beAnnotatedWith(CircuitBreaker::class.java)
            .because(
                "Token services interact with Redis directly — all public methods must be " +
                    "protected by @CircuitBreaker(name = \"redis\") to handle Redis unavailability gracefully",
            )

    @ArchTest
    val publicMethodsOfMailServicesShouldBeAnnotatedWithRetry: ArchRule =
        methods()
            .that()
            .areDeclaredInClassesThat(haveJavaMailSenderField)
            .and()
            .arePublic()
            .should()
            .beAnnotatedWith(Retry::class.java)
            .because(
                "Services that send email must protect their public methods with " +
                    "@Retry(name = \"mailSend\") to retry on transient SMTP failures",
            )
}
