/*
 * Copyright 2018 the original author or authors. 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package com.gitee.drinkjava2.frog;

import java.awt.Graphics;
import java.io.FileInputStream;

import javax.imageio.ImageIO;

import com.gitee.drinkjava2.frog.egg.Egg;

/**
 * Snake has similar brain like Frog, snake eat frog <br>
 * 蛇是青蛙的子类，区别是： 蛇只能看到青蛙，并将其当作一个单个象素点的食物， 青蛙能看到真的食物(Food)和蛇
 * 
 * @since 1.0
 */
public class Snake extends Animal {

	public Snake(int x, int y, Egg egg) {
		super(x, y, egg);
		try {
			animalImage = ImageIO.read(new FileInputStream(Application.CLASSPATH + "snake.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void show(Graphics g) {// 显示蛇的图象
		if (!alive)
			return;
		g.drawImage(animalImage, x - 16, y - 5, 18, 18, null);// 减去坐标，保证蛇嘴巴显示在当前x,y处
	}
}