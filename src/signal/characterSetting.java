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
			connection = DriverManager.getConnection("jdbc:mysql://localhost/User", "root", "");
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
//			characterSettingScreen();
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
			} else if ((key <= 'A' || key >= 'Z') && (key <= 'a' || key >= 'z')){
				System.out.println("here is not alphbet");
				return;
			} else {
				String keys = key + "";
				username += keys;
			}
		}
		System.out.println(username);
	}

	// Character Setting screen
	int pink = color(255, 182, 193);
	boolean readyOver = false;
	boolean readyClicked = false;
	int buttonValue;
	int lightValue;
	double axisValue;

	void polygon(float x, float y, float radius, int npoints, float side) { // 多邊形函數
		float angle = TWO_PI / npoints;
		beginShape();
		for (float a = 0; a < TWO_PI; a += angle) {
			float sx = x + cos(a) * radius * side;
			float sy = y + sin(a) * radius;
			vertex(sx, sy);
		}
		endShape(CLOSE);
	}

	float detectButton = 0;
	float detectLight = 0;
	float detectAxis = 0;
	double axis1 = 0;
	double axis2 = 0;
	double axis3 = 0;
	
	double side = 0;
	int shape = 0;
	double colorR = 0;
	double colorG = 0;
	double colorB = 0;
	
	
	public void characterSettingScreen() {
		update(mouseX, mouseY);
		background(pink);
		fill(white);
		textFont(createFont("Marker Felt", 60));
		text("Settings", 300, 100);
		textFont(createFont("Marker Felt", 30));
		try {
			// sensor value
			// 旋轉感測器 大小
			buttonValue = ifkit.getSensorValue(0);
			// 感光感測器 形狀
			lightValue = ifkit.getSensorValue(2);
			// 三軸感測器 顏色
			axisValue = spatial.getAcceleration(0);
			axis1 = spatial.getAcceleration(0);
			axis2 = spatial.getAcceleration(1);
			axis3 = spatial.getAcceleration(2);
			strokeWeight(3);
			
			//System.out.println();
			// 偵測是否感應器有沒有感應到東西
			if (buttonValue == detectButton) {
				fill(255, 255, 255);
				stroke(lightBlue);
				detectButton = buttonValue;
			} else {
				fill(20, 200, 150);
				stroke(20, 200, 150);
				detectButton = buttonValue;
			}
			ellipse(155, 200, 40, 40);
			text("Size", 130, 300);
			noFill();
			rect(70,150,170,180);

			if (lightValue <= detectLight + 2 && lightValue >= detectLight - 2) {
				fill(255, 255, 255);
				stroke(lightBlue);
				detectLight = lightValue;
			} else {
				fill(200, 20, 170);
				stroke(200, 20, 170);
				detectLight = lightValue;
			}
			ellipse(400, 200, 40, 40);
			text("Shape", 370, 300);
			noFill();
			rect(315,150,170,180);

			if ((float) axis1 <= detectAxis + 0.03 && (float) axis1 >= detectAxis - 0.03) {
				fill(255, 255, 255);
				stroke(lightBlue);
				detectAxis = (float) axis1;
			} else {
				fill(100, 20, 200);
				stroke(100, 20, 200);
				detectAxis = (float) axis1;
			}
			ellipse(645, 200, 40, 40);
			text("Color", 620, 300);
			noFill();
			rect(560,150,170,180);
			
			// 控制角色的長相
			// 大小
			side = map(buttonValue, 0, 999, 1, 3);
			// 形狀
			shape = (int) map(lightValue, 0, 900, 0, 20);
			// 顏色
			colorR = map((float) axis1, -1, 1, 0, 255);
			colorG = map((float) axis2, -1, 1, 0, 255);
			colorB = map((float) axis3, -1, 1, 0, 255);
			fill((float)colorR, (float)colorG, (float)colorB);
			// 角色顯示
			if (shape < 3) {
				ellipse(400, 350, (float)side * 50, 100);
			} else {
				polygon(400, 350, 50, shape, (float)side);
//				fill(white);
//				text("^", 430, 360);
//				text("^", 370, 360);
//				text("_", 400, 370);
				fill(white);
				noStroke();
				ellipse(430, 350, 20, 20);
				ellipse(370, 350, 20, 20);
				fill(0,0,0);
				ellipse(430, 350, 10, 10);
				ellipse(370, 350, 10, 10);
			}

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
		background(pink);
//		System.out.println("start game");
		fill(white);
		text("開始遊戲",300,300);
	}

	public void gameOverScreen() {

	}

	// test whether mouse is on the particular area
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

	// test whether mouse is pressed on the particular area
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
				stmt = connection.prepareStatement("Insert into user values (null,?,?,?,?,?,?,?)");
				System.out.println(username);
				stmt.setString(1, username);// name
				stmt.setString(2, null);// score
				stmt.setDouble(3, shape);// shape
				stmt.setDouble(4, side);// size
				stmt.setDouble(5, colorR);// color
				stmt.setDouble(6, colorG);
				stmt.setDouble(7, colorB);
				int result = stmt.executeUpdate();
				System.out.println(result);
				gameScreen = 2;
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}

}
