module dev.watercooler.coolcoin.core {
    requires static lombok;
    requires netty.all;
    requires com.google.gson;
    requires org.slf4j;
    requires ch.qos.logback.classic;
    requires org.bouncycastle.provider;

    exports dev.watercooler.coolcoin;
}