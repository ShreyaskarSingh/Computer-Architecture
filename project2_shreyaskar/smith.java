import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class smith {
//     intializing the instance variable
	int n_bits, n_mispred, total_pred, counter_value, max_count;
	double mispred_rate;
	String trace;
   
	public smith(int num_bits, String trace_file) {
		this.trace = trace_file;
		this.n_bits = num_bits;
		this.n_mispred = 0;
		this.total_pred = 0;
		this.counter_value = (int)Math.pow(2,num_bits-1);
		this.max_count = this.counter_value*2 -1;
		

		input_read();
		print();
	}

	public void prediction(String actual_prediction) {
		String local_prediction = "n";
		this.total_pred++;
		if (this.counter_value > this.max_count / 2)
			local_prediction = "t";
		else
			local_prediction = "n";

		if (!actual_prediction.equals(local_prediction)) {
			this.n_mispred += 1;
		}
//       update the counter table according to threshold
		if (actual_prediction.equals("t")) {
			this.counter_value = Math.min(this.counter_value + 1, this.max_count);
		} else {
			this.counter_value = Math.max(this.counter_value - 1, 0);
		}
	}
// to take the input 
	public void input_read() {

		try {
			File file = new File( trace);

			BufferedReader br = new BufferedReader(new FileReader(file));

			String next_line;

			while ((next_line = br.readLine()) != null) {
				String actual_state = next_line.split(" ")[1];
				prediction(actual_state);

			}

		} catch (Exception ignored) {
			System.out.println(ignored);
		}

	}
// to print the result 
	public void print() {
		System.out.println("OUTPUT");
		System.out.println("Number of predictions:     " + this.total_pred);
		System.out.println("Number of Misprediction:   " + this.n_mispred);
		mispred_rate = ((double)(this.n_mispred)/(double)(this.total_pred))*100;
		System.out.println("Misprediction rate:        " + String.format("%.2f", mispred_rate) + "%");
		System.out.println("FINAL COUNTER CONTENT:     " + this.counter_value);
	}

}
