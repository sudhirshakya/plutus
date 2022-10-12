package co.sorus.plutus.products;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.json.bind.annotation.JsonbCreator;
import javax.validation.constraints.NotBlank;

public record ProductDto(@NotBlank String category, String code, @NotBlank String name, String description,
        List<String> images, BigDecimal price, double rating, Map<String, String> attributes,
        long updatedAt) {

    @JsonbCreator
    public ProductDto {
    }
}
