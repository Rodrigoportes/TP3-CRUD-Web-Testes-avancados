package br.com.infnet.service;

import br.com.infnet.model.Funcionario;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional; // NOVO: Importação de Optional

public class FuncionarioService {
    private final List<Funcionario> funcionarios = new ArrayList<>();
    private int nextId = 1;

    public FuncionarioService() {
        // Dados iniciais
        addFuncionario("João Silva", "Engenheiro de Software");
        addFuncionario("Clara Oliveira", "Analista de RH");
    }

    public void addFuncionario(String nome, String cargo) {
        funcionarios.add(new Funcionario(nextId++, nome, cargo));
    }


     //Busca um funcionário pelo ID e retorna um Optional contendo o funcionário, se encontrado.

    public Optional<Funcionario> findById(int id) {
        return funcionarios.stream().filter(
                (Funcionario funcionario) -> funcionario.getId() == id
        ).findFirst();
    }


     //Atualiza um funcionário, se ele for encontrado.

    public void updateFuncionario(int id, String nome, String cargo) {
        findById(id).ifPresent(funcionario -> {
            funcionario.setNome(nome);
            funcionario.setCargo(cargo);
        });

    }


     //Deleta um funcionário.

    public void deleteFuncionario(int id) {

        funcionarios.removeIf((Funcionario funcionario) -> funcionario.getId() == id);
    }

    public List<Funcionario> listar(){
        return funcionarios;
    }
}
