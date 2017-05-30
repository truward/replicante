package com.truward.replicante.srs;

import com.google.protobuf.Int32Value;
import com.google.protobuf.StringValue;
import com.truward.replicante.api.ReplicatedEntity;
import com.truward.replicante.api.ReplicatedEntityConsumer;
import com.truward.replicante.api.ReplicatedEntitySupport;
import com.truward.replicante.api.Replicator;
import com.truward.replicante.srs.support.ImmediateReplicationManager;
import org.junit.Test;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class ReplicationManagerTest {

  @Test
  public void shouldOpenAndCloseManager() throws Exception {
    try (final ImmediateReplicationManager manager = new ImmediateReplicationManager(
        ReplicationManagerSettings.newBuilder()
            .setLocation(ReplicationNodeLocation.from("127.0.0.1:9101"))
            .setReplicationDelay(500L)
            .setReplicationDelayTimeUnits(TimeUnit.MILLISECONDS)
            .setThreadPoolSize(4)
            .build())) {

      assertNotNull(manager.getReplicator());
    }
  }

  @Test
  public void shouldReplicateChanges() {
    // Create two nodes
    final ReplicationNodeLocation location1 = ReplicationNodeLocation.from("1");
    final ReplicationNodeLocation location2 = ReplicationNodeLocation.from("2");

    final Map<ReplicationNodeLocation, TestReplicationManager> testManagerMap = new ConcurrentHashMap<>();
    final StringToIntApplicationNode app1 = new StringToIntApplicationNode(testManagerMap, location1);
    final StringToIntApplicationNode app2 = new StringToIntApplicationNode(testManagerMap, location2);

    testManagerMap.put(location1, app1.manager);
    testManagerMap.put(location2, app2.manager);

    // Make nodes aware about each other
    app1.manager.addClusterNode(location2);
    app2.manager.addClusterNode(location1);

    // Operate with two distinct DAOs simultaneously
    final StringToIntDao dao1 = app1.getDao();
    final StringToIntDao dao2 = app2.getDao();

    // Writes to one DAO should be reflected in the other and vice versa
    assertNull(dao1.put("one", 1));
    assertNull(dao2.put("two", 2));

    assertEquals(Integer.valueOf(1), dao2.get("one"));
    assertEquals(Integer.valueOf(2), dao1.get("two"));
  }

  /**
   * An entity, that simulates distinct instance of an application, that uses {@link StringToIntDao}.
   */
  private static final class StringToIntApplicationNode {
    private final TestReplicationManager manager;
    private final ReplicationAwareStringToIntDao stringToIntDao;

    StringToIntApplicationNode(Map<ReplicationNodeLocation, TestReplicationManager> managerMap, ReplicationNodeLocation location) {
      this.manager = new TestReplicationManager(ReplicationManagerSettings.newBuilder().setLocation(location).build(),
          managerMap);
      this.stringToIntDao = new ReplicationAwareStringToIntDao(this.manager.getReplicator());
    }

    StringToIntDao getDao() {
      return stringToIntDao;
    }
  }

  /**
   * String-to-Int DAO.
   */
  @ParametersAreNonnullByDefault private interface StringToIntDao {
    Integer get(String key);
    Integer put(String key, int value);
  }

  /**
   * Replication-aware implementation of String-to-Int DAO.
   */
  @ParametersAreNonnullByDefault
  private static final class ReplicationAwareStringToIntDao implements ReplicatedEntityConsumer, StringToIntDao {
    private final Map<String, Integer> datastore = new ConcurrentHashMap<>();
    private final Replicator replicator;

    ReplicationAwareStringToIntDao(Replicator replicator) {
      this.replicator = replicator;
      this.replicator.setConsumer(this);
    }

    // sample read operation
    @Override
    public Integer get(String key) {
      return datastore.get(key);
    }

    @Override
    public Integer put(String key, int value) {
      final Integer result = datastore.put(key, value);

      // before returning result pack given key-value pair into replicated entity and send it via replicator interface
      replicator.replicate(ReplicatedEntitySupport.from((os) -> {
        StringValue.newBuilder().setValue(key).build().writeDelimitedTo(os);
        Int32Value.newBuilder().setValue(value).build().writeDelimitedTo(os);
      }));

      return result;
    }

    @Override
    public void applyReplicatedEntity(ReplicatedEntity entity) {
      entity.read((is) -> {
        // extract key+value from received entity and put it into the map
        // NOTE: the format should be exactly as in the command above
        final StringValue key = StringValue.parseDelimitedFrom(is);
        final Int32Value value = Int32Value.parseDelimitedFrom(is);

        // put key-value pair into the map
        datastore.put(key.getValue(), value.getValue());
      });
    }
  }

  @ParametersAreNonnullByDefault
  private static final class TestReplicationManager extends ImmediateReplicationManager {
    private final Map<ReplicationNodeLocation, TestReplicationManager> testNodeMap;

    TestReplicationManager(
        ReplicationManagerSettings settings,
        Map<ReplicationNodeLocation, TestReplicationManager> testNodeMap) {
      super(settings);
      this.testNodeMap = testNodeMap;
    }

    @Override
    protected ReplicationNode createNodeFromLocation(ReplicationNodeLocation location) {
      final TestReplicationManager other = testNodeMap.get(location);
      if (other == null) {
        throw new AssertionError("Unable to get other node");
      }

      return other;
    }
  }
}
