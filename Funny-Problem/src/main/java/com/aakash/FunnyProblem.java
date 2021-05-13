package com.aakash;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

class Point{
    private int x;
    private int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}



public class FunnyProblem {

    private static List<List<Point>> finalPathList = new ArrayList<List<Point>>();

    public void addPathToFinalPathList(List<Integer> pathFromSourceToSink, int dimension) {
        List<Point> path = new ArrayList<Point>();
        double nodeNumber = 0;
        double x = 0;
        double y = 0;
        for(int j = 1; j < pathFromSourceToSink.size()-1; j++) {
            int i = pathFromSourceToSink.get(j);
            if(i % 2 == 0) {
                nodeNumber = Math.ceil(i / 2);
                x = Math.ceil(nodeNumber/ dimension);

                if((int)nodeNumber % dimension == 0) {
                    y = dimension;
                }
                else {
                    y = nodeNumber % dimension;
                }

                Point p = new Point((int)x, (int)y);
                path.add(p);
            }
        }

        finalPathList.add(path);
    }

    public int[][] generateResidualGraph(int[][] adajacentMatrix, List<Integer> pathFromSourceToSink, int minFlowFromPath){
        int i = 1;
        while(i < pathFromSourceToSink.size()) {
            adajacentMatrix[pathFromSourceToSink.get(i-1)][pathFromSourceToSink.get(i)] -= minFlowFromPath;
            adajacentMatrix[pathFromSourceToSink.get(i)][pathFromSourceToSink.get(i-1)] += minFlowFromPath;
            i++;
        }

        return adajacentMatrix;
    }

    public int getMinFlowFromShortestPath(int[][] adajacentMatrix, List<Integer> pathFromSourceToSink) {
        int minValue = Integer.MAX_VALUE;
        for(int i = 1; i < pathFromSourceToSink.size(); i++) {
            if(adajacentMatrix[pathFromSourceToSink.get(i-1)][pathFromSourceToSink.get(i)] < minValue)
                minValue = adajacentMatrix[pathFromSourceToSink.get(i-1)][pathFromSourceToSink.get(i)];
        }

        return minValue;
    }

    public List<Integer> getPath(int[] parentPath, int source, int sink){
        List<Integer> shortestPath = new ArrayList<>();
        shortestPath.add(0,sink);
        int temp = sink;
        while(parentPath[temp] != Integer.MIN_VALUE) {
            shortestPath.add(0,parentPath[temp]);
            temp = parentPath[temp];
        }

        return shortestPath;
    }

    public int[] getParentPathUsingBFS(int[][] adajacentMatrix, int dimension, int source) {

        List<Integer> path = new ArrayList<Integer>();
        int[] parentPath = new int[dimension];
        boolean[] visited = new boolean[dimension];
        Arrays.fill(visited, false);
        Arrays.fill(parentPath, Integer.MIN_VALUE);
        List<Integer> q = new ArrayList<>();
        q.add(source);
        visited[source] = true;

        int vis;
        while (!q.isEmpty())
        {
            vis = q.get(0);
            path.add(vis);
            q.remove(q.get(0));
            for(int i = 0; i < dimension; i++)
            {
                if (adajacentMatrix[vis][i] == 1 && (!visited[i]))
                {
                    q.add(i);
                    parentPath[i] = vis;
                    visited[i] = true;
                }
            }
        }
        return parentPath;
    }

    public int getFlow(int[][] adajacentMatrix, int dimension, int source, int sink) {
        int[] parentPath = getParentPathUsingBFS(adajacentMatrix, sink+1, source);
        List<Integer> pathFromSourceToSink = getPath(parentPath, source, sink);
        int flow = 0;
        int minFlowFromPath = 0;
        while(pathFromSourceToSink.contains(source)) {
            minFlowFromPath = getMinFlowFromShortestPath(adajacentMatrix, pathFromSourceToSink);
            flow += minFlowFromPath;
            adajacentMatrix = generateResidualGraph(adajacentMatrix, pathFromSourceToSink, minFlowFromPath);
            parentPath = getParentPathUsingBFS(adajacentMatrix, sink+1, source);
            addPathToFinalPathList(pathFromSourceToSink, dimension);
            pathFromSourceToSink = getPath(parentPath, source, sink);
        }
        return flow;
    }

    public int getMaxFlow(int n, List<Point> pointList) {
        int dimension = n * n * 2 + 2;
        int[][] adajacentMatrix = new int[dimension][dimension];

        for(int row = 0; row < dimension; row++)
            Arrays.fill(adajacentMatrix[row], 0);

        for(int i = 1; i < dimension - 2; i = i+2)
            adajacentMatrix[i][i+1] = 1;

        for(int i = 1; i < n + 1; i++) {
            if(i > 0) {
                adajacentMatrix[2*i][dimension-1] = 1;
                adajacentMatrix[2*(n-1)*n+2*i][dimension-1] = 1;
            }

            if(i < n) {
                adajacentMatrix[i*2*n + 2][dimension-1] = 1;
                adajacentMatrix[2*n*(i+1)][dimension-1] = 1;
            }
        }

        for(int i = 0; i < pointList.size(); i++) {
            Point p = pointList.get(i);
            adajacentMatrix[0][(p.getX()-1)*2*n + (2*(p.getY()-1))+1] = 1;
        }

        for(int i = 2; i < dimension-1; i = i+2) {
            if(i % (2*n) != 0) {
                adajacentMatrix[i][i+1] = 1;
            }

            if((i-2) % (2*n) != 0) {
                adajacentMatrix[i][i-3] = 1;
            }

            if(i - (2*n) > 0) {
                adajacentMatrix[i][i-(2*n)-1] = 1;
            }

            if(i + (2*n) < dimension-1) {
                adajacentMatrix[i][i+(2*n)-1] = 1;
            }
        }

        return getFlow(adajacentMatrix, n, 0, dimension-1);
    }


    public static void main(String[] args) {
        Scanner in = new Scanner(new BufferedReader(new InputStreamReader(System.in)));
        System.out.println("Enter the number the dimension");
        int n = in.nextInt();
        boolean flag = false;
        System.out.println("Enter the number of starting points");
        int m = in.nextInt();

        if(m <= n*n) {
            List<Point> pointList = new ArrayList<Point>();
            for(int i = 0; i < m; i++) {
                System.out.print("Enter Point "+ (i+1) +", x coordinate: ");
                int x = in.nextInt();
                System.out.print("Enter Point "+ (i+1) +", y coordinate: ");
                int y = in.nextInt();
                if(x < 1 || x > n || y < 1 || y > n)
                    flag = true;
                Point p = new Point(x, y);
                pointList.add(p);
            }

            if(flag)
                System.out.println("Wrong Input, UseCase:value of x,y >= 1 and x,y < value(number of dimnesion)");
            else {
                FunnyProblem f = new FunnyProblem();
                int maxFlow = f.getMaxFlow(n, pointList);

                if(m == maxFlow) {
                    System.out.println("YES, a solution exists.");
                    System.out.println("A solution to this problem (a set of vertex-disjoint paths) is:");
                    for(List<Point> li: finalPathList) {
                        System.out.print("PATH from (" + li.get(0).getX()+", " + li.get(0).getY() + "): ");
                        for(Point p : li) {
                            System.out.print("("+p.getX()+", "+p.getY()+") -> ");
                        }
                        System.out.print("end");
                        System.out.println("");
                    }
                }
                else
                    System.out.println("No, solution does not exists.");
            }
        }
        else
            System.out.println("Number of starting points should be <= n x n grid capacity, please enter the correct input");
    }
}