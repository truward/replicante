package com.truward.replicante.api;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Callback that writes replicated entity into the local datastore.
 * To be provided by an application that uses replication library.
 */
@ParametersAreNonnullByDefault
public interface ReplicatedEntityConsumer {

  void applyReplicatedEntity(ReplicatedEntity entity);
}
