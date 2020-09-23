package it.unibo.oop.lab10.ex03;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.util.function.Function;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

/**
 * Modify this small program adding new filters.
 * 
 * 1) Convert to lowercase
 * 
 * 2) Count the number of chars
 * 
 * 3) Count the number of lines
 * 
 * 4) List all the words in alphabetical order
 * 
 * 5) Write the count for each word, e.g. "word word pippo" should output "pippo -> 1 word -> 2
 * 
 * @author Danilo Pianini
 *
 */
public final class LambdaFilter extends JFrame {

	private static final long serialVersionUID = 1760990730218643730L;
	private static final String TOKEN_SYMBOLS = " \t\n\r\f,.:;?!¡";

	private static enum Command {
		IDENTITY("No modifications", s -> s),
		TO_LOWER("Lowercase", s -> s.toLowerCase()),
		COUNT("Count chars", s -> Integer.toString(s.length())),
		LINES("Count lines", s -> Long.toString(s.chars().filter(e -> e == '\n').count() + 1)),
		WORDS("Sort words in alphabetical order", s -> {
			final StringTokenizer tk = new StringTokenizer(s, TOKEN_SYMBOLS);
			final SortedSet<String> set = new TreeSet<>();
			while (tk.hasMoreElements()) {
				set.add(tk.nextToken());
			}
			final StringBuilder sb = new StringBuilder();
			for (final String word: set) {
				sb.append(word);
				sb.append('\n');
			}
			return sb.toString();
		}),
		WORDCOUNT("Count words", s -> {
			final StringTokenizer tk = new StringTokenizer(s, TOKEN_SYMBOLS);
			final Map<String, Integer> map = new HashMap<>();
			while (tk.hasMoreElements()) {
				final String key = tk.nextToken();
				map.merge(key, 1, (pre, cur) -> pre + 1);
			}
			final StringBuilder sb = new StringBuilder();
			map.forEach((k, v) -> {
				sb.append(k);
				sb.append(" -> ");
				sb.append(v);
				sb.append('\n');
			});
			return sb.toString();
		});

		private final String commandName;
		private final Function<String, String> fun;

		private Command(final String name, final Function<String, String> process) {
			commandName = name;
			fun = process;
		}

		@Override
		public String toString() {
			return commandName;
		}

		public String translate(final String s) {
			return fun.apply(s);
		}

	}

	private LambdaFilter() {
		super("Lambda filter GUI");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final JPanel panel1 = new JPanel();
		final LayoutManager layout = new BorderLayout();
		panel1.setLayout(layout);

		final JComboBox<Command> combo = new JComboBox<>(Command.values());
		panel1.add(combo, BorderLayout.NORTH);

		final JPanel centralPanel = new JPanel(new GridLayout(1, 2));
		final JTextArea left = new JTextArea();
		left.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		final JTextArea right = new JTextArea();
		right.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		right.setEditable(false);
		centralPanel.add(left);
		centralPanel.add(right);

		panel1.add(centralPanel, BorderLayout.CENTER);

		final JButton apply = new JButton("Apply");
		apply.addActionListener((ev) -> right.setText(((Command) combo.getSelectedItem()).translate(left.getText())));
		panel1.add(apply, BorderLayout.SOUTH);

		setContentPane(panel1);

		final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		final int sw = (int) screen.getWidth();
		final int sh = (int) screen.getHeight();
		setSize(sw / 4, sh / 4);

		setLocationByPlatform(true);
	}

	/**
	 * @param a
	 *            unused
	 */
	public static void main(final String... a) {
		final LambdaFilter gui = new LambdaFilter();
		gui.setVisible(true);
	}

}
