package ru.aplix.ltk.store.web.rcm.ui;

import static ru.aplix.ltk.core.collector.DefaultRfTrackingPolicy.RF_INVALIDATION_TIMEOUT;
import static ru.aplix.ltk.core.collector.DefaultRfTrackingPolicy.RF_TRANSACTION_TIMEOUT;
import static ru.aplix.ltk.core.collector.RfTrackingPolicy.DEFAULT_TRACKING_POLICY;
import ru.aplix.ltk.core.RfSettings;
import ru.aplix.ltk.core.collector.DefaultRfTrackingPolicy;
import ru.aplix.ltk.core.collector.RfTrackingPolicy;


public class RcmUITrackingPolicy {

	private String id;
	private Long transactionTimeout;
	private Long invalidationTimeout;

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getTransactionTimeout() {
		return this.transactionTimeout;
	}

	public void setTransactionTimeout(Long transactionTimeout) {
		this.transactionTimeout = transactionTimeout;
	}

	public Long getInvalidationTimeout() {
		return this.invalidationTimeout;
	}

	public void setInvalidationTimeout(Long invalidationTimeout) {
		this.invalidationTimeout = invalidationTimeout;
	}

	public RcmUITrackingPolicy set(RfTrackingPolicy policy) {
		if (policy == DEFAULT_TRACKING_POLICY) {
			return setDefault();
		}
		if (policy.getClass() == DefaultRfTrackingPolicy.class) {
			return set((DefaultRfTrackingPolicy) policy);
		}
		return reset();
	}

	public RcmUITrackingPolicy reset() {
		this.id = null;
		this.transactionTimeout = null;
		this.invalidationTimeout = null;
		return this;
	}

	public RcmUITrackingPolicy setDefault() {
		this.id = "default";
		this.transactionTimeout = RF_TRANSACTION_TIMEOUT.getDefault();
		this.invalidationTimeout = RF_INVALIDATION_TIMEOUT.getDefault();
		return this;
	}

	public RcmUITrackingPolicy set(DefaultRfTrackingPolicy policy) {
		setId("custom");
		setTransactionTimeout(policy.getTransactionTimeout());
		setInvalidationTimeout(policy.getInvalidationTimeout());
		return this;
	}

	public void fill(RfSettings settings) {

		final String id = getId();

		if (id == null) {
			return;
		}
		switch (id) {
		case "custom":
			fillCustom(settings);
			return;
		case "default":
			fillDefault(settings);
			return;
		}
	}

	private void fillDefault(RfSettings settings) {
		settings.setTrackingPolicy(DEFAULT_TRACKING_POLICY);
	}

	private void fillCustom(RfSettings settings) {

		final DefaultRfTrackingPolicy policy = new DefaultRfTrackingPolicy();

		policy.setTransactionTimeout(
				RF_TRANSACTION_TIMEOUT.valueOrDefault(
						getTransactionTimeout()));
		policy.setInvalidationTimeout(
				RF_INVALIDATION_TIMEOUT.valueOrDefault(
						getInvalidationTimeout()));

		settings.setTrackingPolicy(policy);
	}

}
