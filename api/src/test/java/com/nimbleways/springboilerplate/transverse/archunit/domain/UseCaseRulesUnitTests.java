package com.nimbleways.springboilerplate.transverse.archunit.domain;

import com.nimbleways.springboilerplate.testhelpers.annotations.UnitTest;
import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import java.util.List;

import static com.nimbleways.springboilerplate.Application.BASE_PACKAGE_NAME;
import static com.tngtech.archunit.lang.conditions.ArchConditions.have;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@UnitTest
@AnalyzeClasses(packages = BASE_PACKAGE_NAME, importOptions = { ImportOption.DoNotIncludeTests.class })
public class UseCaseRulesUnitTests {

    @ArchTest
    static final ArchRule usecases_should_have_a_handle_method_that_takes_at_most_one_argument =
        classes()
            .that().resideInAPackage("..usecases.(*)")
            .and().haveSimpleNameEndingWith("UseCase")
            .should(have(DescribedPredicate.describe(
                "a 'handle' method that takes at most one argument",
                javaClass -> {
                    List<JavaMethod> handleMethods = javaClass.getAllMethods().stream()
                        .filter(method -> "handle".equals(method.getName())).toList();
                    return handleMethods.size() == 1 && handleMethods.get(0).getParameters().size() <= 1;
                }
            )));
}
