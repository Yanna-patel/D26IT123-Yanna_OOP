import model.Account;
import model.Customer;
import model.SavingsAccount;
import service.Repository;
import util.AnnotationValidator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for the generic Repository (Practical 13) and annotations (Practical 7). */
class RepositoryAndAnnotationTest {

    @Test
    void repositoryFindsIdOfSubclassByReflection() {
        Repository<Account> repo = new Repository<>();
        Account a = new SavingsAccount("Riya", 1000, 0);

        repo.save(a);

        assertSame(a, repo.findById(a.getAccountNumber()));
        assertEquals(1, repo.count());
        assertTrue(repo.delete(a.getAccountNumber()));
        assertFalse(repo.delete(a.getAccountNumber()));
        assertNull(repo.findById(a.getAccountNumber()));
    }

    @Test
    void sameRepositoryClassWorksForCustomer() {
        Repository<Customer> repo = new Repository<>();
        Customer c = new Customer("Riya", "riya@mail.com", "9876543210");

        repo.saveAll(c);

        assertEquals("Riya", repo.findById(c.getCustomerId()).getName());
    }

    @Test
    void entityWithoutIdIsRejected() {
        Repository<String> repo = new Repository<>();
        assertThrows(IllegalArgumentException.class, () -> repo.save("hello"));
    }

    @Test
    void boundedGenericAverageWorksForDifferentNumberTypes() {
        assertEquals(20.0, Repository.average(new Integer[]{10, 20, 30}));
        assertEquals(2.5, Repository.average(new Double[]{1.5, 3.5}));
        assertEquals(0.0, Repository.average(new Long[]{}));
    }

    @Test
    void negativeBalanceProducesPositiveError() {
        Account bad = new SavingsAccount("Test", -100, 0);
        String[] errors = AnnotationValidator.validate(bad);

        assertEquals(1, errors.length);
        assertEquals("must be > 0", errors[0]);
    }

    @Test
    void validAccountProducesNoErrors() {
        Account good = new SavingsAccount("Test", 100, 0);
        assertEquals(0, AnnotationValidator.validate(good).length);
    }
}
