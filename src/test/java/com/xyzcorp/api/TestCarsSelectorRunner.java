package com.xyzcorp.api;


import cucumber.api.CucumberOptions;
import cucumber.api.SnippetType;
import cucumber.api.testng.AbstractTestNGCucumberTests;

@CucumberOptions(strict = false,features = "src/test/resources/com/xyzcorp", format = { "pretty",
        "html:target/site/cucumber-pretty","rerun:target/rerun.txt",
        "json:target/cucumber.json" })
public class TestCarsSelectorRunner extends AbstractTestNGCucumberTests {
}
