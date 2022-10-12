package co.sorus.plutus.products.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.enterprise.event.Event;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.sorus.plutus.products.CategoryDto;
import co.sorus.plutus.products.services.Category;
import co.sorus.plutus.products.services.CategoryDeletionEvent;
import co.sorus.plutus.products.services.CategoryMapper;
import co.sorus.plutus.products.services.CategoryRepository;
import co.sorus.plutus.products.services.CategoryService;

@ExtendWith({ MockitoExtension.class })
@DisplayName("Product Categories")
class CategoryServiceTest {

    @InjectMocks
    private CategoryService sut;

    @Mock
    private CategoryRepository repo;

    @Mock
    private Event<CategoryDeletionEvent> event;

    @BeforeEach
    public void setup() {
        sut.mapper = new CategoryMapper();
    }

    @Nested
    @DisplayName("Fetch Categories")
    public class Fetch {

        @Test
        @DisplayName("Empty list")
        void testFetch_Empty() {
            // arrange
            when(repo.listActive()).thenReturn(Stream.empty());

            // act
            List<CategoryDto> categories = sut.fetch(0);

            // assert
            assertThat(categories).isEmpty();
        }

        @Test
        @DisplayName("Non empty list for sync")
        void testFetch_Sync() {
            // arrange
            Stream<Category> stream = Stream.of(createRootCategory("E", "Electronics", false),
                    createRootCategory("F", "Furniture", false),
                    createRootCategory("D", "Deleted Category", true),
                    createChildCategory("F.C", "Chair"));
            when(repo.listUpdatedSince(anyLong())).thenReturn(stream);

            // act
            List<CategoryDto> categories = sut.fetch(100);

            // assert
            assertThat(categories).hasSize(4)
                    .extracting("code", "name", "parent", "deleted")
                    .contains(tuple("F", "Furniture", "", false),
                            tuple("E", "Electronics", "", false),
                            tuple("D", "Deleted Category", "", false),
                            tuple("F.C", "Chair", "F", false));
        }

        @Test
        @DisplayName("Non empty list of all categories")
        void testFetch_All() {
            // arrange
            Stream<Category> stream = Stream.of(createRootCategory("E", "Electronics", false),
                    createRootCategory("F", "Furniture", false),
                    createChildCategory("F.C", "Chair"));
            when(repo.listActive()).thenReturn(stream);

            // act
            List<CategoryDto> categories = sut.fetch(0);

            // assert
            assertThat(categories).hasSize(3)
                    .extracting("code", "name", "parent", "deleted")
                    .contains(tuple("F", "Furniture", "", false),
                            tuple("E", "Electronics", "", false),
                            tuple("F.C", "Chair", "F", false));
        }
    }

    @Nested
    @DisplayName("Add Category")
    public class Add {

        @Test
        @DisplayName("Valid root category")
        void testAdd_valid_root_category() {
            // arrange
            CategoryDto dto = createCategoryDto("f", "", "", false);
            CategoryDto expected = createCategoryDto("F", "", "", false);
            when(repo.findByCode(anyString())).thenReturn(Optional.empty());

            // act
            CategoryDto actual = sut.add(dto);

            // assert
            assertThat(actual).usingRecursiveComparison()
                    .ignoringFields("updatedAt")
                    .isEqualTo(expected);
            verify(repo).persist(any(Category.class));

        }

        @Test
        @DisplayName("Valid child category")
        void testAdd_valid_child_category() {
            // arrange
            CategoryDto dto = createCategoryDto("t", "Table", "F", false);
            CategoryDto expected = createCategoryDto("F.T", "Table", "F", false);
            Optional<Category> parent = Optional.of(createRootCategory("F", "Furniture", false));
            when(repo.findByCode("F")).thenReturn(parent);
            when(repo.findByCode("F.T")).thenReturn(Optional.empty());

            // act
            CategoryDto actual = sut.add(dto);

            // assert
            assertThat(actual).usingRecursiveComparison()
                    .ignoringFields("updatedAt")
                    .isEqualTo(expected);
            verify(repo).persist(any(Category.class));
        }

        @Test
        @DisplayName("Invalid code")
        void testAdd_invalid_code() {
            // arrange
            CategoryDto dto = createCategoryDto("t", "Table", "", false);
            Optional<Category> category = Optional.of(createRootCategory("F", "Furniture", false));
            when(repo.findByCode("T")).thenReturn(category);

            // act - assert
            assertThatExceptionOfType(BadRequestException.class)
                    .isThrownBy(() -> {
                        sut.add(dto);
                    })
                    .withMessage("Category with code(T) already exists.");
            verify(repo, never()).persist(any(Category.class));
        }

        @Test
        @DisplayName("Invalid parent")
        void testAdd_invalid_parent() {
            // arrange
            CategoryDto dto = createCategoryDto("c", "Chair", "F", false);
            when(repo.findByCode(anyString())).thenReturn(Optional.empty());

            // act - assert
            assertThatExceptionOfType(BadRequestException.class)
                    .isThrownBy(() -> {
                        sut.add(dto);
                    })
                    .withMessage("Category with code(F) does not exist.");
            verify(repo, never()).persist(any(Category.class));
        }
    }

    @Nested
    @DisplayName("Update Category")
    public class Update {

        @Test
        @DisplayName("Invalid code")
        void testUpdate_invalid_code() {
            // arrange
            CategoryDto dto = createCategoryDto("C", "Chair", "", false);
            when(repo.findByCode(anyString())).thenReturn(Optional.empty());

            // act - assert
            assertThatExceptionOfType(NotFoundException.class)
                    .isThrownBy(() -> {
                        sut.update("C", dto);
                    })
                    .withMessage("Category with code(C) was not found.");
            verify(repo, never()).persist(any(Category.class));
        }

        @Test
        @DisplayName("Valid")
        void testUpdate_valid() {
            // arrange
            CategoryDto dto = createCategoryDto("F", "New Furniture", "", false);
            CategoryDto expected = createCategoryDto("F", "New Furniture", "", false);
            Optional<Category> category = Optional.of(createRootCategory("F", "Furniture", false));
            when(repo.findByCode(anyString())).thenReturn(category);

            // act - assert
            CategoryDto actual = sut.update("F", dto);

            // assert
            assertThat(actual).usingRecursiveComparison()
                    .ignoringFields("updatedOn")
                    .isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("Delete Category")
    public class Delete {
        @Test
        @DisplayName("Invalid code")
        void testDelete_invalid_code() {
            // arrange
            when(repo.findByCode(anyString())).thenReturn(Optional.empty());

            // act - assert
            assertThatExceptionOfType(NotFoundException.class)
                    .isThrownBy(() -> {
                        sut.delete("C");
                    })
                    .withMessage("Category with code(C) was not found.");
            verify(repo, never()).persist(any(Category.class));
        }

        @Test
        @DisplayName("Valid")
        void testDelete_valid() {
            // arrange
            CategoryDto expected = createCategoryDto("F", "Furniture", "", true);
            Optional<Category> category = Optional.of(createRootCategory("F", "Furniture", false));
            when(repo.findByCode(anyString())).thenReturn(category);

            // act - assert
            CategoryDto actual = sut.delete("F");

            // assert
            assertThat(actual).usingRecursiveComparison()
                    .ignoringFields("updatedOn")
                    .isEqualTo(expected);
            verify(repo).persist(any(Category.class));
            verify(event).fire(any(CategoryDeletionEvent.class));
        }
    }

    private Category createChildCategory(String code, String name) {
        Category category = new Category();
        category.code = code;
        category.deleted = false;
        category.name = name;
        category.parent = createRootCategory("F", "Furniture", false);
        return category;
    }

    private Category createRootCategory(String code, String name, boolean deleted) {
        Category category = new Category();
        category.code = code;
        category.deleted = false;
        category.name = name;
        category.updatedAt = 1000;
        return category;
    }

    private CategoryDto createCategoryDto(String code, String name, String parent, boolean deleted) {
        var dto = new CategoryDto();
        dto.code = code;
        dto.name = name;
        dto.parent = parent;
        dto.deleted = deleted;
        dto.updatedAt = 1000;
        return dto;
    }
}
