public class Instruction {
	boolean issue = false;
	boolean dispatch = false;
	boolean execute = false;
	boolean write = false;
	boolean commit = false;
	String type = "";
	String operands = "";
	String[] addressAndValue;
	int startExecution;
	int endExecution;
	int issued;
	int executingTime;
	int positionInScoreboard;
	int dispatchCycle;
	int positionInROB;
	int writingTime;
	int startStoreWriting;
	int endStoreWriting;
	int fetched;
	int jumpAddress;
	public Instruction(String instruction) {// R0 has index 0 in main memory, R1
											// has index 1 in main memory and so
											// on till R7 having index 7
		String [] InstructionSplitted = instruction.split(" "); // index 0 ->
																// type , index
																// 1-> operands
		//String [] InstructionSplitted = {"ADDI","R1,R2,50"};
		System.out.println("The instruction is "+instruction);
		type = InstructionSplitted[0];
		operands = InstructionSplitted[1];
	}
	public void execute() {

		switch (type) {
		case "ADD":
			addressAndValue = add();
			executingTime = Processor.cyclesPerInst[0];
			break;
		case "SUB":
			addressAndValue = sub();
			executingTime = Processor.cyclesPerInst[5];
			break;
		case "ADDI":
			addressAndValue = addi();
			executingTime = Processor.cyclesPerInst[1];
			break;
		case "NAND":
			addressAndValue = nand();
			executingTime = Processor.cyclesPerInst[6];
			break;
		case "MUL":
			addressAndValue = mul();
			executingTime = Processor.cyclesPerInst[2];
			break;
		case "JALR":
			addressAndValue = jalr();
			executingTime = Processor.cyclesPerInst[9];
			break;
		case "RET":
			addressAndValue = ret();
			executingTime = Processor.cyclesPerInst[10];
			break;
		case "JMP":
			addressAndValue = jmp();
			executingTime = Processor.cyclesPerInst[8];
			break;
		case "BEQ":
			addressAndValue = beq();
			executingTime = Processor.cyclesPerInst[7];
			break;
		case "LW":
			addressAndValue = lw();
			executingTime = Processor.cyclesPerInst[3];
			break;
		case "SW":
			addressAndValue = sw();
			writingTime = Processor.cyclesPerInst[4];
			executingTime = 1;
			break;
		}
	}

	// each method should return an array in the form of
	// [RD,value] for example if the RD is R5 and the
	// value is 6 it should be as [5,6] and it will be assigned to the array
	// addressAndValue

	public String[] add() {
		String[] addSplit = operands.split(",");
		String addReg1 = addSplit[1];
		String addReg2 = addSplit[2];
		String addDestReg = addSplit[0];
		int r1 = Integer.parseInt(addReg1.charAt(1) + "");
		int r2 = Integer.parseInt(addReg2.charAt(1) + "");
		int destreg = Integer.parseInt(addDestReg.charAt(1) + "");
		r1 =Integer.parseInt( Processor.mainMemory.data[r1]);
		r2 =Integer.parseInt( Processor.mainMemory.data[r2]);
		String [] addAndVal = new String[2];
		addAndVal[0] = destreg+"";
		addAndVal[1] = (r1+r2)+"";
		return addAndVal;
	}

	public String[] sub() {
		String[] subSplit = operands.split(",");
		String subReg1 = subSplit[1];
		String subReg2 = subSplit[2];
		String subDestReg = subSplit[0];
		int r1 = Integer.parseInt(subReg1.charAt(1) + "");
		int r2 = Integer.parseInt(subReg2.charAt(1) + "");
		int destreg = Integer.parseInt(subDestReg.charAt(1) + "");
		r1 =Integer.parseInt( Processor.mainMemory.data[r1]);
		r2 =Integer.parseInt( Processor.mainMemory.data[r2]);
		String [] addAndVal = new String[2];
		addAndVal[0] = destreg+"";
		addAndVal[1] = (r1-r2)+"";
		return addAndVal;

	}

	public String[] addi() {
		String[] addiSplit = operands.split(",");
		String addiReg1 = addiSplit[1];
		String addiValue = addiSplit[2];
		String addiDestReg = addiSplit[0];
		int r1 = Integer.parseInt(addiReg1.charAt(1) + "");
		int r2 = Integer.parseInt(addiValue);
		int destreg = Integer.parseInt(addiDestReg.charAt(1) + "");
		r1 =Integer.parseInt( Processor.mainMemory.data[r1]);
		String [] addAndVal = new String[2];
		addAndVal[0] = destreg+"";
		addAndVal[1] = (r1+r2)+"";
		return addAndVal;

	}

	public String[] nand() {
		String[] nandSplit = operands.split(",");
		String nandReg1 = nandSplit[1];
		String nandReg2 = nandSplit[2];
		String nandDestReg = nandSplit[0];
		String nandRes = null;

		int r3 = Integer.parseInt(nandDestReg.charAt(1) + "");
		int r1 = Integer.parseInt(nandReg1.charAt(1) + "");
		r1 =Integer.parseInt( Processor.mainMemory.data[r1]);
		String R1 = Integer.toBinaryString(r1);
		int r2 = Integer.parseInt(nandReg2.charAt(1) + "");
		r2 =Integer.parseInt( Processor.mainMemory.data[r2]);
		String R2 = Integer.toBinaryString(r2);
		int diff = (R2.length()>R1.length()?R2.length()-R1.length():R1.length()-R2.length());
		if (R2.length()>R1.length()) {
			for (int j=0; j < diff; j++) {
				R1 = "0" + R1;
			}
		} else {
			for (int j=0; j < diff; j++) {
				R2 = "0" + R2;
			}
		}
		for (int i = 0 ; i<(R2.length()<R1.length()?R2.length():R1.length());i++){
			if (R1.charAt(i) == '1' && R2.charAt(i) == '1') {
			nandRes = nandRes +  "0" ;
		  }
			else {
				nandRes = nandRes + "1";
			}
		}
		int x = 0;
		for (int k=0;k<nandRes.length();k++) {
		
			if (nandRes.charAt(k) == '1') {
			x = x + (int) (Math.pow(2, nandRes.length()-k-1));
		}
		}
		String [] addAndVal = new String[2];
		addAndVal[0] = r3 + "";
		addAndVal[1] = x +  "";
		return addAndVal;
	}

	public String[] mul() {
		String[] mulSplit = operands.split(",");
		String mulReg1 = mulSplit[1];
		String mulReg2 = mulSplit[2];
		String mulDestReg = mulSplit[0];
		int r1 = Integer.parseInt(mulReg1.charAt(1) + "");
		int r2 = Integer.parseInt(mulReg2.charAt(1) + "");
		int destreg = Integer.parseInt(mulDestReg.charAt(1) + "");
		r1 =Integer.parseInt( Processor.mainMemory.data[r1]);
		r2 =Integer.parseInt( Processor.mainMemory.data[r2]);
		String [] addAndVal = new String[2];
		addAndVal[0] = destreg+"";
		addAndVal[1] = (r1*r2)+"";
		return addAndVal;
	}
	
	public String[] jalr() {
		String[] jalrSplit = operands.split(",");
		String jalrReg1 = jalrSplit[0];
		String jalrReg2 = jalrSplit[1];
		int curr = Processor.mainMemory.instructionPointer+1;
		int r1 = Integer.parseInt(jalrReg1.charAt(1) + "");
		Processor.mainMemory.data[r1] = curr +"";
		int r2 = Integer.parseInt(jalrReg2.charAt(1) + "");
		r2 = Integer.parseInt(Processor.mainMemory.data[r2]);
		Processor.mainMemory.instructionPointer = r2;
		return null;
	}

	public String[] ret() {
		String retReg = operands;
		int r1 = Integer.parseInt(retReg.charAt(1) + "");
		r1 = Integer.parseInt(Processor.mainMemory.data[r1]);
		Processor.mainMemory.instructionPointer = r1;
		
		return null;

	}

	public String[] jmp() {
		String[] jmpSplit = operands.split(",");
		String jmpReg1 = jmpSplit[0];
		int r1 = Integer.parseInt(jmpReg1.charAt(1) + "");
		r1 = Integer.parseInt( Processor.mainMemory.data[r1]);
		int jmpAdd = (int) Processor.scoreBoard[this.positionInScoreboard][8];
		Processor.mainMemory.instructionPointer = jmpAdd;
		return null;
	}

	public String[] beq() {
		String[] beqSplit = operands.split(",");
		String beqReg1 = beqSplit[0];
		String beqReg2 = beqSplit[1];
		String beqImm = beqSplit[2];
		int r1 = Integer.parseInt(beqReg1.charAt(1) + "");
		int r2 = Integer.parseInt(beqReg2.charAt(1) + "");
		r1 =Integer.parseInt( Processor.mainMemory.data[r1]);
		r2 =Integer.parseInt( Processor.mainMemory.data[r2]);
		if (r1 == r2) {
			Processor.actualBranch = true;
		}
		else  {
			Processor.actualBranch = false;
		}
		
		
		return null;

	}

	public String[] lw() {
		String[] lwSplit = operands.split(",");
		String lwReg1 = lwSplit[1];
		String lwImm = lwSplit[2];
		String lwDestReg = lwSplit[0];
		int r1 = Integer.parseInt(lwReg1.charAt(1) + "");
		int r3 = Integer.parseInt(lwDestReg.charAt(1) + "");
		r1 = Integer.parseInt( Processor.mainMemory.data[r1]);
		int address = (int) Processor.scoreBoard[this.positionInScoreboard][8];
		Object search = null;
		int i = 0;
		for (i =0 ; i<Processor.caches.size();i++ ) {
			search = Processor.caches.get(i).searchData(address, 0);
			if (search != null) {
				Processor.caches.get(i).cacheData(address, i+1, 0, null);
				break;
			}
			if(search == null && i == Processor.caches.size()-1){
				Processor.caches.get(i).cacheData(address, i+1, 0, null);
				search = Processor.caches.get(0).searchData(address, 0);
			}
		}
		String value = search.toString();
		String [] addAndVal = new String[2];
		addAndVal[0] = r3+"";
		addAndVal[1] = value;
		return addAndVal;

	}

	public String[] sw() {
		String[] swSplit = operands.split(",");
		String swReg1 = swSplit[0];
		String swReg2 = swSplit[1];
		String swImm = swSplit[2];
		int r2 = Integer.parseInt(swReg2.charAt(1) + "");
		int r3 = Integer.parseInt(swImm);
		r2 =Integer.parseInt( Processor.mainMemory.data[r2]);
		int address = (int) Processor.scoreBoard[this.positionInScoreboard][8];
		int r1 = Integer.parseInt(swReg1.charAt(1) + "");
		r1 =Integer.parseInt( Processor.mainMemory.data[r1]);
		Object search = null;
		for (int i=0; i<Processor.caches.size();i++){
			search = Processor.caches.get(i).searchData(address, 0);
			if (search != null){
				Processor.caches.get(i).cacheData(address, i+1, 0,search.toString());
			}
			if (search == null && i == Processor.caches.size()-1) {
				Processor.caches.get(i).cacheData(address, i+1, 0,r1+"");
			}
		}
		return null;
	}
}
