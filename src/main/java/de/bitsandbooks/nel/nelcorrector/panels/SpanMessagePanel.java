package de.bitsandbooks.nel.nelcorrector.panels;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import de.bitsandbooks.nel.nelcorrector.ComponentMap;
import de.bitsandbooks.nel.nelcorrector.properties.GeneralProperties;

public class SpanMessagePanel extends JPanel {
	
	private Font font;
	private EmptyBorder borderPlain = new EmptyBorder(new Insets(10, 10, 10, 10));
	
	public SpanMessagePanel(ComponentMap components) {
		GeneralProperties props = GeneralProperties.getInstance();
		font = props.getMainFont();
		setLayout(new FlowLayout());
		setBorder(borderPlain);
		//Button
		JLabel lb = (JLabel)components.get(ComponentMap.LabelEntrySpansWarning);
		lb.setFont(font);
		lb.setForeground(Color.RED);
		add(lb);
	}

}
