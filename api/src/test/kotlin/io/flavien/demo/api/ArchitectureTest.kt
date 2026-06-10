package io.flavien.demo.api

import com.tngtech.archunit.base.DescribedPredicate
import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.core.importer.Location
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import io.flavien.demo.domain.tenant.TenantContext
import io.flavien.demo.libtest.SpringModuleArchitectureTest
import jakarta.persistence.Entity
import org.mapstruct.Mapper
import org.springframework.stereotype.Component
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.filter.OncePerRequestFilter

private class DoNotIncludeGenerated : ImportOption {
    override fun includes(location: Location) = !location.contains("/generated/")
}

@AnalyzeClasses(
    packages = ["io.flavien.demo.api"],
    importOptions = [ImportOption.DoNotIncludeTests::class, DoNotIncludeGenerated::class],
)
@Suppress("ktlint:standard:property-naming")
class ArchitectureTest : SpringModuleArchitectureTest() {
    private val isApplicationController =
        object : DescribedPredicate<JavaClass>("is an application controller") {
            override fun test(clazz: JavaClass) =
                clazz.packageName.startsWith("io.flavien.demo.api.") && clazz.simpleName.endsWith("Controller")
        }

    @ArchTest
    val `controller classes should be annotated with @Controller`: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("Controller")
            .should()
            .beAnnotatedWith(Controller::class.java)
            .because("All controller classes must be annotated with @Controller")

    @ArchTest
    val `controller classes should not be annotated with @RestController`: ArchRule =
        noClasses()
            .that()
            .haveSimpleNameEndingWith("Controller")
            .should()
            .beAnnotatedWith(RestController::class.java)
            .because(
                "Controllers must use @Controller, not @RestController — " +
                    "response body handling is provided by the generated OpenAPI interface",
            )

    @ArchTest
    val `@Configuration classes should reside in a configuration package`: ArchRule =
        classes()
            .that()
            .areAnnotatedWith("org.springframework.context.annotation.Configuration")
            .should()
            .resideInAPackage("..configuration..")
            .because("@Configuration beans in the api module must be organized in the configuration sub-package")
            .allowEmptyShould(true)

    @ArchTest
    val `controller classes should not access repository packages directly`: ArchRule =
        noClasses()
            .that()
            .haveSimpleNameEndingWith("Controller")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("..repository..")
            .because("Controllers must delegate to domain services and must never access repositories directly")

    @ArchTest
    val `mapper interfaces should reside in a mapper package`: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("Mapper")
            .and()
            .areInterfaces()
            .should()
            .resideInAPackage("..mapper..")
            .because("Mapper interfaces must be organized in mapper sub-packages")

    @ArchTest
    val `mapper interfaces should be annotated with @Mapper`: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("Mapper")
            .and()
            .areInterfaces()
            .should()
            .beAnnotatedWith(Mapper::class.java)
            .because("Mapper interfaces must be annotated with @Mapper (MapStruct)")

    @ArchTest
    val `filter classes should extend OncePerRequestFilter`: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("Filter")
            .and()
            .areNotInterfaces()
            .should()
            .beAssignableTo(OncePerRequestFilter::class.java)
            .because("HTTP filters must extend OncePerRequestFilter to ensure single execution per request")

    @ArchTest
    val `filter classes should be annotated with @Component`: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("Filter")
            .and()
            .areNotInterfaces()
            .should()
            .beAnnotatedWith(Component::class.java)
            .because("Filters must be registered as @Component Spring beans")

    @ArchTest
    val `api module should not contain @Service beans`: ArchRule =
        noClasses()
            .that()
            .resideInAPackage("io.flavien.demo.api..")
            .should()
            .beAnnotatedWith(Service::class.java)
            .because("Business logic services must only reside in the domain module, not in the api module")

    @ArchTest
    val `api module should not contain @Repository beans`: ArchRule =
        noClasses()
            .that()
            .resideInAPackage("io.flavien.demo.api..")
            .should()
            .beAnnotatedWith(Repository::class.java)
            .because("Repositories must only reside in the domain module, not in the api module")

    @ArchTest
    val `api module should not contain @Entity classes`: ArchRule =
        noClasses()
            .that()
            .resideInAPackage("io.flavien.demo.api..")
            .should()
            .beAnnotatedWith(Entity::class.java)
            .because("JPA entities must only reside in the domain module, not in the api module")

    @ArchTest
    val `api module should not depend on the batch module`: ArchRule =
        noClasses()
            .that()
            .resideInAPackage("io.flavien.demo.api..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("io.flavien.demo.batch..")
            .because("The api module must not depend on the batch module — they are sibling modules")

    @ArchTest
    val `filter classes should reside in a filter package`: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("Filter")
            .and()
            .areNotInterfaces()
            .should()
            .resideInAPackage("..filter..")
            .because("HTTP filters must be organized in filter sub-packages")

    @ArchTest
    val `mapper interfaces should not depend on repository packages`: ArchRule =
        noClasses()
            .that()
            .areAnnotatedWith(Mapper::class.java)
            .should()
            .dependOnClassesThat()
            .resideInAPackage("..repository..")
            .because("Mappers are pure transformers and must not access repositories directly")

    @ArchTest
    val `only the tenant resolution filter should manage the TenantContext lifecycle`: ArchRule =
        noClasses()
            .that()
            .resideOutsideOfPackage("..configuration.filter..")
            .should()
            .callMethod(TenantContext::class.java, "set", String::class.java)
            .orShould()
            .callMethod(TenantContext::class.java, "clear")
            .because("Tenant resolution happens once per request in TenantResolutionFilter")

    @ArchTest
    val `controllers should not depend on tenant infrastructure`: ArchRule =
        noClasses()
            .that()
            .haveSimpleNameEndingWith("Controller")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("..domain.tenant..")
            .because("Controllers must stay tenant-agnostic — the tenant is resolved by TenantResolutionFilter")

    @ArchTest
    val `@RestControllerAdvice classes should reside in the shared package`: ArchRule =
        classes()
            .that()
            .areAnnotatedWith(RestControllerAdvice::class.java)
            .should()
            .resideInAPackage("..shared..")
            .because("Exception handlers must be centralized in the shared sub-package")

    @ArchTest
    val `controllers should not depend on other controllers`: ArchRule =
        noClasses()
            .that()
            .haveSimpleNameEndingWith("Controller")
            .and()
            .resideInAPackage("io.flavien.demo.api..")
            .should()
            .dependOnClassesThat(isApplicationController)
            .because("Controllers must delegate to domain services, never call each other directly")

    @ArchTest
    val `util classes should reside in a util package`: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("Util")
            .and()
            .areNotInterfaces()
            .and()
            .resideOutsideOfPackage("io.flavien.demo.api.generated..")
            .should()
            .resideInAPackage("..util..")
            .because("Utility classes must be organized in util sub-packages")
}
