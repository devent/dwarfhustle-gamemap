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
rid = [:]

// ObjectType
rid["TILE-RAMP-TRI"] = 825
rid["TILE-RAMP-SINGLE"] = 824
rid["TILE-RAMP-PERP"] = 823
rid["TILE-RAMP-EDGE-OUT"] = 822
rid["TILE-RAMP-EDGE-IN"] = 821
rid["TILE-RAMP-CORNER"] = 820
rid["TILE-BLOCK"] = 819

m = new ModelMap()

m[rid["TILE-RAMP-TRI"]] = [model: "Models/block-ramp-tri/block-ramp-tri.j3o"]
m[rid["TILE-RAMP-SINGLE"]] = [model: "Models/block-ramp-single/block-ramp-single.j3o"]
m[rid["TILE-RAMP-PERP"]] = [model: "Models/block-ramp-perp/block-ramp-perp.j3o"]
m[rid["TILE-RAMP-EDGE-OUT"]] = [model: "Models/block-ramp-edge-out/block-ramp-edge-out.j3o"]
m[rid["TILE-RAMP-EDGE-IN"]] = [model: "Models/block-ramp-edge-in/block-ramp-edge-in.j3o"]
m[rid["TILE-RAMP-CORNER"]] = [model: "Models/block-ramp-corner/block-ramp-corner.j3o"]
m[rid["TILE-BLOCK"]] = [model: "Models/tile-block/tile-block.j3o"]

m
