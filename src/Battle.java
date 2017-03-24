import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.Enumeration;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.plaf.FontUIResource;

public class Battle extends JPanel implements Runnable {

	JPanel rightPanel, buttons, danceFloor, danceGIF, leftPanel, topPanel;
	JButton[] dancebuttons = new JButton[5];
	ImageIcon dance1, dance2, dance3, dance4, dance5;
	JLabel moves[] = new JLabel[5];
	JButton start, done, pause, resume, reset;
	JLabel playerScore, opponentScore, timer, countdownText, leftLight,
			rightLight;

	static String playerName, firstPlayer;
	int score = 0;
	int oppscore = 0;
	static Timer countdownTimer, gifTimer;
	int timeRemaining = 10; // start countdown
	Color myColor = new Color(153, 0, 76);
	static int[] pattern = new int[5];
	static int[] oppPattern = new int[5];
	int moveNum = 0;
	int totalduration = 1080 * 5;
	JLabel d1, d2, d3, d4, d5;
	boolean isFirst = false; // rub from server maa
	boolean isPlaying = false;
	boolean isPaused = false;
	int increasedScore;
	int turnCount;
	boolean gifDone = false;

	Socket con;
	PrintWriter out;
	BufferedReader in;

	public Battle(String player, Socket con) {
		super();
		playerName = player;
		// BELLE
		// this.firstPlayer=firstPlayer;
		/*
		 * if(playerName.equals(firstPlayer)){ isFirst=true; timeRemaining = 10;
		 * }else{ isFirst = false; timeRemaining = 20; }
		 */
		this.con = con;
		try {
			this.out = new PrintWriter(con.getOutputStream(), true);
			this.in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// setSize(900,400);
		setLayout(new BorderLayout());
		init();
		setGUI();

	}

	public void init() {

		// JPanels
		topPanel = new JPanel(new BorderLayout());
		rightPanel = new JPanel(new GridLayout(0, 1));
		danceFloor = new JPanel(new BorderLayout());
		buttons = new JPanel(new GridLayout(1, 0));

		// ImageIcons
		dance1 = new ImageIcon("media/images/1.gif");
		dance2 = new ImageIcon("media/images/octopus.gif");
		dance3 = new ImageIcon("media/images/2.gif");
		dance4 = new ImageIcon("media/images/shuffling.gif");
		dance5 = new ImageIcon("media/images/im-on-fire.gif");

		// JButtons
		dancebuttons[0] = new JButton(dance1);
		dancebuttons[0].setBorder(BorderFactory
				.createLineBorder(Color.WHITE, 5));
		dancebuttons[0].addActionListener(action);
		dancebuttons[0].setEnabled(false);
		dancebuttons[0].setName("0");

		dancebuttons[1] = new JButton(dance2);
		dancebuttons[1].setBorder(BorderFactory
				.createLineBorder(Color.WHITE, 5));
		dancebuttons[1].addActionListener(action);
		dancebuttons[1].setEnabled(false);
		dancebuttons[1].setName("1");

		dancebuttons[2] = new JButton(dance3);
		dancebuttons[2].setBorder(BorderFactory
				.createLineBorder(Color.WHITE, 5));
		dancebuttons[2].addActionListener(action);
		dancebuttons[2].setEnabled(false);
		dancebuttons[2].setName("2");

		dancebuttons[3] = new JButton(dance4);
		dancebuttons[3].setBorder(BorderFactory
				.createLineBorder(Color.WHITE, 5));
		dancebuttons[3].addActionListener(action);
		dancebuttons[3].setEnabled(false);
		dancebuttons[3].setName("3");

		dancebuttons[4] = new JButton(dance5);
		dancebuttons[4].setBorder(BorderFactory
				.createLineBorder(Color.WHITE, 5));
		dancebuttons[4].addActionListener(action);
		dancebuttons[4].setEnabled(false);
		dancebuttons[4].setName("4");

		start = new JButton("Start Game");
		start.addActionListener(action);
		if (!isFirst)
			start.setEnabled(false);
		// start.setForeground(myColor);

		done = new JButton("STOP TIMER!");
		done.addActionListener(action);
		done.setEnabled(false);
		// done.setForeground(myColor);

		pause = new JButton("Pause");
		pause.addActionListener(action);

		resume = new JButton("Resume");
		resume.addActionListener(action);
		resume.setEnabled(false);
		
		reset = new JButton("Reset Game");
		reset.addActionListener(action);
		reset.setEnabled(true);

		// JLabel
		playerScore = new JLabel(playerName + "'s Score: " + score);
		opponentScore = new JLabel("Opponent Score: " + oppscore);
		countdownText = new JLabel("00:" + timeRemaining);
		danceGIF = new JPanel(new BorderLayout());
		danceGIF.setBackground(Color.black);
		leftLight = new JLabel();
		rightLight = new JLabel();

		ImageIcon left = new ImageIcon("media/images/spotlightleft.jpg"); // add
																			// loser
																			// gif
																			// later
		leftLight.setIcon(left);
		ImageIcon right = new ImageIcon("media/images/spotlightright.jpg"); // add
																			// loser
																			// gif
																			// later
		rightLight.setIcon(right);
		gifReset();

		// System.out.println("total" + totalduration);
		// Timer
		countdownTimer = new Timer(1000, new CountdownTimerListener());
		gifTimer = new Timer(1080, new GIFCountdownTimerListener());

	}

	public void setGUI() {
		// dance floor
		danceFloor.setSize(100, 100);
		danceFloor.setBackground(Color.BLACK);
		add(danceFloor, BorderLayout.CENTER);
		danceFloor.add(leftLight, BorderLayout.WEST);
		danceFloor.add(rightLight, BorderLayout.EAST);
		danceFloor.add(danceGIF, BorderLayout.CENTER);

		// game buttons
		buttons.add(dancebuttons[0]);
		buttons.add(dancebuttons[1]);
		buttons.add(dancebuttons[2]);
		buttons.add(dancebuttons[3]);
		buttons.add(dancebuttons[4]);
		add(buttons, BorderLayout.SOUTH);
		buttons.setBackground(myColor);

		// player info
		topPanel.setPreferredSize(new Dimension(900, 20));
		topPanel.setBackground(Color.ORANGE);
		playerScore.setFont(new Font("Courier", Font.BOLD, 14));
		opponentScore.setFont(new Font("Courier", Font.PLAIN, 14));
		playerScore.setForeground(Color.white);
		opponentScore.setForeground(Color.white);

		topPanel.add(playerScore, BorderLayout.WEST);
		topPanel.add(opponentScore, BorderLayout.EAST);
		add(topPanel, BorderLayout.NORTH);
		rightPanel.setBackground(Color.ORANGE);

		add(rightPanel, BorderLayout.EAST);
		countdownText.setHorizontalAlignment(countdownText.CENTER);
		countdownText.setFont(new Font("Courier", Font.BOLD, 18));
		rightPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

		rightPanel.add(countdownText);
		rightPanel.add(start);
		rightPanel.add(done);
		rightPanel.add(pause);
		rightPanel.add(resume);
		rightPanel.add(reset);

		setBackground(myColor);
		setVisible(true);

	}

	private ActionListener action = new ActionListener() { // change the content
		public void actionPerformed(ActionEvent event) {
			// TODO Auto-generated method stub
			if (event.getSource().equals(dancebuttons[0])) {
				dancebuttons[0].setEnabled(false);
				pattern[moveNum] = Integer.parseInt(dancebuttons[0].getName());
				moves[moveNum] = new JLabel(dancebuttons[0].getIcon());
				moves[moveNum].setName(dancebuttons[0].getName());

				System.out.println("move#" + moveNum + " = "
						+ dancebuttons[0].getName());

				moveNum++;
				checkMovesLeft();
			}
			if (event.getSource().equals(dancebuttons[1])) {
				System.out.println(dancebuttons[1].getName());

				dancebuttons[1].setEnabled(false);
				pattern[moveNum] = Integer.parseInt(dancebuttons[1].getName());
				moves[moveNum] = new JLabel(dancebuttons[1].getIcon());
				moves[moveNum].setName(dancebuttons[1].getName());

				System.out.println("move#" + moveNum + " = "
						+ dancebuttons[1].getName());

				moveNum++;
				checkMovesLeft();
			}
			if (event.getSource().equals(dancebuttons[2])) {
				dancebuttons[2].setEnabled(false);
				pattern[moveNum] = Integer.parseInt(dancebuttons[2].getName());
				moves[moveNum] = new JLabel(dancebuttons[2].getIcon());
				moves[moveNum].setName(dancebuttons[2].getName());

				System.out.println("move#" + moveNum + " = "
						+ dancebuttons[2].getName());

				moveNum++;
				checkMovesLeft();
			}
			if (event.getSource().equals(dancebuttons[3])) {
				dancebuttons[3].setEnabled(false);
				pattern[moveNum] = Integer.parseInt(dancebuttons[3].getName());
				moves[moveNum] = new JLabel(dancebuttons[3].getIcon());
				moves[moveNum].setName(dancebuttons[3].getName());
				System.out.println("move#" + moveNum + " = "
						+ dancebuttons[3].getName());

				moveNum++;
				checkMovesLeft();
			}
			if (event.getSource().equals(dancebuttons[4])) {
				dancebuttons[4].setEnabled(false);
				pattern[moveNum] = Integer.parseInt(dancebuttons[4].getName());
				moves[moveNum] = new JLabel(dancebuttons[4].getIcon());
				moves[moveNum].setName(dancebuttons[4].getName());

				System.out.println("move#" + moveNum + " = "
						+ dancebuttons[4].getName());

				moveNum++;
				checkMovesLeft();
			}
			if (event.getSource().equals(start)) {
				isPlaying = true;
				// TO SERVER
				out.println("\\battle-playing");
				out.flush();
				start.setEnabled(false);
				initializeTimer();

				if (isFirst) {
					enableButtons();
					danceFloorReset();
					// countdownTimer.start();

				}
				// else {
				// displayGIF();
				// }
				countdownTimer.start();

			}
			if (event.getSource().equals(done)) {
				isPlaying = false;
				if (isFirst) { // first player finished making pattern
					setAudio("media/sounds/cheering.wav");
					displayGIF();
					// BELLE
					out.println("\\battle-done: " + playerName);
					out.flush();
					System.out.println(playerName + " pressed done");

				} else { // secondplayer finished copying
					countdownTimer.stop();
					increaseScore();
					// takeTurn();
					System.out.println(playerName + " pressed done");

				}

			}
			if (event.getSource().equals(pause)) {
				// Pause p = new Pause(timeRemaining, countdownText);
				pause.setEnabled(false);
				countdownTimer.stop();
				resume.setEnabled(true);
				isPaused = true;
				// TO SERVER
				out.println("\\battle-pause");
				out.flush();
				// pause();
			}
			if (event.getSource().equals(resume)) {
				isPaused = false;
				// TO SERVER
				out.println("\\battle-unpause");
				out.flush();
				countdownTimer.restart();
				resume.setEnabled(false);
				pause.setEnabled(true);

			}
			if(event.getSource().equals(reset)){
				out.println("\\TO:CLIENT reset");
				out.flush();
			}
		}
	};

	public void updateScores() { // for user's UI :
									// update my score +
									// opponent score
		playerScore.setText(playerName + "'s Score: " + score);
		opponentScore.setText("Opponent Score: " + oppscore);
		playerScore.revalidate();
		opponentScore.revalidate();
		takeTurn();

	}

	public int increaseScore() {
		checkPattern();
		if (increasedScore == 5) {
			System.out.println("score+" + timeRemaining + "="
					+ (score + timeRemaining));
			score += timeRemaining; // add timeRemaining to score as bonus
		}
		// TO SERVER
		out.println("\\battle-" + playerName + "-score |" + score); // POON: get
																	// "|"
		out.flush();

		return score;

	}

	public void checkPattern() { // increase score by 1 for patterns that got
									// right
		increasedScore = 0;
		for (int i = 0; i < pattern.length; i++) {
			if (pattern[i] == oppPattern[i]) {
				increasedScore++;
				score += 1;
				System.out.println("score=" + score);
			}
		}
	}

	public void pause() {
		isPaused = true;
		countdownTimer.stop();
		pause.setEnabled(false);
	}

	public void displayGIF() {
		System.out.println("in displayGIF");
		System.out.println(playerName + "isFirst" + isFirst);

		countdownTimer.stop();
		if (gifDone) {
			System.out.println("giftimer restart");

			gifTimer.restart();
			gifDone = false;
		} else {
			System.out.println("giftimer start");
			gifTimer.start();
		}

	}

	public void checkMovesLeft() {
		if (moveNum > 4) {
			done.setEnabled(true);
			// check du cheichei
			String s = "";
			for (int i = 0; i < pattern.length; i++) {
				s += pattern[i];
			}

			// TO SERVER
			if (isFirst) {
				out.println("\\battle-pattern |" + s);
				out.flush();
			}

			moveNum = 0;
			// isPlaying = false;
		}

	}

	public void enableButtons() {
		dancebuttons[0].setEnabled(true);
		dancebuttons[1].setEnabled(true);
		dancebuttons[2].setEnabled(true);
		dancebuttons[3].setEnabled(true);
		dancebuttons[4].setEnabled(true);
	}

	class CountdownTimerListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (countdownTimer.isRunning() && timeRemaining >= 0) {
				if (timeRemaining >= 10)
					countdownText.setText("00:" + timeRemaining);
				else
					countdownText.setText("00:0" + timeRemaining);

				System.out.println("timer running: " + timeRemaining--);

			} else {
				countdownTimer.stop();
				initializeTimer();
			}

		}
	}

	class GIFCountdownTimerListener implements ActionListener {
		int t = totalduration;
		int i = 0;

		public void actionPerformed(ActionEvent event) {
			System.out.println("/////// i=" + i);
			// if (!gifDone) {
			// i = 0;
			// t = totalduration;
			// }
			if (isFirst && i == 0) {
				setAudio("media/sounds/biebs.wav");
				System.out.println("isfirst & i");

			}
			if (!isFirst) {
				System.out.println("!isfirst");
				if (i == 0) {
					System.out.println("!isfirst & i=" + i);
				}
			}
			if (gifTimer.isRunning() && t >= 0 && i < 5) {
				if (isFirst) {
					System.out.println("gif #: " + pattern[i]);
					System.out.println(moves[i].getName());
					t = t - 1080;
					gifReset();
					danceGIF.add(moves[i]);
					dancebuttons[pattern[i]].setBorder(BorderFactory
							.createLineBorder(Color.RED, 5));
					dancebuttons[pattern[i]].setEnabled(true);
					buttons.revalidate();
					i++;
				} else {
					System.out.println("gif #: " + oppPattern[i]);
					System.out.println(moves[i].getName());
					t = t - 1080;
					gifReset();
					danceGIF.add(moves[i]);
					dancebuttons[oppPattern[i]].setBorder(BorderFactory
							.createLineBorder(Color.RED, 5));
					dancebuttons[oppPattern[i]].setEnabled(true);
					buttons.revalidate();
					i++;
				}

			} else {
				if (isFirst) {
					gifReset();
					danceFloorReset();
				}
				// else {
				// countdownTimer.start();
				// }
				gifTimer.stop();
				gifReset();
				gifDone = true;
				System.out.println("gifdone = true");
				i = 0;
				t = totalduration;

			}

		}
	}

	public void initializeTimer() {
		if (isFirst) {
			timeRemaining = 10;
		} else {
			timeRemaining = 20;
		}
		countdownText.setText("00:" + timeRemaining);
		countdownText.revalidate();
	}

	public void takeTurn() {
		boolean b = isFirst;
		System.out.println("taking turns");
		turnCount++;

		if (b) { // originally first player
			isFirst = false;
			isPlaying = false;
			start.setEnabled(false);
		} else {
			isFirst = true;
			isPlaying = true;
			enableButtons();
			start.setEnabled(true);
			if (turnCount < 4) {
				if (turnCount != 2) {
					JOptionPane.showMessageDialog(null, "Turn #" + (turnCount)
							+ ": " + playerName + "make a pattern!");

				}
			}
		}
		
		System.out.println(playerName + " is first? " + isFirst);
		System.out.println("turn count is increased to " + turnCount);
		out.println("\\battle-turnCount |" + turnCount);
	}

	public void danceFloorReset() {
		danceGIF.removeAll();
		danceGIF.setBackground(Color.BLACK);
		danceFloor.revalidate();
		danceFloor.repaint();
	}

	public void gifReset() {
		if (!isPaused) {
			dance1.getImage().flush();
			dance2.getImage().flush();
			dance3.getImage().flush();
			dance4.getImage().flush();
			dance5.getImage().flush();
			d1 = new JLabel(dance1);
			d2 = new JLabel(dance2);
			d3 = new JLabel(dance3);
			d4 = new JLabel(dance4);
			d5 = new JLabel(dance5);
			danceGIF.removeAll();
			danceGIF.setBackground(Color.BLACK);
			danceGIF.revalidate();
			danceGIF.repaint();

			dancebuttons[0].setBorder(BorderFactory.createLineBorder(
					Color.WHITE, 5));
			// a.revalidate();
			dancebuttons[1].setBorder(BorderFactory.createLineBorder(
					Color.WHITE, 5));
			// b.revalidate();
			dancebuttons[2].setBorder(BorderFactory.createLineBorder(
					Color.WHITE, 5));
			// c.revalidate();
			dancebuttons[3].setBorder(BorderFactory.createLineBorder(
					Color.WHITE, 5));
			// d.revalidate();
			dancebuttons[4].setBorder(BorderFactory.createLineBorder(
					Color.WHITE, 5));
			// e.revalidate();
			buttons.revalidate();
		}

	}

	public void setAudio(String soundName) {
		try {
			AudioInputStream audioInputStream = AudioSystem
					.getAudioInputStream(new File(soundName).getAbsoluteFile());
			Clip clip = AudioSystem.getClip();
			clip.open(audioInputStream);
			clip.start();
		} catch (Exception ex) {
			System.out.println("Error with playing sound.");
			ex.printStackTrace();
		}

	}

	public void insertBGM(String soundName) throws LineUnavailableException {
		// if (activeClip != null && activeClip.isRunning()) {
		// activeClip.close();
		// }

		File soundFile = new File(soundName);
		AudioInputStream audioIn = null;
		Clip bgclip;
		try {
			audioIn = AudioSystem.getAudioInputStream(soundFile);

			bgclip = AudioSystem.getClip();
			bgclip.open(audioIn);
			bgclip.loop(Clip.LOOP_CONTINUOUSLY);

			// FloatControl gainControl = (FloatControl)
			// bgclip.getControl(FloatControl.Type.MASTER_GAIN);
			// gainControl.setValue(-5.0f); // increase volume by 10 decibels.
			// setAudio("media/sounds/nes-05-00.wav");
			bgclip.start();
			// activeClip = bgclip;

		} catch (IOException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) { // TODO Auto-generated catch
			// // block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// new Battle(playerName);
	}

	public void run() {
		// TODO Auto-generated method stub
		try {
			// BELLE
			String msg = "";
			while (true) { // read message receive from the server
				if ((msg = in.readLine()) != null) {
					processMsg(msg);
					System.out
							.println(playerName + " battle received ||" + msg);

					// out.println(playerName+"YO CLIENT");
					// out.flush();
					// System.out.println("battle mode");
					// out.println("battle mode");
				} else {
					System.out.println("not working");
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void processMsg(String msg) {
		// BELLE
		// out.println(msg);
		if (msg.contains("\\already")) {
			msg.replace(msg, "");
		} else if (msg.contains("\\TO:CLIENT")) {
			out.println(msg);
			out.flush();
		} else {
			if (msg.contains("\\battle") || msg.contains("\\TO:BATTLE")) {
				// System.out.println("working");
				// out.println(msg);

				if (msg.contains("-playing")) {
					initializeTimer();
					if (isFirst && isPlaying) {
						danceFloorReset();
						enableButtons();
					}
				}
				if (msg.contains("turnCount")) {
					int x = msg.indexOf("|") + 1;
					turnCount = Integer.parseInt(msg.replace(
							"\\TO:BATTLE battle-turnCount |", ""));
					System.out.println("turn count (battle) = " + turnCount);
					
					if ((msg.substring(x).equals("1"))) {
						pattern = new int[5];
						oppPattern = new int[5];
						moves = new JLabel[5];
						System.out.println("reset arrays");
						danceFloorReset();
						// takeTurn();

						// Boolean b = isFirst;
						// isFirst = !b;

						initializeTimer();
						if (isFirst) {
							/*
							 * out.println("\\battle " + playerName +
							 * " start playing");
							 */
							start.setEnabled(true);
							// timeRemaining = 10;
						} else {
							start.setEnabled(false);
						}
					}

				}
				if (msg.contains("new round")) {
					if (msg.contains(playerName)) {
						isFirst = true;
						start.setEnabled(true);
						initializeTimer();

						pattern = new int[5];
						oppPattern = new int[5];
						moves = new JLabel[5];
						System.out.println("reset arrays");
						danceFloorReset();
						
						// JOptionPane.showMessageDialog(null,
						// "New Round! "+playerName+" make a pattern!");

					} else {

						isFirst = false;
						start.setEnabled(false);
						danceFloorReset();
						// displayGIF();
						initializeTimer();
					}

				} else if (msg.contains(playerName)) {
					System.out.println(playerName);
					if (msg.contains("first player")) {
						if(turnCount!=2)
							JOptionPane.showMessageDialog(null, playerName
								+ " goes first!");

						isFirst = true;
						start.setEnabled(true);
						initializeTimer();
						if (turnCount==2)
							JOptionPane.showMessageDialog(null, "New round! "
									+ playerName + "make a pattern!");

						// timeRemaining = 10;
						// countdownText.setText("00:" + timeRemaining);

						// out.println("first");
						// System.out.print("first");
					}
					if (msg.contains("start playing")) {
						isPlaying = true;
						start.setEnabled(true);
						System.out.println(playerName + "is first =" + isFirst);
						if (!isFirst) {
							System.out.println("-------gor kao niiiii-----");
							danceFloorReset();
							displayGIF();
							initializeTimer();
							// timeRemaining = 20;
						} else {
							pattern = new int[5];
							oppPattern = new int[5];
							moves = new JLabel[5];
							System.out.println("reset arrays");
							danceFloorReset();
							System.out.println("----reset----");
						}

					}

					if (msg.contains("score")) {

						score = Integer
								.parseInt(msg.substring(msg.indexOf("|") + 1));

						updateScores();
						/*
						 * pattern = new int[5]; oppPattern = new int[5]; moves
						 * = new JLabel[5]; danceFloorReset(); takeTurn();
						 * System.out.println(playerName + " is now first:" +
						 * isFirst); // Boolean b = isFirst; // isFirst = !b;
						 * 
						 * initializeTimer(); if (isFirst) {
						 * out.println("\\battle " + playerName +
						 * " start playing"); start.setEnabled(true); //
						 * timeRemaining = 10; } else { start.setEnabled(false);
						 * }
						 */
					}
				} else { // no playername
					if (msg.contains("gave pattern")) {
						int x = msg.indexOf("|") + 1;
						String s = msg.substring(x);
						for (int i = 0; i < oppPattern.length; i++) {
							if (i == oppPattern.length - 1)
								oppPattern[i] = Integer
										.parseInt(s.substring(i));
							else
								oppPattern[i] = Integer.parseInt(s.substring(i,
										i + 1));
							System.out.println(oppPattern[i]);
						}
						for (int i = 0; i < 5; i++) {
							moves[i] = new JLabel(
									dancebuttons[oppPattern[i]].getIcon());
							moves[i].setName(dancebuttons[oppPattern[i]]
									.getName());
							System.out.println(moves[i].getName());
						}
						isPlaying = true;
						start.setEnabled(true);
						// displayGIF();
						timeRemaining = 20;
						// countdownTimer.start();

					}
					if (msg.contains("score")) {

						oppscore = Integer.parseInt(msg.substring(msg
								.indexOf("|") + 1));

						updateScores();
						System.out.println("updated opp score");
					}

					if (msg.contains("first player")) {
						isFirst = false;
						initializeTimer();
						// timeRemaining = 20;
						// countdownText.setText("00:" + timeRemaining);
					}

				}

			} else {
				// code

			}
		}

	}

}
