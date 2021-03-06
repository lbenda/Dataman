package cz.lbenda.test;

import javafx.application.Application;
import javafx.stage.Stage;

public class JavaFXInitializer extends Application {

  private static final Object barrier = new Object();
  private static boolean initialized = false;

  @Override
  public void start(Stage primaryStage) throws Exception {
    synchronized(barrier) {
      barrier.notify();
    }
  }

  public static void initialize() throws InterruptedException {
    if (!initialized) {
      initialized = true;
      Thread t = new Thread("JavaFX Init Thread") {
        public void run() {
          Application.launch(JavaFXInitializer.class);
        }
      };
      t.setDaemon(true);
      t.start();
      synchronized (barrier) {
        barrier.wait();
      }
    }
  }
}
