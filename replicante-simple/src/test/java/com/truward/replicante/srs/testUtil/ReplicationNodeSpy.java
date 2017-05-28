package com.truward.replicante.srs.testUtil;

import com.truward.replicante.srs.ReplicatedChangeset;
import com.truward.replicante.srs.ReplicationNode;
import com.truward.replicante.srs.ReplicationNodeLocation;
import com.truward.replicante.srs.ReplicationNodeStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public final class ReplicationNodeSpy implements ReplicationNode {
  private final Logger log = LoggerFactory.getLogger(getClass());
  private final ReplicationNode delegate;

  public ReplicationNodeSpy(ReplicationNode delegate) {
    this.delegate = delegate;
  }

  @Override
  public ReplicationNodeLocation getCurrentLocation() {
    log.info("getCurrentLocation");
    return delegate.getCurrentLocation();
  }

  @Override
  public List<ReplicationNodeLocation> getClusterLocations() {
    log.info("getClusterLocations");
    return delegate.getClusterLocations();
  }

  @Override
  public void addClusterNode(ReplicationNodeLocation location) {
    log.info("addClusterNode");
    delegate.addClusterNode(location);
  }

  @Override
  public void removeClusterNode(ReplicationNodeLocation location) {
    log.info("removeClusterNode");
    delegate.removeClusterNode(location);
  }

  @Override
  public List<ReplicationNodeStatus> validateCluster() {
    log.info("validateCluster");
    return delegate.validateCluster();
  }

  @Override
  public ReplicationNodeStatus getStatus() {
    log.info("getStatus");
    return delegate.getStatus();
  }

  @Override
  public void processChangeset(ReplicatedChangeset changeset) {
    log.info("processChangeset");
    delegate.processChangeset(changeset);
  }
}
