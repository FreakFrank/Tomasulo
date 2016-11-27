public class MainMemory {
	int entriesPerBlock;
	int instructionPointer;
	int initialPointer;
	String[] data;
	int accessTime;
	int noOfaccesses = 0;

	public MainMemory(int blockSize, int instructionPointer, int accessTime) {

		entriesPerBlock = blockSize / 2;
		instructionPointer = instructionPointer * entriesPerBlock;
		initialPointer = instructionPointer;
		data = new String[32768];// each index is 2 bytes
		this.accessTime = accessTime;
	}

	public void insertData(int address, int data) {
		this.data[address] = data + "";
	}
	public String[] readBlock(int startAddress) {
		noOfaccesses++;
		String [] readedBlock = new String[this.entriesPerBlock];
		
		for(int i = 0; i<entriesPerBlock; i++){
			readedBlock [i] = this.data[startAddress+i];
		}
		return readedBlock;
	}
}
