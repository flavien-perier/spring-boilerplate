package io.flavien.demo.api

import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import jakarta.persistence.Entity
import org.mapstruct.Mapper
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.filter.OncePerRequestFilter

@AnalyzeClasses(
    packages = ["io.flavien.demo.api"],
    importOptions = [ImportOption.DoNotIncludeTests::class],
)
class ArchitectureTest {
    @ArchTest
    val controllersShouldBeAnnotatedWithController: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("Controller")
            .should()
            .beAnnotatedWith(Controller::class.java)
            .because("All controller classes must be annotated with @Controller")

    @ArchTest
    val controllersShouldNotBeAnnotatedWithRestController: ArchRule =
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
    val controllersShouldNotAccessRepositoriesDirectly: ArchRule =
        noClasses()
            .that()
            .haveSimpleNameEndingWith("Controller")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("..repository..")
            .because("Controllers must delegate to domain services and must never access repositories directly")

    @ArchTest
    val mapperInterfacesShouldResideInMapperPackage: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("Mapper")
            .and()
            .areInterfaces()
            .should()
            .resideInAPackage("..mapper..")
            .because("Mapper interfaces must be organized in mapper sub-packages")

    @ArchTest
    val mapperInterfacesShouldBeAnnotatedWithMapper: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("Mapper")
            .and()
            .areInterfaces()
            .should()
            .beAnnotatedWith(Mapper::class.java)
            .because("Mapper interfaces must be annotated with @Mapper (MapStruct)")

    @ArchTest
    val filtersShouldExtendOncePerRequestFilter: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("Filter")
            .and()
            .areNotInterfaces()
            .should()
            .beAssignableTo(OncePerRequestFilter::class.java)
            .because("HTTP filters must extend OncePerRequestFilter to ensure single execution per request")

    @ArchTest
    val filtersShouldBeAnnotatedWithComponent: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("Filter")
            .and()
            .areNotInterfaces()
            .should()
            .beAnnotatedWith(Component::class.java)
            .because("Filters must be registered as @Component Spring beans")

    @ArchTest
    val apiShouldNotContainServiceBeans: ArchRule =
        noClasses()
            .that()
            .resideInAPackage("io.flavien.demo.api..")
            .should()
            .beAnnotatedWith(Service::class.java)
            .because("Business logic services must only reside in the domain module, not in the api module")

    @ArchTest
    val apiShouldNotContainRepositoryBeans: ArchRule =
        noClasses()
            .that()
            .resideInAPackage("io.flavien.demo.api..")
            .should()
            .beAnnotatedWith(Repository::class.java)
            .because("Repositories must only reside in the domain module, not in the api module")

    @ArchTest
    val apiShouldNotContainJpaEntities: ArchRule =
        noClasses()
            .that()
            .resideInAPackage("io.flavien.demo.api..")
            .should()
            .beAnnotatedWith(Entity::class.java)
            .because("JPA entities must only reside in the domain module, not in the api module")

    @ArchTest
    val apiShouldNotDependOnBatchModule: ArchRule =
        noClasses()
            .that()
            .resideInAPackage("io.flavien.demo.api..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("io.flavien.demo.batch..")
            .because("The api module must not depend on the batch module — they are sibling modules")

    @ArchTest
    val filtersShouldResideInFilterPackage: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("Filter")
            .and()
            .areNotInterfaces()
            .should()
            .resideInAPackage("..filter..")
            .because("HTTP filters must be organized in filter sub-packages")

    @ArchTest
    val configurationClassesShouldResideInConfigPackage: ArchRule =
        classes()
            .that()
            .areAnnotatedWith(Configuration::class.java)
            .and()
            .resideInAPackage("io.flavien.demo.api..")
            .and()
            .resideOutsideOfPackage("io.flavien.demo.api.api..")
            .should()
            .resideInAPackage("..config..")
            .because("@Configuration beans in the api module must be organized in the config sub-package")

    @ArchTest
    val mappersShouldNotDependOnRepositories: ArchRule =
        noClasses()
            .that()
            .areAnnotatedWith(Mapper::class.java)
            .should()
            .dependOnClassesThat()
            .resideInAPackage("..repository..")
            .because("Mappers are pure transformers and must not access repositories directly")
}
