package co.sorus.plutus.products;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import co.sorus.plutus.products.services.CategoryService;

@Path("categories")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoryResource {

    @Inject
    CategoryService service;

    @PathParam("code")
    String code;

    @GET
    public List<CategoryDto> fetch(@QueryParam("since") long since) {
        return service.fetch(since);
    }

    @POST
    public CategoryDto add(@Valid final CategoryDto dto) {
        return service.add(dto);
    }

    @PUT
    @Path("{code}")
    public CategoryDto update(final CategoryDto dto) {
        return service.update(code, dto);
    }

    @DELETE
    @Path("{code}")
    public CategoryDto delete() {
        return service.delete(code);
    }

}
