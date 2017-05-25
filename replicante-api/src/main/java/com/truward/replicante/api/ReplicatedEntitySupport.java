package com.truward.replicante.api;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

/**
 * Support for replicated entities.
 */
@ParametersAreNonnullByDefault
public final class ReplicatedEntitySupport {
  private ReplicatedEntitySupport() {} // hidden

  public static ReplicatedEntity from(byte[] bytes) {
    return new ByteArrayReplicatedEntity(bytes);
  }

  public static ReplicatedEntity from(byte[] bytes, int offset, int limit) {
    return new OffsetLimitByteArrayReplicatedEntity(bytes, offset, limit);
  }

  //
  // Private
  //

  private static class ByteArrayReplicatedEntity implements ReplicatedEntity {
    private final byte[] bytes;

    ByteArrayReplicatedEntity(byte[] bytes) {
      this.bytes = Objects.requireNonNull(bytes, "bytes");
    }

    @Override
    public final byte[] getBytes() {
      return bytes;
    }

    @Override
    public int getBytesOffset() {
      return 0;
    }

    @Override
    public int getBytesLength() {
      return bytes.length;
    }
  }

  private static final class OffsetLimitByteArrayReplicatedEntity extends ByteArrayReplicatedEntity {
    private final int offset;
    private final int length;

    OffsetLimitByteArrayReplicatedEntity(byte[] bytes, int offset, int length) {
      super(bytes);
      if (offset < 0 || offset > bytes.length) {
        throw new IllegalArgumentException("offset");
      }
      if (length < 0 || (offset + length) > getBytes().length) {
        throw new IllegalArgumentException("length");
      }

      this.offset = offset;
      this.length = length;
    }

    @Override
    public int getBytesOffset() {
      return offset;
    }

    @Override
    public int getBytesLength() {
      return length;
    }
  }
}
