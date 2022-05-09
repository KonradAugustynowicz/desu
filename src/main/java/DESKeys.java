public class DESKeys {

	private final boolean[][] keys;

	public DESKeys(boolean[] key) {
		keys = new boolean[16][48];
		boolean[] left = new boolean[28];
		boolean[] right = new boolean[28];

		for (int i = 0; i < 28; i++) {

			left[i] = key[Permutations.P1[i] - 1];

			right[i] = key[Permutations.P1[i + 28] - 1];
		}

		for (int i = 0; i < 16; i++) {

			for (int j = 0; j < Permutations.shiftBits[i]; j++) {
				left = leftShift(left);
				right = leftShift(right);
			}
			boolean[] tmpKey = new boolean[56];

			for (int j = 0; j < 28; j++) {
				tmpKey[j + 28] = right[j];
				tmpKey[j] = left[j];
			}

			for (int j = 0; j < 48; j++) {
				keys[i][j] = tmpKey[Permutations.P2[j] - 1];
			}
		}
	}
	public boolean[] leftShift(boolean[] part) {
		boolean tmp = part[0];
		System.arraycopy(part, 1, part, 0, 27);
		part[27] = tmp;
		return part;
	}

	public boolean[][] getKeys() {
		return keys;
	}
}
