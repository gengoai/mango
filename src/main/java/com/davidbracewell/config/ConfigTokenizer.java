/* The following code was generated by JFlex 1.6.1 */

/*
 * Copyright 2005 Illuminating Knowledge
 * NOTICE:  All information contained herein is, and remains the property
 * of Illuminating Knowledge and its suppliers, if any.  The intellectual
 * and technical concepts contained herein are proprietary to Illuminating
 * Knowledge and its suppliers and may be covered by U.S. and Foreign
 * Patents, patents in process, and are protected by trade secret or
 * copyright law. Dissemination of this information or reproduction of this
 * material is strictly forbidden unless prior written permission is
 * obtained from Illuminating Knowledge.
 */

package com.davidbracewell.config;

import com.davidbracewell.parsing.ParserToken;
import com.davidbracewell.parsing.ParserTokenType;

import java.util.Stack;


/**
 * This class is a scanner generated by 
 * <a href="http://www.jflex.de/">JFlex</a> 1.6.1
 * from the specification file <tt>/home/david/prj/mango/src/main/jflex/ConfigTokenizer.jflex</tt>
 */
public class ConfigTokenizer {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int YYINITIAL = 0;
  public static final int VALUE = 2;
  public static final int SECTION_PROPERTY = 4;
  public static final int SECTION_VALUE = 6;
  public static final int IMPORT = 8;
  public static final int SCRIPT = 10;
  public static final int APPENDER = 12;
  public static final int START_SECTION = 14;
  public static final int START_VALUE = 16;
  public static final int START_SECTION_VALUE = 18;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = { 
     0,  0,  1,  1,  2,  2,  3,  3,  4,  4,  4,  4,  5,  5,  6,  6, 
     7,  7,  8, 8
  };

  /** 
   * Translates characters to character classes
   */
  private static final String ZZ_CMAP_PACKED = 
    "\11\0\1\1\1\13\1\15\1\15\1\14\22\0\1\1\2\0\1\21"+
    "\1\17\6\0\1\6\2\0\1\4\1\0\12\2\1\5\2\0\1\7"+
    "\2\0\1\20\32\2\1\0\1\12\2\0\1\3\1\0\2\2\1\31"+
    "\5\2\1\22\3\2\1\23\1\2\1\25\1\24\1\2\1\26\1\30"+
    "\1\27\6\2\1\10\1\0\1\11\7\0\1\15\32\0\1\1\11\0"+
    "\1\2\12\0\1\2\4\0\1\2\5\0\27\2\1\0\37\2\1\0"+
    "\u01ca\2\4\0\14\2\16\0\5\2\7\0\1\2\1\0\1\2\201\0"+
    "\5\2\1\0\2\2\2\0\4\2\1\0\1\2\6\0\1\2\1\0"+
    "\3\2\1\0\1\2\1\0\24\2\1\0\123\2\1\0\213\2\10\0"+
    "\246\2\1\0\46\2\2\0\1\2\7\0\47\2\110\0\33\2\5\0"+
    "\3\2\55\0\53\2\25\0\12\2\4\0\2\2\1\0\143\2\1\0"+
    "\1\2\17\0\2\2\7\0\2\2\12\2\3\2\2\0\1\2\20\0"+
    "\1\2\1\0\36\2\35\0\131\2\13\0\1\2\16\0\12\2\41\2"+
    "\11\0\2\2\4\0\1\2\5\0\26\2\4\0\1\2\11\0\1\2"+
    "\3\0\1\2\27\0\31\2\107\0\23\2\121\0\66\2\3\0\1\2"+
    "\22\0\1\2\7\0\12\2\4\0\12\2\1\0\20\2\4\0\10\2"+
    "\2\0\2\2\2\0\26\2\1\0\7\2\1\0\1\2\3\0\4\2"+
    "\3\0\1\2\20\0\1\2\15\0\2\2\1\0\3\2\4\0\12\2"+
    "\2\2\23\0\6\2\4\0\2\2\2\0\26\2\1\0\7\2\1\0"+
    "\2\2\1\0\2\2\1\0\2\2\37\0\4\2\1\0\1\2\7\0"+
    "\12\2\2\0\3\2\20\0\11\2\1\0\3\2\1\0\26\2\1\0"+
    "\7\2\1\0\2\2\1\0\5\2\3\0\1\2\22\0\1\2\17\0"+
    "\2\2\4\0\12\2\25\0\10\2\2\0\2\2\2\0\26\2\1\0"+
    "\7\2\1\0\2\2\1\0\5\2\3\0\1\2\36\0\2\2\1\0"+
    "\3\2\4\0\12\2\1\0\1\2\21\0\1\2\1\0\6\2\3\0"+
    "\3\2\1\0\4\2\3\0\2\2\1\0\1\2\1\0\2\2\3\0"+
    "\2\2\3\0\3\2\3\0\14\2\26\0\1\2\25\0\12\2\25\0"+
    "\10\2\1\0\3\2\1\0\27\2\1\0\20\2\3\0\1\2\32\0"+
    "\2\2\6\0\2\2\4\0\12\2\25\0\10\2\1\0\3\2\1\0"+
    "\27\2\1\0\12\2\1\0\5\2\3\0\1\2\40\0\1\2\1\0"+
    "\2\2\4\0\12\2\1\0\2\2\22\0\10\2\1\0\3\2\1\0"+
    "\51\2\2\0\1\2\20\0\1\2\21\0\2\2\4\0\12\2\12\0"+
    "\6\2\5\0\22\2\3\0\30\2\1\0\11\2\1\0\1\2\2\0"+
    "\7\2\37\0\12\2\21\0\60\2\1\0\2\2\14\0\7\2\11\0"+
    "\12\2\47\0\2\2\1\0\1\2\2\0\2\2\1\0\1\2\2\0"+
    "\1\2\6\0\4\2\1\0\7\2\1\0\3\2\1\0\1\2\1\0"+
    "\1\2\2\0\2\2\1\0\4\2\1\0\2\2\11\0\1\2\2\0"+
    "\5\2\1\0\1\2\11\0\12\2\2\0\4\2\40\0\1\2\37\0"+
    "\12\2\26\0\10\2\1\0\44\2\33\0\5\2\163\0\53\2\24\0"+
    "\1\2\12\2\6\0\6\2\4\0\4\2\3\0\1\2\3\0\2\2"+
    "\7\0\3\2\4\0\15\2\14\0\1\2\1\0\12\2\6\0\46\2"+
    "\1\0\1\2\5\0\1\2\2\0\53\2\1\0\u014d\2\1\0\4\2"+
    "\2\0\7\2\1\0\1\2\1\0\4\2\2\0\51\2\1\0\4\2"+
    "\2\0\41\2\1\0\4\2\2\0\7\2\1\0\1\2\1\0\4\2"+
    "\2\0\17\2\1\0\71\2\1\0\4\2\2\0\103\2\45\0\20\2"+
    "\20\0\125\2\14\0\u026c\2\2\0\21\2\1\1\32\2\5\0\113\2"+
    "\6\0\10\2\7\0\15\2\1\0\4\2\16\0\22\2\16\0\22\2"+
    "\16\0\15\2\1\0\3\2\17\0\64\2\43\0\1\2\4\0\1\2"+
    "\3\0\12\2\46\0\12\2\6\0\130\2\10\0\51\2\1\0\1\2"+
    "\5\0\106\2\12\0\37\2\47\0\12\2\36\2\2\0\5\2\13\0"+
    "\54\2\25\0\7\2\10\0\12\2\46\0\27\2\11\0\65\2\53\0"+
    "\12\2\6\0\12\2\15\0\1\2\135\0\57\2\21\0\7\2\4\0"+
    "\12\2\51\0\36\2\15\0\2\2\12\2\54\2\32\0\44\2\34\0"+
    "\12\2\3\0\3\2\12\2\44\2\153\0\4\2\1\0\4\2\3\0"+
    "\2\2\11\0\300\2\100\0\u0116\2\2\0\6\2\2\0\46\2\2\0"+
    "\6\2\2\0\10\2\1\0\1\2\1\0\1\2\1\0\1\2\1\0"+
    "\37\2\2\0\65\2\1\0\7\2\1\0\1\2\3\0\3\2\1\0"+
    "\7\2\3\0\4\2\2\0\6\2\4\0\15\2\5\0\3\2\1\0"+
    "\7\2\3\0\13\1\35\0\1\16\1\16\5\0\1\1\57\0\1\1"+
    "\21\0\1\2\15\0\1\2\20\0\15\2\145\0\1\2\4\0\1\2"+
    "\2\0\12\2\1\0\1\2\3\0\5\2\6\0\1\2\1\0\1\2"+
    "\1\0\1\2\1\0\4\2\1\0\13\2\2\0\4\2\5\0\5\2"+
    "\4\0\1\2\64\0\2\2\u0a7b\0\57\2\1\0\57\2\1\0\205\2"+
    "\6\0\4\2\3\0\2\2\14\0\46\2\1\0\1\2\5\0\1\2"+
    "\2\0\70\2\7\0\1\2\20\0\27\2\11\0\7\2\1\0\7\2"+
    "\1\0\7\2\1\0\7\2\1\0\7\2\1\0\7\2\1\0\7\2"+
    "\1\0\7\2\120\0\1\2\u01d0\0\1\1\4\0\2\2\52\0\5\2"+
    "\5\0\2\2\4\0\126\2\6\0\3\2\1\0\132\2\1\0\4\2"+
    "\5\0\51\2\3\0\136\2\21\0\33\2\65\0\20\2\u0200\0\u19b6\2"+
    "\112\0\u51cd\2\63\0\u048d\2\103\0\56\2\2\0\u010d\2\3\0\20\2"+
    "\12\2\2\2\24\0\57\2\20\0\37\2\2\0\106\2\61\0\11\2"+
    "\2\0\147\2\2\0\4\2\1\0\36\2\2\0\2\2\105\0\13\2"+
    "\1\0\3\2\1\0\4\2\1\0\27\2\35\0\64\2\16\0\62\2"+
    "\34\0\12\2\30\0\6\2\3\0\1\2\4\0\12\2\34\2\12\0"+
    "\27\2\31\0\35\2\7\0\57\2\34\0\1\2\12\2\6\0\5\2"+
    "\1\0\12\2\12\2\5\2\1\0\51\2\27\0\3\2\1\0\10\2"+
    "\4\0\12\2\6\0\27\2\3\0\1\2\3\0\62\2\1\0\1\2"+
    "\3\0\2\2\2\0\5\2\2\0\1\2\1\0\1\2\30\0\3\2"+
    "\2\0\13\2\7\0\3\2\14\0\6\2\2\0\6\2\2\0\6\2"+
    "\11\0\7\2\1\0\7\2\1\0\53\2\1\0\4\2\4\0\2\2"+
    "\132\0\43\2\15\0\12\2\6\0\u2ba4\2\14\0\27\2\4\0\61\2"+
    "\u2104\0\u016e\2\2\0\152\2\46\0\7\2\14\0\5\2\5\0\1\2"+
    "\1\0\12\2\1\0\15\2\1\0\5\2\1\0\1\2\1\0\2\2"+
    "\1\0\2\2\1\0\154\2\41\0\u016b\2\22\0\100\2\2\0\66\2"+
    "\50\0\14\2\164\0\5\2\1\0\207\2\23\0\12\2\7\0\32\2"+
    "\6\0\32\2\13\0\131\2\3\0\6\2\2\0\6\2\2\0\6\2"+
    "\2\0\3\2\43\0\14\2\1\0\32\2\1\0\23\2\1\0\2\2"+
    "\1\0\17\2\2\0\16\2\42\0\173\2\u0185\0\35\2\3\0\61\2"+
    "\57\0\40\2\20\0\21\2\1\0\10\2\6\0\46\2\12\0\36\2"+
    "\2\0\44\2\4\0\10\2\60\0\236\2\2\0\12\2\126\0\50\2"+
    "\10\0\64\2\234\0\u0137\2\11\0\26\2\12\0\10\2\230\0\6\2"+
    "\2\0\1\2\1\0\54\2\1\0\2\2\3\0\1\2\2\0\27\2"+
    "\12\0\27\2\11\0\37\2\141\0\26\2\12\0\32\2\106\0\70\2"+
    "\6\0\2\2\100\0\1\2\17\0\4\2\1\0\3\2\1\0\33\2"+
    "\54\0\35\2\3\0\35\2\43\0\10\2\1\0\34\2\33\0\66\2"+
    "\12\0\26\2\12\0\23\2\15\0\22\2\156\0\111\2\u03ba\0\65\2"+
    "\56\0\12\2\23\0\55\2\40\0\31\2\7\0\12\2\11\0\44\2"+
    "\17\0\12\2\20\0\43\2\3\0\1\2\14\0\60\2\16\0\4\2"+
    "\13\0\12\2\1\2\45\0\22\2\1\0\31\2\204\0\57\2\21\0"+
    "\12\2\13\0\10\2\2\0\2\2\2\0\26\2\1\0\7\2\1\0"+
    "\2\2\1\0\5\2\3\0\1\2\37\0\5\2\u011e\0\60\2\24\0"+
    "\2\2\1\0\1\2\10\0\12\2\246\0\57\2\121\0\60\2\24\0"+
    "\1\2\13\0\12\2\46\0\53\2\25\0\12\2\u01d6\0\100\2\12\2"+
    "\25\0\1\2\u01c0\0\71\2\u0507\0\u0399\2\u0c67\0\u042f\2\u33d1\0\u0239\2"+
    "\7\0\37\2\1\0\12\2\146\0\36\2\22\0\60\2\20\0\4\2"+
    "\14\0\12\2\11\0\25\2\5\0\23\2\u0370\0\105\2\13\0\1\2"+
    "\102\0\15\2\u4060\0\2\2\u0bfe\0\153\2\5\0\15\2\3\0\11\2"+
    "\7\0\12\2\u1766\0\125\2\1\0\107\2\1\0\2\2\2\0\1\2"+
    "\2\0\2\2\2\0\4\2\1\0\14\2\1\0\1\2\1\0\7\2"+
    "\1\0\101\2\1\0\4\2\2\0\10\2\1\0\7\2\1\0\34\2"+
    "\1\0\4\2\1\0\5\2\1\0\1\2\3\0\7\2\1\0\u0154\2"+
    "\2\0\31\2\1\0\31\2\1\0\37\2\1\0\31\2\1\0\37\2"+
    "\1\0\31\2\1\0\37\2\1\0\31\2\1\0\37\2\1\0\31\2"+
    "\1\0\10\2\2\0\62\2\u1000\0\305\2\u053b\0\4\2\1\0\33\2"+
    "\1\0\2\2\1\0\1\2\2\0\1\2\1\0\12\2\1\0\4\2"+
    "\1\0\1\2\1\0\1\2\6\0\1\2\4\0\1\2\1\0\1\2"+
    "\1\0\1\2\1\0\3\2\1\0\2\2\1\0\1\2\2\0\1\2"+
    "\1\0\1\2\1\0\1\2\1\0\1\2\1\0\1\2\1\0\2\2"+
    "\1\0\1\2\2\0\4\2\1\0\7\2\1\0\4\2\1\0\4\2"+
    "\1\0\1\2\1\0\12\2\1\0\21\2\5\0\3\2\1\0\5\2"+
    "\1\0\21\2\u1144\0\ua6d7\2\51\0\u1035\2\13\0\336\2\u3fe2\0\u021e\2"+
    "\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\u05f0\0";

  /** 
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\1\0\1\1\13\0\1\2\1\3\4\2\1\3\1\1"+
    "\1\4\1\1\1\3\1\2\1\3\2\2\1\5\3\6"+
    "\2\7\1\2\1\10\1\11\1\12\10\0\1\13\1\0"+
    "\1\14\2\0\1\1\2\0\1\3\3\0\1\15\1\0"+
    "\1\16\1\6\1\0\2\6\4\0\1\17\3\0\1\1"+
    "\3\0\1\20\1\6\1\0\2\6\7\0\1\6\4\0"+
    "\1\21\1\22";

  private static int [] zzUnpackAction() {
    int [] result = new int[96];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** 
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\32\0\64\0\116\0\150\0\202\0\234\0\266"+
    "\0\320\0\352\0\u0104\0\u011e\0\u0138\0\u0152\0\u0152\0\u016c"+
    "\0\u0186\0\u01a0\0\u01ba\0\u01d4\0\u01ee\0\u01ee\0\u0208\0\u0222"+
    "\0\u023c\0\u0256\0\u0270\0\u028a\0\u0152\0\u02a4\0\u02be\0\u02d8"+
    "\0\u02f2\0\u030c\0\u0326\0\u0152\0\u0152\0\u0152\0\u0340\0\u0152"+
    "\0\u035a\0\u0374\0\u038e\0\u03a8\0\u016c\0\u03c2\0\u0152\0\u03dc"+
    "\0\u0152\0\u03f6\0\u0410\0\u042a\0\u0222\0\u0444\0\u045e\0\u0478"+
    "\0\u0270\0\u0492\0\u0152\0\u04ac\0\u0152\0\u04c6\0\u04e0\0\u04fa"+
    "\0\u0514\0\u052e\0\u0548\0\u0562\0\u057c\0\u0152\0\u0596\0\u05b0"+
    "\0\u05ca\0\u05e4\0\u05fe\0\u0618\0\u0632\0\u0152\0\u064c\0\u0666"+
    "\0\u0680\0\u069a\0\u035a\0\u06b4\0\u06ce\0\u06e8\0\u0702\0\u071c"+
    "\0\u0736\0\u0750\0\u076a\0\u0784\0\u079e\0\u07b8\0\u0152\0\u0152";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[96];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /** 
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\16\1\17\1\20\1\21\7\16\1\17\1\22\1\16"+
    "\1\17\1\16\1\23\1\24\10\20\10\25\1\26\1\25"+
    "\1\27\1\30\1\31\4\25\1\32\10\25\1\16\1\17"+
    "\1\33\1\34\5\16\1\35\1\16\1\17\1\22\1\16"+
    "\1\17\2\16\1\24\10\33\11\36\1\16\1\37\1\17"+
    "\1\22\2\36\2\40\1\24\10\36\13\41\1\17\1\42"+
    "\15\41\1\16\1\17\4\16\1\43\4\16\1\17\1\22"+
    "\1\16\1\17\2\16\1\24\11\16\1\17\6\16\1\44"+
    "\2\16\1\17\1\22\1\16\1\17\2\16\1\24\11\16"+
    "\1\17\3\16\1\45\1\16\1\45\3\16\1\17\1\22"+
    "\1\16\1\17\2\16\1\24\11\16\1\17\3\16\1\46"+
    "\1\16\1\46\3\16\1\17\1\22\1\16\1\17\2\16"+
    "\1\24\10\16\2\0\1\47\1\50\16\0\10\47\10\0"+
    "\1\51\30\0\1\52\27\0\1\53\1\0\1\53\55\0"+
    "\1\54\1\55\2\56\1\57\1\60\1\57\1\61\5\0"+
    "\1\54\3\0\10\55\1\0\1\54\3\0\1\57\1\60"+
    "\1\57\1\61\5\0\1\54\26\0\1\17\40\0\1\62"+
    "\5\0\1\63\1\0\13\24\4\0\13\24\12\25\1\64"+
    "\2\65\4\25\1\65\22\25\1\27\2\65\15\25\12\0"+
    "\1\66\31\0\1\66\1\17\16\0\12\24\1\67\4\0"+
    "\13\24\1\0\1\70\1\71\2\72\1\73\1\74\1\73"+
    "\1\75\5\0\1\70\3\0\10\71\1\0\1\70\3\0"+
    "\1\73\1\74\1\73\1\75\5\0\1\70\13\0\11\76"+
    "\1\77\1\37\2\77\2\76\2\100\1\77\22\76\1\37"+
    "\2\77\2\76\2\100\21\76\1\101\1\77\1\37\2\77"+
    "\2\76\2\100\1\77\10\76\13\41\1\0\31\41\1\17"+
    "\16\41\7\0\1\45\24\0\1\47\2\102\15\0\10\47"+
    "\1\0\1\51\11\0\1\103\2\0\1\51\21\0\1\53"+
    "\24\0\1\53\14\0\1\53\14\0\1\54\3\0\1\57"+
    "\1\60\1\57\1\61\2\0\1\104\1\105\1\0\1\54"+
    "\15\0\1\55\17\0\10\55\7\0\1\106\45\0\1\107"+
    "\37\0\1\110\12\25\1\27\1\25\1\111\15\25\13\0"+
    "\1\112\1\113\15\0\13\24\1\112\1\113\2\0\13\24"+
    "\1\0\1\70\3\0\1\73\1\74\1\73\1\75\2\0"+
    "\1\114\1\115\1\0\1\70\15\0\1\71\17\0\10\71"+
    "\7\0\1\116\22\0\11\76\1\77\1\117\2\77\2\76"+
    "\2\100\1\77\10\76\12\0\1\120\17\0\10\76\1\101"+
    "\1\77\1\117\2\77\2\76\2\100\1\77\12\76\1\121"+
    "\1\122\5\76\1\77\1\117\2\77\2\76\2\100\1\77"+
    "\10\121\2\0\1\47\17\0\10\47\1\0\1\51\11\0"+
    "\1\103\1\123\1\0\1\51\14\0\1\104\6\0\1\61"+
    "\2\0\1\104\1\105\1\0\1\104\26\0\1\104\42\0"+
    "\1\124\33\0\1\125\15\0\1\66\1\112\16\0\12\25"+
    "\1\27\2\65\4\25\1\65\10\25\13\0\1\112\17\0"+
    "\1\114\6\0\1\75\2\0\1\114\1\115\1\0\1\114"+
    "\26\0\1\114\16\0\12\76\1\37\1\126\1\127\2\76"+
    "\2\100\11\76\13\0\1\130\1\131\15\0\2\76\1\121"+
    "\2\132\5\76\1\117\2\77\2\76\2\100\1\77\10\121"+
    "\12\76\1\117\2\77\2\76\2\100\1\77\10\76\25\0"+
    "\1\133\26\0\1\134\7\0\11\36\1\0\1\117\2\0"+
    "\2\36\2\40\1\0\10\36\12\0\1\120\1\130\16\0"+
    "\11\36\1\0\1\37\2\0\2\36\2\40\1\0\10\36"+
    "\13\0\1\130\16\0\2\76\1\121\6\76\1\77\1\117"+
    "\2\77\2\76\2\100\1\77\10\121\26\0\1\135\27\0"+
    "\1\136\34\0\1\137\31\0\1\140\2\0";

  private static int [] zzUnpackTrans() {
    int [] result = new int[2002];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /* error messages for the codes above */
  private static final String ZZ_ERROR_MSG[] = {
    "Unknown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\1\0\1\1\13\0\2\11\15\1\1\11\6\1\3\11"+
    "\1\1\1\11\1\1\1\0\1\1\3\0\1\11\1\0"+
    "\1\11\2\0\1\1\2\0\1\1\3\0\1\11\1\0"+
    "\1\11\1\1\1\0\2\1\4\0\1\11\3\0\1\1"+
    "\3\0\1\11\1\1\1\0\2\1\7\0\1\1\4\0"+
    "\2\11";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[96];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the input device */
  private java.io.Reader zzReader;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private char zzBuffer[] = new char[ZZ_BUFFERSIZE];

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /** number of newlines encountered up to the start of the matched text */
  private int yyline;

  /** the number of characters up to the start of the matched text */
  private int yychar;

  /**
   * the number of characters from the last newline up to the start of the 
   * matched text
   */
  private int yycolumn;

  /** 
   * zzAtBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /** denotes if the user-EOF-code has already been executed */
  private boolean zzEOFDone;
  
  /** 
   * The number of occupied positions in zzBuffer beyond zzEndRead.
   * When a lead/high surrogate has been read from the input stream
   * into the final zzBuffer position, this will have a value of 1;
   * otherwise, it will have a value of 0.
   */
  private int zzFinalHighSurrogate = 0;

  /** For the backwards DFA of general lookahead statements */
  private boolean [] zzFin = new boolean [ZZ_BUFFERSIZE+1];

  /* user code: */



public static enum ConfigTokenType implements ParserTokenType {
    PROPERTY,
    APPEND_PROPERTY,
    VALUE,
    SCRIPT,
    IMPORT,
    COMMENT,
    SECTION_HEADER,
    END_SECTION;

    @Override
    public boolean isInstance(ParserTokenType tokenType) {
      return tokenType == this;
    }
}

private final Stack<Integer> stack = new Stack<>();


  /**
   * Creates a new scanner
   *
   * @param   in  the java.io.Reader to read input from.
   */
  public ConfigTokenizer(java.io.Reader in) {
    this.zzReader = in;
  }


  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    char [] map = new char[0x110000];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 2450) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }


  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   * 
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {

    /* first: make room (if you can) */
    if (zzStartRead > 0) {
      zzEndRead += zzFinalHighSurrogate;
      zzFinalHighSurrogate = 0;
      System.arraycopy(zzBuffer, zzStartRead,
                       zzBuffer, 0,
                       zzEndRead-zzStartRead);

      /* translate stored positions */
      zzEndRead-= zzStartRead;
      zzCurrentPos-= zzStartRead;
      zzMarkedPos-= zzStartRead;
      zzStartRead = 0;
    }

    /* is the buffer big enough? */
    if (zzCurrentPos >= zzBuffer.length - zzFinalHighSurrogate) {
      /* if not: blow it up */
      char newBuffer[] = new char[zzBuffer.length*2];
      System.arraycopy(zzBuffer, 0, newBuffer, 0, zzBuffer.length);
      zzBuffer = newBuffer;
      zzEndRead += zzFinalHighSurrogate;
      zzFinalHighSurrogate = 0;
    }

    /* fill the buffer with new input */
    int requested = zzBuffer.length - zzEndRead;
    int numRead = zzReader.read(zzBuffer, zzEndRead, requested);

    /* not supposed to occur according to specification of java.io.Reader */
    if (numRead == 0) {
      throw new java.io.IOException("Reader returned 0 characters. See JFlex examples for workaround.");
    }
    if (numRead > 0) {
      zzEndRead += numRead;
      /* If numRead == requested, we might have requested to few chars to
         encode a full Unicode character. We assume that a Reader would
         otherwise never return half characters. */
      if (numRead == requested) {
        if (Character.isHighSurrogate(zzBuffer[zzEndRead - 1])) {
          --zzEndRead;
          zzFinalHighSurrogate = 1;
        }
      }
      /* potentially more input available */
      return false;
    }

    /* numRead < 0 ==> end of stream */
    return true;
  }

    
  /**
   * Closes the input stream.
   */
  public final void yyclose() throws java.io.IOException {
    zzAtEOF = true;            /* indicate end of file */
    zzEndRead = zzStartRead;  /* invalidate buffer    */

    if (zzReader != null)
      zzReader.close();
  }


  /**
   * Resets the scanner to read from a new input stream.
   * Does not close the old reader.
   *
   * All internal variables are reset, the old input stream 
   * <b>cannot</b> be reused (internal buffer is discarded and lost).
   * Lexical state is set to <tt>ZZ_INITIAL</tt>.
   *
   * Internal scan buffer is resized down to its initial length, if it has grown.
   *
   * @param reader   the new input stream 
   */
  public final void yyreset(java.io.Reader reader) {
    zzReader = reader;
    zzAtBOL  = true;
    zzAtEOF  = false;
    zzEOFDone = false;
    zzEndRead = zzStartRead = 0;
    zzCurrentPos = zzMarkedPos = 0;
    zzFinalHighSurrogate = 0;
    yyline = yychar = yycolumn = 0;
    zzLexicalState = YYINITIAL;
    if (zzBuffer.length > ZZ_BUFFERSIZE)
      zzBuffer = new char[ZZ_BUFFERSIZE];
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final String yytext() {
    return new String( zzBuffer, zzStartRead, zzMarkedPos-zzStartRead );
  }


  /**
   * Returns the character at position <tt>pos</tt> from the 
   * matched text. 
   * 
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch. 
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer[zzStartRead+pos];
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of 
   * yypushback(int) and a match-all fallback rule) this method 
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  } 


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public com.davidbracewell.parsing.ParserToken next() throws java.io.IOException,     com.davidbracewell.parsing.ParseException
 {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    char [] zzBufferL = zzBuffer;
    char [] zzCMapL = ZZ_CMAP;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      yychar+= zzMarkedPosL-zzStartRead;

      boolean zzR = false;
      int zzCh;
      int zzCharCount;
      for (zzCurrentPosL = zzStartRead  ;
           zzCurrentPosL < zzMarkedPosL ;
           zzCurrentPosL += zzCharCount ) {
        zzCh = Character.codePointAt(zzBufferL, zzCurrentPosL, zzMarkedPosL);
        zzCharCount = Character.charCount(zzCh);
        switch (zzCh) {
        case '\u000B':
        case '\u000C':
        case '\u0085':
        case '\u2028':
        case '\u2029':
          yyline++;
          zzR = false;
          break;
        case '\r':
          yyline++;
          zzR = true;
          break;
        case '\n':
          if (zzR)
            zzR = false;
          else {
            yyline++;
          }
          break;
        default:
          zzR = false;
        }
      }

      if (zzR) {
        // peek one character ahead if it is \n (if we have counted one line too much)
        boolean zzPeek;
        if (zzMarkedPosL < zzEndReadL)
          zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        else if (zzAtEOF)
          zzPeek = false;
        else {
          boolean eof = zzRefill();
          zzEndReadL = zzEndRead;
          zzMarkedPosL = zzMarkedPos;
          zzBufferL = zzBuffer;
          if (eof) 
            zzPeek = false;
          else 
            zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        }
        if (zzPeek) yyline--;
      }
      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;
  
      zzState = ZZ_LEXSTATE[zzLexicalState];

      // set up zzAction for empty match case:
      int zzAttributes = zzAttrL[zzState];
      if ( (zzAttributes & 1) == 1 ) {
        zzAction = zzState;
      }


      zzForAction: {
        while (true) {
    
          if (zzCurrentPosL < zzEndReadL) {
            zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL, zzEndReadL);
            zzCurrentPosL += Character.charCount(zzInput);
          }
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL, zzEndReadL);
              zzCurrentPosL += Character.charCount(zzInput);
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMapL[zzInput] ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
        zzAtEOF = true;
        return null;
      }
      else {
        switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
          case 1: 
            { yybegin(stack.pop());  return new ParserToken(yytext(),ConfigTokenType.VALUE);
            }
          case 19: break;
          case 2: 
            { throw new com.davidbracewell.parsing.ParseException("UNEXPECTED TOKEN \"" + yytext() +"\" at line: " + yyline + " char offset: " + yychar + " state: " + yystate());
            }
          case 20: break;
          case 3: 
            { 
            }
          case 21: break;
          case 4: 
            { stack.push(YYINITIAL);yybegin(SECTION_PROPERTY);
            }
          case 22: break;
          case 5: 
            { yybegin(stack.pop()); return new ParserToken(yytext(),ConfigTokenType.END_SECTION);
            }
          case 23: break;
          case 6: 
            { yybegin(SECTION_PROPERTY); return new ParserToken(yytext(),ConfigTokenType.VALUE);
            }
          case 24: break;
          case 7: 
            { yybegin(YYINITIAL); return new ParserToken(yytext(),ConfigTokenType.VALUE);
            }
          case 25: break;
          case 8: 
            { yybegin(SECTION_PROPERTY);
            }
          case 26: break;
          case 9: 
            { yybegin(VALUE);
            }
          case 27: break;
          case 10: 
            { yybegin(SECTION_VALUE);
            }
          case 28: break;
          case 11: 
            // general lookahead, find correct zzMarkedPos
            { int zzFState = 9;
              int zzFPos = zzStartRead;
              if (zzFin.length <= zzBufferL.length) { zzFin = new boolean[zzBufferL.length+1]; }
              boolean zzFinL[] = zzFin;
              while (zzFState != -1 && zzFPos < zzMarkedPos) {
                zzFinL[zzFPos] = ((zzAttrL[zzFState] & 1) == 1);
                zzInput = Character.codePointAt(zzBufferL, zzFPos, zzMarkedPos);
                zzFPos += Character.charCount(zzInput);
                zzFState = zzTransL[ zzRowMapL[zzFState] + zzCMapL[zzInput] ];
              }
              if (zzFState != -1) { zzFinL[zzFPos++] = ((zzAttrL[zzFState] & 1) == 1); } 
              while (zzFPos <= zzMarkedPos) {
                zzFinL[zzFPos++] = false;
              }

              zzFState = 12;
              zzFPos = zzMarkedPos;
              while (!zzFinL[zzFPos] || (zzAttrL[zzFState] & 1) != 1) {
                zzInput = Character.codePointBefore(zzBufferL, zzFPos, zzStartRead);
                zzFPos -= Character.charCount(zzInput);
                zzFState = zzTransL[ zzRowMapL[zzFState] + zzCMapL[zzInput] ];
              };
              zzMarkedPos = zzFPos;
            }
            { stack.push(YYINITIAL);yybegin(START_VALUE); return new ParserToken(yytext(),ConfigTokenType.PROPERTY);
            }
          case 29: break;
          case 12: 
            // general lookahead, find correct zzMarkedPos
            { int zzFState = 9;
              int zzFPos = zzStartRead;
              if (zzFin.length <= zzBufferL.length) { zzFin = new boolean[zzBufferL.length+1]; }
              boolean zzFinL[] = zzFin;
              while (zzFState != -1 && zzFPos < zzMarkedPos) {
                zzFinL[zzFPos] = ((zzAttrL[zzFState] & 1) == 1);
                zzInput = Character.codePointAt(zzBufferL, zzFPos, zzMarkedPos);
                zzFPos += Character.charCount(zzInput);
                zzFState = zzTransL[ zzRowMapL[zzFState] + zzCMapL[zzInput] ];
              }
              if (zzFState != -1) { zzFinL[zzFPos++] = ((zzAttrL[zzFState] & 1) == 1); } 
              while (zzFPos <= zzMarkedPos) {
                zzFinL[zzFPos++] = false;
              }

              zzFState = 10;
              zzFPos = zzMarkedPos;
              while (!zzFinL[zzFPos] || (zzAttrL[zzFState] & 1) != 1) {
                zzInput = Character.codePointBefore(zzBufferL, zzFPos, zzStartRead);
                zzFPos -= Character.charCount(zzInput);
                zzFState = zzTransL[ zzRowMapL[zzFState] + zzCMapL[zzInput] ];
              };
              zzMarkedPos = zzFPos;
            }
            { stack.push(YYINITIAL);yybegin(START_SECTION); return new ParserToken(yytext(),ConfigTokenType.SECTION_HEADER);
            }
          case 30: break;
          case 13: 
            // general lookahead, find correct zzMarkedPos
            { int zzFState = 9;
              int zzFPos = zzStartRead;
              if (zzFin.length <= zzBufferL.length) { zzFin = new boolean[zzBufferL.length+1]; }
              boolean zzFinL[] = zzFin;
              while (zzFState != -1 && zzFPos < zzMarkedPos) {
                zzFinL[zzFPos] = ((zzAttrL[zzFState] & 1) == 1);
                zzInput = Character.codePointAt(zzBufferL, zzFPos, zzMarkedPos);
                zzFPos += Character.charCount(zzInput);
                zzFState = zzTransL[ zzRowMapL[zzFState] + zzCMapL[zzInput] ];
              }
              if (zzFState != -1) { zzFinL[zzFPos++] = ((zzAttrL[zzFState] & 1) == 1); } 
              while (zzFPos <= zzMarkedPos) {
                zzFinL[zzFPos++] = false;
              }

              zzFState = 12;
              zzFPos = zzMarkedPos;
              while (!zzFinL[zzFPos] || (zzAttrL[zzFState] & 1) != 1) {
                zzInput = Character.codePointBefore(zzBufferL, zzFPos, zzStartRead);
                zzFPos -= Character.charCount(zzInput);
                zzFState = zzTransL[ zzRowMapL[zzFState] + zzCMapL[zzInput] ];
              };
              zzMarkedPos = zzFPos;
            }
            { yybegin(START_SECTION_VALUE); return new ParserToken(yytext(),ConfigTokenType.PROPERTY);
            }
          case 31: break;
          case 14: 
            // general lookahead, find correct zzMarkedPos
            { int zzFState = 9;
              int zzFPos = zzStartRead;
              if (zzFin.length <= zzBufferL.length) { zzFin = new boolean[zzBufferL.length+1]; }
              boolean zzFinL[] = zzFin;
              while (zzFState != -1 && zzFPos < zzMarkedPos) {
                zzFinL[zzFPos] = ((zzAttrL[zzFState] & 1) == 1);
                zzInput = Character.codePointAt(zzBufferL, zzFPos, zzMarkedPos);
                zzFPos += Character.charCount(zzInput);
                zzFState = zzTransL[ zzRowMapL[zzFState] + zzCMapL[zzInput] ];
              }
              if (zzFState != -1) { zzFinL[zzFPos++] = ((zzAttrL[zzFState] & 1) == 1); } 
              while (zzFPos <= zzMarkedPos) {
                zzFinL[zzFPos++] = false;
              }

              zzFState = 10;
              zzFPos = zzMarkedPos;
              while (!zzFinL[zzFPos] || (zzAttrL[zzFState] & 1) != 1) {
                zzInput = Character.codePointBefore(zzBufferL, zzFPos, zzStartRead);
                zzFPos -= Character.charCount(zzInput);
                zzFState = zzTransL[ zzRowMapL[zzFState] + zzCMapL[zzInput] ];
              };
              zzMarkedPos = zzFPos;
            }
            { stack.push(SECTION_PROPERTY);yybegin(START_SECTION); return new ParserToken(yytext(),ConfigTokenType.SECTION_HEADER);
            }
          case 32: break;
          case 15: 
            // general lookahead, find correct zzMarkedPos
            { int zzFState = 9;
              int zzFPos = zzStartRead;
              if (zzFin.length <= zzBufferL.length) { zzFin = new boolean[zzBufferL.length+1]; }
              boolean zzFinL[] = zzFin;
              while (zzFState != -1 && zzFPos < zzMarkedPos) {
                zzFinL[zzFPos] = ((zzAttrL[zzFState] & 1) == 1);
                zzInput = Character.codePointAt(zzBufferL, zzFPos, zzMarkedPos);
                zzFPos += Character.charCount(zzInput);
                zzFState = zzTransL[ zzRowMapL[zzFState] + zzCMapL[zzInput] ];
              }
              if (zzFState != -1) { zzFinL[zzFPos++] = ((zzAttrL[zzFState] & 1) == 1); } 
              while (zzFPos <= zzMarkedPos) {
                zzFinL[zzFPos++] = false;
              }

              zzFState = 11;
              zzFPos = zzMarkedPos;
              while (!zzFinL[zzFPos] || (zzAttrL[zzFState] & 1) != 1) {
                zzInput = Character.codePointBefore(zzBufferL, zzFPos, zzStartRead);
                zzFPos -= Character.charCount(zzInput);
                zzFState = zzTransL[ zzRowMapL[zzFState] + zzCMapL[zzInput] ];
              };
              zzMarkedPos = zzFPos;
            }
            { stack.push(YYINITIAL);yybegin(APPENDER); return new ParserToken(yytext(),ConfigTokenType.APPEND_PROPERTY);
            }
          case 33: break;
          case 16: 
            // general lookahead, find correct zzMarkedPos
            { int zzFState = 9;
              int zzFPos = zzStartRead;
              if (zzFin.length <= zzBufferL.length) { zzFin = new boolean[zzBufferL.length+1]; }
              boolean zzFinL[] = zzFin;
              while (zzFState != -1 && zzFPos < zzMarkedPos) {
                zzFinL[zzFPos] = ((zzAttrL[zzFState] & 1) == 1);
                zzInput = Character.codePointAt(zzBufferL, zzFPos, zzMarkedPos);
                zzFPos += Character.charCount(zzInput);
                zzFState = zzTransL[ zzRowMapL[zzFState] + zzCMapL[zzInput] ];
              }
              if (zzFState != -1) { zzFinL[zzFPos++] = ((zzAttrL[zzFState] & 1) == 1); } 
              while (zzFPos <= zzMarkedPos) {
                zzFinL[zzFPos++] = false;
              }

              zzFState = 11;
              zzFPos = zzMarkedPos;
              while (!zzFinL[zzFPos] || (zzAttrL[zzFState] & 1) != 1) {
                zzInput = Character.codePointBefore(zzBufferL, zzFPos, zzStartRead);
                zzFPos -= Character.charCount(zzInput);
                zzFState = zzTransL[ zzRowMapL[zzFState] + zzCMapL[zzInput] ];
              };
              zzMarkedPos = zzFPos;
            }
            { stack.push(SECTION_PROPERTY); yybegin(APPENDER); return new ParserToken(yytext(),ConfigTokenType.APPEND_PROPERTY);
            }
          case 34: break;
          case 17: 
            { yybegin(IMPORT); return new ParserToken(yytext(),ConfigTokenType.IMPORT);
            }
          case 35: break;
          case 18: 
            { yybegin(SCRIPT); return new ParserToken(yytext(),ConfigTokenType.SCRIPT);
            }
          case 36: break;
          default:
            zzScanError(ZZ_NO_MATCH);
        }
      }
    }
  }


}
