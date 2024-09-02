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

// BlockObject
rid["BLOCK-RAMP-TWO-SE"] = 990
rid["BLOCK-RAMP-TWO-NE"] = 989
rid["BLOCK-RAMP-TRI-S"] = 988
rid["BLOCK-RAMP-TRI-W"] = 987
rid["BLOCK-RAMP-TRI-E"] = 986
rid["BLOCK-RAMP-TRI-N"] = 985
rid["BLOCK-RAMP-SINGLE"] = 984
rid["BLOCK-RAMP-PERP-W"] = 983
rid["BLOCK-RAMP-PERP-S"] = 982
rid["BLOCK-RAMP-PERP-E"] = 981
rid["BLOCK-RAMP-PERP-N"] = 980
rid["BLOCK-RAMP-EDGE-OUT-SW"] = 979
rid["BLOCK-RAMP-EDGE-OUT-SE"] = 978
rid["BLOCK-RAMP-EDGE-OUT-NW"] = 977
rid["BLOCK-RAMP-EDGE-OUT-NE"] = 976
rid["BLOCK-RAMP-EDGE-IN-SW"] = 975
rid["BLOCK-RAMP-EDGE-IN-SE"] = 974
rid["BLOCK-RAMP-EDGE-IN-NW"] = 973
rid["BLOCK-RAMP-EDGE-IN-NE"] = 972
rid["BLOCK-RAMP-CORNER-SW"] = 971
rid["BLOCK-RAMP-CORNER-SE"] = 970
rid["BLOCK-RAMP-CORNER-NW"] = 969
rid["BLOCK-RAMP-CORNER-NE"] = 968
rid["BLOCK-CEILING"] = 967
rid["BLOCK-WATER"] = 966
rid["BLOCK-NORMAL"] = 965

m = [:]

m[rid["BLOCK-RAMP-TWO-SE"]] = 0xfff6
m[rid["BLOCK-RAMP-TWO-NE"]] = 0xfff6
m[rid["BLOCK-RAMP-TRI-S"]] = 0xfff7
m[rid["BLOCK-RAMP-TRI-W"]] = 0xfff7
m[rid["BLOCK-RAMP-TRI-E"]] = 0xfff7
m[rid["BLOCK-RAMP-TRI-N"]] = 0xfff7
m[rid["BLOCK-RAMP-SINGLE"]] = 0xfff8
m[rid["BLOCK-RAMP-PERP-W"]] = 0xfff9
m[rid["BLOCK-RAMP-PERP-S"]] = 0xfff9
m[rid["BLOCK-RAMP-PERP-E"]] = 0xfff9
m[rid["BLOCK-RAMP-PERP-N"]] = 0xfff9
m[rid["BLOCK-RAMP-EDGE-OUT-SW"]] = 0xfffa
m[rid["BLOCK-RAMP-EDGE-OUT-SE"]] = 0xfffa
m[rid["BLOCK-RAMP-EDGE-OUT-NW"]] = 0xfffa
m[rid["BLOCK-RAMP-EDGE-OUT-NE"]] = 0xfffa
m[rid["BLOCK-RAMP-EDGE-IN-SW"]] = 0xfffb
m[rid["BLOCK-RAMP-EDGE-IN-SE"]] = 0xfffb
m[rid["BLOCK-RAMP-EDGE-IN-NW"]] = 0xfffb
m[rid["BLOCK-RAMP-EDGE-IN-NE"]] = 0xfffb
m[rid["BLOCK-RAMP-CORNER-SW"]] = 0xfffc
m[rid["BLOCK-RAMP-CORNER-SE"]] = 0xfffc
m[rid["BLOCK-RAMP-CORNER-NW"]] = 0xfffc
m[rid["BLOCK-RAMP-CORNER-NE"]] = 0xfffc
m[rid["BLOCK-NORMAL"]] = 0xfffd

m
