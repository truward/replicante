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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ReplicationNodeLocation location = (ReplicationNodeLocation) o;

    return address.equals(location.address);
  }

  @Override
  public int hashCode() {
    return address.hashCode();
  }

  @Override
  public String toString() {
    return "ReplicationNodeLocation{" +
        "address='" + address + '\'' +
        ", name='" + name + '\'' +
        '}';
  }
}
