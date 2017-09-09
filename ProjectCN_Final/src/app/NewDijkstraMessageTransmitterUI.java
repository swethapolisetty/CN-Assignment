package app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import static java.awt.SystemColor.text;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.stage.Screen;
import javax.imageio.ImageIO;
import javax.swing.*;

public class NewDijkstraMessageTransmitterUI extends JFrame {

    int x, y, x2, y2, x3;
    int xred = 1045, xblue = 1045;
    boolean secondtimeflag_red = false;
    boolean tryflagred = false;
    boolean tryflagblue = false;

    ShortestPathCalculator1 redPathObj = new ShortestPathCalculator1();
    LinkedList<Vertex> redpath = redPathObj.calculateShortestRoute();
    List<Edge> redEdges = redPathObj.getEdgesList();

    ShortestPathCalculator1 bluePathObj = new ShortestPathCalculator1();
    LinkedList<Vertex> bluepath = bluePathObj.calculateShortestRoute();
    List<Edge> blueEdges = bluePathObj.getEdgesList();

    JFrame frame = new JFrame();
    JFrame imgFrameAnalog = null, imgFrameAnalog2 = null, imgFrameDigital = null, imgFrameDigital2 = null;
    String Msg1 = JOptionPane.showInputDialog("Please input Message 1 ");
    String Msg2 = JOptionPane.showInputDialog("Please input Message 2 ");

    String PHYMsgCRC = "1011";

    MyDrawPanel drawpanel;
    ButtonGroup btnGroup = new ButtonGroup();

    JRadioButton rbtn_CorrectMsg = new JRadioButton("Correct Message");
    JRadioButton rbtn_CorruptMsg = new JRadioButton("Corrupt Message");
    JRadioButton rbtn_DummyBtn = new JRadioButton("Dummy Message");

    boolean stopFlag = false;
    boolean remflag = true;//flag for remainder 000 ar r2

    boolean redballmoves15 = false;
    boolean blueballmoves15 = false;

    Thread paintThread = null;
    Thread paintThread2 = null;

    // Correct -1 Corrupt-2
    int userMsgType = 0;

    public void setUserMsgType(int value) {
        userMsgType = value;
    }

    public int getUserMessagetype() {
        return userMsgType;
    }

    public String getBinaryCRCMsg1() {
        String binary1 = new BigInteger(Msg1.getBytes()).toString(2);
        String PHYMsg1 = "01010101010101010101010101010101010101010101010101010101101010111000000000000000001000000111101000111100001111101000000000000000001000000010000000111010101011100000100000000000";
//        return PHYMsg1 + binary1 + PHYMsgCRC;
        return binary1 + PHYMsgCRC;
    }

    public String getBinaryCRCMsg2() {
        String binary2 = new BigInteger(Msg2.getBytes()).toString(2);
        String PHYMsg2 = "01010101010101010101010101010101010101010101010101010101101010110111101100011011001010100111101000111100001111111000000000000000001000000010000000111010101011100000100000000000";
        //return PHYMsg2 + binary2 + PHYMsgCRC;
        return binary2 + PHYMsgCRC;
    }

    public String getTotalMessage1() {
        String binary1 = new BigInteger(Msg1.getBytes()).toString(2);
        String PHYMsg1 = "01010101010101010101010101010101010101010101010101010101101010111000000000000000001000000111101000111100001111101000000000000000001000000010000000111010101011100000100000000000";
        return PHYMsg1 + binary1 + PHYMsgCRC;

    }

    public String getTotalMessage2() {
        String binary2 = new BigInteger(Msg2.getBytes()).toString(2);
        String PHYMsg2 = "01010101010101010101010101010101010101010101010101010101101010110111101100011011001010100111101000111100001111111000000000000000001000000010000000111010101011100000100000000000";
        return PHYMsg2 + binary2 + PHYMsgCRC;

    }

    public void go() {
        //Set Initial Ball coordinates

        x = 20;
        y = 105;
        x2 = 20;
        y2 = 40;

        // Load Panel Content & Add to Frame
        JPanel panelButtons = new JPanel();

        //Draw Image in drawPanel
        drawpanel = new MyDrawPanel();

        JButton btnPlay = new JButton("Play");
        JButton btnPause = new JButton("Pause");
        //    JButton btnShowMessage = new JButton("Show Message");
        JButton btnReset = new JButton("Reset");

        panelButtons.setBackground(Color.BLACK);
        panelButtons.add(btnPlay);
        panelButtons.add(btnPause);

        btnGroup.add(rbtn_CorrectMsg);
        btnGroup.add(rbtn_DummyBtn);

        rbtn_DummyBtn.setVisible(false);
        panelButtons.add(rbtn_DummyBtn);
        rbtn_CorrectMsg.addActionListener(new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                userMsgType = rbtn_CorrectMsg.isSelected() ? 1 : 0;
            }
        });

        btnGroup.add(rbtn_CorruptMsg);
        rbtn_CorruptMsg.addActionListener(new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                userMsgType = rbtn_CorruptMsg.isSelected() ? 2 : 0;
            }
        });

        panelButtons.add(rbtn_CorrectMsg);
        panelButtons.add(rbtn_CorruptMsg);
        //  panelButtons.add(btnShowMessage);
        panelButtons.add(btnReset);

        //Define Button Action Listeners
        btnPlay.addActionListener(playAction);
        btnPause.addActionListener(new ActionListener() {

            @Override
            public synchronized void actionPerformed(ActionEvent ae) {
                stopFlag = true;

                try {
                    paintThread.wait();
                } catch (Exception ex) {
                }
                try {
                    paintThread2.wait();
                } catch (Exception ex) {

                }
                showMessage();
            }

            private void showMessage() {
                //To change body of generated methods, choose Tools | Templates.

                if (x < 70 && y < 172) {
                    displayMessageFrame(Msg1, "Application Layer of Red Ball");
                }
                if (x < 70 && y < 222 && y > 172) {
                    displayMessageFrame(Msg1, "Presentation Layer of Red Ball");
                }
                if (x < 70 && y < 267 && y > 222) {
                    displayMessageFrame(Msg1, "Session Layer of Red Ball");

                }
                if (x < 70 && y < 314 && y > 267) {
                    displayMessageFrame("H1|" + Msg1, "Transport Layer of Red Ball");
                }

                if (x < 70 && y < 358 && y > 314) {
                    displayMessageFrame("H1| H2 " + Msg1, "Network Layer of Red Ball");
                }
                if (x < 70 && y < 403 && y > 358) {
                    String DLLMsg1, DLLMsg2;

                    DLLMsg1 = "Preamble | SFD | 80 00 20 7A 3C 3E | 80 00 20 20 3A AE | 08 00 |" + Msg1 + "| 00 20 20 3A";
                    displayMessageFrame(DLLMsg1, "DataLink Layer of Red Ball");

                }
                if (x < 220 && y < 486 && y > 397) {

                    displayMessageFrame(getTotalMessage1(), "Physical Layer of Red Ball");
                }

                if (x > 220 && x < 345 && y == 485) {
                    //digital of red ball 
                    BufferedImage DigitalImage1 = createJoinedImage(getBinaryCRCMsg1(), "digital", 1);
                    displayMessageFrameDigital(new ImageIcon(DigitalImage1), "Digital signal for Red ball ");

                }
                if (x > 345 && x < 1148) {
                    //analog signal
                    BufferedImage AnalogImage1 = createJoinedImage(getBinaryCRCMsg1(), "analog", 1);
                    displayMessageFrameAnalog(new ImageIcon(AnalogImage1), "Analog signal for Red Ball");

                }
                if (x > 1148 && x < 1261 && y == 485) {
                    //digital of red ball
                    BufferedImage DigitalImage1 = createJoinedImage(getBinaryCRCMsg1(), "digital", 1);
                    displayMessageFrameDigital(new ImageIcon(DigitalImage1), "Digital signal for Red  ball");

                }
                if (x > 1400 && y < 172) {
                    displayMessageFrame(Msg1, "Application Layer of Red Ball ");
                }
                if (x > 1400 && y < 218 && y > 173) {
                    displayMessageFrame(Msg1, "Presentation Layer of Red Ball");
                }
                if (x > 1400 && y < 265 && y > 219) {
                    displayMessageFrame(Msg1, "Session Layer of Red Ball");

                }
                if (x > 1400 && y < 313 && y > 264) {
                    displayMessageFrame("H1|" + Msg1, "Transport Layer of Red Ball");
                }

                if (x > 1400 && y < 357 && y > 312) {
                    displayMessageFrame("H1| H2 " + Msg1, "Network Layer of Red Ball");
                }
                if (x > 1400 && y > 358 && y < 400) {
                    String DLLMsg1, DLLMsg2;

                    DLLMsg1 = "Preamble | SFD | 80 00 20 7A 3C 3E | 80 00 20 20 3A AE | 08 00 |" + Msg1 + "| 00 20 20 3A";
                    displayMessageFrame(DLLMsg1, "DataLink Layer of Red Ball");

                }
                if (x > 1260 && y < 486 && y > 397) {

                    displayMessageFrame(getTotalMessage1(), "Physical Layer of Red Ball");
                }

                if (x2 < 70 && y2 < 172) {
                    displayMessageFrame(Msg2, "Application Layer of Blue Ball");
                }
                if (x2 < 70 && y2 < 222 && y2 > 172) {
                    displayMessageFrame(Msg2, "Presentation Layer of Blue Ball");
                }
                if (x2 < 70 && y2 < 267 && y2 > 222) {
                    displayMessageFrame(Msg2, "Session Layer of Blue Ball");

                }
                if (x2 < 70 && y2 < 314 && y2 > 267) {
                    displayMessageFrame("H1|" + Msg2, "Transport Layer of Blue Ball");
                }

                if (x2 < 70 && y2 < 358 && y2 > 314) {
                    displayMessageFrame("H1| H2 " + Msg2, "Network Layer of Blue Ball");
                }
                if (x2 < 70 && y2 < 403 && y2 > 358) {
                    String DLLMsg1, DLLMsg2;

                    DLLMsg2 = "Preamble | SFD | 7B 1B 2A 7A 3C 3E | 80 00 20 20 3A AE | 08 00 |" + Msg2 + "| 00 20 20 3A";
                    displayMessageFrame(DLLMsg2, "DataLink Layer of Blue Ball");

                }
                if (x2 < 220 && y2 < 486 && y2 > 397) {

                    displayMessageFrame(getTotalMessage2(), "Physical Layer of Blue Ball");
                }

                if (x2 > 220 && x2 < 345 && y2 == 485) {
                    //digital of blue ball 
                    BufferedImage DigitalImage2 = createJoinedImage(getBinaryCRCMsg2(), "digital", 2);
                    displayMessageFrameDigital2(new ImageIcon(DigitalImage2), "Digital Signal for Blue Ball");

                }
                if (x2 > 345 && x2 < 1148) {
                    //analog signal
                    BufferedImage AnalogImage2 = createJoinedImage(getBinaryCRCMsg2(), "analog", 2);
                    displayMessageFrameAnalog2(new ImageIcon(AnalogImage2), "Analog Signal for Blue Ball");

                }
                if (x2 > 1148 && x2 < 1261 && y2 == 485) {
                    //digital of blue ball
                    BufferedImage DigitalImage2 = createJoinedImage(getBinaryCRCMsg2(), "digital", 2);
                    displayMessageFrameDigital2(new ImageIcon(DigitalImage2), "Digital Signal for Blue Ball");

                }
                if (x2 > 1400 && y2 < 172) {
                    displayMessageFrame(Msg2, "Application Layer of Blue Ball");
                }
                if (x2 > 1400 && y2 < 218 && y2 > 173) {
                    displayMessageFrame(Msg2, "Presentation Layer of Blue Ball");
                }
                if (x2 > 1400 && y2 < 265 && y2 > 219) {
                    displayMessageFrame(Msg2, "Session Layer of Bllue Ball");

                }
                if (x2 > 1400 && y2 < 313 && y2 > 264) {
                    displayMessageFrame("H1|" + Msg2, "Transport Layer of Blue Ball");
                }

                if (x2 > 1400 && y2 < 357 && y2 > 312) {
                    displayMessageFrame("H1| H2 " + Msg2, "Network Layer of Blue Ball");
                }
                if (x2 > 1400 && y2 > 358 && y2 < 400) {
                    String DLLMsg1, DLLMsg2;

                    DLLMsg2 = "Preamble | SFD | 7B 1B 2A 7A 3C 3E | 80 00 20 20 3A AE | 08 00 |" + Msg2 + "| 00 20 20 3A";
                    displayMessageFrame(DLLMsg2, "DataLink Layer of Blue Ball");

                }
                if (x2 > 1260 && y2 < 486 && y2 > 397) {

                    displayMessageFrame(getTotalMessage2(), "Physical Layer of Blue Ball");
                }

            }

        });

        btnReset.addActionListener(new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent ae) {

                //Reset to Initial Ball coordinates
                x = 20;
                y = 105;
                x2 = 20;
                y2 = 40;
                x3 = 818;
                xred = 1045;
                xblue = 1045;
                userMsgType = 0;
                stopFlag = true;
                redballmoves15 = false;
                secondtimeflag_red = false;
                tryflagred = false;
                tryflagblue = false;
                blueballmoves15 = false;
                
                redpath = null;
                bluepath = null;
                redpath = redPathObj.calculateShortestRoute();
                redEdges = redPathObj.getEdgesList();

                bluepath = bluePathObj.calculateShortestRoute();
                blueEdges = bluePathObj.getEdgesList();

                //Deselect Radio buttons
                btnGroup.clearSelection();


                try {
                    paintThread.interrupt();

                } catch (Exception ex) {
                }
                try {
                    paintThread2.interrupt();

                } catch (Exception ex) {
                }
                                drawpanel.repaint();


            }
        });

        try {
            setDrawPanelButtons();
        } catch (IOException ex) {
            Logger.getLogger(NewDijkstraMessageTransmitterUI.class.getName()).log(Level.SEVERE, null, ex);
        }

        frame.add(BorderLayout.NORTH, panelButtons);
        frame.setBackground(Color.WHITE);
        frame.add(BorderLayout.CENTER, drawpanel);
        frame.setSize(1500, 900);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public ActionListener playAction = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent A) {

            if (x == 20 && (!rbtn_CorrectMsg.isSelected() && !rbtn_CorruptMsg.isSelected())) {
                JOptionPane.showMessageDialog(frame, "Select the Message Type- Correct or Corrupt", "Message", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            stopFlag = false;
            if (paintThread != null && paintThread.isAlive()) {

                try {
                    paintThread.notify();
                } catch (Exception e) {
                }
            } else {
                paintThread = new Thread() {
                    @Override
                    public void run() {

                        // Contents of your existing handler go here, unchanged!
                        while (x == 20 && y < 485 && !stopFlag) {
                            y++;

                            drawpanel.repaint();
                            try {
                                paintThread.sleep(10);
                            } catch (Exception ex) {
                            }
                        }
                        System.out.println("(x,y):" + x + "," + y);

                        while ((x < 345 && y == 485) && !stopFlag) {
                            x++;
                            drawpanel.repaint();
                            try {
                                paintThread.sleep(10);
                            } catch (Exception ex) {
                            }
                        }

                        if (x == 445 && y == 385) {
                        }

                        String pathString = "";

                        for (Vertex v : redpath) {
                            pathString += v.getName();
                        }

                        //call dijkstra method
                        //retrieve array list 
                        //switch case to links
                        int indexPos = 0;
                        while (indexPos < pathString.length() - 1) {

                            String routeEdge = pathString.substring(indexPos, indexPos + 2);
                            switch (routeEdge) {
                                case "01":
                                    while (x < 445 && y > 385 && !stopFlag) {
                                        x++;
                                        y--;

                                        drawpanel.repaint();
                                        try {
                                            paintThread.sleep(10);
                                        } catch (Exception ex) {
                                        }
                                    }

                                    break;
                                case "02":
                                    while (x < 445 && y < 585 && !stopFlag) {
                                        x++;
                                        y++;

                                        drawpanel.repaint();
                                        try {
                                            paintThread.sleep(10);
                                        } catch (Exception ex) {
                                        }
                                    }

                                    break;
                                case "13":
                                    while (x < 545 && y < 485 && !stopFlag) {
                                        x++;
                                        y++;

                                        drawpanel.repaint();
                                        try {
                                            paintThread.sleep(10);
                                        } catch (Exception ex) {
                                        }
                                    }
                                    break;
                                case "15":
                                    while (x < 1045 && y == 385 && !stopFlag) {
                                        redballmoves15 = true;
                                        x++;

                                        drawpanel.repaint();

                                        try {
                                            paintThread.sleep(10);
                                        } catch (Exception ex) {
                                        }
                                    }
                                    break;
                                case "23":
                                    while (x < 545 && y < 485 && !stopFlag) {
                                        x++;
                                        y--;

                                        drawpanel.repaint();
                                        try {
                                            paintThread.sleep(10);
                                        } catch (Exception ex) {
                                        }
                                    }

                                    break;
                                case "26":
                                    while (x < 1045 && y == 585 && !stopFlag) {
                                        x++;
                                        drawpanel.repaint();
                                        try {
                                            paintThread.sleep(10);
                                        } catch (Exception ex) {
                                        }
                                    }
                                    break;
                                case "32":
                                    while (x > 445 && y < 585 && !stopFlag) {
                                        x--;
                                        y++;

                                        drawpanel.repaint();
                                        try {
                                            paintThread.sleep(10);
                                        } catch (Exception ex) {
                                        }
                                    }
                                    break;
                                case "31":
                                    while (x > 445 && y > 385 && !stopFlag) {
                                        x--;
                                        y--;

                                        drawpanel.repaint();
                                        try {
                                            paintThread.sleep(10);
                                        } catch (Exception ex) {
                                        }
                                    }
                                    break;
                                case "34":
                                    while (x < 945 && y == 485 && !stopFlag) {
                                        x++;

                                        drawpanel.repaint();
                                        try {
                                            paintThread.sleep(10);
                                        } catch (Exception ex) {
                                        }
                                    }
                                    break;
                                case "43":
                                    while (x > 545 && y == 485 && !stopFlag) {
                                        x--;

                                        drawpanel.repaint();
                                        try {
                                            paintThread.sleep(10);
                                        } catch (Exception ex) {
                                        }
                                    }
                                    break;
                                case "45":
                                    while (x < 1045 && y > 385 && !stopFlag) {
                                        x++;
                                        y--;

                                        drawpanel.repaint();
                                        try {
                                            paintThread.sleep(10);
                                        } catch (Exception ex) {
                                        }
                                    }
                                    break;
                                case "46":
                                    while (x < 1045 && y < 585 && !stopFlag) {
                                        x++;
                                        y++;

                                        drawpanel.repaint();
                                        try {
                                            paintThread.sleep(10);
                                        } catch (Exception ex) {
                                        }
                                    }
                                    break;
                                case "54":
                                    while (x > 945 && y < 485 && !stopFlag) {
                                        x--;
                                        y++;

                                        drawpanel.repaint();
                                        try {
                                            paintThread.sleep(10);
                                        } catch (Exception ex) {
                                        }
                                    }
                                    break;
                                case "64":
                                    while (x > 945 && y > 485 && !stopFlag) {
                                        x--;
                                        y--;

                                        drawpanel.repaint();
                                        try {
                                            paintThread.sleep(10);
                                        } catch (Exception ex) {
                                        }
                                    }
                                    break;
                                case "57":

                                    while (x < 1145 && y < 485 && !stopFlag) {
                                        if (xred >= 445 && x >= 1045 && redballmoves15 && (rbtn_CorrectMsg.isSelected() || rbtn_DummyBtn.isSelected())) {
                                            xred--;
                                        }

                                        if (x == 1046 && rbtn_CorruptMsg.isSelected() && redballmoves15) {
                                            try {
                                                paintThread.sleep(3000);
                                            } catch (Exception ex) {
                                            }
                                            x = 445;
                                            y = 385;
                                            secondtimeflag_red = true;
                                            rbtn_DummyBtn.setSelected(true);
                                            break;
                                        }
                                        x++;
                                        y++;

                                        drawpanel.repaint();
                                        try {
                                            paintThread.sleep(10);
                                        } catch (Exception ex) {
                                        }
                                    }
                                    break;
                                case "67":
                                    while (x < 1145 && y > 485 && !stopFlag) {
                                        if (xred >= 445 && x >= 1045 && redballmoves15 && (rbtn_CorrectMsg.isSelected() || rbtn_DummyBtn.isSelected())) {
                                            xred--;
                                        }
                                        x++;
                                        y--;
                                        System.out.println("In 6,7 RED" + x + "," + y);
                                        drawpanel.repaint();
                                        try {
                                            paintThread.sleep(10);
                                        } catch (Exception ex) {
                                        }
                                    }
                                    break;
                                default:
                                    System.err.println("Please enter a valid route path");
                            }
                            indexPos++;

                        }

                        while (x < 1465 && y == 485 && !stopFlag && !secondtimeflag_red) {

                            if (xred >= 445 && x >= 1045 && (rbtn_CorrectMsg.isSelected() && redballmoves15)) {
                                xred--;
                            }
                            System.out.println(" In Loop x<1465 && y== 485, (x,y)=" + x + "," + y);

                            drawpanel.repaint();
                            x++;
                            try {
                                paintThread.sleep(10);
                            } catch (Exception ex) {
                            }
                        }

                        while (x == 1465 && y >= 130 && !stopFlag && !secondtimeflag_red) {
                            if (xred >= 445 && x >= 1045 && (rbtn_CorrectMsg.isSelected() && redballmoves15)) {
                                xred--;
                            }
                            y--;
                            System.out.println("(x,y) y>=130 loop" + x + "," + y);
                            drawpanel.repaint();
                            try {
                                paintThread.sleep(10);
                            } catch (Exception ex) {
                            }
                        }

                        //  if (rbtn_DummyBtn.isSelected() && redballmoves15 && secondtimeflag_red) {
                        while (x < 1045 && y == 385 && !stopFlag && (rbtn_DummyBtn.isSelected() && redballmoves15 && secondtimeflag_red)) {
                            x++;

                            drawpanel.repaint();
                            System.out.println("Value of x and y in if loop rbtn_DummyBtn:" + x + "," + y);
                            try {
                                paintThread.sleep(10);
                            } catch (Exception ex) {
                            }
                        }
                        while (x < 1145 && y < 485 && !stopFlag && (rbtn_DummyBtn.isSelected() && redballmoves15 && secondtimeflag_red)) {
                            x++;
                            y++;

                            drawpanel.repaint();
                            System.out.println("Value of x and y :" + x + "," + y);
                            try {
                                paintThread.sleep(10);
                            } catch (Exception ex) {
                            }
                        }
                        while (x < 1465 && y == 485 && !stopFlag && (rbtn_DummyBtn.isSelected() && redballmoves15 && secondtimeflag_red)) {
                            x++;
                            drawpanel.repaint();
                            try {
                                paintThread.sleep(10);
                            } catch (Exception ex) {
                            }
                        }

                        while (x == 1465 && y >= 130 && !stopFlag && (rbtn_DummyBtn.isSelected() && redballmoves15 && secondtimeflag_red)) {
                            y--;
                            drawpanel.repaint();
                            try {
                                paintThread.sleep(10);
                            } catch (Exception ex) {
                            }
                        }

                        //   }
                    }
                };
                paintThread.start();
            }
            if (paintThread2 != null && paintThread2.isAlive()) {

                try {
                    paintThread2.notify();
                } catch (Exception e) {
                }
            } else {
                paintThread2 = new Thread() {
                    @Override
                    public void run() {

                        // Contents of your existing handler go here, unchanged!
                        while (x2 == 20 && y2 < 485 && !stopFlag) {
                            y2++;

                            drawpanel.repaint();
                            try {
                                paintThread2.sleep(10);
                            } catch (Exception ex) {
                            }
                        }
                        System.out.println("(x2,y2): " + x2 + "," + y2);

                        while ((x2 < 345 && y2 == 485) && !stopFlag) {
                            x2++;
                            drawpanel.repaint();
                            try {
                                paintThread2.sleep(10);
                            } catch (Exception ex) {
                            }
                        }

                        String pathString = "";

                        for (Vertex v : bluepath) {
                            pathString += v.getName();
                        }

                        //<editor-fold desc="Switch path cases for BluePath">
                        //call dijkstra method
                        //retrieve array list 
                        //switch case to links
                        int indexPos = 0;
                        while (indexPos < pathString.length() - 1) {

                            String routeEdge = pathString.substring(indexPos, indexPos + 2);
                            switch (routeEdge) {
                                case "01":
                                    while (x2 < 445 && y2 > 385 && !stopFlag) {
                                        x2++;
                                        y2--;

                                        drawpanel.repaint();
                                        try {
                                            paintThread2.sleep(10);
                                        } catch (Exception ex) {
                                        }
                                    }

                                    break;
                                case "02":
                                    while (x2 < 445 && y2 < 585 && !stopFlag) {
                                        x2++;
                                        y2++;

                                        drawpanel.repaint();
                                        try {
                                            paintThread2.sleep(10);
                                        } catch (Exception ex) {
                                        }
                                    }

                                    break;
                                case "13":
                                    while (x2 < 545 && y2 < 485 && !stopFlag) {
                                        x2++;
                                        y2++;

                                        drawpanel.repaint();
                                        try {
                                            paintThread2.sleep(10);
                                        } catch (Exception ex) {
                                        }
                                    }
                                    break;
                                case "15":

                                    while (x2 < 1045 && y2 == 385 && !stopFlag) {
                                        blueballmoves15 = true;

                                        x2++;

                                        drawpanel.repaint();
                                        try {
                                            paintThread2.sleep(10);
                                        } catch (Exception ex) {
                                        }
                                    }
                                    break;
                                case "23":
                                    while (x2 < 545 && y2 < 485 && !stopFlag) {
                                        x2++;
                                        y2--;

                                        drawpanel.repaint();
                                        try {
                                            paintThread2.sleep(10);
                                        } catch (Exception ex) {
                                        }
                                    }

                                    break;
                                case "26":
                                    while (x2 < 1045 && y2 == 585 && !stopFlag) {
                                        x2++;
                                        drawpanel.repaint();
                                        try {
                                            paintThread2.sleep(10);
                                        } catch (Exception ex) {
                                        }
                                    }
                                    break;
                                case "32":
                                    while (x2 > 445 && y2 < 585 && !stopFlag) {
                                        x2--;
                                        y2++;

                                        drawpanel.repaint();
                                        try {
                                            paintThread2.sleep(10);
                                        } catch (Exception ex) {
                                        }
                                    }
                                    break;
                                case "31":
                                    while (x2 > 445 && y2 > 385 && !stopFlag) {
                                        x2--;
                                        y2--;

                                        drawpanel.repaint();
                                        try {
                                            paintThread2.sleep(10);
                                        } catch (Exception ex) {
                                        }
                                    }
                                    break;
                                case "34":
                                    while (x2 < 945 && y2 == 485 && !stopFlag) {
                                        x2++;

                                        drawpanel.repaint();
                                        try {
                                            paintThread2.sleep(10);
                                        } catch (Exception ex) {
                                        }
                                    }
                                    break;
                                case "43":
                                    while (x2 > 545 && y2 == 485 && !stopFlag) {
                                        x2--;

                                        drawpanel.repaint();
                                        try {
                                            paintThread2.sleep(10);
                                        } catch (Exception ex) {
                                        }
                                    }
                                    break;
                                case "45":
                                    while (x2 < 1045 && y2 > 385 && !stopFlag) {
                                        x2++;
                                        y2--;

                                        drawpanel.repaint();
                                        try {
                                            paintThread2.sleep(10);
                                        } catch (Exception ex) {
                                        }
                                    }
                                    break;
                                case "46":
                                    while (x2 < 1045 && y2 < 585 && !stopFlag) {
                                        x2++;
                                        y2++;

                                        drawpanel.repaint();
                                        try {
                                            paintThread2.sleep(10);
                                        } catch (Exception ex) {
                                        }
                                    }
                                    break;
                                case "54":
                                    while (x2 > 945 && y2 < 485 && !stopFlag) {
                                        x2--;
                                        y2++;

                                        drawpanel.repaint();
                                        try {
                                            paintThread2.sleep(10);
                                        } catch (Exception ex) {
                                        }
                                    }
                                    break;
                                case "64":
                                    while (x2 > 945 && y2 > 485 && !stopFlag) {
                                        x2--;
                                        y2--;

                                        drawpanel.repaint();
                                        try {
                                            paintThread2.sleep(10);
                                        } catch (Exception ex) {
                                        }
                                    }
                                    break;
                                case "57":

                                    while (x2 < 1145 && y2 < 485 && !stopFlag) {
                                        if (xblue >= 435 && x2 >= 1045 && blueballmoves15 && (rbtn_CorrectMsg.isSelected() || rbtn_DummyBtn.isSelected())) {
                                            xblue--;
                                        }
                                        if (x2 == 1046 && rbtn_CorruptMsg.isSelected() || rbtn_DummyBtn.isSelected() && blueballmoves15) {
                                            try {
                                                paintThread2.sleep(3000);
                                            } catch (Exception ex) {
                                            }
                                            x2 = 445;
                                            y2 = 385;
                                            tryflagblue = true;
                                            rbtn_DummyBtn.setSelected(true);
                                            break;
                                        }
                                        x2++;
                                        y2++;

                                        drawpanel.repaint();
                                        try {
                                            paintThread2.sleep(10);
                                        } catch (Exception ex) {
                                        }
                                    }
                                    break;
                                case "67":
                                    while (x2 < 1145 && y2 > 485 && !stopFlag) {
                                        if (xblue >= 435 && x2 >= 1045 && blueballmoves15 && (rbtn_CorrectMsg.isSelected() || rbtn_DummyBtn.isSelected())) {
                                            xblue--;
                                        }
                                        x2++;
                                        y2--;

                                        drawpanel.repaint();
                                        try {
                                            paintThread2.sleep(10);
                                        } catch (Exception ex) {
                                        }
                                    }
                                    break;
                                default:
                                    System.err.println("Please enter a valid route path");
                            }
                            indexPos++;

                        }
                        //</editor-fold>

                        while (x2 < 1465 && y2 == 485 && !stopFlag) {
                            if (xblue >= 435 && x2 >= 1045 && blueballmoves15 && (rbtn_CorrectMsg.isSelected() || rbtn_DummyBtn.isSelected())) {
                                xblue--;
                            }

                            x2++;

                            drawpanel.repaint();
                            try {
                                paintThread2.sleep(20);
                            } catch (Exception ex) {
                            }
                        }

                        while (x2 == 1465 && y2 >= 130 && !stopFlag) {
                            if (xblue >= 435 && x2 >= 1045 && blueballmoves15 && (rbtn_CorrectMsg.isSelected() || rbtn_DummyBtn.isSelected())) {
                                xblue--;
                            }
                            y2--;

                            drawpanel.repaint();
                            try {
                                paintThread2.sleep(20);
                            } catch (Exception ex) {
                            }
                        }

                        //  if (rbtn_DummyBtn.isSelected() && blueballmoves15 && tryflagblue) {
                        while (x2 < 1045 && y2 == 385 && !stopFlag && (rbtn_DummyBtn.isSelected() && blueballmoves15 && tryflagblue)) {
                            x2++;

                            drawpanel.repaint();
                            System.out.println("Value of x2 and y2 :" + x2 + "," + y2);
                            try {
                                paintThread2.sleep(10);
                            } catch (Exception ex) {
                            }
                        }
                        while (x2 < 1145 && y2 < 485 && !stopFlag && (rbtn_DummyBtn.isSelected() && blueballmoves15 && tryflagblue)) {
                            x2++;
                            y2++;

                            drawpanel.repaint();
                            System.out.println("Value of x and y :" + x + "," + y);
                            try {
                                paintThread2.sleep(10);
                            } catch (Exception ex) {
                            }
                        }
                        while (x2 < 1465 && y2 == 485 && !stopFlag && (rbtn_DummyBtn.isSelected() && blueballmoves15 && tryflagblue)) {
                            x2++;
                            drawpanel.repaint();
                            try {
                                paintThread2.sleep(10);
                            } catch (Exception ex) {
                            }
                        }

                        while (x2 == 1465 && y2 >= 130 && !stopFlag && (rbtn_DummyBtn.isSelected() && blueballmoves15 && tryflagblue)) {
                            y2--;
                            drawpanel.repaint();
                            try {
                                paintThread2.sleep(10);
                            } catch (Exception ex) {
                            }
                        }

                        //}
                    }
                };
                paintThread2.start();
            }

        }
    };

    private void setDrawPanelButtons() throws IOException {
        /* Left Coordinates
         (135,189), (135,233), (135,278) , (135,320), (135,367), (135,413), (135,455)
         */

        ImageIcon buttonIconImg = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("buttonIcon.png")));
        //Router1 Remainder
        JButton btn_Rem_R1 = new JButton(buttonIconImg);
        btn_Rem_R1.setBounds(426, 330, 30, 30);
        btn_Rem_R1.setBorderPainted(false);
        btn_Rem_R1.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (redballmoves15 && (x > 450)) {
                    try {
                        //BufferedImage DigitalImage1 = createJoinedImage(getBinaryCRCMsg1(), "digital", 1);
                        String binary1 = new BigInteger(Msg1.getBytes()).toString(2);
                        displayRemainder(binary1, PHYMsgCRC, "Remainder for Red Ball");

                    } catch (Exception ex) {
                    }
                }
                if (blueballmoves15 && (x2 > 450)) {
                    try {
                        //BufferedImage DigitalImage1 = createJoinedImage(getBinaryCRCMsg1(), "digital", 1);
                        String binary2 = new BigInteger(Msg2.getBytes()).toString(2);
                        displayRemainder(binary2, PHYMsgCRC, "Remainder for Blue Ball");

                    } catch (Exception ex) {
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "The message has not reached this position", "Info", JOptionPane.INFORMATION_MESSAGE);
                }
            }

        });
        //Router1 Remainder

        JButton btn_Rem_R2 = new JButton(buttonIconImg);
        btn_Rem_R2.setBounds(1027, 330, 30, 30);
        btn_Rem_R2.setBorderPainted(false);
        btn_Rem_R2.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ((x > 1030) && redballmoves15) {
                    try {
                        String binary1 = new BigInteger(Msg1.getBytes()).toString(2);
                        displayRemainderR2(binary1, PHYMsgCRC, "Remainder for Red Ball");

                    } catch (Exception ex) {
                        System.err.println("Error in Displaying R2 remainder - btn_Rem_R2 action listener");
                    }
                }
                if ((x2 > 1030) && blueballmoves15) {
                    try {
                        String binary2 = new BigInteger(Msg2.getBytes()).toString(2);
                        displayRemainderR2(binary2, PHYMsgCRC, "Remainder for Blue Ball");

                    } catch (Exception ex) {
                        System.err.println("Error in Displaying R2 remainder - btn_Rem_R2 action listener");
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "The message has not reached this position", "Info", JOptionPane.INFORMATION_MESSAGE);
                }
            }

        });

        drawpanel.setLayout(null);

//        drawpanel.add(btn_Link1);
        drawpanel.add(btn_Rem_R1);
        drawpanel.add(btn_Rem_R2);

    }

    //Method to Display Remainder and Encoded Data
    private void displayRemainder(String binaryCRCMsg1, String PHYMsgCRC1, String title) {
        RemainderCalculator rcObj = new RemainderCalculator();
        ArrayList<String> encodedword = rcObj.encodeData(binaryCRCMsg1, PHYMsgCRC1);

        JFrame remainderMsgFrame = new JFrame();

        JLabel label1 = new JLabel("<html>Remainder at R1 =" + encodedword.get(0) + "<br>" + "CodeWord =" + encodedword.get(1) + "</html>");
        label1.setHorizontalAlignment(JLabel.CENTER);
        JPanel panel1 = new JPanel(new GridBagLayout());
        label1.setFont(new Font("Serif", Font.BOLD, 16));
        panel1.add(label1);
        JScrollPane Scrollpane1 = new JScrollPane(panel1);
        remainderMsgFrame.setTitle(title);
        remainderMsgFrame.add(Scrollpane1);
        remainderMsgFrame.setVisible(true);
        remainderMsgFrame.setSize(800, 400);
        remainderMsgFrame.setAlwaysOnTop(true);
        remainderMsgFrame.setLocation(300, 300);
        remainderMsgFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }

    //Method to display remainder at router 2
    private void displayRemainderR2(String binaryCRCMsg1, String PHYMsgCRC1, String title) {
        RemainderCalculator rcObj = new RemainderCalculator();
        ArrayList<String> encodedword = rcObj.encodeData(binaryCRCMsg1, PHYMsgCRC1);
        ArrayList<String> encodedword2;
        String rem = "";

        if (rbtn_CorrectMsg.isSelected() || rbtn_DummyBtn.isSelected()) {

            if (redballmoves15) {

                encodedword2 = rcObj.encodeData(encodedword.get(1), PHYMsgCRC1);
                rem += encodedword2.get(0);
            }
            if (blueballmoves15) {

                encodedword2 = rcObj.encodeData(encodedword.get(1), PHYMsgCRC1);
                rem += encodedword2.get(0);
            }

            String CRCMessage = "Correct Message has been delivered from Router 1 to Router 2";
            remflag = true;
            JFrame msgFrame = new JFrame();

            JLabel label1 = new JLabel("<html>Remainder at R2 =" + rem + "<br>" + CRCMessage + "</html>");

            label1.setHorizontalAlignment(JLabel.CENTER);
            JPanel panel1 = new JPanel(new GridBagLayout());
            label1.setFont(new Font("Serif", Font.BOLD, 16));
            panel1.add(label1);
            JScrollPane Scrollpane1 = new JScrollPane(panel1);
            msgFrame.setTitle(title);
            msgFrame.add(Scrollpane1);
            msgFrame.setVisible(true);
            msgFrame.setSize(800, 400);
            msgFrame.setAlwaysOnTop(true);
            msgFrame.setLocation(300, 300);
            msgFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            msgFrame.setLayout(new GridLayout(1, 2));

        }
        if (rbtn_CorruptMsg.isSelected()) {
            String CRCMessage = " ";

            String CorruptWord = encodedword.get(1).contains("1") ? encodedword.get(1).replaceFirst("1", "0") : encodedword.get(1).replaceFirst("0", "1");

            encodedword2 = rcObj.encodeData(CorruptWord, PHYMsgCRC1);
            if (encodedword2.get(0).equals("000")) {
                CRCMessage = "Correct Message has been delivered from Router 1 to Router 2";

                remflag = true;
            } else {

                CRCMessage = "Non-Zero Remainder -> Message is Corrupted";
                remflag = false;
            }

            JFrame temp_msgFrame = new JFrame();

            JLabel temp_label = new JLabel("<html>Remainder at R2 =" + encodedword2.get(0) + "<br>" + "Recieved Message:" + CorruptWord + " <br>" + CRCMessage + "</html>");

            temp_label.setHorizontalAlignment(JLabel.CENTER);
            JPanel temp_Panel = new JPanel(new GridBagLayout());
            temp_label.setFont(new Font("Serif", Font.BOLD, 16));
            temp_Panel.add(temp_label);
            JScrollPane temp_Scrollpane1 = new JScrollPane(temp_Panel);
            temp_msgFrame.setTitle(title);
            temp_msgFrame.add(temp_Scrollpane1);
            temp_msgFrame.setVisible(true);
            temp_msgFrame.setSize(800, 400);
            temp_msgFrame.setAlwaysOnTop(true);
            temp_msgFrame.setLocation(300, 300);
            temp_msgFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            temp_msgFrame.setLayout(new GridLayout(1, 2));
        }
    }

    /* Method to display String Message Panel 
     Parameter - Messages that needs to be displayed inside the Frame.
     */
    private void displayMessageFrame(String messageText, String title) {
        JFrame msgFrame = new JFrame();

        JLabel label1 = new JLabel(messageText);
        label1.setHorizontalAlignment(JLabel.CENTER);
        JPanel panel1 = new JPanel(new GridBagLayout());
        label1.setFont(new Font("Serif", Font.BOLD, 28));
        panel1.add(label1);
        JScrollPane Scrollpane1 = new JScrollPane(panel1);
        msgFrame.add(Scrollpane1);
        msgFrame.setVisible(true);
        msgFrame.setSize(800, 400);
        msgFrame.setTitle(title);
        msgFrame.setAlwaysOnTop(true);
        msgFrame.setLocation(300, 300);
        msgFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        msgFrame.setLayout(new GridLayout(1, 2));
    }

    /* Method to display Images Message Panel 
     Parameter - ImageIcon that needs to be displayed inside the Frame.
     */
    private synchronized void displayMessageFrameAnalog(ImageIcon img, String title) {

        if (imgFrameAnalog != null && !imgFrameAnalog.isShowing()) {
            imgFrameAnalog.setVisible(true);
            return;
        }

        imgFrameAnalog = new JFrame();
        imgFrameAnalog.setSize(600, 200);
        imgFrameAnalog.setTitle(title);
        imgFrameAnalog.setAlwaysOnTop(true);
        imgFrameAnalog.setLocation(300, 300);
        imgFrameAnalog.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());

        JLabel label = new JLabel(img);
        label.setHorizontalAlignment(JLabel.CENTER);
        panel.add(label);

        JScrollPane scrollPane = new JScrollPane(panel);

        imgFrameAnalog.add(scrollPane);
        imgFrameAnalog.setVisible(true);
    }

    private synchronized void displayMessageFrameAnalog2(ImageIcon img, String title) {

        if (imgFrameAnalog2 != null && !imgFrameAnalog2.isShowing()) {
            imgFrameAnalog2.setVisible(true);
            return;
        }

        imgFrameAnalog2 = new JFrame();
        imgFrameAnalog2.setSize(600, 200);
        imgFrameAnalog2.setTitle(title);
        imgFrameAnalog2.setAlwaysOnTop(true);
        imgFrameAnalog2.setLocation(300, 300);
        imgFrameAnalog2.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());

        JLabel label = new JLabel(img);
        label.setHorizontalAlignment(JLabel.CENTER);
        panel.add(label);

        JScrollPane scrollPane = new JScrollPane(panel);

        imgFrameAnalog2.add(scrollPane);
        imgFrameAnalog2.setVisible(true);
    }

    private synchronized void displayMessageFrameDigital(ImageIcon img, String title) {

        if (imgFrameDigital != null && !imgFrameDigital.isShowing()) {
            imgFrameDigital.setVisible(true);
            return;
        }

        imgFrameDigital = new JFrame();
        imgFrameDigital.setSize(600, 200);
        imgFrameDigital.setTitle(title);
        imgFrameDigital.setAlwaysOnTop(true);
        imgFrameDigital.setLocation(300, 300);
        imgFrameDigital.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());

        JLabel label = new JLabel(img);
        label.setHorizontalAlignment(JLabel.CENTER);
        panel.add(label);

        JScrollPane scrollPane = new JScrollPane(panel);

        imgFrameDigital.add(scrollPane);
        imgFrameDigital.setVisible(true);
    }

    private synchronized void displayMessageFrameDigital2(ImageIcon img, String title) {

        if (imgFrameDigital2 != null && !imgFrameDigital2.isShowing()) {
            imgFrameDigital2.setVisible(true);
            return;
        }

        imgFrameDigital2 = new JFrame();
        imgFrameDigital2.setSize(600, 200);
        imgFrameDigital2.setTitle(title);
        imgFrameDigital2.setAlwaysOnTop(true);
        imgFrameDigital2.setLocation(300, 300);
        imgFrameDigital2.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());

        JLabel label = new JLabel(img);
        label.setHorizontalAlignment(JLabel.CENTER);
        panel.add(label);

        JScrollPane scrollPane = new JScrollPane(panel);

        imgFrameDigital2.add(scrollPane);
        imgFrameDigital2.setVisible(true);
    }

    /* to cocatenate images
     */
    private synchronized BufferedImage createJoinedImage(String message, String type, int resValue) {

        BufferedImage result = null;
        int x = 0, y = 0;
        try {
            BufferedImage[] buffImages = new BufferedImage[message.length() + 1];

            int imgWidth = 0, imgHeight = 0, baseWidth = 0;
            if (type == "analog") {
                baseWidth = 8272;
                imgWidth = 47;
                imgHeight = 51;
            } else {
                baseWidth = 8272;
                imgWidth = 47;
                imgHeight = 63;
            }

            buffImages[0] = ImageIO.read(getClass().getResource(type + resValue + "_result.png"));

            //Set Images - Analog & Digital based on type
            BufferedImage type0Img = ImageIO.read(getClass().getResource(type + "bit_0.png"));
            BufferedImage type1Img = ImageIO.read(getClass().getResource(type + "bit_1.png"));

            for (int i = 0; i < message.length(); i++) {
                System.out.println(type + "bit_" + message.charAt(i) + ".png");
                //  BufferedImage img = type0Img;
                //  list_Images.add(message.charAt(i)== '1'?type1Img: type0Img );
                buffImages[i + 1] = message.charAt(i) == '1' ? type1Img : type0Img;
            }

            result = new BufferedImage(
                    baseWidth + (message.length() * imgWidth), 1 * imgHeight, //work these out
                    BufferedImage.TYPE_INT_RGB);
            Graphics g = result.getGraphics();

            int counter = 1;
            for (Object image : buffImages) {

                g.drawImage((BufferedImage) image, x, y, null);

                if (counter == 1) {
                    x += 8272;
                    counter++;
                } else {
                    x += imgWidth;
                }

                if (x > result.getWidth()) {
                    x = 0;
                    y += ((BufferedImage) image).getHeight();
                }
            }
            ImageIO.write(result, "png", new File("result.png"));

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    class MyDrawPanel extends JPanel {

        public MyDrawPanel() {

        }

        @Override
        public void setBackground(Color color) {
            super.setBackground(Color.WHITE); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void paintComponent(Graphics g) {

            try {
                BufferedImage img, img1, img2;
                img = ImageIO.read(getClass().getResource("system2.png"));
                img1 = ImageIO.read(getClass().getResource("redball.png"));
                img2 = ImageIO.read(getClass().getResource("blueball.png"));
                g.drawImage(img, 0, 0, this);

                //<editor-fold desc="Draw Label Weights">
                for (int i = 0; i < 11; i++) {
                    switch (i) {
                        case 0:
                            g.setColor(Color.red);
                            g.drawString(redEdges.get(i).getWeight() + "", 364, 423);
                            g.setColor(Color.blue);
                            g.drawString(blueEdges.get(i).getWeight() + "", 394, 402);
                            break;
                        case 1:
                            g.setColor(Color.red);
                            g.drawString(redEdges.get(i).getWeight() + "", 353, 528);
                            g.setColor(Color.blue);
                            g.drawString(blueEdges.get(i).getWeight() + "", 384, 554);
                            break;
                        case 2:
                            g.setColor(Color.red);
                            g.drawString(redEdges.get(i).getWeight() + "", 480, 410);
                            g.setColor(Color.blue);
                            g.drawString(blueEdges.get(i).getWeight() + "", 506, 438);
                            break;
                        case 3:
                            g.setColor(Color.red);
                            g.drawString(redEdges.get(i).getWeight() + "", 496, 503);
                            g.setColor(Color.blue);
                            g.drawString(blueEdges.get(i).getWeight() + "", 464, 542);
                            break;
                        case 4:
                            g.setColor(Color.red);
                            g.drawString(redEdges.get(i).getWeight() + "", 655, 360);
                            g.setColor(Color.blue);
                            g.drawString(blueEdges.get(i).getWeight() + "", 720, 360);
                            break;
                        case 5:
                            g.setColor(Color.red);
                            g.drawString(redEdges.get(i).getWeight() + "", 655, 472);
                            g.setColor(Color.blue);
                            g.drawString(blueEdges.get(i).getWeight() + "", 720, 472);
                            break;
                        case 6:
                            g.setColor(Color.red);
                            g.drawString(redEdges.get(i).getWeight() + "", 655, 605);
                            g.setColor(Color.blue);
                            g.drawString(blueEdges.get(i).getWeight() + "", 720, 605);
                            break;
                        case 7:
                            g.setColor(Color.red);
                            g.drawString(redEdges.get(i).getWeight() + "", 986, 458);
                            g.setColor(Color.blue);
                            g.drawString(blueEdges.get(i).getWeight() + "", 1026, 416);
                            break;
                        case 8:
                            g.setColor(Color.red);
                            g.drawString(redEdges.get(i).getWeight() + "", 974, 508);
                            g.setColor(Color.blue);
                            g.drawString(blueEdges.get(i).getWeight() + "", 1010, 542);
                            break;
                        case 9:
                            g.setColor(Color.red);
                            g.drawString(redEdges.get(i).getWeight() + "", 1084, 402);
                            g.setColor(Color.blue);
                            g.drawString(blueEdges.get(i).getWeight() + "", 1099, 423);
                            break;
                        case 10:
                            g.setColor(Color.red);
                            g.drawString(redEdges.get(i).getWeight() + "", 1088, 558);
                            g.setColor(Color.blue);
                            g.drawString(blueEdges.get(i).getWeight() + "", 1132, 516);
                            break;
                        default:
                            break;
                    }

                    System.out.println("Edge Weight:" + redEdges.get(i).getWeight());
                }

                if (!(rbtn_CorrectMsg.isSelected() || rbtn_CorruptMsg.isSelected() || rbtn_DummyBtn.isSelected())) {
                    g.drawImage(img1, x, y, this);
                    g.drawImage(img2, x2, y2, this);
                }

                //<editor-fold desc="CorrectMsg PathDrawing">
                if (rbtn_CorrectMsg.isSelected()) {

                    //buffer should be created when red ball enters link 15 - so check the flag if it entered link 15
                    if (redballmoves15) {
                        //show buffer ball until ack reaches it
                        if (xred >= 445) {
                            g.setColor(Color.red);
                            g.fillOval(445, 385, 30, 30);
                        }
                    }

                    if (x >= 1045 && redballmoves15) //red ball acknowledgement should be sent 
                    {
                        if (xred >= 445) {
                            g.setColor(Color.red);
                            g.fillOval(xred, 400, 10, 10);

                        }

                    }
                    if (blueballmoves15) {
                        //show buffer ball until ack reaches it
                        if (xblue >= 435) {
                            g.setColor(Color.blue);
                            g.fillOval(435, 385, 30, 30);
                        }
                    }

                    if (x2 >= 1045 && blueballmoves15) //blue ball acknowledgement should be sent 
                    {
                        if (xblue >= 435) {
                            g.setColor(Color.blue);
                            g.fillOval(xblue, 400, 10, 10);

                        }

                    }

                    g.drawImage(img1, x, y, this);
                    g.drawImage(img2, x2, y2, this);

                }
                //</editor-fold>

                //<editor-fold desc="CorruptMsg PathDraw">
                //corruptmessage 
                //ball stops at 1045 
                //ball resets to 445
                //continue like correct message
                //buffer should be created when red ball enters link 15 - so check the flag if it entered link 15
                if(rbtn_CorruptMsg.isSelected()|| rbtn_DummyBtn.isSelected())
                {
                    
                if (redballmoves15) {
                    //show buffer ball until ack reaches it

                    g.setColor(Color.red);
                    g.fillOval(445, 385, 30, 30);

                }

                if (x >= 1045 && redballmoves15 && !tryflagred) //red ball acknowledgement should be sent 
                {
                    if (x == 1045) {
                        paintThread.sleep(50);
                        x = 445;
                        y = 385;
                        try {
                            paintThread.notify();
                        } catch (Exception e) {

                        }

                    }

                    if ((x >= 1450)) {

                        g.setColor(Color.white);
                        g.fillOval(435, 385, 30, 30);
                        x = 445;
                        y = 385;
                        try {
                            paintThread.wait();
                        } catch (Exception e) {

                        }
                        try {
                            paintThread.notify();
                        } catch (Exception e) {

                        }

                        System.out.println("Entered second traverse: x, y " + x + "," + y);
                        secondtimeflag_red = true;
                        if(!rbtn_CorrectMsg.isSelected()) rbtn_DummyBtn.doClick();
                        tryflagred = true;

                    }
                    System.out.println("Entered second traverse: x, y " + x + "," + y);
                    secondtimeflag_red = true;
                    
                    if(!rbtn_CorrectMsg.isSelected()) rbtn_DummyBtn.doClick();
                    tryflagred = true;

                } else {
                    g.drawImage(img1, x, y, this);
                }

                if (blueballmoves15) {
                    //show buffer ball until ack reaches it

                    g.setColor(Color.blue);
                    g.fillOval(435, 385, 30, 30);

                }

                if (x2 >= 1045 && blueballmoves15 && (rbtn_CorruptMsg.isSelected() || rbtn_DummyBtn.isSelected()) && !tryflagblue) //red ball acknowledgement should be sent 
                {

                    if (x2 < 1450) {
                        g.drawImage(img2, 1045, 385, this);

                    }

                    if ((x2 >= 1450)) {

                        g.setColor(Color.white);
                        g.fillOval(435, 385, 30, 30);
                        x2 = 445;
                        y2 = 385;
                        System.out.println("Red Thread status:" + paintThread2.isAlive());
                        System.out.println("Red Thread status:" + paintThread2.isInterrupted());

                        try {

                            paintThread2.sleep(10);
                        } catch (Exception e) {

                        }

                        rbtn_DummyBtn.doClick();

                        tryflagblue = true;

                    }

                } else {
                    g.drawImage(img2, x2, y2, this);
                }
            }

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

    }
}
