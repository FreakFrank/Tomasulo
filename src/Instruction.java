public class Instruction {
	boolean issue = false;
	boolean execute = false;
	boolean write = false;
	boolean commit = false;
	String type = "";
	String operands = "";

	public Instruction(String instruction) {//R0 has index 0 in main memory, R1 has index 1 in main memory and so on till R7 having index 7
		String[] InstructionSplitted = instruction.split(" "); //index 0 -> type , index 1-> operands
		type = InstructionSplitted[0];
		operands = InstructionSplitted[1];
	}

	public void execute() {

		switch (type) {
		case "ADD":
			add();
			break;
		case "SUB":
			sub();
			break;
		case "ADDI":
			addi();
			break;
		case "NAND":
			nand();
			break;
		case "MUL":
			mul();
			break;
		case "JALR":
			jalr();
			break;
		case "RET":
			ret();
			break;
		case "JMP":
			jmp();
			break;
		case "BEQ":
			beq();
			break;
		case "LW":
			lw();
			break;
		case "SW":
			sw();
			break;
		}
	}

	public void add() {

	}

	public void sub() {

	}

	public void addi() {

	}

	public void nand() {

	}

	public void mul() {

	}

	public void jalr() {

	}

	public void ret() {

	}

	public void jmp() {

	}

	public void beq() {

	}

	public void lw() {

	}

	public void sw() {

	}

}
