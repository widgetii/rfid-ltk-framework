package ru.aplix.ltk.tester.ui;

import static javax.swing.BorderFactory.createLineBorder;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JPanel;

import ru.aplix.ltk.core.source.RfTag;


public class TagsPanel extends JPanel {

	private static final long serialVersionUID = 4354959487677274857L;

	private final ProgressTab progressTab;
	private final HashMap<RfTag, JLabel> tags = new HashMap<>();

	public TagsPanel(ProgressTab progressTab) {
		super(new FlowLayout(FlowLayout.LEFT));
		setPreferredSize(new Dimension(800, 250));
		this.progressTab = progressTab;
	}

	public final ProgressTab getProgressTab() {
		return this.progressTab;
	}

	public final LogPanel getLog() {
		return getProgressTab().getLog();
	}

	public final TesterContent getContent() {
		return getProgressTab().getContent();
	}

	public void clear() {
		for (JLabel label : this.tags.values()) {
			remove(label);
		}
		this.tags.clear();
	}

	public void addTag(final RfTag tag) {

		final JLabel existingLabel = this.tags.get(tag);
		final JLabel label;

		if (existingLabel != null) {
			// Move to the end
			remove(existingLabel);
			label = existingLabel;
			getLog().append("Тег появился вновь: " + tag);
		} else {
			label = new JLabel(tag.toString());
			label.setBorder(
					createLineBorder(getForeground().brighter(), 1, true));
			getLog().append("Тег появился: " + tag);
		}

		this.tags.put(tag, label);
		add(label);

		validate();
		repaint();
	}

	public void removeTag(final RfTag tag) {

		final JLabel label = this.tags.remove(tag);

		if (label == null) {
			getLog().append("Незамеченный тег исчез: " + tag);
			return;
		}

		remove(label);
		getLog().append("Тег исчез: " + tag);

		validate();
		repaint();
	}

}
