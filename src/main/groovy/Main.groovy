/*
 * Autora: Liz Soares
 * Projeto: Linketinder
 */

import java.util.Scanner

class Main {

    static void main(String[] args) {
        Scanner scanner = new Scanner(System.in)

        try {
            BancoDeDados bancoDeDados = new BancoDeDados()
            Menu menu = new Menu(scanner, bancoDeDados)

            menu.executar()

        } catch (Exception e) {
            println "Erro ao iniciar o Linketinder: ${e.message}"

        } finally {
            scanner.close()
        }
    }
}