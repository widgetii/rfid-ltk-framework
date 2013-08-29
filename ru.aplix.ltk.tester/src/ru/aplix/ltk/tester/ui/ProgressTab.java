package ru.aplix.ltk.tester.ui;

import static javax.swing.BorderFactory.createLineBorder;
import static ru.aplix.ltk.tester.ui.UIUtil.invokeInUI;

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
		invokeInUI(new Runnable() {
			@Override
			public void run() {
				doAddTag(tag);
			}
		});
	}

	public void removeTag(final RfTag tag) {
		invokeInUI(new Runnable() {
			@Override
			public void run() {
				doRemoveTag(tag);
			}
		});
	}

	private void doAddTag(RfTag tag) {

		final JLabel existingLabel = this.tags.get(tag);
		final JLabel label;

		if (existingLabel != null) {
			// Move to the end
			remove(existingLabel);
			label = existingLabel;
			getContent().getLogTab().appendMessage(
					"Тег появился вновь: " + tag);
		} else {
			label = new JLabel(tag.toString());
			getContent().getLogTab().appendMessage("Тег появился: " + tag);
		}

		label.setBorder(createLineBorder(getForeground().brighter(), 1, true));
		this.tags.put(tag, label);
		add(label, FlowLayout.LEADING);
	}

	private void doRemoveTag(RfTag tag) {

		final JLabel label = this.tags.remove(tag);

		if (label == null) {
			getContent().getLogTab().appendMessage(
					"Незамеченный тег исчез: " + tag);
			return;
		}

		remove(label);
		getContent().getLogTab().appendMessage("Тег исчез: " + tag);
	}

}
