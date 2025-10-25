package br.com.infnet.tests;
import br.com.infnet.model.Funcionario;
import br.com.infnet.service.FuncionarioService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class FuncionarioUnitTest {

    @Test
    void testFuncionarioModelCoverage() {
        // Testando construtor e getters
        Funcionario f = new Funcionario(10, "Novo", "Cargo Novo");
        assertEquals(10, f.getId());
        assertEquals("Novo", f.getNome());
        assertEquals("Cargo Novo", f.getCargo());

        // Testando setters
        f.setId(20);
        f.setNome("Atualizado");
        f.setCargo("Cargo Atualizado");
        assertEquals(20, f.getId());
        assertEquals("Atualizado", f.getNome());
        assertEquals("Cargo Atualizado", f.getCargo());
    }

    @Test
    void testFuncionarioServiceFailureBranches() {
        FuncionarioService service = new FuncionarioService();
        final int NON_EXISTENT_ID = 999;

        // Testando o caminho de falha no findById
        assertTrue(service.findById(NON_EXISTENT_ID).isEmpty(),
                "findById deve retornar vazio para ID inexistente.");

        // Testando o caminho de falha no updateFuncionario
        assertDoesNotThrow(() -> service.updateFuncionario(
                        NON_EXISTENT_ID, "Nome Falso", "Cargo Falso"),
                "Update em ID inexistente não deve lançar exceção.");

        // Testando o caminho de falha no deleteFuncionario
        int initialSize = service.listar().size();
        service.deleteFuncionario(NON_EXISTENT_ID);
        assertEquals(initialSize, service.listar().size(),
                "Delete em ID inexistente não deve alterar o tamanho da lista.");
    }
}