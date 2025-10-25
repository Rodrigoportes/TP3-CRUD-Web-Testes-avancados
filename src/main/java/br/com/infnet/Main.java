package br.com.infnet;

import br.com.infnet.controller.FuncionarioController;
import io.javalin.Javalin;

public class Main {

    public static void main(String[] args) {
        startApp(7000);
    }

    public static Javalin startApp(int port) {
        Javalin app = Javalin.create(config ->{
            // Se necessário, configurações básicas
        }).start(port);

        new FuncionarioController(app);

        // Rota de redirecionamento
        app.get("/", ctx -> ctx.redirect("/funcionarios"));

        System.out.println("Aplicação iniciada em: http://localhost:" + port + "/");
        return app;
    }
}