package jmodern;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.strands.Strand;
import co.paralleluniverse.strands.channels.Channel;
import co.paralleluniverse.strands.channels.Channels;

public class Main {
    public static void main(String[] args) throws Exception {
        final Channel<Integer> ch = Channels.newChannel(0);

	new Thread(Strand.toRunnable(() -> {
	    for (int i = 0; i < 10; i++) {
		Strand.sleep(100);
		ch.send(i);
	    }
	    ch.close();
	})).start();

        new Fiber<Void>(() -> {
            Integer x;
            while((x = ch.receive()) != null)
                System.out.println("--> " + x);
        }).start().join(); // join waits for this fiber to finish
    }
}
