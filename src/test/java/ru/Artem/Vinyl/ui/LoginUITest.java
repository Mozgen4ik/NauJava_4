package ru.Artem.Vinyl.ui;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UI-тесты с использованием Selenium WebDriver.
 *
 * Проверяет сценарии авторизации и выхода из системы через браузер.
 *
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LoginUITest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;
    private static WebDriverWait wait;

    private String baseUrl;

    /**
     * Настройка перед всеми тестами.
     * Инициализируем WebDriver и настраиваем Chrome.
     */
    @BeforeAll
    static void setUpAll() {
        // WebDriverManager автоматически загружает нужную версию ChromeDriver
        WebDriverManager.chromedriver().setup();

        // Настройки Chrome
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");  // запуск без GUI (можно убрать для отладки)
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /**
     * Настройка перед каждым тестом.
     */
    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
    }

    /**
     * Очистка после всех тестов. Закрывает браузер.
     */
    @AfterAll
    static void tearDownAll() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * ПОЗИТИВНЫЙ ТЕСТ: Успешный вход в систему.
     *
     */
    @Test
    @Order(1)
    void testSuccessfulLogin() {
        // 1. Переход на страницу логина
        driver.get(baseUrl + "/login");

        // Ждём загрузки страницы
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("username")));

        // 2. Ввод учётных данных
        WebElement usernameInput = driver.findElement(By.name("username"));
        WebElement passwordInput = driver.findElement(By.name("password"));

        usernameInput.clear();
        usernameInput.sendKeys("admin");

        passwordInput.clear();
        passwordInput.sendKeys("admin123");

        // 3. Нажатие кнопки входа
        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));
        loginButton.click();

        // 4. Проверка успешной авторизации
        // Ждём перенаправления на страницу со списком пластинок
        wait.until(ExpectedConditions.urlContains("/vinyl/view/list"));

        // Проверяем, что мы на правильной странице
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/vinyl/view/list"),
                "После успешного входа должен быть редирект на /vinyl/view/list");

        // Проверяем, что отображается имя пользователя
        WebElement userInfo = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".user-info, [sec\\:authentication='name']")));
        assertNotNull(userInfo, "Информация о пользователе должна отображаться");
    }

    /**
     * НЕГАТИВНЫЙ ТЕСТ: Вход с неверными учётными данными.
     */
    @Test
    @Order(2)
    void testLoginWithInvalidCredentials() {
        // 1. Переход на страницу логина
        driver.get(baseUrl + "/login");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("username")));

        // 2. Ввод неверных учётных данных
        WebElement usernameInput = driver.findElement(By.name("username"));
        WebElement passwordInput = driver.findElement(By.name("password"));

        usernameInput.sendKeys("admin");
        passwordInput.sendKeys("wrongpassword");

        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));
        loginButton.click();

        // 3. Проверка, что остались на странице логина
        wait.until(ExpectedConditions.urlContains("/login"));
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/login"), "Должны остаться на странице логина");

        // 4. Проверка сообщения об ошибке
        assertTrue(currentUrl.contains("error") || driver.getPageSource().contains("Неверное"),
                "Должно отображаться сообщение об ошибке");
    }

    /**
     * ПОЗИТИВНЫЙ ТЕСТ: Выход из системы.
     */
    @Test
    @Order(3)
    void testLogout() {
        // 1. Предусловие: вход в систему
        driver.get(baseUrl + "/login");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("username")));

        driver.findElement(By.name("username")).sendKeys("admin");
        driver.findElement(By.name("password")).sendKeys("admin123");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Ждём успешного входа
        wait.until(ExpectedConditions.urlContains("/vinyl/view/list"));

        // 2. Нажатие кнопки выхода
        WebElement logoutButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button.logout-btn, form[action*='logout'] button")));
        logoutButton.click();

        // 3. Проверка перенаправления на страницу логина
        wait.until(ExpectedConditions.urlContains("/login"));
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/login"),
                "После выхода должен быть редирект на страницу логина");

        // Проверка наличия параметра logout в URL
        assertTrue(currentUrl.contains("logout"),
                "URL должен содержать параметр logout");

        // 4. Проверка, что доступ к защищённым страницам закрыт
        driver.get(baseUrl + "/vinyl/view/list");

        // Должно перенаправить обратно на логин
        wait.until(ExpectedConditions.urlContains("/login"));
        currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("/login"),
                "Неавторизованный пользователь должен быть перенаправлен на логин");
    }

    /**
     * ТЕСТ: Проверка элементов страницы логина.
     */
    @Test
    @Order(4)
    void testLoginPageElements() {
        driver.get(baseUrl + "/login");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("username")));

        // Проверяем наличие основных элементов
        WebElement usernameInput = driver.findElement(By.name("username"));
        WebElement passwordInput = driver.findElement(By.name("password"));
        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));

        assertNotNull(usernameInput, "Поле username должно присутствовать");
        assertNotNull(passwordInput, "Поле password должно присутствовать");
        assertNotNull(submitButton, "Кнопка входа должна присутствовать");

        // Проверяем, что поля пустые
        assertEquals("", usernameInput.getAttribute("value"));
        assertEquals("", passwordInput.getAttribute("value"));
    }

    /**
     * ТЕСТ: Проверка ссылки на регистрацию.
     */
    @Test
    @Order(5)
    void testRegistrationLinkExists() {
        driver.get(baseUrl + "/login");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("username")));

        // Ищем ссылку на регистрацию
        WebElement registrationLink = driver.findElement(By.linkText("Зарегистрироваться"));
        assertNotNull(registrationLink, "Ссылка на регистрацию должна присутствовать");

        // Проверяем, что ссылка ведёт на /registration
        String href = registrationLink.getAttribute("href");
        assertTrue(href.contains("/registration"),
                "Ссылка должна вести на страницу регистрации");
    }

    /**
     * ИНТЕГРАЦИОННЫЙ ТЕСТ: Полный цикл - регистрация, вход, выход.
     */
    @Test
    @Order(6)
    void testFullAuthenticationCycle() {
        String uniqueUsername = "testuser_" + System.currentTimeMillis();
        String password = "testpass123";

        // 1. Регистрация
        driver.get(baseUrl + "/registration");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("username")));

        driver.findElement(By.name("username")).sendKeys(uniqueUsername);
        driver.findElement(By.name("password")).sendKeys(password);
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Должно перенаправить на страницу логина
        wait.until(ExpectedConditions.urlContains("/login"));

        // 2. Вход с новыми учётными данными
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("username")));
        driver.findElement(By.name("username")).sendKeys(uniqueUsername);
        driver.findElement(By.name("password")).sendKeys(password);
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Проверка успешного входа
        wait.until(ExpectedConditions.urlContains("/vinyl/view/list"));
        assertTrue(driver.getCurrentUrl().contains("/vinyl/view/list"));

        // 3. Выход
        WebElement logoutButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button.logout-btn, form[action*='logout'] button")));
        logoutButton.click();

        // Проверка успешного выхода
        wait.until(ExpectedConditions.urlContains("/login"));
        assertTrue(driver.getCurrentUrl().contains("/login"));
    }
}