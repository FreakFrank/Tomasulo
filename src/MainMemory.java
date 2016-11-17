import java.util.ArrayList;

public class MainMemory {
	int entriesPerBlock;
	int instructionPointer;
	String[] data;
	int accessTime;

	public MainMemory(int blockSize, int instructionPointer, int accessTime) {

		entriesPerBlock = blockSize / 2;
		instructionPointer = instructionPointer * entriesPerBlock;
		data = new String[32768];// each index is 2 bytes
		this.accessTime = accessTime;
	}

	public void insertData(ArrayList<String> data) {
	}

	public String[] readBlock() {
		return null;
	}
}
