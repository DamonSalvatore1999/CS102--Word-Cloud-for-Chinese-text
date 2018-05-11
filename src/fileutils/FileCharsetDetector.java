package fileutils;/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is mozilla.org code.
 *
 * The Initial Developer of the Original Code is
 * Netscape Communications Corporation.
 * Portions created by the Initial Developer are Copyright (C) 1998
 * the Initial Developer. All Rights Reserved.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either of the GNU General Public License Version 2 or later (the "GPL"),
 * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */

/*
 * DO NOT EDIT THIS DOCUMENT MANUALLY !!!
 * THIS FILE IS AUTOMATICALLY GENERATED BY THE TOOLS UNDER
 *    AutoDetect/tools/
 */

// package org.mozilla.intl.chardet
// needs charset.jar

import org.mozilla.intl.chardet.nsDetector;
import org.mozilla.intl.chardet.nsICharsetDetectionObserver;
import org.mozilla.intl.chardet.nsPSMDetector;

import java.io.*;
import java.net.URL;
import java.util.Scanner;

public class FileCharsetDetector {

    public static String[] getProbableCharset(String file) {
        charSetDetect(file);
        return mProb;
    }

    private static boolean mFound = false;
    private static String[] mProb;

    public static void main(String[] args) throws Exception {

        if (args.length < 1) {
            System.out.println(
                    "Usage: FileCharsetDetector <path> ");
            return;
        }

        charSetDetect(args[0]);
    }

    private static void charSetDetect(String path) {

        // Initalize the nsDetector() ;
        int lang = nsPSMDetector.ALL;
        nsDetector det = new nsDetector(lang);

        // Set an observer...
        // The Notify() will be called when a matching charset is mFound.

        det.Init(new nsICharsetDetectionObserver() {
            public void Notify(String charset) {
                FileCharsetDetector.mFound = true;
                FileCharsetDetector.mProb = new String[]{charset};
                System.out.println("CHARSET = " + charset);
            }
        });

        URL url = null;
        BufferedInputStream buffInStream = null;
        try {
            url = new File(path).toURI().toURL();
            buffInStream = new BufferedInputStream(url.openStream());
            byte[] buf = new byte[1024];
            int len;
            boolean done = false;
            boolean isAscii = true;

            while ((len = buffInStream.read(buf, 0, buf.length)) != -1) {



                // Check if the stream is only ascii.
                if (isAscii)
                    isAscii = det.isAscii(buf, len);

                // DoIt if non-ascii and not done yet.
                if (!isAscii && !done)
                    done = det.DoIt(buf, len, false);
            }
            det.DataEnd();
            buffInStream.close();

            if (isAscii) {
                System.out.print("CHARSET = ASCII");
                tryToOutput(path, "ASCII");
                mFound = true;
            }

            if (!mFound) {
                mProb = det.getProbableCharsets();
                for (int i = 0; i < mProb.length; i++) {
                    System.out.print("Probable Charset = " + mProb[i]);
                    tryToOutput(path, mProb[i]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void tryToOutput(String path, String charSetName) {
        InputStream inputStream = null;
        BufferedReader in = null;
        Scanner ins = null;
        try {
            inputStream = new FileInputStream(path);
            in = new BufferedReader(new InputStreamReader(inputStream, charSetName));
            ins = new Scanner(in);
            String s = ins.next();
            System.out.println(" : " + s);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ins.close();
        }
    }
}
