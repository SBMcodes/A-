package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.TimerTask;

public class Node extends JButton implements MouseListener,KeyListener {

    Node parent;
    NodeState nodeState;
    boolean opened = false;
    int row,col;

    // gCost: Distance between current node and start node
    // hCost: Distance between current node and goal node
    // fCost: gCost+hCost
    int gCost,hCost,fCost;


    Color inactiveBgColor = new Color(200,200,200);
    Color inactiveFgColor = new Color(40,40,40);
    Color activeBgColor = new Color(40,40,40);
    Color activeFgColor = new Color(200,200,200);

    Color startBgColor = new Color(30,40,80);
    Color startFgColor = activeFgColor;
    Color goalBgColor = new Color(80,10,30);
    Color goalFgColor = activeFgColor;

    Panel panel;

    public Node(Panel panel,int row,int col){
        this.row=row;
        this.col=col;

        this.panel = panel;

        nodeState = NodeState.OPEN;

        setContentAreaFilled(false);

        setBackground(inactiveBgColor);
        setForeground(inactiveFgColor);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
//        setFocusable(false);


        addMouseListener(this);
        addKeyListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
//        if (nodeState==NodeState.OPEN) {
//            g.setColor(inactiveBgColor);
//        } else if (nodeState==NodeState.SOLID) {
//            g.setColor(activeBgColor);
//        } else {
//            g.setColor(getBackground());
//        }
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(e.getButton()==1){
            panel.isLeftClicked=true;
            isControlLeftClick();
        } else if (e.getButton()==3) {
            panel.isRightClicked=true;
            isControlRightClick();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        panel.isLeftClicked=false;
        panel.isRightClicked=false;
    }

    public void leftClickEvent(){
        if(this.nodeState==NodeState.START){
            panel.setStartNodeStateNull();
        }
        if(this.nodeState==NodeState.GOAL){
            panel.setGoalNodeStateNull();
        }
        setBackground(activeBgColor);
        setForeground(activeFgColor);
        setText("");
        nodeState = NodeState.SOLID;
    }

    public void rightClickEvent(){
        if(this.nodeState==NodeState.START){
            panel.setStartNodeStateNull();
        }
        if(this.nodeState==NodeState.GOAL){
            panel.setGoalNodeStateNull();
        }
        setBackground(inactiveBgColor);
        setForeground(inactiveFgColor);
        setText("");
        nodeState = NodeState.OPEN;
    }

    public void isControlLeftClick(){
        if(panel.isControlClicked){
            panel.setStartNode(this.row,this.col);
            panel.findCostOnNodes();
        }
        else{
            leftClickEvent();
        }
    }

    public void isControlRightClick(){
        if(panel.isControlClicked){
            panel.setGoalNode(this.row,this.col);
            panel.findCostOnNodes();
        }
        else{
            rightClickEvent();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if(panel.isLeftClicked){
            isControlLeftClick();
        } else if (panel.isRightClicked) {
           isControlRightClick();
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }


    public void setAsStart(){
        nodeState = NodeState.START;
        setBackground(startBgColor);
        setForeground(startFgColor);
        opened=true;
//        setText("<html>S</html>");
    }

    public void setAsGoal(){
        nodeState = NodeState.GOAL;
        setBackground(goalBgColor);
        setForeground(goalFgColor);
//        setText("<html>G</html>");
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if(code==KeyEvent.VK_CONTROL){
            panel.isControlClicked=true;
        } else if (code==KeyEvent.VK_ENTER) {
//            panel.search();
            if(panel.startNode==null || panel.goalNode==null){
                return;
            }
            while(!panel.goalReached && panel.totalIterations<=(panel.maxRow*panel.maxCol)){
                panel.search();
            }
        } else if (code==KeyEvent.VK_SPACE) {
            panel.clearChecked();
        } else if (code==KeyEvent.VK_ESCAPE) {
            panel.clearSolid();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if(code==KeyEvent.VK_CONTROL){
            panel.isControlClicked=false;
        }
    }

    public void setAsOpen(){
        this.nodeState = NodeState.OPEN;
        opened=true;
    }

    public void unsetAsChecked(){
        opened=false;
        if(this.nodeState==NodeState.CHECKED){
            this.nodeState = NodeState.OPEN;
            setBackground(inactiveBgColor);
        }
    }

    public void setAsChecked(){
        if(this.nodeState==NodeState.OPEN){
            this.nodeState = NodeState.CHECKED;
            panel.timer.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            setBackground(Color.ORANGE);
                        }
                    }
            ,panel.lastDrawTime);
            panel.lastDrawTime+=3;
        }
    }


    public void setAsPath(){
        if(this.nodeState==NodeState.CHECKED){
            panel.timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    setBackground(Color.green);
                }
            }, panel.lastDrawTime);
            panel.lastDrawTime+=20;

//            setBackground(Color.green);

        }
    }
}

// @SBMcodes