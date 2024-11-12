package cleanbank.infra.pipeline;

import org.slf4j.MDC;

import java.util.concurrent.atomic.AtomicLong;

class Correlable implements Now {

  private final AtomicLong counter = new AtomicLong();

  private final Now origin;

  Correlable(Now origin) {
    this.origin = origin;
  }

  @Override
  public <C extends Command<R>, R> R execute(C command) {
    var MDC_KEY = "ccid";
    var closeable = MDC.putCloseable(MDC_KEY, next());
    try (closeable) {
      return origin.execute(command);
    }
  }

  private String next() {
    return String.valueOf(counter.incrementAndGet() % 1000);
  }
}