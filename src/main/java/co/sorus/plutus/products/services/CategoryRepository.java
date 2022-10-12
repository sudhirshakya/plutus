package co.sorus.plutus.products.services;

import java.util.Optional;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class CategoryRepository implements PanacheRepository<Category> {

    public Optional<Category> findByCode(String code) {
        return find("code", code)
                .firstResultOptional();
    }

    public Stream<Category> listUpdatedSince(long since) {
        return find("updatedAt > ?1", since).stream();
    }

    public Stream<Category> listActive() {
        return find("deleted = false").stream();
    }

    public Stream<Category> listChildren(Category category) {
        return find("parent = ?1 and deleted = false", category).stream();
    }
}
