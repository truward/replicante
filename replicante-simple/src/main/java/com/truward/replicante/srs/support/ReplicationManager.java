package com.truward.replicante.srs.support;

import com.google.protobuf.ByteString;
import com.truward.replicante.api.ReplicatedEntity;
import com.truward.replicante.api.ReplicatedEntityConsumer;
import com.truward.replicante.api.Replicator;
import com.truward.replicante.srs.ReplicatedChangeset;
import com.truward.replicante.srs.ReplicationNode;
import com.truward.replicante.srs.ReplicationNodeLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;

/**
 * TBD
 */
public class ReplicationManager {

  private final List<ReplicatedEntityConsumer> consumers = new CopyOnWriteArrayList<>();
  private final List<ReplicationNode> replicationNodes = new CopyOnWriteArrayList<>();
  private final ExecutorService executorService;
  private final ReplicationNodeLocation location;
  private final Replicator replicator = new BoundReplicator();

  public ReplicationManager(ReplicationNodeLocation location,
                            ExecutorService executorService) {
    this.location = Objects.requireNonNull(location);
    this.executorService = Objects.requireNonNull(executorService);
  }

  public void processChangeset(ReplicatedChangeset changeset) {
//    for (final ReplicatedEntity entity : changeset.entities) {
//      replicationCallback.applyReplicatedEntity(entity);
//    }
  }

  public Replicator getReplicator() {
    return replicator;
  }

  //
  // Private
  //

  private void enqueueEntity(ReplicatedEntity entity) {
    final ReplicatedChangeset changeset = new ReplicatedChangeset(Collections.singletonList(entity));

    // TODO: async processing, single-thread-based uniform message processing
    for (final ReplicationNode node : replicationNodes) {
      if (node.getCurrentLocation().equals(location)) {
        continue;
      }

      node.processChangeset(changeset);
    }
  }

  private final class BoundReplicator implements Replicator {

    @Override
    public void replicate(ReplicatedEntity entity) {
      ReplicationManager.this.enqueueEntity(entity);
    }
  }
}
