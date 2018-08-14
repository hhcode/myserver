package myservice;

import java.util.UUID;

public class Test {
	@org.junit.Test
	public void testUUID() {
		System.out.println(UUID.randomUUID().toString());
	}
}
