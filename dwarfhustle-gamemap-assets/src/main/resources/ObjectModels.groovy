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
rid["BLOCK-RAMP-TWO-SE"] = 1004
rid["BLOCK-RAMP-TWO-NE"] = 1003
rid["BLOCK-RAMP-TRI-S"] = 1002
rid["BLOCK-RAMP-TRI-W"] = 1001
rid["BLOCK-RAMP-TRI-E"] = 1000
rid["BLOCK-RAMP-TRI-N"] = 999
rid["BLOCK-RAMP-SINGLE"] = 998
rid["BLOCK-RAMP-PERP-W"] = 997
rid["BLOCK-RAMP-PERP-S"] = 996
rid["BLOCK-RAMP-PERP-E"] = 995
rid["BLOCK-RAMP-PERP-N"] = 994
rid["BLOCK-RAMP-EDGE-OUT-SW"] = 993
rid["BLOCK-RAMP-EDGE-OUT-SE"] = 992
rid["BLOCK-RAMP-EDGE-OUT-NW"] = 991
rid["BLOCK-RAMP-EDGE-OUT-NE"] = 990
rid["BLOCK-RAMP-EDGE-IN-SW"] = 989
rid["BLOCK-RAMP-EDGE-IN-SE"] = 988
rid["BLOCK-RAMP-EDGE-IN-NW"] = 987
rid["BLOCK-RAMP-EDGE-IN-NE"] = 986
rid["BLOCK-RAMP-CORNER-SW"] = 985
rid["BLOCK-RAMP-CORNER-SE"] = 984
rid["BLOCK-RAMP-CORNER-NW"] = 983
rid["BLOCK-RAMP-CORNER-NE"] = 982
rid["BLOCK-FOCUS"] = 981
rid["BLOCK-SELECT"] = 980
rid["BLOCK-CEILING"] = 979
rid["BLOCK-WATER"] = 978
rid["BLOCK-NORMAL"] = 977

// Tree-Branch
rid["BIRCH-BRANCH"] = 1083
rid["PINE-BRANCH"] = 1075

// Tree-Leaf
rid["BIRCH-LEAF"] = 1085
rid["PINE-LEAF"] = 1077

// Tree-Root
rid["BIRCH-ROOT"] = 1081
rid["PINE-ROOT"] = 1073

// Tree-Trunk
rid["BIRCH-TRUNK"] = 1082
rid["PINE-TRUNK"] = 1074

// Tree-Twig
rid["BIRCH-TWIG"] = 1084
rid["PINE-TWIG"] = 1076

// Building
rid["BUILDING-WOOD-FURNANCE"] = 1139
rid["BUILDING-MAGMA-SMELTER"] = 1138
rid["BUILDING-SMELTER"] = 1137
rid["BUILDING-MAGMA-KILT"] = 1136
rid["BUILDING-KILT"] = 1135
rid["BUILDING-MAGMA-GLASS-FURNACE"] = 1134
rid["BUILDING-GLASS-FURNACE"] = 1133
rid["BUILDING-TRADE-DEPOT"] = 1132
rid["BUILDING-MILLSTONE"] = 1131
rid["BUILDING-HIVE"] = 1130
rid["BUILDING-NEST-BOX"] = 1129
rid["BUILDING-KENNEL"] = 1128
rid["BUILDING-TANNER"] = 1127
rid["BUILDING-STILL"] = 1126
rid["BUILDING-QUERN"] = 1125
rid["BUILDING-KITCHEN"] = 1124
rid["BUILDING-FISHERY"] = 1123
rid["BUILDING-FARMER"] = 1122
rid["BUILDING-BUTCHER"] = 1121
rid["BUILDING-FARM-PLOT"] = 1120
rid["BUILDING-WEAVING-MILL"] = 1119
rid["BUILDING-LOOM"] = 1118
rid["BUILDING-TANNERY"] = 1117
rid["BUILDING-DYER"] = 1116
rid["BUILDING-TAILOR"] = 1115
rid["BUILDING-SOAP-MAKER"] = 1114
rid["BUILDING-SIEGE-WORKSHOP"] = 1113
rid["BUILDING-SCREW-PRESS"] = 1112
rid["BUILDING-METALSMITH"] = 1111
rid["BUILDING-STONEMASON"] = 1110
rid["BUILDING-JEWELER"] = 1109
rid["BUILDING-CRAFT"] = 1108
rid["BUILDING-SAWMILL"] = 1102
rid["BUILDING-CARPENTER"] = 1095
rid["BUILDING-BOWYER"] = 1094
rid["BUILDING-ASHERY"] = 1093
rid["BUILDING-APIARY"] = 1092
rid["BUILDING-APOTHECARY"] = 1091
rid["BUILDING-SALT-WORKS"] = 1088

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

m[rid["BUILDING-CARPENTER"]] = [model: "Models/building-carpenter/building-carpenter.j3o"]

m
