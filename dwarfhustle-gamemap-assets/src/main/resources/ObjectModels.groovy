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
rid["TILE-RAMP-TRI-S"] = 840
rid["TILE-RAMP-TRI-W"] = 839
rid["TILE-RAMP-TRI-E"] = 838
rid["TILE-RAMP-TRI-N"] = 837
rid["TILE-RAMP-SINGLE"] = 836
rid["TILE-RAMP-PERP-W"] = 835
rid["TILE-RAMP-PERP-S"] = 834
rid["TILE-RAMP-PERP-E"] = 833
rid["TILE-RAMP-PERP-N"] = 832
rid["TILE-RAMP-EDGE-OUT-SW"] = 831
rid["TILE-RAMP-EDGE-OUT-SE"] = 830
rid["TILE-RAMP-EDGE-OUT-NW"] = 829
rid["TILE-RAMP-EDGE-OUT-NE"] = 828
rid["TILE-RAMP-EDGE-IN-SW"] = 827
rid["TILE-RAMP-EDGE-IN-SE"] = 826
rid["TILE-RAMP-EDGE-IN-NW"] = 825
rid["TILE-RAMP-EDGE-IN-NE"] = 824
rid["TILE-RAMP-CORNER-SW"] = 823
rid["TILE-RAMP-CORNER-SE"] = 822
rid["TILE-RAMP-CORNER-NW"] = 821
rid["TILE-RAMP-CORNER-NE"] = 820
rid["TILE-BLOCK"] = 819

m = new ModelMap()

m[rid["TILE-RAMP-TRI-S"]] = [model: "Models/block-ramp-tri/block-ramp-tri-flat.j3o", rotationDeg: [0, 0, 270]]
m[rid["TILE-RAMP-TRI-W"]] = [model: "Models/block-ramp-tri/block-ramp-tri-flat.j3o", rotationDeg: [0, 0, 180]]
m[rid["TILE-RAMP-TRI-E"]] = [model: "Models/block-ramp-tri/block-ramp-tri-flat.j3o", rotationDeg: [0, 0, 0]]
m[rid["TILE-RAMP-TRI-N"]] = [model: "Models/block-ramp-tri/block-ramp-tri-flat.j3o", rotationDeg: [0, 0, 90]]
m[rid["TILE-RAMP-SINGLE"]] = [model: "Models/block-ramp-single/blocks-ramp-single-flat.j3o", rotationDeg: [0, 0, 0]]
m[rid["TILE-RAMP-PERP-W"]] = [model: "Models/block-ramp-perp/block-ramp-perp.j3o"]
m[rid["TILE-RAMP-PERP-S"]] = [model: "Models/block-ramp-perp/block-ramp-perp.j3o"]
m[rid["TILE-RAMP-PERP-E"]] = [model: "Models/block-ramp-perp/block-ramp-perp.j3o"]
m[rid["TILE-RAMP-PERP-N"]] = [model: "Models/block-ramp-perp/block-ramp-perp.j3o"]
m[rid["TILE-RAMP-EDGE-OUT-SW"]] = [model: "Models/block-ramp-edge-out/block-ramp-edge-out.j3o"]
m[rid["TILE-RAMP-EDGE-OUT-SE"]] = [model: "Models/block-ramp-edge-out/block-ramp-edge-out.j3o"]
m[rid["TILE-RAMP-EDGE-OUT-NW"]] = [model: "Models/block-ramp-edge-out/block-ramp-edge-out.j3o"]
m[rid["TILE-RAMP-EDGE-OUT-NE"]] = [model: "Models/block-ramp-edge-out/block-ramp-edge-out.j3o"]
m[rid["TILE-RAMP-EDGE-IN-SW"]] = [model: "Models/block-ramp-edge-in/block-ramp-edge-in.j3o"]
m[rid["TILE-RAMP-EDGE-IN-SE"]] = [model: "Models/block-ramp-edge-in/block-ramp-edge-in.j3o"]
m[rid["TILE-RAMP-EDGE-IN-NW"]] = [model: "Models/block-ramp-edge-in/block-ramp-edge-in.j3o"]
m[rid["TILE-RAMP-EDGE-IN-NE"]] = [model: "Models/block-ramp-edge-in/block-ramp-edge-in.j3o"]
m[rid["TILE-RAMP-CORNER-SW"]] = [model: "Models/block-ramp-corner/block-ramp-corner-flat.j3o", rotationDeg: [0, 0, 0]]
m[rid["TILE-RAMP-CORNER-SE"]] = [model: "Models/block-ramp-corner/block-ramp-corner-flat.j3o", rotationDeg: [0, 0, 90]]
m[rid["TILE-RAMP-CORNER-NW"]] = [model: "Models/block-ramp-corner/block-ramp-corner-flat.j3o", rotationDeg: [0, 0, 270]]
m[rid["TILE-RAMP-CORNER-NE"]] = [model: "Models/block-ramp-corner/block-ramp-corner-flat.j3o", rotationDeg: [0, 0, 180]]
m[rid["TILE-BLOCK"]] = [model: "Models/tile-block/tile-block.j3o"]

m
