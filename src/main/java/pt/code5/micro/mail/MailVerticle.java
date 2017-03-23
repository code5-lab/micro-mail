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
        Config.getInstance().getConfig("mail", result -> {
            if(result.failed()){
                System.err.println("mail::" + result.cause());
                System.exit(-1);
            }


            MailConfig cfg = new MailConfig();
            cfg.setHostname(result.result().getJsonObject("result").getString("host"));
            cfg.setPort(result.result().getJsonObject("result").getInteger("port"));
            cfg.setUsername(result.result().getJsonObject("result").getString("username"));
            cfg.setPassword(result.result().getJsonObject("result").getString("password"));
            this.mailClient = MailClient.createShared(vertx, cfg);


            eb.consumer("mail", this::sendMail);
        });
    }

    private <T> void sendMail(Message<T> message) {
        JsonObject data = ((JsonObject) message.body());
        MailMessage mailMessage = new MailMessage();
        mailMessage.setFrom("sender@webstaging.pt");
        mailMessage.setTo(data.getString("to"));
        mailMessage.setText(data.getJsonObject("content").getString("plain"));
        mailMessage.setHtml(data.getJsonObject("content").getString("html"));

        mailClient.sendMail(mailMessage, result -> {
            if (result.succeeded()) {
                message.reply(new JsonObject().put("success", true));
            } else {
                message.reply(new JsonObject().put("fail", result.cause()));
                result.cause().printStackTrace();
            }
        });
    }
}
