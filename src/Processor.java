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
	int[] constantFunctionalUnits;
	Object[][] scoreBoard;// same columns as in lecture 11
	int[] registersStatusTable = new int[7];// index 0->R1 and so on...
	int pipelineWidth;
	Object ROB[][];
	int head = 0;
	int tail = 0;
	ArrayList<Instruction> instructionBuffer = new ArrayList<Instruction>();
	int sizeOfInstructionBuffer;
	int noOfInstrutions = 0;
	ArrayList<Instruction> allInstructions = new ArrayList<Instruction>();
	int cycle = 0;
	int commitedInstructions = 0;
	boolean write;
	boolean commit;
	boolean predictedBranch = true;
	boolean actualBranch;
	int branchIssuedCycle;
	int mispredictionPointer;

	public static void main(String[] args) {
		Processor p = new Processor();
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
					.println("Please enter the authorization of cache level"
							+ (i + 1)
							+ ".\nThe options are : 1- For (entry equals 1) Direct mapped will be chosen.\n2- For (1 < entry < "
							+ (size / 2)
							+ ") Set associativity will be chosen.\n3- For entry equals ("
							+ size / 2 + ") Full associativity will be chosen.");
			int auhto = sc.nextInt();
			System.out
					.println("Please enter the writing policy if its a 'write back' or 'write through' in case of HIT");
			String Hit = sc.nextLine();
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
		System.out.println("Please enter how many add funtional units");
		functionalUnits[0] = sc.nextInt();
		System.out.println("Please enter the cost of the add");
		cyclesPerInst[0] = sc.nextInt();
		System.out
				.println("Please enter how many add immediate funtional units");
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
		constantFunctionalUnits = functionalUnits;
		int rows = 0;
		for (int i = 0; i < functionalUnits.length; i++) {
			rows += functionalUnits[i];
		}
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
		while (true) {
			String check = sc.nextLine();
			noOfInstrutions++;
			if (check.equals("q"))
				break;
			instructions += check + "\n";
		}

		System.out
				.println("Please enter the address where you want to place the program in the memory. P.S. Your range is from word 8174 to word 32768");
		int pointer = sc.nextInt();
		mainMemory = new MainMemory(blockSize, pointer, memAccessTime);
		String[] instructionsList = instructions.split("\n");
		for (int i = 0; i < instructionsList.length; i++) {
			mainMemory.data[pointer] = instructionsList[i];
			pointer++;
		}
		System.out
				.println("If you would like to insert any data initially, please insert the address followed by the value in the form of (address,value), then press enter for a new entry; and when you are finished please enter the character 'q'. If you dont want to insert data enter the character 'q'. P.S. Your range is from word 8 to word 8173");
		String checkData = sc.nextLine();
		while (!checkData.equals("q")) { // insertion of initial data is done
											// here !!!!!
			String[] addKey = checkData.split(",");
			mainMemory.data[Integer.parseInt(addKey[0])] = addKey[1];
			checkData = sc.nextLine();
		}
		sc.close();
	}

	public void InstructionFetch() {
		Object search = null;
		int fetchedInstruction = (pipelineWidth > (sizeOfInstructionBuffer - instructionBuffer
				.size())) ? sizeOfInstructionBuffer - instructionBuffer.size()
				: pipelineWidth;

		for (int k = 0; k < fetchedInstruction
				&& ((mainMemory.instructionPointer - mainMemory.initialPointer + 1) <= noOfInstrutions); k++) {
			for (int i = 0; i < caches.size(); i++) {
				search = caches.get(i).searchData(
						mainMemory.instructionPointer, 1);
				if (search != null) {
					// for (int j = i; j > 0; j--) {
					caches.get(i).cacheData(mainMemory.instructionPointer, i+1,
							1, null);
					// }
					break;
				}
				if (search == null && i == caches.size() - 1) {
					//for (int i = caches.size(); i > 0; i++) {
						caches.get(i).cacheData(mainMemory.instructionPointer, i+1,
								1, null);
					//}
					search = caches.get(0).searchData(mainMemory.instructionPointer, 1);
				}
			}
			mainMemory.instructionPointer++;
			Instruction fetchedInst = new Instruction(search.toString());
			fetchedInst.fetched = cycle;
			instructionBuffer.add(fetchedInst);

		}

	}

	public void issue(Instruction instruction) {

	}

	public void execute(Instruction instruction) {

	}

	public void write(Instruction instruction) {

	}

	public void commit(Instruction instruction) {

	}

	public void stage() {
		while (commitedInstructions != noOfInstrutions) {
			write = false;
			commit = false;
			InstructionFetch();
			boolean issue = true;
			while (issue) {
				issue = false;
				if (ROB[this.tail][1] == null && instructionBuffer.size() > 0) {// 0->add,
																				// 1->addI,
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
				if (inst.issued != cycle) {
					if (inst.dispatch) {
						if (inst.type.equals("LW")
								&& inst.issued == (cycle - 2)) {
							execute(inst);
							inst.startExecution = cycle;
							inst.endExecution = cycle + inst.executingTime - 1;
						} else if (!inst.type.equals("LW")) {
							execute(inst);
							inst.startExecution = cycle;
							inst.endExecution = cycle + inst.executingTime - 1;
						}

					} else if (inst.execute && inst.endExecution == cycle
							&& !write) {
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
												if(l==1){
													scoreBoard[m][l]="NOT BUSY";
												}else if(l==2){
													//nothing
												}else{
													scoreBoard[m][l] = null;
												}
											
											}

										}
									}
								}

								for (int m = 0; m < instructionBuffer.size(); m++) {
									if (instructionBuffer.get(m).fetched > inst.fetched) {
										instructionBuffer.remove(m);
									}
								}

								String[] split = inst.operands.split(",");
								int registerStatusPosition = Integer
										.parseInt(split[0].charAt(1) + "");
								registersStatusTable[registerStatusPosition] = 0;

							}
						}
						write(inst);
					} else if (inst.write && !commit) {
						if (head == inst.positionInROB) {
							if (inst.type.equals("SW")
									&& inst.endStoreWriting == cycle) {
								commit(inst);
								commit = true;
								commitedInstructions++;
							} else if (!inst.type.equals("SW")) {
								commit(inst);
								commit = true;
								commitedInstructions++;
							}
						}
					}
				}
			}
			cycle++;
		}
	}
}
