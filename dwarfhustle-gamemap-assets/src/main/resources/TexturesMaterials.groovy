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
rid["FIRE-CLAY"] = 868
rid["SILTY-CLAY"] = 866
rid["SANDY-CLAY"] = 864
rid["CLAY-LOAM"] = 862
rid["CLAY"] = 779
// Gas
rid["SULFUR-DIOXIDE"] = 896
rid["CARBON-DIOXIDE"] = 895
rid["POLLUTED-OXYGEN"] = 893
rid["OXYGEN"] = 891
rid["VACUUM"] = 890
// Igneous-Extrusive
rid["RHYOLITE"] = 830
rid["OBSIDIAN"] = 829
rid["DACITE"] = 828
rid["BASALT"] = 827
rid["ANDESITE"] = 826
// Igneous-Intrusive
rid["GRANITE"] = 824
rid["GABBRO"] = 823
rid["DIORITE"] = 822
// Metamorphic
rid["SLATE"] = 837
rid["SCHIST"] = 836
rid["QUARTZITE"] = 835
rid["PHYLLITE"] = 834
rid["MARBLE"] = 833
rid["GNEISS"] = 832
// Sand
rid["YELLOW-SAND"] = 874
rid["WHITE-SAND"] = 873
rid["RED-SAND"] = 872
rid["BLACK-SAND"] = 871
rid["SAND"] = 778
// Seabed
rid["CALCAREOUS-OOZE"] = 878
rid["SILICEOUS-OOZE"] = 877
rid["PELAGIC-CLAY"] = 876
// Sedimentary
rid["SILTSTONE"] = 820
rid["SHALE"] = 819
rid["SANDSTONE"] = 818
rid["ROCK-SALT"] = 817
rid["MUDSTONE"] = 816
rid["LIMESTONE"] = 815
rid["DOLOMITE"] = 814
rid["CONGLOMERATE"] = 813
rid["CLAYSTONE"] = 812
rid["CHERT"] = 811
rid["CHALK"] = 810
// Special-Stone-Layer
rid["MAGMA"] = 808
rid["UNKNOWN"] = 0xffff
// Topsoil
rid["SILT-LOAM"] = 888
rid["SILTY-CLAY-LOAM"] = 887
rid["SILT"] = 886
rid["SANDY-LOAM"] = 885
rid["SANDY-CLAY-LOAM"] = 884
rid["PEAT"] = 883
rid["LOAMY-SAND"] = 882
rid["LOAM"] = 881
// FloorType
rid["FLOOR-CONSTRUCTURED"] = 806
rid["FLOOR-NATURAL"] = 805
rid["FLOOR-NO"] = 804
// LightType
rid["LIGHT-ARTIFICIAL"] = 800
rid["LIGHT-NATURAL"] = 799
rid["LIGHT-DARK"] = 798
// RoofType
rid["ROOF-CONSTRUCTURED"] = 803
rid["ROOF-NATURAL"] = 802
rid["ROOF-NO"] = 801
// TileType
rid["TILE-FLOOR"] = 797
rid["TILE-RAMP"] = 796
rid["TILE-MINED"] = 795
rid["TILE-TILE"] = 794

int ww = 128, hh = 128
def white = [1f, 1f, 1f, 1f]
def trans = [0f, 0f, 0f, 1f]

texturesMap = new TexturesMap()

texturesMap.Clay.image = "Textures/Tiles/Clay/clay-map.png"
texturesMap.Clay.frames rid: rid["FIRE-CLAY"], x: 0*ww, y: 2*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
texturesMap.Clay.frames rid: rid["SILTY-CLAY"], x: 1*ww, y: 0*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
texturesMap.Clay.frames rid: rid["SANDY-CLAY"], x: 0*ww, y: 3*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
texturesMap.Clay.frames rid: rid["CLAY-LOAM"], x: 0*ww, y: 1*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
texturesMap.Clay.frames rid: rid["CLAY"], x: 0*ww, y: 0*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f

texturesMap.Gas.image = "Textures/Tiles/Gas/gas-map.png"
texturesMap.Gas.frames rid: rid["SULFUR-DIOXIDE"], x: 0*ww, y: 3*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 0f
texturesMap.Gas.frames rid: rid["CARBON-DIOXIDE"], x: 0*ww, y: 0*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 0f
texturesMap.Gas.frames rid: rid["POLLUTED-OXYGEN"], x: 0*ww, y: 2*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 0f
texturesMap.Gas.frames rid: rid["OXYGEN"], transparent: true
texturesMap.Gas.frames rid: rid["VACUUM"], transparent: true

texturesMap["Igneous-Extrusive"].image = "Textures/Tiles/IgneousExtrusive/igneousextrusive-map.png"
texturesMap["Igneous-Extrusive"].frames rid: rid["RHYOLITE"], x: 1*ww, y: 0*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
texturesMap["Igneous-Extrusive"].frames rid: rid["OBSIDIAN"], x: 0*ww, y: 3*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
texturesMap["Igneous-Extrusive"].frames rid: rid["DACITE"], x: 0*ww, y: 2*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
texturesMap["Igneous-Extrusive"].frames rid: rid["BASALT"], x: 0*ww, y: 1*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
texturesMap["Igneous-Extrusive"].frames rid: rid["ANDESITE"], x: 0*ww, y: 0*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f

texturesMap["Igneous-Intrusive"].image = "Textures/Tiles/IgneousIntrusive/igneousintrusive-map.png"
texturesMap["Igneous-Intrusive"].frames rid: rid["GRANITE"], x: 0*ww, y: 2*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
texturesMap["Igneous-Intrusive"].frames rid: rid["GABBRO"], x: 0*ww, y: 1*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
texturesMap["Igneous-Intrusive"].frames rid: rid["DIORITE"], x: 0*ww, y: 0*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f

texturesMap["Metamorphic"].image = "Textures/Tiles/Metamorphic/metamorphic-map.png"
texturesMap["Metamorphic"].frames rid: rid["SLATE"], x: 2*ww, y: 0*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
texturesMap["Metamorphic"].frames rid: rid["SCHIST"], x: 1*ww, y: 0*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
texturesMap["Metamorphic"].frames rid: rid["QUARTZITE"], x: 0*ww, y: 3*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
texturesMap["Metamorphic"].frames rid: rid["PHYLLITE"], x: 0*ww, y: 2*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
texturesMap["Metamorphic"].frames rid: rid["MARBLE"], x: 0*ww, y: 1*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
texturesMap["Metamorphic"].frames rid: rid["GNEISS"], x: 0*ww, y: 0*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f

texturesMap["Sand"].image = "Textures/Tiles/Sand/sand-map.png"
texturesMap["Sand"].frames rid: rid["YELLOW-SAND"], x: 1*ww, y: 0*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
texturesMap["Sand"].frames rid: rid["WHITE-SAND"], x: 0*ww, y: 3*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
texturesMap["Sand"].frames rid: rid["RED-SAND"], x: 0*ww, y: 1*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
texturesMap["Sand"].frames rid: rid["BLACK-SAND"], x: 0*ww, y: 0*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
texturesMap["Sand"].frames rid: rid["SAND"], x: 0*ww, y: 2*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f

texturesMap["Seabed"].image = "Textures/Tiles/Seabed/seabed-map.png"
texturesMap["Seabed"].frames rid: rid["CALCAREOUS-OOZE"], x: 0*ww, y: 0*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
texturesMap["Seabed"].frames rid: rid["SILICEOUS-OOZE"], x: 0*ww, y: 2*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
texturesMap["Seabed"].frames rid: rid["PELAGIC-CLAY"], x: 0*ww, y: 1*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f

texturesMap["Sedimentary"].image = "Textures/Tiles/Sedimentary/sedimentary-map.png"
texturesMap["Sedimentary"].frames rid: rid["SILTSTONE"], x: 2*ww, y: 1*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
texturesMap["Sedimentary"].frames rid: rid["SHALE"], x: 1*ww, y: 3*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
texturesMap["Sedimentary"].frames rid: rid["SANDSTONE"], x: 1*ww, y: 2*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
texturesMap["Sedimentary"].frames rid: rid["ROCK-SALT"], x: 1*ww, y: 1*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
texturesMap["Sedimentary"].frames rid: rid["MUDSTONE"], x: 3*ww, y: 0*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
texturesMap["Sedimentary"].frames rid: rid["LIMESTONE"], x: 2*ww, y: 0*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
texturesMap["Sedimentary"].frames rid: rid["DOLOMITE"], x: 1*ww, y: 0*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
texturesMap["Sedimentary"].frames rid: rid["CONGLOMERATE"], x: 0*ww, y: 3*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
texturesMap["Sedimentary"].frames rid: rid["CLAYSTONE"], x: 0*ww, y: 2*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
texturesMap["Sedimentary"].frames rid: rid["CHERT"], x: 0*ww, y: 1*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
texturesMap["Sedimentary"].frames rid: rid["CHALK"], x: 0*ww, y: 0*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f

texturesMap["Special-Stone-Layer"].image = "Textures/Tiles/SpecialStoneLayer/specialstonelayer-map.png"
texturesMap["Special-Stone-Layer"].frames rid: rid["MAGMA"], x: 0*ww, y: 0*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
texturesMap["Special-Stone-Layer"].frames rid: rid["UNKNOWN"], x: 0*ww, y: 1*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f

texturesMap["Topsoil"].image = "Textures/Tiles/Topsoil/topsoil-map.png"
texturesMap["Topsoil"].frames rid: rid["SILT-LOAM"], x: 3*ww, y: 0*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
texturesMap["Topsoil"].frames rid: rid["SILTY-CLAY-LOAM"], x: 1*ww, y: 1*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
texturesMap["Topsoil"].frames rid: rid["SILT"], x: 2*ww, y: 0*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
texturesMap["Topsoil"].frames rid: rid["SANDY-LOAM"], x: 1*ww, y: 0*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
texturesMap["Topsoil"].frames rid: rid["SANDY-CLAY-LOAM"], x: 0*ww, y: 3*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
texturesMap["Topsoil"].frames rid: rid["PEAT"], x: 0*ww, y: 2*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
texturesMap["Topsoil"].frames rid: rid["LOAMY-SAND"], x: 0*ww, y: 1*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
texturesMap["Topsoil"].frames rid: rid["LOAM"], x: 0*ww, y: 0*hh, w: ww, h: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f

texturesMap
