public class Cache {
	int bytesSize;
	int noOfInstructions;
	int BlockSize;
	int Associativity;
	int miss = 0;
	int hit = 0;
	int tempTagLength;
	int tempIndexLength;
	int tempOffsetLength;
	int tempTag;
	int tempIndex;
	int tempOffset;
	Object[] dData;
	Object[] iData;
	boolean[] iValidBit;
	boolean[] dValidBit;
	boolean[] iDirtyBit;
	boolean[] dDirtyBit;
	int[] iTag;
	int[] dTag;
	int wpHit; // in case of of 0 -> "write through", in case of 1 ->
	// "write back"
	int wpMiss;
	int accessTime;
	int tempPointer;

	public Cache(int size, int BlockSize, int Associativity, int wpHit,
			int wpMiss, int accessTime) {
		this.noOfInstructions = size / 2;// since each instruction is
		// 2 bytes
		this.BlockSize = BlockSize;// block size is in bytes
		this.bytesSize = size;
		this.Associativity = Associativity;// 1->direct mapped, >1 set
		// associative, == size/2 means full
		// associative
		dData = new Object[noOfInstructions];
		iData = new Object[noOfInstructions];
		dValidBit = new boolean[noOfInstructions];
		iValidBit = new boolean[noOfInstructions];
		iDirtyBit = new boolean[noOfInstructions];
		dDirtyBit = new boolean[noOfInstructions];
		dTag = new int[noOfInstructions];
		iTag = new int[noOfInstructions];
		this.wpHit = wpHit;
		this.wpMiss = wpMiss;
		this.accessTime = accessTime;
	}

	public Object searchData(int address, int cType) { /*
	 * cType = 0 -> DCache,
	 * cType = 1 -> ICache
	 */
		String binaryValue = Integer.toBinaryString(address);

		tempIndexLength = (int) (Math.log(bytesSize
				/ (Associativity * BlockSize)) / Math.log(2));
		tempOffsetLength = (int) (Math.log(BlockSize / 2) / Math.log(2));
		tempTagLength = 32 - (tempIndexLength + tempOffsetLength);

		while (binaryValue.length() < 32) {
			binaryValue = '0' + binaryValue;
		}

		tempOffset = binaryToInteger(binaryValue.substring(tempTagLength
				+ tempIndexLength, binaryValue.length()));
		tempIndex = binaryToInteger(binaryValue.substring(tempTagLength,
				tempTagLength + tempIndexLength));
		tempTag = binaryToInteger(binaryValue.substring(0, tempTagLength));

		int pointer = (tempIndex * (BlockSize / 2) * Associativity)
				+ tempOffset;

		for (int i = Associativity; i > 0; i--) {

			if (cType == 0) {
				if (dValidBit[pointer] == true) {
					if (dTag[pointer] == tempTag) {
						hit++;
						tempPointer = pointer;
						return dData[pointer];
					}
				}
				pointer += BlockSize / 2;
			} else { // ctype==1 so searching in ICache
				if (iValidBit[pointer] == true) {
					if (iTag[pointer] == tempTag) {
						hit++;
						tempPointer = pointer;
						return iData[pointer];
					}
				}
				pointer += BlockSize / 2;
			}

		}

		miss++;
		return null;
	}

	public void cacheData(int address, int level, int cType, String data) {

		String binaryValue = Integer.toBinaryString(address);
		tempIndexLength = (int) (Math.log(bytesSize
				/ (Associativity * BlockSize)) / Math.log(2));
		tempOffsetLength = (int) (Math.log(BlockSize / 2) / Math.log(2));
		tempTagLength = 32 - (tempIndexLength + tempOffsetLength);
		while (binaryValue.length() < 32) {
			binaryValue = '0' + binaryValue;
		}
		tempOffset = binaryToInteger(binaryValue.substring(tempTagLength
				+ tempIndexLength, binaryValue.length()));
		tempIndex = binaryToInteger(binaryValue.substring(tempTagLength,
				tempTagLength + tempIndexLength));
		tempTag = binaryToInteger(binaryValue.substring(0, tempTagLength));

		int index = this.FindIndexToCache(tempIndex, cType);

		if (data == null) { // no data

			if (cType == 0 ? dValidBit[index] == true
					: iValidBit[index] == true) {
				this.removeToBeReplaced(index, level, cType); // if block was
				// not empty
				// (replacing a
				// block)
			}

			// getting block from next level and writing it into cache
			String offset;

			int a = (address / (BlockSize / 2));
			a *= BlockSize / 2;
			for (int b = 0; b < BlockSize / 2; b++) {

				if (b == 0) {
					offset = "";
				} else {
					offset = Integer.toBinaryString(b);
				}
				while (offset.length() < tempOffset) {
					offset = '0' + offset;
				}

				address = binaryToInteger((binaryValue.substring(0,
						tempTagLength)
						+ binaryValue.substring(tempTagLength, tempTagLength
								+ tempIndexLength) + offset));

				if (Processor.caches.size() > level) { // getting data from next
					// level of caches
					if (cType == 0) {// dCache
						dData[index + b] = Processor.caches.get(level)
								.searchData(address, cType);
						dValidBit[index + b] = true;
						dDirtyBit[index + b] = true;
						dTag[index + b] = tempTag;
					} else {// iCache
						iData[index + b] = Processor.caches.get(level)
								.searchData(address, cType);
						iValidBit[index + b] = true;
						iDirtyBit[index + b] = true;
						iTag[index + b] = tempTag;

					}
				} else { // getting data from main memory
					int c = a + b;
					if (cType == 0) {// dCache
						dData[index + b] = Processor.mainMemory.data[c];
						dValidBit[index + b] = true;
						dDirtyBit[index + b] = true;
						dTag[index + b] = tempTag;

					} else {// iCache
						System.out.println(index + b + "icache index + b " + c
								+ "a+b");
						System.out.println(a + "a");
						System.out.println(Processor.mainMemory.data[c]);
						iData[index + b] = Processor.mainMemory.data[c];
						iValidBit[index + b] = true;
						iDirtyBit[index + b] = true;
						iTag[index + b] = tempTag;

					}

				}

			}
			if (level > 1) {
				Processor.caches.get(level - 2).cacheData(address, level - 1,
						cType, data);
			}

		} else { // input data has value

			if (this.searchData(address, cType) == null) { // write miss
				if (cType == 0 ? dValidBit[index] == true
						: iValidBit[index] == true) { // replacing existing
					// block

					this.removeToBeReplaced(index, level, cType);

					if (cType == 0) { // dCache
						dData[index] = data;
						dValidBit[index] = true;
						dDirtyBit[index] = true; // in case of write through the
						// dirty bit array won't
						// affect anything because
						// we don't check on it
						dTag[index] = tempTag;

					} else {// iCache
						iData[index] = data;
						iValidBit[index] = true;
						iDirtyBit[index] = true;
						iTag[index] = tempTag;

					}

				} else {// inserting to empty block
					if (cType == 0) { // dcache
						dData[index] = data;
						dValidBit[index] = true;
						dDirtyBit[index] = true;
						dTag[index] = tempTag;

					} else {// icache
						iData[index] = data;
						iValidBit[index] = true;
						iDirtyBit[index] = true;
						iTag[index] = tempTag;

					}
				}

			} else { // write hit
				if (wpHit == 0) {// write through
					if (cType == 0) {
						dData[tempPointer] = data;
						dValidBit[tempPointer] = true;
						dTag[tempPointer] = tempTag;

					} else {
						iData[tempPointer] = data;
						iValidBit[tempPointer] = true;
						iTag[tempPointer] = tempTag;

					}

					// if (Processor.caches.size() > level)
					// Processor.caches.get(level).cacheData(address,
					// level + 1, cType, data);
					// else
					// Processor.mainMemory.data[address] = data;

				} else { // write back

					if (cType == 0) {
						dData[tempPointer] = data;
						dValidBit[tempPointer] = true;
						dDirtyBit[tempPointer] = true;
						dTag[tempPointer] = tempTag;

					} else {
						iData[tempPointer] = data;
						iValidBit[tempPointer] = true;
						iDirtyBit[tempPointer] = true;
						iTag[tempPointer] = tempTag;

					}
				}
			}
		}
		System.out.println("The level is " + level);


	}

	public int FindIndexToCache(int index, int cType) {
		int pointer = (index * (BlockSize / 2) * Associativity);

		if (Associativity == 1)
			return pointer;

		for (int a = Associativity; a > 0; a--) {
			if (cType == 0) { // dcache
				if (dValidBit[pointer] == false)
					return pointer;
			} else { // iCache
				if (iValidBit[pointer] == false)
					return pointer;
			}
			pointer += (BlockSize / 2);
		}

		return (index * (BlockSize / 2) * Associativity)
				+ (Associativity * (BlockSize / 2) * ((int) Math.random()));

	}

	public void removeToBeReplaced(int indexInCacheArray, int level, int cType) {

		if ((wpHit == 0)
				|| ((wpHit == 1) && (cType == 0 ? dDirtyBit[indexInCacheArray] == true
				: iDirtyBit[indexInCacheArray] == true))) { // if write
			// through
			// or write
			// back &
			// dirty==true
			int indexLength = (int) (Math.log(bytesSize
					/ (Associativity * BlockSize)) / Math.log(2));
			int offsetLength = (int) (Math.log(BlockSize / 2) / Math.log(2));
			int tagLength = 32 - (tempIndexLength + tempOffsetLength);

			String tag = cType == 0 ? Integer
					.toBinaryString(dTag[indexInCacheArray]) : Integer
					.toBinaryString(iTag[indexInCacheArray]);
					while (tag.length() < tagLength) {
						tag = '0' + tag;
					}

					String index = null;
					if (tempIndexLength > 0) {
						index = Integer.toBinaryString(indexInCacheArray
								/ (((int) Associativity * (BlockSize / 2))));
						while (index.length() < indexLength) {
							index = '0' + index;
						}
					}

					int address;
					String offset;

					for (int b = 0; b < BlockSize / 2; b++) {

						if (b == 0) {
							offset = "";
						} else {
							offset = Integer.toBinaryString(b);
						}
						while (offset.length() < offsetLength) {
							offset = '0' + offset;
						}

						if (index != null)
							address = binaryToInteger((tag + index + offset));
						else
							address = binaryToInteger((tag + offset));
						// String data = (cType == 0)?dData[indexInCacheArray + b]
						// .toString() : iData[indexInCacheArray + b].toString();
						String data = "";
						if (cType == 0 && dData[indexInCacheArray + b] != null) {
							data = dData[indexInCacheArray + b].toString();
						}
						if (cType == 1 && iData[indexInCacheArray + b] != null) {
							data = iData[indexInCacheArray + b].toString();
						}

						if (Processor.caches.size() > level) {
							Processor.caches.get(level).cacheData(address, level + 1,
									cType, data);
						} else {
							Processor.mainMemory.data[address] = data;
						}
					}
		}// if dirty==false we just ignore
	}

	public static int binaryToInteger(String binary) {
		char[] numbers = binary.toCharArray();
		int result = 0;
		for (int i = numbers.length - 1; i >= 0; i--) {
			if (numbers[i] == '1')
				result += Math.pow(2, (numbers.length - i - 1));
		}
		return result;
	}

}