package com.truward.replicante.api;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Represents serialized entity to be replicated.
 * Replicated entity shall be used during the lifetime of callback and then discarded.
 * There is no guarantee that entity will retain its state if it is saved for longer.
 */
@ParametersAreNonnullByDefault
public interface ReplicatedEntity {

  default void writeTo(OutputStream outputStream) throws IOException {
    outputStream.write(getBytes(), getBytesOffset(), getBytesLength());
  }

  byte[] getBytes();
  int getBytesOffset();
  int getBytesLength();
}
