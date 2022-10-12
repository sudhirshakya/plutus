package co.sorus.plutus.core.jaxrs;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
@PreMatching
@Priority(0)
public class PathFilter implements ContainerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(PathFilter.class);

    @Override
    public void filter(ContainerRequestContext arg0) throws IOException {
        logger.info("{}: {}.", arg0.getMethod(), arg0.getUriInfo().getAbsolutePath());
    }
}
