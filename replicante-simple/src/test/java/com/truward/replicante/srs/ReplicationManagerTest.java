package com.truward.replicante.srs;

import com.truward.replicante.srs.support.ReplicationManager;
import org.junit.Test;

import java.io.IOException;
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
}
