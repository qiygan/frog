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
package com.gitee.drinkjava2.frog.organ.frog;

import com.gitee.drinkjava2.frog.Animal;
import com.gitee.drinkjava2.frog.brain.Organ;

/**
 * Frog jump if FrogJump organ activate, to escape from earthquake， after a
 * short time, high restore to 0
 * 
 * 这个器官激活，青蛙会跳在空中，躲开地震波的伤害
 * 
 */
public class FrogJump extends Organ {// FrogJump这个器官的作用就是让青蛙跳在空中
	private static final long serialVersionUID = 1L;

	@Override
	public void active(Animal a) {
		if (outputActive(a)) {
			a.high = 100; // 跳起来了的青蛙用画小红点表示，见Frog.show()方法
		}

		a.high--;
		if(a.high>0)
			 a.energy-=1000;
		if (a.high < 0) // 跳起来后，隔一段时间会落在地上，high回原到0
			a.high = 0;

	}

}
