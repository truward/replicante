package com.truward.replicante.srs.support;

import com.truward.replicante.api.ReplicatedEntity;
import com.truward.replicante.api.ReplicatedEntityConsumer;
import com.truward.replicante.api.ReplicationManager;
import com.truward.replicante.api.Replicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

/**
 * @author Alexander Shabanov
 */
@ParametersAreNonnullByDefault
public abstract class AbstractReplicationManager implements ReplicationManager, Replicator {
  protected final Logger log = LoggerFactory.getLogger(getClass());

  private volatile boolean closed;
  private volatile boolean stopped;
  private volatile ReplicatedEntityConsumer consumer = (ignored) -> {
    throw new IllegalStateException("consumer has not been assigned yet");
  };

  @Override
  public final Replicator getReplicator() {
    checkIsRunning();
    return this;
  }

  @Override
  public final void flush() {
    checkIsRunning();
    triggerImmediateProcessing();
  }

  @Override
  public final void close() throws Exception {
    stopped = true;

    if (closed) {
      return;
    }

    triggerImmediateProcessing();
    doClose();
    closed = true;

    log.debug("ReplicationManager {} stopped", this);
  }

  @Override
  public final void replicate(ReplicatedEntity entity) {
    checkIsRunning();
    doReplicate(entity);
  }

  @Override
  public final void setConsumer(ReplicatedEntityConsumer consumer) {
    checkIsRunning();
    this.consumer = Objects.requireNonNull(consumer, "consumer");
  }

  //
  // Protected
  //

  protected abstract void doClose();

  protected abstract void doReplicate(ReplicatedEntity entity);

  protected abstract void triggerImmediateProcessing();

  protected final void sendToConsumer(ReplicatedEntity entity) {
    this.consumer.applyReplicatedEntity(entity);
  }

  //
  // Private
  //

  private void checkIsRunning() {
    if (stopped) {
      throw new IllegalStateException("ReplicationManager has been stopped");
    }
  }
}
