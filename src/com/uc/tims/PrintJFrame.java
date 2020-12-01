package com.uc.tims;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableColumn;
import net.proteanit.sql.DbUtils;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTable;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;

import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.SwingConstants;
import java.awt.Cursor;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

public class PrintJFrame extends JFrame {

	private JPanel contentPane;
	private JTextField txtprint;
	private JTable table;
	

	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;
	private Connection connection = null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					PrintJFrame frame = new PrintJFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * 
	 * 
	 */

	public PrintJFrame() {

		setTitle("Print details");
		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/tims.png")));

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setBounds(100, 100, 1093, 602);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JComboBox comboBoxPrint = new JComboBox();
		comboBoxPrint.setFont(new Font("Dialog", Font.BOLD, 15));
		comboBoxPrint.setModel(new DefaultComboBoxModel(new String[] { "park", "parkno" }));
		comboBoxPrint.setBounds(29, 68, 212, 35);
		contentPane.add(comboBoxPrint);

		JScrollPane scrollPane = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		scrollPane.setBackground(UIManager.getColor("Button.background"));
		scrollPane.setViewportBorder(new LineBorder(new Color(0, 0, 0)));
		scrollPane.setBounds(29, 133, 1019, 361);
		contentPane.add(scrollPane);

		table = new JTable();
		scrollPane.setViewportView(table);
		table.setRowHeight(40);
		/*
		 * DefaultTableCellRenderer renderer = (DefaultTableCellRenderer)
		 * table.getDefaultRenderer(Object.class);
		 * renderer.setHorizontalAlignment(SwingConstants.CENTER);
		 */

		JLabel lblNewLabel = new JLabel("Provide details to print");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Dialog", Font.BOLD, 18));
		lblNewLabel.setBounds(419, 5, 354, 30);
		contentPane.add(lblNewLabel);

		txtprint = new JTextField();
		txtprint.setFont(new Font("Dialog", Font.BOLD, 15));
		txtprint.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				txtprint.setText(txtprint.getText().toUpperCase());
				try {
					
					connection = MySQLConnection.establishMySqlConnection();
					
					String selection = (String) comboBoxPrint.getSelectedItem();
					String query = "SELECT `park`,`parkno`,`wheelno`,`name`,`address`,`nic`,`phoneno`,`gs` FROM `driver` WHERE `"
							+ selection + "` = ?";
					System.out.println(query);
					preparedStatement = connection.prepareStatement(query);
					preparedStatement.setString(1, txtprint.getText());
					resultSet = preparedStatement.executeQuery();

					table.setModel(DbUtils.resultSetToTableModel(resultSet));

					SearchJFrame.setJTableColumnsWidth(table, 1024, 5, 5, 10, 20, 30, 10, 15, 5);

				} catch (SQLException e1) {
					e1.printStackTrace();
				} finally {
					try {
						preparedStatement.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
					try {
						resultSet.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
					try {
						connection.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		txtprint.setBounds(357, 68, 340, 35);
		contentPane.add(txtprint);
		txtprint.setColumns(10);

		JButton btnPrint = new JButton("Print");
		btnPrint.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnPrint.setFont(new Font("Dialog", Font.BOLD, 15));
		btnPrint.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MessageFormat header = new MessageFormat("HDUC:Three-Wheel Information");
				MessageFormat footer = new MessageFormat("Generated by TIMS " + "-{0}-");
				try {
					table.print(JTable.PrintMode.FIT_WIDTH, header, footer);
				} catch (PrinterException e1) {
					JOptionPane.showMessageDialog(null, "Printing process has failed. Try again.");
					e1.printStackTrace();
				}

			}
		});
		btnPrint.setBounds(241, 506, 114, 35);
		contentPane.add(btnPrint);

		JButton btnBack = new JButton("Back\n");
		btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnBack.setFont(new Font("Dialog", Font.BOLD, 15));
		btnBack.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DashboardJFrame dashboardJFrame = new DashboardJFrame();
				dashboardJFrame.setVisible(true);
				dashboardJFrame.setLocationRelativeTo(null);
				dispose();
			}
		});
		btnBack.setBounds(29, 506, 114, 35);
		contentPane.add(btnBack);

	}

	static void setJTableColumnsWidth(JTable table, int tablePreferredWidth, double... percentages) {
		double total = 0;
		for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
			total += percentages[i];
		}

		for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
			TableColumn column = table.getColumnModel().getColumn(i);
			column.setPreferredWidth((int) (tablePreferredWidth * (percentages[i] / total)));
		}
	}

}
