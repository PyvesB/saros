package de.fu_berlin.inf.dpp.stf.client.testProject.testsuits.testSTF.contextMenu;

import java.rmi.RemoteException;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import de.fu_berlin.inf.dpp.stf.client.testProject.testsuits.STFTest;

public class TestContextMenuDelete extends STFTest {

    @BeforeClass
    public static void runBeforeClass() throws RemoteException {
        initTesters(TypeOfTester.ALICE);
        setUpWorkbench();
    }

    @After
    public void runAfterEveryTest() throws RemoteException {
        deleteAllProjectsByActiveTesters();
    }

    @Test
    public void testDeleteProject() throws RemoteException {
        alice.sarosBot().views().packageExplorerView().tree().newC()
            .project(PROJECT1);
        // alice.sarosBot().views().packageExplorerView().tree().newC()
        // .pkg(PROJECT1, PKG1);
        alice.sarosBot().views().packageExplorerView().selectProject(PROJECT1)
            .copy();
    }

    @Test
    public void testDeleteAllItems() throws RemoteException {
        alice.sarosBot().views().packageExplorerView().tree().newC()
            .javaProjectWithClasses(PROJECT1, PKG1, CLS1);
        alice.sarosBot().views().packageExplorerView()
            .selectPkg(PROJECT1, PKG1).newC().cls(CLS2);
        alice.sarosBot().views().packageExplorerView().tree().newC()
            .pkg(PROJECT1, PKG2);
        alice.sarosBot().views().packageExplorerView()
            .selectPkg(PROJECT1, PKG1).delete();

    }
}
