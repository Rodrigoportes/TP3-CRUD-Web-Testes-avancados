package br.com.infnet.controller;

import br.com.infnet.model.Funcionario;
import br.com.infnet.service.FuncionarioService;
import br.com.infnet.view.FuncionarioView;
import io.javalin.Javalin;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FuncionarioController {

    private static final int MAX_LENGTH = 50;
    private final FuncionarioService service = new FuncionarioService();

    public FuncionarioController(Javalin app) {
        // Listar Funcionários (GET /funcionarios)
        app.get("/funcionarios", ctx ->
                ctx.html(FuncionarioView.renderList(service.listar())));

        // Formulário para Novo Funcionário (GET /funcionarios/new)
        app.get("/funcionarios/new", ctx ->
                ctx.html(FuncionarioView.renderForm(new HashMap<>())));

        // Adicionar Novo Funcionário  - Adição do FAIL EARLY
        app.post("/funcionarios", ctx -> {
            String nome = ctx.formParam("nome");
            String cargo = ctx.formParam("cargo");

            // Validação Fail Early para nulos/vazios
            if (nome == null || nome.trim().isEmpty() || cargo == null || cargo.trim().isEmpty()) {
                ctx.status(400).result("Erro: Nome e Cargo são obrigatórios.");
                return; // <-- CORREÇÃO: Interrompe a execução!
            }

            // Validação contra Fuzzing
            if (nome.length() > MAX_LENGTH || cargo.length() > MAX_LENGTH) {
                ctx.status(400).result("Erro: O limite de " + MAX_LENGTH + " caracteres foi excedido.");
                return; // <-- CORREÇÃO: Interrompe a execução!
            }

            service.addFuncionario(nome, cargo);
            ctx.redirect("/funcionarios");
        });

        // Editar Funcionário
        app.get("/funcionarios/edit/{id}", ctx -> {
            int id = ctx.pathParamAsClass( "id", Integer.class).get();

            Optional<Funcionario> funcionarioOptional = service.findById(id);

            if (funcionarioOptional.isPresent()) {
                Funcionario funcionario = funcionarioOptional.get();

                Map<String, Object> model = new HashMap<>();
                model.put("id", funcionario.getId());
                model.put("nome", funcionario.getNome());
                model.put("cargo", funcionario.getCargo());
                ctx.html(FuncionarioView.renderForm(model));
            } else {
                ctx.status(404).result( "Funcionário não encontrado");
            }
        });

        // Atualizar Funcionário  - Adição do FAIL EARLY

        app.post("/funcionarios/edit/{id}", ctx -> {
            int id = ctx.pathParamAsClass("id", Integer.class).get();
            String nome = ctx.formParam("nome");
            String cargo = ctx.formParam("cargo");

            // Aplica  validações de Fail Early para edição
            if (nome == null || nome.trim().isEmpty() || cargo == null || cargo.trim().isEmpty()) {
                ctx.status(400).result("Erro: Nome e Cargo são obrigatórios.");
                return; // <-- CORREÇÃO: Interrompe a execução!
            }
            if (nome.length() > MAX_LENGTH || cargo.length() > MAX_LENGTH) {
                ctx.status(400).result("Erro: O limite de " + MAX_LENGTH + " caracteres foi excedido.");
                return; // <-- CORREÇÃO: Interrompe a execução!
            }

            service.updateFuncionario(id, nome, cargo);
            ctx.redirect("/funcionarios");
        });

        // Deletar Funcionário
        app.post("/funcionarios/delete/{id}",  ctx -> {
            int id = ctx.pathParamAsClass("id", Integer.class).get();
            service.deleteFuncionario(id);
            ctx.redirect("/funcionarios");
        });
    }
}
