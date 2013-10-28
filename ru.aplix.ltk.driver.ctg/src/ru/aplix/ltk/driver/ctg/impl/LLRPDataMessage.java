package ru.aplix.ltk.driver.ctg.impl;

import static java.lang.System.currentTimeMillis;

import org.llrp.ltk.generated.interfaces.EPCParameter;
import org.llrp.ltk.generated.parameters.EPCData;
import org.llrp.ltk.generated.parameters.EPC_96;
import org.llrp.ltk.generated.parameters.TagReportData;
import org.llrp.ltk.types.BitArray_HEX;
import org.llrp.ltk.types.LLRPBitList;

import ru.aplix.ltk.core.source.RfDataMessage;
import ru.aplix.ltk.core.source.RfTag;


public class LLRPDataMessage implements RfDataMessage {

	private final long timestamp;
	private final RfTag tag;
	private final int antennaId;

	public LLRPDataMessage(CtgReaderThread reader, TagReportData tag) {
		this.timestamp = currentTimeMillis();
		this.antennaId = reader.antennaID(tag).getAntennaID().intValue();
		this.tag = new RfTag(tagData(tag));
	}

	@Override
	public long getTimestamp() {
		return this.timestamp;
	}

	@Override
	public int getAntennaId() {
		return this.antennaId;
	}

	@Override
	public RfTag getRfTag() {
		return this.tag;
	}

	@Override
	public int getRfTransactionId() {
		return 0;
	}

	@Override
	public boolean isRfTransactionEnd() {
		return false;
	}

	private static byte[] tagData(TagReportData tag) {

		final EPCParameter epc = tag.getEPCParameter();

		if (epc instanceof EPCData) {
			return epcData((EPCData) epc);
		}
		if (epc instanceof EPC_96) {
			return epc96Data((EPC_96) epc);
		}

		throw new IllegalArgumentException("Unsupported EPC: " + epc);
	}

	private static byte[] epcData(final EPCData epcData) {

		final BitArray_HEX epc = epcData.getEPC();
		final LLRPBitList result = new LLRPBitList();
		final int len = epc.size();

        for (int i = 0; i < len; i++) {
            result.add(epc.get(i).toBoolean());
        }

        return result.toByteArray();
	}

	private static byte[] epc96Data(final EPC_96 epc96) {
		return epc96.getEPC().encodeBinary().toByteArray();
	}

}
