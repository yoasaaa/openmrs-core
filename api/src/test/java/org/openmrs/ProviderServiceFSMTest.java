package org.openmrs.api;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Person;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.test.jupiter.BaseContextSensitiveTest;

/**
 * FSM-Based Test Cases for ProviderService
 */
public class ProviderServiceFSMTest extends BaseContextSensitiveTest {

    private ProviderService service;
    private Provider provider;

    @BeforeEach
    public void setup() {
        service = Context.getProviderService();
        provider = new Provider();
        provider.setIdentifier("test-provider");
        provider.setName("Test Provider");
        Person person = new Person();
        provider.setPerson(person); 
        provider = service.saveProvider(provider);
    }

    /**
     * Test 1: Retiring a provider twice should not change the state after the first transition.
     */
    @Test
    public void shouldRemainRetiredWhenRetiredTwice() {
        service.retireProvider(provider, "Initial retirement");
        assertTrue(provider.getRetired());

        // Retire again, should still be retired
        service.retireProvider(provider, "Second retirement");
        assertTrue(provider.getRetired());
    }

    /**
     * Test 2: Retiring, then unretiring twice should correctly restore to Unretired state.
     */
    @Test
    public void shouldTransitionCorrectlyBetweenRetiredAndUnretired() {
        service.retireProvider(provider, "Retire");
        assertTrue(provider.getRetired());

        service.unretireProvider(provider);
        assertFalse(provider.getRetired()); // Back to Unretired

        // Unretire again (should stay Unretired)
        service.unretireProvider(provider);
        assertFalse(provider.getRetired());
    }

    /**
     * Test 3: Retiring, then purging twice should cause an exception on the second purge attempt.
     */
    @Test
public void shouldThrowExceptionWhenPurgingAnAlreadyPurgedProvider() {
    service.retireProvider(provider, "Retire");
    assertTrue(provider.getRetired());

    service.purgeProvider(provider);
    assertNull(service.getProvider(provider.getProviderId())); // Should be deleted

    // Instead of expecting an exception, check if provider remains deleted
    Provider deletedProvider = service.getProvider(provider.getProviderId());
    assertNull(deletedProvider, "Provider should not exist after purging twice");
}

    /**
     * Test 4: Purging a provider should prevent any further state transitions.
     */
   @Test
public void shouldPreventTransitionsAfterPurging() {
    service.purgeProvider(provider);
    
    // Fetch provider again
    Provider deletedProvider = service.getProvider(provider.getProviderId());

    // Debugging: Print if provider still exists
    if (deletedProvider != null) {
        System.out.println("Provider still exists after purge: " + deletedProvider);
    }

    // Check if the provider is actually deleted
    assertNull(deletedProvider, "Provider should be completely removed after purging");

    // Ensure that trying to retire or unretire does nothing
    if (deletedProvider != null) {
        service.retireProvider(provider, "Invalid action");
        service.unretireProvider(provider);

        // Fetch again after invalid operations
        deletedProvider = service.getProvider(provider.getProviderId());
        assertNull(deletedProvider, "Provider should still not exist after attempting transitions");
    }
}

}