import java.io.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.lang.Math;

public class sim {
	int N, S, in, tag = 0, count = 0;
	String address, inst, di;

	int[] get_tag = new int[1000];
	String[] issued = new String[1000];
	Stack lifo = new Stack();
	Stack queue = new Stack();
	double ipc;
	HashMap<Integer, Pointer> mapping = new HashMap<Integer, Pointer>();
	ArrayList<Integer> temp_list = new ArrayList<Integer>();

	public static void main(String[] args) {
		new sim().main2(args);
	}

	

	public boolean next_itr(Stack s, ArrayList<Integer> frob, ArrayList<Integer> eq, ArrayList<Integer> il) {

		in++;

		if (s.empty() && frob.isEmpty() && eq.isEmpty() && il.isEmpty()) {

			return false;
		} else {
			return true;
		}

	}
	public void main2(String[] args) {
		int f, d, s, x, j = 0, val = 1;
		Arrays.fill(get_tag, -1);
		Arrays.fill(issued, "R");

		try {
			S = Integer.parseInt(args[0]);
			N = Integer.parseInt(args[1]);
			address = args[2];
			File trace_file = new File( address);
			Scanner file = new Scanner(trace_file);
			Pointer ptr = null;

			while (file.hasNextLine()) {

				inst = file.nextLine();
				lifo.push(inst);
				count++;

			}

			while (true) {
				if (lifo.isEmpty()) {
					break;
				}
				di = (String) lifo.pop();
				queue.push(di);
			}

			Fetch iq = new Fetch(queue, N);

			Dispatch to_alu = new Dispatch(N, S);

			Reserve_station register_buffer = new Reserve_station(N, S);

			while (true) {
				IE(register_buffer, ptr, mapping, get_tag, issued, to_alu, iq);

				IX(to_alu, mapping, ptr, register_buffer);

				ID(to_alu, iq, mapping, get_tag, issued);

				IF(iq, ptr, mapping);
				if (!(next_itr(iq.lifo, iq.in_order, register_buffer.exect, to_alu.order_out))) {
					break;
				}

			}

			ipc = (double) count / (double) in;

			while (true) {
				Pointer curr_instr = null;
				curr_instr = mapping.get(j);
				f = curr_instr.it_n.get(val) - curr_instr.it_n.get(val - 1);
				d = curr_instr.it_n.get(val + 1) - curr_instr.it_n.get(val);
				s = curr_instr.it_n.get(val + 2) - curr_instr.it_n.get(val + 1);
				x = curr_instr.it_n.get(val + 3) - curr_instr.it_n.get(val + 2);
				System.out.println(j + "   " + "fu{" + curr_instr.decision + "} " + " src{" + curr_instr.s1 + ","
						+ curr_instr.s2 + "}" + " dst{" + curr_instr.dest_reg + "} " + "IF{" + curr_instr.it_n.get(0)
						+ "," + f + "} " + "ID{" + curr_instr.it_n.get(1) + "," + d + "} " + "IS{"
						+ curr_instr.it_n.get(2) + "," + s + "} " + "EX{" + curr_instr.it_n.get(3) + "," + x + "} "
						+ "WB{" + curr_instr.it_n.get(4) + "," + "1" + "}");

				j++;
				if (j == mapping.size()) {
					break;
				}
			}

			System.out.println("Number of instructions =    " + count);
			System.out.println("Number of cycles       =    " + in);
			System.out.println("IPC                    =    " + ipc);

		} catch (Exception e) {
			System.out.println(e);
		}

	}

	public void IE(Reserve_station frob, Pointer iterator, HashMap<Integer, Pointer> inst, int[] tag_idx, String[] phase,
			Dispatch di, Fetch fe) {

		int tag, val;
		ArrayList<Integer> ie = new ArrayList<Integer>();

		for (int i = 0; i < frob.exect.size(); i++) {
			ie.add(frob.exect.get(i));
		}

		int len = frob.exect.size();

		for (int i = 0; i < len; i++) {

			tag = frob.exect.get(i);

			iterator = inst.get(tag);

			if (iterator.update == in) {

				ie.remove((Object) (tag));

				temp_list.add(tag);

				iterator.is = "WB";

				iterator.iteration(in);

				if (iterator.dest_reg != -1) {
					val = tag_idx[iterator.dest_reg];

					if (tag == val) {
						phase[iterator.dest_reg] = "R";
					}
				}

				for (int search = 0; search < di.order_out.size(); search++) {
					int inorder_to_wake_up;

					Pointer ins;
					inorder_to_wake_up = di.order_out.get(search);
					ins = inst.get(inorder_to_wake_up);

					if (ins.t1 == tag) {
						ins.updt1 = "R";

					}
					if (ins.t2 == tag) {
						ins.updt2 = "R";

					}

				}

				for (int search = 0; search < fe.in_order.size(); search++) {
					int delay;

					Pointer ins;
					delay = fe.in_order.get(search);
					ins = inst.get(delay);
					if (ins.is.contentEquals("ID")) {
						if (ins.t1 == tag) {
							ins.updt1 = "R";

						}
						if (ins.t2 == tag) {
							ins.updt2 = "R";

						}
					}

				}

			}
		}

		frob.exect.clear();

		for (int i = 0; i < ie.size(); i++) {
			frob.exect.add(ie.get(i));
		}
	}

	public void del(int el, Dispatch dispatch) {

		dispatch.order_out.remove((Object) (el));

		dispatch.sc--;

	}

	ArrayList<Integer> execute_list = new ArrayList<Integer>();

	public ArrayList<Integer> dispatched(HashMap<Integer, Pointer> ptr_inst, Pointer lcl_ptr, Dispatch dispatch) {

		ArrayList<Integer> ready = new ArrayList<Integer>();
		int val_is;

		for (int i = 0; i < dispatch.order_out.size(); i++) {
			val_is = dispatch.order_out.get(i);
			lcl_ptr = ptr_inst.get(val_is);

			if (lcl_ptr.updt1.contentEquals("R") && lcl_ptr.updt2.contentEquals("R")) {
				ready.add(val_is);
			}

		}
		return ready;

	}

	public int func_unit(int i) {
		int f = 0;

		if (i <= this.N + 1) {
			f = i;
		} else if (i > this.N + 1) {
			f = this.N + 1;
		}

		return f;

	}
	public void IF(Fetch f, Pointer action, HashMap<Integer, Pointer> lcl_ptr) {

		int len, cntr;

		if (f.lifo.size() != 0) {

			len = f.lifo.size();

			cntr = q_size(len, f);

			for (int i = 0; i < cntr; i++) {
				String pre;

				pre = (String) f.lifo.pop();

				action = new Pointer(pre);

				action.iteration(in);

				lcl_ptr.put(tag, action);

				action.is = "IF";

				f.in_order.add(tag);
				f.dp_cntr++;
				tag++;

			}

		}

	}

	public void out_of_order(int element_in_the_issue_that_was_moved_to_array_list) {

		execute_list.add(element_in_the_issue_that_was_moved_to_array_list);
	}

	public void IX(Dispatch dispatch, HashMap<Integer, Pointer> action, Pointer lcl_ptr, Reserve_station issue) {

		int j, f_val, target_val;

		ArrayList<Integer> executed = new ArrayList<Integer>();

		j = check_ready(action, lcl_ptr, dispatch);

		f_val = func_unit(j);

		executed = issue.check_ready(action, lcl_ptr, dispatch);

		for (int i = 0; i < f_val; i++) {

			target_val = executed.get(i);

			del(target_val, dispatch);

			issue.executed(target_val);

			lcl_ptr = action.get(target_val);

			lcl_ptr.is = "EX";

			lcl_ptr.iteration(in);

			lcl_ptr.rat();

		}

	}

	public int check_ready(HashMap<Integer, Pointer> instrct, Pointer ref, Dispatch dispatch) {

		int pc = 0, val;
	

		for (int i = 0; i < dispatch.order_out.size(); i++) {
			val = dispatch.order_out.get(i);
			ref = instrct.get(val);

			if (ref.updt1.contentEquals("R") && ref.updt2.contentEquals("R")) {
				pc++;
			}

		}
		return pc;
	}

	public void ID(Dispatch d, Fetch f, HashMap<Integer, Pointer> action, int[] tag, String[] phase) {

		int len, f_val;

		len = d.check_size(f);

		for (int i = 0; i < len; i++) {
			Pointer tag_val = null;

			f_val = f.in_order.get(0);

			tag_val = action.get(f_val);

			if (tag_val.is.contentEquals("ID")) {

				d.order_out.add(f_val);
				d.sc++;

				f.in_order.remove(0);
				f.dp_cntr--;
				tag_val.is = "IS";

				tag_val.iteration(in);

			}

		}

		for (int i = 0; i < f.in_order.size(); i++) {
			int get_tag = f.in_order.get(i);

			Pointer get_tag_val = null;
			get_tag_val = action.get(get_tag);

			if (get_tag_val.is.contentEquals("IF")) {
				String S1_state = null;
				String S2_state = null;

				get_tag_val.is = "ID";

				if (get_tag_val.s1 == -1) {
					S1_state = "R";
				} else if (phase[get_tag_val.s1].contentEquals("R")) {
					S1_state = phase[get_tag_val.s1];
				} else if (phase[get_tag_val.s1].contentEquals("N")) {
					S1_state = phase[get_tag_val.s1];
					get_tag_val.t1 = tag[get_tag_val.s1];

				}

				get_tag_val.updt1 = S1_state;

				if (get_tag_val.s2 == -1) {
					S2_state = "R";
				} else if (phase[get_tag_val.s2].contentEquals("R")) {
					S2_state = phase[get_tag_val.s2];
				} else if (phase[get_tag_val.s2].contentEquals("N")) {
					S2_state = phase[get_tag_val.s2];
					get_tag_val.t2 = tag[get_tag_val.s2];
				}

				get_tag_val.updt2 = S2_state;

				if (get_tag_val.dest_reg != -1) {

					tag[get_tag_val.dest_reg] = get_tag;
					phase[get_tag_val.dest_reg] = "N";

				}

				get_tag_val.iteration(in);
			}

		}

	}

	public int q_size(int stk_l, Fetch fetch) {

		int curr_size;
		int size = 0;
		int i = 0;

		if (stk_l >= N) {
			size = N;
		} else if (stk_l < N) {
			size = stk_l;
		}

		curr_size = (2 * N) - fetch.dp_cntr;

		if (curr_size == 0)
			i = 0;
		else if (curr_size >= size)
			i = size;

		else if (curr_size < size)
			i = curr_size;

		return i;
	}

	

}
