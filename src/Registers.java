public class Registers {

	int[] registers;

	public Registers() {
		registers = new int[8];
		registers[0] = 0;
	}

	public void set(int regNum, int data) {
		if (regNum == 0)
			return;
		registers[regNum] = data;
	}

	public int get(int regNum) {
		return registers[regNum];
	}
}
