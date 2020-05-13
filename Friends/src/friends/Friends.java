package friends;

import structures.Queue;
import structures.Stack;

import java.util.*;

public class Friends {

	/**
	 * Finds the shortest chain of people from p1 to p2.
	 * Chain is returned as a sequence of names starting with p1,
	 * and ending with p2. Each pair (n1,n2) of consecutive names in
	 * the returned chain is an edge in the graph.
	 * 
	 * @param g Graph for which shortest chain is to be found.
	 * @param p1 Person with whom the chain originates
	 * @param p2 Person at whom the chain terminates
	 * @return The shortest chain from p1 to p2. Null if there is no
	 *         path from p1 to p2
	 */
	public static ArrayList<String> shortestChain(Graph g, String p1, String p2) {
		
		if (g == null || p1 == null || p2 == null) return null; // Any null values
		
		//Lower case strings
        p1 = p1.toLowerCase(); 
        p2 = p2.toLowerCase();
        
        if (g.map.get(p1) == null || g.map.get(p2) == null) return null; // Values do not exist
        
        ArrayList<String> shortestChain = new ArrayList<>(); // Array list to be returned
        
        if (p1.equals(p2)) {
        	
        	shortestChain.add(g.members[g.map.get(p1)].name);
            return shortestChain;
        }

        Queue<Integer> q = new Queue<>(); 
        int[] distance = new int[g.members.length];
        int[] predecessor = new int[g.members.length];
        boolean[] visited = new boolean[g.members.length];

        for (int index = 0; index < visited.length; index++) {
            visited[index] = false;
            distance[index] = Integer.MAX_VALUE;
            predecessor[index] = -1;
        }

        int startIndex = g.map.get(p1);
        visited[startIndex] = true;
        distance[startIndex] = 0;
        q.enqueue(startIndex);

        while (!q.isEmpty()) {
            
            int vertex = q.dequeue(); 
            Person p = g.members[vertex];
            Friend link = p.first; // First friend
           
            while (link != null) {
                
            	int num = link.fnum;

                if (!visited[num]) {
                    
                	distance[num] = (distance[vertex] + 1); 
                	predecessor[num] = vertex;
                    visited[num] = true;
                    q.enqueue(num); 
                }
                
                link = link.next;
            }
        }

        Stack<String> list = new Stack<>();
        int location = g.map.get(p2);

        if (!visited[location]) return null; // P2 not found

        while(location != -1) { //Pushing locations to stack
        	list.push(g.members[location].name);
            location = predecessor[location];
        }

        while (!list.isEmpty()) { // Popping from stack in correct order
        	shortestChain.add(list.pop());
        }

        return shortestChain;
    }
	
	/**
	 * Finds all cliques of students in a given school.
	 * 
	 * Returns an array list of array lists - each constituent array list contains
	 * the names of all students in a clique.
	 * 
	 * @param g Graph for which cliques are to be found.
	 * @param school Name of school
	 * @return Array list of clique array lists. Null if there is no student in the
	 *         given school
	 */
	public static ArrayList<ArrayList<String>> cliques(Graph g, String school) {

		if (g == null || school == null) return null;

		ArrayList<ArrayList<String>> cliques = new ArrayList<>();
		school = school.toLowerCase();
		boolean[] visited = new boolean[g.members.length];

		for (int i = 0; i < visited.length; i++) {
			visited[i] = false; // Initialize
		}

		for (Person links : g.members) {

			if (!visited[g.map.get(links.name)] && links.school != null && links.school.equals(school)) {

				Queue<Integer> q = new Queue<>();
				ArrayList<String> members = new ArrayList<>();

				int startIndex = g.map.get(links.name);
				visited[startIndex] = true;

				q.enqueue(startIndex);
				members.add(links.name);

				while (!q.isEmpty()) {

					int vertex = q.dequeue();
					Person p = g.members[vertex];

					for (Friend fri = p.first; fri != null; fri = fri.next) {
						
						int num = fri.fnum;
						Person per = g.members[num];

						if (!visited[num] && per.school != null && per.school.equals(school)) {
							
							visited[num] = true;
							q.enqueue(num);
							members.add(g.members[num].name);
						}
					}
				}

				cliques.add(members);
			}
		}

		return cliques;
	}
	
	/**
	 * Finds and returns all connectors in the graph.
	 * 
	 * @param g Graph for which connectors needs to be found.
	 * @return Names of all connectors. Null if there are no connectors.
	 */
	public static ArrayList<String> connectors(Graph g) {

		boolean[] visited = new boolean[g.members.length]; 
		int[] prev = new int[g.members.length];
		ArrayList<String> connectors = new ArrayList<>();

		for (Person links : g.members) {
			
			if (!visited[g.map.get(links.name)]) {
				
				int[] num = new int[g.members.length];
				dfs(g.map.get(links.name), g.map.get(links.name), g, visited, num, prev, connectors);
			}
		}
		
		for (int i = 0; i < connectors.size(); i++) {
			
			Friend fri = g.members[g.map.get(connectors.get(i))].first;

			int count = 0;
			while (fri != null) {
				fri = fri.next;
				count++;
			}

			if (count == 0 || count == 1) connectors.remove(i);
		}

		for (Person p : g.members) {
			
			if ((p.first.next == null && !connectors.contains(g.members[p.first.fnum].name))) 
				connectors.add(g.members[p.first.fnum].name);
		}

		return connectors;
	}

	private static void dfs(int vertex, int start, Graph g, boolean[] visited, int[] num, int[] prev, ArrayList<String> connectors) {
		
		Person p = g.members[vertex];
		visited[g.map.get(p.name)] = true;
		int count = sizeArr(num) + 1;

		if (num[vertex] == 0 && prev[vertex] == 0) {
			num[vertex] = count;
			prev[vertex] = num[vertex];
		}

		for (Friend ptr = p.first; ptr != null; ptr = ptr.next) {
			
			if (!visited[ptr.fnum]) {
				
				dfs(ptr.fnum, start, g, visited, num, prev, connectors);

				if (num[vertex] <= prev[ptr.fnum]) {
					
					if (Math.abs(num[vertex] - prev[ptr.fnum]) < 1 && Math.abs(num[vertex] - num[ptr.fnum]) <= 1
							&& prev[ptr.fnum] == 1 && vertex == start) {
						continue;
					}

					if (num[vertex] <= prev[ptr.fnum] && (vertex != start || prev[ptr.fnum] == 1)) {
						
						if (!connectors.contains(g.members[vertex].name)) 
							
							connectors.add(g.members[vertex].name);
					}
				}
				
				else {
					prev[vertex] = Math.min(prev[vertex], prev[ptr.fnum]);
				} 
				
			} 
			
			else {
				prev[vertex] = Math.min(prev[vertex], num[ptr.fnum]);
			}
		}
	}
	
	private static int sizeArr(int[] arr) {
		int count = 0;
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] != 0) {
				count++;
			}
		}
		return count;
	}


}
