package pt.code5.micro.mail;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mail.MailClient;
import io.vertx.ext.mail.MailConfig;
import io.vertx.ext.mail.MailMessage;
import pt.code5.micro.utils.Config;
import pt.code5.micro.utils.vertx.VertxManager;

/**
 * Created by eduardo on 21/03/2017.
 */
public class MailVerticle extends AbstractVerticle {

    private MailClient mailClient;
    private EventBus eb;

    @Override
    public void start() throws Exception {
        super.start();
        VertxManager.getInstance().boot(vertx);
        this.eb = vertx.eventBus();

        Config.getInstance().boot(this::boot);
    }

    private void boot(Boolean b) {
        Config.getInstance().getConfig("mail", config -> {
            MailConfig cfg = new MailConfig();
            cfg.setHostname(config.getString("host"));
            cfg.setPort(config.getInteger("port"));
            cfg.setUsername(config.getString("username"));
            cfg.setPassword(config.getString("password"));
            this.mailClient = MailClient.createShared(vertx, cfg);


            eb.consumer("mail", this::sendMail);
        }, event -> {
            System.err.println("mail::" + event.getString("reason"));
            System.exit(-1);
        });
    }

    private <T> void sendMail(Message<T> message) {
        JsonObject data = ((JsonObject) message.body());
        MailMessage mailMessage = new MailMessage();
        mailMessage.setFrom("ender@webstaging.pt");
        mailMessage.setTo(data.getString("to"));
        mailMessage.setText(data.getJsonObject("content").getString("plain"));
        mailMessage.setHtml(data.getJsonObject("content").getString("html"));

        mailClient.sendMail(mailMessage, result -> {
            if (result.succeeded()) {
                System.out.println("sent");
            } else {
                result.cause().printStackTrace();
            }
        });
    }
}
