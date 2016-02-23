import java.applet.Applet;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Random;

public class Juego extends Applet implements Runnable, KeyListener {
	int ancho = 1024;
	int alto = 500;
	int score=0;
	Image i;
	Graphics doubleG;
	Juego j;
	Paleta p1;
	Pelota b1;
	Thread t1;
	Paleta2 p2;
	PaletaCPU pcpu;
	Image fondo;
	int fondox=0;
	int fondoy=0;
	URL url;
	protected ObjectOutputStream output;
	protected ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;
	
	
	//configurar y correr el servidor
	
		
	public Juego(){
		try{
		server = new ServerSocket(6789,100);
			
				try{
					waitForConnection();
					setupStreams();
					
		
				}catch(EOFException eofException){
					
				}finally{
					//closeCrap();
				}
			
	}catch(IOException ioException){
		ioException.printStackTrace();
	}
	}
	
	
	//esperando a que se conecten
	private void waitForConnection()throws IOException{
		System.out.print("esperando al cliente\n");
		connection= server.accept();
		System.out.print("\nCliente encontrado");
		
	}
	
	//configurar streams
		private void setupStreams()throws IOException{
			output= new ObjectOutputStream(connection.getOutputStream());
			output.flush();
			input=new ObjectInputStream(connection.getInputStream());
			System.out.print("\nStreams configurados");
		}
				
	@Override
	public void init() {
		System.out.print("\n andando");
		setSize(ancho, alto);
		addKeyListener(this);
		try{
			url=getDocumentBase();
		}catch(Exception e){
			
		}
		
		fondo=getImage(url,"league-of-legends-caballero.jpg");
		Imagenes i=new Imagenes(this);
	}

	@Override
	public void start() {
		System.out.print("\n andando");
		j=new Juego();
		Random r=new Random();
		b1 = new Pelota(r.nextInt(ancho/2), r.nextInt(alto));
		p1 = new Paleta();
		p2=new Paleta2();
		pcpu=new PaletaCPU();
		Thread thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
		
			
		System.out.print("andando Server");
			while (true) {
			
			if(b1.getY()<this.getHeight()&&b1.getY()>0){
			score++;
			}
			b1.update(this);
			p1.update(this, b1);
			p1.MoveRight(this);
			p1.MoveLeft(this);
			p2.update(this, b1);
			p2.MoveRight(this);
			p2.MoveLeft(this);
			//pcpu.update(this, b1);
			
			repaint();
			try {
				Thread.sleep(1000 / 60);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void stop() {

	}

	@Override
	public void destroy() {

		
	}

	public void update(Graphics g) {
		if (i == null) {
			i = createImage(this.getSize().width, this.getSize().height);
			doubleG = i.getGraphics();
		}
		doubleG.setColor(getBackground());
		doubleG.fillRect(0, 0, this.getSize().width, this.getSize().height);

		doubleG.setColor(getForeground());
		paint(doubleG);
		g.drawImage(i, 0, 0, this);
	}

	@Override
	public void paint(Graphics g) {
		
		g.drawImage(fondo, 0, 0,this );
		b1.paint(g);
		p1.paint(g);
		p2.paint(g);
		//pcpu.paint(g);
		Font f=new Font("score",Font.ITALIC,40);
		g.setFont(f);
		g.setColor(Color.BLACK);
		String s=Integer.toString(score);
		g.drawString(s, this.getWidth()-150, this.getHeight()-50);
		if (b1.getY() < 0) {
			Font f1=new Font("score",Font.ITALIC,200);
			g.setFont(f1);
			g.drawString("Ganaste", 0, this.getHeight()/2);
		}
		if (b1.getY() > this.getHeight()) {
			Font f1=new Font("score",Font.ITALIC,200);
			g.setFont(f1);
			g.drawString("Perdiste", 0, this.getHeight()/2);
		}
	}

	@Override


	public void keyPressed(KeyEvent e) {
	    switch(e.getKeyCode()) {
	        case KeyEvent.VK_LEFT: p1.setLeft(true);
	        try {
				output.writeObject("Izquierda P1 ");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        break;
	        case KeyEvent.VK_RIGHT: p1.setRight(true);
	        try {
				output.writeObject("Derecha P1 ");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        break;
	        case KeyEvent.VK_A: p2.setLeft2(true);
	        break;
	        case KeyEvent.VK_D: p2.setRight2(true);
	        break;
	    }

	}

	    public void keyReleased(KeyEvent e) {
		switch(e.getKeyCode()) {
		case KeyEvent.VK_LEFT: p1.setLeft(false);
        break;
        case KeyEvent.VK_RIGHT: p1.setRight(false);
        break;
        case KeyEvent.VK_A: p2.setLeft2(false);
        break;
        case KeyEvent.VK_D: p2.setRight2(false);
        break;
		}

	    }

		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}
		public int getScore() {
			return score;
		}
		public void setScore(int score) {
			this.score = score;
		}	
		
		//cerrar streams
		private void closeCrap(){
			try{
				input.close();
				output.close();
				connection.close();
			}catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
	}


