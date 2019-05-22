package io.helidon.examples.conference.mp;

import java.util.Properties;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GreetMananger {

	private Properties systemProterties = new Properties();

	public void update(Properties systemProterties) {

		this.systemProterties = systemProterties;
	}

	public Properties getProperties() {

		return this.systemProterties;
	}
}
