import java.util.ArrayList;

public class MainMemory {
	int entriesPerBlock;
	int pointer;
	String[] data;

	public MainMemory(int blockSize, int address) {

		entriesPerBlock = blockSize / 2;
		pointer = address * entriesPerBlock;
		data = new String[32768];
	}

	public void insertData(ArrayList<String> data) {

	}

	public String[] readBlock() {
		return null;
	}
}
