/*
 * (c) 2005 David B. Bracewell
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.davidbracewell;

import com.davidbracewell.logging.Logger;
import com.google.common.collect.Lists;

import java.text.Collator;
import java.text.DateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Enumeration of world languages with helpful information on whether or not the language is Whitespace delimited or if
 * the language is read right to left.
 *
 * @author David B. Bracewell
 */
public enum Language {
  ENGLISH("EN") {
    @Override
    public Locale asLocale() {
      return Locale.US;
    }

  },
  JAPANESE("JA") {
    @Override
    public boolean usesWhitespace() {
      return false;
    }

    @Override
    public Locale asLocale() {
      return Locale.JAPAN;
    }

  },
  CHINESE("ZH") {
    @Override
    public boolean usesWhitespace() {
      return false;
    }

    @Override
    public Locale asLocale() {
      return Locale.CHINA;
    }

  },
  ABKHAZIAN("AB"),
  AFAR("AA"),
  AFRIKAANS("AF"),
  ALBANIAN("SQ"),
  AMHARIC("AM"),
  ARABIC("AR") {
    @Override
    public boolean isRightToLeft() {
      return true;
    }
  },
  ARMENIAN("HY"),
  ASSAMESE("AS"),
  AYMARA("AY"),
  AZERBAIJANI("AZ"),
  BASHKIR("BA"),
  BASQUE("EU"),
  BENGALI("BN"),
  BHUTANI("DZ"),
  BIHARI("BH"),
  BISLAMA("BI"),
  BRETON("BR"),
  BULGARIAN("BG"),
  BURMESE("MY"),
  BYELORUSSIAN("BE"),
  CAMBODIAN("KM"),
  CATALAN("CA"),
  CORSICAN("CO"),
  CROATIAN("HR"),
  CZECH("CS"),
  DANISH("DA"),
  DUTCH("NL"),
  ESPERANTO("EO"),
  ESTONIAN("ET"),
  FAEROESE("FO"),
  FIJI("FJ"),
  FINNISH("FI"),
  FRENCH("FR"),
  FRISIAN("FY"),
  GAELIC("GD"),
  GALICIAN("GL"),
  GEORGIAN("KA"),
  GERMAN("DE"),
  GREEK("EL"),
  GREENLANDIC("KL"),
  GUARANI("GN"),
  GUJARATI("GU"),
  HAUSA("HA"),
  HEBREW("IW") {
    @Override
    public boolean isRightToLeft() {
      return true;
    }
  },
  HINDI("HI"),
  HUNGARIAN("HU"),
  ICELANDIC("IS"),
  INDONESIAN("IN"),
  INTERLINGUA("IA"),
  INTERLINGUE("IE"),
  INUPIAK("IK"),
  IRISH("GA"),
  ITALIAN("IT"),
  JAVANESE("JW") {
    @Override
    public boolean isRightToLeft() {
      return true;
    }
  },
  KANNADA("KN"),
  KASHMIRI("KS") {
    @Override
    public boolean isRightToLeft() {
      return true;
    }
  },
  KAZAKH("KK"),
  KINYARWANDA("RW"),
  KIRGHIZ("KY"),
  KIRUNDI("RN"),
  KOREAN("KO"),
  KURDISH("KU") {
    @Override
    public boolean isRightToLeft() {
      return true;
    }
  },
  LAOTHIAN("LO"),
  LATIN("LA"),
  LATVIAN("LV"),
  LINGALA("LN"),
  LITHUANIAN("LT"),
  MACEDONIAN("MK"),
  MALAGASY("MG"),
  MALAY("MS") {
    @Override
    public boolean isRightToLeft() {
      return true;
    }
  },
  MALAYALAM("ML") {
    @Override
    public boolean isRightToLeft() {
      return true;
    }
  },
  MALTESE("MT"),
  MAORI("MI"),
  MARATHI("MR"),
  MOLDAVIAN("MO"),
  MONGOLIAN("MN"),
  NAURU("NA"),
  NEPALI("NE"),
  NORWEGIAN("NO"),
  OCCITAN("OC"),
  ORIYA("OR"),
  OROMO("OM"),
  PASHTO("PS") {
    @Override
    public boolean isRightToLeft() {
      return true;
    }
  },
  PERSIAN("FA") {
    @Override
    public boolean isRightToLeft() {
      return true;
    }
  },
  POLISH("PL"),
  PORTUGUESE("PT"),
  PUNJABI("PA") {
    @Override
    public boolean isRightToLeft() {
      return true;
    }
  },
  QUECHUA("QU"),
  ROMANIAN("RO"),
  RUSSIAN("RU"),
  SAMOAN("SM"),
  SANGRO("SG"),
  SANSKRIT("SA"),
  SERBIAN("SR"),
  SERBO_CROATIAN("SH"),
  SESOTHO("ST"),
  SETSWANA("TN"),
  SHONA("SN"),
  SINDHI("SD") {
    @Override
    public boolean isRightToLeft() {
      return true;
    }
  },
  SINGHALESE("SI"),
  SISWATI("SS"),
  SLOVAK("SK"),
  SLOVENIAN("SL"),
  SOMALI("SO") {
    @Override
    public boolean isRightToLeft() {
      return true;
    }
  },
  SPANISH("ES"),
  SUDANESE("SU"),
  SWAHILI("SW"),
  SWEDISH("SV"),
  TAGALOG("TL"),
  TAJIK("TG"),
  TAMIL("TA"),
  TATAR("TT"),
  TEGULU("TE"),
  THAI("TH"),
  TIBETAN("BO"),
  TIGRINYA("TI"),
  TONGA("TO"),
  TSONGA("TS"),
  TURKISH("TR"),
  TURKMEN("TK") {
    @Override
    public boolean isRightToLeft() {
      return true;
    }
  },
  TWI("TW"),
  UKRAINIAN("UK"),
  URDU("UR") {
    @Override
    public boolean isRightToLeft() {
      return true;
    }
  },
  UZBEK("UZ"),
  VIETNAMESE("VI"),
  VOLAPUK("VO"),
  WELSH("CY"),
  WOLOF("WO"),
  XHOSA("XH"),
  YIDDISH("JI") {
    @Override
    public boolean isRightToLeft() {
      return true;
    }
  },
  YORUBA("YO"),
  ZULU("ZU"),


  UNKNOWN("UNKNOWN") {
    @Override
    public Locale asLocale() {
      return Locale.getDefault();
    }
  };

  private static final Logger log = Logger.getLogger(Language.class);
  private final String code;
  private transient List<Locale> locales;

  Language(String code) {
    this.code = code;
  }

  /**
   * Parses a language code to get its corresponding LanguageId
   *
   * @param code language code
   * @return The parsed language or null
   */
  public static Language fromString(String code) {
    try {
      return Language.valueOf(code);
    } catch (Exception e) {

      Locale toFind;
      if (code.contains("_") || code.contains("-")) {
        String[] parts = code.split("[_\\-]");
        toFind = new Locale(parts[0], parts[1]);
      } else {
        toFind = new Locale(code);
      }

      return fromLocale(toFind);
    }
  }

  /**
   * @return The possible locales associated with the language.
   */
  public synchronized List<Locale> getLocales() {
    if (locales != null) {
      return locales;
    }
    locales = Lists.newArrayList();
    for (Locale locale : DateFormat.getAvailableLocales()) {
      if (locale.getLanguage().equalsIgnoreCase(code)) {
        locales.add(locale);
      }
    }
    return locales;
  }

  /**
   * @return True if the language uses white space to separate words, false if not
   */
  public boolean usesWhitespace() {
    return true;
  }

  /**
   * @return True if the language is written  right to left
   */
  public boolean isRightToLeft() {
    return false;
  }

  /**
   * Gets the language as a {@link java.util.Locale}
   *
   * @return The language locale
   */
  public Locale asLocale() {
    return Locale.forLanguageTag(name());
  }

  /**
   * @return The ISO2 Language code
   */
  public String getCode() {
    return code;
  }

  /**
   * Convenience method for constructing a collator.
   *
   * @param strength      The strength {@see Collator}
   * @param decomposition The decomposition {@see Collator}
   * @return The collator
   */
  public final Collator getCollator(int strength, int decomposition) {
    Collator collator = Collator.getInstance(asLocale());
    collator.setStrength(strength);
    collator.setDecomposition(decomposition);
    return collator;
  }

  public static Language fromLocale(Locale locale) {
    if (locale == null) {
      locale = Locale.getDefault();
    }
    for (Language l : Language.values()) {
      if (l.asLocale().getLanguage().equals(locale.getLanguage())) {
        return l;
      }
    }
    log.severe("{0} is an invalid language code", locale);
    return UNKNOWN;
  }

  /**
   * Convenience method for constructing a collator using <code>FULL_DECOMPOSITION</code>
   *
   * @param strength The strength {@see Collator}
   * @return The collator
   */
  public final Collator getCollator(int strength) {
    return getCollator(strength, Collator.FULL_DECOMPOSITION);
  }

  /**
   * Convenience method for constructing a collator using a strength of <code>TERTIARY</code> and decomposition of
   * <code>FULL_DECOMPOSITION</code>
   *
   * @return The collator
   */
  public final Collator getCollator() {
    return getCollator(Collator.TERTIARY, Collator.FULL_DECOMPOSITION);
  }

}// END OF Language
