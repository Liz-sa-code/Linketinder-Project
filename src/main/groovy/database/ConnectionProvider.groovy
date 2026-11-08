
package database

import java.sql.Connection

interface ConnectionProvider {
    Connection conectar()
}
