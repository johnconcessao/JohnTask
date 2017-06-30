package com.xyzcorp.api;

import com.xyzcorp.bussinessEntities.Car;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import java.util.*;

import static com.xyzcorp.api.RestApiBasicClient.getInstance;

public class CarsSelectorDefs {
    protected static final Logger LOGGER = LogManager.getLogger();

    private static final String MANUFACTURERS_QUERY = "/v1/car-types/manufacturer";
    private static final String MODELS_QUERY = "/v1/car-types/main-types";
    private static final String YEARS_QUERY = "/v1/car-types/built-dates";

    private Map<String, String> allManufacturers = new HashMap<>();
    private Car car;

    @Given("^non-empty cars manufacturers list is grabbed$")
    public void getMapOfManufacturers() {
        car = new Car();
        allManufacturers = getResponseWkdaFromQuery(MANUFACTURERS_QUERY, new HashMap<>());
    }

    @When("^select one random manufacturer$")
    public void selectOneRandomManufacturer() {
        car.setManufacturer(getRandomKeyFromMap(allManufacturers));
    }

    @When("^choose one of manufacturer models$")
    public void selectOneRandomModelOfManufacturer() {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("manufacturer", car.getManufacturer());

        Map<String, String> models = getResponseWkdaFromQuery(MODELS_QUERY, queryParams);

        car.setModel(models.get(getRandomKeyFromMap(models)));

    }


    @Then("^at least one manufacturing model year is available$")
    public void selectRandomYearOfManufacturedModel() {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("manufacturer", car.getManufacturer());
        queryParams.put("main-type", car.getModel());

        Map<String, String> years = getResponseWkdaFromQuery(YEARS_QUERY, queryParams);

        car.setYear(Integer.parseInt(years.get(getRandomKeyFromMap(years))));
    }

    @And("^set non-existing manufacturer model$")
    public void setNonExistingManufacturerModel() {
        car.setModel("non-existing");
    }

    @Then("^empty entries returned$")
    public void emptyEntriesReturned() {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("manufacturer", car.getManufacturer());
        queryParams.put("main-type", car.getModel());

        Assert.assertTrue(getResponseWkdaFromQuery(YEARS_QUERY, queryParams).isEmpty());
    }


    /**
     * Usually soft asserts is not very good practice. Here we use it to verify that we will check each entry and will not break our loop early and will not miss some errors from the end. This method executes quite long but ensures us that there are no broken objects.
     */
    @Then("^each model have its years of manufacturing$")
    public void getMapOfAllModelsAllYears() {
        SoftAssert asert = new SoftAssert();

        for (Map.Entry<String, String> entry : allManufacturers.entrySet()) {

            Map<String, String> queryParams = new HashMap<>();
            queryParams.put("manufacturer", entry.getKey());
            Map<String, String> models = getResponseWkdaFromQuery(MODELS_QUERY, queryParams);
            List<String> modelsForCurrentManufacturer = new ArrayList<>(models.keySet());

            asert.assertTrue(!modelsForCurrentManufacturer.isEmpty(), String.format("Manufacturer with code %s does not have any models for it!", entry.getKey()));

            for (String carModel : modelsForCurrentManufacturer) {
                Map<String, String> queryParameters = queryParams;
                queryParameters.put("main-type", carModel);
                Map<String, String> years = getResponseWkdaFromQuery(YEARS_QUERY, queryParameters);
                List<String> yearsForCurrentModel = new ArrayList<>(years.keySet());

                asert.assertTrue(!yearsForCurrentModel.isEmpty(), String.format("Model %s of manufacturer with code %s does not have any manufactured years for it!", carModel, entry.getKey()));
            }
        }
        asert.assertAll();
    }

    @Then("^selected car is set$")
    public void logSelectedCar() {
        LOGGER.info(car.toString());
    }


    private <K> K getRandomKeyFromMap(Map<K, ?> inputMap) {
        int randomEntryNumber = new Random().nextInt(inputMap.size());
        return new ArrayList<>(inputMap.keySet()).get(randomEntryNumber);
    }

    private Map<String, String> getResponseWkdaFromQuery(String requestUrl, Map<String, String> queryParams) {
        return getInstance()
                .executeGetRequest(requestUrl, queryParams)
                .statusCode(200)
                .extract()
                .body()
                .jsonPath().getMap("wkda");
    }
}
