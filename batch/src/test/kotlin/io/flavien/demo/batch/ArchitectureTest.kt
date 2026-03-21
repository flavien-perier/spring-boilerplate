package io.flavien.demo.batch

import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import jakarta.persistence.Entity
import org.springframework.batch.infrastructure.item.ItemProcessor
import org.springframework.batch.infrastructure.item.ItemReader
import org.springframework.batch.infrastructure.item.ItemWriter
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service

@AnalyzeClasses(
    packages = ["io.flavien.demo.batch"],
    importOptions = [ImportOption.DoNotIncludeTests::class],
)
class ArchitectureTest {
    @ArchTest
    val itemReadersShouldBeAnnotatedWithComponent: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("ItemReader")
            .and()
            .areNotInterfaces()
            .should()
            .beAnnotatedWith(Component::class.java)
            .because("Item readers must be registered as @Component Spring beans")

    @ArchTest
    val itemWritersShouldBeAnnotatedWithComponent: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("ItemWriter")
            .and()
            .areNotInterfaces()
            .should()
            .beAnnotatedWith(Component::class.java)
            .because("Item writers must be registered as @Component Spring beans")

    @ArchTest
    val itemProcessorsShouldBeAnnotatedWithComponent: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("ItemProcessor")
            .and()
            .areNotInterfaces()
            .should()
            .beAnnotatedWith(Component::class.java)
            .because("Item processors must be registered as @Component Spring beans")

    @ArchTest
    val itemReadersShouldImplementItemReader: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("ItemReader")
            .and()
            .areNotInterfaces()
            .should()
            .beAssignableTo(ItemReader::class.java)
            .because("Item reader classes must implement the Spring Batch ItemReader interface")

    @ArchTest
    val itemWritersShouldImplementItemWriter: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("ItemWriter")
            .and()
            .areNotInterfaces()
            .should()
            .beAssignableTo(ItemWriter::class.java)
            .because("Item writer classes must implement the Spring Batch ItemWriter interface")

    @ArchTest
    val itemProcessorsShouldImplementItemProcessor: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("ItemProcessor")
            .and()
            .areNotInterfaces()
            .should()
            .beAssignableTo(ItemProcessor::class.java)
            .because("Item processor classes must implement the Spring Batch ItemProcessor interface")

    @ArchTest
    val itemReadersShouldResideInStepPackage: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("ItemReader")
            .and()
            .areNotInterfaces()
            .should()
            .resideInAPackage("..step..")
            .because("Item readers must be co-located in their step sub-package under step/")

    @ArchTest
    val itemWritersShouldResideInStepPackage: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("ItemWriter")
            .and()
            .areNotInterfaces()
            .should()
            .resideInAPackage("..step..")
            .because("Item writers must be co-located in their step sub-package under step/")

    @ArchTest
    val itemProcessorsShouldResideInStepPackage: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("ItemProcessor")
            .and()
            .areNotInterfaces()
            .should()
            .resideInAPackage("..step..")
            .because("Item processors must be co-located in their step sub-package under step/")

    @ArchTest
    val stepConfigsShouldBeAnnotatedWithConfiguration: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("StepConfig")
            .should()
            .beAnnotatedWith(Configuration::class.java)
            .because("Step configuration classes must be annotated with @Configuration")

    @ArchTest
    val stepConfigsShouldResideInStepPackage: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("StepConfig")
            .should()
            .resideInAPackage("..step..")
            .because("Step configuration classes must reside in their step sub-package under step/")

    @ArchTest
    val jobConfigsShouldBeAnnotatedWithConfiguration: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("JobConfig")
            .should()
            .beAnnotatedWith(Configuration::class.java)
            .because("Job configuration classes must be annotated with @Configuration")

    @ArchTest
    val jobConfigsShouldResideInJobPackage: ArchRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("JobConfig")
            .should()
            .resideInAPackage("..job..")
            .because("Job configuration classes must reside in the job/ package")

    @ArchTest
    val batchShouldNotContainServiceBeans: ArchRule =
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
    val batchShouldNotContainRepositoryBeans: ArchRule =
        noClasses()
            .that()
            .resideInAPackage("io.flavien.demo.batch..")
            .should()
            .beAnnotatedWith(Repository::class.java)
            .because("Repositories must only reside in the domain module, not in the batch module")

    @ArchTest
    val batchShouldNotContainJpaEntities: ArchRule =
        noClasses()
            .that()
            .resideInAPackage("io.flavien.demo.batch..")
            .should()
            .beAnnotatedWith(Entity::class.java)
            .because("JPA entities must only reside in the domain module, not in the batch module")

    @ArchTest
    val batchShouldNotDependOnApiModule: ArchRule =
        noClasses()
            .that()
            .resideInAPackage("io.flavien.demo.batch..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("io.flavien.demo.api..")
            .because("The batch module must not depend on the api module — they are sibling modules")

    @ArchTest
    val batchShouldNotHaveControllerAnnotation: ArchRule =
        noClasses()
            .that()
            .resideInAPackage("io.flavien.demo.batch..")
            .should()
            .beAnnotatedWith(Controller::class.java)
            .because("The batch module is a standalone CLI-style application with no HTTP layer")

    @ArchTest
    val batchShouldNotHaveRestControllerAnnotation: ArchRule =
        noClasses()
            .that()
            .resideInAPackage("io.flavien.demo.batch..")
            .should()
            .beAnnotatedWith("org.springframework.web.bind.annotation.RestController")
            .because("The batch module is a standalone CLI-style application with no HTTP layer")

    @ArchTest
    val batchShouldNotDependOnServletApi: ArchRule =
        noClasses()
            .that()
            .resideInAPackage("io.flavien.demo.batch..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("jakarta.servlet..")
            .because("The batch module has no HTTP/servlet concerns")
}
