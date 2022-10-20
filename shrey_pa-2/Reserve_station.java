import java.util.*;

public class Reserve_station {
	int N;
	int S;
	ArrayList<Integer> exect;

	public Reserve_station(int N, int S) {
		this.N = N;
		this.S = S;
		exect = new ArrayList<Integer>();
	}

	public ArrayList<Integer> check_ready(HashMap<Integer, Pointer> mapping, Pointer pointer, Dispatch d) {

		ArrayList<Integer> reorder_buffer = new ArrayList<Integer>();
		int element, i;

		for (i = 0; i < d.order_out.size(); i++) {
			element = d.order_out.get(i);
			pointer = mapping.get(element);

			if (pointer.updt1.contentEquals("R") && pointer.updt2.contentEquals("R")) {
				reorder_buffer.add(element);
			}

		}
		return reorder_buffer;

	}

	public void executed(int issued_val) {

		exect.add(issued_val);
	}

}
