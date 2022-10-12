package co.sorus.plutus.products;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import co.sorus.plutus.products.services.ProductService;

@Path("categories/{categoryCode}/products")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {

    @Inject
    ProductService service;

    @PathParam("categoryCode")
    String categoryCode;

    @PathParam("code")
    String code;

    @GET
    public List<ProductDto> fetch() {
        return service.fetch(categoryCode);
    }

    @POST
    public ProductDto add(@Valid final ProductDto dto) {
        return service.add(categoryCode, dto);
    }

    @PATCH
    @Path("{code}")
    public ProductDto update(final JsonObject dto) {
        return service.update(categoryCode, code, dto);
    }

    @DELETE
    @Path("{code}")
    public ProductDto delete() {
        return service.delete(categoryCode, code);
    }
}
