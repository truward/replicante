package com.truward.replicante.api;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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

  interface ReaderCallback {
    void read(InputStream inputStream) throws IOException;
  }

  default void read(ReaderCallback callback) {
    try (final ByteArrayInputStream inputStream = new ByteArrayInputStream(
        getBytes(),
        getBytesOffset(),
        getBytesLength())) {
      callback.read(inputStream);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  byte[] getBytes();
  int getBytesOffset();
  int getBytesLength();
}
