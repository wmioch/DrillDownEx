#ifdef GL_ES
	#define LOWP lowp
	precision mediump float;
#else
	#define LOWP
#endif

varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform vec4 u_tintColor;

/**
 * TINTING SYSTEM DOCUMENTATION
 * =============================
 * 
 * This shader supports dynamic tinting for structures via the u_tintColor uniform.
 * 
 * BLEND MODE: Overlay Blend
 * - Preserves brightness and contrast of original colors
 * - Formula: mix(original, original * tint * 2.0, tint.alpha)
 * - Bright areas stay bright, dark areas stay dark
 * - The tint color shifts the hue without washing out or darkening
 * 
 * CONDITIONAL CHECK:
 * - Only applies tint if it's not the default white (1,1,1,1)
 * - Checks if any channel is < 0.9 to detect non-white tints
 * - Prevents subtle tinting when tint is reset to white
 * 
 * HOW TO ADD NEW TINTS:
 * 1. Define a new RenderingStyle constant in RenderingStyle.java
 * 2. Set appropriate RGBA values for your tint color
 * 3. Implement ISpecialRenderer on your structure
 * 4. Return your new RenderingStyle from getRenderingStyle()
 * 5. No shader changes needed!
 * 
 * TINT COLOR RECOMMENDATIONS:
 * - Blue tint: (0.4, 0.6, 1.0, 0.8) - Good for Item Elevators
 * - Red tint: (1.0, 0.4, 0.4, 0.8) - For danger/warning structures
 * - Green tint: (0.4, 1.0, 0.6, 0.8) - For eco/organic structures
 * - Alpha range: 0.5-0.9 (lower = subtle, higher = strong)
 */

void main() {
	vec4 col = texture2D(u_texture, v_texCoords);
	if(col.a == 0.0) 
		discard;
	
	// Apply tint only if it's not the default white
	// This prevents tint from being applied when we explicitly reset to white
	if(u_tintColor.r < 0.9 || u_tintColor.g < 0.9 || u_tintColor.b < 0.9) {
		// Overlay blend: preserves brightness and contrast
		// mix(original, original * tint * 2.0, tint.alpha)
		col.rgb = mix(col.rgb, col.rgb * u_tintColor.rgb * 2.0, u_tintColor.a);
	}
	
	gl_FragColor = col;
}