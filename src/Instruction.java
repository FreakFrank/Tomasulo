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

	public Instruction(String instruction) {// R0 has index 0 in main memory, R1
											// has index 1 in main memory and so
											// on till R7 having index 7
		String[] InstructionSplitted = instruction.split(" "); // index 0 ->
																// type , index
																// 1-> operands
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
		return null;

	}

	public String[] sub() {
		return null;

	}

	public String[] addi() {
		return null;

	}

	public String[] nand() {
		return null;

	}

	public String[] mul() {
		return null;

	}

	public String[] jalr() {
		return null;

	}

	public String[] ret() {
		return null;

	}

	public String[] jmp() {
		return null;

	}

	public String[] beq() {
		return null;

	}

	public String[] lw() {
		return null;

	}

	public String[] sw() {
		return null;

	}

}
