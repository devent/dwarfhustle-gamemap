import com.anrisoftware.dwarfhustle.gamemap.jme.assets.TexturesMap

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
// Clay
rid["FIRE-CLAY"] = 885
rid["SILTY-CLAY"] = 883
rid["SANDY-CLAY"] = 881
rid["CLAY-LOAM"] = 879
rid["CLAY"] = 779

// Gas
rid["SULFUR-DIOXIDE"] = 913
rid["CARBON-DIOXIDE"] = 912
rid["POLLUTED-OXYGEN"] = 910
rid["OXYGEN"] = 908
rid["VACUUM"] = 907

// Igneous-Extrusive
rid["RHYOLITE"] = 847
rid["OBSIDIAN"] = 846
rid["DACITE"] = 845
rid["BASALT"] = 844
rid["ANDESITE"] = 843

// Igneous-Intrusive
rid["GRANITE"] = 841
rid["GABBRO"] = 840
rid["DIORITE"] = 839

// Metamorphic
rid["SLATE"] = 854
rid["SCHIST"] = 853
rid["QUARTZITE"] = 852
rid["PHYLLITE"] = 851
rid["MARBLE"] = 850
rid["GNEISS"] = 849

// Sand
rid["YELLOW-SAND"] = 891
rid["WHITE-SAND"] = 890
rid["RED-SAND"] = 889
rid["BLACK-SAND"] = 888
rid["SAND"] = 778

// Seabed
rid["CALCAREOUS-OOZE"] = 895
rid["SILICEOUS-OOZE"] = 894
rid["PELAGIC-CLAY"] = 893

// Sedimentary
rid["SILTSTONE"] = 837
rid["SHALE"] = 836
rid["SANDSTONE"] = 835
rid["ROCK-SALT"] = 834
rid["MUDSTONE"] = 833
rid["LIMESTONE"] = 832
rid["DOLOMITE"] = 831
rid["CONGLOMERATE"] = 830
rid["CLAYSTONE"] = 829
rid["CHERT"] = 828
rid["CHALK"] = 827

// Liquid
rid["MAGMA"] = 916
rid["WATER"] = 915

// Topsoil
rid["SILT-LOAM"] = 905
rid["SILTY-CLAY-LOAM"] = 904
rid["SILT"] = 903
rid["SANDY-LOAM"] = 902
rid["SANDY-CLAY-LOAM"] = 901
rid["PEAT"] = 900
rid["LOAMY-SAND"] = 899
rid["LOAM"] = 898


rid["UNKNOWN"] = 0xffff

int w = 128, h = 128
int ww = 512, hh = 512
def white = [1f, 1f, 1f, 1f]
def trans = [0f, 0f, 0f, 1f]

m = new TexturesMap()

m.Clay.image = "Textures/Tiles/clay-map.png"
m.Clay.frames rid: rid["FIRE-CLAY"], x: 0*w, y: 2*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m.Clay.frames rid: rid["SILTY-CLAY"], x: 1*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m.Clay.frames rid: rid["SANDY-CLAY"], x: 0*w, y: 3*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m.Clay.frames rid: rid["CLAY-LOAM"], x: 0*w, y: 1*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m.Clay.frames rid: rid["CLAY"], x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f

m.Gas.image = "Textures/Tiles/gas-map.png"
m.Gas.frames rid: rid["SULFUR-DIOXIDE"], x: 0*w, y: 3*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 0f
m.Gas.frames rid: rid["CARBON-DIOXIDE"], x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 0f
m.Gas.frames rid: rid["POLLUTED-OXYGEN"], x: 0*w, y: 2*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 0f
m.Gas.frames rid: rid["OXYGEN"], ww: ww, hh: hh, transparent: true
m.Gas.frames rid: rid["VACUUM"], ww: ww, hh: hh, transparent: true

m["Igneous-Extrusive"].image = "Textures/Tiles/igneousextrusive-map.png"
m["Igneous-Extrusive"].frames rid: rid["RHYOLITE"], x: 1*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Igneous-Extrusive"].frames rid: rid["OBSIDIAN"], x: 0*w, y: 3*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Igneous-Extrusive"].frames rid: rid["DACITE"], x: 0*w, y: 2*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Igneous-Extrusive"].frames rid: rid["BASALT"], x: 0*w, y: 1*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Igneous-Extrusive"].frames rid: rid["ANDESITE"], x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f

m["Igneous-Intrusive"].image = "Textures/Tiles/igneousintrusive-map.png"
m["Igneous-Intrusive"].frames rid: rid["GRANITE"], x: 0*w, y: 2*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Igneous-Intrusive"].frames rid: rid["GABBRO"], x: 0*w, y: 1*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Igneous-Intrusive"].frames rid: rid["DIORITE"], x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f

m["Metamorphic"].image = "Textures/Tiles/metamorphic-map.png"
m["Metamorphic"].frames rid: rid["SLATE"], x: 2*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Metamorphic"].frames rid: rid["SCHIST"], x: 1*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Metamorphic"].frames rid: rid["QUARTZITE"], x: 0*w, y: 3*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Metamorphic"].frames rid: rid["PHYLLITE"], x: 0*w, y: 2*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Metamorphic"].frames rid: rid["MARBLE"], x: 0*w, y: 1*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Metamorphic"].frames rid: rid["GNEISS"], x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f

m["Sand"].image = "Textures/Tiles/sand-map.png"
m["Sand"].frames rid: rid["YELLOW-SAND"], x: 1*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Sand"].frames rid: rid["WHITE-SAND"], x: 0*w, y: 3*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Sand"].frames rid: rid["RED-SAND"], x: 0*w, y: 1*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Sand"].frames rid: rid["BLACK-SAND"], x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Sand"].frames rid: rid["SAND"], x: 0*w, y: 2*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f

m["Seabed"].image = "Textures/Tiles/seabed-map.png"
m["Seabed"].frames rid: rid["CALCAREOUS-OOZE"], x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Seabed"].frames rid: rid["SILICEOUS-OOZE"], x: 0*w, y: 2*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Seabed"].frames rid: rid["PELAGIC-CLAY"], x: 0*w, y: 1*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f

m["Sedimentary"].image = "Textures/Tiles/sedimentary-map.png"
m["Sedimentary"].frames rid: rid["SILTSTONE"], x: 2*w, y: 1*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Sedimentary"].frames rid: rid["SHALE"], x: 1*w, y: 3*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Sedimentary"].frames rid: rid["SANDSTONE"], x: 1*w, y: 2*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Sedimentary"].frames rid: rid["ROCK-SALT"], x: 1*w, y: 1*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Sedimentary"].frames rid: rid["MUDSTONE"], x: 3*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Sedimentary"].frames rid: rid["LIMESTONE"], x: 2*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Sedimentary"].frames rid: rid["DOLOMITE"], x: 1*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Sedimentary"].frames rid: rid["CONGLOMERATE"], x: 0*w, y: 3*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Sedimentary"].frames rid: rid["CLAYSTONE"], x: 0*w, y: 2*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Sedimentary"].frames rid: rid["CHERT"], x: 0*w, y: 1*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Sedimentary"].frames rid: rid["CHALK"], x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f

m["Special-Stone-Layer"].image = "Textures/Tiles/specialstonelayer-map.png"
m["Special-Stone-Layer"].frames rid: rid["MAGMA"], x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Special-Stone-Layer"].frames rid: rid["UNKNOWN"], x: 0*w, y: 1*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f

m["Topsoil"].image = "Textures/Tiles/topsoil-map.png"
m["Topsoil"].frames rid: rid["SILT-LOAM"], x: 3*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Topsoil"].frames rid: rid["SILTY-CLAY-LOAM"], x: 1*w, y: 1*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Topsoil"].frames rid: rid["SILT"], x: 2*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Topsoil"].frames rid: rid["SANDY-LOAM"], x: 1*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Topsoil"].frames rid: rid["SANDY-CLAY-LOAM"], x: 0*w, y: 3*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Topsoil"].frames rid: rid["PEAT"], x: 0*w, y: 2*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Topsoil"].frames rid: rid["LOAMY-SAND"], x: 0*w, y: 1*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Topsoil"].frames rid: rid["LOAM"], x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f

m
