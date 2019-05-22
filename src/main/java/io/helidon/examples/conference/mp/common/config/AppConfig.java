package io.helidon.examples.conference.mp.common.config;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Provider;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@RequestScoped
public class AppConfig {

	/**
	 * The Provider<> interface used, forces the service to retrieve the
	 * inMaintenance value just in time. This retrieval of the value just in time
	 * makes the config injection dynamic and able to change without having to
	 * restart the application.
	 */
	@Inject
	@ConfigProperty(name = "default.region")
	private Provider<String> DEFAULT_REGION;

	@Inject
	@ConfigProperty(name = "tenancy.ocid")
	private String TENANCY_OCID;

	@Inject
	@ConfigProperty(name = "user.ocid")
	private String USER_OCID;

	@Inject
	@ConfigProperty(name = "fingerprint")
	private String FINGERPRINT;

	@Inject
	@ConfigProperty(name = "private.pem")
	private String PRIVATE_PEM;

	@Inject
	@ConfigProperty(name = "default.compartment.id")
	private String DEFAULT_COMPARTMENT_ID;

	/**
	 * The Provider<> interface used, forces the service to retrieve the
	 * inMaintenance value just in time. This retrieval of the value just in time
	 * makes the config injection dynamic and able to change without having to
	 * restart the application.
	 */
	@Inject
	@ConfigProperty(name = "endpoint.containerengine.default")
	private Provider<String> ENDPOINT_CONTAINERENGINE_DEFAULT;

	@Inject
	@ConfigProperty(name = "restapi.listclusters")
	private String RESTAPI_LIST_CLUSTERS;

	public String getDEFAULT_REGION() {
		return DEFAULT_REGION.get();
	}

	public String getTENANCY_OCID() {
		return TENANCY_OCID;
	}

	public String getUSER_OCID() {
		return USER_OCID;
	}

	public String getFINGERPRINT() {
		return FINGERPRINT;
	}

	public String getPRIVATE_PEM() {
		return PRIVATE_PEM;
	}

	public String getDEFAULT_COMPARTMENT_ID() {
		return DEFAULT_COMPARTMENT_ID;
	}

	public String getENDPOINT_CONTAINERENGINE_DEFAULT() {
		return ENDPOINT_CONTAINERENGINE_DEFAULT.get();
	}

	public String getRESTAPI_LIST_CLUSTERS() {
		return RESTAPI_LIST_CLUSTERS;
	}

}
