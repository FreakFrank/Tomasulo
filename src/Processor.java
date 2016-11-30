import java.util.ArrayList;
import java.util.Scanner;

public class Processor {
	int cacheLevel;
	int blockSize;
	static ArrayList<Cache> caches = new ArrayList<Cache>();
	static MainMemory mainMemory;
	int[] functionalUnits = new int[11];// 0->add, 1->addI, 2->multiply,
										// 3->load,4->store, 5 -> sub, 6-> nand,
										// 7-> beq, 8->jmp,9->jalr,10-> ret
	static int[] cyclesPerInst = new int[11];
	int[] constantFunctionalUnits = new int [11];
	static Object[][] scoreBoard;// same columns as in lecture 11
	int[] registersStatusTable = new int[7];// index 0->R1 and so on...
	int pipelineWidth;
	Object ROB[][];
	int head = 0;
	int tail = 0;
	ArrayList<Instruction> instructionBuffer = new ArrayList<Instruction>();
	int sizeOfInstructionBuffer;
	ArrayList<Instruction> allInstructions = new ArrayList<Instruction>();
	int cycle = 0;
	int commitedInstructions = 0;
	boolean write;
	boolean commit;
	boolean predictedBranch = true;
	static boolean actualBranch;
	int branchIssuedCycle;
	int mispredictionPointer;
	int RSrows;
	int scorebordrow;
	boolean firstime=true;
	int firstoperand;
	int secondoperand;
	String regA;
	String x;
	int nregA;
	String regB;
	String x2;
	int nregB;
	String imm;
	String regC;
	String x3;
	int nimm;
	int nregC;
	boolean changeAddress = false;
	int noOfInstructions = 0;

	public static void main(String[] args) {
		Processor p = new Processor();
		p.stage();
	}

	public Processor() {

		Scanner sc = new Scanner(System.in);
		System.out.println("Please enter the number of cache levels prefered");
		this.cacheLevel = sc.nextInt();
		System.out.println("Please enter the Block size in bytes");
		blockSize = sc.nextInt();
		System.out
				.println("Please enter the access time of the main memory in cycles");
		int memAccessTime = sc.nextInt();
		 for (int i = 0; i < cacheLevel; i++) {
		 System.out.println("Please enter the size of cache level "
		 + (i + 1) + " in bytes");
		 int size = sc.nextInt();
		 System.out
		 .println("Please enter the associativity of cache level"
		 + (i + 1)
		 +
		 ".\nThe options are : 1- For (entry equals 1) Direct mapped will be chosen.\n2- For (1 < entry < "
		 + (size / 2)
		 + ") Set associativity will be chosen.\n3- For entry equals ("
		 + size / blockSize + ") Full associativity will be chosen.");
		 int auhto = sc.nextInt();
		 System.out
		 .println("Please enter the writing policy if its a 'write back' or 'write through' in case of HIT");
		
		 String Hit = sc.nextLine();
		 Hit = sc.nextLine();
		 System.out
		 .println("Please enter the writing policy if its a 'write back' or 'write through' in case of MISS");
		 String Miss = sc.nextLine();
		 int wpHit = 0;
		 int wpMiss = 0;
		 if (Hit.equals("write back"))
		 wpHit = 1;
		 if (Miss.equals("write back"))
		 wpMiss = 1;
		 System.out.println("Please enter the access time in cycles");
		 int accessTime = sc.nextInt();
		 caches.add(new Cache(size, blockSize, auhto, wpHit, wpMiss,
		 accessTime));
		 }
//		int size1 = 32;
//		int size2 = 64;
//		int size3 = 128;
//		int assoc1 = 1;
//		int assoc2 = 2;
//		int assoc3 = 32;
//		int policy1 = 0;
//		int policy2 = 0;
//		int acc1 = 4;
//		int acc2 = 8;
//		int acc3 = 16;
//		caches.add(new Cache(size1, 4, assoc1, policy1, policy1, acc1));
//		caches.add(new Cache(size2, 4, assoc2, policy1, policy1, acc2));
//		caches.add(new Cache(size3, 4, assoc3, policy2, policy2, acc3));

		System.out.println("Please enter how many add funtional units");
		functionalUnits[0] = sc.nextInt();
		System.out.println("Please enter the cost of the add");
		cyclesPerInst[0] = sc.nextInt();
		System.out.println("Please enter how many add immediate funtional units");
		functionalUnits[1] = sc.nextInt();
		System.out.println("Please enter the cost of the addi");
		cyclesPerInst[1] = sc.nextInt();
		System.out.println("Please enter how many multiply funtional units");
		functionalUnits[2] = sc.nextInt();
		System.out.println("Please enter the cost of the multiply");
		cyclesPerInst[2] = sc.nextInt();
		System.out.println("Please enter how many load funtional units");
		functionalUnits[3] = sc.nextInt();
		System.out.println("Please enter the cost of the load");
		cyclesPerInst[3] = sc.nextInt();
		System.out.println("Please enter how many store funtional units");
		functionalUnits[4] = sc.nextInt();
		System.out.println("Please enter the cost of the store");
		cyclesPerInst[4] = sc.nextInt();
		System.out.println("Please enter how many sub funtional units");//
		functionalUnits[5] = sc.nextInt();
		System.out.println("Please enter the cost of the sub");
		cyclesPerInst[5] = sc.nextInt();
		System.out.println("Please enter how many nand funtional units");
		functionalUnits[6] = sc.nextInt();
		System.out.println("Please enter the cost of the nand");
		cyclesPerInst[6] = sc.nextInt();
		System.out.println("Please enter how many beq funtional units");
		functionalUnits[7] = sc.nextInt();
		System.out.println("Please enter the cost of the beq");
		cyclesPerInst[7] = sc.nextInt();
		System.out.println("Please enter how many jmp funtional units");
		functionalUnits[8] = sc.nextInt();
		System.out.println("Please enter the cost of the jmp");
		cyclesPerInst[8] = sc.nextInt();
		System.out.println("Please enter how many jalr funtional units");
		functionalUnits[9] = sc.nextInt();
		System.out.println("Please enter the cost of the jalr");
		cyclesPerInst[9] = sc.nextInt();
		System.out.println("Please enter how many ret funtional units");
		functionalUnits[10] = sc.nextInt();
		System.out.println("Please enter the cost of the ret");
		cyclesPerInst[10] = sc.nextInt();
		for (int i = 0; i < functionalUnits.length; i++) {
			constantFunctionalUnits[i] = functionalUnits[i];
		}
		int rows = 0;
		for (int i = 0; i < functionalUnits.length; i++) {
			rows += functionalUnits[i];
		}
		RSrows=rows;
		scoreBoard = new Object[rows][9];
		System.out.println("Please enter the pipeline width");
		pipelineWidth = sc.nextInt();
		System.out.println("Please enter the number of ROB entries");
		ROB = new Object[sc.nextInt()][5];
		System.out.println("Please enter the size of the instruction buffer");
		sizeOfInstructionBuffer = sc.nextInt();
		String instructions = ""; // this variable will hold the instructions
									// inserted by the user
		System.out
				.println("Please enter you program in the same form as described in the project except that to eliminate all spaces except after the operand, ex.(inst regA,regB,regC). When you are done please enter the letter 'q'");
		noOfInstructions = 0;
		String check = null;
		while (true) {
			check = sc.nextLine();
			System.out.println("The inserted instruction is "+check);
			if (check.equals("q"))
				break;
			System.out.println("3amalt incerement");
			//noOfInstructions++;
			instructions += check + "\n";
		}
		noOfInstructions = 1;
		System.out.println(noOfInstructions + " Number of inst");

		System.out
				.println("Please enter the address where you want to place the program in the memory. P.S. Your range is from word 8174 to word 32768");
		int pointer = sc.nextInt();
		pointer *= (blockSize / 2);
		System.out.println(pointer + " Initial input of memory");
		mainMemory = new MainMemory(blockSize, pointer, memAccessTime);
		String[] instructionsList = instructions.split("\n");
		System.out.println(instructionsList.length +" is the size of the instruction list ");
		for (int i = 0; i < instructionsList.length; i++) {
			mainMemory.data[pointer] = instructionsList[i];
			pointer++;
		}
		System.out
				.println("If you would like to insert any data initially, please insert the address followed by the value in the form of (address,value), then press enter for a new entry; and when you are finished please enter the character 'q'. If you dont want to insert data enter the character 'q'. P.S. Your range is from word 8 to word 8173");
		String checkData = sc.nextLine();
		checkData = sc.nextLine();
		while (!checkData.equals("q")) { // insertion of initial data is done
											// here !!!!!
			String[] addKey = checkData.split(",");
			mainMemory.data[Integer.parseInt(addKey[0])] = addKey[1];
			checkData = sc.nextLine();
		}
		sc.close();
	}

	public void ReservationStaions() {
		int index=0;
		
		
		// 0->add, 1->addI, 2->multiply,
		// 3->load,4->store, 5 -> sub, 6-> nand,
		// 7-> beq, 8->jmp,9->jalr,10-> ret
		for(int i=constantFunctionalUnits[0];i>0;i--){
			scoreBoard[index][2]="ADD";
			scoreBoard[index][1]="NOT BUSY";
			index++;
		}
		for(int i=constantFunctionalUnits[1];i>0;i--){
			scoreBoard[index][2]="ADDI";
			scoreBoard[index][1]="NOT BUSY";
			index++;
		}
		for(int i=constantFunctionalUnits[2];i>0;i--){
			scoreBoard[index][2]="MUL";	
			scoreBoard[index][1]="NOT BUSY";
			index++;
		}
		for(int i=constantFunctionalUnits[3];i>0;i--){
			scoreBoard[index][2]="LW";	
			scoreBoard[index][1]="NOT BUSY";
			index++;
		}
		for(int i=constantFunctionalUnits[4];i>0;i--){
			scoreBoard[index][2]="SW";
			scoreBoard[index][1]="NOT BUSY";
			index++;
		}
		for(int i=constantFunctionalUnits[5];i>0;i--){
			scoreBoard[index][2]="SUB";	
			scoreBoard[index][1]="NOT BUSY";
			index++;
		}
		for(int i=constantFunctionalUnits[6];i>0;i--){
			scoreBoard[index][2]="NAND";
			scoreBoard[index][1]="NOT BUSY";
			index++;
		}
		for(int i=constantFunctionalUnits[7];i>0;i--){
			scoreBoard[index][2]="BEQ";	
			scoreBoard[index][1]="NOT BUSY";
			index++;
		}
		for(int i=constantFunctionalUnits[8];i>0;i--){
			scoreBoard[index][2]="JMP";
			scoreBoard[index][1]="NOT BUSY";
			index++;
		}
		for(int i=constantFunctionalUnits[9];i>0;i--){
			scoreBoard[index][2]="JALR";
			scoreBoard[index][1]="NOT BUSY";
			index++;
		}
		for(int i=constantFunctionalUnits[10];i>0;i--){
			scoreBoard[index][2]="RET";
			scoreBoard[index][1]="NOT BUSY";
			index++;
		}
		

		
		
	}

	public void ROBcreation() {
		for (int i = 0; i < ROB.length; i++) {
			ROB[i][0] = i + 1;
			ROB[i][4] = "NOT Ready";
		}
	}

	public boolean insertROB(Instruction instruction) {
		if (ROB[tail][1] != null)
			return false;

		instruction.positionInROB = tail;

		String sop = instruction.operands;
		String[] speal = sop.split(",");
		String des = speal[0];

		if (instruction.type.equals("LW")) {
			ROB[tail][1] = "LD";
		} else if (instruction.type.equals("SW")) {
			ROB[tail][1] = "SD";
		} else {
			ROB[tail][1] = "FP";
		}
		if (instruction.type.equals("RET") || instruction.type.equals("BEQ")
				|| instruction.type.equals("JMP")) {
			ROB[tail][2] = null;
		} else if (instruction.type.equals("SW")) {
			String insop = instruction.operands;
			String[] s = insop.split(",");
			String destination = s[0];
			String source1 = s[1];
			String source2 = s[2];
			String Mem = source1 + source2;
			ROB[tail][2] = Mem;
		} else {
			ROB[tail][2] = des;
		}

		tail++;
		if (ROB.length == tail) {
			tail = 0;
		}
		return true;
	}

	public void issue(Instruction instruction) {
		System.out.println("Da5alt fel issue");
		
		if (firstime == true) {
			ReservationStaions();
			ROBcreation();
			mainMemory.data[0]="0";
			mainMemory.data[1]="1";
			mainMemory.data[2]="2";
			mainMemory.data[3]="3";
			mainMemory.data[4]="4";
			mainMemory.data[5]="5";
			mainMemory.data[6]="6";
			mainMemory.data[7]="7";

			firstime = false;
		}

		if (instruction.type.equals("LW") || instruction.type.equals("SW")) {
			String insop = instruction.operands;
			String[] s = insop.split(",");
			regA = s[0];
			x = regA.charAt(1) + "";
			nregA = Integer.parseInt(x);
			regB = s[1];
			x2 = regB.charAt(1) + "";
			nregB = Integer.parseInt(x2);
			imm = s[2];
			// x3=imm.charAt(1)+"";
			// nimm=Integer.parseInt(x3);

		} else if (instruction.type.equals("JMP")) {
			String insop = instruction.operands;
			String[] s = insop.split(",");
			regA = s[0];
			x = regA.charAt(1) + "";
			nregA = Integer.parseInt(x);
			imm = s[1];
			// x3=imm.charAt(1)+"";
			// nimm=Integer.parseInt(x3);
		} else if (instruction.type.equals("BEQ")) {
			String insop = instruction.operands;
			String[] s = insop.split(",");
			regA = s[0];
			x = regA.charAt(1) + "";
			nregA = Integer.parseInt(x);
			regB = s[1];
			x2 = regB.charAt(1) + "";
			nregB = Integer.parseInt(x2);
			imm = s[2];
			// x3=imm.charAt(1)+"";
			// nimm=Integer.parseInt(x3);
		} else if (instruction.type.equals("JALR")) {
			String insop = instruction.operands;
			String[] s = insop.split(",");
			regA = s[0];
			x = regA.charAt(1) + "";
			nregA = Integer.parseInt(x);
			regB = s[1];
			x2 = regB.charAt(1) + "";
			nregB = Integer.parseInt(x2);
		} else if (instruction.type.equals("RET")) {
			String insop = instruction.operands;
			String[] s = insop.split(",");
			regA = s[0];
			x = regA.charAt(1) + "";
			nregA = Integer.parseInt(x);
		} else {
			String insop = instruction.operands;
			String[] s = insop.split(",");
			regA = s[0];
			x = regA.charAt(1) + "";
			nregA = Integer.parseInt(x);
			regB = s[1];
			x2 = regB.charAt(1) + "";
			nregB = Integer.parseInt(x2);
			regC = s[2];
			if(instruction.type.equals("ADDI")){
				x3 = regC;
			}
			else
			x3 = regC.charAt(1) + "";
			nregC = Integer.parseInt(x3);
			//System.out.println("Da5alt fel issue");

		}

		// ///////////////////////////////////////////////
				System.out.println(RSrows+"hhhjhsajjsahas");

		for (int i = 0; i < RSrows; i++) {
			System.out.println(i+"iiiiiiii");
			String ins = scoreBoard[i][2].toString();
			String busy = scoreBoard[i][1].toString();
			if (ins.equals(instruction.type) && busy.equals("NOT BUSY")
					&& ROB[tail][1] == null) {				
				instruction.positionInScoreboard = i;
				boolean ROBstatus = insertROB(instruction);
				int positonrob = Integer
						.parseInt(ROB[instruction.positionInROB][0] + "");
				scoreBoard[instruction.positionInScoreboard][1] = "BUSY";
				// /////////// Load/store////////////////

				if (instruction.type.equals("LW")) {

					if (registersStatusTable[nregB] != 0) {
						scoreBoard[i][5] = regB;
						scoreBoard[i][3] = null;
					} else {
						scoreBoard[i][3] = regB;
						scoreBoard[i][5] = null;
					}
					scoreBoard[i][7] = positonrob;
					int offset = Integer.parseInt(imm);
					scoreBoard[i][8] = offset;
				} else if (instruction.type.equals("SW")) {
					if (registersStatusTable[nregB] != 0) {
						scoreBoard[i][5] = regB;
						scoreBoard[i][3] = null;
					} else {
						scoreBoard[i][3] = regB;
						scoreBoard[i][5] = null;
					}
					if (registersStatusTable[nregA] != 0) {
						scoreBoard[i][6] = regA;
						scoreBoard[i][4] = null;
					} else {
						scoreBoard[i][4] = regA;
						scoreBoard[i][6] = null;
					}
					int offset = Integer.parseInt(imm);
					scoreBoard[i][8] = offset;
					scoreBoard[i][7] = positonrob;
				}
				// /////////////////////////Unconditional branch///////////////
				else if (instruction.type.equals("JMP")) {
					if (registersStatusTable[nregA] != 0) {
						scoreBoard[i][3] = null;
						scoreBoard[i][5] = regA;
					} else {
						scoreBoard[i][3] = regA;
						scoreBoard[i][5] = null;
					}
					int offset = Integer.parseInt(imm);
					scoreBoard[i][8] = offset;
					scoreBoard[i][7] = positonrob;

					// ////////////////Conditional branch//////////////////////
				} else if (instruction.type.equals("BEQ")) {
					if (registersStatusTable[nregA] != 0) {
						scoreBoard[i][5] = regA;
						scoreBoard[i][3] = null;
					} else {
						scoreBoard[i][3] = regA;
						scoreBoard[i][5] = null;
					}
					if (registersStatusTable[nregB] != 0) {
						scoreBoard[i][6] = regB;
						scoreBoard[i][4] = null;
					} else {
						scoreBoard[i][6] = null;
						scoreBoard[i][4] = regB;
					}
					scoreBoard[i][7] = positonrob;
					int offset = Integer.parseInt(imm);
					scoreBoard[i][8] = offset;
				}
				// ///////////////Call/Return///////////////////
				else if (instruction.type.equals("JALR")) {
					registersStatusTable[nregA] = positonrob;
					if (registersStatusTable[nregB] != 0) {
						scoreBoard[i][5] = regB;
						scoreBoard[i][3] = null;
					} else {
						scoreBoard[i][5] = null;
						scoreBoard[i][3] = regB;
					}
					scoreBoard[i][7] = positonrob;
				}
				// /RET//////
				else if (instruction.type.equals("RET")) {
					if (registersStatusTable[nregA] != 0) {
						scoreBoard[i][5] = regA;
						scoreBoard[i][3] = null;
					} else {
						scoreBoard[i][3] = regA;
						scoreBoard[i][5] = null;
					}
					scoreBoard[i][7] = positonrob;
				}
				// ///////////////Arithmetic////////////////

				else {
					System.out.println("hereeeeeeeeee");
					registersStatusTable[nregA] = positonrob;
					if (registersStatusTable[nregB] != 0) {
						scoreBoard[i][5] = regB;
						scoreBoard[i][3] = null;
					} else {
						scoreBoard[i][5] = null;
						scoreBoard[i][3] = regB;
					}
					if ( !instruction.type.equals("ADDI")&& registersStatusTable[nregC] != 0) {
						scoreBoard[i][6] = regC;
						scoreBoard[i][4] = null;
					} else {
						scoreBoard[i][6] = null;
						scoreBoard[i][4] = regC;
					}
					scoreBoard[i][7] = positonrob;
				}
				if (scoreBoard[i][5] == null && scoreBoard[i][6] == null){
					instruction.dispatch = true;
				}

				return;
			}

		}

	}

	public void execute(Instruction instruction) {
		System.out.println("Da5alt");
		if (instruction.type.equals("SW")) {
			int x = instruction.positionInScoreboard;
			char c = scoreBoard[x][3].toString().charAt(1);
			int r = Integer.parseInt(c + "");
			int value = Integer.parseInt(mainMemory.data[r]);
			scoreBoard[x][8] = Integer.parseInt(scoreBoard[x][8].toString())
					+ value;
			return;
		}

		if (instruction.type == "LW") {
			int x = instruction.positionInScoreboard;
			char c = scoreBoard[x][3].toString().charAt(1);
			int r = Integer.parseInt(c + "");
			int value = Integer.parseInt(mainMemory.data[r]);
			scoreBoard[x][8] = Integer.parseInt(scoreBoard[x][8].toString())
					+ value;
		}

		if (instruction.type == "JMP") {
			int x = instruction.positionInScoreboard;
			char c = scoreBoard[x][3].toString().charAt(1);
			int r = Integer.parseInt(c + "");
			int value = Integer.parseInt(mainMemory.data[r]);
			scoreBoard[x][8] = Integer.parseInt(scoreBoard[x][8].toString())
					+ value;
		}

		instruction.execute();
	}

	public void write(Instruction instruction) {
		if (instruction.type.equals("SW")) {
			instruction.execute();
			return;
		}
		ROB[instruction.positionInROB][3] = instruction.addressAndValue[1];
		ROB[instruction.positionInROB][4] = "Ready";
		scoreBoard[instruction.positionInScoreboard][1] = "NOT BUSY";
		scoreBoard[instruction.positionInScoreboard][3] = null;
		scoreBoard[instruction.positionInScoreboard][4] = null;
		scoreBoard[instruction.positionInScoreboard][5] = null;
		scoreBoard[instruction.positionInScoreboard][6] = null;
		scoreBoard[instruction.positionInScoreboard][7] = null;
		scoreBoard[instruction.positionInScoreboard][8] = null;

	}

	public void commit(Instruction instruction) {
		String insop = instruction.operands;
		String[] s = insop.split(",");

		int entry = Integer.parseInt(ROB[instruction.positionInROB][0]
				.toString());
		if (ROB[instruction.positionInROB][4] == "Ready" && head == entry - 1) {
			ROB[instruction.positionInROB][4] = "NOT Ready";
			ROB[instruction.positionInROB][1] = null;
			ROB[instruction.positionInROB][2] = null;
			ROB[instruction.positionInROB][3] = null;

			// String x=firstsource.charAt(1)+"";
			// int n=Integer.parseInt(x);
			// registersStatusTable[n]=0;

			if(++head==ROB.length){
				head=0;
			}
			for (int i = 0; i < registersStatusTable.length; i++) {
				if (registersStatusTable[i] == entry) {
					registersStatusTable[i] = 0;
					break;
				}
			}
		}

	}

	public void InstructionFetch() {
		Object search = null;
		int fetchedInstruction = (pipelineWidth > (sizeOfInstructionBuffer - instructionBuffer
				.size())) ? sizeOfInstructionBuffer - instructionBuffer.size()
				: pipelineWidth;
		
		for (int k = 0; k < fetchedInstruction
				&& ((mainMemory.instructionPointer - mainMemory.initialPointer + 1) <= noOfInstructions); k++) {
			System.out.println(mainMemory.instructionPointer
					- mainMemory.initialPointer + 1 + "giii");
						for (int i = 0; i < caches.size(); i++) {
				search = caches.get(i).searchData(
						mainMemory.instructionPointer, 1);
				if (search != null) {
					
					caches.get(i).cacheData(mainMemory.instructionPointer,
							i + 1, 1, null);
					
					break;
				}
				if (search == null && i == caches.size() - 1) {
					caches.get(i).cacheData(mainMemory.instructionPointer,
							i + 1, 1, null);
					search = caches.get(2).searchData(
							mainMemory.instructionPointer, 1);

				}
			}
//			for (int t = 0; t < caches.get(0).iData.length; t++) {
//				if (caches.get(0).iData[t] != null){
//					System.out.print(caches.get(0).iTag[t]+" Taaaag ");
//					System.out.println(caches.get(0).iData[t].toString()
//							+ " Entry in cache");
//					
//				}
//					
//				else
//					System.out.println("Helllo Mousa");
			//}
			mainMemory.instructionPointer++;

			//System.out.println(search.toString()+"The final searchADD");

			Instruction fetchedInst = new Instruction(search.toString());
			fetchedInst.fetched = cycle;
			instructionBuffer.add(fetchedInst);
			if (fetchedInst.type.equals("JMP")
					|| fetchedInst.type.equals("JALR")
					|| fetchedInst.type.equals("RET")) {
				changeAddress = true;
				
				return;
			}
		}
	}

	public void stage() {
		while (commitedInstructions != noOfInstructions) {
			write = false;
			commit = false;
			if (!changeAddress)
				InstructionFetch();
			boolean issue = true;
			while (issue) {
				issue = false;
				if (ROB[this.tail][1] == null && instructionBuffer.size() > 0) {// 0->add,
									System.out.println(instructionBuffer.size()+"7abibiii");											// 1->addI,
																				// 2->multiply,
					// 3->load,4->store, 5 -> sub, 6-> nand, 7-> beq,
					// 8->jmp,9->jalr,10-> ret
					switch (instructionBuffer.get(0).type) {
					case "ADD":
						issue = (functionalUnits[0] > 0) ? true : false;
						if (issue)
							functionalUnits[0]--;
						break;
					case "MUL":
						issue = (functionalUnits[2] > 0) ? true : false;
						if (issue)
							functionalUnits[2]--;
						break;
					case "BEQ":
						issue = (functionalUnits[7] > 0) ? true : false;
						if (issue) {
							functionalUnits[7]--;
							branchIssuedCycle = cycle;
							String[] split = instructionBuffer.get(0).operands
									.split(",");
							if (Integer.parseInt(split[2]) > 0)
								predictedBranch = false;
							if (!predictedBranch)
								mispredictionPointer = mainMemory.instructionPointer
										+ Integer.parseInt(split[2]);
							else
								mispredictionPointer = mainMemory.instructionPointer;
						}
						break;
					case "NAND":
						issue = (functionalUnits[6] > 0) ? true : false;
						if (issue)
							functionalUnits[6]--;
						break;
					case "SUB":
						issue = (functionalUnits[5] > 0) ? true : false;
						if (issue)
							functionalUnits[5]--;
						break;
					case "ADDI":
						issue = (functionalUnits[1] > 0) ? true : false;
						if (issue)
							functionalUnits[1]--;
						break;
					case "JMP":
						issue = (functionalUnits[8] > 0) ? true : false;
						if (issue)
							functionalUnits[8]--;
						break;
					case "JALR":
						issue = (functionalUnits[9] > 0) ? true : false;
						if (issue)
							functionalUnits[9]--;
						break;
					case "RET":
						issue = (functionalUnits[10] > 0) ? true : false;
						if (issue)
							functionalUnits[10]--;
						break;
					case "LW":
						issue = (functionalUnits[3] > 0) ? true : false;
						if (issue)
							functionalUnits[3]--;
						break;
					case "SW":
						issue = (functionalUnits[4] > 0) ? true : false;
						if (issue)
							functionalUnits[4]--;
						break;
					}
					if (issue) {
						instructionBuffer.get(0).issue = true;
						allInstructions.add(instructionBuffer.get(0));

						issue(instructionBuffer.remove(0));
						allInstructions.get(allInstructions.size() - 1).issued = cycle;
					}
				}
			}
			for (int i = 0; i < allInstructions.size(); i++) {
				Instruction inst = allInstructions.get(i);
				System.out.println(inst.issue + " The issue variable");
				System.out.println(inst.dispatch + " The dispatch variable");
				System.out.println(inst.execute + " The execute variable");
				System.out.println(inst.write + " The write variable");
				System.out.println(inst.commit + " The commit variable");
				if (inst.issued != cycle) {
					if (inst.dispatch) {
						if (inst.type.equals("LW")
								&& inst.issued == (cycle - 2)) {
							execute(inst);
							inst.startExecution = cycle;
							inst.endExecution = cycle + inst.executingTime - 1;
						} else if (!inst.type.equals("LW")) {
							if (inst.type.equals("JMP")
									|| inst.type.equals("JALR")
									|| inst.type.equals("RET"))
								changeAddress = false;
							inst.execute = true;
							inst.issue = false;
							inst.dispatch = false;
							execute(inst);
							inst.startExecution = cycle;
							inst.endExecution = cycle + inst.executingTime - 1;
							System.out.println(inst.startExecution + " start execution function "+(i+1));
							System.out.println(inst.endExecution + " end execution function "+(i+1));

						}

					} else if (inst.execute && inst.endExecution >= cycle
							&& !write) {
						System.out.println("Da5alt henaaaaaaaaaaaaaaaaadsasdasdasdasdasd");
						if (inst.type.equals("SW")) {
							inst.startStoreWriting = cycle;
							inst.endStoreWriting = cycle + inst.writingTime - 1;
						}
						write = true;
						if (inst.type.equals("BEQ")) {
							if (actualBranch != predictedBranch) {
								mainMemory.instructionPointer = mispredictionPointer;
								for (int m = 0; m < ROB.length; m++) {
									for (int k = 0; k < allInstructions.size(); k++) {
										if (allInstructions.get(k).positionInROB == m
												&& allInstructions.get(k).issued > inst.issued) {
											for (int l = 1; l < ROB[0].length; l++) {
												if (l == ROB[0].length - 1) {
													ROB[m][l] = "NOT Ready";
												} else
													ROB[m][l] = null;
											}
										}
									}
								}

								for (int m = 0; m < scoreBoard.length; m++) {
									for (int k = 0; k < allInstructions.size(); k++) {
										if (allInstructions.get(k).positionInScoreboard == m
												&& allInstructions.get(k).issued > inst.issued) {
											allInstructions.remove(k);
											for (int l = 1; l < scoreBoard.length; l++) {
												if (l == 1)
													scoreBoard[m][l] = "NOT BUSY";
												else if (l == 2) {
													// nothing
												} else {
													scoreBoard[m][l] = null;
												}

											}
										}
									}
								}
								for (int m = 0; m < instructionBuffer.size(); m++)
									if (instructionBuffer.get(m).fetched > inst.fetched)
										instructionBuffer.remove(m);
								String[] split = inst.operands.split(",");
								int registerStatusPosition = Integer
										.parseInt(split[0].charAt(1) + "");
								registersStatusTable[registerStatusPosition] = 0;
							}
						}
						inst.execute = false;
						inst.write = true;
						System.out.println("Da5alt el write");
						write(inst);
					} else if (inst.write && !commit)
						if (head == inst.positionInROB)
							if (inst.type.equals("SW")
									&& inst.endStoreWriting >= cycle) {
								inst.write = false;
								inst.commit = true;
								System.out.println("Da5alt el commit");
								commit(inst);
								commit = true;
								commitedInstructions++;
							} else if (!inst.type.equals("SW")) {
								inst.write = false;
								inst.commit = true;
								System.out.println("Da5alt el commit");
								commit(inst);
								commit = true;
								commitedInstructions++;
							}
				}
			}
			cycle++;
			System.out.println("<<<<<<<<<<< Printing the ROB >>>>>>>>>>>>>");
			for (int i = 0; i < ROB.length; i++) {
				for (int j = 0; j < ROB[i].length; j++) {
					if (ROB[i][j] != null)
						System.out.print(ROB[i][j].toString() + " , ");
				}
				System.out.println();
			}
			System.out.println("<<<<<<<<<<< Printing the Score Board >>>>>>>>>>>>>");
			for (int i = 0; i < scoreBoard.length; i++) {
				for (int j = 0; j < scoreBoard[i].length; j++) {
					if (scoreBoard[i][j] != null)
						System.out.print(scoreBoard[i][j].toString() + " , ");
				}
				System.out.println();
			}
			if(cycle == 10)
				return;
		}
		int totalAccess;
		int hitRate;
		int missRate;
		for(int j=0;j<Processor.caches.size();j++){
			totalAccess= Processor.caches.get(j).hit +Processor.caches.get(j).miss;
			hitRate = (Processor.caches.get(j).hit/totalAccess) *100;
			missRate = (Processor.caches.get(j).miss/totalAccess) *100;
			System.out.println( "hit rate of cache level"+ (j+1)+ "is :" +hitRate);
			System.out.println( "miss rate of cache level"+ (j+1)+ "is :" +missRate);
		}
	}
}



