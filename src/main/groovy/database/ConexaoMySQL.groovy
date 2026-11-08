package database

import java.sql.Connection
import java.sql.DriverManager

class ConexaoMySQL implements ConnectionProvider {

    @Override
    Connection conectar() {
        String url = System.getenv('MYSQL_DB_URL')
        String usuario = System.getenv('MYSQL_DB_USER')
        String senha = System.getenv('MYSQL_DB_PASSWORD')

        if (!url || !usuario || !senha) {
            throw new IllegalStateException(
                    'Configure MYSQL_DB_URL, MYSQL_DB_USER e MYSQL_DB_PASSWORD.'
            )
        }

        return DriverManager.getConnection(url, usuario, senha)
    }
}