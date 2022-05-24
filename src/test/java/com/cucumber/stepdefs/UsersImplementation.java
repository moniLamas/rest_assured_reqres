package com.cucumber.stepdefs;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasKey;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.messages.internal.com.fasterxml.jackson.databind.JsonNode;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import java.io.Serializable;
import java.util.HashMap;

public class UsersImplementation implements Serializable {
    private Response putUsers = null;
    private Response postUsers = null;
    private Response deleteUsers = null;

    @Before
    public void before(){
        RestAssured.baseURI = "https://reqres.in/api/";
    }

// GET: nos devuelve la lista de usuarios con un response getUsers
// .log().all() opcional, es solo para obtener trazas por el log
//En .param pasamos el parámetro 2 para 'page', igual que en postman
//En .get pasamos lo que va después de la baseURI ("https://reqres.in/api/")
    @Given("the following get request that brings us the users")
    public Response getUsers(){
        //Response responseGetUsers = given().baseUri("https://reqres.in/api/users?page=2").get();
        // given().param("page", 2).baseUri("https://reqres.in/api/users").get();
        Response responseGetUsers = given().log().all().param("page", 2).get("/users");
        return responseGetUsers;
    }

    @And("the response is 200")
    public void validateResponse(){
        assertTrue("The response is not 200",getUsers().statusCode()==200);
    }

    @And("the body response contains the corresponding ids")
    public void validateUserIds(){
        getUsers().then().body("data.id", hasItems(7,8,9,10,11,12));
    }
    @Then("the total page contains {int}")
    public void validateTotalPages(Integer page){
        getUsers().then().body("total_pages", equalTo(page));
    }

// POST creat un nuevo usuario
    @Given("the following post request that add users")
    public void postUsers(){
// given()
// .accept(ContentType.JSON)
// . body("{\"name\":\"juan\", \"job\":\"developer\"}")
// .post("/users");
        HashMap<String, String> bodyRequestMap = new HashMap<>();
        bodyRequestMap.put("name", "ramlr93");
        bodyRequestMap.put("job", "leader");
        postUsers =
                given().contentType(ContentType.JSON).body(bodyRequestMap).post("/users");
    }

    @And("the response is 201 for the post")
    public void validateResponsePost() {
        assertTrue("The response is not 201",postUsers.statusCode()==201);
    }

    @And("the body response contains key name")
    public void validateResponsePostKeyBody(){
        postUsers.then().body("$",hasKey("id"));
    }

    @Then("the body response contains the {string} of the user created")
    public void validateResponsePostBodyValueName(String valueName) {
        JsonPath jsonPathUsers = new JsonPath(postUsers.body().asString());
        String jsonUsers=jsonPathUsers.getString("name");
        assertEquals("The value of the name field is not what is expected",valueName,jsonUsers);
    }

// PUT modificar los datos de un usuario
    @Given("the following put request that update users")
    public void putUsers(){
        postUsers();
        JsonPath jsonPathUsers = new JsonPath(postUsers.body().asString());
        String jsonIdCreate=jsonPathUsers.getString("id");
        HashMap<String, String> bodyRequestMapPut = new HashMap<>();
        bodyRequestMapPut.put("name", "ramlr93Modif");
        putUsers =
                given().contentType(ContentType.JSON).body(bodyRequestMapPut).put("/users/"+jsonIdCreate);
// given().contentType(ContentType.JSON).body("{\"name\":\"juan\"}").put("/users/2");
    }

    @And("the response is 200 for the put")
    public void validateResponsePut() {
        assertTrue("The response is not 200",putUsers.statusCode()==200);
    }

    @Then("the body response contains update {string}")
    public void validateResponsePutBodyUpdatedValueName(String updatedName) {
        JsonPath jsonPathUsers = new JsonPath(putUsers.body().asString());
        String jsonUserName=jsonPathUsers.getString("name");
        assertEquals("The value of the name field is not what is expected",updatedName,jsonUserName);
    }

// DELETE: eliminar un ususario
    //@Given("the following post request that add users")

    @And("the following delete request that delete user")
    public void deleteUsers(){
        JsonPath jsonPathUsers = new JsonPath(postUsers.body().asString());
        String jsonIdCreate=jsonPathUsers.getString("id");
        deleteUsers =
                given().accept(ContentType.JSON).delete("/users/"+jsonIdCreate);
    }

    @And("the response is 204 for the delete")
    public void validateCodeResponseDelete() {
        assertTrue("The response is not 204",deleteUsers.statusCode()==204);
    }

    @Then("the body response is empty")
    public void validateResponseDelete() {
        assertTrue("The value of the name field is not what is expected",
                deleteUsers.body().asString().isEmpty());
    }

}