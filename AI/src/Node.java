import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
/**
 * @author Eli Ruvinov
 * In this class we create the Node that we using in the Bayesian Network
 */
public class Node {

	String[] ParentsNames;   
	Node[] Parents;     
	String[] VarValues;    
	String name;   
	double[][] CPT;  
	int numOfParents;  
	File cptText;
	int[] PrentSwithVal;
	
	/**
	 * This function initialize Node by: name
	 */
	public Node(String name) {
		this.name = name;
		this.VarValues = null;
	}
	
	/**
	 * @return String - the node name 
	 */
	public String toSrting() {
		return this.name;
	}
	
	/**
	 * This function initialize the CPT of this node
	 * @param br - is BufferedReader which contains lines containing probabilities by the Nodes parents Values  
	 */
	public void getCPT(BufferedReader br) {
		try {
			boolean Dbag= false; // true || false
			int x = 1;
			for (int i = 0; i < this.numOfParents; i++) {
				x *= this.Parents[i].VarValues.length;
			}
			this.CPT = new double[x][this.VarValues.length];
			String st = br.readLine();
			
			for (int RowNum = 0; RowNum < x; RowNum++) {
				if(Dbag){System.out.println(st);}
				this.nextCPT_line(st);
				if(RowNum !=x-1)
					st = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("");
		}
	}
	
	/**
	 * This function sets a row in the CTP matrix
	 * @param st - String that contain this node probability
	 */
	public void nextCPT_line( String st) {
		boolean Dbag = false; // true || false
		String[] tempWordArray = st.split(",");
		if (tempWordArray.length != this.numOfParents+2*(this.VarValues.length-1)) {
			throw new RuntimeException("This row dosn't present a part of "+this.name+" CPT.\ngot: "+st );
		}
		
		int SwithValueIndex = 1;
		int[] PrentnumSwithValueIndex = new int[this.numOfParents];
		for (int i = this.numOfParents-1; i >= 0; i--) {
			PrentnumSwithValueIndex[i] = SwithValueIndex;
			SwithValueIndex *= this.Parents[i].VarValues.length;
		}
		this.PrentSwithVal = PrentnumSwithValueIndex;
		if(Dbag){System.out.println(this.name);}
		if(Dbag){System.out.println(this.numOfParents);}
		int RowNum = 0;
		String RowString = "";
		for (int i = 0; i < this.numOfParents; i++) {
			for (int j = 0; j < this.Parents[i].VarValues.length; j++) {
				if(tempWordArray[i].equals(Ex1.BN.get(this.ParentsNames[i]).VarValues[j])){
					RowNum += j*PrentnumSwithValueIndex[i];
					RowString +=PrentnumSwithValueIndex[i]+" ";
				}
			}
		}
		
		double sum = 0;
		for (int i = 0; i < this.VarValues.length-1; i++) {
			if(Dbag){System.out.println(RowNum);}
			if(Dbag){System.out.println(RowString);}
			this.CPT[RowNum][i] = Double.parseDouble(tempWordArray[this.numOfParents+2*i+1]);
			sum = sum +this.CPT[RowNum][i];
		}
		
		double lastValProb =1-sum;
		if(String.valueOf(lastValProb).length() > 10 ) {
			if(String.valueOf(lastValProb).charAt(10) == '9') {
				lastValProb+= 0.00000001;
			}
			lastValProb = ((int) (lastValProb*100000000))/100000000.0;
		}
		
		this.CPT[RowNum][this.VarValues.length-1] = lastValProb;
	}
	
	public static void printCPT( String nodeName) {
		for (int i = 0; i < Ex1.BN.get(nodeName).CPT.length; i++) {
			System.out.print("[");
			for (int j = 0; j < Ex1.BN.get(nodeName).CPT[0].length; j++) {
				System.out.print(Ex1.BN.get(nodeName).CPT[i][j]);
				if(j != Ex1.BN.get(nodeName).CPT[0].length-1)
					System.out.print(", ");
			}
			System.out.println("]");
		}
	}
	
	public Factor CTPtoFactor() {
		String[] NodeGiven = new String[this.numOfParents+1];
		NodeGiven[0] = this.name;
		for (int i = 1; i < this.ParentsNames.length; i++) {
			NodeGiven[i] = this.ParentsNames[i];
		}
		double[] probabilities = new double[this.CPT[0].length*this.CPT.length];
		for (int i = 0; i < this.CPT[0].length; i++) 
			for (int j = 0; j < this.CPT.length; j++) 
				probabilities[j*this.VarValues.length+i] = this.CPT[j][i];
		
		int[] switchByVal = new int[this.numOfParents+1];
		switchByVal[0]= 1;
		for (int i = 0; i < this.numOfParents; i++) {
			switchByVal[i+1]= this.PrentSwithVal[i];
		}
		if(this.numOfParents > 0) 
			switchByVal[0]= switchByVal[1]*this.VarValues.length;
		else
			switchByVal[0]= 1;
		Factor f = new Factor();
		f.known = NodeGiven;
		f.switchByVal = switchByVal;
		f.unknown = probabilities;
		return f;
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
