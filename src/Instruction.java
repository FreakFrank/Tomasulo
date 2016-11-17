public class Instruction {
	boolean issue = false;
	boolean execute = false;
	boolean write = false;
	boolean commit = false;
	String type = "";
	String operands = "";

	public Instruction(String instruction) {// R0 has index 0 in main memory, R1
											// has index 1 in main memory and so
											// on till R7 having index 7
		String[] InstructionSplitted = instruction.split(" "); // index 0 ->
																// type , index
																// 1-> operands
		type = InstructionSplitted[0];
		operands = InstructionSplitted[1];
	}

	public String execute() {

		switch (type) {
		case "ADD":
			return add();
		case "SUB":
			return sub();
		case "ADDI":
			return addi();
		case "NAND":
			return nand();
		case "MUL":
			return mul();
		case "JALR":
			return jalr();
		case "RET":
			return ret();
		case "JMP":
			return jmp();
		case "BEQ":
			return beq();
		case "LW":
			return lw();
		case "SW":
			return sw();
		default:
			return null;
		}
	}

	// each method should return a string in the form of
	// RD,value for example if the RD is R5 and the
	// value is 6 it should be as 5,6

	public String add() {
		return null;

	}

	public String sub() {
		return null;

	}

	public String addi() {
		return null;

	}

	public String nand() {
		return null;

	}

	public String mul() {
		return null;

	}

	public String jalr() {
		return null;

	}

	public String ret() {
		return null;

	}

	public String jmp() {
		return null;

	}

	public String beq() {
		return null;

	}

	public String lw() {
		return null;

	}

	public String sw() {
		return null;

	}

}
