package signal;

import com.phidgets.InterfaceKitPhidget;
import com.phidgets.PhidgetException;
import com.phidgets.SpatialPhidget;

import ddf.minim.Minim;
import javafx.scene.control.TableRow;
import processing.core.PApplet;
import processing.core.PImage;
import processing.data.Table;
import sun.audio.AudioPlayer;

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
	ResultSet rs;
	
	ddf.minim.AudioPlayer hit;

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
	boolean setGameStatus = false;
	boolean setGameOver = false;
	public void draw() {
		if (gameScreen == 0) {
			initScreen();
		} else if (gameScreen == 1) {
			characterSettingScreen();
		} else if (gameScreen == 2) {
			
			if(setGameStatus == false){
				setGame();
				setGameStatus = true;
			}
			
			gameScreen();
		} else if (gameScreen == 3) {
			
			if(setGameOver == false){
				setGameOver();
				setGameOver = true;
			}
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
	
	boolean count = true;
	boolean initMusic = false;
	ddf.minim.AudioPlayer player;
	ddf.minim.AudioPlayer player2;
	ddf.minim.AudioPlayer player3;
	
	public void initScreen() {

		// music
		if(initMusic == false){
			Minim minim;
			minim = new Minim(this);
			player = minim.loadFile("music/gameInit.mp3");
			player.loop();
			initMusic = true;
		}
		
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
			} else if ((key < 'A' || key > 'Z') && (key < 'a' || key > 'z')) {
				System.out.println("here is not alphbet");
				return;
			} else {
				String keys = key + "";
				username += keys;
			}
		}
		System.out.println(username);
		
		//move
		  if((gameScreen==2)&&(key==CODED)){
		    switch(keyCode){
		      case LEFT:
		        leftPressed=true;
		        break;  
		      case RIGHT:
		        rightPressed=true;
		        break;  
		    }
		  }
	}
	public void keyReleased(){
	    if((gameScreen==2)&&(key==CODED)){
	    switch(keyCode){
	      case LEFT:
	        leftPressed=false;
	        break;  
	      case RIGHT:
	        rightPressed=false;
	        break;  
	    }
	  }
	}

	// Character Setting screen
	int pink = color(255, 182, 193);
	boolean readyOver = false;
	boolean readyClicked = false;
	int buttonValue;
	int lightValue;
	double axisValue;
	int slideValue;
	
    //多邊形
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

	double size = 0;
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

			// System.out.println();
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
			rect(70, 150, 170, 180);

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
			rect(315, 150, 170, 180);

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
			rect(560, 150, 170, 180);

			// 控制角色的長相
			// 大小
			size = map(buttonValue, 0, 999, 1, 3);
			// 形狀
			shape = (int) map(lightValue, 0, 900, 2, 20);
			// 顏色
			colorR = map((float) axis1, -1, 1, 0, 255);
			colorG = map((float) axis2, -1, 1, 0, 255);
			colorB = map((float) axis3, -1, 1, 0, 255);
			fill((float) colorR, (float) colorG, (float) colorB);
			// 角色顯示
//			if (shape < 3) {
//				ellipse(400, 350, (float) size * 50, 100);
//			} else {
//				polygon(400, 350, 50, shape, (float) size);
//				// fill(white);
//				// text("^", 430, 360);
//				// text("^", 370, 360);
//				// text("_", 400, 370);
//				fill(white);
//				noStroke();
//				ellipse(430, 350, 20, 20);
//				ellipse(370, 350, 20, 20);
//				fill(0, 0, 0);
//				ellipse(430, 350, 10, 10);
//				ellipse(370, 350, 10, 10);
//			}

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

	// img
	PImage enemy, enemy2, enemy3, enemy4, hp;
	PImage over, re, again;

	// float
	float blood, chaX, chaY, enemyX, enemyY;
	float enemy2X, enemy2Y, enemy3X, enemy3Y, enemy4X, enemy4Y;

	// boolean
	boolean leftPressed = false;
	boolean rightPressed = false;

	// mode
	int gameState, scoreState;

	// life
	int life, score;

	public void setGame(){
		background(pink);
		// System.out.println("start game");
		
		//hp
		  hp=loadImage("img/hp.png");
		  blood=200;
		  life=100;
		  
		  //score
		  score = 0;
		  
		  //enemy
		  enemy=loadImage("img/enemy.png");
		  enemy2=loadImage("img/enemy2.png");
		  enemy3=loadImage("img/enemy3.png");
		  enemy4=loadImage("img/enemy4.png");
		  enemyX=random(0,160);
		  enemy2X=random(210,390);
		  enemy3X=random(440,550);
		  enemy4X=random(600,740);
		  enemyY=0;
		  enemy2Y=-200;
		  enemy3Y=-100;
		  enemy4Y=-300;
		  
		  //character
		  chaX=420;
		  chaY=520;
		  
		  try{
		  Statement stmt = connection.createStatement();
		  rs = stmt.executeQuery("select colorR, colorG, colorB, shape, size from user");
		  
		  }catch (Exception e) {
				e.printStackTrace();
			}
	}
	boolean gameMusic = false;
	public void gameScreen() {
		// music
		player.pause();
		if(gameMusic == false){
			Minim minim;
			minim = new Minim(this);
			player2 = minim.loadFile("music/gameStart.mp3");
			player2.loop();
			gameMusic = true;
		}

		//bg
		  background(140,208,146);
		  
		  
		//score
		  textFont(createFont("Marker Felt", 32));
		  fill(116,75,18);
		  text("Score : " + score/60 ,42,108);

		  score+=1;
		  if (score>1200){
		    scoreState=1;}
		  
		  //hp
		  blood=2*life;
		  fill(226,36,87);
		  stroke(226,36,87);
		  rect(27,40,blood,20);
		  image(hp,20,35); 
		  
		  //enemy
		  image(enemy,enemyX,enemyY);
		  enemyY+=3;
		  if(enemyY>=600){
		        enemyX=floor(random(0,100));
		        enemyY=0;
		      }
		  image(enemy2,enemy2X,enemy2Y);
		  enemy2Y+=4;
		  if(enemy2Y>=600){
		        enemy2X=floor(random(200,300));
		        enemy2Y=-50;
		      }
		  image(enemy3,enemy3X,enemy3Y);
		  enemy3Y+=3;
		  if(enemy3Y>=600){
		        enemy3X=floor(random(400,500));
		        enemy3Y=-210;
		      }
		  image(enemy4,enemy4X,enemy4Y);
		  enemy4Y+=4;
		  if(enemy4Y>=600){
		        enemy4X=floor(random(600,700));
		        enemy4Y=-100;
		      }
		    

		  //character
		  
		  try {
			     fill((float) colorR, (float) colorG, (float) colorB);
					// 角色顯示
					if (shape < 3) {
						noStroke();
						ellipse(chaX, chaY, (float) size * 50, 100);
						fill(white);
						noStroke();
						ellipse(chaX+20, chaY, 20, 20);
						ellipse(chaX-20, chaY, 20, 20);
						fill(0, 0, 0);
						ellipse(chaX+20, chaY, 10, 10);
						ellipse(chaX-20, chaY, 10, 10);
					} else {
						noStroke();
						polygon(chaX, chaY, 50, shape, (float) size);
						fill(white);
						noStroke();
						ellipse(chaX+20, chaY, 20, 20);
						ellipse(chaX-20, chaY, 20, 20);
						fill(0, 0, 0);
						ellipse(chaX+20, chaY, 10, 10);
						ellipse(chaX-20, chaY, 10, 10);
					}
			} catch (Exception e) {
			           e.printStackTrace();
			     }
		  
		  //move
		  try{
			  slideValue = ifkit.getSensorValue(7);
			  chaX = map(slideValue, 0, 995, 750, 50);
			  
		  } catch (PhidgetException e) {
				e.printStackTrace();
			}
		  
		  //boundary detection
		    if(chaX>750){
		      chaX=750;
		    }
		    if(chaX<50){
		      chaX=50;
		    }
		    
		  //hit detection
		  if(chaX >= enemyX-enemy.width && chaX <= enemyX+enemy.width){
		      if(chaY >= enemyY-enemy.height &&  chaY <= enemyY+enemy.height){
		        life -= 20;
		        enemyX = floor(random(0,100));
		        enemyY = -200;
		        ddf.minim.AudioPlayer hit;
				Minim minim;
				minim = new Minim(this);
				hit = minim.loadFile("music/hit.mp3");
		        hit.play();
		      }
		    }
		      
		  if(chaX >= enemy2X-enemy2.width && chaX <= enemy2X+enemy2.width){
		      if(chaY >= enemy2Y-enemy2.height &&  chaY <= enemy2Y+enemy2.height){
		        life -= 20;
		        enemy2X = floor(random(200,300));
		        enemy2Y = -200;
		        
		        ddf.minim.AudioPlayer hit;
				Minim minim;
				minim = new Minim(this);
				hit = minim.loadFile("music/hit.mp3");
		        hit.play();
		      }
		    }
		    
		  if(chaX >= enemy3X-enemy3.width && chaX <= enemy3X+enemy3.width){
		      if(chaY >= enemy3Y-enemy3.height &&  chaY <= enemy3Y+enemy3.height){
		        life -= 20;
		        enemy3X = floor(random(400,500));
		        enemy3Y = -200;
		        
		        ddf.minim.AudioPlayer hit;
				Minim minim;
				minim = new Minim(this);
				hit = minim.loadFile("music/hit.mp3");
		        hit.play();
		      }
		    }
		    
		    if(chaX >= enemy4X-enemy4.width && chaX <= enemy4X+enemy4.width){
		      if(chaY >= enemy4Y-enemy4.height &&  chaY <= enemy4Y+enemy4.height){
		        life -= 20;
		        enemy4X = floor(random(600,700));
		        enemy4Y = -200;
		        
		        ddf.minim.AudioPlayer hit;
				Minim minim;
				minim = new Minim(this);
				hit = minim.loadFile("music/hit.mp3");
		        hit.play();
		      }
		    }
		    
		    switch (scoreState){
		    case 1:
		    enemyY+=1;
		    enemy2Y+=1;
		    enemy3Y+=1;
		    enemy4Y+=1;
		    
		    if(score>2400){
		    scoreState=2;
		    enemyY+=1;
		    enemy2Y+=1;
		    enemy3Y+=1;
		    enemy4Y+=1;
		    }
		    break;
		    
		    case 2:
		    enemyY+=1;
		    enemy2Y+=1;
		    enemy3Y+=1;
		    enemy4Y+=1;
		    
		    if(score>3600){
		    scoreState=3;
		    enemyY+=2;
		    enemy2Y+=2;
		    enemy3Y+=2;
		    enemy4Y+=2;
		    }
		    break;
		    
		    case 3:
		    enemyY+=2;
		    enemy2Y+=2;
		    enemy3Y+=2;
		    enemy4Y+=2;
		    
		    if(score>6000){
		    enemyY+=20;
		    enemy2Y+=20;
		    enemy3Y+=20;
		    enemy4Y+=20;
		    }
		    break;
		  }
		  int nowId = 0;
		  if(life == 0){
			  gameScreen=3;
			  count = true;
			  num = 1;
			  try{
				  Statement stmt = connection.createStatement();
				  ResultSet rs = stmt.executeQuery("SELECT id FROM user WHERE id=( SELECT max(id) FROM user )");
				  rs.next();
				  nowId = rs.getInt("id");
				  }catch (Exception e) {
						e.printStackTrace();
				}
			//存score
			  try{
				  PreparedStatement stmt = connection.prepareStatement("Update user set score=? where id=? ");// score
				  System.out.println(score/60);
				  stmt.setInt(1, score/60);
				  stmt.setInt(2, nowId);
				  int result = stmt.executeUpdate();
					System.out.println(result);
			  }catch (SQLException e1) {
					e1.printStackTrace();
				}
		  }
	}
	
	private void setGameOver() {
		over = loadImage("img/over.png");
		re = loadImage("img/re.png");
		again = loadImage("img/again.png");
		
	}
	int num = 1;
	public void gameOverScreen() {
		if(count == true){
			image(over,0,0);
			count=false;
		}
		if(mouseX>400 && mouseX<600 && mouseY >480 && mouseY<580){
//			fill(white);
			noFill();
			stroke(255);
			  rect(378, 495, 210, 90);
		      if(mousePressed){
		    	setGameStatus = false;
		        gameScreen = 2;
		        blood=200;
				life=100;
				
				PreparedStatement stmt;
				try {
					stmt = connection.prepareStatement("Insert into user values (null,?,?,?,?,?,?,?)");
					System.out.println(username);
					stmt.setString(1, username);// name
					stmt.setString(2, null);// score
					stmt.setDouble(3, shape);// shape
					stmt.setDouble(4, size);// size
					stmt.setDouble(5, colorR);// color
					stmt.setDouble(6, colorG);
					stmt.setDouble(7, colorB);
					int result = stmt.executeUpdate();
					System.out.println(result);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				
		      }
		}else{
			noFill();
			stroke(0);
			rect(378, 495, 210, 90);
		}
		
		if(mouseX>610 && mouseX<750 && mouseY >480 && mouseY<580){
			noFill();
			stroke(0);
			rect(610, 495, 170, 90);
			if(mousePressed){
				setGameStatus = false;
				gameScreen = 1;
				blood=200;
				life=100;
				// readyOver = true;
		    }
		}else{
			noFill();
			stroke(255);
			rect(610, 495, 170, 90);
		}
		
		  try{
			  Statement stmt = connection.createStatement();
			  ResultSet rs = stmt.executeQuery("SELECT name,score FROM user order by score DESC Limit 10 ");
			  fill(white);
			  textFont(createFont("Marker Felt", 32));
			  while(rs.next() && num < 10){
				  System.out.println( rs.getString("name"));
				  System.out.println( rs.getString("score"));
				  text(num,50,100+40*num);
				  text(rs.getString("name"),150,100+40*num);
				  text(rs.getString("score"),300,100+40*num);
				  System.out.println(num);
				  num++;
				  
			  }
			  }catch (Exception e) {
					e.printStackTrace();
			}
		
		  textFont(createFont("Marker Felt", 80));
		  fill(240);
		  text("Score : " + score/60 ,380,350);
		  
		  
//		  if(mouseX>450 && mouseX<600 && mouseY >450 && mouseY<550){
//			  image(again,0,0);
//		      if(mousePressed){
//		    	setGameStatus = false;
//		        gameScreen = 2;
//		        blood=200;
//				life=100;
//				
//		      }
//		     else{
//		     textFont(createFont("Marker Felt", 100));
//		     fill(240);
//		     text("Score : " + score/60 ,350,420);
//		     
//		     }   
//		  }   
//		      if(mouseX>650 && mouseX<750 && mouseY >450 && mouseY<550){
//		      if(mousePressed){
//		        gameScreen = 1;
//		        readyOver = true;
//		        
//		      }
//		     else{
//		     image(re,0,0);
//		     textFont(createFont("Marker Felt", 100));
//		     fill(240);
//		     text("Score : " + score/60 ,350,420);
//		     
//		     }    
//		   }
		
		
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
	boolean firstMusic = false;
	// test whether mouse is pressed on the particular area
	public void mousePressed() {
		// initial screen
		if (gameScreen == 0 && rectOver) {
			gameScreen = 1;
			firstMusic = true;
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
				stmt.setDouble(4, size);// size
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
