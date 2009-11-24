package de.fu_berlin.inf.dpp.concurrent.jupiter.test.puzzles;

/**
 * This test class represent local execution of document changes and
 * appropriate jupiter operations.
 */
import de.fu_berlin.inf.dpp.User;
import de.fu_berlin.inf.dpp.activities.business.JupiterActivity;
import de.fu_berlin.inf.dpp.concurrent.jupiter.Algorithm;
import de.fu_berlin.inf.dpp.concurrent.jupiter.Operation;
import de.fu_berlin.inf.dpp.concurrent.jupiter.internal.Jupiter;
import de.fu_berlin.inf.dpp.concurrent.jupiter.internal.text.InsertOperation;
import de.fu_berlin.inf.dpp.concurrent.jupiter.test.util.Document;
import de.fu_berlin.inf.dpp.concurrent.jupiter.test.util.JupiterTestCase;

public class SimpleJupiterDocumentTest extends JupiterTestCase {

    /**
     * simple test to generate local operations and compute the
     * JupiterActivities for other sides.
     */
    public void testExecuteLocalOperations() {
        Algorithm algo = new Jupiter(true);

        Document doc = new Document("abc");
        assertEquals("abc", doc.getDocument());

        /* insert one char. */
        Operation op = new InsertOperation(2, "d");
        doc.execOperation(op);
        assertEquals("abdc", doc.getDocument());

        User user = JupiterTestCase.createUserMock("user");

        JupiterActivity jupiterActivity = algo.generateJupiterActivity(op,
            user, null);
        assertTrue(jupiterActivity.getOperation().equals(op));

        /* insert one short string. */
        op = new InsertOperation(2, "insert");
        doc.execOperation(op);
        assertEquals("abinsertdc", doc.getDocument());

        jupiterActivity = algo.generateJupiterActivity(op, user, null);
        System.out.println(jupiterActivity.getOperation().toString());

    }

}
