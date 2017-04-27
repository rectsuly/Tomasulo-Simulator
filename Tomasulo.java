
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

/**
 * @author yanqing.qyq 2012-2015@USTC
 * 模板说明：该模板主要提供依赖Swing组件提供的JPanle，JFrame，JButton等提供的GUI。使用“监听器”模式监听各个Button的事件，从而根据具体事件执行不同方法。
 * Tomasulo算法核心需同学们自行完成，见说明（4）
 * 对于界面必须修改部分，见说明(1),(2),(3)
 *
 *  (1)说明：根据你的设计完善指令设置中的下拉框内容
 *	(2)说明：请根据你的设计指定各个面板（指令状态，保留站，Load部件，寄存器部件）的大小
 *	(3)说明：设置界面默认指令
 *	(4)说明： Tomasulo算法实现
 */

public class Tomasulo extends JFrame implements ActionListener{
	/*
	 * 界面上有六个面板：
	 * ins_set_panel : 指令设置
	 * EX_time_set_panel : 执行时间设置
	 * ins_state_panel : 指令状态
	 * RS_panel : 保留站状态
	 * Load_panel : Load部件
	 * Registers_state_panel : 寄存器状态
	 */
	private JPanel ins_set_panel,EX_time_set_panel,ins_state_panel,RS_panel,Load_panel,Registers_state_panel;

	/*
	 * 四个操作按钮：步进，进5步，重置，执行
	 */
	private JButton stepbut,step5but,resetbut,startbut;

	/*
	 * 指令选择框
	 */
	private JComboBox inst_typebox[]=new JComboBox[24];

	/*
	 * 每个面板的名称
	 */
	private JLabel inst_typel, timel, tl1,tl2,tl3,tl4,resl,regl,ldl,insl,stepsl;
	private int time[]=new int[4];

	/*
	 * 部件执行时间的输入框
	 */
	private JTextField tt1,tt2,tt3,tt4;

	private int intv[][]=new int[6][4],cnow,inst_typenow=0;
	private int cal[][]={{-1,0,0},{-1,0,0},{-1,0,0},{-1,0,0},{-1,0,0}};
	private int ld[][]={{0,0},{0,0},{0,0}};
	private int ff[]={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

	/*
	 * (1)说明：根据你的设计完善指令设置中的下拉框内容
	 * inst_type： 指令下拉框内容:"NOP","L.D","ADD.D","SUB.D","MULT.D","DIV.D"…………
	 * regist_table：       目的寄存器下拉框内容:"F0","F2","F4","F6","F8" …………
	 * rx：       源操作数寄存器内容:"R0","R1","R2","R3","R4","R5","R6","R7","R8","R9" …………
	 * ix：       立即数下拉框内容:"0","1","2","3","4","5","6","7","8","9" …………
	 */
	private String  inst_type[]={"NOP","L.D","ADD.D","SUB.D","MULT.D","DIV.D"},
					regist_table[]={"F0","F2","F4","F6","F8","F10","F12","F14","F16"
							,"F18","F20","F22","F24","F26","F28","F30","F32"},
					rx[]={"R0","R1","R2","R3","R4","R5","R6"},
					ix[]={"0","1","2","3","4","5","6","7","8","9","10","11","12",
							"13","14","15","16","17","18","19","20","21","22","23","24"};

	/*
	 * (2)说明：请根据你的设计指定各个面板（指令状态，保留站，Load部件，寄存器部件）的大小
	 * 		指令状态 面板
	 * 		保留站 面板
	 * 		Load部件 面板
	 * 		寄存器 面板
	 * 					的大小
	 */
	private	String  my_inst_type[][]=new String[7][4], my_rs[][]=new String[6][8],
					my_load[][]=new String[4][4], my_regsters[][]=new String[3][17];
	private	JLabel  inst_typejl[][]=new JLabel[7][4], resjl[][]=new JLabel[6][8],
					ldjl[][]=new JLabel[4][4], regjl[][]=new JLabel[3][17];

//构造方法
	public Tomasulo(){
		super("Tomasulo Simulator");

		//设置布局
		Container cp=getContentPane();
		FlowLayout layout=new FlowLayout();
		cp.setLayout(layout);

		//指令设置。GridLayout(int 指令条数, int 操作码+操作数, int hgap, int vgap)
		inst_typel = new JLabel("指令设置");
		ins_set_panel = new JPanel(new GridLayout(6,4,0,0));
		ins_set_panel.setPreferredSize(new Dimension(350, 150));
		ins_set_panel.setBorder(new EtchedBorder(EtchedBorder.RAISED));

		//操作按钮:执行，重设，步进，步进5步
		timel = new JLabel("执行时间设置");
		EX_time_set_panel = new JPanel(new GridLayout(2,4,0,0));
		EX_time_set_panel.setPreferredSize(new Dimension(280, 80));
		EX_time_set_panel.setBorder(new EtchedBorder(EtchedBorder.RAISED));

		//指令状态
		insl = new JLabel("指令状态");
		ins_state_panel = new JPanel(new GridLayout(7,4,0,0));
		ins_state_panel.setPreferredSize(new Dimension(420, 175));
		ins_state_panel.setBorder(new EtchedBorder(EtchedBorder.RAISED));


		//寄存器状态
		regl = new JLabel("寄存器");
		Registers_state_panel = new JPanel(new GridLayout(3,17,0,0));
		Registers_state_panel.setPreferredSize(new Dimension(1360, 75));
		Registers_state_panel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		//保留站
		resl = new JLabel("保留站");
		RS_panel = new JPanel(new GridLayout(6,7,0,0));
		RS_panel.setPreferredSize(new Dimension(700, 150));
		RS_panel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		//Load部件
		ldl = new JLabel("Load部件");
		Load_panel = new JPanel(new GridLayout(4,4,0,0));
		Load_panel.setPreferredSize(new Dimension(400, 100));
		Load_panel.setBorder(new EtchedBorder(EtchedBorder.RAISED));

		tl1 = new JLabel("Load");
		tl2 = new JLabel("加/减");
		tl3 = new JLabel("乘法");
		tl4 = new JLabel("除法");

//操作按钮:执行，重设，步进，步进5步
		stepsl = new JLabel();
		stepsl.setPreferredSize(new Dimension(200, 30));
		stepsl.setHorizontalAlignment(SwingConstants.CENTER);
		stepsl.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		stepbut = new JButton("步进");
		stepbut.addActionListener(this);
		step5but = new JButton("步进5步");
		step5but.addActionListener(this);
		startbut = new JButton("执行");
		startbut.addActionListener(this);
		resetbut= new JButton("重设");
		resetbut.addActionListener(this);
		tt1 = new JTextField("2");
		tt2 = new JTextField("2");
		tt3 = new JTextField("10");
		tt4 = new JTextField("40");

//指令设置
		/*
		 * 设置指令选择框（操作码，操作数，立即数等）的default选择
		 */
		for (int i=0;i<2;i++)
			for (int j=0;j<4;j++){
				if (j==0){
					inst_typebox[i*4+j]=new JComboBox(inst_type);
				}
				else if (j==1){
					inst_typebox[i*4+j]=new JComboBox(regist_table);
				}
				else if (j==2){
					inst_typebox[i*4+j]=new JComboBox(ix);
				}
				else {
					inst_typebox[i*4+j]=new JComboBox(rx);
				}
				inst_typebox[i*4+j].addActionListener(this);
				ins_set_panel.add(inst_typebox[i*4+j]);
			}
		for (int i=2;i<6;i++)
			for (int j=0;j<4;j++){
				if (j==0){
					inst_typebox[i*4+j]=new JComboBox(inst_type);
				}
				else {
					inst_typebox[i*4+j]=new JComboBox(regist_table);
				}
				inst_typebox[i*4+j].addActionListener(this);
				ins_set_panel.add(inst_typebox[i*4+j]);
			}
		/*
		 * (3)说明：设置界面默认指令，根据你设计的指令，操作数等的选择范围进行设置。
		 * 默认6条指令。待修改
		 */
		inst_typebox[0].setSelectedIndex(1);
		inst_typebox[1].setSelectedIndex(3);
		inst_typebox[2].setSelectedIndex(21);
		inst_typebox[3].setSelectedIndex(2);

		inst_typebox[4].setSelectedIndex(1);
		inst_typebox[5].setSelectedIndex(1);
		inst_typebox[6].setSelectedIndex(2);
		inst_typebox[7].setSelectedIndex(3);

		inst_typebox[8].setSelectedIndex(4);
		inst_typebox[9].setSelectedIndex(0);
		inst_typebox[10].setSelectedIndex(1);
		inst_typebox[11].setSelectedIndex(2);

		inst_typebox[12].setSelectedIndex(3);
		inst_typebox[13].setSelectedIndex(4);
		inst_typebox[14].setSelectedIndex(3);
		inst_typebox[15].setSelectedIndex(1);

		inst_typebox[16].setSelectedIndex(5);
		inst_typebox[17].setSelectedIndex(5);
		inst_typebox[18].setSelectedIndex(0);
		inst_typebox[19].setSelectedIndex(3);

		inst_typebox[20].setSelectedIndex(2);
		inst_typebox[21].setSelectedIndex(3);
		inst_typebox[22].setSelectedIndex(4);
		inst_typebox[23].setSelectedIndex(1);

//执行时间设置
		EX_time_set_panel.add(tl1);
		EX_time_set_panel.add(tt1);
		EX_time_set_panel.add(tl2);
		EX_time_set_panel.add(tt2);
		EX_time_set_panel.add(tl3);
		EX_time_set_panel.add(tt3);
		EX_time_set_panel.add(tl4);
		EX_time_set_panel.add(tt4);

//指令状态设置
		for (int i=0;i<7;i++)
		{
			for (int j=0;j<4;j++){
				inst_typejl[i][j]=new JLabel(my_inst_type[i][j]);
				inst_typejl[i][j].setBorder(new EtchedBorder(EtchedBorder.RAISED));
				ins_state_panel.add(inst_typejl[i][j]);
			}
		}
//保留站设置
		for (int i=0;i<6;i++)
		{
			for (int j=0;j<8;j++){
				resjl[i][j]=new JLabel(my_rs[i][j]);
				resjl[i][j].setBorder(new EtchedBorder(EtchedBorder.RAISED));
				RS_panel.add(resjl[i][j]);
			}
		}
//Load部件设置
		for (int i=0;i<4;i++)
		{
			for (int j=0;j<4;j++){
				ldjl[i][j]=new JLabel(my_load[i][j]);
				ldjl[i][j].setBorder(new EtchedBorder(EtchedBorder.RAISED));
				Load_panel.add(ldjl[i][j]);
			}
		}
//寄存器设置
		for (int i=0;i<3;i++)
		{
			for (int j=0;j<17;j++){
				regjl[i][j]=new JLabel(my_regsters[i][j]);
				regjl[i][j].setBorder(new EtchedBorder(EtchedBorder.RAISED));
				Registers_state_panel.add(regjl[i][j]);
			}
		}

//向容器添加以上部件
		cp.add(inst_typel);
		cp.add(ins_set_panel);
		cp.add(timel);
		cp.add(EX_time_set_panel);

		cp.add(startbut);
		cp.add(resetbut);
		cp.add(stepbut);
		cp.add(step5but);

		cp.add(Load_panel);
		cp.add(ldl);
		cp.add(RS_panel);
		cp.add(resl);
		cp.add(stepsl);
		cp.add(Registers_state_panel);
		cp.add(regl);
		cp.add(ins_state_panel);
		cp.add(insl);

		stepbut.setEnabled(false);
		step5but.setEnabled(false);
		ins_state_panel.setVisible(false);
		insl.setVisible(false);
		RS_panel.setVisible(false);
		ldl.setVisible(false);
		Load_panel.setVisible(false);
		resl.setVisible(false);
		stepsl.setVisible(false);
		Registers_state_panel.setVisible(false);
		regl.setVisible(false);
		setSize(820,620);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

/*
 * 点击”执行“按钮后，根据选择的指令，初始化其他几个面板
 */
	public void init(){
		// get value
		for (int i=0;i<6;i++){
			intv[i][0]=inst_typebox[i*4].getSelectedIndex();
			if (intv[i][0]!=0){
				intv[i][1]=2*inst_typebox[i*4+1].getSelectedIndex();
				if (intv[i][0]==1){
					intv[i][2]=inst_typebox[i*4+2].getSelectedIndex();
					intv[i][3]=inst_typebox[i*4+3].getSelectedIndex();
				}
				else {
					intv[i][2]=2*inst_typebox[i*4+2].getSelectedIndex();
					intv[i][3]=2*inst_typebox[i*4+3].getSelectedIndex();
				}
			}
		}
		time[0]=Integer.parseInt(tt1.getText());
		time[1]=Integer.parseInt(tt2.getText());
		time[2]=Integer.parseInt(tt3.getText());
		time[3]=Integer.parseInt(tt4.getText());
		//System.out.println(time[0]);
		// set 0
		my_inst_type[0][0]="指令";
		my_inst_type[0][1]="流出";
		my_inst_type[0][2]="执行";
		my_inst_type[0][3]="写回";


		my_load[0][0]="名称";
		my_load[0][1]="Busy";
		my_load[0][2]="地址";
		my_load[0][3]="值";
		my_load[1][0]="Load1";
		my_load[2][0]="Load2";
		my_load[3][0]="Load3";
		my_load[1][1]="no";
		my_load[2][1]="no";
		my_load[3][1]="no";

		my_rs[0][0]="Time";
		my_rs[0][1]="名称";
		my_rs[0][2]="Busy";
		my_rs[0][3]="Op";
		my_rs[0][4]="Vj";
		my_rs[0][5]="Vk";
		my_rs[0][6]="Qj";
		my_rs[0][7]="Qk";
		my_rs[1][1]="Add1";
		my_rs[2][1]="Add2";
		my_rs[3][1]="Add3";
		my_rs[4][1]="Mult1";
		my_rs[5][1]="Mult2";
		my_rs[1][2]="no";
		my_rs[2][2]="no";
		my_rs[3][2]="no";
		my_rs[4][2]="no";
		my_rs[5][2]="no";

		my_regsters[0][0]="字段";
		for (int i=1;i<17;i++){
			//System.out.print(i+" "+regist_table[i-1];
			my_regsters[0][i]=regist_table[i-1];
			my_regsters[1][i] = "0";

		}
		my_regsters[1][0]="状态";
		my_regsters[2][0]="值";

		for (int i=1;i<7;i++)
		for (int j=0;j<4;j++){
			if (j==0){
				int temp=i-1;
				String disp;
				disp = inst_type[inst_typebox[temp*4].getSelectedIndex()]+" ";
				if (inst_typebox[temp*4].getSelectedIndex()==0) disp=disp;
				else if (inst_typebox[temp*4].getSelectedIndex()==1){
					disp=disp+regist_table[inst_typebox[temp*4+1].getSelectedIndex()]+','+ix[inst_typebox[temp*4+2].getSelectedIndex()]+'('+rx[inst_typebox[temp*4+3].getSelectedIndex()]+')';
				}
				else {
					disp=disp+regist_table[inst_typebox[temp*4+1].getSelectedIndex()]+','+regist_table[inst_typebox[temp*4+2].getSelectedIndex()]+','+regist_table[inst_typebox[temp*4+3].getSelectedIndex()];
				}
				my_inst_type[i][j]=disp;
			}
			else my_inst_type[i][j]="";
		}
		for (int i=1;i<6;i++)
		for (int j=0;j<8;j++)if (j!=1&&j!=2){
			my_rs[i][j]="";
		}
		for (int i=1;i<4;i++)
		for (int j=2;j<4;j++){
			my_load[i][j]="";
		}
		for (int i=2;i<3;i++)
		for (int j=1;j<17;j++){
			my_regsters[i][j]="";
		}
		inst_typenow=0;
		for (int i=0;i<5;i++){
			for (int j=1;j<3;j++) cal[i][j]=0;
			cal[i][0]=-1;
		}
		for (int i=0;i<3;i++)
			for (int j=0;j<2;j++) ld[i][j]=0;
		for (int i=0;i<17;i++) ff[i]=0;
	}

/*
 * 点击操作按钮后，用于显示结果
 */
	public void display(){
		for (int i=0;i<7;i++)
			for (int j=0;j<4;j++){
				inst_typejl[i][j].setText(my_inst_type[i][j]);
			}
		for (int i=0;i<6;i++)
			for (int j=0;j<8;j++){
				resjl[i][j].setText(my_rs[i][j]);
			}
		for (int i=0;i<4;i++)
			for (int j=0;j<4;j++){
				ldjl[i][j].setText(my_load[i][j]);
			}
		for (int i=0;i<3;i++)
			for (int j=0;j<17;j++){
				regjl[i][j].setText(my_regsters[i][j]);
			}
		stepsl.setText("当前周期："+String.valueOf(cnow-1));
	}

	public void actionPerformed(ActionEvent e){
//点击“执行”按钮的监听器
		if (e.getSource()==startbut) {
			for (int i=0;i<24;i++) inst_typebox[i].setEnabled(false);
			tt1.setEnabled(false);tt2.setEnabled(false);
			tt3.setEnabled(false);tt4.setEnabled(false);
			stepbut.setEnabled(true);
			step5but.setEnabled(true);
			startbut.setEnabled(false);
			//根据指令设置的指令初始化其他的面板
			init();
			cnow=1;
			//展示其他面板
			display();
			ins_state_panel.setVisible(true);
			RS_panel.setVisible(true);
			Load_panel.setVisible(true);
			Registers_state_panel.setVisible(true);
			insl.setVisible(true);
			ldl.setVisible(true);
			resl.setVisible(true);
			stepsl.setVisible(true);
			regl.setVisible(true);
		}
//点击“重置”按钮的监听器
		if (e.getSource()==resetbut) {
			for (int i=0;i<24;i++) inst_typebox[i].setEnabled(true);
			tt1.setEnabled(true);tt2.setEnabled(true);
			tt3.setEnabled(true);tt4.setEnabled(true);
			stepbut.setEnabled(false);
			step5but.setEnabled(false);
			startbut.setEnabled(true);
			ins_state_panel.setVisible(false);
			insl.setVisible(false);
			RS_panel.setVisible(false);
			ldl.setVisible(false);
			Load_panel.setVisible(false);
			resl.setVisible(false);
			stepsl.setVisible(false);
			Registers_state_panel.setVisible(false);
			regl.setVisible(false);
		}
//点击“步进”按钮的监听器
		if (e.getSource()==stepbut) {
			core();
			cnow++;
			display();
		}
//点击“进5步”按钮的监听器
		if (e.getSource()==step5but) {
			for (int i=0;i<5;i++){
				core();
				cnow++;
			}
			display();
		}

		for (int i=0;i<24;i=i+4)
		{
			if (e.getSource()==inst_typebox[i]) {
				if (inst_typebox[i].getSelectedIndex()==1){
					inst_typebox[i+2].removeAllItems();
					for (int j=0;j<ix.length;j++) inst_typebox[i+2].addItem(ix[j]);
					inst_typebox[i+3].removeAllItems();
					for (int j=0;j<rx.length;j++) inst_typebox[i+3].addItem(rx[j]);
				}
				else {
					inst_typebox[i+2].removeAllItems();
					for (int j=0;j<regist_table.length;j++) inst_typebox[i+2].addItem(regist_table[j]);
					inst_typebox[i+3].removeAllItems();
					for (int j=0;j<regist_table.length;j++) inst_typebox[i+3].addItem(regist_table[j]);
				}
			}
		}
	}
/*
 * (4)说明： Tomasulo算法实现
 */
	public int load_time[]=new int[3];
	public int ready;
	public void core()
	{
		ready = 1;
		int done = -1;
		for (int i = 0; i < 6; i++)		//对六组指令循环遍历
		{
			String rd = "F" + (inst_typebox[i * 4 + 1].getSelectedIndex() * 2);
			String rs = "F" + (inst_typebox[i * 4 + 2].getSelectedIndex() * 2);
			String rt = "F" + (inst_typebox[i * 4 + 3].getSelectedIndex() * 2);
			
			System.out.println("time: " + cnow);
			
			switch(inst_typebox[i * 4].getSelectedIndex())
			{
			case 1:			// L.D F2, 4 (R2),载入指令
							// 格式为 rt imm rs
				if(my_inst_type[i + 1][1] == "")	//若指令未流出，判断是否满足流出条件
				{
					int remain = -1;
					for (int j = 1; j < 4; j++)
					{
						if(my_load[j][1] == "no")
						{
							remain = j;			//寻找一个空闲的load保留站
							break;
						}
					}
					if(remain > 0)		//找到空闲保留站
					{
						my_load[remain][1] = "Yes";		//修改load保留站状态为忙碌
						//写入load保留站地址
						my_load[remain][2] = inst_typebox[i * 4 + 2].getSelectedIndex() + "";
						//将load保留站名称写入寄存器状态表项
						my_regsters[1][inst_typebox[i * 4 + 1].getSelectedIndex() + 1] = my_load[remain][0];
						my_inst_type[i + 1][1] = cnow + "";		//发射成功,将当前周期写入流出表项
						done = 1;
					}
					else			//没有空闲load保留站
					{
						System.out.println("my_load is full.");
					}
				}
				else				//指令已流出
				{
					if(my_inst_type[i + 1][2] == "")	//指令未执行
					{
						int line = -1;
						for (int j = 1; j < 4; j++)		//找地址长度不大于2的行
						{
							if (my_load[j][1] == "Yes" && (my_load[j][2].length() <= 2))
							{
								line = j;
								break;
							}
						}
						load_time[i] = time[0] - 1;
						//将load保留站的地址表项作修改
						my_load[line][2] = "R[R" + inst_typebox[i * 4 + 3].getSelectedIndex() + "]+"
								+ inst_typebox[i * 4 + 2].getSelectedIndex();
						my_inst_type[i + 1][2] = cnow + "";		//指令执行，将当前周期写入执行表项
					}
					else if (my_inst_type[i + 1][2].length() == 1)	//已经开始执行，但未执行结束
					{
						int remain_t = load_time[i];	
						if ((remain_t--) > 0)		//执行所剩时间减一
						{
							load_time[i] = remain_t;
							if(remain_t == 0)
								//如果执行所剩时间为0，添加箭头和执行结束时间
								{
									my_inst_type[i + 1][2] = my_inst_type[i + 1][2] + "~" + cnow;	//执行表项加入箭头和执行结束周期
									int line = -1;
									for (int j = 1; j < 4; j++)
									{
										if (my_load[j][1] == "Yes")
										{
											line = j;
											break;
										}
									}
									
									my_load[line][3] = "M[" + my_load[line][2] + "]";	//将load保留站的值写入
								}	
						}
						
						
					}
					else if (my_inst_type[i + 1][3] == "")		//执行结束，但未写回
					{
						//保留站r执行结束，且CDB就绪
						my_inst_type[i + 1][3] = "" + cnow;		//指令写回，将当前周期写入写回表项
						int line = -1;
						for (int j = 1; j < 4; j++)
						{
							if (my_load[j][1] == "Yes")
							{
								line = j;
								break;
							}
						}
						//寄存器操作
						for (int j = 1; j < 17; j++)
						{
							if (my_regsters[1][j].equals(my_load[line][0]))
							{
								//向该寄存器写入结果，并其状态设为数据就绪
								my_regsters[2][j] = my_load[line][3];
								my_regsters[1][j] = "0";
							}
						}
						
						String r = my_load[line][0];
		/*				//通过CDB总线广播
						for (int j = 1; j < 17; j++)
						{
							if (my_regsters[1][j].equals(my_load[line][0]))
							{
								my_regsters[2][j] = my_load[line][3];	// 向该寄存器写入结果
								my_regsters[1][j] = "0";		// 把该寄存器的状态置为数据状态
							}
						}		*/
						//保留站操作
						for (int j = 1; j < 6; j++)
						{
							//对于任何一个正在等待该结果作为第一操作数的保留站x，向该保留站的Vj写入结果，置Qj为0，
							//表示该保留站的Vj操作数就绪
							if (my_rs[j][6].equals(r))	//Qj == loadr
							{
								my_rs[j][4] = my_load[line][3];		//Vj == load部件表中的值项
								my_rs[j][6] = "0";		//Qj == 0
								ready = -1;
							}
							//对于等待结果作为第二操作数的保留站，需改变Qk和Vk
							if (my_rs[j][7].equals(r))
							{
								my_rs[j][5] = my_load[line][3];
								my_rs[j][7] = "0";
								ready = -1;
							}
						}
						
						my_load[line][1] = "no";	//改变Load部件中的忙碌为空闲
						my_load[line][2] = "";		//清空
						my_load[line][3] = "";		//清空
					}
				}
				break;		// 对load指令的处理结束
			case 2: 	//ADD.D F0, F0, F0,加法指令
						//格式为 rd rs rt
				if (Execute("ADD", rd, rs, rt, i))	//调用执行子方法，处理加法指令
				{
					done = 1;
				}
				break;		//对 add.d指令的处理结束
				
			case 3:		// SUB.D
				if (Execute("SUB", rd, rs, rt, i))
				{
					done = 1;
				}
				break;		//对sub.d指令的处理结束
				
			case 4:		//MULT.D
				if (Execute("MULT", rd, rs, rt, i))
				{
					done = 1;
				}
				break;
				
			case 5:		//DIV.D
				if (Execute("DIV",rd, rs, rt, i))
				{
					done = 1;
				}
				break;
				
			default:
				System.out.println("NOP");		//NOP空指令,什么都不做
				break;
			}
			if (done > 0)
			{
				break;
			}
		}
			
	}
	//对于加、减、乘、除指令，core方法中所调用的方法
	boolean Execute(String op, String rd, String rs, String rt, int i)
	{
		int done = -1;
		
		if(my_inst_type[i + 1][1] == "")	//若指令未流出
		{
			instIssue(op, rd, rs, rt);		//发射指令
			my_inst_type[i + 1][1] = "" + cnow;		//发射成功
			done = 1;
		}
		else if (my_inst_type[i + 1][2] == "")	//若指令未执行
		{
			if (instExcute(op, rd, rs, rt))		//指令执行成功
				my_inst_type[i + 1][2] = "" + cnow;
			else
				return false;
				
		}
		else if (my_inst_type[i + 1][2].length() <= 2)	//指令已执行，未执行结束
		{
			//修改执行的状态
			int line = -1;
			for (int j = 1; j < 6; j++)
			{
				if(my_rs[j][3] == op)
					line = j;
			}
			
			int remain_time = Integer.parseInt(my_rs[line][0]);	
			if ((remain_time--) > 0)		//执行所剩时间减一
			{
				my_rs[line][0] = remain_time + "";
				if(remain_time == 0)
					//如果执行所剩时间为0，添加箭头和执行结束时间
					my_inst_type[i + 1][2] = my_inst_type[i + 1][2] + "~" + cnow;	
			}
			
		}
		else if (my_inst_type[i + 1][3] == "")		//未开始写回
		{
			instWB(op, rd, rs, rt);
			my_inst_type[i + 1][3] = cnow + "";		//写回成功
			ready = -1;		//设置ready状态为未准备好
		}
		
		if (done > 0)
			return true;
		else
			return false;
		
	}
	
	//Excute方法中所调用的instIssue指令发射方法
	void instIssue(String op, String rd, String rs, String rt)
	{
		int remain = -1;
		//选择空闲的保留站
		if (op.equals("ADD") || op.equals("SUB"))
		{
			for (int i = 1; i < 4; i++)		//对Add1,2,3三个保留站遍历
			{
				if (my_rs[i][2].equals("no"))	//空闲
				{
					remain = i;
					break;
				}
			}
		}
		else
		{
			for (int i = 4; i < 6; i++)		//对Mult1,2两个保留站遍历
			{
				if(my_rs[i][2].equals("no"))
				{
					remain = i;
					break;
				}
			}
		}
		
		if (remain > 0)		//找到空闲保留站
		{
			String r = my_rs[remain][1];	//保留站名称
			for (int i = 1; i < 17; i++)
			{
				//检查第一个操作数是否就绪
				if (my_regsters[0][i].equals(rs)){
					if (my_regsters[1][i].equals("0"))
					{
						//第一个操作数就绪，把寄存器rs中的操作数取到当前保留站Vj
						if(my_regsters[2][i].equals(""))
							my_rs[remain][4] = "R[" + my_regsters[0][i] + "]";
						else
							my_rs[remain][4] = my_regsters[2][i];
						my_rs[remain][6] = "0";		//Qj 置为0
					}
					else	//未就绪
					{
						//寄存器换名，把将产生该操作数的保留站的编号放入当前保留站的Qj中
						my_rs[remain][6] = my_regsters[1][i];
					}
				}
				//检查第二个操作数是否就绪
				if (my_regsters[0][i].equals(rt))
				{
					if (my_regsters[1][i].equals("0"))
					{
						//第二个操作数就绪，把寄存器rt中的操作数取到当前保留站Vk
						if(my_regsters[2][i].equals(""))
							my_rs[remain][5] = "R[" + my_regsters[0][i] + "]";
						else
							my_rs[remain][5] = my_regsters[2][i];
						my_rs[remain][7] = "0";		//Qk 置为0
					}
					else	//未就绪
					{
						//寄存器换名，把将产生该操作数的保留站的编号放入当前保留站的Qk中
						my_rs[remain][7] = my_regsters[1][i];
					}
				}
			}
			
			my_rs[remain][2] = "Yes";	//修改状态为忙碌
			my_rs[remain][3] = op;
			for (int i = 1; i < 17; i++)
			{
				if (my_regsters[0][i].equals(rd))
					my_regsters[1][i] = r;		//目的寄存器状态置为保留站名称
			}
			
		}
//		else 	//未找到空闲运算保留站
//		{
//			System.out.println("未找到空闲保留站。");
//		}
//		if (done > 0)
//			return true;
//		else
//			return false;
	}
	
	//Excute方法中所调用的instExcute指令执行方法，计算执行时间
	boolean instExcute(String op, String rd, String rs, String rt)
	{
		int line = -1;
		for (int i = 1; i < 6; i++)
			if (my_rs[i][3].equals(op))
				line = i;
		//若Time为空，判断运算是否符合执行条件: Qj = Qk == 0
		if(my_rs[line][6].equals("0") && my_rs[line][7].equals("0") && ready == 1)
		{
			//准备就绪，判断指令类型，添加时间
			if (op == "ADD")
				my_rs[line][0] = Integer.toString(time[1]-1);
			else if (op == "SUB")
				my_rs[line][0] = Integer.toString(time[1]-1);
			else if (op == "MULT")
				my_rs[line][0] = Integer.toString(time[2]-1);
			else if (op == "DIV")
				my_rs[line][0] = Integer.toString(time[3]-1);
			return true;
		}
		else
			return false;
	}
	
	//Excute方法中所调用的instWB指令写回方法
	void instWB(String op, String rd, String rs, String rt)
	{
		int line = -1;
		for (int i = 1; i < 6; i++)
			if (my_rs[i][3] == op)
				line = i;
		
		String r = my_rs[line][1];		//保留站名称
		for(int i = 1; i < 17; i++)
		{
			//遍历等待写回该结果的寄存器
			if(my_regsters[1][i].equals(r))
			{
				//向该寄存器写入结果
				if (op == "ADD")
					my_regsters[2][i] = my_rs[line][4] + "+" + my_rs[line][5];
				else if (op == "SUB")
					my_regsters[2][i] = my_rs[line][4] + "-" + my_rs[line][5];
				else if (op == "MULT")
					my_regsters[2][i] = my_rs[line][4] + "*" + my_rs[line][5];
				else if (op == "DIV")
					my_regsters[2][i] = my_rs[line][4] + "/" + my_rs[line][5];
				my_regsters[1][i] = "0";	//把该寄存器的状态值置为就绪	
			}	
		}
		
		for (int i = 1; i < 6; i++)	
		{
			//遍历等待该结果作为第一个操作数的保留站
			if (my_rs[i][6].equals(r))
			{
				//向该保留站的Vj写入结果
				if (op == "ADD")
					my_rs[i][4] = my_rs[line][4] + "+" + my_rs[line][5];
				else if (op == "SUB")
					my_rs[i][4] = my_rs[line][4] + "-" + my_rs[line][5];
				else if (op == "MULT")
					my_rs[i][4] = my_rs[line][4] + "*" + my_rs[line][5];
				else if (op == "DIV")
					my_rs[i][4] = my_rs[line][4] + "/" + my_rs[line][5];
				//置Qj为0
				my_rs[i][6] = "0";
			}
		}
		
		for (int i = 1; i < 6; i++)	
		{
			//遍历等待该结果作为第二个操作数的保留站
			if (my_rs[i][7].equals(r))
			{
				//向该保留站的Vk写入结果
				if (op == "ADD")
					my_rs[i][5] = my_rs[line][4] + "+" + my_rs[line][5];
				else if (op == "SUB")
					my_rs[i][5] = my_rs[line][4] + "-" + my_rs[line][5];
				else if (op == "MULT")
					my_rs[i][5] = my_rs[line][4] + "*" + my_rs[line][5];
				else if (op == "DIV")
					my_rs[i][5] = my_rs[line][4] + "/" + my_rs[line][5];
				//置Qk为0
				my_rs[i][7] = "0";
			}
		}
		//释放当前保留站，设置为空闲状态
		my_rs[line][0] = "";
		my_rs[line][2] = "no";
		my_rs[line][3] = "";
		my_rs[line][4] = "";
		my_rs[line][5] = "";
		my_rs[line][6] = "";
		my_rs[line][7] = "";
	}

	public static void main(String[] args) {
		new Tomasulo();
	}

}
