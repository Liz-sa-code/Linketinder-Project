
package database

import java.sql.Connection
import java.sql.DriverManager

class ConexaoBanco implements ConnectionProvider {

    @Override
    Connection conectar() {
        String url = System.getenv('DB_URL')
        String usuario = System.getenv('DB_USER')
        String senha = System.getenv('DB_PASSWORD')

        if (!url || !usuario || !senha) {
            throw new IllegalStateException(
                    'Configure as variáveis DB_URL, DB_USER e DB_PASSWORD.'
            )
        }

        return DriverManager.getConnection(url, usuario, senha)
    }
}
