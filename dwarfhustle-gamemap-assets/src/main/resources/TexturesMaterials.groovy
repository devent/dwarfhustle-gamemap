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
rid["FIRE-CLAY"] = 845
rid["SILTY-CLAY"] = 844
rid["SANDY-CLAY"] = 843
rid["CLAY-LOAM"] = 842
rid["CLAY"] = 779

// Gas
rid["SULFUR-DIOXIDE"] = 872
rid["CARBON-DIOXIDE"] = 871
rid["POLLUTED-OXYGEN"] = 869
rid["OXYGEN"] = 867
rid["VACUUM"] = 866

// Igneous-Extrusive
rid["RHYOLITE"] = 810
rid["OBSIDIAN"] = 809
rid["DACITE"] = 808
rid["BASALT"] = 807
rid["ANDESITE"] = 806

// Igneous-Intrusive
rid["GRANITE"] = 804
rid["GABBRO"] = 803
rid["DIORITE"] = 802

// Metamorphic
rid["SLATE"] = 817
rid["SCHIST"] = 816
rid["QUARTZITE"] = 815
rid["PHYLLITE"] = 814
rid["MARBLE"] = 813
rid["GNEISS"] = 812

// Sand
rid["YELLOW-SAND"] = 850
rid["WHITE-SAND"] = 849
rid["RED-SAND"] = 848
rid["BLACK-SAND"] = 847
rid["SAND"] = 778

// Seabed
rid["CALCAREOUS-OOZE"] = 854
rid["SILICEOUS-OOZE"] = 853
rid["PELAGIC-CLAY"] = 852

// Sedimentary
rid["SILTSTONE"] = 800
rid["SHALE"] = 799
rid["SANDSTONE"] = 798
rid["ROCK-SALT"] = 797
rid["MUDSTONE"] = 796
rid["LIMESTONE"] = 795
rid["DOLOMITE"] = 794
rid["CONGLOMERATE"] = 793
rid["CLAYSTONE"] = 792
rid["CHERT"] = 791
rid["CHALK"] = 790

// Liquid
rid["MAGMA"] = 875
rid["WATER"] = 874

// Topsoil
rid["SILT-LOAM"] = 864
rid["SILTY-CLAY-LOAM"] = 863
rid["SILT"] = 862
rid["SANDY-LOAM"] = 861
rid["SANDY-CLAY-LOAM"] = 860
rid["PEAT"] = 859
rid["LOAMY-SAND"] = 858
rid["LOAM"] = 857

// Wood
rid["BIRCH-WOOD"] = 1042
rid["PINE-WOOD"] = 1034

rid["SELECTED-BLOCK-RAMP-TWO"] = 0xfff6
rid["SELECTED-BLOCK-RAMP-TRI"] = 0xfff7
rid["SELECTED-BLOCK-RAMP-SINGLE"] = 0xfff8
rid["SELECTED-BLOCK-RAMP-PERP"] = 0xfff9
rid["SELECTED-BLOCK-RAMP-EDGE-OUT"] = 0xfffa
rid["SELECTED-BLOCK-RAMP-EDGE-IN"] = 0xfffb
rid["SELECTED-BLOCK-RAMP-CORNER"] = 0xfffc
rid["SELECTED-BLOCK-NORMAL"] = 0xfffd
rid["UNDISCOVERED"] = 0xfffe
rid["UNKNOWN"] = 0xffff

// Tree-Branch
rid["SELECTED-BLOCK-BRANCH"] = 0xfff5

// Tree-Leaf
rid["SELECTED-BLOCK-LEAF"] = 0xfff4

// Tree-Root
rid["SELECTED-BLOCK-ROOT"] = 0xfff3

// Tree-Trunk
rid["SELECTED-BLOCK-TRUNK"] = 0xfff2

// Tree-Twig
rid["SELECTED-BLOCK-TWIG"] = 0xfff1

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

m.Other.image = "Textures/Tiles/other-map.png"
m.Other.frames rid: rid["UNDISCOVERED"],                 x: 0*w, y: 1*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m.Other.frames rid: rid["UNKNOWN"],                      x: 0*w, y: 2*h, w: w, h: h, ww: ww, hh: hh, color: white, transparent: false, glossiness: 0f, metallic: 0f, roughness: 1f
m.Other.frames rid: rid["SELECTED-BLOCK-RAMP-TWO"],      x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh
m.Other.frames rid: rid["SELECTED-BLOCK-RAMP-TRI"],      x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh
m.Other.frames rid: rid["SELECTED-BLOCK-RAMP-SINGLE"],   x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh
m.Other.frames rid: rid["SELECTED-BLOCK-RAMP-PERP"],     x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh
m.Other.frames rid: rid["SELECTED-BLOCK-RAMP-EDGE-OUT"], x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh
m.Other.frames rid: rid["SELECTED-BLOCK-RAMP-EDGE-IN"],  x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh
m.Other.frames rid: rid["SELECTED-BLOCK-RAMP-CORNER"],   x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh
m.Other.frames rid: rid["SELECTED-BLOCK-NORMAL"],        x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh
// Tree-Branch
m.Other.frames rid: rid["SELECTED-BLOCK-BRANCH"],        x: 0*w, y: 3*h, w: w, h: h, ww: ww, hh: hh

// Tree-Leaf
m.Other.frames rid: rid["SELECTED-BLOCK-LEAF"],          x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh

// Tree-Root
m.Other.frames rid: rid["SELECTED-BLOCK-ROOT"],          x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh

// Tree-Trunk
m.Other.frames rid: rid["SELECTED-BLOCK-TRUNK"],         x: 0*w, y: 3*h, w: w, h: h, ww: ww, hh: hh

// Tree-Twig
m.Other.frames rid: rid["SELECTED-BLOCK-TWIG"],          x: 0*w, y: 0*h, w: w, h: h, ww: ww, hh: hh

m
