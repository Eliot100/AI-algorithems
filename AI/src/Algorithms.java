import java.util.Iterator;

/**
 * @author Eli and Dvir
 * in this class we write the Variable Elimination algorithm
 */
public class Algorithms {
	/**
	 * @param st - the String query
	 * @return String that contain the query answer, comma
	 * with The number of connection operations required by the algorithm to answer the query, comma, 
	 * and then the number of multiplication operations required by the algorithm.
	 * like:  
	 * 0.28417,7,16
	 * 0.84902,7,12
	 */
	public static String VariableElimination( String st) {
		boolean Dbag = false; // true || false
		String ans ="";
		boolean flag1 = true;
		boolean flag2 = true;
		boolean flag3 = true;
		int indexLine = 0, indexEndP = 0, indexeq = 0;
		for (int i = 0; flag3  ; i++) {
			if (flag1 && st.charAt(i) == '=') {
				indexeq = i;
				flag1 =  false;
			}
			else if (!flag1 && flag2 && st.charAt(i)== '|' ) {
				indexLine = i;
				flag2 =  false;
			} 
			else if (!flag2 && st.charAt(i)== ')') {
				indexEndP = i;
				flag3 =  false;
			}
		}
		Node target = Ex1.BN.get(""+st.substring(2, indexeq));
		String valTarget = st.substring(indexeq+1, indexLine);
		String[] GivenPs = st.substring(indexLine+1, indexEndP).split(",");
		String[] toEliminate = st.substring( indexEndP+2, st.length() ).split("-");
		String[] GivenNodes = new String[GivenPs.length];
		String[] GivenValsByNode = new String[GivenPs.length];
		for (int i = 0; i < GivenPs.length; i++) {
			String[] Given = GivenPs[i].split("=");
			GivenNodes[i] = Given[0];
			GivenValsByNode[i] = Given[1];
		}
		
		if(Dbag) {
			System.out.println("Node target : "+target.name);
			System.out.println("value Target : "+valTarget);
			System.out.print("GivenNodes : ");
			for (int i = 0; i < GivenNodes.length; i++) {
				System.out.print(GivenNodes[i]+" ");
			}
			System.out.print("\nGivenValsByNode : ");
			for (int i = 0; i < GivenValsByNode.length; i++) {
				System.out.print(GivenValsByNode[i]+" ");
			}
			System.out.print("\ntoEliminate : ");
			for (int i = 0; i < toEliminate.length; i++) {
				System.out.print(toEliminate[i]+" ");
			}
			System.out.print("\nGivenNodes : ");
			for (int i = 0; i < GivenNodes.length; i++) {
				System.out.print(GivenNodes[i]+" ");
			}
			System.out.println("\n");
		}
		
		Factor[] arrayF = new Factor[Ex1.BN.size()];
		Iterator<Node> it = Ex1.BN.iteretor();
		int cont = 0;
		while(it.hasNext()) {
			Node tempNode = it.next();
			arrayF[cont] = tempNode.CTPtoFactor();
			if(Factor.contains(arrayF[cont].dependent,target.name))
				arrayF[cont].removeGivens(target.name, valTarget);
			for (int i = 0; i < GivenNodes.length; i++) {
				if(Factor.contains(arrayF[cont].dependent, GivenNodes[i]))
					arrayF[cont].removeGivens(GivenNodes[i], GivenValsByNode[i]);
			}
			

			for (int i = 0; i < arrayF[cont].switchByVal.length; i++) {
				System.out.print(arrayF[cont].switchByVal[i]+",");
			}
			System.out.println();
//			arrayF[cont].print();
			cont++;
		}
		
		
		
//		for (int i = 0; i < toEliminate.length; i++) {
//			boolean[] flags = ifEliminate(arrayF, toEliminate[i]);
//			cont = 0;
//			for (int j = 0; j < flags.length; j++) {
//				if(flags[j] == true)
//					cont++;
//			}
//			Factor[] Factors2Eliminate = new Factor[cont];
//			cont = 0;
//			for (int j = 0; j < arrayF.length; j++) {
//				if(flags[j]) {
//					Factors2Eliminate[cont] = arrayF[j];
//					cont++;
//				}
//				
//			}
//		}
		return ans;
	}
	
	
	/**
	 * We dosn't need to do this algorithm at the moment
	 * @param st - the String query
	 * @return String - the query answer.
	 */
	public static String BayesBall( String st) {
		String ans ="\n";

		return ans;
	}
	
	private static boolean[] ifEliminate( Factor[] allFactors, String nodeNameToEliminate) {
		boolean[] flags = new boolean[allFactors.length];
		for (int i = 0; i < allFactors.length; i++) {
			for (int j = 0; flags[i] && j < allFactors[i].dependent.length; j++) {
				if(allFactors[i].dependent[j] == nodeNameToEliminate)
					flags[i] = true;
			}
		}
		
		return flags;
	}
}
