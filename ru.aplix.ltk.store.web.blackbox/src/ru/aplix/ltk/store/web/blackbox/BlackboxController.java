package ru.aplix.ltk.store.web.blackbox;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller("blackboxController")
@RequestMapping("/blackbox")
public class BlackboxController {

	@Autowired
	private BlackboxRunner runner;

	@RequestMapping(method = RequestMethod.GET)
	public void runTests(HttpServletResponse resp) throws IOException {

		final TestResults result = this.runner.run();
		@SuppressWarnings("resource")
		final PrintWriter out = resp.getWriter();

		result.print(out);
		out.flush();
	}

}
