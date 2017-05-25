package com.truward.replicante.srs;

import com.google.protobuf.Message;
import com.truward.replicante.api.ReplicatedEntity;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

/**
 * Replicated entity
 */
@ParametersAreNonnullByDefault
public final class ReplicatedProtobufEntity implements ReplicatedEntity {
  private final Message message;
  private transient byte[] bytes;

  public ReplicatedProtobufEntity(Message message) {
    this.message = Objects.requireNonNull(message, "message");
  }

  @Override
  public void writeTo(OutputStream outputStream) throws IOException {
    message.writeTo(outputStream);
  }

  @Override
  public byte[] getBytes() {
    if (bytes == null) {
      bytes = message.toByteArray();
    }
    return bytes;
  }

  @Override
  public int getBytesOffset() {
    return 0;
  }

  @Override
  public int getBytesLength() {
    return getBytes().length;
  }
}
