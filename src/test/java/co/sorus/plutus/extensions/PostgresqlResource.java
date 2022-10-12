package co.sorus.plutus.extensions;

import java.util.Collections;
import java.util.Map;

import org.testcontainers.containers.PostgreSQLContainer;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class PostgresqlResource implements QuarkusTestResourceLifecycleManager {

    static PostgreSQLContainer<?> db;

    protected static final String DB_USERNAME = "postgres";

    protected static final String DB_PASSWORD = "postgres";

    protected static final String DB_URL_KEY = "database.url.key";

    @SuppressWarnings("resource")
    @Override
    public Map<String, String> start() {
        db = new PostgreSQLContainer<>("postgres:13.2-alpine")
                .withDatabaseName("plutustest")
                .withUsername(DB_USERNAME)
                .withPassword(DB_PASSWORD);
        db.start();
        System.out.println("............ starting the database =======================================>>>> " + db.getJdbcUrl());
        System.setProperty(DB_URL_KEY, db.getJdbcUrl());
        return Collections.singletonMap(
                "quarkus.datasource.jdbc.url", db.getJdbcUrl());
    }

    @Override
    public void stop() {
        db.stop();
    }
}
