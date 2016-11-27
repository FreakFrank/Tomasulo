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
	int RSrows;
	int scorebordrow;
	boolean firstime;
	int firstoperand;
	int secondoperand;
	String regA;
	String x;
	int nregA;
	String  regB;
	String x2;
	int nregB;
	String imm;
	String regC;
	String x3;
	int nimm;
	int nregC;
	public static void main(String[] args) {
		Processor p = new Processor();
	}

	public Processor() {
		firstoperand=0;
		secondoperand=0;
		firstime=true;
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
		RSrows=-1;
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
							for (int j = i; j > 0; j--) {
								caches.get(j - 1).cacheData(
										mainMemory.instructionPointer, j, 1);
							}
							break;

						}
					}

					if (search == null) {
						for (int i = caches.size(); i > 0; i++) {
							caches.get(i)
							.cacheData(mainMemory.instructionPointer, i, 1);
						}
					}
					mainMemory.instructionPointer++;
					Instruction fetchedInst = new Instruction(search.toString());
					instructionBuffer.add(fetchedInst);

				}

	}
	public void ReservationStaions(Instruction instruction) {
		switch (instruction.type) {
		case "ADD":
			for(int i=1;i<=constantFunctionalUnits[0];i++){
				scoreBoard[RSrows++][0]="ADD"+i;
				scoreBoard[RSrows][1]="NOT BUSY";
				scoreBoard[RSrows][2]=instruction.type;
			}
			break;
		case "MUL":
			for(int i=1;i<=constantFunctionalUnits[2];i++){
				scoreBoard[RSrows++][0]="MUL"+i;
				scoreBoard[RSrows][1]="NOT BUSY";
				scoreBoard[RSrows][2]=instruction.type;
			}
			break;
		case "BEQ":
			for(int i=1;i<=constantFunctionalUnits[7];i++){
				scoreBoard[RSrows++][0]="BEQ"+i;
				scoreBoard[RSrows][1]="NOT BUSY";
				scoreBoard[RSrows][2]=instruction.type;
			}
			break;
		case "NAND":
			for(int i=1;i<=constantFunctionalUnits[6];i++){
				scoreBoard[RSrows++][0]="NAND"+i;
				scoreBoard[RSrows][1]="NOT BUSY";
				scoreBoard[RSrows][2]=instruction.type;
			}
			break;
		case "SUB":
			for(int i=1;i<=constantFunctionalUnits[5];i++){
				scoreBoard[RSrows++][0]="SUB"+i;
				scoreBoard[RSrows][1]="NOT BUSY";
				scoreBoard[RSrows][2]=instruction.type;
			}
			break;
		case "ADDI":
			for(int i=1;i<=constantFunctionalUnits[1];i++){
				scoreBoard[RSrows++][0]="ADDI"+i;
				scoreBoard[RSrows][1]="NOT BUSY";
				scoreBoard[RSrows][2]=instruction.type;
			}
			break;
		case "JMP":
			for(int i=1;i<=constantFunctionalUnits[8];i++){
				scoreBoard[RSrows++][0]="JMP"+i;
				scoreBoard[RSrows][1]="NOT BUSY";
				scoreBoard[RSrows][2]=instruction.type;
			}
			break;
		case "JALR":
			for(int i=1;i<=constantFunctionalUnits[9];i++){
				scoreBoard[RSrows++][0]="JALR"+i;
				scoreBoard[RSrows][1]="NOT BUSY";
				scoreBoard[RSrows][2]=instruction.type;
			}
			break;
		case "RET":
			for(int i=1;i<=constantFunctionalUnits[10];i++){
				scoreBoard[RSrows++][0]="RET"+i;
				scoreBoard[RSrows][1]="NOT BUSY";
				scoreBoard[RSrows][2]=instruction.type;
			}
			break;
		case "LW":
			for(int i=1;i<=constantFunctionalUnits[3];i++){
				scoreBoard[RSrows++][0]="LW"+i;
				scoreBoard[RSrows][1]="NOT BUSY";
				scoreBoard[RSrows][2]=instruction.type;
			}
			break;
		case "SW":
			for(int i=1;i<=constantFunctionalUnits[4];i++){
				scoreBoard[RSrows++][0]="SW"+i;
				scoreBoard[RSrows][1]="NOT BUSY";
				scoreBoard[RSrows][2]=instruction.type;
			}
			break;
		}
	}
	public void ROBcreation(){
		for(int i=0;i<ROB.length;i++){
			ROB[i][0]=i+1;
			ROB[i][4]="NOT Ready";
		}
	}
	public boolean insertROB(Instruction instruction){
		if(ROB[tail][1]!=null)
			return false;


		instruction.positionInROB=tail;

		String sop=instruction.operands;
		String[] speal=sop.split(",");
		String des=speal[0];


		if(instruction.type.equals("LW")){
			ROB[tail][1]="LD";
		}else if(instruction.type.equals("SW")){
			ROB[tail][1]="SD";
		}else{
			ROB[tail][1]="FP";
		}
		if(instruction.type.equals("RET")||instruction.type.equals("BEQ")||instruction.type.equals("JMP")){
			ROB[tail][2]=null;
		}else if(instruction.type.equals("SW")){
			String insop=instruction.operands;
			String[] s=insop.split(",");
			String destination=s[0];
			String source1=s[1];
			String source2=s[2];
			String Mem=source1+source2;
			ROB[tail][2]=Mem;
		}else{
			ROB[tail][2]=des;
		}

		tail++;
		if(ROB.length==tail){
			tail=0;
		}
		return true;
	}

	public void issue(Instruction instruction) {
		if(instruction.type.equals("LW")||instruction.type.equals("SW")){
			String insop=instruction.operands;
			String[] s=insop.split(",");
			regA=s[0];
			x=regA.charAt(1)+"";
			nregA=Integer.parseInt(x);
			regB=s[1];
			x2=regB.charAt(1)+"";
			nregB=Integer.parseInt(x2);
			imm=s[2];
			x3=imm.charAt(1)+"";
			nimm=Integer.parseInt(x3);
			firstoperand=Integer.parseInt(regB);
			secondoperand=Integer.parseInt(imm);
		}
		else if(instruction.type.equals("JMP")){
			String insop=instruction.operands;
			String[] s=insop.split(",");
			regA =s[0];
			x=regA.charAt(1)+"";
			nregA=Integer.parseInt(x);
			imm=s[1];
			x3=imm.charAt(1)+"";
			nimm=Integer.parseInt(x3);
		}
		else if(instruction.type.equals("BEQ")){
			String insop=instruction.operands;
			String[] s=insop.split(",");
			regA=s[0];
			x=regA.charAt(1)+"";
			nregA=Integer.parseInt(x);
			regB=s[1];
			x2=regB.charAt(1)+"";
			nregB=Integer.parseInt(x2);
			imm=s[2];
			x3=imm.charAt(1)+"";
			nimm=Integer.parseInt(x3);
		}
		else if(instruction.type.equals("JALR")){
			String insop=instruction.operands;
			String[] s=insop.split(",");
			regA =s[0];
			x=regA.charAt(1)+"";
			nregA=Integer.parseInt(x);
			regB=s[1];
			x2=regB.charAt(1)+"";
			nregB=Integer.parseInt(x2);
		}
		else if(instruction.type.equals("RET")){
			String insop=instruction.operands;
			String[] s=insop.split(",");
			regA =s[0];
			x=regA.charAt(1)+"";
			nregA=Integer.parseInt(x);
		}
		else {
			String insop=instruction.operands;
			String[] s=insop.split(",");
			regA=s[0];
			x=regA.charAt(1)+"";
			nregA=Integer.parseInt(x);
			regB=s[1];
			x2=regB.charAt(1)+"";
			nregB=Integer.parseInt(x2);
			regC=s[2];
			x3=regC.charAt(1)+"";
			nregC=Integer.parseInt(x3);
		}

		/////////////////////////////////////////////////
		if(firstime==true){
			ReservationStaions(instruction);
			ROBcreation();
			firstime=false; 
		}

		for(int i=0;i<=RSrows;i++){
			String ins=scoreBoard[i][2].toString();
			String busy=scoreBoard[i][1].toString();
			if(ins.equals(instruction.type)&&busy.equals("NOT BUSY")&&ROB[tail][1]==null){
				instruction.positionInScoreboard=i;
				boolean ROBstatus=insertROB(instruction);
				int positonrob=Integer.parseInt(ROB[instruction.positionInROB][0]+"");
				scoreBoard[instruction.positionInScoreboard][1]="BUSY";
				///////////// Load/store////////////////

				if(instruction.type.equals("LW")){
					registersStatusTable[nregA]=positonrob;
					if(registersStatusTable[nregB]!=0){
						scoreBoard[i][5]=firstoperand+secondoperand;
						scoreBoard[i][3]=null;
					}else{
						scoreBoard[i][3]=firstoperand+secondoperand;
						scoreBoard[i][5]=null;
					}
					scoreBoard[i][7]=positonrob;
					int offset=Integer.parseInt(imm);
					scoreBoard[i][8]=offset;
				}else if(instruction.type.equals("SW")){
					if(registersStatusTable[nregB]!=0){
						scoreBoard[i][5]=firstoperand+secondoperand;
						scoreBoard[i][3]=null;
					}else{
						scoreBoard[i][3]=firstoperand+secondoperand;
						scoreBoard[i][5]=null;
					}
					if(registersStatusTable[nregA]!=0){
						scoreBoard[i][6]=regB;
						scoreBoard[i][4]=null;
					}else{
						scoreBoard[i][4]=regB;
						scoreBoard[i][6]=null;
					}
					int offset=Integer.parseInt(imm);
					scoreBoard[i][8]=offset;
					scoreBoard[i][7]=positonrob;
				}
				///////////////////////////Unconditional branch///////////////
				else if(instruction.type.equals("JMP")){
					if(registersStatusTable[nregA]!=0){
						scoreBoard[i][3]=null;
						scoreBoard[i][5]=regA;
					}else{
						scoreBoard[i][3]=regA;
						scoreBoard[i][5]=null;
					}
					scoreBoard[i][7]=positonrob;
					//////////////////Conditional branch//////////////////////			
				}else if(instruction.type.equals("BEQ")){
					if(registersStatusTable[nregA]!=0){
						scoreBoard[i][5]=regA;
						scoreBoard[i][3]=null;
					}else{
						scoreBoard[i][3]=regA;
						scoreBoard[i][5]=null;
					}
					if(registersStatusTable[nregB]!=0){
						scoreBoard[i][6]=regB;
						scoreBoard[i][4]=null;
					}else{
						scoreBoard[i][6]=null;
						scoreBoard[i][4]=regB;
					}
					scoreBoard[i][7]=positonrob;
				}
				/////////////////Call/Return///////////////////
				else if(instruction.type.equals("JALR")){
					registersStatusTable[nregA]=positonrob;
					if(registersStatusTable[nregB]!=0){
						scoreBoard[i][5]=regB;
						scoreBoard[i][3]=null;
					}else{
						scoreBoard[i][5]=null;
						scoreBoard[i][3]=regB;
					}
					scoreBoard[i][7]=positonrob;
				}
				               ///RET//////
				else if(instruction.type.equals("RET")){
					if(registersStatusTable[nregA]!=0){
						scoreBoard[i][5]=regA;
						scoreBoard[i][3]=null;
					}else{
						scoreBoard[i][3]=regA;
						scoreBoard[i][5]=null;
					}
					scoreBoard[i][7]=positonrob;
				}
				/////////////////Arithmetic////////////////
				
				else{
					registersStatusTable[nregA]=positonrob;
					if(registersStatusTable[nregB]!=0){
						scoreBoard[i][5]=regB;
						scoreBoard[i][3]=null;
					}else{
						scoreBoard[i][5]=null;
						scoreBoard[i][3]=regB;
					}
					if(registersStatusTable[nregC]!=0){
						scoreBoard[i][6]=regC;
						scoreBoard[i][4]=null;
					}else{
						scoreBoard[i][6]=null;
						scoreBoard[i][4]=regC;
					}
					scoreBoard[i][7]=positonrob;
				}
				if(scoreBoard[i][5]==null&&scoreBoard[i][6]==null)
					instruction.dispatch=true;
				
			}

		}



	}

	public void execute(Instruction instruction) {
		if(instruction.type.equals("SW"))
			return;
		instruction.execute();
	}

	public void write(Instruction instruction) {
		if(instruction.type.equals("SW")){
			instruction.execute();
		}
		ROB[instruction.positionInROB][3]=instruction.addressAndValue[1];
		ROB[instruction.positionInROB][4]="Ready";
		scoreBoard[instruction.positionInScoreboard][1]="NOT BUSY";
		scoreBoard[instruction.positionInScoreboard][3]=null;
		scoreBoard[instruction.positionInScoreboard][4]=null;
		scoreBoard[instruction.positionInScoreboard][5]=null;
		scoreBoard[instruction.positionInScoreboard][6]=null;
		scoreBoard[instruction.positionInScoreboard][7]=null;
		scoreBoard[instruction.positionInScoreboard][8]=null;

	}

	public void commit(Instruction instruction) {
		String insop=instruction.operands;
		String[] s=insop.split(",");
		String destination=s[0];
		String source=s[1];
		String[] s2=source.split(",");
		String first_Source=s2[0];
		String second_Source=s2[1];
		int entry=Integer.parseInt(ROB[instruction.positionInROB][0].toString());
		if(ROB[instruction.positionInROB][4]=="Ready"&&head==entry-1){
			ROB[instruction.positionInROB][4]="NOT Ready";
			ROB[instruction.positionInROB][1]=null;
			ROB[instruction.positionInROB][2]=null;
			ROB[instruction.positionInROB][3]=null;

			String x=first_Source.charAt(1)+"";
			int n=Integer.parseInt(x);
			registersStatusTable[n]=0;
		}


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
						if (issue)
							functionalUnits[7]--;
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
						execute(inst);
						inst.startExecution = cycle;
						inst.endExecution = cycle + inst.executingTime - 1;
					} else if (inst.execute && inst.endExecution == cycle
							&& !write) {
						write = true;
						write(inst);
					} else if (inst.write && !commit) {
						if (head == inst.positionInROB) {
							commit(inst);
							commit = true;
							commitedInstructions++;
						}
					}
				}
			}
			cycle++;
		}
	}
}
