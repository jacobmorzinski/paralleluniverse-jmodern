package jmodern;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.strands.Strand;
import co.paralleluniverse.strands.channels.Channel;
import co.paralleluniverse.strands.channels.Channels;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        final Channel<Integer> ch = Channels.newChannel(0);

        new Fiber<Void>(() -> {
            for (int i = 0; i < 100000; i++) {
                Strand.sleep(100);
                log.info("Sending {}", i); // log something
                ch.send(i);
                if (i % 10 == 0)
                    log.warn("Sent {} messages", i + 1); // log something
            }
            ch.close();
        }).start();

        new Fiber<Void>(() -> {
            Integer x;
            while ((x = ch.receive()) != null)
                System.out.println("--> " + x);
        }).start().join(); // join waits for this fiber to finish
    }
}
