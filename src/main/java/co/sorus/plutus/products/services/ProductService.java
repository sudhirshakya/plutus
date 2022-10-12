package co.sorus.plutus.products.services;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.sorus.plutus.products.ProductDto;

@ApplicationScoped
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Inject
    public ProductMapper mapper;

    @Inject
    ProductRepository repository;

    public List<ProductDto> fetch(String categoryCode) {
        logger.info("Fetching all products in category ().", categoryCode);
        return null;
    }

    public ProductDto add(String categoryCode, @Valid ProductDto dto) {
        return null;
    }

    public ProductDto update(String categoryCode, String code, JsonObject dto) {
        // TODO Auto-generated method stub
        return null;
    }

    public ProductDto delete(String categoryCode, String code) {
        // TODO Auto-generated method stub
        return null;
    }

}
