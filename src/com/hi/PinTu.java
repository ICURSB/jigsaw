package com.hi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * 拼图游戏是存在不可完成的拼图的
 * 构造函数里进行了处理，防止这种情况。
 * @author ICURSB
 *
 */
public class PinTu{         
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		int [][] aa = {{1,8,7},{4,9,3},{5,6,2}};//20步
		int [][] bb = {{6,4,7},{8,3,2},{9,5,1}};//30步
		int [][] b2 = {{9,5,2},{3,4,7},{1,6,8}};//完不成的
		int [][] b3 = {{9,8,7},{6,5,4},{3,2,1}};
		Sheet sheet = new Sheet(bb);
		

		sheet.print();
		long time1 = System.currentTimeMillis();
		Sheet[] result = StateNode.getResult(sheet);
		System.out.println("求解耗时：" + (System.currentTimeMillis() - time1));
		if(result == null) {
			System.out.println("此拼图无解。");
		}
		else{
			System.out.println("*******还原拼图开始*******");
			
			int k = 1;//k为第几步
			for (int i = result.length - 1; i >= 0 ; i--) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				result[i].print(k++);
			}
		}
		//下面部分为正常游戏
/*		Scanner sc = new Scanner(System.in);
		//Sheet sheet = new Sheet();

		for(String way = "";!sheet.check();){
			sheet.print();
			way = sc.next();
			switch(way){
				case "a":sheet.left();break;
				case "w":sheet.up();break;
				case "s":sheet.down();break;
				case "d":sheet.right();break;
				default: break;
			}
		}
		sheet.print();
		sc.close();
		try {Thread.sleep(10);} catch (InterruptedException e) {}
		System.err.println("Congratulation ~~~ !");
		*/
/*		//寻找最难拼图，结果为30步之内必然可以完成3*3拼图
		int max = 1;
		Sheet[] r = null;
		while (true){
			while((r = StateNode.getResult(sheet)).length < max){
				sheet = new Sheet();
			}
			sheet.print();
			max = r.length;
			System.out.println("步数 ："+max);
			sheet = new Sheet();
		}*/
	}
	

	
}

/**
 * 正常拼图时使用的拼图类，自动求解时没有使用该类。
 * @author ICURSB
 *
 */
class Sheet{
	private int row = 3;
	private int col = 3;
	private int[][] a = new int[row][col];//拼图表
	private int x = 0;//9(显示为空格)的x坐标
	private int y = 0;//9(显示为空格)的y坐标
	private int step = 0;//总步数
	
	public Sheet() {
		Random ran = new Random();
		int[] src = {1,2,3,4,5,6,7,8,9};
		for (int i = 8; i > 0; i--) {
			int k = ran.nextInt(i);
			int tmp = src[i];
			src[i] = src[k];
			src[k] = tmp;
		}//随机交换了8次，逆序数为偶数
		for (int i = 0; i < src.length; i++) {
			a[i/3][i%3] = src[i];
		}
		find(9);//找到9的位置，记录到x、y中，供移动用
		if((row-x+col-y)%2 != 0){//若9到右下角的距离为奇数，则需要将其跟一个同样3
			//到右下角的距离为奇数的格子交换，这样表格的逆序数跟9到终点的距离就同奇偶性了。都是奇数
			//这里面涉及到数字拼图是否可解的问题，参考https://air20.com/archives/323.html
			int m = 0,n = 0;
			while(((row-m+col-n)%2 == 0)||(m==x&&n==y)){
				m = ran.nextInt(row);
				n = ran.nextInt(col);
			}
			
			a[x][y] = a[m][n];
			a[m][n] = 9;//交换完毕
			x=m;
			y=n;
		}
	}
	public Sheet(int[][] a) {
		this.a = a;
		find(9);
	}
	
	public int getY() {
		return y;
	}

	public int getX() {
		return x;
	}


	/**
	 * 打印数字拼图，遇到9打印空格
	 */
	public void print(){
		System.out.println("----------");
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[i].length; j++) {
				if (a[i][j]==9){
					System.out.print("  ");
				}else System.out.print(a[i][j] + " ");
			}
			System.out.println("");
		}
		System.out.println("---第"+step+"步---");
	}
	public void print(int k){
		System.out.println("----------");
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[i].length; j++) {
				if (a[i][j]==9){
					System.out.print("  ");
				}else System.out.print(a[i][j] + " ");
			}
			System.out.println("");
		}
		System.out.println("---第"+k+"步---");
	}
	public void up(){//上，碰壁不动
		if(x==2) return;
		a[x][y] = a[x+1][y];
		a[++x][y] = 9;
		step++;
	}
	public void down(){//下，碰壁不动
		if(x==0) return;
		a[x][y] = a[x-1][y];
		a[--x][y] = 9;
		step++;
	}
	public void left(){//左，碰壁不动
		if(y==2) return;
		a[x][y] = a[x][y+1];
		a[x][++y] = 9;
		step++;
	}
	public void right(){//右，碰壁不动
		if(y==0) return;
		a[x][y] = a[x][y-1];
		a[x][--y] = 9;
		step++;
	}
	/**
	 * 检查游戏是否结束
	 * @return true 游戏结束<p>false 继续
	 */
	public boolean check(){
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[i].length; j++) {
				if (!(j == 2 && i == 2)){
					if(a[i][j] != i*3+j+1) return false;
					System.out.print(a[i][j] + "\t");
				}
			}
		}
		return true;
	}
	/**
	 * 查询某数字的坐标
	 * @param n
	 * @Return 类成员变量x,y为其坐标
	 */
	private void find(int n){
		for(int i=0;i<a.length;i++){
			for (int j = 0; j < a[i].length; j++) {
				if(a[i][j] == n) {
					x = i;y = j;
				}
			}
		}
	}
	
	public int[][] getA(){
		return a;
	}

	
	/**
	 * 求逆序数，逆序数与9到终点的最少步数必须同奇同偶，否则拼图不可解
	 * @return 逆序数
	 */
	/*private int numOfWrong(){
		int r = 0;
		int[] ax = new int[9];
		for (int i = 0; i < a.length; i++) {
			ax[i*3] = a[i][0];
			ax[i*3+1] = a[i][1];
			ax[i*3+2] = a[i][2];
		}//排成一行完成，下面进行逆序数计算
		for (int i = 0; i < ax.length; i++) {
			int tmp = ax[i];
			for (int j = i + 1; j < ax.length; j++) {
				if (tmp > ax[j]) r++;
			}
		}
		
		return r;
	}*/
	

	/**
	 * 原移动函数，每次都要find(9)，可靠性高
	 * 但是鉴于封装严密，无需每次都find9
	 */
	/*public void right(){//右，碰壁不动
		find(9);
		if(y==0) return;
		a[x][y] = a[x][y-1];
		a[x][y-1] = 9;
		step++;
	}*/
	/**
	 * 初始化的一种算法，排成表格，随机一个数r，从第1个不为9
	 * 的数开始找，找到第r个不为9的数，放进sheet中，并将原位置
	 * 改为9。
	 */
	/*private boolean sheetInit(){
		Random ran = new Random();
		int[] src = {1,2,3,4,5,6,7,8};
		for(int i = 0; i < 8;i++){//填充9个数字
			int r = ran.nextInt(8);//数到第几个就填充啥
			int k = 0;
			for(int j=0;j<=r;){
				while(src[k]==9) {
					k++;
					if(k>7) k = 0;
				}
				
				j++;k++;
				if(k>7) k = 0;
			}
			a[i/3][i%3] = src[k==0?7:k-1];
			src[k==0?7:k-1]=9;
		}

		find(0);//全部处理完，把0替换成9
		a[x][y]=9;
		return numOfWrong()%2 == 0;
	}*/
}
/**
 * 用于表示状态和求解拼图
 * @author ICURSB
 *
 */
class StateNode{
	private int[][] sheet = new int[3][3];
	//private int gen;//generation，第几代
	private StateNode parent;//父节点，方便找到解之后反向遍历
	private int x,y;
	private int hash;
				//不提供子节点，不需要自上而下的遍历。
	public static final StateNode SUCCESS = new StateNode(new int[][]{
		{1,2,3},{4,5,6},{7,8,9}
	}, null,2,2);
	private static StateNode endNode;

	public StateNode(int[][] sheet, StateNode parent,int x,int y) {
		super();
		this.sheet = sheet;
		this.parent = parent;
		this.x = x;
		this.y = y;
		calcHash();
	}
	public int[][] getSheet() {
		return sheet;
	}
	public StateNode getParent() {
		return parent;
	}
	public void setParent(StateNode parent) {
		this.parent = parent;
	}
	/**
	 * 获取改节点的所有可能的子节点
	 * @return StateNode[]
	 */
	public StateNode[] getChildNodes(){

		//0-3分别为上右下左
		StateNode[] sn = {null,null,null,null};
		int[][] tmp = null;
		int nodeCount = 0;
		//上处理
		if(y != 0 && y-1 != parent.y){
			tmp = cloneSheet();
			tmp[x][y]=tmp[x][y-1];
			tmp[x][y-1] = 9;
			sn[0] = new StateNode(tmp, this,x,y-1);
			nodeCount++;
		}
		//右处理
		if(x != 2 && x+1 != parent.x){
			tmp = cloneSheet();
			tmp[x][y]=tmp[x+1][y];
			tmp[x+1][y] = 9;
			sn[1] = new StateNode(tmp, this,x+1,y);
			nodeCount++;
		}
		//下处理
		if(y != 2 && y+1 != parent.y){
			tmp = cloneSheet();
			tmp[x][y]=tmp[x][y+1];
			tmp[x][y+1] = 9;
			sn[2] = new StateNode(tmp, this,x,y+1);
			nodeCount++;
		}
		//左处理
		if(x != 0 && x-1 != parent.x){
			tmp = cloneSheet();
			tmp[x][y]=tmp[x-1][y];
			tmp[x-1][y] = 9;
			sn[3] = new StateNode(tmp, this,x-1,y);
			nodeCount++;
		}
		
		StateNode[] nodes = new StateNode[nodeCount];
		for (int i = 0,j=0; i < nodes.length; i++) {
			while(sn[j] == null) j++;
			nodes[i] = sn[j++];
		}
		return nodes;
	}
	/**
	 * 复制数组表
	 * @return 返回一个新的int二维数组，内容完全一致
	 */
	private int[][] cloneSheet(){
		int[][] c = new int[3][3];
		for (int i = 0; i < c.length; i++) {
			for (int j = 0; j < c[i].length; j++) {
				c[i][j] = sheet[i][j];
			}
		}

		return c;
	}
	/**
	 * 返回当前节点对应的Sheet
	 * @return Sheet
	 */
	public Sheet nodeToSheet(){
		return new Sheet(sheet);
	}
	
	/**
	 * 这里因为必然要用到hashCode，构造函数中就求了，直接return就可以了
	 * 
	 */
	@Override
	public int hashCode() {
		return hash;
	}
	private void calcHash(){//hash为int，乘法得hash
		hash = sheet[0][0]*100000000 + sheet[0][1]*10000000 + sheet[0][2]*1000000 + sheet[1][0]*100000 + sheet[1][1]*10000 
		+ sheet[2][0]*1000 + sheet[2][1]*100 + sheet[2][2]*10 + sheet[1][2]; 
		//先测试移位，再测试乘法，乘法能得到int的hash
	}
	/**
	 * 此方法与calcHash的效率基本一致，不如用后者，为int型
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private void calcHash0(){//hash为long，移位得hash
		//一个0-9的数只占4个位，通过移位的方式得到其hash值
		hash = sheet[0][0]<<16 | sheet[0][1]<<12 | sheet[0][2]<<8 | sheet[1][0]<<4 | sheet[1][1]; 
		hash = hash<<20 | sheet[2][0]<<12 | sheet[2][1]<<8 | sheet[2][2]<<4 | sheet[1][2]<<0; 
	}
	/**
	 * 哈希相等必然相等
	 * @param obj
	 * @return
	 */
	public boolean equals(StateNode obj) {
		if(hash == obj.hash) return true;
		return false;
	}
	public boolean seccess() {
		if(hash == SUCCESS.hash) return true;
		return false;
	}
	/**
	 * 打印数组表
	 */
	public void print(){
		System.out.println("----------");
		for (int i = 0; i < sheet.length; i++) {
			for (int j = 0; j < sheet[i].length; j++) {
				if (sheet[i][j]==9){
					System.out.print("  ");
				}else System.out.print(sheet[i][j] + " ");
			}
			System.out.println("");
		}
	}
	
	/**
	 * 宽度优先求解拼图，得到最优走法
	 * @param sheet
	 * @return 返回结果数组
	 */
	public static Sheet[] getResult(Sheet sheet){
		//set存储树中的所有元素
		Set<Integer> set = new HashSet<Integer>();
		int gen = 0;//gen为当前遍历第几代
		//baseNode为根节点,pNode为根的虚拟的父节点，因为算法需要，建立一个虚拟父节点更能提高速度
		StateNode pNode = new StateNode(sheet.getA(),null,-1,-1);
		StateNode baseNode = new StateNode(sheet.getA(),pNode,sheet.getX(),sheet.getY());
		//如果进来的就是OK的，直接传出去。
		if(baseNode.seccess()){
			Sheet[] res = new Sheet[1];
			res[0] = sheet;
			return res;
		}
		/**
		 * 定义内部类，用于多线程计算，效率略有提升，不多。
		 * @author ICURSB
		 *
		 */
		class Thread1 extends Thread{
			Set<Integer> set;
			List<StateNode> thisFloor,nextFloor;
			int begin,end;
			public Thread1(Set<Integer> set, List<StateNode> thisFloor, List<StateNode> nextFloor, int begin,
					int end) {
				super();
				this.set = set;
				this.thisFloor = thisFloor;
				this.nextFloor = nextFloor;
				this.begin = begin;
				this.end = end;
			}
			@Override
			public void run() {
				floorClear(set, thisFloor, nextFloor, begin, end);
			}
		}
		
		//当前层和下一层
		List<StateNode> thisFloor = new ArrayList<StateNode>();
		List<StateNode> nextFloor = new ArrayList<StateNode>();
		//最终节点
		endNode = null;
		set.add(baseNode.hashCode());
		thisFloor.add(baseNode);
		
		whileLabel:
		while(gen < 40){//超过40代依旧无解就退出，说明确实无解。
			int size = thisFloor.size();
			
			//=============双线程计算==============
			Thread1 thread1 = new Thread1(set, thisFloor, nextFloor,0, size/2);
			thread1.start();
			
			floorClear(set, thisFloor, nextFloor, size/2, size);
			try {
				thread1.join();
				thread1 = null;
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//==================================
			if (endNode != null) break whileLabel;
			//当前层处理完，处理下一层
			thisFloor = nextFloor;
			nextFloor = new ArrayList<StateNode>(size*3*3);
			gen++;
		}
		
		System.out.println("穷举拼图数量: " + set.size());
		
		if (gen >= 1000){
			return null;
		}
		//list存放解集，从解开始，找父节点，到初始节点为止
		ArrayList<Sheet> list = new ArrayList<Sheet>();
		StateNode thisNode = endNode;
		while(thisNode != null && !thisNode.equals(baseNode)){
			list.add(thisNode.nodeToSheet());
			thisNode = thisNode.getParent();
		}
		Sheet[] result = list.toArray(new Sheet[1]);
		return result;
	}
	/**
	 * 用于求当前层的一部分拼图的子代是否为结果，并存入下一层拼图。
	 * @param set 求过的都放set里
	 * @param thisFloor 当前层
	 * @param nextFloor 下一层，放子代
	 * @param begin 从第几个开始
	 * @param end 到第几个结束
	 */
	static void floorClear(Set<Integer> set,List<StateNode> thisFloor,List<StateNode> nextFloor,int begin,int end){
		int i;
		whileLabel:
		for (i = begin; i < end; i++) {
			StateNode node = null;
			node = thisFloor.get(i);
			StateNode[] childNodes = node.getChildNodes();
			//得到当前节点的子节点之后处理子节点
			for (int j = 0; j < childNodes.length; j++) {
				//childNodes[j].print();
				//加进set中，如果size增大就是新的节点，否则就是之前检查过的节点，不动。
				//由于set若equals，是不会更新该元素的，所以刚好不会影响求解的步数。
				boolean flag = false;
				synchronized(set){
					flag = set.add(childNodes[j].hashCode());
				}
				if (flag){
					if ( childNodes[j].seccess() ){
						endNode = childNodes[j];
						break whileLabel;//为最终节点则跳出求解循环
					}else{
						//synchronized (nextFloor) {
							nextFloor.add(childNodes[j]);
						//}
					}
				}
			}
			if (endNode != null) break whileLabel;
		}
	}
}


