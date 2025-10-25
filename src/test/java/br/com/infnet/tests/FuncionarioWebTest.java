package br.com.infnet.tests;
import br.com.infnet.Main;
import br.com.infnet.pages.FuncionarioFormPage;
import br.com.infnet.pages.FuncionarioListPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

// Oss testes iniciem e parem o servidor
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FuncionarioWebTest {

    private static final String BASE_URL = "http://localhost:7000";
    private static WebDriver driver;
    private io.javalin.Javalin app;
    private FuncionarioListPage listPage;
    private FuncionarioFormPage formPage;

    // Garante que o driver é fechado no final
    @BeforeAll
    public static void setupDriver() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        driver = new ChromeDriver(options);
    }

    @BeforeEach
    public void setupApp() {
        app = Main.startApp(7000);
        listPage = new FuncionarioListPage(driver);
        formPage = new FuncionarioFormPage(driver);
    }

    @AfterEach
    public void teardownApp() {
        if (app != null) {
            app.stop();
        }
    }

    @AfterAll
    public static void tearDownDriver() {
        if (driver != null) {
            driver.quit();
        }
    }


    //CREATE - Teste
    @Order(1) // Força a execução primeiro para ter dados de teste
    @ParameterizedTest(name = "Cadastro: {0} - {1}")
    @CsvSource({
            "Pedro Alvares, Gerente de Projetos",
            "Ana Carolina, Desenvolvedora Júnior"
    })
    public void testCreateNewFuncionario(String nome, String cargo) {
        listPage.navigateToList();
        listPage.clickNewFuncionario();
        formPage.fillForm(nome, cargo);
        formPage.submitForm();

        // Verifica o sucesso
        assertTrue(listPage.isFuncionarioPresent(nome, cargo),
                "O novo funcionário não foi encontrado na lista após o cadastro.");
    }

    // UPDATE - Teste

    @Test
    @Order(2)
    public void testUpdateFuncionario() {
        String nomeAntigo = "João Silva";
        String novoCargo = "Tech Lead Sênior";

        listPage.navigateToList();
        listPage.clickEdit(nomeAntigo);

        formPage.fillForm(nomeAntigo, novoCargo);
        formPage.submitForm();

        // Verifica o sucesso
        assertTrue(listPage.isFuncionarioPresent(nomeAntigo, novoCargo),
                "O cargo do funcionário não foi atualizado na lista.");
    }


    // DELETE - Teste

    @Test
    @Order(3)
    public void testDeleteFuncionario() {
        String nomeParaDeletar = "Clara Oliveira";

        listPage.navigateToList();
        listPage.deleteFuncionario(nomeParaDeletar);

        // Verifica a ausência
        String pageText = driver.getPageSource();
        assertTrue(!pageText.contains(nomeParaDeletar),
                "O funcionário não foi deletado da lista.");
    }

    @Test
    @Order(4)
    @DisplayName("Teste Negativo: Bloqueio de Submissão HTML (Campo Required Vazio)")
    public void testNegativeCreateEmptyFields() {
        listPage.navigateToList();
        listPage.clickNewFuncionario();

        // Tenta submeter o formulário DEIXANDO um campo vazio (Cargo: "")
        formPage.fillForm("Nome OK", "");

        String currentUrlBeforeClick = driver.getCurrentUrl();

        // Clica no botão. O navegador deve BLOQUEAR
        formPage.clickSubmitButton();

        // Verifica se a URL PERMANECE a mesma.
        assertTrue(driver.getCurrentUrl().equals(currentUrlBeforeClick),
                "A validação HTML 'required' falhou, permitindo a mudança de URL.");
    }


    @Test
    @Order(5)
    @DisplayName("Teste Negativo: Edição de Funcionário Inexistente (404)")
    public void testNegativeEditNonExistentFuncionario() {
        final int nonExistentId = 999;

        // Tenta acessar uma rota que deve falhar
        driver.get(BASE_URL + "/funcionarios/edit/" + nonExistentId);

        // Espera a URL, garantindo que o navegador tentou carregar a página
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlContains("/edit/" + nonExistentId));

        // A string esperada pelo Controller
        String expectedErrorText = "Funcionário não encontrado";

        // Verifica o código-fonte bruto
        String pageSource = driver.getPageSource();

        boolean errorTextFound = pageSource.contains(expectedErrorText);
        boolean formTitleMissing = !pageSource.contains("Editar Item") && !pageSource.contains("form action=\"/items/edit");

        // O teste passa se uma das duas condições for verdadeira
        assertTrue(errorTextFound || formTitleMissing,
                "O erro 404 não foi exibido E a página de edição foi carregada. Falha na manipulação do ID.");
    }


    @Test
    @Order(6)
    @DisplayName("Teste de Segurança: Fuzzing e Entrada Maliciosa (Limite de Caracteres)")
    public void testFuzzingExceedsMaxLength() {
        // String que excede o limite de 50 caracteres
        String tooLongInput = "A".repeat(51);
        listPage.navigateToList();
        listPage.clickNewFuncionario();

        // Tenta submeter a string muito longa
        formPage.fillForm(tooLongInput, "Avaliador");
        formPage.clickSubmitButton();

        // Adiciona uma espera passiva para o servidor responder com 400
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        String expectedErrorMessage = "Erro: O limite de 50 caracteres foi excedido.";

        // Verifica se o Controller bloqueou o envio e retornou a mensagem de erro.
        String pageSource = driver.getPageSource();
        assertTrue(pageSource.contains(expectedErrorMessage),
                "O Controller não bloqueou a entrada de mais de 50 caracteres e/ou não retornou a mensagem de erro correta.");
    }


}