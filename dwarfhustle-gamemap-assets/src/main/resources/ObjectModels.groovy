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
rid["BLOCK-RAMP-TWO-SE"] = 991
rid["BLOCK-RAMP-TWO-NE"] = 990
rid["BLOCK-RAMP-TRI-S"] = 989
rid["BLOCK-RAMP-TRI-W"] = 988
rid["BLOCK-RAMP-TRI-E"] = 987
rid["BLOCK-RAMP-TRI-N"] = 986
rid["BLOCK-RAMP-SINGLE"] = 985
rid["BLOCK-RAMP-PERP-W"] = 984
rid["BLOCK-RAMP-PERP-S"] = 983
rid["BLOCK-RAMP-PERP-E"] = 982
rid["BLOCK-RAMP-PERP-N"] = 981
rid["BLOCK-RAMP-EDGE-OUT-SW"] = 980
rid["BLOCK-RAMP-EDGE-OUT-SE"] = 979
rid["BLOCK-RAMP-EDGE-OUT-NW"] = 978
rid["BLOCK-RAMP-EDGE-OUT-NE"] = 977
rid["BLOCK-RAMP-EDGE-IN-SW"] = 976
rid["BLOCK-RAMP-EDGE-IN-SE"] = 975
rid["BLOCK-RAMP-EDGE-IN-NW"] = 974
rid["BLOCK-RAMP-EDGE-IN-NE"] = 973
rid["BLOCK-RAMP-CORNER-SW"] = 972
rid["BLOCK-RAMP-CORNER-SE"] = 971
rid["BLOCK-RAMP-CORNER-NW"] = 970
rid["BLOCK-RAMP-CORNER-NE"] = 969
rid["BLOCK-CEILING"] = 968
rid["BLOCK-WATER"] = 967
rid["BLOCK-NORMAL"] = 966

// Grass
rid["RED-POPPY"] = 1023
rid["DAISY"] = 1022
rid["CARROT"] = 1021
rid["WHEAT"] = 1019

// Shrub
rid["BLUEBERRIES"] = 1025

// Tree-Branch
rid["BIRCH-BRANCH"] = 1039
rid["PINE-BRANCH"] = 1031

// Tree-Leaf
rid["BIRCH-LEAF"] = 1041
rid["PINE-LEAF"] = 1033

// Tree-Root
rid["BIRCH-ROOT"] = 1037
rid["PINE-ROOT"] = 1029

// Tree-Sapling
rid["BIRCH-SAPLING"] = 1036
rid["PINE-SAPLING"] = 1028

// Tree-Trunk
rid["BIRCH-TRUNK"] = 1038
rid["PINE-TRUNK"] = 1030

// Tree-Twig
rid["BIRCH-TWIG"] = 1040
rid["PINE-TWIG"] = 1032

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

m[rid["PINE-BRANCH"]] = [model: "Models/trees/tree-branch/tree-branch.j3o"]
m[rid["PINE-LEAF"]] = [model: "Models/trees/tree-trunk/tree-trunk.j3o"]
m[rid["PINE-ROOT"]] = [model: "Models/trees/tree-trunk/tree-trunk.j3o"]
m[rid["PINE-SAPLING"]] = [model: "Models/trees/tree-sampling-pine/tree-sampling-pine-6.j3o"]
m[rid["PINE-TRUNK"]] = [model: "Models/trees/tree-trunk/tree-trunk.j3o"]
m[rid["PINE-TWIG"]] = [model: "Models/trees/tree-trunk/tree-trunk.j3o"]

m[rid["BLUEBERRIES"]] = [model: "Models/shrubs/blueberry/blueberry-shrub-3-leafs.j3o"]

m
