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
package com.github.drinkjava2.frog.brain.organ;

import com.github.drinkjava2.frog.Application;
import com.github.drinkjava2.frog.Env;
import com.github.drinkjava2.frog.Frog;
import com.github.drinkjava2.frog.brain.BrainPicture;
import com.github.drinkjava2.frog.brain.Cell;
import com.github.drinkjava2.frog.brain.Organ;
import com.github.drinkjava2.frog.brain.Zone;
import com.github.drinkjava2.frog.util.RandomUtils;

/**
 * Eye can only see 4 direction
 * 
 * @Deprecated use Eyes classes instead.
 * 
 * @author Yong Zhu
 * @since 1.0
 */
public class Eye extends Organ {// 这个Eye是老版的眼睛，只能看到四个方向，但它的调节距离会自动随机调整到一个最佳值，这就是随机试错算法的一个应用
	private static final long serialVersionUID = 1L;
	public int seeDistance; // 眼睛能看到的距离

	@Override
	public void initFrog(Frog f) { // 仅在Frog生成时这个方法会调用一次
		if (!initilized) {
			initilized = true;
			organOutputEnergy = 30;
			seeDistance = 8;
		}
	}

	@Override
	public void drawOnBrainPicture(Frog f, BrainPicture pic) {// 把自已这个器官在脑图上显示出来
		if (!Application.SHOW_FIRST_FROG_BRAIN)
			return;
		super.drawOnBrainPicture(f, pic);
		float qRadius = r / 4;
		float q3Radius = (float) (r * .75);
		Zone seeUp = new Zone(x, y + q3Radius, qRadius);
		Zone seeDown = new Zone(x, y - q3Radius, qRadius);
		Zone seeLeft = new Zone(x - q3Radius, y, qRadius);
		Zone seeRight = new Zone(x + q3Radius, y, qRadius);
		pic.drawZone(seeUp);
		pic.drawZone(seeDown);
		pic.drawZone(seeLeft);
		pic.drawZone(seeRight);
	}

	@Override
	public Organ[] vary() {
		if (RandomUtils.percent(5)) { // 可视距离有5%的机率变异
			seeDistance = seeDistance + 1 - 2 * RandomUtils.nextInt(2);
			if (seeDistance < 1)
				seeDistance = 1;
			if (seeDistance > 50)
				seeDistance = 50;
		}
		return new Organ[] { this };
	}

	@Override
	public void active(Frog f) {
		// 第一个眼睛只能观察上、下、左、右四个方向有没有食物
		float qRadius = r / 4;
		float q3Radius = (float) (r * .75);
		Zone seeUp = new Zone(x, y + q3Radius, qRadius);
		Zone seeDown = new Zone(x, y - q3Radius, qRadius);
		Zone seeLeft = new Zone(x - q3Radius, y, qRadius);
		Zone seeRight = new Zone(x + q3Radius, y, qRadius);

		boolean seeSomething = false;
		boolean atUp = false;
		boolean atDown = false;
		boolean atLeft = false;
		boolean atRight = false;

		for (int i = 1; i < seeDistance; i++)
			if (Env.foundAnyThing(f.x, f.y + i)) {
				seeSomething = true;
				atUp = true;
				break;
			}

		for (int i = 1; i < seeDistance; i++)
			if (Env.foundAnyThing(f.x, f.y - i)) {
				seeSomething = true;
				atDown = true;
				break;
			}

		for (int i = 1; i < seeDistance; i++)
			if (Env.foundAnyThing(f.x - i, f.y)) {
				seeSomething = true;
				atLeft = true;
				break;
			}

		for (int i = 1; i < seeDistance; i++)
			if (Env.foundAnyThing(f.x + i, f.y)) {
				seeSomething = true;
				atRight = true;
				break;
			}

		if (seeSomething)
			for (Cell cell : f.cells) {
				if (cell.energy < 100)
					if (cell.input.nearby(this)) {
						if (atUp && cell.input.nearby(seeUp)) {
							cell.energy += organOutputEnergy;
						}
						if (atDown && cell.input.nearby(seeDown)) {
							cell.energy += organOutputEnergy;
						}
						if (atLeft && cell.input.nearby(seeLeft)) {
							cell.energy += organOutputEnergy;
						}
						if (atRight && cell.input.nearby(seeRight)) {
							cell.energy += organOutputEnergy;
						}
					}
			}

	}

}
