package co.sorus.plutus.products.services;

import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class ProductRepository implements PanacheRepository<Product> {

    public Stream<Product> listActive(String categoryCode) {
        return find("category = ? and deleted = false", categoryCode).stream();
    }


}
