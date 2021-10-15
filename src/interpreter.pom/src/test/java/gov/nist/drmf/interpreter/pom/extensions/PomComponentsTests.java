package gov.nist.drmf.interpreter.pom.extensions;

import gov.nist.drmf.interpreter.pom.MLPWrapper;
import gov.nist.drmf.interpreter.pom.SemanticMLPWrapper;
import gov.nist.drmf.interpreter.pom.common.meta.AssumeMLPAvailability;
import mlp.ParseException;
import mlp.PomTaggedExpression;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * We carefully build {@link PrintablePomTaggedExpression} so that each component
 * in the original {@link mlp.PomTaggedExpression} is actually an instance of
 * {@link PrintablePomTaggedExpression}. If that's true, we can try a hacky solution
 * to cast the components the a list of the subclass. But this is only secure if
 * {@link PrintablePomTaggedExpression} was properly implemented. So this tests try
 * to check if the elements were properly implemented.
 *
 * @author Andre Greiner-Petter
 */
@AssumeMLPAvailability
public class PomComponentsTests {

    private static MLPWrapper mlp;

    @BeforeAll
    public static void setup() {
        mlp = SemanticMLPWrapper.getStandardInstance();
    }

    @Test
    public void castComponentsTest() throws ParseException {
        PrintablePomTaggedExpression t = mlp.parse("a + \\frac{1}{2}");
        List<PomTaggedExpression> components = t.getComponents();

        // the test fails if the following cast throws an exception
        // using a double cast hack!
        List<PrintablePomTaggedExpression> printableComponents =
                (List<PrintablePomTaggedExpression>)(List<?>) components;

        // check if the reference is the same
        assertEquals(components, printableComponents);

        // check that no changes are allowed because getting the components should be unmodifiable
        assertThrows(UnsupportedOperationException.class, () -> components.remove(0));
        assertThrows(UnsupportedOperationException.class, () -> printableComponents.remove(0));
    }

    @Test
    public void unmodifiableComponentTest() throws ParseException {
        PrintablePomTaggedExpression t = mlp.parse("a + \\frac{1}{2}");
        List<PomTaggedExpression> components = t.getComponents();
        List<PrintablePomTaggedExpression> printComponents = t.getPrintableComponents();

        // both reference to the exact same components list
        assertEquals(components, printComponents);

        // check that no changes are allowed because getting the components should be unmodifiable
        assertThrows(UnsupportedOperationException.class, () -> components.remove(0));
        assertThrows(UnsupportedOperationException.class, () -> printComponents.remove(0));
    }
}
