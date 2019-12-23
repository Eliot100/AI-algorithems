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
//		boolean Dbag = false; // true || false
		
		
		// Making variables for later use (From the input file queries) 
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
			removeUnwantedDependecies(arrayF[cont]);
			arrayF[cont].makeMatrix(findNumOfRows(arrayF[cont]));
//			Ex1.printArray(arrayF[cont].dependent);
//			for (int i = 0; i < arrayF[cont].matrix.length; i++) {
//				Ex1.printArray(arrayF[cont].matrix[i]);
//			}
			cont++;
		}
		
		for (int i = 0; i < toEliminate.length; i++) {
			boolean[] flags = ifEliminate(arrayF, toEliminate[i]);
			cont = 0;
			for (int j = 0; j < flags.length; j++) {
				if(flags[j] == true)
					cont++;
			}
			Factor[] Factors2Eliminate = new Factor[cont];
			cont = 0;
			for (int j = 0; j < arrayF.length; j++) {
				if(flags[j]) {
					Factors2Eliminate[cont] = arrayF[j];
					cont++;
				}
				
			}
		}
		
		Factor f = join(arrayF[4],arrayF[3]);
		return ans;
	}
	public static Factor joinAll(Factor[] farr, String toEliminate) 
	{
		Factor f1 = farr[0];
		for (int i = 1; i < farr.length; i++) {
			f1 = join(f1, farr[i]);
		}
		return f1.Eliminaton(f1, toEliminate);
	}

	private static Factor join(Factor f1, Factor f2) {
		removeUnwantedDependecies(f1);
		removeUnwantedDependecies(f2);
		Factor newfactor = new Factor();
		newfactor.dependent = get_unique(f1.dependent, f2.dependent, Factor.count_unique(f1.dependent, f2.dependent));
		newfactor.switchByVal = makeSwitch(newfactor.dependent, Factor.count_unique(f1.dependent, f2.dependent));
		int numOfRows = findNumOfRows(newfactor);
		newfactor.makeMatrix(numOfRows);
		for (int i = 0; i < newfactor.matrix.length; i++) {
			Ex1.printArray(newfactor.matrix[i]);
		}
		System.out.println();
		newfactor.probability = makeProbability(f1, f2, numOfRows, newfactor);
//		Ex1.printArray(newfactor.matrix[i]);
		
		
	
		return newfactor;
	}
	
	private static int findNumOfRows(Factor newfactor) {
		for (int i = 0; i < newfactor.switchByVal.length; i++) {
			if(newfactor.switchByVal[i] != 0 )
				return newfactor.switchByVal[i]*Ex1.BN.get(newfactor.dependent[i]).VarValues.length;
			else
				continue;
		}
		return 1;
	}
	

	private static double[] makeProbability(Factor f1, Factor f2, int ProbabilitySize, Factor newfactor) {
		double[] probability = new double[ProbabilitySize];
		for (int i = 0; i < probability.length; i++) {
			f1.printFactor();
//			System.out.println(f1.probability[1]);
//			System.out.println(f2.probability[1]);
			probability[i] = ProbByRow(i, f1, newfactor)*ProbByRow(i, f2, newfactor);
			//System.out.println(ProbByRow(i, f1, newfactor)+" "+ProbByRow(i, f2, newfactor));
		}
		System.out.println("probability:");
		for (int i = 0; i < probability.length; i++) {
			System.out.println(probability[i]);
		}
		return probability;
	}
	
	private static double ProbByRow(int rowProb, Factor f, Factor newfactor )//String[] row, String[] dependent ) 
	{
		int rowOldFactor = 0, colOldFactor = 0, colNewFactor = 0, counter = 0;
		for (; rowOldFactor < f.probability.length; rowOldFactor++) {
			for (; colOldFactor < f.dependent.length; colOldFactor++) {
				if(newfactor.matrix[rowProb][Factor.get_index_by_value(newfactor.dependent,f.dependent[colOldFactor])]!=f.matrix[rowOldFactor][colOldFactor])
					break;
				else
					counter++;

			}
			if(counter == f.dependent.length)
				return f.probability[rowOldFactor];
		}
		
		
//		for (; colOldFactor < f.matrix[0].length; colOldFactor++) {
//			for ( ; colNewFactor < newfactor.dependent.length; colNewFactor++) {
//				if(f.dependent[colOldFactor].equals(newfactor.dependent[colNewFactor]))
//					counter++;
//			}
//			if(counter == f.dependent.length)
//				return f.probability[colOldFactor];
////			if(f.matrix[row][].equals(newfactor.matrix[rowProb][col]))
////				;
//		}
		return 0;
	}
	
	private static int[] makeSwitch(String[] dependent, int size) {
		int[] switchByVal = new int[size];
		int switch0 = 1;
		for (int i = switchByVal.length-1; i >= 0; i--) {
			switchByVal[i] = switch0;
			switch0 *= Ex1.BN.get(dependent[i]).VarValues.length;
		}
		return switchByVal;
	}
	
	private static void removeUnwantedDependecies(Factor f1) 
	{
		boolean[] flags = getZerosSwitch(f1);
		int size = 0;
		for (int i = 0; i < flags.length; i++) {
			if(!flags[i])
				size++;
		}
		
		String[] dependents = new String[size];
		int counter = 0;
		for (int j = 0; j < f1.dependent.length; j++) {
			if(!flags[j]) {
				dependents[counter] = f1.dependent[j];
				
				counter++;
			}
		}
		
		f1.switchByVal = getNonZeroArr(f1, flags);

		f1.dependent = dependents;
	} 
	private static int[] getNonZeroArr(Factor f1, boolean[] flags) 
	{
		int counter = 0;
		for (int i = 0; i < flags.length; i++) {
			if(!flags[i]) 
				counter++;
		}
		int[] switchbByVal = new int[counter];
		counter = 0;
		for (int i = 0; i < flags.length; i++) {
			if(!flags[i]) {
				switchbByVal[counter] = f1.switchByVal[i];
				counter++;
			}
		}
		return switchbByVal;
		
	}
	private static boolean[] getZerosSwitch(Factor f1) 
	{
		boolean[] zeroCols = new boolean[f1.switchByVal.length];
		for (int i = 0; i < f1.switchByVal.length; i++) {
			if(f1.switchByVal[i] == 0)
				zeroCols[i] = true;
			else
				zeroCols[i] = false;
		}
		return zeroCols;
	}
	private static String[] get_unique(String[] s1, String[] s2, int size) 
	{
//		System.out.println("size: " + size);
		String[] newS = new String[size];
		int counter = 0;
//		System.out.println("newS = "+newS.length);
//		System.out.println("s1 = "+s1.length);
		for (int i = 0; i < s1.length; i++) {
			newS[i] = s1[i];
			counter = i+1;
		}
		for (int i = 0; i < s2.length; i++) {
			
			if(!Factor.contains(s1,s2[i])) 
			{
				newS[counter+i] = s2[i];
			}
		}
		return newS;
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
