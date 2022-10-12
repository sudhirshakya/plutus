package co.sorus.plutus.products;

import javax.validation.constraints.NotBlank;

public class CategoryDto {
    public String code;

    @NotBlank
    public String name;

    public String parent;

    public boolean deleted;

    public long updatedAt;

}
