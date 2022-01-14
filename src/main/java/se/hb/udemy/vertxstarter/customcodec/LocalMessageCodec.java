package se.hb.udemy.vertxstarter.customcodec;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

public class LocalMessageCodec<T> implements MessageCodec<T, T> {

  private final Class<T> type;

  public LocalMessageCodec(Class<T> type) {
    this.type = type;
  }

  @Override
  public void encodeToWire(Buffer buffer, T t) {

  }

  @Override
  public T decodeFromWire(int pos, Buffer buffer) {
    return null;
  }

  @Override
  public T transform(T t) {
    return t;
  }

  @Override
  public String name() {
    return this.type.getName();
  }

  @Override
  public byte systemCodecID() {
    return -1;
  }
}
