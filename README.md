Objetivo
Este manual descreve os procedimentos para iniciar a aplicação web CRUD de Funcionários, executar a suíte de testes automatizados (Selenium/JUnit) e gerar/interpretar o relatório de cobertura de código (JaCoCo), comprovando a qualidade do sistema.

Pré-Requisitos do Ambiente
Java Development Kit (JDK): Versão 21 ou superior (conforme configurado no pom.xml).

Apache Maven: Versão 3.x ou superior.

Terminal/Linha de Comando: Acesso ao terminal na pasta raiz do projeto (onde se encontra o arquivo pom.xml).

Conexão com a Internet: Necessária para o Maven baixar as dependências na primeira execução.

I. Iniciação do Sistema (Aplicação Web)
O servidor Javalin será iniciado na porta 7000.

1. Iniciar Via IDE (Recomendado)
Abra o projeto na IDE (IntelliJ IDEA, Eclipse, etc.).

Navegue até a classe br.com.infnet.Main.java.

Execute o método main().

2. Verificar o Acesso
Console: O console deve exibir a mensagem: Aplicação iniciada em: http://localhost:7000/.

Navegador: A aplicação CRUD de Funcionários estará acessível em: http://localhost:7000/funcionarios.

II. Execução da Suíte de Testes Automatizados (Selenium)
A suíte de testes (7 cenários) valida o CRUD, os testes parametrizados e as proteções de segurança (Fail Early, Fuzzing).

IMPORTANTE: Certifique-se de que a aplicação NÃO ESTEJA RODANDO MANUALMENTE antes deste passo. O comando abaixo irá iniciar e parar o servidor automaticamente para garantir o isolamento de estado.

1. Comando de Execução Completa
Execute este comando no terminal, a partir da pasta raiz do projeto (pom.xml):

Bash

mvn clean verify
