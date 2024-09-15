package main;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Timer;

public class Panel extends JPanel{

//    final int maxCol=20,maxRow=14,nodeSize=40;

    final int maxCol=40,maxRow=28,nodeSize=20;

    final int screenWidth=maxCol*nodeSize;
    final int screenHeight = maxRow*nodeSize;

    boolean isLeftClicked=false;
    boolean isRightClicked=false;

    boolean isControlClicked = false;

    Node[][] nodeMatrix = new Node[maxRow][maxCol];
    Node startNode,goalNode,currentNode;
    ArrayList<Node> openList = new ArrayList<>();
    ArrayList<Node> checkedList = new ArrayList<>();

    boolean goalReached = false;

    public int totalIterations=0;
    public long lastDrawTime = 0;

    Timer timer = new Timer();

    public Panel(){
        this.setPreferredSize(new Dimension(screenWidth,screenHeight));
        this.setBackground(new Color(40,40,40));
        this.setLayout(new GridLayout(maxRow,maxCol));

        for (int i = 0; i < maxRow ; i++) {
            for (int j = 0; j < maxCol ; j++) {
             nodeMatrix[i][j] = new Node(this,i,j);
             this.add(nodeMatrix[i][j]);
            }
        }

        setStartNode(8,1);
        setGoalNode(14,28);
        findCostOnNodes();
    }

    public void setStartNodeStateNull(){
        startNode=null;
    }

    public void setGoalNodeStateNull(){
        goalNode=null;
    }

    private void clearStartNode(){
        for (int i = 0; i < maxRow ; i++) {
            for (int j = 0; j < maxCol ; j++) {
                if(nodeMatrix[i][j].nodeState==NodeState.START){
                    nodeMatrix[i][j].rightClickEvent();
                }
            }
        }
        setStartNodeStateNull();
    }

    private void clearGoalNode(){
        for (int i = 0; i < maxRow ; i++) {
            for (int j = 0; j < maxCol ; j++) {
                if(nodeMatrix[i][j].nodeState==NodeState.GOAL){
                    nodeMatrix[i][j].rightClickEvent();
                }
            }
        }
        setGoalNodeStateNull();
    }

    public void setStartNode(int row,int col){
        clearStartNode();
        nodeMatrix[row][col].setAsStart();
        startNode = nodeMatrix[row][col];
        currentNode=startNode;
    }

    public void setGoalNode(int row,int col){
        clearGoalNode();
        nodeMatrix[row][col].setAsGoal();
        goalNode = nodeMatrix[row][col];
    }

    public void findCostOnNodes(){
        if(startNode==null || goalNode==null){
            return;
        }
        for (int i = 0; i < maxRow ; i++) {
            for (int j = 0; j < maxCol ; j++) {
                getNodeCost(nodeMatrix[i][j]);
            }
        }
    }

    private void getNodeCost(Node node){
        // Get gCost (Distance from startNode)
        int xDistance = Math.abs(node.col-startNode.col);
        int yDistance = Math.abs(node.row-startNode.row);
        node.gCost = xDistance+yDistance;

        // Get hCost (Distance from goalNode)
        xDistance = Math.abs(node.col-goalNode.col);
        yDistance = Math.abs(node.row-goalNode.row);
        node.hCost = xDistance+yDistance;

        node.fCost = node.gCost+node.hCost;

    }

    public void clearChecked(){

        timer.cancel();
        timer = new Timer();

        openList.clear();
        checkedList.clear();
        goalReached=false;
        this.totalIterations=0;
        this.lastDrawTime=0;
        currentNode=startNode;
        openList.add(currentNode);
        for (int i = 0; i < maxRow ; i++) {
            for (int j = 0; j < maxCol ; j++) {
                nodeMatrix[i][j].unsetAsChecked();
            }
        }
    }

    public void clearSolid(){
        clearChecked();
        for (int i = 0; i < maxRow ; i++) {
            for (int j = 0; j < maxCol ; j++) {
                if(nodeMatrix[i][j].nodeState==NodeState.SOLID){
                    nodeMatrix[i][j].rightClickEvent();
                }
            }
        }

    }

    boolean isDiagonal=false;
    public void search(){
        if(startNode==null || goalNode==null){
            return;
        }
        if(!goalReached){
            this.totalIterations+=1;
            int row = currentNode.row , col= currentNode.col;

            checkedList.add(currentNode);
            openList.remove(currentNode);

            // Add Adjacent Open Nodes to the list
            if(row+1<maxRow){
                openNode(nodeMatrix[row+1][col]);
            }
            if(row-1>=0){
                openNode(nodeMatrix[row-1][col]);
            }
            if(col+1<maxCol){
                openNode(nodeMatrix[row][col+1]);
            }
            if(col-1>=0){
                openNode(nodeMatrix[row][col-1]);
            }

            if(isDiagonal){
                if(row+1<maxRow && col-1>=0){
                    openNode(nodeMatrix[row+1][col-1]);
                }

                if(row-1>=0 && col-1>=0){
                    openNode(nodeMatrix[row-1][col-1]);
                }

                if(row-1>=0 && col+1<maxCol){
                    openNode(nodeMatrix[row-1][col+1]);
                }

                if(row+1<maxRow && col+1<maxCol){
                    openNode(nodeMatrix[row+1][col+1]);
                }
            }


            // Find the best node
            // Just use a heap bruh
            int bestNodeIndex=-1, bestNodefCost=99999;

            for(int i=0;i< openList.size();i++){
                if(openList.get(i).fCost<bestNodefCost){
                    bestNodeIndex=i;
                    bestNodefCost=openList.get(i).fCost;
                } else if (openList.get(i).fCost==bestNodefCost) {
                    if(openList.get(i).gCost<openList.get(bestNodeIndex).gCost){
                        bestNodeIndex=i;
                    }
                }
            }

            if(bestNodeIndex!=-1){
                currentNode = openList.get(bestNodeIndex);
                currentNode.setAsChecked();
            }

//            if(currentNode==goalNode){
//                goalReached=true;
//            }

        }
    }

    public void drawPath(){
        currentNode=goalNode.parent;
        int totalSteps=0;
        while (currentNode!=startNode){
            currentNode.setAsPath();
            currentNode=currentNode.parent;
            totalSteps+=1;
        }
        System.out.println("Total Steps: "+totalSteps);
    }

    private void openNode(Node node){
        if(node==goalNode){
            goalReached=true;
            node.parent=currentNode;
            drawPath();
            return;
        }
        if(!node.opened && node.nodeState==NodeState.OPEN){
            node.setAsOpen();
            node.parent = currentNode;
            openList.add(node);
        }

    }


}
