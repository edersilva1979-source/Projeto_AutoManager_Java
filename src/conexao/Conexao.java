package conexao;

import java.sql.*;
public class Conexao {
    public static Connection conectar() throws SQLException {
        String url = System.getenv().getOrDefault("AUTO_DB_URL", "jdbc:postgresql://localhost/AutoManager_T261T");
        String usuario = System.getenv().getOrDefault("AUTO_DB_USER", "postgres");
        String senha = System.getenv().getOrDefault("AUTO_DB_PASSWORD", "root");
        return DriverManager.getConnection(url, usuario, senha);
    }
}
