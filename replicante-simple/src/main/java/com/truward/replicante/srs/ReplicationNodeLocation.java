package com.truward.replicante.srs;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Represents location of this replication node.
 * The only required parameter is node address (implementation specific).
 * Name is optional and can be empty (but never null).
 */
@ParametersAreNonnullByDefault
public final class ReplicationNodeLocation {
  private final String address;
  private final String name;

  private ReplicationNodeLocation(String address, String name) {
    this.address = address;
    this.name = name;
  }

  public String getAddress() {
    return address;
  }

  public String getName() {
    return name;
  }

  public static ReplicationNodeLocation from(String address) {
    return new ReplicationNodeLocation(address, "");
  }
}
