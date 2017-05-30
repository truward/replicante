package com.truward.replicante.api;

/**
 * An interface to replication manager that distributes passed entities over cluster of nodes.
 */
public interface Replicator {

  /**
   * Enqueues entities in the replication queue.
   *
   * @param entity Entity to be replicated.
   */
  void replicate(ReplicatedEntity entity);

  /**
   * Sets application-level consumer that holds logic that unwraps entities from the other nodes in the cluster
   * and stores them in the application-level datastore.
   *
   * @param consumer Non-nullable entity consumer
   */
  void setConsumer(ReplicatedEntityConsumer consumer);
}
