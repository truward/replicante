package com.truward.replicante.srs;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@ParametersAreNonnullByDefault
public final class ReplicationManagerSettings {
  private final long replicationDelay;
  private final TimeUnit replicationDelayTimeUnits;
  private final ReplicationNodeLocation location;
  private final int threadPoolSize;

  private ReplicationManagerSettings(
      long replicationDelay,
      TimeUnit replicationDelayTimeUnits,
      ReplicationNodeLocation location,
      int threadPoolSize) {
    if (replicationDelay <= 0) {
      throw new IllegalArgumentException("replicationDelay");
    }

    if (threadPoolSize < 1) {
      throw new IllegalArgumentException("threadPoolSize");
    }

    this.replicationDelay = replicationDelay;
    this.replicationDelayTimeUnits = Objects.requireNonNull(replicationDelayTimeUnits, "replicationDelayTimeUnits");
    this.location = Objects.requireNonNull(location, "location");
    this.threadPoolSize = threadPoolSize;
  }

  public long getReplicationDelay() {
    return replicationDelay;
  }

  public TimeUnit getReplicationDelayTimeUnits() {
    return replicationDelayTimeUnits;
  }

  public ReplicationNodeLocation getLocation() {
    return location;
  }

  public int getThreadPoolSize() {
    return threadPoolSize;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {
    private long replicationDelay = 1500L;
    private TimeUnit replicationDelayTimeUnits = TimeUnit.MILLISECONDS;
    private ReplicationNodeLocation location;
    private int threadPoolSize = 10;

    private Builder() {}

    public ReplicationManagerSettings build() {
      return new ReplicationManagerSettings(replicationDelay, replicationDelayTimeUnits, location, threadPoolSize);
    }

    public Builder setReplicationDelay(long replicationDelay) {
      this.replicationDelay = replicationDelay;
      return this;
    }

    public Builder setReplicationDelayTimeUnits(TimeUnit replicationDelayTimeUnits) {
      this.replicationDelayTimeUnits = replicationDelayTimeUnits;
      return this;
    }

    public Builder setLocation(ReplicationNodeLocation location) {
      this.location = location;
      return this;
    }

    public Builder setThreadPoolSize(int threadPoolSize) {
      this.threadPoolSize = threadPoolSize;
      return this;
    }
  }
}
