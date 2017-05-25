package com.truward.replicante.api;

/**
 * An interface to replication manager that distributes passed entities over cluster of nodes.
 */
public interface Replicator {

  void replicate(ReplicatedEntity entity);
}
