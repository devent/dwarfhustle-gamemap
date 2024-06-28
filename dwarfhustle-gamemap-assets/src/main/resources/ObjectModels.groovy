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
rid["BLOCK-RAMP-TWO-SE"] = 844
rid["BLOCK-RAMP-TWO-NE"] = 843
rid["BLOCK-RAMP-TRI-S"] = 842
rid["BLOCK-RAMP-TRI-W"] = 841
rid["BLOCK-RAMP-TRI-E"] = 840
rid["BLOCK-RAMP-TRI-N"] = 839
rid["BLOCK-RAMP-SINGLE"] = 838
rid["BLOCK-RAMP-PERP-W"] = 837
rid["BLOCK-RAMP-PERP-S"] = 836
rid["BLOCK-RAMP-PERP-E"] = 835
rid["BLOCK-RAMP-PERP-N"] = 834
rid["BLOCK-RAMP-EDGE-OUT-SW"] = 833
rid["BLOCK-RAMP-EDGE-OUT-SE"] = 832
rid["BLOCK-RAMP-EDGE-OUT-NW"] = 831
rid["BLOCK-RAMP-EDGE-OUT-NE"] = 830
rid["BLOCK-RAMP-EDGE-IN-SW"] = 829
rid["BLOCK-RAMP-EDGE-IN-SE"] = 828
rid["BLOCK-RAMP-EDGE-IN-NW"] = 827
rid["BLOCK-RAMP-EDGE-IN-NE"] = 826
rid["BLOCK-RAMP-CORNER-SW"] = 825
rid["BLOCK-RAMP-CORNER-SE"] = 824
rid["BLOCK-RAMP-CORNER-NW"] = 823
rid["BLOCK-RAMP-CORNER-NE"] = 822
rid["BLOCK-CEILING"] = 821
rid["BLOCK-WATER"] = 820
rid["BLOCK-NORMAL"] = 819

m = new ModelMap()

m[rid["BLOCK-RAMP-TWO-SE"]] = [model: "Models/block-ramp-two/block-ramp-two.j3o", rotationDeg: [0, 0, 180]]
m[rid["BLOCK-RAMP-TWO-NE"]] = [model: "Models/block-ramp-two/block-ramp-two.j3o", rotationDeg: [0, 0, 90]]
m[rid["BLOCK-RAMP-TRI-S"]] = [model: "Models/block-ramp-tri/block-ramp-tri-flat.j3o", rotationDeg: [0, 0, 270]]
m[rid["BLOCK-RAMP-TRI-W"]] = [model: "Models/block-ramp-tri/block-ramp-tri-flat.j3o", rotationDeg: [0, 0, 180]]
m[rid["BLOCK-RAMP-TRI-E"]] = [model: "Models/block-ramp-tri/block-ramp-tri-flat.j3o", rotationDeg: [0, 0, 0]]
m[rid["BLOCK-RAMP-TRI-N"]] = [model: "Models/block-ramp-tri/block-ramp-tri-flat.j3o", rotationDeg: [0, 0, 90]]
m[rid["BLOCK-RAMP-SINGLE"]] = [model: "Models/block-ramp-single/blocks-ramp-single-flat.j3o"]
m[rid["BLOCK-RAMP-PERP-W"]] = [model: "Models/block-ramp-perp/block-ramp-perp.j3o"]
m[rid["BLOCK-RAMP-PERP-S"]] = [model: "Models/block-ramp-perp/block-ramp-perp.j3o", rotationDeg: [0, 0, 270]]
m[rid["BLOCK-RAMP-PERP-E"]] = [model: "Models/block-ramp-perp/block-ramp-perp.j3o"]
m[rid["BLOCK-RAMP-PERP-N"]] = [model: "Models/block-ramp-perp/block-ramp-perp.j3o"]
m[rid["BLOCK-RAMP-EDGE-OUT-SW"]] = [model: "Models/block-ramp-edge-out/block-ramp-edge-out.j3o", rotationDeg: [0, 0, 180]]
m[rid["BLOCK-RAMP-EDGE-OUT-SE"]] = [model: "Models/block-ramp-edge-out/block-ramp-edge-out.j3o", rotationDeg: [0, 0, 270]]
m[rid["BLOCK-RAMP-EDGE-OUT-NW"]] = [model: "Models/block-ramp-edge-out/block-ramp-edge-out.j3o", rotationDeg: [0, 0, 90]]
m[rid["BLOCK-RAMP-EDGE-OUT-NE"]] = [model: "Models/block-ramp-edge-out/block-ramp-edge-out.j3o"]
m[rid["BLOCK-RAMP-EDGE-IN-SW"]] = [model: "Models/block-ramp-edge-in/block-ramp-edge-in.j3o", rotationDeg: [0, 0, 180]]
m[rid["BLOCK-RAMP-EDGE-IN-SE"]] = [model: "Models/block-ramp-edge-in/block-ramp-edge-in.j3o"]
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
