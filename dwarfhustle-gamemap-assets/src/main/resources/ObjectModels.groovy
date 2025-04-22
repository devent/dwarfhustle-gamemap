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
rid["BLOCK-RAMP-TWO-SE"] = 1012
rid["BLOCK-RAMP-TWO-NE"] = 1011
rid["BLOCK-RAMP-TRI-S"] = 1010
rid["BLOCK-RAMP-TRI-W"] = 1009
rid["BLOCK-RAMP-TRI-E"] = 1008
rid["BLOCK-RAMP-TRI-N"] = 1007
rid["BLOCK-RAMP-SINGLE"] = 1006
rid["BLOCK-RAMP-PERP-W"] = 1005
rid["BLOCK-RAMP-PERP-S"] = 1004
rid["BLOCK-RAMP-PERP-E"] = 1003
rid["BLOCK-RAMP-PERP-N"] = 1002
rid["BLOCK-RAMP-EDGE-OUT-SW"] = 1001
rid["BLOCK-RAMP-EDGE-OUT-SE"] = 1000
rid["BLOCK-RAMP-EDGE-OUT-NW"] = 999
rid["BLOCK-RAMP-EDGE-OUT-NE"] = 998
rid["BLOCK-RAMP-EDGE-IN-SW"] = 997
rid["BLOCK-RAMP-EDGE-IN-SE"] = 996
rid["BLOCK-RAMP-EDGE-IN-NW"] = 995
rid["BLOCK-RAMP-EDGE-IN-NE"] = 994
rid["BLOCK-RAMP-CORNER-SW"] = 993
rid["BLOCK-RAMP-CORNER-SE"] = 992
rid["BLOCK-RAMP-CORNER-NW"] = 991
rid["BLOCK-RAMP-CORNER-NE"] = 990
rid["BLOCK-FOCUS"] = 989
rid["BLOCK-SELECT"] = 988
rid["BLOCK-CEILING"] = 987
rid["BLOCK-WATER"] = 986
rid["BLOCK-NORMAL"] = 985

// Tree-Branch
rid["BIRCH-BRANCH"] = 1095
rid["PINE-BRANCH"] = 1087

// Tree-Leaf
rid["BIRCH-LEAF"] = 1097
rid["PINE-LEAF"] = 1089

// Tree-Root
rid["BIRCH-ROOT"] = 1093
rid["PINE-ROOT"] = 1085

// Tree-Trunk
rid["BIRCH-TRUNK"] = 1094
rid["PINE-TRUNK"] = 1086

// Tree-Twig
rid["BIRCH-TWIG"] = 1096
rid["PINE-TWIG"] = 1088

// Building
rid["BUILDING-WOOD-FURNANCE"] = 1149
rid["BUILDING-MAGMA-SMELTER"] = 1148
rid["BUILDING-SMELTER"] = 1147
rid["BUILDING-MAGMA-KILT"] = 1146
rid["BUILDING-KILT"] = 1145
rid["BUILDING-MAGMA-GLASS-FURNACE"] = 1144
rid["BUILDING-GLASS-FURNACE"] = 1143
rid["BUILDING-TRADE-DEPOT"] = 1142
rid["BUILDING-MILLSTONE"] = 1141
rid["BUILDING-HIVE"] = 1140
rid["BUILDING-NEST-BOX"] = 1139
rid["BUILDING-KENNEL"] = 1138
rid["BUILDING-TANNER"] = 1137
rid["BUILDING-STILL"] = 1136
rid["BUILDING-QUERN"] = 1135
rid["BUILDING-KITCHEN"] = 1134
rid["BUILDING-FISHERY"] = 1133
rid["BUILDING-FARMER"] = 1132
rid["BUILDING-BUTCHER"] = 1131
rid["BUILDING-FARM-PLOT"] = 1130
rid["BUILDING-WEAVING-MILL"] = 1129
rid["BUILDING-LOOM"] = 1128
rid["BUILDING-TANNERY"] = 1127
rid["BUILDING-DYER"] = 1126
rid["BUILDING-TAILOR"] = 1125
rid["BUILDING-SOAP-MAKER"] = 1124
rid["BUILDING-SIEGE-WORKSHOP"] = 1123
rid["BUILDING-SCREW-PRESS"] = 1122
rid["BUILDING-METALSMITH"] = 1121
rid["BUILDING-STONEMASON"] = 1120
rid["BUILDING-JEWELER"] = 1119
rid["BUILDING-CRAFT"] = 1118
rid["BUILDING-SAWMILL"] = 1114
rid["BUILDING-CARPENTER"] = 1107
rid["BUILDING-BOWYER"] = 1106
rid["BUILDING-ASHERY"] = 1105
rid["BUILDING-APIARY"] = 1104
rid["BUILDING-APOTHECARY"] = 1103
rid["BUILDING-SALT-WORKS"] = 1100

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
