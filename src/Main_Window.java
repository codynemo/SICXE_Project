import java.awt.EventQueue;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;


@SuppressWarnings("serial")
public class Main_Window extends JFrame {

	protected static File cf;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main_Window frame = new Main_Window();
					frame.setVisible(true);
					/*File chosenFile = new File("D:\\test.txt");  
					Main_Window.cf = chosenFile;*/                 // For Testing Proposes
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Main_Window() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);


		JButton btnSelectFile = new JButton("Select File");
		btnSelectFile.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt", "text");
				chooser.setFileFilter(filter);
				chooser.showOpenDialog(null);
				File chosenFile = chooser.getSelectedFile();
				Main_Window.cf = chosenFile;
			}
		});
		btnSelectFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnSelectFile.setBounds(170, 50, 89, 23);
		contentPane.add(btnSelectFile);

		JButton btnRun = new JButton("Run");
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try
				{
					Pass1 p1 = new Pass1();
					p1.LocationCounter(Main_Window.cf);
					p1.SaveToFile();
				}
				catch(Exception e)
				{
					e.printStackTrace();
					//JOptionPane.showMessageDialog(rootPane, "No File Selected, Please select a text File! or maybe something went wrong with the processing :)");
				}
			}
		});
		btnRun.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		btnRun.setBounds(170, 124, 89, 23);
		contentPane.add(btnRun);
	}
}
