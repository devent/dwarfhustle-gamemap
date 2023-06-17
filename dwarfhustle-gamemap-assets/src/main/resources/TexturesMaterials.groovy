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
rid["FIRE-CLAY"] = 875
rid["SILTY-CLAY"] = 873
rid["SANDY-CLAY"] = 871
rid["CLAY-LOAM"] = 869
rid["CLAY"] = 779
// Gas
rid["SULFUR-DIOXIDE"] = 903
rid["CARBON-DIOXIDE"] = 902
rid["POLLUTED-OXYGEN"] = 900
rid["OXYGEN"] = 898
rid["VACUUM"] = 897
// Igneous-Extrusive
rid["RHYOLITE"] = 837
rid["OBSIDIAN"] = 836
rid["DACITE"] = 835
rid["BASALT"] = 834
rid["ANDESITE"] = 833
// Igneous-Intrusive
rid["GRANITE"] = 831
rid["GABBRO"] = 830
rid["DIORITE"] = 829
// Metamorphic
rid["SLATE"] = 844
rid["SCHIST"] = 843
rid["QUARTZITE"] = 842
rid["PHYLLITE"] = 841
rid["MARBLE"] = 840
rid["GNEISS"] = 839
// Sand
rid["YELLOW-SAND"] = 881
rid["WHITE-SAND"] = 880
rid["RED-SAND"] = 879
rid["BLACK-SAND"] = 878
rid["SAND"] = 778
// Seabed
rid["CALCAREOUS-OOZE"] = 885
rid["SILICEOUS-OOZE"] = 884
rid["PELAGIC-CLAY"] = 883
// Sedimentary
rid["SILTSTONE"] = 827
rid["SHALE"] = 826
rid["SANDSTONE"] = 825
rid["ROCK-SALT"] = 824
rid["MUDSTONE"] = 823
rid["LIMESTONE"] = 822
rid["DOLOMITE"] = 821
rid["CONGLOMERATE"] = 820
rid["CLAYSTONE"] = 819
rid["CHERT"] = 818
rid["CHALK"] = 817
// Special-Stone-Layer
rid["MAGMA"] = 815
// Topsoil
rid["SILT-LOAM"] = 895
rid["SILTY-CLAY-LOAM"] = 894
rid["SILT"] = 893
rid["SANDY-LOAM"] = 892
rid["SANDY-CLAY-LOAM"] = 891
rid["PEAT"] = 890
rid["LOAMY-SAND"] = 889
rid["LOAM"] = 888

rid["UNKNOWN"] = 0xffff

int w = 128, h = 128
int ww = 512, hh = 512
def white = [1f, 1f, 1f, 1f]
def trans = [0f, 0f, 0f, 1f]

m = new TexturesMap()

m.Clay.image = "Textures/Tiles/Clay/clay-map.png"
m.Clay.frames rid: rid["FIRE-CLAY"], x: 0*w, y: 2*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m.Clay.frames rid: rid["SILTY-CLAY"], x: 1*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m.Clay.frames rid: rid["SANDY-CLAY"], x: 0*w, y: 3*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m.Clay.frames rid: rid["CLAY-LOAM"], x: 0*w, y: 1*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m.Clay.frames rid: rid["CLAY"], x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f

m.Gas.image = "Textures/Tiles/Gas/gas-map.png"
m.Gas.frames rid: rid["SULFUR-DIOXIDE"], x: 0*w, y: 3*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 0f
m.Gas.frames rid: rid["CARBON-DIOXIDE"], x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 0f
m.Gas.frames rid: rid["POLLUTED-OXYGEN"], x: 0*w, y: 2*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 0f
m.Gas.frames rid: rid["OXYGEN"], ww: ww, hh: hh, transparent: true
m.Gas.frames rid: rid["VACUUM"], ww: ww, hh: hh, transparent: true

m["Igneous-Extrusive"].image = "Textures/Tiles/IgneousExtrusive/igneousextrusive-map.png"
m["Igneous-Extrusive"].frames rid: rid["RHYOLITE"], x: 1*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Igneous-Extrusive"].frames rid: rid["OBSIDIAN"], x: 0*w, y: 3*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Igneous-Extrusive"].frames rid: rid["DACITE"], x: 0*w, y: 2*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Igneous-Extrusive"].frames rid: rid["BASALT"], x: 0*w, y: 1*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Igneous-Extrusive"].frames rid: rid["ANDESITE"], x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f

m["Igneous-Intrusive"].image = "Textures/Tiles/IgneousIntrusive/igneousintrusive-map.png"
m["Igneous-Intrusive"].frames rid: rid["GRANITE"], x: 0*w, y: 2*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Igneous-Intrusive"].frames rid: rid["GABBRO"], x: 0*w, y: 1*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Igneous-Intrusive"].frames rid: rid["DIORITE"], x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f

m["Metamorphic"].image = "Textures/Tiles/Metamorphic/metamorphic-map.png"
m["Metamorphic"].frames rid: rid["SLATE"], x: 2*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Metamorphic"].frames rid: rid["SCHIST"], x: 1*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Metamorphic"].frames rid: rid["QUARTZITE"], x: 0*w, y: 3*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Metamorphic"].frames rid: rid["PHYLLITE"], x: 0*w, y: 2*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Metamorphic"].frames rid: rid["MARBLE"], x: 0*w, y: 1*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Metamorphic"].frames rid: rid["GNEISS"], x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f

m["Sand"].image = "Textures/Tiles/Sand/sand-map.png"
m["Sand"].frames rid: rid["YELLOW-SAND"], x: 1*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Sand"].frames rid: rid["WHITE-SAND"], x: 0*w, y: 3*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Sand"].frames rid: rid["RED-SAND"], x: 0*w, y: 1*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Sand"].frames rid: rid["BLACK-SAND"], x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Sand"].frames rid: rid["SAND"], x: 0*w, y: 2*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f

m["Seabed"].image = "Textures/Tiles/Seabed/seabed-map.png"
m["Seabed"].frames rid: rid["CALCAREOUS-OOZE"], x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Seabed"].frames rid: rid["SILICEOUS-OOZE"], x: 0*w, y: 2*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Seabed"].frames rid: rid["PELAGIC-CLAY"], x: 0*w, y: 1*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f

m["Sedimentary"].image = "Textures/Tiles/Sedimentary/sedimentary-map.png"
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

m["Special-Stone-Layer"].image = "Textures/Tiles/SpecialStoneLayer/specialstonelayer-map.png"
m["Special-Stone-Layer"].frames rid: rid["MAGMA"], x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Special-Stone-Layer"].frames rid: rid["UNKNOWN"], x: 0*w, y: 1*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f

m["Topsoil"].image = "Textures/Tiles/Topsoil/topsoil-map.png"
m["Topsoil"].frames rid: rid["SILT-LOAM"], x: 3*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Topsoil"].frames rid: rid["SILTY-CLAY-LOAM"], x: 1*w, y: 1*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Topsoil"].frames rid: rid["SILT"], x: 2*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Topsoil"].frames rid: rid["SANDY-LOAM"], x: 1*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Topsoil"].frames rid: rid["SANDY-CLAY-LOAM"], x: 0*w, y: 3*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Topsoil"].frames rid: rid["PEAT"], x: 0*w, y: 2*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Topsoil"].frames rid: rid["LOAMY-SAND"], x: 0*w, y: 1*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Topsoil"].frames rid: rid["LOAM"], x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f

m
