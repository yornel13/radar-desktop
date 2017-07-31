/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Yornel
 */
public class Password {
    
    public static String MD5(String md5) {
        try {
             MessageDigest md = MessageDigest.getInstance("MD5");
             byte[] array = md.digest(md5.getBytes());
             StringBuffer sb = new StringBuffer();
             for (int i = 0; i < array.length; ++i) {
               sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
             return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // nothing to do
        }
        return null;
    }

    public static boolean isValidMD5(String s) {
        return s.matches("[a-fA-F0-9]{32}");
    }

}
