package net.luminis.futureclock;

import net.luminis.clock.Clock;

public class FutureClock implements Clock {

	@Override
	public String time() {
		return "THE FUTURE!";
	}

}
