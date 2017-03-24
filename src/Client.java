import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client extends JFrame {

	Socket con = null;
	PrintWriter out = null;
	BufferedReader in = null;
	InputPanel inputPanel = new InputPanel();
	JTextArea textView = new JTextArea();
	JScrollPane scrolledTextView = new JScrollPane(textView);
	int id = 0;
	final static String FRAME_TITLE = "Echo Client";

	private BufferedImage img;

	String playerName, firstPlayer;
	int scores;

	MainMenu menu;
	MyPanel chat;

	boolean isReset = false;
	boolean isPlaying;
	boolean isFirst;

	int score1, score2;
	String player1, player2;
	String playerName1, playerName2;

	Executor x = Executors.newFixedThreadPool(10);

	public Client(String title) {
		super(title);
		playerName = JOptionPane.showInputDialog("What is your name?");
		connectToEchoServer();
		runUpdateText();
		setGUI();

	}

	private void runUpdateText() {
		// Executor x = Executors.newSingleThreadExecutor();
		Runnable r = new textUpdate();
		Thread t = new Thread(r);
		x.execute(t);
	}

	public void setGUI() {
		setLayout(new BorderLayout());

		menu = new MainMenu(playerName, con, x);
		menu.setPreferredSize(new Dimension(900, 400));
		// Executor a = Executors.newSingleThreadExecutor();
		// Runnable run = menu;
		// x.execute(run);
		add(menu, BorderLayout.WEST);

		chat = new MyPanel();
		chat.setVisible(true);

		try {
			// con = new Socket(InetAddress.getByName("127.0.0.1"), 10007);
			// out = new PrintWriter(con.getOutputStream(), true);
			// out.println("\\menu");
			img = ImageIO.read(new File("media/images/chatwallpaper.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		chat.setLayout(new BorderLayout());

		scrolledTextView.setPreferredSize(new Dimension(200, 400));
		scrolledTextView.setOpaque(false);

		chat.add(scrolledTextView, BorderLayout.CENTER);
		textView.setEditable(false);
		textView.setOpaque(false);
		// textView.setBackground(Color.red);
		textView.setForeground(Color.BLACK);
		// textView.setBackground(Color.BLACK);
		textView.setFont(new Font("Courier", Font.ITALIC, 14));
		chat.add(inputPanel, BorderLayout.SOUTH);
		// chat.setOpaque(false);
		add(chat);

	}

	private class MyPanel extends JPanel {
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(img, 0, 0, getWidth(), getHeight(), this);

		}
	}

	public void processEchoedMsg(String msg) throws IOException {
		System.out.println("client received ||" + msg);
		if (msg.contains("\\already")) {
			msg.replace(msg, "");
		} else if (msg.contains("\\TO:BATTLE")) {
			out.println(msg);
			out.flush();
		} else {
			if (msg.contains("\\close")) {
				msg = msg.replace("\\close", "");
				textView.append(msg + "\n");
				inputPanel.setEnabled(false);
			} else if (msg.indexOf("\\clientid") != -1) {
				int id = Integer.parseInt(msg.replace("\\clientid", ""));
				setTitle(FRAME_TITLE + " #" + id);
			} else if (msg.contains("reset") || (msg.contains("\\TO:CLIENT goToScoreboard"))) {
				this.remove(menu);
				menu = new MainMenu(playerName, con, x);
				menu.setPreferredSize(new Dimension(900, 400));
				// Runnable run = menu;
				// x.execute(run);
				add(menu, BorderLayout.WEST);
				this.add(menu, BorderLayout.WEST);
				this.revalidate();
				msg.replace(msg, "");
				out.println("\\already reset");
				// } else if (msg.contains("\\TO:CLIENT goToScoreboard")){
				// msg.replace(msg, "");
				// this.remove(menu);
				//
				//
				// Scoreboard s = new Scoreboard(playerName, score1, player2,
				// score2, con, x);
				// s.setPreferredSize(new Dimension(900, 400));
				//
				// add(s, BorderLayout.WEST);
				// this.add(s, BorderLayout.WEST);
				// this.revalidate();
				//
				// out.println("\\already goToScoreboard");
				// out.flush();

			} else if (msg.contains("goToResults")) {
				this.remove(menu);
				msg=msg.replace("\\TO:CLIENT goToResults ", "");
				int index = msg.indexOf("|");
				setNameScore(msg, index);
				
				System.out.println(playerName+" " + playerName2);
				
				Results r = new Results(playerName, playerName2, score1, score2, con);
				r.setPreferredSize(new Dimension(900, 400));

				add(r, BorderLayout.WEST);
				this.add(r, BorderLayout.WEST);
				this.revalidate();
				msg.replace(msg, "");
				out.println("\\already goToResults");
				out.flush();
			} else if (msg.contains("\\battle first player: ")) {
				firstPlayer = msg.replace("Client #" + id + "\\battle first player: ", "");
			} else if (msg.contains("turnCount")) {
				// int x = msg.indexOf("|") + 1;

				out.println("\\battle-new-round");
				out.flush();

			} else if (msg.contains("goToBattle")) {
				try {
					out = new PrintWriter(con.getOutputStream(), true);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				out.println("\\TO:BATTLE battle first player: " + firstPlayer);
				out.flush();
				// this.inputPanel.setEnabled(false);
			} else {
				textView.append(msg + "\n");
			}
		}
	}

	public void setNameScore(String msg, int index) {
		player1 = msg.substring(0, index);
		player2 = msg.substring(index + 1);
		System.out.println(player1);
		System.out.println(player2);

		if (player1.contains(playerName)) {
			score1 = Integer.parseInt(player1.substring(player1.indexOf(",") + 1));
			score2 = Integer.parseInt(player2.substring(player2.indexOf(",") + 1));
			playerName1 = player1.substring(0, player1.indexOf(","));
			playerName2 = player2.substring(0, player2.indexOf(","));
		} else {
			score1 = Integer.parseInt(player2.substring(player2.indexOf(",") + 1));
			score2 = Integer.parseInt(player1.substring(player1.indexOf(",") + 1));
			playerName1 = player2.substring(0, player2.indexOf(","));
			playerName2 = player1.substring(0, player1.indexOf(","));
		}
	}

	public void connectToEchoServer() {
		try {
			// con = new Socket(InetAddress.getByName("10.207.156.109"), 10007);
			con = new Socket(InetAddress.getByName("127.0.0.1"), 10007);
			out = new PrintWriter(con.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			try {
				out.println("\\clientName: " + playerName);
				out.println("\\clientid");
				out.flush();
				processEchoedMsg(in.readLine());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void createAndRunClient() {
		Client mainFrame = new Client(FRAME_TITLE);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setPreferredSize(new Dimension(1100, 400));
		mainFrame.pack();
		mainFrame.setVisible(true);
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndRunClient();
			}
		});
	}

	class InputPanel extends JPanel {
		JButton buttonSend = new JButton("Send");
		JTextField textInput = new JTextField();

		public InputPanel() {
			setLayout(new BorderLayout());
			add(textInput, BorderLayout.CENTER);
			add(buttonSend, BorderLayout.EAST);
			buttonSend.addActionListener(new TalkHandler());
			textInput.addActionListener(new TalkHandler());
			setOpaque(false);

		}

		public void setEnabled(boolean b) {
			buttonSend.setEnabled(b);
			textInput.setEnabled(b);
		}

		class TalkHandler implements ActionListener, KeyListener {

			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				String msg = textInput.getText();
				textInput.setText("");
				Client.this.out.println(msg);
				Client.this.out.flush();
				/*
				 * try { Client.this.processEchoedMsg(in.readLine()); } catch
				 * (IOException e) { // TODO Auto-generated catch block
				 * e.printStackTrace(); }
				 */
			}

			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					String msg = textInput.getText();
					textInput.setText("");
					Client.this.out.println(msg);
					Client.this.out.flush();
				}

			}

			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}
		}
	}

	class textUpdate implements Runnable {

		public void run() {
			boolean run = true;
			String input;
			while (run) {
				try {
					while ((input = Client.this.in.readLine()) != null) {
						Client.this.processEchoedMsg(input);
					}
				} catch (IOException e) {
					run = false;
				}
			}
		}

	}

}
