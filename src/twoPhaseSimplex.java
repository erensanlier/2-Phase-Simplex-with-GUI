

import javax.swing.SwingUtilities;

public class twoPhaseSimplex {

	//Fields:
	private int eqN; //Constrains quantity
	private int orgN; //Original Value quantity
	private double[][] map;
	private int[] basis; 
	private static final double E = 1.0E-8; // epsilon

	//Main function to run GUI Application
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new guiApp().setVisible(true);
			}
		});
	}

	/* Constructor have been implemented to set up everything and do all the work. 
	 * Whenever a new twoPhaseSimplex object is created;
	 * all the calculations take place one by one. 
	 * After that the only thing we have to do is to call our public value() method.
	 */
	
	public twoPhaseSimplex(double[][] leftHS, double[] rightHS, double[] objectiveF) {

		eqN = rightHS.length;
		orgN = objectiveF.length;
		map = new double[eqN+2][orgN+2*eqN+1];

		//Filling the map to work on
		fillMap(map, leftHS, rightHS, objectiveF);

		//Form basis array
		basis();

		//First phase operations
		firstPhase();

		//Second phase operations
		secondPhase();

	}

	private void fillMap(double[][] map, double[][] leftHS, double[] rightHS, double[] objectiveF) {

		//Fill LHS part
		for (int i = 0; i < eqN; i++) {
			for (int j = 0; j < orgN; j++) {
				map[i][j] = leftHS[i][j];	
			}
		}

		for (int i = 0; i < eqN; i++) {
			map[i][orgN+i] = 1;
		}

		//Fill RHS part
		for (int i = 0; i < eqN; i++) {
			map[i][orgN+2*eqN] = rightHS[i];
		}

		//Fill objective function part
		for (int j = 0; j < orgN; j++) {
			map[eqN][j] = objectiveF[j];
		}

		negativeRHSCheck(rightHS);

		// artificial variables form initial basis
		for (int i = 0; i < eqN; i++) {
			map[i][orgN+eqN+i] = 1;
		}	
		for (int i = 0; i < eqN; i++) {
			map[eqN+1][orgN+eqN+i] = -1;
		}
		for (int i = 0; i < eqN; i++) {
			takePivot(i, orgN+eqN+i);
		}
			

	}

	// if negative RHS, multiply by -1
	private void negativeRHSCheck(double[] b) {
		
		for (int i = 0; i < eqN; i++) {
			if (b[i] < 0) {
				for (int j = 0; j <= orgN+2*eqN; j++)
					map[i][j] = -map[i][j];
			}
		}
	}

	//Fill basis array
	private void basis() {
		basis = new int[eqN];
		for (int i = 0; i < eqN; i++) {
			basis[i] = orgN + eqN + i;
		}
	}

	//Phase 1: Basic feasible solution
	private void firstPhase() {
		while (true) {

			// find entering column q
			int q = artificiallowestIndex();
			if (q == -1) break;  // optimal

			// find leaving row p
			int p = minRatioRule(q);
			assert p != -1 : "Entering column = " + q;

			// pivot
			takePivot(p, q);

			// update basis
			basis[p] = q;
			// show();
		}
		if (map[eqN+1][orgN+2*eqN] > E) throw new ArithmeticException("Infeasible LP");
	}


	// Phase 2: Simplex algorithm
	private void secondPhase() {
		while (true) {

			// find entering column q
			int q = lowestIndex();
			if (q == -1) break;  // optimal

			// find leaving row p
			int p = minRatioRule(q);
			if (p == -1) throw new ArithmeticException("Unbounded LP");

			// pivot
			takePivot(p, q);

			// update basis
			basis[p] = q;
		}
	}

	private int artificiallowestIndex() {
		for (int j = 0; j < orgN+eqN; j++)
			if (map[eqN+1][j] > E) return j;
		return -1;
	}

	private int lowestIndex() {
		for (int j = 0; j < orgN+eqN; j++)
			if (map[eqN][j] > E) return j;
		return -1;
	}


	// To find row p
	private int minRatioRule(int a) {
		int p = -1;
		for (int i = 0; i < eqN; i++) {
			if (map[i][a] <= E) continue;
			else if (p == -1) p = i;
			else if ((map[i][orgN+2*eqN] / map[i][a]) < (map[p][orgN+2*eqN] / map[p][a])) p = i;
		}
		return p;
	}

	
	private void takePivot(int x, int y) {

		// everything but row p and column q
		for (int i = 0; i <= eqN+1; i++)
			for (int j = 0; j <= orgN+2*eqN; j++)
				if (i != x && j != y) map[i][j] -= map[x][j] * map[i][y] / map[x][y];

		// zero out column q
		for (int i = 0; i <= eqN+1; i++)
			if (i != x) map[i][y] = 0;

		// scale row p
		for (int j = 0; j <= orgN+2*eqN; j++)
			if (j != y) map[x][j] /= map[x][y];
		map[x][y] = 1;
	}

	// Finally returning optimal value
	public double value() {
		return -map[eqN][orgN+2*eqN];
	}

}