package com.truward.replicante.api;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Callback to be provided by an application that uses replication library.
 */
@ParametersAreNonnullByDefault
public interface ReplicatedEntityConsumer {

  void applyReplicatedEntity(ReplicatedEntity entity);
}
