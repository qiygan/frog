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

import com.github.drinkjava2.frog.Env;
import com.github.drinkjava2.frog.Frog;
import com.github.drinkjava2.frog.brain.Organ;

/**
 * Eat food at current x, y position
 */
public class Eat extends Organ {// Eat这个类将食物转化为能量，能量小于0，则青蛙死掉
	private static final long serialVersionUID = 1L;

	@Override
	public void active(Frog f) {
		if (Env.foundAndAteFood(f.x, f.y)) {
			f.ateFood++; 
			f.energy += 1000;// 如果青蛙的坐标与食物重合，吃掉food，能量境加 
		}
	}

}
