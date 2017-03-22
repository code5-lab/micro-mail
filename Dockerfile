FROM vertx/vertx3

WORKDIR /usr/verticles

COPY ./target .
COPY env.json .

ENTRYPOINT ["sh", "-c"]
#ENTRYPOINT ["tail", "-f", "/dev/null"] #debug
CMD ["export CLASSPATH=`find /usr/verticles -printf '%p:' | sed 's/:$//'`; exec vertx run pt.code5.micro.mail.MailVerticle -cluster"]