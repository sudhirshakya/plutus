package co.sorus.plutus.extensions;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimingExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

    private static final Logger logger = LoggerFactory.getLogger(TimingExtension.class);

    private long startTime;

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        long endTime = System.currentTimeMillis();
        logger.info("Test '{}' took {} ms.", context.getDisplayName(), (endTime - startTime));
    }

}
