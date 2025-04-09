import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class MazeGroup {
    private List<Maze> nodeList;

    public MazeGroup() {
        this.nodeList = new ArrayList<>();
    }

    public void setupTestNodes() {
        Maze nodeA = new Maze(80, 80);
        Maze nodeB = new Maze(160, 80);
        Maze nodeC = new Maze(80, 160);
        Maze nodeD = new Maze(160, 160);
        Maze nodeE = new Maze(208, 160);
        Maze nodeF = new Maze(80, 320);
        Maze nodeG = new Maze(208, 320);

        // set up the connections between nodes (neighbors)
        nodeA.setNeighbor(Constants.RIGHT, nodeB);
        nodeA.setNeighbor(Constants.DOWN, nodeC);
        nodeB.setNeighbor(Constants.LEFT, nodeA);
        nodeB.setNeighbor(Constants.DOWN, nodeD);
        nodeC.setNeighbor(Constants.UP, nodeA);
        nodeC.setNeighbor(Constants.RIGHT, nodeD);
        nodeC.setNeighbor(Constants.DOWN, nodeF);
        nodeD.setNeighbor(Constants.UP, nodeB);
        nodeD.setNeighbor(Constants.LEFT, nodeC);
        nodeD.setNeighbor(Constants.RIGHT, nodeE);
        nodeE.setNeighbor(Constants.LEFT, nodeD);
        nodeE.setNeighbor(Constants.DOWN, nodeG);
        nodeF.setNeighbor(Constants.UP, nodeC);
        nodeF.setNeighbor(Constants.RIGHT, nodeG);
        nodeG.setNeighbor(Constants.UP, nodeE);
        nodeG.setNeighbor(Constants.LEFT, nodeF);

        // add all nodes to the list
        nodeList.add(nodeA);
        nodeList.add(nodeB);
        nodeList.add(nodeC);
        nodeList.add(nodeD);
        nodeList.add(nodeE);
        nodeList.add(nodeF);
        nodeList.add(nodeG);
    }

    public List<Maze> getNodeList() {
        return nodeList;
    }

    public void render(Graphics g) {
        for (Maze node : nodeList) {
            node.render(g);
        }
    }
}
