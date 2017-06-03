package signal;

import com.phidgets.InterfaceKitPhidget;
import com.phidgets.PhidgetException;
import com.phidgets.SpatialPhidget;

import processing.core.PApplet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class characterSetting extends PApplet {
	private InterfaceKitPhidget ifkit;
	private SpatialPhidget spatial;
	static Connection connection = null;

	public static void main(String[] args) {
		PApplet.main(characterSetting.class.getName());
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// create table in advance and change your own password
			connection = DriverManager.getConnection("jdbc:mysql://localhost/signal", "root", "123698745+");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	int screenX = 800;
	int screenY = 600;
	int gameScreen = 0;

	public void settings() {
		size(screenX, screenY);
		try {
			ifkit = new InterfaceKitPhidget();
			ifkit.openAny();
			ifkit.waitForAttachment();
			spatial = new SpatialPhidget();
			spatial.openAny();
			spatial.waitForAttachment();
		} catch (PhidgetException e) {
			e.printStackTrace();
		}
	}

	public void draw() {
		if (gameScreen == 0) {
			initScreen();
		} else if (gameScreen == 1) {
			characterSettingScreen();
		} else if (gameScreen == 2) {
			gameScreen();
		} else if (gameScreen == 3) {
			gameOverScreen();
		}
	}

	// initial screen
	boolean rectOver = false;
	boolean textBoxOver = false;
	boolean textBoxClicked = false;
	int rectXSize = 200;
	int rectYSize = 35;
	int rectX = screenX / 2 - rectXSize / 2;
	int rectY = 500;

	int lightBlue = color(160, 215, 243);// light blue
	int white = color(255);

	public void initScreen() {
		update(mouseX, mouseY);

		// setting background color
		float backgroundBlock = (float) (screenY * 0.7);
		fill(lightBlue);
		rect(0, 0, screenX, backgroundBlock);
		fill(white);
		rect(0, backgroundBlock, screenX, screenY - backgroundBlock);
		textFont(createFont("Marker Felt", 60));
		text("Signal", 330, 200);

		textFont(createFont("Marker Felt", 20));
		float cw = textWidth(username);

		// text box
		fill(lightBlue);
		text("USERNAME", rectX, rectY - 25);
		fill(white);
		stroke(lightBlue);
		if (cw < rectXSize - textWidth("USERNAME") - 4) {
			rect(rectX + textWidth("USERNAME") + 4, rectY - 50, rectXSize - textWidth("USERNAME") - 4, rectYSize, 3, 3,
					3, 3);
		} else {
			rect(rectX + textWidth("USERNAME") + 4, rectY - 50, cw + 8, rectYSize, 3, 3, 3, 3);
		}

		// text box indicator
		if (textBoxClicked) {
			stroke(color(0));
			line(rectX + textWidth("USERNAME") + 8 + cw, rectY - 45, rectX + textWidth("USERNAME") + 8 + cw,
					rectY - 20);
		} else {
			stroke(white);
			line(rectX + textWidth("USERNAME") + 8 + cw, rectY - 45, rectX + textWidth("USERNAME") + 8 + cw,
					rectY - 20);
		}

		// print username
		fill(color(100, 100, 100));
		text(username, rectX + textWidth("USERNAME") + 8, rectY - 25);

		// button color
		if (rectOver) {
			fill(white);
			stroke(lightBlue);
			rect(rectX, rectY, rectXSize, rectYSize, 3, 3, 3, 3);
			fill(lightBlue);
			textFont(createFont("Marker Felt", 30));
			text("Start", rectX + (rectXSize - textWidth("Start")) / 2, rectY + 29);
		} else {
			fill(lightBlue);
			stroke(white);
			rect(rectX, rectY, rectXSize, rectYSize, 3, 3, 3, 3);
			fill(white);
			textFont(createFont("Marker Felt", 30));
			text("Start", rectX + (rectXSize - textWidth("Start")) / 2, rectY + 29);
		}

	}

	String username = "";

	public void keyPressed() {
		if (textBoxClicked) {
			if (key == BACKSPACE) {
				username = username.substring(0, max(0, username.length() - 1));
			} else {
				String keys = key + "";
				username += keys;
			}
		}
	}

	//Character Setting screen
	int pink = color(255, 182, 193);
	boolean readyOver = false;
	boolean readyClicked = false;
	int buttonValue;
	int lightValue;
	double axisValue;

	public void characterSettingScreen() {
		update(mouseX, mouseY);
		background(pink);
		fill(white);
		textFont(createFont("Marker Felt", 60));
		text("Settings", 300, 100);
		textFont(createFont("Marker Felt", 30));
		text("Color", 200, 150);
		text("Shape", 200, 200);
		text("Size", 200, 250);
		try {
			// sensor value
			buttonValue = ifkit.getSensorValue(0);
			lightValue = ifkit.getSensorValue(2);
			axisValue = spatial.getAcceleration(0);
			double axis2 = spatial.getAcceleration(1);
			double axis3 = spatial.getAcceleration(2);

			text(buttonValue, 300, 150);
			text(lightValue, 300, 200);
			text("not available", 300, 250);
			// System.out.println(axis + " " + axis2 + " " + axis3);
			// float v = map(value, 30, 700, 0, 255);
			// fill(v, 0, 0);
			// ellipse(150, 200, v, v);
		} catch (PhidgetException e) {
			e.printStackTrace();
		}
		if (readyOver) {
			fill(white);
			stroke(pink);
			rect(rectX, rectY, rectXSize, rectYSize, 3, 3, 3, 3);
			fill(pink);
			textFont(createFont("Marker Felt", 30));
			text("Ready", rectX + (rectXSize - textWidth("Start")) / 2, rectY + 29);
		} else {
			fill(pink);
			stroke(white);
			rect(rectX, rectY, rectXSize, rectYSize, 3, 3, 3, 3);
			fill(white);
			textFont(createFont("Marker Felt", 30));
			text("Ready", rectX + (rectXSize - textWidth("Start")) / 2, rectY + 29);
		}
	}

	public void gameScreen() {

	}

	public void gameOverScreen() {

	}

	//test whether mouse is on the particular area
	private void update(int x, int y) {
		if (gameScreen == 0 && overRect(rectX, rectY, rectXSize, rectYSize)) {
			rectOver = true;
		} else {
			rectOver = false;
		}
		if (gameScreen == 0 && overRect(rectX, rectY - 50, rectXSize, rectYSize)) {
			textBoxOver = true;
		} else {
			textBoxOver = false;
		}
		if (gameScreen == 1 && overRect(rectX, rectY, rectXSize, rectYSize)) {
			readyOver = true;
		} else {
			readyOver = false;
		}
	}

	private boolean overRect(int x, int y, int width, int height) {
		if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
			return true;
		} else {
			return false;
		}
	}

	//test whether mouse is pressed on the particular area
	public void mousePressed() {
		// initial screen
		if (gameScreen == 0 && rectOver) {
			gameScreen = 1;
		}
		if (textBoxOver) {
			textBoxClicked = true;
		} else {
			textBoxClicked = false;
		}
		// character setting screen
		if (gameScreen == 1 && readyOver) {
			PreparedStatement stmt;
			try {
				stmt = connection.prepareStatement("Insert into user values (null,?,?,?,?,?)");
				stmt.setString(1, username);// name
				stmt.setString(2, null);// score
				stmt.setDouble(3, buttonValue);// shape
				stmt.setDouble(4, axisValue);// size
				stmt.setDouble(5, lightValue);// color
				int result = stmt.executeUpdate();
				System.out.println(result);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}

}
