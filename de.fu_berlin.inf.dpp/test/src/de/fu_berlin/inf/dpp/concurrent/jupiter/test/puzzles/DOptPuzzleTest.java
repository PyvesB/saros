package de.fu_berlin.inf.dpp.concurrent.jupiter.test.puzzles;

import de.fu_berlin.inf.dpp.concurrent.jupiter.internal.text.DeleteOperation;
import de.fu_berlin.inf.dpp.concurrent.jupiter.internal.text.InsertOperation;
import de.fu_berlin.inf.dpp.concurrent.jupiter.test.util.ClientSynchronizedDocument;
import de.fu_berlin.inf.dpp.concurrent.jupiter.test.util.JupiterTestCase;
import de.fu_berlin.inf.dpp.concurrent.jupiter.test.util.ServerSynchronizedDocument;

/**
 * this test case simulate the unsolved dOPT Puzzle scenario which described in
 * Fig. 2 in "Operational Transformation in Real-Time Group Editors: Issues,
 * Algorithm, Achievements", Sun et.al.
 * 
 * @author orieger
 * 
 */
public class DOptPuzzleTest extends JupiterTestCase {

    /**
     * dOPT puzzle scenario with three sides and three concurrent insert
     * operations of a character at the same position.
     * 
     * @throws Exception
     */
    public void testThreeConcurrentInsertOperations() throws Exception {
        /* init simulated client and server components. */
        ClientSynchronizedDocument client_1 = new ClientSynchronizedDocument(
            host.getJID(), "abcd", network, alice);
        ClientSynchronizedDocument client_2 = new ClientSynchronizedDocument(
            host.getJID(), "abcd", network, bob);
        ClientSynchronizedDocument client_3 = new ClientSynchronizedDocument(
            host.getJID(), "abcd", network, carl);
        ServerSynchronizedDocument server = new ServerSynchronizedDocument(
            network, host);

        /* connect all with simulated network. */
        network.addClient(client_1);
        network.addClient(client_2);
        network.addClient(client_3);
        network.addClient(server);

        /* create proxyqueues. */
        server.addProxyClient(alice);
        server.addProxyClient(bob);
        server.addProxyClient(carl);

        Thread.sleep(100);

        /* O3 || O2 */
        client_3.sendOperation(new InsertOperation(0, "z"), 100);
        client_2.sendOperation(new InsertOperation(0, "x"), 700);

        Thread.sleep(300);
        /* O1 -> O3 */
        client_1.sendOperation(new InsertOperation(0, "y"), 100);

        Thread.sleep(700);

        assertEquals(client_1.getDocument(), client_2.getDocument());
        assertEquals(client_2.getDocument(), client_3.getDocument());
        System.out.println(client_1.getDocument());
    }

    /**
     * dOPT puzzle scenario with three sides and three concurrent insert
     * operations of a character at the same position.
     * 
     * @throws Exception
     */
    public void testThreeConcurrentInsertStringOperations() throws Exception {

        ClientSynchronizedDocument[] clients = setUp(3, "abcd");

        /* O2 || O1 */
        clients[2].sendOperation(new InsertOperation(0, "zzz"), 100);
        clients[1].sendOperation(new InsertOperation(0, "x"), 700);

        Thread.sleep(300);
        /* O0 -> O2 */
        clients[0].sendOperation(new InsertOperation(0, "yy"), 100);

        Thread.sleep(700);

        assertEqualDocs("yyzzzxabcd", clients);
    }

    /**
     * dOPT puzzle scenario with three sides and three concurrent delete
     * operations.
     * 
     * @throws Exception
     */
    public void testThreeConcurrentDeleteOperations() throws Exception {

        ClientSynchronizedDocument[] clients = setUp(3, "abcdefg");

        clients[0].sendOperation(new DeleteOperation(0, "a"), 100);
        Thread.sleep(300);
        clients[2].sendOperation(new DeleteOperation(1, "cde"), 500);
        clients[1].sendOperation(new DeleteOperation(3, "e"), 300);

        Thread.sleep(1000);

        assertEqualDocs("bfg", clients);
    }

    /**
     * dOPT puzzle scenario with three sides and insert / delete operations.
     * 
     * @throws Exception
     */
    public void testConcurrentInsertDeleteOperations() throws Exception {

        ClientSynchronizedDocument[] clients = setUp(3, "abc");

        clients[0].sendOperation(new InsertOperation(0, "a"), 0);
        clients[1].sendOperation(new InsertOperation(1, "b"), 100);

        Thread.sleep(200);
        clients[2].sendOperation(new DeleteOperation(1, "ab"), 700);
        clients[1].sendOperation(new InsertOperation(2, "by"), 100);
        clients[0].sendOperation(new InsertOperation(1, "x"), 400);

        Thread.sleep(1000);

        assertEqualDocs("axbybc", clients);
    }
}
