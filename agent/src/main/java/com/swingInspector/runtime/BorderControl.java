package com.swingInspector.runtime;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Set;

/**
 * author: alex
 * date  : 11/10/13
 */
public class BorderControl {
	private MouseAdapter currentBorderListener;
	private ComponentHighlightConfiguration currentBorderConfig;
	private final ComponentListener BORDER_LISTENER = new ComponentListener() {
		@Override
		public void onComponent(JComponent c) {
			if (currentBorderListener != null)
				c.addMouseListener(currentBorderListener);
		}
	};

	public void enableBorder(final ComponentHighlightConfiguration configuration) {
		if (currentBorderConfig != null && currentBorderConfig.equals(configuration)) {
			return;
		}

		if (currentBorderConfig != null) {
			disableBorder();//remove all old borders
		}

		Components components = SwingComponentHolder.components;
		currentBorderListener = new BorderMouseListeners(configuration, components);
		currentBorderConfig = configuration;
		Set<JComponent> set = components.componentsSet();
		for (JComponent cc : set) {
			cc.addMouseListener(currentBorderListener);
		}
		components.addListener(BORDER_LISTENER);
	}

	public void disableBorder() {
		Components components = SwingComponentHolder.components;
		for (JComponent component : components.componentsSet()) {
			component.removeMouseListener(currentBorderListener);
			restoreOriginalBorder(components, component);
		}
		components.removeListener(BORDER_LISTENER);
	}

	private static void restoreOriginalBorder(Components components, JComponent c) {
		Components.ComponentInformationHolder data = components.getData(c);
		Border b = data.getData("border");
		Boolean hasBorder = data.getData("has_border");
		if (hasBorder != null && hasBorder) {
			c.setBorder(b);
		}
	}

	private static void setupCustomBorder(Components components, ComponentHighlightConfiguration configuration, JComponent c) {
		Border newBorder = BorderFactory.createLineBorder(configuration.getBorderColor());
		Border currentBorder = c.getBorder();

		Components.ComponentInformationHolder data = components.getData(c);
		data.addData("border", currentBorder);
		data.addData("has_border", true);
		c.setBorder(newBorder);
	}

	private static class BorderMouseListeners extends MouseAdapter {
		private final ComponentHighlightConfiguration configuration;
		private final Components components;

		public BorderMouseListeners(ComponentHighlightConfiguration configuration, Components components) {
			this.configuration = configuration;
			this.components = components;
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			Component component = e.getComponent();
			if (component instanceof JComponent) {
				JComponent c = (JComponent) component;
				setupCustomBorder(components, configuration, c);
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			Component component = e.getComponent();
			if (component instanceof JComponent) {
				JComponent c = (JComponent) component;
				restoreOriginalBorder(components, c);
			}
		}
	}
}