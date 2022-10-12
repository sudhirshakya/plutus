package co.sorus.plutus.extensions;

import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.dbunit.util.fileloader.CsvDataFileLoader;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseSeedingExtension implements BeforeAllCallback {

    private static final String STORE_KEY = "SEEDED.TABLES";

    private static final Namespace NAMESPACE = Namespace.create(DatabaseSeedingExtension.class);

    private static final Logger logger = LoggerFactory.getLogger(DatabaseSeedingExtension.class);

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        Optional<AnnotatedElement> element = context.getElement();
        if (element.isEmpty())
            return;

        Set<String> tables = getTablesForSeeding(context);
        Set<String> seededTables = getSeededTables(context);
        tables = difference(tables, seededTables);
        tables.forEach(this::seedTable);

        storeSeededTables(context, tables);
    }

    @SuppressWarnings("unchecked")
    private void storeSeededTables(ExtensionContext context, Set<String> tables) {
        Store store = context.getRoot().getStore(NAMESPACE);
        Set<String> seededTables = store.getOrDefault(STORE_KEY, Set.class, new HashSet<String>());
        seededTables.addAll(tables);
        store.put(STORE_KEY, seededTables);
    }

    @SuppressWarnings("unchecked")
    private Set<String> getSeededTables(ExtensionContext context) {
        return context.getRoot()
                .getStore(NAMESPACE)
                .getOrDefault(STORE_KEY, Set.class, Collections.emptySet());
    }

    private Set<String> difference(Set<String> firstSet, Set<String> secondSet) {
        firstSet.removeAll(secondSet);
        return firstSet;
    }

    private Set<String> getTablesForSeeding(ExtensionContext ctx) {
        String[] tableNames = ctx.getElement()
                .map(a -> a.getAnnotation(SeedTables.class))
                .map(SeedTables::value)
                .get();
        return new HashSet<>(Arrays.asList(tableNames));
    }

    private void seedTable(String table) {
        logger.info("Seeding table using data from file {}. ", table);
        try {
            // Load data from CSV file
            CsvDataFileLoader loader = new CsvDataFileLoader();
            URL url = this.getClass().getResource("/dumps/category/table-ordering.txt");
            IDataSet dataSet = loader.loadDataSet(url);

            // Store data into database table
            Class.forName("org.postgresql.Driver");
            String databaseUrl = System.getProperty(PostgresqlResource.DB_URL_KEY);
            Connection jdbcConnection = DriverManager.getConnection(databaseUrl,
                    PostgresqlResource.DB_USERNAME, PostgresqlResource.DB_PASSWORD);
            IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);
            DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);

        } catch (IOException | ClassNotFoundException | SQLException | DatabaseUnitException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
