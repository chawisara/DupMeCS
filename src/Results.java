import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Enumeration;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;

public class Results extends JPanel {

	JPanel topPanel, bottomPanel, gifPanel;
	static String playername, oppname;
	String result;
	JLabel score, resultannounce, dance, playerscoreannounce, oppscoreannounce;
	Color myColor = new Color(153, 0, 76);
	int playerscore, oppscore, winnerscore;
	static Timer countdownTimer, gif1, gif2, gif3;
	int timeRemaining = 1;
	boolean b1, b2, b3 = false;
	PrintWriter out;
	Socket con;
	Executor x;

	public Results(String player, String oppname, int playerscore,
			int oppscore, Socket con) {
		super();
		playername = player;
		this.oppname = oppname;
		this.playerscore = playerscore;
		this.oppscore = oppscore;
		this.con = con;
		try {
			this.out = new PrintWriter(con.getOutputStream(), true);
			// this.in = new BufferedReader(new InputStreamReader(
			// con.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		setLayout(new BorderLayout());
		setAlignmentX(Component.CENTER_ALIGNMENT);
		init();
		setGUI();
	}

	public void init() {
		// JLabels
		score = new JLabel("Score: " + score);
		dance = new JLabel();

		// JPanels
		topPanel = new JPanel(new BorderLayout());
		bottomPanel = new JPanel(new GridLayout(0, 1));
		gifPanel = new JPanel(new BorderLayout());
		countdownTimer = new Timer(1000, new CountdownTimerListener());
		gif1 = new Timer(1120, new GIFTimerListener());
		gif2 = new Timer(2640, new GIFTimerListener());
		gif3 = new Timer(3100, new GIFTimerListener());

		compareScore();
		resultannounce = new JLabel("You are " + result);
		playerscoreannounce = new JLabel("Your score : " + playerscore);
		oppscoreannounce = new JLabel(oppname + "'s score : " + oppscore);
	}

	public void setGUI() {

		// top panel
		add(topPanel, BorderLayout.NORTH);
		topPanel.setBackground(myColor);
		resultannounce.setFont(new Font("Courier", Font.BOLD, 40));
		resultannounce.setForeground(Color.white);
		resultannounce.setHorizontalAlignment(JLabel.CENTER);
		topPanel.add(resultannounce, BorderLayout.CENTER); // add JLabel

		// gifPanel
		add(gifPanel);

		// gifPanel.add(dance, java.awt.BorderLayout.CENTER);
		gifPanel.add(dance);
		dance.setHorizontalAlignment(JLabel.CENTER);
		dance.setVerticalAlignment(JLabel.CENTER);

		// bottomPanel
		add(bottomPanel, BorderLayout.SOUTH);
		bottomPanel.setBackground(myColor);
		playerscoreannounce.setFont(new Font("Courier", Font.BOLD, 20));
		playerscoreannounce.setForeground(Color.white);
		playerscoreannounce.setHorizontalAlignment(JLabel.CENTER);
		oppscoreannounce.setFont(new Font("Courier", Font.BOLD, 20));
		oppscoreannounce.setForeground(Color.white);
		oppscoreannounce.setHorizontalAlignment(JLabel.CENTER);
		bottomPanel.add(playerscoreannounce, BorderLayout.CENTER);
		bottomPanel.add(oppscoreannounce, BorderLayout.AFTER_LAST_LINE);

		// bottomPanel.add(new JLabel("Your score: "+playerscore));
		// bottomPanel.add(new JLabel("Opponent score: "+oppscore));
		// bottomPanel.setForeground(Color.WHITE);

		setBackground(Color.white);
		setVisible(true);
		// countdownTimer.start();

	}

	public void compareScore() {
		if (playerscore > oppscore) {
			result = "the winner!";
			ImageIcon ii = new ImageIcon("media/images/dog.gif");
			dance.setIcon(ii);
			gif1.start();
//			
//			timeRemaining=1120;
			b1 = true;
			winnerscore = playerscore;
		} else if (playerscore == oppscore) {
			result = "tied...";
			ImageIcon ii = new ImageIcon("media/images/dog4.gif");
			dance.setIcon(ii);
			gif3.start();
			b3 = true;
			winnerscore = oppscore;
		} else {
			result = "the loser!";
			ImageIcon ii = new ImageIcon("media/images/dog2.gif");
			dance.setIcon(ii);
			gif2.start();
			b2 = true;
			winnerscore = oppscore;
		}
	}

	class CountdownTimerListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (countdownTimer.isRunning() && timeRemaining >= 0) {
				System.out.println("timer running: " + timeRemaining--);
			} else {
				countdownTimer.stop();
				dance.setIcon(null);
				dance.removeAll();
				dance.setVisible(false);
				gifPanel.removeAll();
				gifPanel.revalidate();
				gifPanel.setVisible(false);
				out.println("\\TO:SERVER resultsDone");
				out.flush();
				removeAll();
				setVisible(false);

				// System.out.println("-----OPP NAME ----: " + Results.oppname);
				// System.out.println("GO TO SCOREBOARD ");
			}
		}
	}

	class GIFTimerListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// if(gif1.isRunning() || gif2.isRunning() || gif3.isRunning())
			// System.out.println("running gif");
			// else{
			// if(!gif1.isRunning())
			// gif1.stop();
			// if(!gif2.isRunning())
			// gif2.stop();
			// if(!gif3.isRunning())
			// gif3.stop();
			// dance.removeAll();
			// dance.revalidate();
			// dance.repaint();
			// out.println("\\TO:SERVER resultsDone");
			// out.flush();
			// removeAll();
			// setVisible(false);
			// System.out.println("******bye1");
			// }
			if ((gif1.isRunning() || gif2.isRunning() || gif3.isRunning())
					&& timeRemaining >= 0)
				timeRemaining--;
			else {
				if (!gif1.isRunning()) {
					gif1.stop();
					dance.removeAll();
					dance.revalidate();
					dance.repaint();
					out.println("\\TO:SERVER resultsDone");
					out.flush();
					out=null;
					removeAll();
					setVisible(false);
					System.out.println("******bye1");
					

				}
				if (!gif2.isRunning()) {
					gif2.stop();
					dance.removeAll();
					dance.revalidate();
					dance.repaint();
					out.println("\\TO:SERVER resultsDone");
					out.flush();
					out=null;
					removeAll();
					setVisible(false);
					System.out.println("******bye2");

				}
				if (!gif3.isRunning()) {
					gif3.stop();
					dance.removeAll();
					dance.revalidate();
					dance.repaint();
					out.println("\\TO:SERVER resultsDone");
					out.flush();
					out=null;
					removeAll();
					setVisible(false);
					System.out.println("******bye3");

				}
//				out.close();
			}
		}
	}

	public static void main(String[] args) {
		// new Results(playername, 20, 30);
	}
}
