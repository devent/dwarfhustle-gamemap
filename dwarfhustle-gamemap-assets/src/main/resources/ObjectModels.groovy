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
rid["BLOCK-RAMP-TWO-SE"] = 989
rid["BLOCK-RAMP-TWO-NE"] = 988
rid["BLOCK-RAMP-TRI-S"] = 987
rid["BLOCK-RAMP-TRI-W"] = 986
rid["BLOCK-RAMP-TRI-E"] = 985
rid["BLOCK-RAMP-TRI-N"] = 984
rid["BLOCK-RAMP-SINGLE"] = 983
rid["BLOCK-RAMP-PERP-W"] = 982
rid["BLOCK-RAMP-PERP-S"] = 981
rid["BLOCK-RAMP-PERP-E"] = 980
rid["BLOCK-RAMP-PERP-N"] = 979
rid["BLOCK-RAMP-EDGE-OUT-SW"] = 978
rid["BLOCK-RAMP-EDGE-OUT-SE"] = 977
rid["BLOCK-RAMP-EDGE-OUT-NW"] = 976
rid["BLOCK-RAMP-EDGE-OUT-NE"] = 975
rid["BLOCK-RAMP-EDGE-IN-SW"] = 974
rid["BLOCK-RAMP-EDGE-IN-SE"] = 973
rid["BLOCK-RAMP-EDGE-IN-NW"] = 972
rid["BLOCK-RAMP-EDGE-IN-NE"] = 971
rid["BLOCK-RAMP-CORNER-SW"] = 970
rid["BLOCK-RAMP-CORNER-SE"] = 969
rid["BLOCK-RAMP-CORNER-NW"] = 968
rid["BLOCK-RAMP-CORNER-NE"] = 967
rid["BLOCK-CEILING"] = 966
rid["BLOCK-WATER"] = 965
rid["BLOCK-NORMAL"] = 964
rid["RED-POPPY"] = 1014
rid["DAISY"] = 1013
rid["CARROT"] = 1012
rid["WHEAT"] = 1010
rid["BLUEBERRIES"] = 1016
rid["PINE"] = 1018
rid["PINE-SAMPLING"] = 1019
rid["PINE-ROOT"] = 1020
rid["PINE-TRUNK"] = 1021
rid["PINE-BRANCH"] = 1022
rid["PINE-TWIG"] = 1023
rid["PINE-LEAF"] = 1024

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
m[rid["PINE-SAMPLING"]] = [model: "Models/tree-sampling-pine/tree-sampling-pine-2.j3o"]

m
