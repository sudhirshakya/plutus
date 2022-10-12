package co.sorus.plutus.products;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import co.sorus.plutus.extensions.IntegrationTest;
import co.sorus.plutus.extensions.PostgresqlResource;
import co.sorus.plutus.extensions.SeedTables;
import co.sorus.plutus.extensions.TimedTest;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@QuarkusTestResource(PostgresqlResource.class)
@IntegrationTest
@SeedTables({ "category" })
@TimedTest
class CategoryResourceTest {

    @Test
    @Order(10)
    @DisplayName("Fetch all categories")
    void testFetch_all() {
        JsonArray response = given()
            .when()
                .get("/categories")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .as(JsonArray.class);
        assertThat(response).hasSize(5);
    }

    @Test
    @Order(20)
    @DisplayName("Sync categories")
    void testFetch_sync() {
        JsonArray response = given()
                .param("since", 100)
            .when()
                .get("/categories")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .as(JsonArray.class);
        assertThat(response).hasSize(6);
    }

    @Test
    @Order(30)
    @DisplayName("Add Root Category")
    void testAdd_Root_Category() {
        long currentTime = System.currentTimeMillis();
        JsonObject payload = Json.createObjectBuilder()
                .add("name", "Furniture")
                .add("code", "F")
                .build();
        given()
                .contentType(ContentType.JSON)
                .body(payload.toString())
            .when()
                .post("/categories")
            .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("name", is("Furniture"))
                .body("code", is("F"))
                .body("parent", is(""))
                .body("deleted", is(false))
                .body("updatedAt", greaterThanOrEqualTo(currentTime));
    }

}
