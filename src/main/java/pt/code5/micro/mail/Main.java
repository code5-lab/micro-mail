package pt.code5.micro.mail;

import io.vertx.core.Vertx;

/**
 * Created by eduardo on 17/03/2017.
 */
public class Main {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new MailVerticle());
    }
}
