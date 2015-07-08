/*
 * Copyright 2015 Julijonas Kikutis
 *
 * This file is part of Libre Explorer.
 *
 * Libre Explorer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Libre Explorer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Libre Explorer.  If not, see <http://www.gnu.org/licenses/>.
 */

package lt.kikutis.libreexplorer.connection.shell;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import lt.kikutis.libreexplorer.DeviceUtils;
import lt.kikutis.libreexplorer.R;
import lt.kikutis.libreexplorer.connection.File;

public class FileIconUtils {

    private static final String TAG = "FileIconUtils";

    private FileIconUtils() {
    }

    public static Drawable getIcon(File file, Context context) {
        int iconRes = file.isDirectory() ? matchDirectoryIconResource(file) : matchFileIconResource(file);
        return formatIcon(ContextCompat.getDrawable(context, iconRes), file, context);
    }

    public static Drawable formatIcon(Drawable icon, File file, Context context) {
        Drawable formattedIcon = addEmblemToIcon(icon, file, context);
        if (file.getName().charAt(0) == '.') {
            formattedIcon.mutate().setAlpha(context.getResources().getInteger(R.integer.hidden_file_alpha));
        }
        return formattedIcon;
    }

    private static Drawable addEmblemToIcon(Drawable icon, File file, Context context) {
        Drawable emblem = null;
        if (file.isLink()) {
            int emblemRes = file.isBrokenLink() ? R.drawable.ic_emblem_unreadable : R.drawable.ic_emblem_symbolic_link;
            emblem = ContextCompat.getDrawable(context, emblemRes);
        } else if (file.isDirectory()) {
            String parent = file.getParentPath();
            if (parent.equals(DeviceUtils.getInternalPath())
                    || parent.equals(DeviceUtils.getExternalPath() + "/Android/data")
                    || parent.equals(DeviceUtils.getExternalPath() + "/Android/obb")
                    || parent.equals(DeviceUtils.getSdCardPath() + "/Android/data")
                    || parent.equals(DeviceUtils.getSdCardPath() + "/Android/obb")) {
                try {
                    emblem = context.getPackageManager().getApplicationIcon(file.getName());
                } catch (PackageManager.NameNotFoundException e) {
                    Log.w(TAG, "addEmblem: Package not found: " + file.getName(), e);
                }
            }
        }

        if (emblem == null) {
            return icon;
        } else {
            LayerDrawable layers = new LayerDrawable(new Drawable[]{icon, emblem});
            int insetPixels = context.getResources().getDimensionPixelSize(R.dimen.file_emblem_inset);
            layers.setLayerInset(1, insetPixels, insetPixels, 0, 0);
            return layers;
        }
    }

    private static int matchDirectoryIconResource(File file) {
        String path = file.getPath();
        String parent = file.getParentPath();
        String name = file.getName();

        if (path.equals(DeviceUtils.getExternalPath())
                || path.equals(DeviceUtils.getSdCardPath())) {
            return R.drawable.ic_folder_publicshare;
        } else if (path.equals(DeviceUtils.getSystemPath()) || path.equals(DeviceUtils.getInternalPath())) {
            return R.drawable.ic_folder_system;
        } else if (parent.equals(DeviceUtils.getExternalPath())
                || parent.equals(DeviceUtils.getSdCardPath())) {
            switch (name) {
                case "Documents":
                    return R.drawable.ic_folder_documents;
                case "Downloads":
                case "Download":
                    return R.drawable.ic_folder_download;
                case "Music":
                case "Playlists":
                case "Podcasts":
                case "Ringtones":
                case "Sounds":
                    return R.drawable.ic_folder_music;
                case "Pictures":
                case "DCIM":
                    return R.drawable.ic_folder_pictures;
                case "Videos":
                case "Movies":
                    return R.drawable.ic_folder_video;
                case "Android":
                    return R.drawable.ic_folder_system;
            }
        }
        return R.drawable.ic_folder;
    }

    private static int matchFileIconResource(File file) {
        if (file.getSize() == 0) {
            return R.drawable.ic_application_blank;
        }

        switch (file.getName().toLowerCase()) {
            case "makefile":
                return R.drawable.ic_gnome_mime_text_x_makefile;
            case "changes":
            case "changelog":
                return R.drawable.ic_text_x_changelog;
            case "copying":
            case "license":
            case "license.txt":
                return R.drawable.ic_text_x_copying;
            case "install":
            case "install.txt":
                return R.drawable.ic_text_x_install;
            case "readme":
            case "readme.txt":
                return R.drawable.ic_text_x_readme;
            default:
                return matchFileIconResourceByExtension(file);
        }
    }

    private static int matchFileIconResourceByExtension(File file) {
        if (!file.hasExtension()) {
            if (file.isExecutable()) {
                return R.drawable.ic_application_executable;
            } else {
                return R.drawable.ic_application_text;
            }
        }

        switch (file.getExtension()) {

            /* Archives */
            case "bz2":
                return R.drawable.ic_application_x_bzip;
            case "tar.bz2":
            case "tbz":
                return R.drawable.ic_application_x_bzip_compressed_tar;
            case "gz":
                return R.drawable.ic_application_x_gzip;
            case "tar.gz":
            case "tgz":
                return R.drawable.ic_application_x_compressed_tar;
            case "tar":
                return R.drawable.ic_application_x_tar;
            case "xz":
                return R.drawable.ic_application_archive; /* generic icon */
            case "lzma":
                return R.drawable.ic_application_x_lzma;
            case "tar.lzma":
            case "tlz":
                return R.drawable.ic_application_x_lzma_compressed_tar;
            case "lzop":
                return R.drawable.ic_application_x_lzop;
            case "7z":
                return R.drawable.ic_application_x_7z_compressed;
            case "zip":
                return R.drawable.ic_application_archive_zip;
            case "cab":
                return R.drawable.ic_application_vnd_ms_cab_compressed;
            case "rar":
                return R.drawable.ic_application_x_rar;
            case "ace":
                return R.drawable.ic_application_x_ace;
            case "arj":
                return R.drawable.ic_application_x_arj;
            case "cpio":
                return R.drawable.ic_application_x_cpio;
            case "lha":
                return R.drawable.ic_application_x_lha;
            case "lhz":
                return R.drawable.ic_application_x_lhz;
            case "shar":
                return R.drawable.ic_application_x_shar;
            case "sitx":
                return R.drawable.ic_application_x_stuffit;
            case "tha":
                return R.drawable.ic_application_x_tha;
            case "thz":
                return R.drawable.ic_application_x_thz;
            case "zoo":
                return R.drawable.ic_application_x_zoo;
            case "deb":
                return R.drawable.ic_application_x_deb;
            case "jar":
                return R.drawable.ic_application_x_jar;
            case "rpm":
                return R.drawable.ic_application_x_rpm;
            case "apk":
                return R.drawable.ic_application_software;

            /* Audio */
            case "aiff":
            case "aac":
            case "amr":
            case "flac":
            case "m4a":
            case "mp3":
            case "ogg":
            case "oga":
            case "opus":
            case "wav":
            case "wma":
            case "wv":
                return R.drawable.ic_application_audio;
            case "m3u":
            case "wpl":
                return R.drawable.ic_application_audio_playlist;

            /* Images */
            case "bmp":
                return R.drawable.ic_application_image_bmp;
            case "gif":
                return R.drawable.ic_application_image_gif;
            case "ico":
                return R.drawable.ic_application_image_ico;
            case "jpg":
            case "jpeg":
                return R.drawable.ic_application_image_jpg;
            case "png":
                return R.drawable.ic_application_image_png;
            case "psd":
                return R.drawable.ic_application_image_psd;
            case "tga":
                return R.drawable.ic_application_image_tga;
            case "tiff":
            case "tif":
                return R.drawable.ic_application_image_tiff;
            case "webp":
                return R.drawable.ic_application_images; /* generic icon */
            case "ps":
                return R.drawable.ic_application_postscript;
            case "svg":
                return R.drawable.ic_application_vector;
            case "eps":
            case "ai":
                return R.drawable.ic_application_drawing;
            case "xcf":
                return R.drawable.ic_image_x_xcf;
            case "xcfbz2":
            case "xcf.bz2":
            case "xcfgz":
            case "xcf.gz":
                return R.drawable.ic_image_x_compressed_xcf;
            case "odg":
            case "odi":
                return R.drawable.ic_application_vnd_oasis_opendocument_drawing;
            case "otg":
                return R.drawable.ic_application_vnd_oasis_opendocument_drawing_template;
            case "oti":
                return R.drawable.ic_application_vnd_oasis_opendocument_image_template;

            /* Video */
            case "mkv":
            case "avi":
            case "webm":
            case "flv":
            case "ogv":
            case "mov":
            case "asf":
            case "mp4":
            case "m4v":
            case "mpg":
            case "mpeg":
            case "3gp":
                return R.drawable.ic_application_video;

            /* Documents */
            case "odt":
            case "fodt":
                return R.drawable.ic_application_vnd_oasis_opendocument_text;
            case "ott":
                return R.drawable.ic_application_vnd_oasis_opendocument_text_template;
            case "odm":
                return R.drawable.ic_application_vnd_oasis_opendocument_master_document;
            case "odh":
                return R.drawable.ic_application_vnd_oasis_opendocument_text_web;
            case "oth":
                return R.drawable.ic_application_vnd_oasis_opendocument_text_web_template;
            case "doc":
                return R.drawable.ic_wps_office_doc;
            case "dot":
                return R.drawable.ic_wps_office_dot;
            case "docx":
            case "docm":
            case "docb":
                return R.drawable.ic_application_word; /* generic icon */
            case "dotx":
            case "dotm":
                return R.drawable.ic_application_word_template; /* generic icon */
            case "rtf":
                return R.drawable.ic_application_rtf;
            case "odf":
                return R.drawable.ic_application_vnd_oasis_opendocument_formula;
            case "otf":
                return R.drawable.ic_application_vnd_oasis_opendocument_formula_template;
            case "wps":
                return R.drawable.ic_wps_office_wps;
            case "wpt":
                return R.drawable.ic_wps_office_wpt;

            /* Presentations */
            case "odp":
            case "fodp":
                return R.drawable.ic_application_vnd_oasis_opendocument_presentation;
            case "otp":
                return R.drawable.ic_application_vnd_oasis_opendocument_presentation_template;
            case "ppt":
            case "pps":
                return R.drawable.ic_wps_office_ppt;
            case "pot":
                return R.drawable.ic_wps_office_pot;
            case "pptx":
            case "pptm":
            case "ppsx":
            case "ppsm":
                return R.drawable.ic_application_presentation; /* generic icon */
            case "potx":
            case "potm":
                return R.drawable.ic_application_presentation_template; /* generic icon */
            case "mgp":
                return R.drawable.ic_gnome_mime_application_magicpoint;
            case "dps":
                return R.drawable.ic_wps_office_dps;
            case "dpt":
                return R.drawable.ic_wps_office_dpt;

            /* Spreadsheets */
            case "fods":
            case "ods":
                return R.drawable.ic_application_vnd_oasis_opendocument_spreadsheet;
            case "ots":
                return R.drawable.ic_application_vnd_oasis_opendocument_spreadsheet_template;
            case "xls":
            case "xlm":
                return R.drawable.ic_wps_office_xls;
            case "xlt":
                return R.drawable.ic_wps_office_xlt;
            case "xlsx":
            case "xlsm":
            case "xlsb":
                return R.drawable.ic_application_table; /* generic icon */
            case "xltx":
            case "xltm":
                return R.drawable.ic_application_table_template; /* generic icon */
            case "odc":
                return R.drawable.ic_application_vnd_oasis_opendocument_chart;
            case "otc":
                return R.drawable.ic_application_vnd_oasis_opendocument_chart_template;
            case "et":
                return R.drawable.ic_wps_office_et;
            case "ett":
                return R.drawable.ic_wps_office_ett;

            /* Databases */
            case "sqlite":
            case "db":
            case "mdb":
                return R.drawable.ic_application_database; /* generic icon */
            case "accdb":
                return R.drawable.ic_application_vnd_ms_access;
            case "odb":
                return R.drawable.ic_application_vnd_oasis_opendocument_database;
            case "sql":
                return R.drawable.ic_text_x_sql;

            /* Programming */
            case "java":
                return R.drawable.ic_application_java;
            case "js":
                return R.drawable.ic_application_javascript;
            case "php":
                return R.drawable.ic_application_x_php;
            case "html":
            case "htm":
                return R.drawable.ic_text_html;
            case "xml":
                return R.drawable.ic_text_xml;
            case "c":
                return R.drawable.ic_text_x_c;
            case "cpp":
                return R.drawable.ic_text_x_c__;
            case "cs":
                return R.drawable.ic_text_x_csharp;
            case "css":
                return R.drawable.ic_text_x_css;
            case "gtkrc":
            case "gtkrc-2.0":
                return R.drawable.ic_text_x_gtkrc;
            case "py":
                return R.drawable.ic_text_x_python;
            case "rb":
                return R.drawable.ic_text_x_ruby;
            case "sh":
            case "pl":
            case "rc":
                return R.drawable.ic_text_x_script; /* generic icon */
            case "blend":
                return R.drawable.ic_application_x_blender;
            case "glade":
                return R.drawable.ic_application_x_glade;
            case "vbproj":
            case "csproj":
            case "vcxproj":
            case "fsproj":
            case "sln":
                return R.drawable.ic_application_x_mono_develop;
            case "oxt":
                return R.drawable.ic_libreoffice_extension;
            case "crx":
            case "xpi":
                return R.drawable.ic_extension;
            case "theme":
                return R.drawable.ic_application_x_theme;
            case "prop":
                return R.drawable.ic_application_script_blank;

            /* Misc */
            case "txt":
                return R.drawable.ic_application_text;
            case "md":
                return R.drawable.ic_application_document;
            case "nfo":
                return R.drawable.ic_application_info;
            case "pdf":
                return R.drawable.ic_application_pdf;
            case "epub":
                return R.drawable.ic_application_epub_zip;
            case "crt":
            case "cer":
            case "pem":
            case "pfx":
            case "pvk":
            case "key":
                return R.drawable.ic_application_certificate;
            case "fla":
            case "swf":
                return R.drawable.ic_application_flash;
            case "ttf":
            case "pfb":
            case "woff":
                return R.drawable.ic_application_font;
            case "rss":
                return R.drawable.ic_application_rss;
            case "torrent":
                return R.drawable.ic_application_torrent;
            case "iso":
                return R.drawable.ic_application_x_cd_image;
            case "desktop":
                return R.drawable.ic_application_x_desktop;
            case "exe":
            case "dll":
            case "sys":
            case "cpl":
            case "ocx":
            case "scr":
            case "drv":
            case "efi":
            case "fon":
                return R.drawable.ic_application_x_ms_dos_executable;
            case "vcf":
            case "vcard":
                return R.drawable.ic_x_office_address_book;
            case "ical":
            case "ics":
                return R.drawable.ic_x_office_calendar;

            default:
                return R.drawable.ic_application_octet_stream;
        }
    }
}
