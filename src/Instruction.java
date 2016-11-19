public class Instruction {
	boolean issue = false;
	boolean dispatch = false;
	boolean execute = false;
	boolean write = false;
	boolean commit = false;
	String type = "";
	String operands = "";
	String[] addressAndValue;

	public Instruction(String instruction) {// R0 has index 0 in main memory, R1
											// has index 1 in main memory and so
											// on till R7 having index 7
		String[] InstructionSplitted = instruction.split(" "); // index 0 ->
																// type , index
																// 1-> operands
		type = InstructionSplitted[0];
		operands = InstructionSplitted[1];
	}

	public String[] execute() {

		switch (type) {
		case "ADD":
			addressAndValue = add();
		case "SUB":
			addressAndValue = sub();
		case "ADDI":
			addressAndValue = addi();
		case "NAND":
			addressAndValue = nand();
		case "MUL":
			addressAndValue = mul();
		case "JALR":
			addressAndValue = jalr();
		case "RET":
			addressAndValue = ret();
		case "JMP":
			addressAndValue = jmp();
		case "BEQ":
			addressAndValue = beq();
		case "LW":
			addressAndValue = lw();
		case "SW":
			addressAndValue = sw();
		default:
			return null;
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
