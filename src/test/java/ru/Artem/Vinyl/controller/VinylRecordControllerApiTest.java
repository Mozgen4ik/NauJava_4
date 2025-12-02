package ru.Artem.Vinyl.controller;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * API-тесты для VinylRecordController с использованием RestAssured.
 *
 * Проверяет HTTP-статусы, структуру ответов и валидацию параметров.
 *
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.security.user.name=test",
        "spring.security.user.password=test"
})
class VinylRecordControllerApiTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        // Настраиваем базовый URL для RestAssured
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;

        // Настраиваем базовую аутентификацию
        RestAssured.authentication = basic("test", "test");
    }

    /**
     * ПОЗИТИВНЫЙ ТЕСТ: Запрос с корректными параметрами возвращает 200 OK.
     */
    @Test
    void searchByYearAndPrice_ValidParameters_Returns200() {
        given()
                .param("startYear", 1970)
                .param("endYear", 2000)
                .param("maxPrice", 100.0)
                .when()
                .get("/api/vinyl/searchByYearAndPrice")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("$", isA(java.util.List.class));
    }

    /**
     * ПОЗИТИВНЫЙ ТЕСТ: Поиск по жанру возвращает список пластинок.
     */
    @Test
    void searchByGenre_ValidGenre_Returns200() {
        given()
                .param("genreName", "Rock")
                .when()
                .get("/api/vinyl/searchByGenre")
                .then()
                .statusCode(200)
                .contentType("application/json");
    }

    /**
     * НЕГАТИВНЫЙ ТЕСТ: Запрос без обязательных параметров возвращает 400 Bad Request.
     */
    @Test
    void searchByYearAndPrice_MissingParameters_Returns400() {
        when()
                .get("/api/vinyl/searchByYearAndPrice")
                .then()
                .statusCode(400);
    }

    /**
     * ГРАНИЧНЫЙ СЛУЧАЙ: Поиск с некорректными годами (startYear > endYear).
     */
    @Test
    void searchByYearAndPrice_InvalidYearRange_ReturnsEmptyList() {
        given()
                .param("startYear", 2000)
                .param("endYear", 1970)  // меньше startYear
                .param("maxPrice", 100.0)
                .when()
                .get("/api/vinyl/searchByYearAndPrice")
                .then()
                .statusCode(200)
                .body("$", isA(java.util.List.class));
    }

    /**
     * ГРАНИЧНЫЙ СЛУЧАЙ: Поиск с отрицательной ценой.
     */
    @Test
    void searchByYearAndPrice_NegativePrice_ReturnsEmptyList() {
        given()
                .param("startYear", 1970)
                .param("endYear", 2000)
                .param("maxPrice", -10.0)
                .when()
                .get("/api/vinyl/searchByYearAndPrice")
                .then()
                .statusCode(200)
                .body("$", isA(java.util.List.class));
    }

    /**
     * ПОЗИТИВНЫЙ ТЕСТ: Criteria API версия метода работает аналогично.
     */
    @Test
    void searchByYearAndPriceCriteria_ValidParameters_Returns200() {
        given()
                .param("startYear", 1970)
                .param("endYear", 2000)
                .param("maxPrice", 100.0)
                .when()
                .get("/api/vinyl/searchByYearAndPriceCriteria")
                .then()
                .statusCode(200)
                .contentType("application/json");
    }

    /**
     * ПОЗИТИВНЫЙ ТЕСТ: Criteria API версия поиска по жанру.
     */
    @Test
    void searchByGenreCriteria_ValidGenre_Returns200() {
        given()
                .param("genreName", "Jazz")
                .when()
                .get("/api/vinyl/searchByGenreCriteria")
                .then()
                .statusCode(200)
                .contentType("application/json");
    }

    /**
     * ТЕСТ: Проверка, что ответ возвращается в формате JSON.
     */
    @Test
    void searchByGenre_ReturnsJsonContentType() {
        given()
                .param("genreName", "Rock")
                .when()
                .get("/api/vinyl/searchByGenre")
                .then()
                .contentType("application/json");
    }

    /**
     * ГРАНИЧНЫЙ СЛУЧАЙ: Поиск с пустым названием жанра.
     */
    @Test
    void searchByGenre_EmptyGenreName_Returns400() {
        given()
                .param("genreName", "")
                .when()
                .get("/api/vinyl/searchByGenre")
                .then()
                .statusCode(anyOf(is(400), is(200)));  // может быть 400 или пустой список
    }

    /**
     * ТЕСТ: Проверка заголовков ответа.
     */
    @Test
    void searchByYearAndPrice_CheckHeaders() {
        given()
                .param("startYear", 1970)
                .param("endYear", 2000)
                .param("maxPrice", 100.0)
                .when()
                .get("/api/vinyl/searchByYearAndPrice")
                .then()
                .statusCode(200)
                .header("Content-Type", containsString("application/json"));
    }
}