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

m = [:]

m[rid["BLOCK-RAMP-TWO-SE"]] = 0xfff6
m[rid["BLOCK-RAMP-TWO-NE"]] = 0xfff6
m[rid["BLOCK-RAMP-TRI-S"]] = 0xfff7
m[rid["BLOCK-RAMP-TRI-W"]] = 0xfff7
m[rid["BLOCK-RAMP-TRI-E"]] = 0xfff7
m[rid["BLOCK-RAMP-TRI-N"]] = 0xfff7
m[rid["BLOCK-RAMP-SINGLE"]] = 0xfff8
m[rid["BLOCK-RAMP-PERP-W"]] = 0xfff9
m[rid["BLOCK-RAMP-PERP-S"]] = 0xfff9
m[rid["BLOCK-RAMP-PERP-E"]] = 0xfff9
m[rid["BLOCK-RAMP-PERP-N"]] = 0xfff9
m[rid["BLOCK-RAMP-EDGE-OUT-SW"]] = 0xfffa
m[rid["BLOCK-RAMP-EDGE-OUT-SE"]] = 0xfffa
m[rid["BLOCK-RAMP-EDGE-OUT-NW"]] = 0xfffa
m[rid["BLOCK-RAMP-EDGE-OUT-NE"]] = 0xfffa
m[rid["BLOCK-RAMP-EDGE-IN-SW"]] = 0xfffb
m[rid["BLOCK-RAMP-EDGE-IN-SE"]] = 0xfffb
m[rid["BLOCK-RAMP-EDGE-IN-NW"]] = 0xfffb
m[rid["BLOCK-RAMP-EDGE-IN-NE"]] = 0xfffb
m[rid["BLOCK-RAMP-CORNER-SW"]] = 0xfffc
m[rid["BLOCK-RAMP-CORNER-SE"]] = 0xfffc
m[rid["BLOCK-RAMP-CORNER-NW"]] = 0xfffc
m[rid["BLOCK-RAMP-CORNER-NE"]] = 0xfffc
m[rid["BLOCK-WATER"]] = 0xfffd
m[rid["BLOCK-NORMAL"]] = 0xfffd

// Tree-Branch
m[rid["BIRCH-BRANCH"]] = 0xfff5
m[rid["PINE-BRANCH"]] = 0xfff5

// Tree-Leaf
m[rid["BIRCH-LEAF"]] = 0xfff4
m[rid["PINE-LEAF"]] = 0xfff4

// Tree-Root
m[rid["BIRCH-ROOT"]] = 0xfff3
m[rid["PINE-ROOT"]] = 0xfff3

// Tree-Trunk
m[rid["BIRCH-TRUNK"]] = 0xfff2
m[rid["PINE-TRUNK"]] = 0xfff2

// Tree-Twig
m[rid["BIRCH-TWIG"]] = 0xfff1
m[rid["PINE-TWIG"]] = 0xfff1

m
