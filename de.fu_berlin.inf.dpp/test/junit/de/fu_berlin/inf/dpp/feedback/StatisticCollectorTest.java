package de.fu_berlin.inf.dpp.feedback;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.replay;

import de.fu_berlin.inf.dpp.context.IContextKeyBindings;
import de.fu_berlin.inf.dpp.editor.EditorManager;
import de.fu_berlin.inf.dpp.editor.FollowModeManager;
import de.fu_berlin.inf.dpp.net.IConnectionManager;
import de.fu_berlin.inf.dpp.net.internal.DataTransferManager;
import de.fu_berlin.inf.dpp.net.xmpp.JID;
import de.fu_berlin.inf.dpp.session.ISarosSession;
import de.fu_berlin.inf.dpp.session.ISessionListener;
import de.fu_berlin.inf.dpp.session.User;
import de.fu_berlin.inf.dpp.test.mocks.EclipseMocker;
import de.fu_berlin.inf.dpp.test.mocks.EditorManagerMock;
import java.util.LinkedList;
import java.util.List;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.osgi.service.prefs.Preferences;
import saros.org.picocontainer.BindKey;
import saros.org.picocontainer.MutablePicoContainer;
import saros.org.picocontainer.PicoBuilder;
import saros.org.picocontainer.injectors.AnnotatedFieldInjection;
import saros.org.picocontainer.injectors.CompositeInjection;
import saros.org.picocontainer.injectors.ConstructorInjection;

public class StatisticCollectorTest {

  private MutablePicoContainer container;
  private List<Object> sessionListeners;
  private List<Object> editorListeners;

  private static ISarosSession createSessionMock(final List<Object> sessionListeners) {
    ISarosSession session = EasyMock.createMock(ISarosSession.class);
    final User bob = new User(new JID("bob"), false, false, 1, -1);
    final User alice = new User(new JID("alice"), false, false, 2, -1);
    final List<User> participants = new LinkedList<User>();
    participants.add(bob);
    participants.add(alice);
    session.addListener(EasyMock.isA(ISessionListener.class));
    EasyMock.expectLastCall()
        .andAnswer(
            new IAnswer<Object>() {

              @Override
              public Object answer() throws Throwable {
                sessionListeners.add(EasyMock.getCurrentArguments()[0]);
                return null;
              }
            })
        .anyTimes();
    session.removeListener(EasyMock.isA(ISessionListener.class));
    EasyMock.expectLastCall()
        .andAnswer(
            new IAnswer<Object>() {
              @Override
              public Object answer() throws Throwable {
                sessionListeners.remove(EasyMock.getCurrentArguments()[0]);
                return null;
              }
            })
        .anyTimes();

    EasyMock.expect(session.getLocalUser()).andStubReturn(bob);
    EasyMock.expect(session.getUsers()).andStubReturn(participants);
    EasyMock.expect(session.getHost()).andStubReturn(bob);
    EasyMock.expect(session.getID()).andStubReturn("0815");

    EasyMock.replay(session);
    return session;
  }

  private void addMockedComponent(Class<?> key, Class<?> impl) {
    Object mock = createNiceMock(impl);
    replay(mock);
    container.addComponent(key, mock);
  }

  @Before
  public void setup() {
    container =
        new PicoBuilder(
                new CompositeInjection(new ConstructorInjection(), new AnnotatedFieldInjection()))
            .withCaching()
            .withLifecycle()
            .build();

    // session
    sessionListeners = new LinkedList<Object>();
    ISarosSession session = createSessionMock(sessionListeners);
    container.addComponent(ISarosSession.class, session);

    // editor
    editorListeners = new LinkedList<Object>();
    container.addComponent(EditorManager.class, EditorManagerMock.createMock(editorListeners));

    // follow mode manager
    addMockedComponent(FollowModeManager.class, FollowModeManager.class);

    IPreferenceStore store = EclipseMocker.initPreferenceStore(container);
    Preferences preferences = EclipseMocker.initPreferences();

    EclipseMocker.mockSarosWithPreferences(container, store, preferences);

    FeedbackPreferences.setPreferences(preferences);

    addMockedComponent(IConnectionManager.class, DataTransferManager.class);

    // Components we want to create
    container.addComponent(StatisticManager.class);
    container.addComponent(FeedbackManager.class);

    container.addComponent(DataTransferCollector.class);
    container.addComponent(PermissionChangeCollector.class);
    container.addComponent(ParticipantCollector.class);
    container.addComponent(SessionDataCollector.class);
    container.addComponent(TextEditCollector.class);
    container.addComponent(JumpFeatureUsageCollector.class);
    container.addComponent(FollowModeCollector.class);
    container.addComponent(SelectionCollector.class);

    container.addComponent(
        BindKey.bindKey(String.class, IContextKeyBindings.SarosVersion.class), "0815");

    container.addComponent(
        BindKey.bindKey(String.class, IContextKeyBindings.PlatformVersion.class), "4711");

    container.getComponents();
  }

  @Test
  public void testCollectorRegistrationAndDestruction() {
    // Verify that the collectors are available
    StatisticManager manager = container.getComponent(StatisticManager.class);
    Assert.assertEquals(8, manager.getAvailableCollectorCount());
    Assert.assertEquals(0, manager.getActiveCollectorCount());

    // Verify that they are active now
    container.start();
    Assert.assertEquals(8, manager.getAvailableCollectorCount());
    Assert.assertEquals(8, manager.getActiveCollectorCount());

    // Verify that they are not active anymore
    container.stop();
    Assert.assertEquals(8, manager.getAvailableCollectorCount());
    Assert.assertEquals(0, manager.getActiveCollectorCount());
    Assert.assertTrue(sessionListeners.isEmpty());
    Assert.assertTrue(editorListeners.isEmpty());
  }
}
