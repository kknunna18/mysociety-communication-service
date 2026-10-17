package com.mysociety.communication;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "com.mysociety.communication", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {
    @ArchTest
    static final ArchRule webDoesNotAccessPersistence = noClasses().that().resideInAPackage("..web..").should().dependOnClassesThat().resideInAPackage("..domain..").andShould().haveSimpleNameEndingWith("Repository");
}
