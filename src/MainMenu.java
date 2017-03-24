import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;

public class MainMenu extends JPanel{
	// final static String FRAME_TITLE = "Dance DupMe";
	static String playerName, firstPlayer;
	JPanel menu, buttons;
	JLabel welcome;
	JButton tutorial, battle, suddenDeath, scoreboard;
	Color myColor = new Color(153, 0, 76);

	static Battle b;
	// boolean readytoplay=false;

	boolean isPlaying;

	Socket con;
	PrintWriter out;
	BufferedReader in;
	//Executor x=Executors.newSingleThreadExecutor();
	
	Executor x;
	
	/*
	 * public MainMenu() { super(); //
	 * setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); setSize(900, 400);
	 * setLayout(new BorderLayout()); //playerName =
	 * JOptionPane.showInputDialog("What is your name?"); init(); setGUI(); }
	 */

	public MainMenu(String player, Socket con, Executor x) {
		super();
		this.con = con;
		try {
			this.out = new PrintWriter(con.getOutputStream(), true);
			this.in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.x=x;
		playerName = player;
		//this.firstPlayer=firstPlayer;
		// setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1100, 400);
		setLayout(new BorderLayout());
		init();
		setGUI();
	}

	public MainMenu(String player) {
		super();
		playerName = player;
		// setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1100, 400);
		setLayout(new BorderLayout());
		init();
		setGUI();
	}

	// initiate variables
	public void init() {
		menu = new JPanel(new BorderLayout());
		welcome = new JLabel("Welcome, " + playerName + "!");
		buttons = new JPanel(new GridLayout(0, 1));
		tutorial = new JButton("Tutorial");
		battle = new JButton("Battle");
		suddenDeath = new JButton("Sudden Death");
		scoreboard = new JButton("View Scoreboard");

	}

	public static String getPlayerName() {
		return playerName;
	}

	// set all GUI
	public void setGUI() {
		menu.setBackground(myColor);

		// welcome string (with player's name)
		welcome.setFont(new Font("Didot", Font.BOLD, 25));
		welcome.setForeground(Color.WHITE);
		menu.add(welcome, BorderLayout.NORTH);

		// buttons to navigate
		tutorial.setFont(new Font("Courier", Font.PLAIN, 20));
		battle.setFont(new Font("Courier", Font.PLAIN, 20));
		suddenDeath.setFont(new Font("Courier", Font.PLAIN, 20));
		scoreboard.setFont(new Font("Courier", Font.PLAIN, 20));

		tutorial.addActionListener(action);

		//battle.setEnabled(false);
		battle.addActionListener(action);

		suddenDeath.addActionListener(action);
		scoreboard.addActionListener(action);

		buttons.setBackground(myColor);
		buttons.add(tutorial);
		buttons.add(battle);
//		buttons.add(suddenDeath);
		buttons.add(scoreboard);

		menu.add(buttons, BorderLayout.CENTER);
		this.add(menu);

		// getRootPane().setContentPane(menu);
		// add(menu, BorderLayout.CENTER);
		// setVisible(true);
	}
	
	/*public void processMsg(String msg) {
		if (msg.contains("\\readyToPlay")) {
			battle.setEnabled(true);
			//System.out.println("ready");
		}else {

		}
	}*/

	/*public void run() {
		// TODO Auto-generated method stub
		try {
			String msg = "";
			while (true) { // read message receive from the server
				if ((msg = in.readLine()) != null) {
					processMsg(msg);
					//System.out.println(msg);
				}else{
					//System.out.println("not working");
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
	}*/
	

	private ActionListener action = new ActionListener() { // change the content
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if (e.getSource().equals(tutorial)) {
				System.out.println("go to tutorial");
				
				remove(menu);
			    Tutorial tutorial=new Tutorial(con);
			    tutorial.setSize(900,400);
			    add(tutorial);
			    revalidate();
			    repaint();

			}
			if (e.getSource().equals(battle)) {
				
				remove(menu);
				b = new Battle(playerName, con);
				b.setSize(900, 400);
				Runnable r = b;
				Thread t = new Thread(r);
				x.execute(t);
				add(b);
				out.println("\\TO:CLIENT goToBattle");

				revalidate();
				repaint();

				
			}

			/*if (e.getSource().equals(suddenDeath)) {
				System.out.println("go to sudden death");

				getRootPane().setContentPane(new Results(playerName, 200000,
				50)); invalidate(); validate();

				remove(menu);
				add(new Results(playerName, 200000, 50, con), BorderLayout.WEST);
				revalidate();
				repaint();

			}*/
			if (e.getSource().equals(scoreboard)) {
				System.out.println("go to scoreboard");
				remove(menu);
				try {
					add(new Scoreboard(con,x), BorderLayout.CENTER);
					System.out.println("in try catch");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				revalidate();
				repaint();
			}
		}
	};

	public static void setUIFont(FontUIResource f) {
		Enumeration keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof FontUIResource) {
				FontUIResource orig = (FontUIResource) value;
				Font font = new Font(f.getFontName(), orig.getStyle(), f.getSize());
				UIManager.put(key, new FontUIResource(font));
			}
		}
	}

	public static int[] getPattern() {
		return b.pattern;
	}

	public static void main(String[] args) {
		setUIFont(new FontUIResource(new Font("Courier", Font.PLAIN, 14)));
	}


}
