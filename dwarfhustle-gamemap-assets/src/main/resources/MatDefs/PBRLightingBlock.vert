#import "Common/ShaderLib/GLSLCompat.glsllib"
#import "Common/ShaderLib/Instancing.glsllib"
#import "Common/ShaderLib/Skinning.glsllib"
#import "Common/ShaderLib/MorphAnim.glsllib"

uniform vec4 m_BaseColor;
uniform vec4 g_AmbientLightColor;
varying vec2 texCoord;

#ifdef SEPARATE_TEXCOORD
  varying vec2 texCoord2;
  attribute vec2 inTexCoord2;
#endif

#if defined(SELECTED) || defined(SELECTEDMAP)
  varying vec2 texCoord3;
  attribute vec2 inTexCoord3;
#endif

#ifdef OBJECT_COLOR_MAP_1
  varying vec2 texCoord4;
  attribute vec2 inTexCoord4;
#endif

#ifdef OBJECT_COLOR_MAP_2
  varying vec2 texCoord5;
  attribute vec2 inTexCoord5;
#endif

#ifdef OBJECT_COLOR_MAP_3
  varying vec2 texCoord6;
  attribute vec2 inTexCoord6;
#endif

#ifdef OBJECT_COLOR_MAP_4
  varying vec2 texCoord7;
  attribute vec2 inTexCoord7;
#endif

#ifdef OBJECT_COLOR_MAP_5
  varying vec2 texCoord8;
  attribute vec2 inTexCoord8;
#endif

varying vec4 Color;

attribute vec3 inPosition;
attribute vec2 inTexCoord;
attribute vec3 inNormal;

#ifdef VERTEX_COLOR
  attribute vec4 inColor;
#endif

varying vec3 wNormal;
varying vec3 wPosition;
#if defined(NORMALMAP) || defined(PARALLAXMAP)
    attribute vec4 inTangent;
    varying vec4 wTangent;
#endif

void main(){
    vec4 modelSpacePos = vec4(inPosition, 1.0);
    vec3 modelSpaceNorm = inNormal;

    #if  ( defined(NORMALMAP) || defined(PARALLAXMAP)) && !defined(VERTEX_LIGHTING)
         vec3 modelSpaceTan  = inTangent.xyz;
    #endif

    #ifdef NUM_MORPH_TARGETS
         #if defined(NORMALMAP) && !defined(VERTEX_LIGHTING)
            Morph_Compute(modelSpacePos, modelSpaceNorm, modelSpaceTan);
         #else
            Morph_Compute(modelSpacePos, modelSpaceNorm);
         #endif
    #endif

    #ifdef NUM_BONES
         #if defined(NORMALMAP) && !defined(VERTEX_LIGHTING)
            Skinning_Compute(modelSpacePos, modelSpaceNorm, modelSpaceTan);
         #else
            Skinning_Compute(modelSpacePos, modelSpaceNorm);
         #endif
    #endif

    gl_Position = TransformWorldViewProjection(modelSpacePos);
    texCoord = inTexCoord;
    #ifdef SEPARATE_TEXCOORD
       texCoord2 = inTexCoord2;
    #endif
	#if defined(SELECTED) || defined(SELECTEDMAP)
       texCoord3 = inTexCoord3;
	#endif
	#ifdef OBJECT_COLOR_MAP_1
       texCoord4 = inTexCoord4;
	#endif
	#ifdef OBJECT_COLOR_MAP_2
       texCoord5 = inTexCoord5;
	#endif
	#ifdef OBJECT_COLOR_MAP_3
       texCoord6 = inTexCoord6;
	#endif
	#ifdef OBJECT_COLOR_MAP_4
       texCoord7 = inTexCoord7;
	#endif
	#ifdef OBJECT_COLOR_MAP_5
       texCoord8 = inTexCoord8;
	#endif

    wPosition = TransformWorld(modelSpacePos).xyz;
    wNormal  = TransformWorldNormal(modelSpaceNorm);

    #if defined(NORMALMAP) || defined(PARALLAXMAP)
       wTangent = vec4(TransformWorldNormal(modelSpaceTan),inTangent.w);
    #endif

    Color = m_BaseColor;
    
    #ifdef VERTEX_COLOR                    
       Color *= inColor;
    #endif
}
