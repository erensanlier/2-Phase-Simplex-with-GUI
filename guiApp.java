

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;


import javax.swing.*;

@SuppressWarnings("serial")
public class guiApp extends JFrame{

	public double[] objectiveF;
	public double[] rightHS;
	public double[][] leftHS;
	private JLabel value = new JLabel("");
	private JPanel panel;
	private JPanel panel2;
	private JPanel panel3;
	private JPanel panel4;
	ArrayList<String> equations;


	public guiApp() {

		createView();

		pack();
		setLocationRelativeTo(null);

		setTitle("Two Phase Simplex Method Calculator");
		setDefaultCloseOperation(EXIT_ON_CLOSE);

	}
	

	private void createView() {

		panel = new JPanel();
		panel2 = new JPanel();
		panel3 = new JPanel();
		panel4 = new JPanel();
		panel.setBackground(Color.WHITE);
		panel2.setBackground(Color.WHITE);
		panel3.setBackground(Color.WHITE);
		panel4.setBackground(Color.WHITE);
		getContentPane().add(panel, BorderLayout.NORTH);

		getObjectiveFunction();
	}



	private void getObjectiveFunction() {

		JLabel objFunc = new JLabel("Please enter the objective function as input (a b c ...) for the function (ax1 + bx2 + cx3 ... = Z) : ");
		JTextField objFuncInput = new JTextField();
		objFuncInput.setPreferredSize(new Dimension(300, 30));


		panel.add(objFunc);
		panel.add(objFuncInput);

		JButton submitObj = new JButton("Send Objective Funtion");


		submitObj.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				objectiveF = null;
				String objInput = objFuncInput.getText();
				String[] temp = objInput.split(" ");
				if(!(temp.length > 1)) {
					objFuncInput.setText("Enter at least 2 variables");
					return;
				}

				objectiveF = new double[temp.length];
				for(int i = 0; i < temp.length; i++) {
					try {		
						objectiveF[i] = Double.parseDouble(temp[i]);
					}catch (NumberFormatException e1){
						objFuncInput.setText("Please enter doubles only!");
						objectiveF = null;
						break;
					}
				}
				if(objectiveF.length == temp.length && objectiveF.length > 1) {
					submitObj.setEnabled(false);
					objFuncInput.setEditable(false);
					getAnB();
				}else getObjectiveFunction();
			}

		});

		panel.add(submitObj);
	}

	private void getAnB() {

		//New panel


		JLabel anbInst = new JLabel("Fill an array with row size " + objectiveF.length + " plus corresponding RHS value (for ax1 + bx2 ≥ RHS, enter 'a b  RHS').");

		String e = "";

		for(int i = 0; i < objectiveF.length; i++) {
			e += ((char)('a' + i)) + " ";
		}
		e += "RHS";
		
		JTextField anbInput = new JTextField(e, 20);

		//JButton to add equation to ArrayList

		equations = new ArrayList<String>();
		JButton addEq = new JButton("Add");

		addEq.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				String temp = anbInput.getText();

				if(temp.split(" ").length != objectiveF.length + 1) {
					anbInput.setText("Size didn't match!");
					return;
				}
				for(String i : temp.split(" ")) {
					try {
						Double.parseDouble(i);
					}catch(NumberFormatException z) {
						anbInput.setText("Only doubles!");
						return;
					}

				}
				if(equations.contains(temp)) {
					anbInput.setText("Try different equation! This one exists.");
					return;
				}else {
					equations.add(temp);
					anbInput.setText("Added!");
				}

				System.out.println(equations.toString());
			}
		});

		//done button
		JButton done = new JButton("Done");

		done.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				done.setEnabled(false);
				addEq.setEnabled(false);

				leftHS = new double[equations.size()][objectiveF.length];
				rightHS = new double[equations.size()];

				String[] temp;
				for(int i = 0; i < equations.size(); i++) {
					for(int j = 0; j < objectiveF.length; j++) {
						temp = equations.get(i).split(" ");
						leftHS[i][j] = Double.parseDouble(temp[j]);
					}
				}

				for(int i = 0; i < equations.size(); i++) {
					temp = equations.get(i).split(" ");
					rightHS[i] = Double.parseDouble(temp[objectiveF.length]);

				}

				twoPhaseSimplex lp;
				try {
					lp = new twoPhaseSimplex(leftHS, rightHS, objectiveF);
				}
				catch (ArithmeticException z) { 
					value.setText(z.toString());
					return;
				}
				value.setText("Value = " + lp.value());

			}
		});

		//current Text
		String textCur = "";

		for(int i = 0; i < objectiveF.length - 1; i++) {
			textCur += (objectiveF[0] + " * x" + (i + 1) + " + ");
		}textCur += (objectiveF[objectiveF.length - 1] + " * x" + (objectiveF.length) + " = Z");

		JLabel currents = new JLabel(textCur);


		//setup panels

		getContentPane().add(panel2, BorderLayout.CENTER);
		panel2.add(currents);

		getContentPane().add(panel3, BorderLayout.AFTER_LAST_LINE);
		panel3.add(anbInst);
		panel3.add(anbInput);
		panel3.add(addEq);
		panel3.add(done);
		


		getContentPane().add(panel4, BorderLayout.EAST);
		panel4.add(value);

		


		pack();



	}


}
