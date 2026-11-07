
package database

import java.sql.Connection
import java.sql.DriverManager

class ConexaoBanco {

    private ConexaoBanco() {
    }

    static Connection conectar() {
        String url = System.getenv('DB_URL') ?:
                'jdbc:postgresql://localhost:5432/linketinder'

        String usuario = System.getenv('DB_USER')
        String senha = System.getenv('DB_PASSWORD')

        if (!usuario || !senha) {
            throw new IllegalStateException(
                    'Configure as variáveis DB_USER e DB_PASSWORD.'
            )
        }

        return DriverManager.getConnection(url, usuario, senha)
    }
}
