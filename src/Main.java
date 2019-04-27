import javax.swing.*;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.text.DecimalFormat;

public class Main extends Applet implements Runnable,MouseListener, MouseWheelListener {
    private double factor = 3;
    private int CLOCK_SIZE = 12;
    private int forward = 1;
    private int color = 0;

    private DecimalFormat printNumber = new DecimalFormat("#0.000");

    private boolean mouseEntered = false;

    private Thread runner = null;

    private Graphics bufferGraphics;
    private Image offScreen;

    private Font font = new Font("Serif", Font.PLAIN, 20);
    private FontMetrics f_metrics = getFontMetrics(font);

    private String playState = "Paused";

    private long nextSecond = System.currentTimeMillis() + 1000;
    private int frameInLastSecond = 0;
    private int frameInCurrentSecond = 0;


    public void init() {

        setVisible(true);
        setSize(800, 800);
        setBackground(Color.GRAY);
        addMouseListener(this);
        addMouseWheelListener(this);
    }

    public void paint(Graphics g)
    {
        super.paint(g);

        if (playState.equals("Playing"))
            factor += forward * .001;

        double[] numbers = new double[CLOCK_SIZE];

        for (int i = 0; i < CLOCK_SIZE; i++) {
            numbers[i] = factor * i;

            numbers[i] = numbers[i] % CLOCK_SIZE;
        }


        for (int i = 0; i < CLOCK_SIZE; i++)
        {
            switch (color)
            {
                default:
                    g.setColor(Color.RED);
                    break;
                case 1:
                    g.setColor(Color.BLUE);
                    break;
                case 2:
                    g.setColor(Color.ORANGE);
                    break;
                case 3:
                    g.setColor(Color.YELLOW);
                    break;
                case 4:
                    g.setColor(Color.GREEN);
                    break;
                case 5:
                    g.setColor(Color.WHITE);
                    break;
                case 6:
                    g.setColor(Color.PINK);
                    break;
                case 7:
                    g.setColor(Color.CYAN);
                    break;

            }


            double PI = Math.PI;
            double x1 = 400 - 350 * Math.cos(2 * PI * i / CLOCK_SIZE);
            double y1 = 400 - 350 * Math.sin(2 * PI * i / CLOCK_SIZE);
            double x2 = 400 - 350 * Math.cos(2 * PI * numbers[i] / CLOCK_SIZE);
            double y2 = 400 - 350 * Math.sin(2 * PI * numbers[i] / CLOCK_SIZE);

            g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
        }
        //Calculate frame rate
        long currentTime = System.currentTimeMillis();
        if (currentTime > nextSecond) {
            nextSecond += 1000;
            frameInLastSecond = frameInCurrentSecond;
            frameInCurrentSecond = 0;
        }


    frameInCurrentSecond++;

        g.setFont(font);
        g.setColor(Color.WHITE);

    String frameRate = "Frame Rate (fps): " + frameInLastSecond;

        g.drawString(frameRate,2,22);

    String fact = "Factor: " + printNumber.format(factor);


        g.drawString("Modulo: "+CLOCK_SIZE,696-(f_metrics.charWidth('0'))*(int)(Math.log10(Math.abs(factor))+2),773);
        g.drawString(fact,696-(f_metrics.charWidth('0'))*(int)(Math.log10(Math.abs(factor))+2),798);

        if(forward< 0)
    {
        g.drawString("Reversed", 696 - (f_metrics.charWidth('0')) * (int) (Math.log10(Math.abs(factor)) + 2), 748);
        g.drawString(playState, 696 - (f_metrics.charWidth('0')) * (int) (Math.log10(Math.abs(factor)) + 2), 723);
    }
    else
    {
        g.drawString(playState, 696 - (f_metrics.charWidth('0')) * (int) (Math.log10(Math.abs(factor)) + 2), 748);
    }

        g.drawString("By: Michael McGruder",2,798);


    }

    @Override
    public void update(Graphics g)
    {
        if(offScreen == null){
            offScreen = createImage(this.getSize().width, this.getSize().height);
            bufferGraphics = offScreen.getGraphics();
        }

        bufferGraphics.setColor(getBackground());
        bufferGraphics.fillRect(0, 0, this.getSize().width, this.getSize().height);

        bufferGraphics.setColor(getForeground());

        paint(bufferGraphics);

        g.drawImage(offScreen, 0, 0, this);
    }

    public void start()
    {

    }


    public void run()
    {
        while ( runner != null) {
            repaint();
            try {

                Thread.sleep( 1);

            } catch ( InterruptedException e ) {

                // do nothing

            }
        }
    }
    public void stop()
    {
        runner = null;
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(mouseEntered) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                if (runner != null) {
                    runner = null;
                    playState = "Paused";
                    repaint();
                } else {
                    runner = new Thread(this);
                    runner.start();
                    playState = "Playing";
                    repaint();
                }
            }else if (SwingUtilities.isRightMouseButton(e)){
                forward *= -1;
                repaint();
            }else{
                color++;
                if(color == 8){
                    color = 0;
                }
                repaint();
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        mouseEntered = true;
    }

    @Override
    public void mouseExited(MouseEvent e) {
        mouseEntered = false;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        double roll = e.getPreciseWheelRotation();

        if(mouseEntered && playState.equals("Paused"))
        {
            if(roll < 0  && e.isControlDown())
            {
                if(Math.abs(roll) <= 1)
                {
                    factor += .001;
                }
                else if(Math.abs(roll) == 2)
                {
                    factor += .01;
                }
                else
                {
                    factor += .1;
                }
                repaint();
            }
            else if(roll > 0 && e.isControlDown())
            {
                if(Math.abs(roll) <= 1)
                {
                    factor -= .001;
                }
                else if(Math.abs(roll) == 2)
                {
                    factor -= .01;
                }
                else
                {
                    factor -= .1;
                }
                repaint();
            }else if (roll < 0){
                if(Math.abs(roll) <= 1)
                {
                        CLOCK_SIZE += 1;
                }
                else if(Math.abs(roll) == 2)
                {
                        CLOCK_SIZE += 10;
                }
                else
                {
                        CLOCK_SIZE += 100;
                }
                repaint();
            }else{
                if(Math.abs(roll) <= 1)
                {
                    if(CLOCK_SIZE -1 < 2){
                        CLOCK_SIZE = 1;
                    }else {
                        CLOCK_SIZE -= 1;
                    }
                }
                else if(Math.abs(roll) == 2)
                {
                    if(CLOCK_SIZE -10 < 2){
                        CLOCK_SIZE = 1;
                    }else {
                        CLOCK_SIZE -= 10;
                    }
                }
                else
                {
                    if(CLOCK_SIZE -100 < 2){
                        CLOCK_SIZE = 1;
                    }else {
                        CLOCK_SIZE -= 100;
                    }
                }
                repaint();
            }
        }
        else if(mouseEntered && playState.equals("Playing"))
        {
            if(roll < 0  && e.isControlDown())
            {
                if(Math.abs(roll) <= 1)
                {
                    factor += 1;
                }
                else if(Math.abs(roll) == 2)
                {
                    factor += 10;
                }
                else
                {
                    factor += 100;
                }
                repaint();
            }
            else if(roll > 0 && e.isControlDown())
            {
                if(Math.abs(roll) == 1)
                {
                    factor -= 1;
                }
                else if(Math.abs(roll) == 2)
                {
                    factor -= 10;
                }
                else
                {
                    factor -= 100;
                }
                repaint();
            }
            else if (roll < 0)
            {
                if(Math.abs(roll) == 1)
                {
                    CLOCK_SIZE += 1;
                }
                else if(Math.abs(roll) == 2)
                {
                    CLOCK_SIZE += 10;
                }
                else
                {
                    CLOCK_SIZE += 100;
                }
                repaint();
            }
            else
            {
                if ( Math.abs(roll) == 1) {
                    if (CLOCK_SIZE -1 < 2) {
                        CLOCK_SIZE = 1;
                    } else {
                        CLOCK_SIZE -= 1;
                    }
                } else if (Math.abs(roll) == 2) {
                    if (CLOCK_SIZE - 10 < 2) {
                        CLOCK_SIZE = 1;
                    } else {
                        CLOCK_SIZE -= 10;
                    }
                } else {
                    if (CLOCK_SIZE - 100 < 2) {
                        CLOCK_SIZE = 1;
                    } else {
                        CLOCK_SIZE -= 100;
                    }
                }
                repaint();
            }

        }
        else
        {
            getParent().dispatchEvent(e);
        }

    }

}
