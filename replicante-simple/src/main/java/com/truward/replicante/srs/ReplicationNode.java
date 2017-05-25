package com.truward.replicante.srs;

import java.util.List;

/**
 * An API of the replication node.
 */
public interface ReplicationNode {

  ReplicationNodeLocation getCurrentLocation();

  //
  // Cluster Management
  //

  List<ReplicationNodeLocation> getClusterLocations();

  void addClusterNode(ReplicationNodeLocation location);

  void removeClusterNode(ReplicationNodeLocation location);

  List<ReplicationNodeStatus> validateCluster();

  ReplicationNodeStatus getStatus();

  //
  // Replication-specific
  //

  void processChangeset(ReplicatedChangeset changeset);
}
