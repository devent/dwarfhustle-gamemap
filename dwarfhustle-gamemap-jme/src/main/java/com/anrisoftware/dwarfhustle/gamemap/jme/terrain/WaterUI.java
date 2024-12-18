/*
 * dwarfhustle-gamemap-jme - Game map.
 * Copyright © 2023 Erwin Müller (erwin.mueller@anrisoftware.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.anrisoftware.dwarfhustle.gamemap.jme.terrain;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.water.SimpleWaterProcessor;

/**
 *
 * @author nehon
 */
public class WaterUI {
    private final SimpleWaterProcessor processor;

    public WaterUI(InputManager inputManager, SimpleWaterProcessor proc) {
        processor = proc;

        System.out.println("----------------- Water UI Debugger --------------------");
        System.out.println("-- Water transparency : press Y to increase, H to decrease");
        System.out.println("-- Water depth : press U to increase, J to decrease");
//        System.out.println("-- AO scale : press I to increase, K to decrease");
//        System.out.println("-- AO bias : press O to increase, P to decrease");
//        System.out.println("-- Toggle AO on/off : press space bar");
//        System.out.println("-- Use only AO : press Num pad 0");
//        System.out.println("-- Output config declaration : press P");
        System.out.println("-------------------------------------------------------");

        inputManager.addMapping("transparencyUp", new KeyTrigger(KeyInput.KEY_Y));
        inputManager.addMapping("transparencyDown", new KeyTrigger(KeyInput.KEY_H));
        inputManager.addMapping("depthUp", new KeyTrigger(KeyInput.KEY_U));
        inputManager.addMapping("depthDown", new KeyTrigger(KeyInput.KEY_J));
//        inputManager.addMapping("scaleUp", new KeyTrigger(KeyInput.KEY_I));
//        inputManager.addMapping("scaleDown", new KeyTrigger(KeyInput.KEY_K));
//        inputManager.addMapping("biasUp", new KeyTrigger(KeyInput.KEY_O));
//        inputManager.addMapping("biasDown", new KeyTrigger(KeyInput.KEY_L));
//        inputManager.addMapping("outputConfig", new KeyTrigger(KeyInput.KEY_P));
//        inputManager.addMapping("toggleUseAO", new KeyTrigger(KeyInput.KEY_SPACE));
//        inputManager.addMapping("toggleUseOnlyAo", new KeyTrigger(KeyInput.KEY_NUMPAD0));

//        ActionListener acl = new ActionListener() {
//
//            public void onAction(String name, boolean keyPressed, float tpf) {
//
//                if (name.equals("toggleUseAO") && keyPressed) {
//                    ssaoConfig.setUseAo(!ssaoConfig.isUseAo());
//                    System.out.println("use AO : "+ssaoConfig.isUseAo());
//                }
//                if (name.equals("toggleUseOnlyAo") && keyPressed) {
//                    ssaoConfig.setUseOnlyAo(!ssaoConfig.isUseOnlyAo());
//                    System.out.println("use Only AO : "+ssaoConfig.isUseOnlyAo());
//
//                }
//                if (name.equals("outputConfig") && keyPressed) {
//                    System.out.println("new SSAOConfig("+ssaoConfig.getSampleRadius()+"f,"+ssaoConfig.getIntensity()+"f,"+ssaoConfig.getScale()+"f,"+ssaoConfig.getBias()+"f,"+ssaoConfig.isUseOnlyAo()+","+ssaoConfig.isUseAo()+");");
//                }
//
//            }
//        };

        AnalogListener anl = (name, value, tpf) -> {
            if (name.equals("transparencyUp")) {
                processor.setWaterTransparency(processor.getWaterTransparency() + 0.001f);
                System.out.println("Water transparency : " + processor.getWaterTransparency());
            }
            if (name.equals("transparencyDown")) {
                processor.setWaterTransparency(processor.getWaterTransparency() - 0.001f);
                System.out.println("Water transparency : " + processor.getWaterTransparency());
            }
            if (name.equals("depthUp")) {
                processor.setWaterDepth(processor.getWaterDepth() + 0.001f);
                System.out.println("Water depth : " + processor.getWaterDepth());
            }
            if (name.equals("depthDown")) {
                processor.setWaterDepth(processor.getWaterDepth() - 0.001f);
                System.out.println("Water depth : " + processor.getWaterDepth());
            }

        };
        // inputManager.addListener(acl,"toggleUseAO","toggleUseOnlyAo","outputConfig");
        inputManager.addListener(anl, "transparencyUp", "transparencyDown", "depthUp", "depthDown");

    }

}