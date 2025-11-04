#!/usr/bin/env python3
"""
Script to mark all used texture regions in tex.png with black rectangles.
This will show which areas of the texture atlas are actually unused.
"""

from PIL import Image, ImageDraw
import re

def parse_atlas_file(atlas_path):
    """Parse the LibGDX atlas file and extract all texture regions."""
    regions = []
    
    with open(atlas_path, 'r', encoding='utf-8') as f:
        lines = f.readlines()
    
    i = 0
    while i < len(lines):
        line = lines[i].strip()
        
        # Skip empty lines and the header info
        if not line or line.startswith('size:') or line.startswith('format:') or line.startswith('filter:') or line.startswith('repeat:') or line.endswith('.png'):
            i += 1
            continue
        
        # This should be a texture region name
        region_name = line
        
        # Read the region properties
        region_data = {}
        i += 1
        
        # Read properties until we hit the next region or end of file
        while i < len(lines):
            prop_line = lines[i].strip()
            
            # Check if this is a new region (doesn't start with whitespace in original)
            if prop_line and not lines[i].startswith('  '):
                break
                
            if not prop_line:
                i += 1
                continue
            
            # Parse property
            if ':' in prop_line:
                key, value = prop_line.split(':', 1)
                region_data[key.strip()] = value.strip()
            
            i += 1
        
        # Extract coordinates and size
        if 'xy' in region_data and 'size' in region_data:
            xy = region_data['xy'].split(',')
            size = region_data['size'].split(',')
            
            if len(xy) == 2 and len(size) == 2:
                x = int(xy[0].strip())
                y = int(xy[1].strip())
                width = int(size[0].strip())
                height = int(size[1].strip())
                
                rotate = region_data.get('rotate', 'false').lower() == 'true'
                
                # If rotated, swap width and height for drawing
                if rotate:
                    width, height = height, width
                
                regions.append({
                    'name': region_name,
                    'x': x,
                    'y': y,
                    'width': width,
                    'height': height,
                    'rotate': rotate
                })
    
    return regions

def mark_used_regions(input_png, output_png, atlas_path):
    """Load the texture, mark all used regions with black, and save."""
    print(f"Loading image: {input_png}")
    img = Image.open(input_png)
    
    # Create a copy
    marked_img = img.copy()
    draw = ImageDraw.Draw(marked_img)
    
    print(f"Parsing atlas: {atlas_path}")
    regions = parse_atlas_file(atlas_path)
    
    print(f"Found {len(regions)} texture regions")
    
    # Draw black rectangles over all used regions
    for region in regions:
        x = region['x']
        y = region['y']
        width = region['width']
        height = region['height']
        
        # Draw a filled black rectangle
        draw.rectangle([x, y, x + width, y + height], fill='black')
    
    print(f"Saving marked image: {output_png}")
    marked_img.save(output_png)
    print(f"Done! Unused areas are now visible in {output_png}")
    
    # Print some statistics
    total_pixels = img.width * img.height
    used_pixels = sum(r['width'] * r['height'] for r in regions)
    unused_pixels = total_pixels - used_pixels
    
    print(f"\nStatistics:")
    print(f"  Total atlas size: {img.width}x{img.height} = {total_pixels:,} pixels")
    print(f"  Used pixels: {used_pixels:,} ({used_pixels/total_pixels*100:.1f}%)")
    print(f"  Unused pixels: {unused_pixels:,} ({unused_pixels/total_pixels*100:.1f}%)")

if __name__ == '__main__':
    import sys
    
    # File paths
    input_png = 'android/assets/tex.png'
    atlas_file = 'android/assets/tex.atlas'
    output_png = 'android/assets/tex_used_marked.png'
    
    mark_used_regions(input_png, output_png, atlas_file)

