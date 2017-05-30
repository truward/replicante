package com.truward.replicante.srs.support;

import com.truward.replicante.api.ReplicatedEntity;
import com.truward.replicante.srs.*;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Replication manager that processes changes as soon as they become available.
 */
@ParametersAreNonnullByDefault
public class ImmediateReplicationManager extends AbstractReplicationManager implements ReplicationNode {

  private final ReplicationManagerSettings settings;
  private final List<ReplicationNode> replicationNodes = new CopyOnWriteArrayList<>();

  public ImmediateReplicationManager(ReplicationManagerSettings settings) {
    this.settings = Objects.requireNonNull(settings, "settings");
  }

  //
  // Protected
  //

  // visible for testing
  protected ReplicationNode createNodeFromLocation(ReplicationNodeLocation location) {
    // TODO: implement
    throw new UnsupportedOperationException();
  }

  @Override
  public void processChangeset(ReplicatedChangeset changeset) {
    for (final ReplicatedEntity entity : changeset.getEntities()) {
      sendToConsumer(entity);
    }
  }
  @Override
  public ReplicationNodeLocation getCurrentLocation() {
    return this.settings.getLocation();
  }

  @Override
  public List<ReplicationNodeLocation> getClusterLocations() {
    // TODO: cache locations to avoid potential remote call
    final List<ReplicationNodeLocation> locations = new ArrayList<>();
    for (final ReplicationNode n : this.replicationNodes) {
      locations.add(n.getCurrentLocation());
    }
    return locations;
  }

  @Override
  public void addClusterNode(ReplicationNodeLocation location) {
    // TODO: locations check, cache locations
    this.replicationNodes.add(this.createNodeFromLocation(location));
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

  //
  // Protected
  //

  @Override
  protected void doClose() {
    this.replicationNodes.clear();
  }

  @Override
  protected void doReplicate(ReplicatedEntity entity) {
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
  protected void triggerImmediateProcessing() {
    // do nothing, changes are replicated instantaneously
  }
}
