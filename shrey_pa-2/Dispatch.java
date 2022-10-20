import java.util.*;

public class Dispatch {
	int N, S, size, sc;

	public Dispatch(int N, int S) {

		this.N = N;
		this.S = S;
		size = this.S;

	}

	public int check_size(Fetch fetch) {
		int size = 0;

		int actual_size = fetch.in_order.size();
		int rem_spc = S - this.sc;

		if (actual_size >= rem_spc) {
			size = rem_spc;
		} else if (actual_size < rem_spc) {
			size = actual_size;
		}

		return size;

	}

	ArrayList<Integer> order_out = new ArrayList<Integer>();

}
