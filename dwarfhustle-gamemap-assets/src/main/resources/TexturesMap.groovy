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
rid["siltstone"] = 816
rid["shale"] = 815
rid["sandstone"] = 814
rid["rock_salt"] = 813
rid["mudstone"] = 812
rid["limestone"] = 811
rid["dolomite"] = 810
rid["conglomerate"] = 809
rid["claystone"] = 808
rid["chert"] = 807
rid["chalk"] = 806
// IgneousIntrusive
rid["granite"] = 820
rid["gabbro"] = 819
rid["diorite"] = 818
// IgneousExtrusive
rid["rhyolite"] = 826
rid["obsidian"] = 825
rid["dacite"] = 824
rid["basalt"] = 823
rid["andesite"] = 822
// Metamorphic
rid["slate"] = 833
rid["schist"] = 832
rid["quartzite"] = 831
rid["phyllite"] = 830
rid["marble"] = 829
rid["gneiss"] = 828
// special
rid["magma"] = 804
// soil
rid["silt_loam"] = 884
rid["silty_clay_loam"] = 883
rid["silt"] = 882
rid["sandy_loam"] = 881
rid["sandy_clay_loam"] = 880
rid["peat"] = 879
rid["loamy_sand"] = 878
rid["loam"] = 877
rid["calcareous_ooze"] = 874
rid["siliceous_ooze"] = 873
rid["pelagic_clay"] = 872
rid["yellow_sand"] = 870
rid["white_sand"] = 869
rid["red_sand"] = 868
rid["black_sand"] = 867
rid["sand"] = 778
rid["fire_clay"] = 864
rid["silty_clay"] = 862
rid["sandy_clay"] = 860
rid["clay_loam"] = 858
rid["clay"] = 779
// gas
rid["sulfur_dioxide"] = 892
rid["carbon_dioxide"] = 891
rid["polluted_oxygen"] = 889
rid["oxygen"] = 887
rid["vacuum"] = 886

materialsTextures = [:]
materialsTextures[rid["siltstone"]] = [baseColorMap: "Textures/Tiles/Sedimentary/siltstone-1.png"]
materialsTextures[rid["shale"]] = [baseColorMap: "Textures/Tiles/Sedimentary/shale-1-1024.png"]
materialsTextures[rid["sandstone"]] = [baseColorMap: "Textures/Tiles/Sedimentary/sandstone-01.png"]
materialsTextures[rid["rock_salt"]] = [baseColorMap: "Textures/Tiles/Sedimentary/rock_salt-1-1024.png"]
materialsTextures[rid["mudstone"]] = [baseColorMap: "Textures/Tiles/Sedimentary/placeholder-1-1024.png"]
materialsTextures[rid["limestone"]] = [baseColorMap: "Textures/Tiles/Sedimentary/placeholder-1-1024.png"]
materialsTextures[rid["dolomite"]] = [baseColorMap: "Textures/Tiles/Sedimentary/placeholder-1-1024.png"]
materialsTextures[rid["conglomerate"]] = [baseColorMap: "Textures/Tiles/Sedimentary/conglomerate-1-1024.png"]
materialsTextures[rid["claystone"]] = [baseColorMap: "Textures/Tiles/Sedimentary/claystone-1-1024.png"]
materialsTextures[rid["chert"]] = [baseColorMap: "Textures/Tiles/Sedimentary/chert-1-1024.png"]
materialsTextures[rid["chalk"]] = [baseColorMap: "Textures/Tiles/Sedimentary/chalk-1-1024.png"]
// IgneousIntrusive
materialsTextures[rid["granite"]] = [baseColorMap: "Textures/Tiles/IgneousIntrusive/granite-2-1024.png"]
materialsTextures[rid["gabbro"]] = [baseColorMap: "Textures/Tiles/IgneousIntrusive/gabbro-1-1024.png"]
materialsTextures[rid["diorite"]] = [baseColorMap: "Textures/Tiles/IgneousIntrusive/diorite-1-1024.png"]
// IgneousExtrusive
materialsTextures[rid["rhyolite"]] = [baseColorMap: "Textures/Tiles/IgneousExtrusive/rhyolite-5-1024.png"]
materialsTextures[rid["obsidian"]] = [baseColorMap: "Textures/Tiles/IgneousExtrusive/obsidian-1-1024.png"]
materialsTextures[rid["dacite"]] = [baseColorMap: "Textures/Tiles/IgneousExtrusive/dacite-1-1024.png"]
materialsTextures[rid["basalt"]] = [baseColorMap: "Textures/Tiles/IgneousExtrusive/basalt-1-1024.png"]
materialsTextures[rid["andesite"]] = [baseColorMap: "Textures/Tiles/IgneousExtrusive/andesite-1-1024.png"]
// Metamorphic
materialsTextures[rid["slate"]] = [baseColorMap: "Textures/Tiles/Metamorphic/slate-1-1024.png"]
materialsTextures[rid["schist"]] = [baseColorMap: "Textures/Tiles/Metamorphic/schist-2-1024.png"]
materialsTextures[rid["quartzite"]] = [baseColorMap: "Textures/Tiles/Metamorphic/quartzite-2-1024.png"]
materialsTextures[rid["phyllite"]] = [baseColorMap: "Textures/Tiles/Metamorphic/phyllite-1-1024.png"]
materialsTextures[rid["marble"]] = [baseColorMap: "Textures/Tiles/Metamorphic/marble-1-1024.png"]
materialsTextures[rid["gneiss"]] = [baseColorMap: "Textures/Tiles/Metamorphic/gneiss-1-1024.png"]
// special
materialsTextures[rid["magma"]] = [baseColorMap: "Textures/Tiles/SpecialStoneLayer/magma-01.png"]
// soil
materialsTextures[rid["silt_loam"]] = [baseColorMap: "Textures/Tiles/Soil/silt_loam-1-1024.png"]
materialsTextures[rid["silty_clay_loam"]] = [baseColorMap: "Textures/Tiles/Soil/silty_clay_loam-1-1024.png"]
materialsTextures[rid["silt"]] = [baseColorMap: "Textures/Tiles/Soil/silt-1-1024.png"]
materialsTextures[rid["sandy_loam"]] = [baseColorMap: "Textures/Tiles/Soil/sandy_loam-1-1024.png"]
materialsTextures[rid["sandy_clay_loam"]] = [baseColorMap: "Textures/Tiles/Soil/sandy_clay_loam-1-1024.png"]
materialsTextures[rid["peat"]] = [baseColorMap: "Textures/Tiles/Soil/peat-1-1024.png"]
materialsTextures[rid["loamy_sand"]] = [baseColorMap: "Textures/Tiles/Soil/loamy_sand-1-1024.png"]
materialsTextures[rid["loam"]] = [baseColorMap: "Textures/Tiles/Soil/loam-3-1024.png"]
materialsTextures[rid["calcareous_ooze"]] = [baseColorMap: "Textures/Tiles/Soil/calcareous_ooze-1-1024.png"]
materialsTextures[rid["siliceous_ooze"]] = [baseColorMap: "Textures/Tiles/Soil/siliceous_ooze-1-1024.png"]
materialsTextures[rid["pelagic_clay"]] = [baseColorMap: "Textures/Tiles/Soil/pelagic_clay-1-1024.png"]
materialsTextures[rid["yellow_sand"]] = [baseColorMap: "Textures/Tiles/Soil/yellow_sand.png"]
materialsTextures[rid["white_sand"]] = [baseColorMap: "Textures/Tiles/Soil/white_sand.png"]
materialsTextures[rid["red_sand"]] = [baseColorMap: "Textures/Tiles/Soil/red_sand.png"]
materialsTextures[rid["black_sand"]] = [baseColorMap: "Textures/Tiles/Soil/black_sand.png"]
materialsTextures[rid["sand"]] = [baseColorMap: "Textures/Tiles/Soil/sand.png"]
materialsTextures[rid["fire_clay"]] = [baseColorMap: "Textures/Tiles/Soil/fire_clay-128.jpg"]
materialsTextures[rid["silty_clay"]] = [baseColorMap: "Textures/Tiles/Soil/silty_clay.jpg"]
materialsTextures[rid["sandy_clay"]] = [baseColorMap: "Textures/Tiles/Soil/sandy_clay.jpg"]
materialsTextures[rid["clay_loam"]] = [baseColorMap: "Textures/Tiles/Soil/clay_loam-128.jpg"]
materialsTextures[rid["clay"]] = [baseColorMap: "Textures/Tiles/Soil/clay-128.jpg"]
// gas
materialsTextures[rid["sulfur_dioxide"]] = [baseColorMap: "Textures/Tiles/Gas/sulfur_dioxide-1-1024.png", render: [baseColor: [0f, 0f, 0f, 0f], "glossiness": 0f, "metallic": 0f, "roughness": 0f]]
materialsTextures[rid["carbon_dioxide"]] = [baseColorMap: "Textures/Tiles/Gas/carbon_dioxide-1-1024.png", render: [baseColor: [0f, 0f, 0f, 0f], "glossiness": 0f, "metallic": 0f, "roughness": 0f]]
materialsTextures[rid["polluted_oxygen"]] = [baseColorMap: "Textures/Tiles/Gas/polluted_oxygen-1-1024.png", render: [baseColor: [0f, 0f, 0f, 0f], "glossiness": 0f, "metallic": 0f, "roughness": 0f]]
materialsTextures[rid["oxygen"]] = [baseColorMap: "Textures/Tiles/Gas/oxygen-01.png", render: [baseColor: [0f, 0f, 0f, 0f], "glossiness": 0f, "metallic": 0f, "roughness": 0f]]
materialsTextures[rid["vacuum"]] = [baseColorMap: "Textures/Tiles/Gas/vacuum-01.png", render: [baseColor: [0f, 0f, 0f, 0f], "glossiness": 0f, "metallic": 0f, "roughness": 0f]]

materialsTextures
