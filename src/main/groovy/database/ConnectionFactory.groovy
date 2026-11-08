package database

class ConnectionFactory {

    private static final ConnectionFactory INSTANCIA =
            new ConnectionFactory()

    private ConnectionFactory() {
    }

    static ConnectionFactory getInstancia() {
        return INSTANCIA
    }

    ConnectionProvider criar(String banco = 'postgresql') {

        if (!banco) {
            throw new IllegalArgumentException(
                    'O banco de dados deve ser informado.'
            )
        }

        switch (banco.toLowerCase()) {
            case 'postgresql':
                return new ConexaoBanco()

            case 'mysql':
                return new ConexaoMySQL()

            default:
                throw new IllegalArgumentException(
                        "Banco de dados não suportado: ${banco}"
                )
        }
    }
}