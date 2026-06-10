package io.flavien.demo.batch

import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import io.flavien.demo.domain.tenant.TenantContext
import io.flavien.demo.libtest.SpringModuleArchitectureTest
import jakarta.persistence.Entity
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.infrastructure.item.ItemProcessor
import org.springframework.batch.infrastructure.item.ItemReader
import org.springframework.batch.infrastructure.item.ItemWriter
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service

@AnalyzeClasses(
    packages = ["io.flavien.demo.batch"],
    importOptions = [ImportOption.DoNotIncludeTests::class],
)
@Suppress("ktlint:standard:property-naming")
class ArchitectureTest : SpringModuleArchitectureTest() {
    @ArchTest
    val `item reader classes should be annotated with @Component`: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("ItemReader")
            .and()
            .areNotInterfaces()
            .should()
            .beAnnotatedWith(Component::class.java)
            .because("Item readers must be registered as @Component Spring beans")

    @ArchTest
    val `item writer classes should be annotated with @Component`: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("ItemWriter")
            .and()
            .areNotInterfaces()
            .should()
            .beAnnotatedWith(Component::class.java)
            .because("Item writers must be registered as @Component Spring beans")

    @ArchTest
    val `item processor classes should be annotated with @Component`: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("ItemProcessor")
            .and()
            .areNotInterfaces()
            .should()
            .beAnnotatedWith(Component::class.java)
            .because("Item processors must be registered as @Component Spring beans")

    @ArchTest
    val `item reader classes should implement ItemReader`: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("ItemReader")
            .and()
            .areNotInterfaces()
            .should()
            .beAssignableTo(ItemReader::class.java)
            .because("Item reader classes must implement the Spring Batch ItemReader interface")

    @ArchTest
    val `item writer classes should implement ItemWriter`: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("ItemWriter")
            .and()
            .areNotInterfaces()
            .should()
            .beAssignableTo(ItemWriter::class.java)
            .because("Item writer classes must implement the Spring Batch ItemWriter interface")

    @ArchTest
    val `item processor classes should implement ItemProcessor`: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("ItemProcessor")
            .and()
            .areNotInterfaces()
            .should()
            .beAssignableTo(ItemProcessor::class.java)
            .because("Item processor classes must implement the Spring Batch ItemProcessor interface")

    @ArchTest
    val `item reader classes should reside in a step package`: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("ItemReader")
            .and()
            .areNotInterfaces()
            .should()
            .resideInAPackage("..step..")
            .because("Item readers must be co-located in their step sub-package under step/")

    @ArchTest
    val `item writer classes should reside in a step package`: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("ItemWriter")
            .and()
            .areNotInterfaces()
            .should()
            .resideInAPackage("..step..")
            .because("Item writers must be co-located in their step sub-package under step/")

    @ArchTest
    val `item processor classes should reside in a step package`: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("ItemProcessor")
            .and()
            .areNotInterfaces()
            .should()
            .resideInAPackage("..step..")
            .because("Item processors must be co-located in their step sub-package under step/")

    @ArchTest
    val `step config classes should be annotated with @Configuration`: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("StepConfig")
            .should()
            .beAnnotatedWith(Configuration::class.java)
            .because("Step configuration classes must be annotated with @Configuration")

    @ArchTest
    val `step config classes should reside in a step package`: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("StepConfig")
            .should()
            .resideInAPackage("..step..")
            .because("Step configuration classes must reside in their step sub-package under step/")

    @ArchTest
    val `job config classes should be annotated with @Configuration`: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("JobConfig")
            .should()
            .beAnnotatedWith(Configuration::class.java)
            .because("Job configuration classes must be annotated with @Configuration")

    @ArchTest
    val `job config classes should reside in a job package`: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("JobConfig")
            .should()
            .resideInAPackage("..job..")
            .because("Job configuration classes must reside in the job/ package")

    @ArchTest
    val `batch module should not contain @Service beans`: ArchRule =
        noClasses()
            .that()
            .resideInAPackage("io.flavien.demo.batch..")
            .should()
            .beAnnotatedWith(Service::class.java)
            .because(
                "Business logic must reside in the domain module — " +
                    "the batch module only orchestrates calls to domain services",
            )

    @ArchTest
    val `batch module should not contain @Repository beans`: ArchRule =
        noClasses()
            .that()
            .resideInAPackage("io.flavien.demo.batch..")
            .should()
            .beAnnotatedWith(Repository::class.java)
            .because("Repositories must only reside in the domain module, not in the batch module")

    @ArchTest
    val `batch module should not contain @Entity classes`: ArchRule =
        noClasses()
            .that()
            .resideInAPackage("io.flavien.demo.batch..")
            .should()
            .beAnnotatedWith(Entity::class.java)
            .because("JPA entities must only reside in the domain module, not in the batch module")

    @ArchTest
    val `batch module should not depend on the api module`: ArchRule =
        noClasses()
            .that()
            .resideInAPackage("io.flavien.demo.batch..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("io.flavien.demo.api..")
            .because("The batch module must not depend on the api module — they are sibling modules")

    @ArchTest
    val `batch module should not contain @Controller beans`: ArchRule =
        noClasses()
            .that()
            .resideInAPackage("io.flavien.demo.batch..")
            .should()
            .beAnnotatedWith(Controller::class.java)
            .because("The batch module is a standalone CLI-style application with no HTTP layer")

    @ArchTest
    val `batch module should not contain @RestController beans`: ArchRule =
        noClasses()
            .that()
            .resideInAPackage("io.flavien.demo.batch..")
            .should()
            .beAnnotatedWith("org.springframework.web.bind.annotation.RestController")
            .because("The batch module is a standalone CLI-style application with no HTTP layer")

    @ArchTest
    val `batch module should not depend on the servlet API`: ArchRule =
        noClasses()
            .that()
            .resideInAPackage("io.flavien.demo.batch..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("jakarta.servlet..")
            .because("The batch module has no HTTP/servlet concerns")

    @ArchTest
    val `only job runners should manage the TenantContext lifecycle`: ArchRule =
        noClasses()
            .that()
            .resideOutsideOfPackage("..batch.runner..")
            .should()
            .callMethod(TenantContext::class.java, "set", String::class.java)
            .orShould()
            .callMethod(TenantContext::class.java, "clear")
            .because(
                "The tenant lifecycle is owned by the job runner — " +
                    "steps, readers, processors and writers must only read the current tenant",
            )

    @ArchTest
    val `application runners should reside in the runner package and be named Runner`: ArchRule =
        classes()
            .that()
            .areAssignableTo(ApplicationRunner::class.java)
            .and()
            .areNotInterfaces()
            .should()
            .resideInAPackage("..runner..")
            .andShould()
            .haveSimpleNameEndingWith("Runner")
            .because("Job orchestration entry points must be uniformly named and located in the runner package")

    @ArchTest
    val `runners should not access domain repositories directly`: ArchRule =
        noClasses()
            .that()
            .resideInAPackage("..batch.runner..")
            .should()
            .dependOnClassesThat()
            .areAnnotatedWith(Repository::class.java)
            .because("Runners must delegate to domain services, never bypass the service layer by accessing repositories directly")

    @ArchTest
    val `item reader classes should be annotated with @StepScope`: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("ItemReader")
            .and()
            .areNotInterfaces()
            .should()
            .beAnnotatedWith(StepScope::class.java)
            .because(
                "Stateful item readers must be @StepScope so Spring Batch " +
                    "creates a fresh instance per step execution",
            )

    @ArchTest
    val `@StepScope classes should also be annotated with @Component`: ArchRule =
        classes()
            .that()
            .areAnnotatedWith(StepScope::class.java)
            .should()
            .beAnnotatedWith(Component::class.java)
            .because(
                "Spring Batch requires @StepScope beans to also be @Component " +
                    "so the scoped proxy is correctly registered in the application context",
            ).allowEmptyShould(true)
}
