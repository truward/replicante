package com.truward.replicante.api;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * An API to the replication manager.
 */
@ParametersAreNonnullByDefault
public interface ReplicationManager extends AutoCloseable {

  /**
   * @return Replicator instance
   */
  Replicator getReplicator();

  /**
   * Instructs replication manager to start immediate processing of the pending changes.
   *
   * This method is rarely used except for tests as application may need this functionality when it closes
   * an instance of this class and this is already provided as part of {@link #close()} method.
   */
  void flush();

  /**
   * Pushes all the enqueued data to the other nodes in replication cluster and closes this instance.
   *
   * @throws Exception On error
   */
  @Override
  void close() throws Exception;
}
