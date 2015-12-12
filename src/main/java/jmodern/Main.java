package jmodern;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.strands.Strand;
import co.paralleluniverse.strands.channels.Channel;
import co.paralleluniverse.strands.channels.Channels;
import co.paralleluniverse.strands.channels.SelectAction;
import static co.paralleluniverse.strands.channels.Selector.*;

public class Main {
    public static void main(String[] args) throws Exception {
        final Channel<Integer> ch1 = Channels.newChannel(0);
        final Channel<String> ch2 = Channels.newChannel(0);

        new Fiber<Void>(() -> {
            for (int i = 0; i < 10; i++) {
                Strand.sleep(100);
                ch1.send(i);
            }
            ch1.close();
        }).start();

        new Fiber<Void>(() -> {
            for (int i = 0; i < 10; i++) {
                Strand.sleep(130);
                ch2.send(Character.toString((char)('a' + i)));
            }
            ch2.close();
        }).start();

        new Fiber<Void>(() -> {
            for (int i = 0; i < 10; i++) {
                SelectAction<Object> sa
                        = select(receive(ch1),
                                receive(ch2));
                switch (sa.index()) {
                    case 0:
                        System.out.println(sa.message() != null ? "Got a number: " + (int) sa.message() : "ch1 closed");
                        break;
                    case 1:
                        System.out.println(sa.message() != null ? "Got a string: " + (String) sa.message() : "ch2 closed");
                        break;
                }
            }
        }).start().join(); // join waits for this fiber to finish
    }
}

