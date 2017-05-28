package com.truward.replicante.srs.support;

import com.google.protobuf.ByteString;
import com.truward.replicante.api.ReplicatedEntity;
import com.truward.replicante.api.ReplicatedEntityConsumer;
import com.truward.replicante.api.Replicator;
import com.truward.replicante.srs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * TBD
 */
@ParametersAreNonnullByDefault
public class ReplicationManager implements AutoCloseable {
  private final Logger log = LoggerFactory.getLogger(getClass());

  protected final ReplicatedEntityConsumer consumer;
  protected final ReplicationManagerSettings settings;
  private final List<ReplicationNode> replicationNodes = new CopyOnWriteArrayList<>();
  private ExecutorService executorService;
  private final Replicator replicator = new BoundReplicator();
  private SenderWorker senderWorker;
  private final ReplicationNode node = new Node();

  public ReplicationManager(ReplicatedEntityConsumer consumer,
                            ReplicationManagerSettings settings) {
    this.consumer = Objects.requireNonNull(consumer, "consumer");
    this.settings = Objects.requireNonNull(settings, "settings");
    this.executorService = new ScheduledThreadPoolExecutor(settings.getThreadPoolSize());

    senderWorker = new SenderWorker();
    this.executorService.submit(senderWorker);
  }

  public Replicator getReplicator() {
    return replicator;
  }

  public ReplicationNode getReplicationNode() {
    return node;
  }

  //
  // Protected
  //

  // visible for testing
  protected ReplicationNode createNodeFromLocation(ReplicationNodeLocation location) {
    // TODO: implement
    throw new UnsupportedOperationException();
  }

  //
  // Private
  //

  private void processChangeset(ReplicatedChangeset changeset) {
    for (final ReplicatedEntity entity : changeset.getEntities()) {
      consumer.applyReplicatedEntity(entity);
    }
  }

  private void enqueueEntity(ReplicatedEntity entity) {
    final ReplicatedChangeset changeset = new ReplicatedChangeset(Collections.singletonList(entity));

    // TODO: async processing, single-thread-based uniform message processing
    for (final ReplicationNode node : replicationNodes) {
      if (node.getCurrentLocation().equals(settings.getLocation())) {
        continue;
      }

      node.processChangeset(changeset);
    }
  }

  @Override
  public void close() throws Exception {
    if (senderWorker != null) {
      senderWorker.close();
      senderWorker = null;
    }

    if (executorService != null) {
      executorService.shutdown();
      executorService = null;
    }

    log.debug("ReplicationManager closed");
  }

  private final class SenderWorker implements Runnable {
    private AtomicBoolean isClosed = new AtomicBoolean();
    private final Object lock = new Object();

    void close() {
      isClosed.set(true);
      synchronized (lock) {
        lock.notify();
      }
    }

    @Override
    public void run() {
      while (!isClosed.get()) {
        synchronized (lock) {
          try {
            lock.wait(ReplicationManager.this.settings.getReplicationDelayTimeUnits().toMillis(
                ReplicationManager.this.settings.getReplicationDelay()));
          } catch (InterruptedException e) {
            Thread.interrupted();
          }
        }
      }
    }
  }

  private final class BoundReplicator implements Replicator {

    @Override
    public void replicate(ReplicatedEntity entity) {
      ReplicationManager.this.enqueueEntity(entity);
    }
  }

  private final class Node implements ReplicationNode {

    @Override
    public ReplicationNodeLocation getCurrentLocation() {
      return ReplicationManager.this.settings.getLocation();
    }

    @Override
    public List<ReplicationNodeLocation> getClusterLocations() {
      // TODO: cache locations to avoid potential remote call
      final List<ReplicationNodeLocation> locations = new ArrayList<>();
      for (final ReplicationNode n : ReplicationManager.this.replicationNodes) {
        locations.add(n.getCurrentLocation());
      }
      return locations;
    }

    @Override
    public void addClusterNode(ReplicationNodeLocation location) {
      // TODO: locations check, cache locations
      ReplicationManager.this.replicationNodes.add(ReplicationManager.this.createNodeFromLocation(location));
    }

    @Override
    public void removeClusterNode(ReplicationNodeLocation location) {
      // TODO: implement
      throw new UnsupportedOperationException();
    }

    @Override
    public List<ReplicationNodeStatus> validateCluster() {
      // TODO: implement
      throw new UnsupportedOperationException();
    }

    @Override
    public ReplicationNodeStatus getStatus() {
      // TODO: implement
      throw new UnsupportedOperationException();
    }

    @Override
    public void processChangeset(ReplicatedChangeset changeset) {
      ReplicationManager.this.processChangeset(changeset);
    }
  }
}
