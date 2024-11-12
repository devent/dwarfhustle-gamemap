import com.anrisoftware.dwarfhustle.gamemap.jme.assets.ModelMap

/*
 * Dwarf Hustle Game Map - Game map.
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

/*
 * -([swen]+)\.j3o
 * .j3o
 */

rid = [:]

// BlockObject
rid["BLOCK-RAMP-TWO-SE"] = 990
rid["BLOCK-RAMP-TWO-NE"] = 989
rid["BLOCK-RAMP-TRI-S"] = 988
rid["BLOCK-RAMP-TRI-W"] = 987
rid["BLOCK-RAMP-TRI-E"] = 986
rid["BLOCK-RAMP-TRI-N"] = 985
rid["BLOCK-RAMP-SINGLE"] = 984
rid["BLOCK-RAMP-PERP-W"] = 983
rid["BLOCK-RAMP-PERP-S"] = 982
rid["BLOCK-RAMP-PERP-E"] = 981
rid["BLOCK-RAMP-PERP-N"] = 980
rid["BLOCK-RAMP-EDGE-OUT-SW"] = 979
rid["BLOCK-RAMP-EDGE-OUT-SE"] = 978
rid["BLOCK-RAMP-EDGE-OUT-NW"] = 977
rid["BLOCK-RAMP-EDGE-OUT-NE"] = 976
rid["BLOCK-RAMP-EDGE-IN-SW"] = 975
rid["BLOCK-RAMP-EDGE-IN-SE"] = 974
rid["BLOCK-RAMP-EDGE-IN-NW"] = 973
rid["BLOCK-RAMP-EDGE-IN-NE"] = 972
rid["BLOCK-RAMP-CORNER-SW"] = 971
rid["BLOCK-RAMP-CORNER-SE"] = 970
rid["BLOCK-RAMP-CORNER-NW"] = 969
rid["BLOCK-RAMP-CORNER-NE"] = 968
rid["BLOCK-CEILING"] = 967
rid["BLOCK-WATER"] = 966
rid["BLOCK-NORMAL"] = 965

// Grass
rid["RED-POPPY"] = 1021
rid["DAISY"] = 1020
rid["CARROT"] = 1019
rid["WHEAT"] = 1017

// Shrub
rid["BLUEBERRIES"] = 1023

// Tree-Branch
rid["BIRCH-BRANCH"] = 1037
rid["PINE-BRANCH"] = 1029

// Tree-Leaf
rid["BIRCH-LEAF"] = 1039
rid["PINE-LEAF"] = 1031

// Tree-Root
rid["BIRCH-ROOT"] = 1035
rid["PINE-ROOT"] = 1027

// Tree-Sampling
rid["BIRCH-SAMPLING"] = 1034
rid["PINE-SAMPLING"] = 1026

// Tree-Trunk
rid["BIRCH-TRUNK"] = 1036
rid["PINE-TRUNK"] = 1028

// Tree-Twig
rid["BIRCH-TWIG"] = 1038
rid["PINE-TWIG"] = 1030

m = new ModelMap()

m[rid["BLOCK-RAMP-TWO-SE"]] = [model: "Models/blocks/block-ramp-two/block-ramp-two.j3o", rotationDeg: [0, 0, 180]]
m[rid["BLOCK-RAMP-TWO-NE"]] = [model: "Models/blocks/block-ramp-two/block-ramp-two.j3o", rotationDeg: [0, 0, 90]]
m[rid["BLOCK-RAMP-TRI-S"]] = [model: "Models/blocks/block-ramp-tri/block-ramp-tri-flat.j3o", rotationDeg: [0, 0, 270]]
m[rid["BLOCK-RAMP-TRI-W"]] = [model: "Models/blocks/block-ramp-tri/block-ramp-tri-flat.j3o", rotationDeg: [0, 0, 180]]
m[rid["BLOCK-RAMP-TRI-E"]] = [model: "Models/blocks/block-ramp-tri/block-ramp-tri-flat.j3o", rotationDeg: [0, 0, 0]]
m[rid["BLOCK-RAMP-TRI-N"]] = [model: "Models/blocks/block-ramp-tri/block-ramp-tri-flat.j3o", rotationDeg: [0, 0, 90]]
m[rid["BLOCK-RAMP-SINGLE"]] = [model: "Models/blocks/block-ramp-single/blocks-ramp-single-flat.j3o"]
m[rid["BLOCK-RAMP-PERP-W"]] = [model: "Models/blocks/block-ramp-perp/block-ramp-perp.j3o", rotationDeg: [0, 0, 180]]
m[rid["BLOCK-RAMP-PERP-S"]] = [model: "Models/blocks/block-ramp-perp/block-ramp-perp.j3o", rotationDeg: [0, 0, 270]]
m[rid["BLOCK-RAMP-PERP-E"]] = [model: "Models/blocks/block-ramp-perp/block-ramp-perp.j3o"]
m[rid["BLOCK-RAMP-PERP-N"]] = [model: "Models/blocks/block-ramp-perp/block-ramp-perp.j3o", rotationDeg: [0, 0, 90]]
m[rid["BLOCK-RAMP-EDGE-OUT-SW"]] = [model: "Models/blocks/block-ramp-edge-out/block-ramp-edge-out.j3o", rotationDeg: [0, 0, 180]]
m[rid["BLOCK-RAMP-EDGE-OUT-SE"]] = [model: "Models/blocks/block-ramp-edge-out/block-ramp-edge-out.j3o", rotationDeg: [0, 0, 270]]
m[rid["BLOCK-RAMP-EDGE-OUT-NW"]] = [model: "Models/blocks/block-ramp-edge-out/block-ramp-edge-out.j3o", rotationDeg: [0, 0, 90]]
m[rid["BLOCK-RAMP-EDGE-OUT-NE"]] = [model: "Models/blocks/block-ramp-edge-out/block-ramp-edge-out.j3o"]
m[rid["BLOCK-RAMP-EDGE-IN-SW"]] = [model: "Models/blocks/block-ramp-edge-in/block-ramp-edge-in.j3o", rotationDeg: [0, 0, 180]]
m[rid["BLOCK-RAMP-EDGE-IN-SE"]] = [model: "Models/blocks/block-ramp-edge-in/block-ramp-edge-in.j3o", rotationDeg: [0, 0, 270]]
m[rid["BLOCK-RAMP-EDGE-IN-NW"]] = [model: "Models/blocks/block-ramp-edge-in/block-ramp-edge-in.j3o", rotationDeg: [0, 0, 90]]
m[rid["BLOCK-RAMP-EDGE-IN-NE"]] = [model: "Models/blocks/block-ramp-edge-in/block-ramp-edge-in.j3o"]
m[rid["BLOCK-RAMP-CORNER-SW"]] = [model: "Models/blocks/block-ramp-corner/block-ramp-corner-flat.j3o", rotationDeg: [0, 0, 0]]
m[rid["BLOCK-RAMP-CORNER-SE"]] = [model: "Models/blocks/block-ramp-corner/block-ramp-corner-flat.j3o", rotationDeg: [0, 0, 90]]
m[rid["BLOCK-RAMP-CORNER-NW"]] = [model: "Models/blocks/block-ramp-corner/block-ramp-corner-flat.j3o", rotationDeg: [0, 0, 270]]
m[rid["BLOCK-RAMP-CORNER-NE"]] = [model: "Models/blocks/block-ramp-corner/block-ramp-corner-flat.j3o", rotationDeg: [0, 0, 180]]
m[rid["BLOCK-CEILING"]] = [model: "Models/blocks/block-ceiling/block-ceiling.j3o"]
m[rid["BLOCK-WATER"]] = [model: "Models/blocks/block-water/block-water.j3o"]
m[rid["BLOCK-NORMAL"]] = [model: "Models/blocks/block-normal/block-normal.j3o"]

m[rid["PINE-TRUNK"]] = [model: "Models/trees/tree-trunk/tree-trunk.j3o"]
m[rid["PINE-SAMPLING"]] = [model: "Models/trees/tree-sampling-pine/tree-sampling-pine-4.j3o"]

m[rid["BLUEBERRIES"]] = [model: "Models/shrubs/blueberry/blueberry-shrub-3-leafs.j3o"]

m
