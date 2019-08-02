package com.github.drinkjava2.frog;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JPanel;

import com.github.drinkjava2.frog.brain.group.RandomConnectGroup;
import com.github.drinkjava2.frog.egg.Egg;
import com.github.drinkjava2.frog.egg.EggTool;
import com.github.drinkjava2.frog.util.RandomUtils;

/**
 * Env is the living space of frog. draw it on JPanel
 * 
 * @author Yong Zhu
 * @since 1.0
 */
@SuppressWarnings("serial")
public class Env extends JPanel {
	/** Speed of test */
	public static final int SHOW_SPEED = 800; // 测试速度，-1000~1000,可调, 数值越小，速度越慢

	/** Delete eggs at beginning of each run */
	public static final boolean DELETE_EGGS = true;// 每次运行是否先删除保存的蛋

	public static final int EGG_QTY = 1; // 每轮下n个蛋，可调，只有最优秀的前n个青蛙们才允许下蛋

	public static final int FROG_PER_EGG = 1; // 每个蛋可以孵出几个青蛙

	public static final int GROUP_SIZE = 12;// 分组测试，每个组里面有多少轮, 利用分组可以在慢的电脑上每次只跑少量的样本，用时间换空间

	public static final int PICK_PER_GROUP = 4;// 一组被测试完后，只有总找食最多的若干组被选中参与下一组测试

	/** Debug mode will print more debug info */
	public static final boolean DEBUG_MODE = false; // Debug 模式下会打印出更多的调试信息

	/** Draw first frog's brain after some steps */
	public static final int DRAW_BRAIN_AFTER_STEPS = 50; // 以此值为间隔动态画出脑图，设为0则关闭这个动态脑图功能，只显示一个静态、不闪烁的脑图

	/** Environment x width, unit: pixels */
	public static final int ENV_WIDTH = 400; // 虚拟环境的宽度, 可调

	/** Environment y height, unit: pixels */
	public static final int ENV_HEIGHT = ENV_WIDTH; // 虚拟环境高度, 可调，通常取正方形

	/** Frog's brain display width on screen, not important */
	public static final int FROG_BRAIN_DISP_WIDTH = 400; // Frog的脑图在屏幕上的显示大小,可调

	/** Steps of one test round */
	public static final int STEPS_PER_ROUND = 2000;// 每轮测试步数,可调

	/** Frog's brain width, fixed to 1000 unit */
	public static final float FROG_BRAIN_WIDTH = 1000; // frog的脑宽度固定为1000,不要随便调整,因为器官的相对位置和大小是按脑大小设定的

	public static final int FOOD_QTY = 1500; // 食物数量, 可调

	private static final Random r = new Random(); // 随机数发生器

	public static boolean pause = false; // 暂停按钮按下将暂停测试

	private static final boolean[][] foods = new boolean[ENV_WIDTH][ENV_HEIGHT];// 食物数组定义

	private static final int TRAP_WIDTH = 350; // 陷阱高, 0~200

	private static final int TRAP_HEIGHT = 10; // 陷阱宽, 0~200

	public static List<Frog> frogs = new ArrayList<>();

	public static Map<Float, List<Egg>> eggsMap;

	static {
		System.out.println("唵缚悉波罗摩尼莎诃!"); // 往生咒
		if (DELETE_EGGS)
			EggTool.deleteEggs();
	}

	public Env() {
		super();
		this.setLayout(null);// 空布局
		this.setBounds(1, 1, ENV_WIDTH, ENV_HEIGHT);
	}

	public static boolean insideEnv(int x, int y) {// 如果指定点在边界内
		return !(x < 0 || y < 0 || x >= ENV_WIDTH || y >= ENV_HEIGHT);
	}

	public static boolean outsideEnv(int x, int y) {// 如果指定点超出边界
		return x < 0 || y < 0 || x >= ENV_WIDTH || y >= ENV_HEIGHT;
	}

	public static boolean foundFood(int x, int y) {// 如果指定点看到食物
		return !(x < 0 || y < 0 || x >= ENV_WIDTH || y >= ENV_HEIGHT) && Env.foods[x][y];
	}

	public static boolean closeToEdge(Frog f) {// 青蛙靠近边界? 离死不远了
		return f.x < 20 || f.y < 20 || f.x > (Env.ENV_WIDTH - 20) || f.y > (Env.ENV_HEIGHT - 20);
	}

	public static boolean inTrap(int x, int y) {// 如果指定点看到食物
		return x >= ENV_WIDTH / 2 - TRAP_WIDTH / 2 && x <= ENV_WIDTH / 2 + TRAP_WIDTH / 2
				&& y >= ENV_HEIGHT / 2 - TRAP_HEIGHT / 2 && y <= ENV_HEIGHT / 2 + TRAP_HEIGHT / 2;
	}

	public static boolean foundAnyThing(int x, int y) {// 如果指定点看到食物或超出边界
		return x < 0 || y < 0 || x >= ENV_WIDTH || y >= ENV_HEIGHT || Env.foods[x][y] || inTrap(x, y);
	}

	public static boolean foundAndDeleteFood(int x, int y) {// 如果x,y有食物，将其清0，返回true
		if (foundFood(x, y)) {
			foods[x][y] = false;
			return true;
		}
		return false;
	}

	private void rebuildFrogAndFood(List<Egg> eggs) {
		frogs.clear();
		for (int i = 0; i < ENV_WIDTH; i++) {// 清除食物
			for (int j = 0; j < ENV_HEIGHT; j++) {
				foods[i][j] = false;
			}
		}
		Random rand = new Random();
		for (int i = 0; i < eggs.size(); i++) {// 创建青蛙，每个蛋生成4个蛙，并随机取一个别的蛋作为精子
			int loop = FROG_PER_EGG;
			if (eggs.size() > 20) { // 如果数量多，进行一些优化，让排名靠前的Egg多孵出青蛙
				if (i < FROG_PER_EGG)// 0,1,2,3
					loop = FROG_PER_EGG + 1;
				if (i >= (eggs.size() - FROG_PER_EGG))
					loop = FROG_PER_EGG - 1;
			}
			for (int j = 0; j < loop; j++) {
				Egg zygote = new Egg(eggs.get(i), eggs.get(r.nextInt(eggs.size())));
				frogs.add(new Frog(rand.nextInt(ENV_WIDTH), rand.nextInt(ENV_HEIGHT), zygote));
			}
		}

		System.out.println("Created " + frogs.size() + " frogs");
		for (int i = 0; i < Env.FOOD_QTY; i++) // 生成食物
			foods[rand.nextInt(ENV_WIDTH)][rand.nextInt(ENV_HEIGHT)] = true;
	}

	private void drawFood(Graphics g) {
		for (int x = 0; x < ENV_WIDTH; x++)
			for (int y = 0; y < ENV_HEIGHT; y++)
				if (foods[x][y]) {
					g.fillOval(x, y, 4, 4);
				}
	}

	private void drawTrap(Graphics g) {// 所有走到陷阱边沿上的的青蛙都死掉
		g.fillRect(ENV_HEIGHT / 2 - TRAP_WIDTH / 2, ENV_HEIGHT / 2 - TRAP_HEIGHT / 2, TRAP_WIDTH, TRAP_HEIGHT);
		g.setColor(Color.white);
		g.fillRect(ENV_HEIGHT / 2 - TRAP_WIDTH / 2 + 3, ENV_HEIGHT / 2 - TRAP_HEIGHT / 2 + 3, TRAP_WIDTH - 6,
				TRAP_HEIGHT - 6);
		g.setColor(Color.black);
	}

	static final NumberFormat format100 = NumberFormat.getPercentInstance();
	static {
		format100.setMaximumFractionDigits(2);
	}

	private static int foodFoundAmount() {// 统计找食数等
		int leftfood = 0;
		for (int x = 0; x < ENV_WIDTH; x++)
			for (int y = 0; y < ENV_HEIGHT; y++)
				if (foods[x][y])
					leftfood++;
		return FOOD_QTY - leftfood;
	}

	private String foodFoundCountText() {// 统计找食数等
		int foodFound = foodFoundAmount();
		int maxFound = 0;
		for (Frog f : frogs)
			if (f.ateFood > maxFound)
				maxFound = f.ateFood;
		return new StringBuilder("找食率:").append(format100.format(foodFound * 1.00 / FOOD_QTY)).append(", 平均: ")
				.append(foodFound * 1.0f / (EGG_QTY * FROG_PER_EGG)).append("，最多:").append(maxFound).toString();
	}

	private static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	public void run() throws InterruptedException {
		EggTool.loadEggs(); // 从磁盘加载egg，或新建一批egg
		int round = 1;
		Image buffImg = createImage(this.getWidth(), this.getHeight());
		Graphics g = buffImg.getGraphics();
		long t1, t2;// 计时用
		do {
			Map<Float, List<Egg>> resultEggsMap = new HashMap<Float, List<Egg>>();
			for (List<Egg> eggs : eggsMap.values()) {
				if (pause)
					do {
						sleep(300);
					} while (pause);
				t1 = System.currentTimeMillis();
				rebuildFrogAndFood(eggs);
				boolean allDead = false;
				Frog firstFrog = frogs.get(0);
				for (int i = 0; i < STEPS_PER_ROUND; i++) {
					if (allDead) {
						System.out.println("All dead at round:" + i);
						break; // 青蛙全死光了就直接跳到下一轮,以节省时间
					}
					allDead = true;
					for (Frog frog : frogs)
						if (frog.active(this))
							allDead = false;

					for (Frog frog : frogs)
						if (frog.alive && RandomUtils.percent(0.2f)) {// 有很小的机率在青蛙活着时就创建新的器官
							RandomConnectGroup newConGrp = new RandomConnectGroup();
							newConGrp.initFrog(frog);
							frog.organs.add(newConGrp);
						}

					if (SHOW_SPEED > 0 && i % SHOW_SPEED != 0) // 画青蛙会拖慢速度
						continue;

					if (SHOW_SPEED < 0) // 如果speed小于0，人为加入延迟
						sleep(-SHOW_SPEED);

					// 开始画青蛙
					g.setColor(Color.white);
					g.fillRect(0, 0, this.getWidth(), this.getHeight());
					g.setColor(Color.BLACK);
					for (Frog frog : frogs)
						frog.show(g);

					if (firstFrog.alive) { // 开始显示第一个Frog的动态脑图
						if (Application.SHOW_FIRST_FROG_BRAIN) {
							g.setColor(Color.red);
							g.drawArc(firstFrog.x - 15, firstFrog.y - 15, 30, 30, 0, 360);
							g.setColor(Color.BLACK);
						}
						if (DRAW_BRAIN_AFTER_STEPS > 0 && i % DRAW_BRAIN_AFTER_STEPS == 0)
							Application.brainPic.drawBrainPicture(firstFrog);
					}

					drawTrap(g);
					drawFood(g);
					Graphics g2 = this.getGraphics();
					g2.drawImage(buffImg, 0, 0, this);

				}
				Application.brainPic.drawBrainPicture(firstFrog);
				EggTool.layEggs(foodFoundAmount(), resultEggsMap);
				t2 = System.currentTimeMillis();
				Application.mainFrame.setTitle(new StringBuilder("轮数: ").append(round++).append(", ")
						.append(foodFoundCountText()).append(", 用时: ").append(t2 - t1).append("ms").toString());
			}
			pickFromResultMap(resultEggsMap);
		} while (true);
	}

	private void pickFromResultMap(Map<Float, List<Egg>> resultEggsMap) {// 从测试组里PICK出找食最多的几组，其余的组被淘汰
		eggsMap.clear();
		Object[] key = resultEggsMap.keySet().toArray();
		Arrays.sort(key);
		for (Object object : key) {
			System.out.print(object+",");
		}
		System.out.println();
		for (int i = 0; i <PICK_PER_GROUP  ; i++) {
			System.out.println("i="+i);
			System.out.println(" key.length="+ key.length);
			System.out.println("PICK_PER_GROUP="+ PICK_PER_GROUP);
			System.out.println("GROUP_SIZE="+ GROUP_SIZE);
			System.out.println("GROUP_SIZE / PICK_PER_GROUP="+ (GROUP_SIZE / PICK_PER_GROUP));
			for (int j = 0; j < (GROUP_SIZE / PICK_PER_GROUP); j++) { // group 10 / pick 2 =5
				eggsMap.put(Float.parseFloat("0." + j), resultEggsMap.get(key[resultEggsMap.size()-1-i   ]));
			}
		}
	}

	public static void main(String[] args) {
		Map<Float, List<Egg>> r = new HashMap<>();
		r.put(3.1f, null);
		r.put(12.1f, null);
		r.put(0.1f, null);
		r.put(1.1f, null);
		r.put(10.1f, null);
		Object[] key = r.keySet().toArray();
		Arrays.sort(key);
		for (Object object : key) {
			System.out.println(object);
		}
	}
}