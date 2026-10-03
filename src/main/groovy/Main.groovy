/*
 * Autora: Liz Soares
 * Projeto: Linketinder
 */

import java.util.Scanner

class Main {

    static void main(String[] args) {

        Scanner scanner = new Scanner(System.in)

        int opcao

        do {
            println "\n===== LINKETINDER ====="
            println "1 - Listar candidatos"
            println "2 - Listar empresas"
            println "0 - Sair"
            print "Escolha uma opção: "

            opcao = scanner.nextInt()

            switch (opcao) {

                case 1:
                    println "\n===== CANDIDATOS ====="
                    Dados.candidatos.each { candidato ->
                        println candidato
                    }
                    break

                case 2:
                    println "\n===== EMPRESAS ====="
                    Dados.empresas.each { empresa ->
                        println empresa
                    }
                    break

                case 0:
                    println "Encerrando o sistema..."
                    break

                default:
                    println "Opção inválida!"
            }

        } while (opcao != 0)

        scanner.close()
    }
}