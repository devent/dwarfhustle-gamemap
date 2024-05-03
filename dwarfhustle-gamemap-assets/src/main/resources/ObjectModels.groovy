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

m[rid["TILE-RAMP-TRI-S"]] = [model: "Models/tile-ramp-tri-s/tile-ramp-tri.j3o"]
m[rid["TILE-RAMP-TRI-W"]] = [model: "Models/tile-ramp-tri-w/tile-ramp-tri.j3o"]
m[rid["TILE-RAMP-TRI-E"]] = [model: "Models/tile-ramp-tri-e/tile-ramp-tri.j3o"]
m[rid["TILE-RAMP-TRI-N"]] = [model: "Models/tile-ramp-tri-n/tile-ramp-tri.j3o"]
m[rid["TILE-RAMP-SINGLE"]] = [model: "Models/tile-ramp-single/tile-ramp-single.j3o"]
m[rid["TILE-RAMP-PERP-W"]] = [model: "Models/tile-ramp-perp-w/tile-ramp-perp.j3o"]
m[rid["TILE-RAMP-PERP-S"]] = [model: "Models/tile-ramp-perp-s/tile-ramp-perp.j3o"]
m[rid["TILE-RAMP-PERP-E"]] = [model: "Models/tile-ramp-perp-e/tile-ramp-perp.j3o"]
m[rid["TILE-RAMP-PERP-N"]] = [model: "Models/tile-ramp-perp-n/tile-ramp-perp.j3o"]
m[rid["TILE-RAMP-EDGE-OUT-SW"]] = [model: "Models/tile-ramp-edge-out-sw/tile-ramp-edge-out.j3o"]
m[rid["TILE-RAMP-EDGE-OUT-SE"]] = [model: "Models/tile-ramp-edge-out-se/tile-ramp-edge-out.j3o"]
m[rid["TILE-RAMP-EDGE-OUT-NW"]] = [model: "Models/tile-ramp-edge-out-nw/tile-ramp-edge-out.j3o"]
m[rid["TILE-RAMP-EDGE-OUT-NE"]] = [model: "Models/tile-ramp-edge-out-ne/tile-ramp-edge-out.j3o"]
m[rid["TILE-RAMP-EDGE-IN-SW"]] = [model: "Models/tile-ramp-edge-in-sw/tile-ramp-edge-in.j3o"]
m[rid["TILE-RAMP-EDGE-IN-SE"]] = [model: "Models/tile-ramp-edge-in-se/tile-ramp-edge-in.j3o"]
m[rid["TILE-RAMP-EDGE-IN-NW"]] = [model: "Models/tile-ramp-edge-in-nw/tile-ramp-edge-in.j3o"]
m[rid["TILE-RAMP-EDGE-IN-NE"]] = [model: "Models/tile-ramp-edge-in-ne/tile-ramp-edge-in.j3o"]
m[rid["TILE-RAMP-CORNER-SW"]] = [model: "Models/tile-ramp-corner-sw/tile-ramp-corner.j3o"]
m[rid["TILE-RAMP-CORNER-SE"]] = [model: "Models/tile-ramp-corner-se/tile-ramp-corner.j3o"]
m[rid["TILE-RAMP-CORNER-NW"]] = [model: "Models/tile-ramp-corner-nw/tile-ramp-corner.j3o"]
m[rid["TILE-RAMP-CORNER-NE"]] = [model: "Models/tile-ramp-corner-ne/tile-ramp-corner.j3o"]
m[rid["TILE-BLOCK"]] = [model: "Models/tile-block/tile-block.j3o"]

m

