public class Instruction {
	boolean issue = false;
	boolean execute = false;
	boolean write = false;
	boolean commit = false;
	String type = "";
	String operands = "";

	public Instruction(String instruction) {
		String[] array = instruction.split(" ");
		type = array[0];
		operands = array[1];
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


