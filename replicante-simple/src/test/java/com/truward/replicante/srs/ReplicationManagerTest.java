package com.truward.replicante.srs;

import com.truward.replicante.api.ReplicatedEntityConsumer;
import com.truward.replicante.srs.support.ReplicationManager;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;

public class ReplicationManagerTest {

  @Test
  public void shouldOpenAndCloseManager() throws Exception {
    try (final ReplicationManager manager = new ReplicationManager((ignored) -> {},
        ReplicationManagerSettings.newBuilder()
            .setLocation(ReplicationNodeLocation.from("127.0.0.1:9101"))
            .setReplicationDelay(500L)
            .setReplicationDelayTimeUnits(TimeUnit.MILLISECONDS)
            .setThreadPoolSize(4)
            .build())) {

      assertNotNull(manager.getReplicator());
    }
  }

  private static final class TestReplicationManager extends ReplicationManager {
    private final Map<ReplicationNodeLocation, ReplicatedEntityConsumer> testNodeMap;

    public TestReplicationManager(
        ReplicatedEntityConsumer consumer,
        ReplicationManagerSettings settings,
        Map<ReplicationNodeLocation, ReplicatedEntityConsumer> testNodeMap) {
      super(consumer, settings);
      this.testNodeMap = testNodeMap;
    }

    @Override
    protected ReplicationNode createNodeFromLocation(ReplicationNodeLocation location) {
      throw new UnsupportedOperationException();
      //final TestReplicationManager testReplicationManager = new TestReplicationManager(this.consumer, ReplicationManagerSettings.);
      //return super.createNodeFromLocation(location);
    }
  }
}
