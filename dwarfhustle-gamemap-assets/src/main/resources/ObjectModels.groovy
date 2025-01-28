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
rid["BLOCK-RAMP-TWO-SE"] = 993
rid["BLOCK-RAMP-TWO-NE"] = 992
rid["BLOCK-RAMP-TRI-S"] = 991
rid["BLOCK-RAMP-TRI-W"] = 990
rid["BLOCK-RAMP-TRI-E"] = 989
rid["BLOCK-RAMP-TRI-N"] = 988
rid["BLOCK-RAMP-SINGLE"] = 987
rid["BLOCK-RAMP-PERP-W"] = 986
rid["BLOCK-RAMP-PERP-S"] = 985
rid["BLOCK-RAMP-PERP-E"] = 984
rid["BLOCK-RAMP-PERP-N"] = 983
rid["BLOCK-RAMP-EDGE-OUT-SW"] = 982
rid["BLOCK-RAMP-EDGE-OUT-SE"] = 981
rid["BLOCK-RAMP-EDGE-OUT-NW"] = 980
rid["BLOCK-RAMP-EDGE-OUT-NE"] = 979
rid["BLOCK-RAMP-EDGE-IN-SW"] = 978
rid["BLOCK-RAMP-EDGE-IN-SE"] = 977
rid["BLOCK-RAMP-EDGE-IN-NW"] = 976
rid["BLOCK-RAMP-EDGE-IN-NE"] = 975
rid["BLOCK-RAMP-CORNER-SW"] = 974
rid["BLOCK-RAMP-CORNER-SE"] = 973
rid["BLOCK-RAMP-CORNER-NW"] = 972
rid["BLOCK-RAMP-CORNER-NE"] = 971
rid["BLOCK-FOCUS"] = 970
rid["BLOCK-SELECT"] = 969
rid["BLOCK-CEILING"] = 968
rid["BLOCK-WATER"] = 967
rid["BLOCK-NORMAL"] = 966

// Tree-Branch
rid["BIRCH-BRANCH"] = 1042
rid["PINE-BRANCH"] = 1034

// Tree-Leaf
rid["BIRCH-LEAF"] = 1044
rid["PINE-LEAF"] = 1036

// Tree-Root
rid["BIRCH-ROOT"] = 1040
rid["PINE-ROOT"] = 1032

// Tree-Trunk
rid["BIRCH-TRUNK"] = 1041
rid["PINE-TRUNK"] = 1033

// Tree-Twig
rid["BIRCH-TWIG"] = 1043
rid["PINE-TWIG"] = 1035

m = new ModelMap()

m[rid["BLOCK-RAMP-TWO-SE"]] = [model: "Models/blocks/block-ramp-two/block-ramp-two.j3o", rotationDeg: [0, 0, 180]]
m[rid["BLOCK-RAMP-TWO-NE"]] = [model: "Models/blocks/block-ramp-two/block-ramp-two.j3o", rotationDeg: [0, 0, 90]]
m[rid["BLOCK-RAMP-TRI-S"]] = [model: "Models/blocks/block-ramp-tri/block-ramp-tri.j3o", rotationDeg: [0, 0, 270]]
m[rid["BLOCK-RAMP-TRI-W"]] = [model: "Models/blocks/block-ramp-tri/block-ramp-tri.j3o", rotationDeg: [0, 0, 180]]
m[rid["BLOCK-RAMP-TRI-E"]] = [model: "Models/blocks/block-ramp-tri/block-ramp-tri.j3o", rotationDeg: [0, 0, 0]]
m[rid["BLOCK-RAMP-TRI-N"]] = [model: "Models/blocks/block-ramp-tri/block-ramp-tri.j3o", rotationDeg: [0, 0, 90]]
m[rid["BLOCK-RAMP-SINGLE"]] = [model: "Models/blocks/block-ramp-single/blocks-ramp-single.j3o"]
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
m[rid["BLOCK-RAMP-CORNER-SW"]] = [model: "Models/blocks/block-ramp-corner/block-ramp-corner.j3o", rotationDeg: [0, 0, 0]]
m[rid["BLOCK-RAMP-CORNER-SE"]] = [model: "Models/blocks/block-ramp-corner/block-ramp-corner.j3o", rotationDeg: [0, 0, 90]]
m[rid["BLOCK-RAMP-CORNER-NW"]] = [model: "Models/blocks/block-ramp-corner/block-ramp-corner.j3o", rotationDeg: [0, 0, 270]]
m[rid["BLOCK-RAMP-CORNER-NE"]] = [model: "Models/blocks/block-ramp-corner/block-ramp-corner.j3o", rotationDeg: [0, 0, 180]]
m[rid["BLOCK-CEILING"]] = [model: "Models/blocks/block-ceiling/block-ceiling.j3o"]
m[rid["BLOCK-WATER"]] = [model: "Models/blocks/block-water/block-water.j3o"]
m[rid["BLOCK-NORMAL"]] = [model: "Models/blocks/block-normal/block-normal.j3o"]
m[rid["BLOCK-SELECT"]] = [model: "Models/blocks/block-normal/block-normal.j3o"]
m[rid["BLOCK-FOCUS"]] = [model: "Models/blocks/block-focused/block-focused.j3o"]

m[rid["PINE-BRANCH"]] = [model: "Models/trees/tree-branch/tree-branch.j3o"]
m[rid["PINE-LEAF"]] = [model: "Models/trees/tree-trunk/tree-trunk.j3o"]
m[rid["PINE-ROOT"]] = [model: "Models/trees/tree-trunk/tree-trunk.j3o"]
m[rid["PINE-TRUNK"]] = [model: "Models/trees/tree-trunk/tree-trunk.j3o"]
m[rid["PINE-TWIG"]] = [model: "Models/trees/tree-trunk/tree-trunk.j3o"]

m
