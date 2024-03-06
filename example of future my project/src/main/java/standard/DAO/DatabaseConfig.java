package standard.DAO;



import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    private static HikariDataSource ds;

    static {
        Properties properties = new Properties();
        try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream("dbconfig.properties")) {
            if (input == null) {
                throw new RuntimeException("Failed to find dbconfig.properties file");
            }
            properties.load(input);
            String DB_URL = properties.getProperty("dbUrl");
            String DB_USERNAME = properties.getProperty("dbUsername");
            String DB_PASSWORD = properties.getProperty("dbPassword");

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(DB_URL);
            config.setUsername(DB_USERNAME);
            config.setPassword(DB_PASSWORD);
            config.setMaximumPoolSize(10);  

            ds = new HikariDataSource(config);

            try (Connection connection = ds.getConnection()) {
                logger.info("Successfully connected to database.");
            } catch (SQLException e) {
                e.printStackTrace();
                logger.error("Failed to connect to the database.");
                throw new RuntimeException("Failed to connect to the database.", e);
            }

        } catch (IOException ex) {
            throw new RuntimeException("Failed to read DB credentials from config", ex);
        }
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
