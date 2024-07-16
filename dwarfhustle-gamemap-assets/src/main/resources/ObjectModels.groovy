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

// ObjectType
rid["BLOCK-RAMP-TWO-SE"] = 950
rid["BLOCK-RAMP-TWO-NE"] = 949
rid["BLOCK-RAMP-TRI-S"] = 948
rid["BLOCK-RAMP-TRI-W"] = 947
rid["BLOCK-RAMP-TRI-E"] = 946
rid["BLOCK-RAMP-TRI-N"] = 945
rid["BLOCK-RAMP-SINGLE"] = 944
rid["BLOCK-RAMP-PERP-W"] = 943
rid["BLOCK-RAMP-PERP-S"] = 942
rid["BLOCK-RAMP-PERP-E"] = 941
rid["BLOCK-RAMP-PERP-N"] = 940
rid["BLOCK-RAMP-EDGE-OUT-SW"] = 939
rid["BLOCK-RAMP-EDGE-OUT-SE"] = 938
rid["BLOCK-RAMP-EDGE-OUT-NW"] = 937
rid["BLOCK-RAMP-EDGE-OUT-NE"] = 936
rid["BLOCK-RAMP-EDGE-IN-SW"] = 935
rid["BLOCK-RAMP-EDGE-IN-SE"] = 934
rid["BLOCK-RAMP-EDGE-IN-NW"] = 933
rid["BLOCK-RAMP-EDGE-IN-NE"] = 932
rid["BLOCK-RAMP-CORNER-SW"] = 931
rid["BLOCK-RAMP-CORNER-SE"] = 930
rid["BLOCK-RAMP-CORNER-NW"] = 929
rid["BLOCK-RAMP-CORNER-NE"] = 928
rid["BLOCK-CEILING"] = 927
rid["BLOCK-WATER"] = 926
rid["BLOCK-NORMAL"] = 925
rid["BLUEBERRIES"] = 975
rid["RED-POPPY"] = 973
rid["DAISY"] = 972
rid["CARROT"] = 971
rid["WHEAT"] = 969
rid["PINE"] = 977
rid["PINE-ROOT"] = 978
rid["PINE-TRUNK"] = 979
rid["PINE-BRANCH"] = 980
rid["PINE-TWIG"] = 981
rid["PINE-LEAF"] = 982

m = new ModelMap()

m[rid["BLOCK-RAMP-TWO-SE"]] = [model: "Models/block-ramp-two/block-ramp-two.j3o", rotationDeg: [0, 0, 180]]
m[rid["BLOCK-RAMP-TWO-NE"]] = [model: "Models/block-ramp-two/block-ramp-two.j3o", rotationDeg: [0, 0, 90]]
m[rid["BLOCK-RAMP-TRI-S"]] = [model: "Models/block-ramp-tri/block-ramp-tri-flat.j3o", rotationDeg: [0, 0, 270]]
m[rid["BLOCK-RAMP-TRI-W"]] = [model: "Models/block-ramp-tri/block-ramp-tri-flat.j3o", rotationDeg: [0, 0, 180]]
m[rid["BLOCK-RAMP-TRI-E"]] = [model: "Models/block-ramp-tri/block-ramp-tri-flat.j3o", rotationDeg: [0, 0, 0]]
m[rid["BLOCK-RAMP-TRI-N"]] = [model: "Models/block-ramp-tri/block-ramp-tri-flat.j3o", rotationDeg: [0, 0, 90]]
m[rid["BLOCK-RAMP-SINGLE"]] = [model: "Models/block-ramp-single/blocks-ramp-single-flat.j3o"]
m[rid["BLOCK-RAMP-PERP-W"]] = [model: "Models/block-ramp-perp/block-ramp-perp.j3o", rotationDeg: [0, 0, 180]]
m[rid["BLOCK-RAMP-PERP-S"]] = [model: "Models/block-ramp-perp/block-ramp-perp.j3o", rotationDeg: [0, 0, 270]]
m[rid["BLOCK-RAMP-PERP-E"]] = [model: "Models/block-ramp-perp/block-ramp-perp.j3o"]
m[rid["BLOCK-RAMP-PERP-N"]] = [model: "Models/block-ramp-perp/block-ramp-perp.j3o", rotationDeg: [0, 0, 90]]
m[rid["BLOCK-RAMP-EDGE-OUT-SW"]] = [model: "Models/block-ramp-edge-out/block-ramp-edge-out.j3o", rotationDeg: [0, 0, 180]]
m[rid["BLOCK-RAMP-EDGE-OUT-SE"]] = [model: "Models/block-ramp-edge-out/block-ramp-edge-out.j3o", rotationDeg: [0, 0, 270]]
m[rid["BLOCK-RAMP-EDGE-OUT-NW"]] = [model: "Models/block-ramp-edge-out/block-ramp-edge-out.j3o", rotationDeg: [0, 0, 90]]
m[rid["BLOCK-RAMP-EDGE-OUT-NE"]] = [model: "Models/block-ramp-edge-out/block-ramp-edge-out.j3o"]
m[rid["BLOCK-RAMP-EDGE-IN-SW"]] = [model: "Models/block-ramp-edge-in/block-ramp-edge-in.j3o", rotationDeg: [0, 0, 180]]
m[rid["BLOCK-RAMP-EDGE-IN-SE"]] = [model: "Models/block-ramp-edge-in/block-ramp-edge-in.j3o", rotationDeg: [0, 0, 270]]
m[rid["BLOCK-RAMP-EDGE-IN-NW"]] = [model: "Models/block-ramp-edge-in/block-ramp-edge-in.j3o", rotationDeg: [0, 0, 90]]
m[rid["BLOCK-RAMP-EDGE-IN-NE"]] = [model: "Models/block-ramp-edge-in/block-ramp-edge-in.j3o"]
m[rid["BLOCK-RAMP-CORNER-SW"]] = [model: "Models/block-ramp-corner/block-ramp-corner-flat.j3o", rotationDeg: [0, 0, 0]]
m[rid["BLOCK-RAMP-CORNER-SE"]] = [model: "Models/block-ramp-corner/block-ramp-corner-flat.j3o", rotationDeg: [0, 0, 90]]
m[rid["BLOCK-RAMP-CORNER-NW"]] = [model: "Models/block-ramp-corner/block-ramp-corner-flat.j3o", rotationDeg: [0, 0, 270]]
m[rid["BLOCK-RAMP-CORNER-NE"]] = [model: "Models/block-ramp-corner/block-ramp-corner-flat.j3o", rotationDeg: [0, 0, 180]]
m[rid["BLOCK-CEILING"]] = [model: "Models/block-ceiling/block-ceiling.j3o"]
m[rid["BLOCK-WATER"]] = [model: "Models/block-water/block-water.j3o"]
m[rid["BLOCK-NORMAL"]] = [model: "Models/block-normal/block-normal.j3o"]

m
