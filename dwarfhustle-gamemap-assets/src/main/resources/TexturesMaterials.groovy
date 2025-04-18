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
rid["FIRE-CLAY"] = 846
rid["SILTY-CLAY"] = 845
rid["SANDY-CLAY"] = 844
rid["CLAY-LOAM"] = 843
rid["CLAY"] = 779

// Gas
rid["SULFUR-DIOXIDE"] = 873
rid["CARBON-DIOXIDE"] = 872
rid["POLLUTED-OXYGEN"] = 870
rid["OXYGEN"] = 868
rid["VACUUM"] = 867

// Igneous-Extrusive
rid["RHYOLITE"] = 811
rid["OBSIDIAN"] = 810
rid["DACITE"] = 809
rid["BASALT"] = 808
rid["ANDESITE"] = 807

// Igneous-Intrusive
rid["GRANITE"] = 805
rid["GABBRO"] = 804
rid["DIORITE"] = 803

// Metamorphic
rid["SLATE"] = 818
rid["SCHIST"] = 817
rid["QUARTZITE"] = 816
rid["PHYLLITE"] = 815
rid["MARBLE"] = 814
rid["GNEISS"] = 813

// Sand
rid["YELLOW-SAND"] = 851
rid["WHITE-SAND"] = 850
rid["RED-SAND"] = 849
rid["BLACK-SAND"] = 848
rid["SAND"] = 778

// Seabed
rid["CALCAREOUS-OOZE"] = 855
rid["SILICEOUS-OOZE"] = 854
rid["PELAGIC-CLAY"] = 853

// Sedimentary
rid["SILTSTONE"] = 801
rid["SHALE"] = 800
rid["SANDSTONE"] = 799
rid["ROCK-SALT"] = 798
rid["MUDSTONE"] = 797
rid["LIMESTONE"] = 796
rid["DOLOMITE"] = 795
rid["CONGLOMERATE"] = 794
rid["CLAYSTONE"] = 793
rid["CHERT"] = 792
rid["CHALK"] = 791

// Liquid
rid["MAGMA"] = 878
rid["BRINE"] = 877
rid["SEA-WATER"] = 876
rid["WATER"] = 875

// Topsoil
rid["SILT-LOAM"] = 865
rid["SILTY-CLAY-LOAM"] = 864
rid["SILT"] = 863
rid["SANDY-LOAM"] = 862
rid["SANDY-CLAY-LOAM"] = 861
rid["PEAT"] = 860
rid["LOAMY-SAND"] = 859
rid["LOAM"] = 858

// Wood
rid["BIRCH-WOOD"] = 1092
rid["PINE-WOOD"] = 1084

// Grass
rid["RED-POPPY"] = 1073
rid["DAISY"] = 1072
rid["CARROT"] = 1071
rid["WHEAT"] = 1069
rid["MEADOW-GRASS"] = 1068

// Shrub
rid["BLUEBERRIES"] = 1075

// Tree-Sapling
rid["BIRCH-SAPLING"] = 1086
rid["PINE-SAPLING"] = 1078

// Furniture
rid["FURNITURE-CHAIR"] = 1041
rid["FURNITURE-TABLE"] = 1040

// Container
rid["VIAL"] = 1039
rid["MUG"] = 1038
rid["JUG"] = 1037
rid["WATERSKIN"] = 1036
rid["BOTTLE"] = 1035
rid["LARGE-JUG"] = 1034
rid["BARREL"] = 1033
rid["BIN"] = 1032
rid["SACK"] = 1031
rid["BUCKET"] = 1030
rid["BASKET"] = 1029

// Misc-Object
rid["MECHANISM"] = 1028
rid["GIANT-SAWBLADE"] = 1027
rid["METAL-BAR"] = 1026
rid["METAL-ORE"] = 771
rid["ROCK-BLOCK"] = 1025
rid["ROCK-STONE"] = 1024
rid["SALT-LUMP"] = 1023
rid["WOOD-PLANK"] = 1022
rid["WOOD-LOG"] = 1021

rid["FOCUSED-BLOCK"] = 0xfffc
rid["SELECTED-BLOCK"] = 0xfffd
rid["UNDISCOVERED"] = 0xfffe
rid["UNKNOWN"] = 0xffff

int w = 256, h = 256
int ww = 1024, hh = 1024
def white = [1f, 1f, 1f, 1f]
def trans = [0f, 0f, 0f, 1f]

m = new TexturesMap()

m.Clay.image = "Textures/Tiles/clay-map.png"
m.Clay.frames rid: rid["FIRE-CLAY"] , x: 0*w, y: 2*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m.Clay.frames rid: rid["SILTY-CLAY"], x: 1*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m.Clay.frames rid: rid["SANDY-CLAY"], x: 0*w, y: 3*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m.Clay.frames rid: rid["CLAY-LOAM"] , x: 0*w, y: 1*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m.Clay.frames rid: rid["CLAY"]      , x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f

m.Gas.image = "Textures/Tiles/gas-map.png"
m.Gas.frames rid: rid["SULFUR-DIOXIDE"] , x: 0*w, y: 3*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: true, glossiness: 0f, metallic: 0f, roughness: 0f
m.Gas.frames rid: rid["CARBON-DIOXIDE"] , x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: true, glossiness: 0f, metallic: 0f, roughness: 0f
m.Gas.frames rid: rid["POLLUTED-OXYGEN"], x: 0*w, y: 2*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: true, glossiness: 0f, metallic: 0f, roughness: 0f
m.Gas.frames rid: rid["OXYGEN"]         , x: 0*w, y: 1*h, w: w, h: h, ww: ww, hh: hh, color: trans, transparent: true, glossiness: 0f, metallic: 0f, roughness: 0f
m.Gas.frames rid: rid["VACUUM"]         , x: 1*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: trans, transparent: true, glossiness: 0f, metallic: 0f, roughness: 0f

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
m["Sedimentary"].frames rid: rid["SILTSTONE"],    x: 2*w, y: 1*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Sedimentary"].frames rid: rid["SHALE"],        x: 1*w, y: 3*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Sedimentary"].frames rid: rid["SANDSTONE"],    x: 1*w, y: 2*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Sedimentary"].frames rid: rid["ROCK-SALT"],    x: 1*w, y: 1*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Sedimentary"].frames rid: rid["MUDSTONE"],     x: 3*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Sedimentary"].frames rid: rid["LIMESTONE"],    x: 2*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Sedimentary"].frames rid: rid["DOLOMITE"],     x: 1*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Sedimentary"].frames rid: rid["CONGLOMERATE"], x: 0*w, y: 3*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Sedimentary"].frames rid: rid["CLAYSTONE"],    x: 0*w, y: 2*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Sedimentary"].frames rid: rid["CHERT"],        x: 0*w, y: 1*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Sedimentary"].frames rid: rid["CHALK"],        x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f

m["Liquid"].image = "Textures/Tiles/liquid-map.png"
m["Liquid"].frames rid: rid["MAGMA"], x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: [1f, 1f, 1f, 0.5f], transparent: false, glossiness: 1f, metallic: 0f, roughness: 0f
m["Liquid"].frames rid: rid["WATER"], x: 0*w, y: 2*h, w: w, h: h, ww: ww, hh: hh, color: [1f, 1f, 1f, 0.5f], transparent: false, glossiness: 1f, metallic: 0f, roughness: 0f

m["Topsoil"].image = "Textures/Tiles/topsoil-map.png"
m["Topsoil"].frames rid: rid["SILT-LOAM"],       x: 3*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Topsoil"].frames rid: rid["SILTY-CLAY-LOAM"], x: 1*w, y: 1*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Topsoil"].frames rid: rid["SILT"],            x: 2*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Topsoil"].frames rid: rid["SANDY-LOAM"],      x: 1*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Topsoil"].frames rid: rid["SANDY-CLAY-LOAM"], x: 0*w, y: 3*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Topsoil"].frames rid: rid["PEAT"],            x: 0*w, y: 2*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Topsoil"].frames rid: rid["LOAMY-SAND"],      x: 0*w, y: 1*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Topsoil"].frames rid: rid["LOAM"],            x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f

m["Wood"].image = "Textures/Tiles/tree-map.dds"
m["Wood"].frames rid: rid["BIRCH-WOOD"], x: 1*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Wood"].frames rid: rid["PINE-WOOD"],  x: 1*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f

m["Grass"].image = "Textures/Tiles/grass-map.png"
m["Grass"].frames rid: rid["MEADOW-GRASS"], x: 0*w, y: 3*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Grass"].frames rid: rid["WHEAT"],        x: 0*w, y: 2*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Grass"].frames rid: rid["RED-POPPY"],    x: 0*w, y: 1*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Grass"].frames rid: rid["DAISY"],        x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f

m["Tree-Sapling"].image = "Textures/Tiles/tree-sapling-map.png"
m["Tree-Sapling"].frames rid: rid["BIRCH-SAPLING"], x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Tree-Sapling"].frames rid: rid["PINE-SAPLING"] , x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f

m["Misc-Object"].image = "Textures/Tiles/tree-sapling-map.png"
m["Misc-Object"].frames rid: rid["MECHANISM"], type: rid["RHYOLITE"], x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Misc-Object"].frames rid: rid["GIANT-SAWBLADE"], type: rid["RHYOLITE"], x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Misc-Object"].frames rid: rid["METAL-BAR"], type: rid["RHYOLITE"], x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Misc-Object"].frames rid: rid["METAL-ORE"], type: rid["RHYOLITE"], x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Misc-Object"].frames rid: rid["ROCK-BLOCK"], type: rid["RHYOLITE"], x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Misc-Object"].frames rid: rid["ROCK-STONE"], type: rid["RHYOLITE"], x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Misc-Object"].frames rid: rid["SALT-LUMP"], type: rid["RHYOLITE"], x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Misc-Object"].frames rid: rid["WOOD-PLANK"], type: rid["RHYOLITE"], x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m["Misc-Object"].frames rid: rid["WOOD-LOG"], type: rid["RHYOLITE"], x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f

m.Other.image = "Textures/Tiles/other-map.png"
m.Other.frames rid: rid["UNDISCOVERED"],          x: 0*w, y: 1*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m.Other.frames rid: rid["UNKNOWN"],               x: 0*w, y: 2*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m.Other.frames rid: rid["SELECTED-BLOCK"],        x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: true, glossiness: 0f, metallic: 0f, roughness: 1f
m.Other.frames rid: rid["FOCUSED-BLOCK"],         x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: true, glossiness: 0f, metallic: 0f, roughness: 1f

m
