#!/usr/bin/env python3
#
# Copyright 2015 Julijonas Kikutis
#
# This file is part of Libre Explorer.
#
# Libre Explorer is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# Libre Explorer is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with Libre Explorer.  If not, see <http://www.gnu.org/licenses/>.

import pathlib
import re
import subprocess
import os


def add_icons():
    """Retrieve icons referenced in source code from icon directories"""
    dest_dir = "app/src/main/res/drawable-xxhdpi"
    source_prefix = "/usr/share/icons/Numix/"
    source_dirs = {
        "256x256/places":    144,
        "256x256/mimetypes": 144,
        "256x256/emblems":   72,
        "scalable/devices":  72,
        "scalable/actions":  72,
    }

    filter_re = re.compile(r"[^a-zA-Z0-9]")
    os.makedirs(dest_dir, exist_ok=True)
    names = resource_names()
    for source_dir, size in source_dirs.items():
        for icon_file in pathlib.Path(source_prefix, source_dir).glob("*.svg"):
            name = filter_re.sub("_", icon_file.stem)
            if name in names:
                dest = "{}/ic_{}.png".format(dest_dir, name)
                convert_svg(str(icon_file), dest, size)
                names.remove(name)

    print(">>> Finished")
    if names:
        print(">>> Could not find {} icons:".format(len(names)))
        print("\n".join(names))


def resource_names():
    """Retrieve drawable resource names from source code"""
    scan_dir = "app/src/main/java"

    resource_re = re.compile(r"R.drawable.ic_([a-zA-X0-9_]+)")
    names = set()
    for java_file in pathlib.Path(scan_dir).glob("**/*.java"):
        with java_file.open() as handle:
            for match in resource_re.finditer(handle.read()):
                names.add(match.group(1))

    print(">>> Found {} R.drawable expressions".format(len(names)))
    return names


def convert_svg(source, dest, size):
    """Call Inkscape to convert SVG to PNG"""
    subprocess.call(["inkscape", "-e", dest, "-w", str(size), "-h", str(size),
                     source])


if __name__ == "__main__":
    add_icons()
