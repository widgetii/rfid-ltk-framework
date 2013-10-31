package ru.aplix.ltk.store.web.receiver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import ru.aplix.ltk.store.RfStore;


@Controller("rfTagController")
public class RfTagController {

	@Autowired
	private RfStore rfStore;

	@RequestMapping(
			value = "/tags/find.json",
			method = RequestMethod.GET)
	@ResponseBody
	public FoundRfTagsBean findTags(
			@RequestBody RfTagQueryBean queryBean) {
		return new FoundRfTagsBean(queryBean.buildQuery(this.rfStore));
	}

}
