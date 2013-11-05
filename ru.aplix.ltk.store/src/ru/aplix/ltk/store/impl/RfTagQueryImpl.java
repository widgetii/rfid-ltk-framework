package ru.aplix.ltk.store.impl;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;

import ru.aplix.ltk.store.RfTagQuery;
import ru.aplix.ltk.store.impl.persist.RfTagEventData;


final class RfTagQueryImpl extends RfTagQuery {

	private final RfStoreImpl store;
	private long totalCount;

	RfTagQueryImpl(RfStoreImpl store) {
		this.store = store;
	}

	@Override
	public List<RfTagEventData> find() {

		final List<RfTagEventData> events =
				this.store.findEvents(this, totalsQuery(), listQuery());

		for (RfTagEventData event : events) {
			event.init(this.store);
		}

		return events;
	}

	@Override
	public long getTotalCount() {
		return this.totalCount;
	}

	void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	private TypedQuery<RfTagEventData> listQuery() {

		final EntityManager em = this.store.getEntityManager();
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<RfTagEventData> qb =
				cb.createQuery(RfTagEventData.class);
		final Root<RfTagEventData> event = qb.from(RfTagEventData.class);

		where(cb, qb, event);
		qb.orderBy(
				cb.desc(event.get("timestamp")),
				cb.desc(event.get("id")));

		final TypedQuery<RfTagEventData> listQuery =
				em.createQuery(qb.select(event));

		listQuery.setFirstResult(getOffset());
		listQuery.setMaxResults(getLimit());

		return listQuery;
	}

	private TypedQuery<Long> totalsQuery() {

		final EntityManager em = this.store.getEntityManager();
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Long> qb =
				cb.createQuery(Long.class);
		final Root<RfTagEventData> event = qb.from(RfTagEventData.class);

		where(cb, qb, event);

		return em.createQuery(qb.select(cb.count(event.get("id"))));
	}

	private void where(
			CriteriaBuilder cb,
			CriteriaQuery<?> qb,
			Root<RfTagEventData> event) {

		Predicate pred = null;

		if (getReceiverId() > 0) {
			pred = cb.equal(event.get("receiverId"), getReceiverId());
		}
		if (getSince() > 0) {
			pred = and(
					cb,
					pred,
					cb.greaterThanOrEqualTo(
							event.get("timestamp").as(Timestamp.class),
							new Timestamp(getSince())));
		}

		if (getTag() != null) {
			pred = and(cb, pred, cb.equal(event.get("tag"), getTag()));
		}
		if (pred != null) {
			qb.where(pred);
		}
	}

	private static Predicate and(
			CriteriaBuilder cb,
			Predicate pred1,
			Predicate pred2) {
		if (pred1 == null) {
			return pred2;
		}
		return cb.and(pred1, pred2);
	}

}
