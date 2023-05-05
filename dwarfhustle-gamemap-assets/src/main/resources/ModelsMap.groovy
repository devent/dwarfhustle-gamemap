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
// TileType
rid["tile-tile"] = 816
rid["tile-mined"] = 815
rid["tile-ramp"] = 814
rid["tile-floor"] = 813

models = [:]
models[rid["tile-tile"]] = [baseColorMap: "Textures/Tiles/Sedimentary/siltstone-1.png", "glossiness": 0f, "metallic": 0f, "roughness": 1f]
models[rid["tile-mined"]] = [baseColorMap: "Textures/Tiles/Sedimentary/shale-1-1024.png", "glossiness": 0f, "metallic": 0f, "roughness": 1f]
models[rid["tile-ramp"]] = [baseColorMap: "Textures/Tiles/Sedimentary/sandstone-01.png", "glossiness": 0f, "metallic": 0f, "roughness": 1f]
models[rid["tile-floor"]] = [baseColorMap: "Textures/Tiles/Sedimentary/rock_salt-1-1024.png", "glossiness": 0f, "metallic": 0f, "roughness": 1f]

models
