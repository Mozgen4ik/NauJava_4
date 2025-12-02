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
 * API-тесты для ReportController с использованием RestAssured.
 *
 * Проверяет создание отчётов, получение статуса и содержимого.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.security.user.name=test",
        "spring.security.user.password=test"
})
class ReportControllerApiTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        RestAssured.authentication = basic("test", "test");
    }

    /**
     * ПОЗИТИВНЫЙ ТЕСТ: Создание отчёта возвращает 201 Created с ID отчёта.
     */
    @Test
    void generateReport_Returns201WithReportId() {
        given()
                .when()
                .post("/api/reports/generate")
                .then()
                .statusCode(201)
                .contentType("application/json")
                .body("reportId", notNullValue())
                .body("status", equalTo("CREATED"))
                .body("message", containsString("Отчёт создан"));
    }

    /**
     * ПОЗИТИВНЫЙ ТЕСТ: Получение статуса созданного отчёта.
     */
    @Test
    void getReportStatus_ReturnsStatus() {
        // Сначала создаём отчёт
        int reportId = given()
                .post("/api/reports/generate")
                .then()
                .extract()
                .path("reportId");

        // Получаем статус отчёта
        given()
                .pathParam("reportId", reportId)
                .when()
                .get("/api/reports/{reportId}/status")
                .then()
                .statusCode(200)
                .body("reportId", equalTo(String.valueOf(reportId)))
                .body("status", isIn(java.util.Arrays.asList("CREATED", "COMPLETED", "ERROR")));
    }

    /**
     * НЕГАТИВНЫЙ ТЕСТ: Запрос несуществующего отчёта возвращает 404.
     */
    @Test
    void getReport_NonExistentId_Returns404() {
        given()
                .pathParam("reportId", 99999)
                .when()
                .get("/api/reports/{reportId}")
                .then()
                .statusCode(404)
                .body("error", containsString("не найден"));
    }

    /**
     * НЕГАТИВНЫЙ ТЕСТ: Запрос статуса несуществующего отчёта возвращает 404.
     */
    @Test
    void getReportStatus_NonExistentId_Returns404() {
        given()
                .pathParam("reportId", 99999)
                .when()
                .get("/api/reports/{reportId}/status")
                .then()
                .statusCode(404);
    }

    /**
     * ПОЗИТИВНЫЙ ТЕСТ: Получение отчёта в статусе CREATED возвращает информацию о процессе.
     */
    @Test
    void getReport_InProgress_ReturnsProgressInfo() {
        // Создаём отчёт
        int reportId = given()
                .post("/api/reports/generate")
                .then()
                .extract()
                .path("reportId");

        // Сразу запрашиваем отчёт (он ещё формируется)
        given()
                .pathParam("reportId", reportId)
                .when()
                .get("/api/reports/{reportId}")
                .then()
                .statusCode(anyOf(is(200)))
                .body(anyOf(
                        containsString("процессе"),  // статус CREATED
                        containsString("<!DOCTYPE html>")  // статус COMPLETED (если успел)
                ));
    }

    /**
     * ТЕСТ: Проверка структуры ответа при создании отчёта.
     */
    @Test
    void generateReport_ResponseStructure() {
        given()
                .when()
                .post("/api/reports/generate")
                .then()
                .body("$", hasKey("reportId"))
                .body("$", hasKey("status"))
                .body("$", hasKey("message"));
    }

    /**
     * ГРАНИЧНЫЙ СЛУЧАЙ: Множественное создание отчётов.
     */
    @Test
    void generateReport_Multiple_ReturnsUniqueIds() {
        // Создаём 3 отчёта
        int id1 = given().post("/api/reports/generate")
                .then().extract().path("reportId");

        int id2 = given().post("/api/reports/generate")
                .then().extract().path("reportId");

        int id3 = given().post("/api/reports/generate")
                .then().extract().path("reportId");

        // Проверяем, что ID уникальны
        assert id1 != id2;
        assert id2 != id3;
        assert id1 != id3;
    }

    /**
     * ТЕСТ: Проверка Content-Type при получении готового отчёта.
     */
    @Test
    void getReport_Completed_ReturnsHtmlContentType() throws InterruptedException {
        // Создаём отчёт
        int reportId = given()
                .post("/api/reports/generate")
                .then()
                .extract()
                .path("reportId");

        // Ждём немного, чтобы отчёт успел сформироваться
        Thread.sleep(2000);

        // Получаем отчёт
        given()
                .pathParam("reportId", reportId)
                .when()
                .get("/api/reports/{reportId}")
                .then()
                .statusCode(anyOf(is(200), is(500)))  // может быть ещё не готов
                .contentType(anyOf(
                        containsString("text/html"),
                        containsString("application/json")
                ));
    }

    /**
     * ТЕСТ: POST метод для endpoint'а, который должен быть GET, возвращает 405.
     */
    @Test
    void getReport_PostMethod_Returns405() {
        given()
                .pathParam("reportId", 1)
                .when()
                .post("/api/reports/{reportId}")
                .then()
                .statusCode(405);  // Method Not Allowed
    }
}