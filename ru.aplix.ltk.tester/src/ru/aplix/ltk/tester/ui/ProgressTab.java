package ru.aplix.ltk.tester.ui;

import static javax.swing.BorderFactory.createLineBorder;

import java.awt.FlowLayout;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JPanel;

import ru.aplix.ltk.core.reader.RfTag;


class ProgressTab extends JPanel {

	private static final long serialVersionUID = -5756411271969549347L;

	private final HashMap<RfTag, JLabel> tags = new HashMap<>();
	private final TesterContent content;

	ProgressTab(TesterContent content) {
		super(new FlowLayout(FlowLayout.LEFT));
		this.content = content;
	}

	public final TesterContent getContent() {
		return this.content;
	}

	public void addTag(final RfTag tag) {

		final JLabel existingLabel = this.tags.get(tag);
		final JLabel label;

		if (existingLabel != null) {
			// Move to the end
			remove(existingLabel);
			label = existingLabel;
			getContent().getLogTab().append("Тег появился вновь: " + tag);
		} else {
			label = new JLabel(tag.toString());
			getContent().getLogTab().append("Тег появился: " + tag);
		}

		label.setBorder(createLineBorder(getForeground().brighter(), 1, true));
		this.tags.put(tag, label);
		add(label);
	}

	public void removeTag(final RfTag tag) {

		final JLabel label = this.tags.remove(tag);

		if (label == null) {
			getContent().getLogTab().append("Незамеченный тег исчез: " + tag);
			return;
		}

		remove(label);
		getContent().getLogTab().append("Тег исчез: " + tag);
	}

}
