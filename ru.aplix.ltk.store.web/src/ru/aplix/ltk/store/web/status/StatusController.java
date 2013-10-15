package ru.aplix.ltk.store.web.status;

import static java.lang.System.currentTimeMillis;
import static ru.aplix.ltk.monitor.MonitoringSeverity.severityByAbbr;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ru.aplix.ltk.monitor.*;


@Controller("status")
@RequestMapping("/status.do")
public class StatusController {

	@Autowired
	private Monitor monitor;

	@RequestMapping(method = RequestMethod.GET)
	public void status(
			@RequestParam(value = "severity", required = false) String severity,
			@RequestParam(value = "period", required = false) String period,
			HttpServletResponse resp)
	throws IOException {
		resp.setContentType("text/plain;charset=UTF-8");

		@SuppressWarnings("resource")
		final PrintWriter out = resp.getWriter();

		out.println("Monitoring Report");
		out.println("=================");
		out.println();

		final TextMonitoringReport report = new TextMonitoringReport(out);

		assignMinSeverity(report, severity);
		assignPeriod(report, period);

		this.monitor.report(report);

		out.flush();
	}

	private void assignMinSeverity(
			TextMonitoringReport report,
			String abbr) {
		if (abbr == null) {
			return;
		}

		final String normalizedAbbr = abbr.toUpperCase();

		if ("ALL".equals(normalizedAbbr)) {
			report.setMinSeverity(MonitoringSeverity.TRACE);
			return;
		}

		final MonitoringSeverity severity = severityByAbbr(normalizedAbbr);

		if (severity != null) {
			report.setMinSeverity(severity);
		}
	}

	private void assignPeriod(MonitoringReport report, String periodParam) {
		if (periodParam == null || periodParam.isEmpty()) {
			return;
		}

		final int hours;
		final int mins;
		final int colonIdx = periodParam.indexOf(':');

		if (colonIdx < 0) {
			hours = Integer.parseInt(periodParam);
			mins = 0;
		} else {
			hours = Integer.parseInt(periodParam.substring(0, colonIdx));
			mins = Integer.parseInt(periodParam.substring(colonIdx + 1));
		}

		final long period = (hours * 60 + mins) * 60 * 1000;

		report.setSince(currentTimeMillis() - period);
	}
}
