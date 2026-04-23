#!/usr/bin/env python3
"""
Generate Android launcher icons from a URL.
Usage: python3 generate_icons.py
"""
import urllib.request
import os
import shutil

ICON_URL = "https://res.cloudinary.com/ddgebw6ej/image/upload/f_auto,q_auto/v1776941029/chrqrtitxrx0xbpdpucr.png"

SIZES = {
    "mipmap-mdpi":    48,
    "mipmap-hdpi":    72,
    "mipmap-xhdpi":   96,
    "mipmap-xxhdpi":  144,
    "mipmap-xxxhdpi": 192,
}

BASE = "app/src/main/res"

def main():
    try:
        from PIL import Image
        import io
    except ImportError:
        print("Installing Pillow...")
        os.system("pip install Pillow --break-system-packages -q")
        from PIL import Image
        import io

    print(f"Downloading icon from {ICON_URL}...")
    with urllib.request.urlopen(ICON_URL) as response:
        img_data = response.read()

    img = Image.open(io.BytesIO(img_data)).convert("RGBA")
    print(f"Original size: {img.size}")

    for folder, size in SIZES.items():
        out_dir = os.path.join(BASE, folder)
        os.makedirs(out_dir, exist_ok=True)

        resized = img.resize((size, size), Image.LANCZOS)
        resized.save(os.path.join(out_dir, "ic_launcher.png"), "PNG")

        # Round icon (circle crop)
        from PIL import ImageDraw
        round_img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
        mask = Image.new("L", (size, size), 0)
        draw = ImageDraw.Draw(mask)
        draw.ellipse((0, 0, size, size), fill=255)
        round_img.paste(resized, (0, 0), mask)
        round_img.save(os.path.join(out_dir, "ic_launcher_round.png"), "PNG")

        print(f"  ✅ {folder}: {size}x{size}px")

    print("\n✅ All icons generated successfully!")

if __name__ == "__main__":
    main()
