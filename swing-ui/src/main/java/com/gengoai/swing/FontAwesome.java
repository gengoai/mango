/*
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

package com.gengoai.swing;

import com.gengoai.io.Resources;

import javax.swing.*;
import java.awt.*;

public enum FontAwesome {
   AD('\uf641'),
   ADDRESS_BOOK('\uf2b9'),
   ADDRESS_CARD('\uf2bb'),
   ADJUST('\uf042'),
   AIR_FRESHENER('\uf5d0'),
   ALIGN_CENTER('\uf037'),
   ALIGN_JUSTIFY('\uf039'),
   ALIGN_LEFT('\uf036'),
   ALIGN_RIGHT('\uf038'),
   ALLERGIES('\uf461'),
   AMBULANCE('\uf0f9'),
   AMERICAN_SIGN_LANGUAGE_INTERPRETING('\uf2a3'),
   ANCHOR('\uf13d'),
   ANGLE_DOUBLE_DOWN('\uf103'),
   ANGLE_DOUBLE_LEFT('\uf100'),
   ANGLE_DOUBLE_RIGHT('\uf101'),
   ANGLE_DOUBLE_UP('\uf102'),
   ANGLE_DOWN('\uf107'),
   ANGLE_LEFT('\uf104'),
   ANGLE_RIGHT('\uf105'),
   ANGLE_UP('\uf106'),
   ANGRY('\uf556'),
   ANKH('\uf644'),
   APPLE_ALT('\uf5d1'),
   ARCHIVE('\uf187'),
   ARCHWAY('\uf557'),
   ARROW_ALT_CIRCLE_DOWN('\uf358'),
   ARROW_ALT_CIRCLE_LEFT('\uf359'),
   ARROW_ALT_CIRCLE_RIGHT('\uf35a'),
   ARROW_ALT_CIRCLE_UP('\uf35b'),
   ARROW_CIRCLE_DOWN('\uf0ab'),
   ARROW_CIRCLE_LEFT('\uf0a8'),
   ARROW_CIRCLE_RIGHT('\uf0a9'),
   ARROW_CIRCLE_UP('\uf0aa'),
   ARROW_DOWN('\uf063'),
   ARROW_LEFT('\uf060'),
   ARROW_RIGHT('\uf061'),
   ARROW_UP('\uf062'),
   ARROWS_ALT('\uf0b2'),
   ARROWS_ALT_H('\uf337'),
   ARROWS_ALT_V('\uf338'),
   ASSISTIVE_LISTENING_SYSTEMS('\uf2a2'),
   ASTERISK('\uf069'),
   AT('\uf1fa'),
   ATLAS('\uf558'),
   ATOM('\uf5d2'),
   AUDIO_DESCRIPTION('\uf29e'),
   AWARD('\uf559'),
   BABY('\uf77c'),
   BABY_CARRIAGE('\uf77d'),
   BACKSPACE('\uf55a'),
   BACKWARD('\uf04a'),
   BACON('\uf7e5'),
   BAHAI('\uf666'),
   BALANCE_SCALE('\uf24e'),
   BALANCE_SCALE_LEFT('\uf515'),
   BALANCE_SCALE_RIGHT('\uf516'),
   BAN('\uf05e'),
   BAND_AID('\uf462'),
   BARCODE('\uf02a'),
   BARS('\uf0c9'),
   BASEBALL_BALL('\uf433'),
   BASKETBALL_BALL('\uf434'),
   BATH('\uf2cd'),
   BATTERY_EMPTY('\uf244'),
   BATTERY_FULL('\uf240'),
   BATTERY_HALF('\uf242'),
   BATTERY_QUARTER('\uf243'),
   BATTERY_THREE_QUARTERS('\uf241'),
   BED('\uf236'),
   BEER('\uf0fc'),
   BELL('\uf0f3'),
   BELL_SLASH('\uf1f6'),
   BEZIER_CURVE('\uf55b'),
   BIBLE('\uf647'),
   BICYCLE('\uf206'),
   BIKING('\uf84a'),
   BINOCULARS('\uf1e5'),
   BIOHAZARD('\uf780'),
   BIRTHDAY_CAKE('\uf1fd'),
   BLENDER('\uf517'),
   BLENDER_PHONE('\uf6b6'),
   BLIND('\uf29d'),
   BLOG('\uf781'),
   BOLD('\uf032'),
   BOLT('\uf0e7'),
   BOMB('\uf1e2'),
   BONE('\uf5d7'),
   BONG('\uf55c'),
   BOOK('\uf02d'),
   BOOK_DEAD('\uf6b7'),
   BOOK_MEDICAL('\uf7e6'),
   BOOK_OPEN('\uf518'),
   BOOK_READER('\uf5da'),
   BOOKMARK('\uf02e'),
   BORDER_ALL('\uf84c'),
   BORDER_NONE('\uf850'),
   BORDER_STYLE('\uf853'),
   BOWLING_BALL('\uf436'),
   BOX('\uf466'),
   BOX_OPEN('\uf49e'),
   BOXES('\uf468'),
   BRAILLE('\uf2a1'),
   BRAIN('\uf5dc'),
   BREAD_SLICE('\uf7ec'),
   BRIEFCASE('\uf0b1'),
   BRIEFCASE_MEDICAL('\uf469'),
   BROADCAST_TOWER('\uf519'),
   BROOM('\uf51a'),
   BRUSH('\uf55d'),
   BUG('\uf188'),
   BUILDING('\uf1ad'),
   BULLHORN('\uf0a1'),
   BULLSEYE('\uf140'),
   BURN('\uf46a'),
   BUS('\uf207'),
   BUS_ALT('\uf55e'),
   BUSINESS_TIME('\uf64a'),
   CALCULATOR('\uf1ec'),
   CALENDAR('\uf133'),
   CALENDAR_ALT('\uf073'),
   CALENDAR_CHECK('\uf274'),
   CALENDAR_DAY('\uf783'),
   CALENDAR_MINUS('\uf272'),
   CALENDAR_PLUS('\uf271'),
   CALENDAR_TIMES('\uf273'),
   CALENDAR_WEEK('\uf784'),
   CAMERA('\uf030'),
   CAMERA_RETRO('\uf083'),
   CAMPGROUND('\uf6bb'),
   CANDY_CANE('\uf786'),
   CANNABIS('\uf55f'),
   CAPSULES('\uf46b'),
   CAR('\uf1b9'),
   CAR_ALT('\uf5de'),
   CAR_BATTERY('\uf5df'),
   CAR_CRASH('\uf5e1'),
   CAR_SIDE('\uf5e4'),
   CARAVAN('\uf8ff'),
   CARET_DOWN('\uf0d7'),
   CARET_LEFT('\uf0d9'),
   CARET_RIGHT('\uf0da'),
   CARET_SQUARE_DOWN('\uf150'),
   CARET_SQUARE_LEFT('\uf191'),
   CARET_SQUARE_RIGHT('\uf152'),
   CARET_SQUARE_UP('\uf151'),
   CARET_UP('\uf0d8'),
   CARROT('\uf787'),
   CART_ARROW_DOWN('\uf218'),
   CART_PLUS('\uf217'),
   CASH_REGISTER('\uf788'),
   CAT('\uf6be'),
   CERTIFICATE('\uf0a3'),
   CHAIR('\uf6c0'),
   CHALKBOARD('\uf51b'),
   CHALKBOARD_TEACHER('\uf51c'),
   CHARGING_STATION('\uf5e7'),
   CHART_AREA('\uf1fe'),
   CHART_BAR('\uf080'),
   CHART_LINE('\uf201'),
   CHART_PIE('\uf200'),
   CHECK('\uf00c'),
   CHECK_CIRCLE('\uf058'),
   CHECK_DOUBLE('\uf560'),
   CHECK_SQUARE('\uf14a'),
   CHEESE('\uf7ef'),
   CHESS('\uf439'),
   CHESS_BISHOP('\uf43a'),
   CHESS_BOARD('\uf43c'),
   CHESS_KING('\uf43f'),
   CHESS_KNIGHT('\uf441'),
   CHESS_PAWN('\uf443'),
   CHESS_QUEEN('\uf445'),
   CHESS_ROOK('\uf447'),
   CHEVRON_CIRCLE_DOWN('\uf13a'),
   CHEVRON_CIRCLE_LEFT('\uf137'),
   CHEVRON_CIRCLE_RIGHT('\uf138'),
   CHEVRON_CIRCLE_UP('\uf139'),
   CHEVRON_DOWN('\uf078'),
   CHEVRON_LEFT('\uf053'),
   CHEVRON_RIGHT('\uf054'),
   CHEVRON_UP('\uf077'),
   CHILD('\uf1ae'),
   CHURCH('\uf51d'),
   CIRCLE('\uf111'),
   CIRCLE_NOTCH('\uf1ce'),
   CITY('\uf64f'),
   CLINIC_MEDICAL('\uf7f2'),
   CLIPBOARD('\uf328'),
   CLIPBOARD_CHECK('\uf46c'),
   CLIPBOARD_LIST('\uf46d'),
   CLOCK('\uf017'),
   CLONE('\uf24d'),
   CLOSED_CAPTIONING('\uf20a'),
   CLOUD('\uf0c2'),
   CLOUD_DOWNLOAD_ALT('\uf381'),
   CLOUD_MEATBALL('\uf73b'),
   CLOUD_MOON('\uf6c3'),
   CLOUD_MOON_RAIN('\uf73c'),
   CLOUD_RAIN('\uf73d'),
   CLOUD_SHOWERS_HEAVY('\uf740'),
   CLOUD_SUN('\uf6c4'),
   CLOUD_SUN_RAIN('\uf743'),
   CLOUD_UPLOAD_ALT('\uf382'),
   COCKTAIL('\uf561'),
   CODE('\uf121'),
   CODE_BRANCH('\uf126'),
   COFFEE('\uf0f4'),
   COG('\uf013'),
   COGS('\uf085'),
   COINS('\uf51e'),
   COLUMNS('\uf0db'),
   COMMENT('\uf075'),
   COMMENT_ALT('\uf27a'),
   COMMENT_DOLLAR('\uf651'),
   COMMENT_DOTS('\uf4ad'),
   COMMENT_MEDICAL('\uf7f5'),
   COMMENT_SLASH('\uf4b3'),
   COMMENTS('\uf086'),
   COMMENTS_DOLLAR('\uf653'),
   COMPACT_DISC('\uf51f'),
   COMPASS('\uf14e'),
   COMPRESS('\uf066'),
   COMPRESS_ALT('\uf422'),
   COMPRESS_ARROWS_ALT('\uf78c'),
   CONCIERGE_BELL('\uf562'),
   COOKIE('\uf563'),
   COOKIE_BITE('\uf564'),
   COPY('\uf0c5'),
   COPYRIGHT('\uf1f9'),
   COUCH('\uf4b8'),
   CREDIT_CARD('\uf09d'),
   CROP('\uf125'),
   CROP_ALT('\uf565'),
   CROSS('\uf654'),
   CROSSHAIRS('\uf05b'),
   CROW('\uf520'),
   CROWN('\uf521'),
   CRUTCH('\uf7f7'),
   CUBE('\uf1b2'),
   CUBES('\uf1b3'),
   CUT('\uf0c4'),
   DATABASE('\uf1c0'),
   DEAF('\uf2a4'),
   DEMOCRAT('\uf747'),
   DESKTOP('\uf108'),
   DHARMACHAKRA('\uf655'),
   DIAGNOSES('\uf470'),
   DICE('\uf522'),
   DICE_D20('\uf6cf'),
   DICE_D6('\uf6d1'),
   DICE_FIVE('\uf523'),
   DICE_FOUR('\uf524'),
   DICE_ONE('\uf525'),
   DICE_SIX('\uf526'),
   DICE_THREE('\uf527'),
   DICE_TWO('\uf528'),
   DIGITAL_TACHOGRAPH('\uf566'),
   DIRECTIONS('\uf5eb'),
   DIVIDE('\uf529'),
   DIZZY('\uf567'),
   DNA('\uf471'),
   DOG('\uf6d3'),
   DOLLAR_SIGN('\uf155'),
   DOLLY('\uf472'),
   DOLLY_FLATBED('\uf474'),
   DONATE('\uf4b9'),
   DOOR_CLOSED('\uf52a'),
   DOOR_OPEN('\uf52b'),
   DOT_CIRCLE('\uf192'),
   DOVE('\uf4ba'),
   DOWNLOAD('\uf019'),
   DRAFTING_COMPASS('\uf568'),
   DRAGON('\uf6d5'),
   DRAW_POLYGON('\uf5ee'),
   DRUM('\uf569'),
   DRUM_STEELPAN('\uf56a'),
   DRUMSTICK_BITE('\uf6d7'),
   DUMBBELL('\uf44b'),
   DUMPSTER('\uf793'),
   DUMPSTER_FIRE('\uf794'),
   DUNGEON('\uf6d9'),
   EDIT('\uf044'),
   EGG('\uf7fb'),
   EJECT('\uf052'),
   ELLIPSIS_H('\uf141'),
   ELLIPSIS_V('\uf142'),
   ENVELOPE('\uf0e0'),
   ENVELOPE_OPEN('\uf2b6'),
   ENVELOPE_OPEN_TEXT('\uf658'),
   ENVELOPE_SQUARE('\uf199'),
   EQUALS('\uf52c'),
   ERASER('\uf12d'),
   ETHERNET('\uf796'),
   EURO_SIGN('\uf153'),
   EXCHANGE_ALT('\uf362'),
   EXCLAMATION('\uf12a'),
   EXCLAMATION_CIRCLE('\uf06a'),
   EXCLAMATION_TRIANGLE('\uf071'),
   EXPAND('\uf065'),
   EXPAND_ALT('\uf424'),
   EXPAND_ARROWS_ALT('\uf31e'),
   EXTERNAL_LINK_ALT('\uf35d'),
   EXTERNAL_LINK_SQUARE_ALT('\uf360'),
   EYE('\uf06e'),
   EYE_DROPPER('\uf1fb'),
   EYE_SLASH('\uf070'),
   FAN('\uf863'),
   FAST_BACKWARD('\uf049'),
   FAST_FORWARD('\uf050'),
   FAX('\uf1ac'),
   FEATHER('\uf52d'),
   FEATHER_ALT('\uf56b'),
   FEMALE('\uf182'),
   FIGHTER_JET('\uf0fb'),
   FILE('\uf15b'),
   FILE_ALT('\uf15c'),
   FILE_ARCHIVE('\uf1c6'),
   FILE_AUDIO('\uf1c7'),
   FILE_CODE('\uf1c9'),
   FILE_CONTRACT('\uf56c'),
   FILE_CSV('\uf6dd'),
   FILE_DOWNLOAD('\uf56d'),
   FILE_EXCEL('\uf1c3'),
   FILE_EXPORT('\uf56e'),
   FILE_IMAGE('\uf1c5'),
   FILE_IMPORT('\uf56f'),
   FILE_INVOICE('\uf570'),
   FILE_INVOICE_DOLLAR('\uf571'),
   FILE_MEDICAL('\uf477'),
   FILE_MEDICAL_ALT('\uf478'),
   FILE_PDF('\uf1c1'),
   FILE_POWERPOINT('\uf1c4'),
   FILE_PRESCRIPTION('\uf572'),
   FILE_SIGNATURE('\uf573'),
   FILE_UPLOAD('\uf574'),
   FILE_VIDEO('\uf1c8'),
   FILE_WORD('\uf1c2'),
   FILL('\uf575'),
   FILL_DRIP('\uf576'),
   FILM('\uf008'),
   FILTER('\uf0b0'),
   FINGERPRINT('\uf577'),
   FIRE('\uf06d'),
   FIRE_ALT('\uf7e4'),
   FIRE_EXTINGUISHER('\uf134'),
   FIRST_AID('\uf479'),
   FISH('\uf578'),
   FIST_RAISED('\uf6de'),
   FLAG('\uf024'),
   FLAG_CHECKERED('\uf11e'),
   FLAG_USA('\uf74d'),
   FLASK('\uf0c3'),
   FLUSHED('\uf579'),
   FOLDER('\uf07b'),
   FOLDER_MINUS('\uf65d'),
   FOLDER_OPEN('\uf07c'),
   FOLDER_PLUS('\uf65e'),
   FONT('\uf031'),
   FONT_AWESOME_LOGO_FULL('\uf4e6'),
   FOOTBALL_BALL('\uf44e'),
   FORWARD('\uf04e'),
   FROG('\uf52e'),
   FROWN('\uf119'),
   FROWN_OPEN('\uf57a'),
   FUNNEL_DOLLAR('\uf662'),
   FUTBOL('\uf1e3'),
   GAMEPAD('\uf11b'),
   GAS_PUMP('\uf52f'),
   GAVEL('\uf0e3'),
   GEM('\uf3a5'),
   GENDERLESS('\uf22d'),
   GHOST('\uf6e2'),
   GIFT('\uf06b'),
   GIFTS('\uf79c'),
   GLASS_CHEERS('\uf79f'),
   GLASS_MARTINI('\uf000'),
   GLASS_MARTINI_ALT('\uf57b'),
   GLASS_WHISKEY('\uf7a0'),
   GLASSES('\uf530'),
   GLOBE('\uf0ac'),
   GLOBE_AFRICA('\uf57c'),
   GLOBE_AMERICAS('\uf57d'),
   GLOBE_ASIA('\uf57e'),
   GLOBE_EUROPE('\uf7a2'),
   GOLF_BALL('\uf450'),
   GOPURAM('\uf664'),
   GRADUATION_CAP('\uf19d'),
   GREATER_THAN('\uf531'),
   GREATER_THAN_EQUAL('\uf532'),
   GRIMACE('\uf57f'),
   GRIN('\uf580'),
   GRIN_ALT('\uf581'),
   GRIN_BEAM('\uf582'),
   GRIN_BEAM_SWEAT('\uf583'),
   GRIN_HEARTS('\uf584'),
   GRIN_SQUINT('\uf585'),
   GRIN_SQUINT_TEARS('\uf586'),
   GRIN_STARS('\uf587'),
   GRIN_TEARS('\uf588'),
   GRIN_TONGUE('\uf589'),
   GRIN_TONGUE_SQUINT('\uf58a'),
   GRIN_TONGUE_WINK('\uf58b'),
   GRIN_WINK('\uf58c'),
   GRIP_HORIZONTAL('\uf58d'),
   GRIP_LINES('\uf7a4'),
   GRIP_LINES_VERTICAL('\uf7a5'),
   GRIP_VERTICAL('\uf58e'),
   GUITAR('\uf7a6'),
   H_SQUARE('\uf0fd'),
   HAMBURGER('\uf805'),
   HAMMER('\uf6e3'),
   HAMSA('\uf665'),
   HAND_HOLDING('\uf4bd'),
   HAND_HOLDING_HEART('\uf4be'),
   HAND_HOLDING_USD('\uf4c0'),
   HAND_LIZARD('\uf258'),
   HAND_MIDDLE_FINGER('\uf806'),
   HAND_PAPER('\uf256'),
   HAND_PEACE('\uf25b'),
   HAND_POINT_DOWN('\uf0a7'),
   HAND_POINT_LEFT('\uf0a5'),
   HAND_POINT_RIGHT('\uf0a4'),
   HAND_POINT_UP('\uf0a6'),
   HAND_POINTER('\uf25a'),
   HAND_ROCK('\uf255'),
   HAND_SCISSORS('\uf257'),
   HAND_SPOCK('\uf259'),
   HANDS('\uf4c2'),
   HANDS_HELPING('\uf4c4'),
   HANDSHAKE('\uf2b5'),
   HANUKIAH('\uf6e6'),
   HARD_HAT('\uf807'),
   HASHTAG('\uf292'),
   HAT_COWBOY('\uf8c0'),
   HAT_COWBOY_SIDE('\uf8c1'),
   HAT_WIZARD('\uf6e8'),
   HDD('\uf0a0'),
   HEADING('\uf1dc'),
   HEADPHONES('\uf025'),
   HEADPHONES_ALT('\uf58f'),
   HEADSET('\uf590'),
   HEART('\uf004'),
   HEART_BROKEN('\uf7a9'),
   HEARTBEAT('\uf21e'),
   HELICOPTER('\uf533'),
   HIGHLIGHTER('\uf591'),
   HIKING('\uf6ec'),
   HIPPO('\uf6ed'),
   HISTORY('\uf1da'),
   HOCKEY_PUCK('\uf453'),
   HOLLY_BERRY('\uf7aa'),
   HOME('\uf015'),
   HORSE('\uf6f0'),
   HORSE_HEAD('\uf7ab'),
   HOSPITAL('\uf0f8'),
   HOSPITAL_ALT('\uf47d'),
   HOSPITAL_SYMBOL('\uf47e'),
   HOT_TUB('\uf593'),
   HOTDOG('\uf80f'),
   HOTEL('\uf594'),
   HOURGLASS('\uf254'),
   HOURGLASS_END('\uf253'),
   HOURGLASS_HALF('\uf252'),
   HOURGLASS_START('\uf251'),
   HOUSE_DAMAGE('\uf6f1'),
   HRYVNIA('\uf6f2'),
   I_CURSOR('\uf246'),
   ICE_CREAM('\uf810'),
   ICICLES('\uf7ad'),
   ICONS('\uf86d'),
   ID_BADGE('\uf2c1'),
   ID_CARD('\uf2c2'),
   ID_CARD_ALT('\uf47f'),
   IGLOO('\uf7ae'),
   IMAGE('\uf03e'),
   IMAGES('\uf302'),
   INBOX('\uf01c'),
   INDENT('\uf03c'),
   INDUSTRY('\uf275'),
   INFINITY('\uf534'),
   INFO('\uf129'),
   INFO_CIRCLE('\uf05a'),
   ITALIC('\uf033'),
   JEDI('\uf669'),
   JOINT('\uf595'),
   JOURNAL_WHILLS('\uf66a'),
   KAABA('\uf66b'),
   KEY('\uf084'),
   KEYBOARD('\uf11c'),
   KHANDA('\uf66d'),
   KISS('\uf596'),
   KISS_BEAM('\uf597'),
   KISS_WINK_HEART('\uf598'),
   KIWI_BIRD('\uf535'),
   LANDMARK('\uf66f'),
   LANGUAGE('\uf1ab'),
   LAPTOP('\uf109'),
   LAPTOP_CODE('\uf5fc'),
   LAPTOP_MEDICAL('\uf812'),
   LAUGH('\uf599'),
   LAUGH_BEAM('\uf59a'),
   LAUGH_SQUINT('\uf59b'),
   LAUGH_WINK('\uf59c'),
   LAYER_GROUP('\uf5fd'),
   LEAF('\uf06c'),
   LEMON('\uf094'),
   LESS_THAN('\uf536'),
   LESS_THAN_EQUAL('\uf537'),
   LEVEL_DOWN_ALT('\uf3be'),
   LEVEL_UP_ALT('\uf3bf'),
   LIFE_RING('\uf1cd'),
   LIGHTBULB('\uf0eb'),
   LINK('\uf0c1'),
   LIRA_SIGN('\uf195'),
   LIST('\uf03a'),
   LIST_ALT('\uf022'),
   LIST_OL('\uf0cb'),
   LIST_UL('\uf0ca'),
   LOCATION_ARROW('\uf124'),
   LOCK('\uf023'),
   LOCK_OPEN('\uf3c1'),
   LONG_ARROW_ALT_DOWN('\uf309'),
   LONG_ARROW_ALT_LEFT('\uf30a'),
   LONG_ARROW_ALT_RIGHT('\uf30b'),
   LONG_ARROW_ALT_UP('\uf30c'),
   LOW_VISION('\uf2a8'),
   LUGGAGE_CART('\uf59d'),
   MAGIC('\uf0d0'),
   MAGNET('\uf076'),
   MAIL_BULK('\uf674'),
   MALE('\uf183'),
   MAP('\uf279'),
   MAP_MARKED('\uf59f'),
   MAP_MARKED_ALT('\uf5a0'),
   MAP_MARKER('\uf041'),
   MAP_MARKER_ALT('\uf3c5'),
   MAP_PIN('\uf276'),
   MAP_SIGNS('\uf277'),
   MARKER('\uf5a1'),
   MARS('\uf222'),
   MARS_DOUBLE('\uf227'),
   MARS_STROKE('\uf229'),
   MARS_STROKE_H('\uf22b'),
   MARS_STROKE_V('\uf22a'),
   MASK('\uf6fa'),
   MEDAL('\uf5a2'),
   MEDKIT('\uf0fa'),
   MEH('\uf11a'),
   MEH_BLANK('\uf5a4'),
   MEH_ROLLING_EYES('\uf5a5'),
   MEMORY('\uf538'),
   MENORAH('\uf676'),
   MERCURY('\uf223'),
   METEOR('\uf753'),
   MICROCHIP('\uf2db'),
   MICROPHONE('\uf130'),
   MICROPHONE_ALT('\uf3c9'),
   MICROPHONE_ALT_SLASH('\uf539'),
   MICROPHONE_SLASH('\uf131'),
   MICROSCOPE('\uf610'),
   MINUS('\uf068'),
   MINUS_CIRCLE('\uf056'),
   MINUS_SQUARE('\uf146'),
   MITTEN('\uf7b5'),
   MOBILE('\uf10b'),
   MOBILE_ALT('\uf3cd'),
   MONEY_BILL('\uf0d6'),
   MONEY_BILL_ALT('\uf3d1'),
   MONEY_BILL_WAVE('\uf53a'),
   MONEY_BILL_WAVE_ALT('\uf53b'),
   MONEY_CHECK('\uf53c'),
   MONEY_CHECK_ALT('\uf53d'),
   MONUMENT('\uf5a6'),
   MOON('\uf186'),
   MORTAR_PESTLE('\uf5a7'),
   MOSQUE('\uf678'),
   MOTORCYCLE('\uf21c'),
   MOUNTAIN('\uf6fc'),
   MOUSE('\uf8cc'),
   MOUSE_POINTER('\uf245'),
   MUG_HOT('\uf7b6'),
   MUSIC('\uf001'),
   NETWORK_WIRED('\uf6ff'),
   NEUTER('\uf22c'),
   NEWSPAPER('\uf1ea'),
   NOT_EQUAL('\uf53e'),
   NOTES_MEDICAL('\uf481'),
   OBJECT_GROUP('\uf247'),
   OBJECT_UNGROUP('\uf248'),
   OIL_CAN('\uf613'),
   OM('\uf679'),
   OTTER('\uf700'),
   OUTDENT('\uf03b'),
   PAGER('\uf815'),
   PAINT_BRUSH('\uf1fc'),
   PAINT_ROLLER('\uf5aa'),
   PALETTE('\uf53f'),
   PALLET('\uf482'),
   PAPER_PLANE('\uf1d8'),
   PAPERCLIP('\uf0c6'),
   PARACHUTE_BOX('\uf4cd'),
   PARAGRAPH('\uf1dd'),
   PARKING('\uf540'),
   PASSPORT('\uf5ab'),
   PASTAFARIANISM('\uf67b'),
   PASTE('\uf0ea'),
   PAUSE('\uf04c'),
   PAUSE_CIRCLE('\uf28b'),
   PAW('\uf1b0'),
   PEACE('\uf67c'),
   PEN('\uf304'),
   PEN_ALT('\uf305'),
   PEN_FANCY('\uf5ac'),
   PEN_NIB('\uf5ad'),
   PEN_SQUARE('\uf14b'),
   PENCIL_ALT('\uf303'),
   PENCIL_RULER('\uf5ae'),
   PEOPLE_CARRY('\uf4ce'),
   PEPPER_HOT('\uf816'),
   PERCENT('\uf295'),
   PERCENTAGE('\uf541'),
   PERSON_BOOTH('\uf756'),
   PHONE('\uf095'),
   PHONE_ALT('\uf879'),
   PHONE_SLASH('\uf3dd'),
   PHONE_SQUARE('\uf098'),
   PHONE_SQUARE_ALT('\uf87b'),
   PHONE_VOLUME('\uf2a0'),
   PHOTO_VIDEO('\uf87c'),
   PIGGY_BANK('\uf4d3'),
   PILLS('\uf484'),
   PIZZA_SLICE('\uf818'),
   PLACE_OF_WORSHIP('\uf67f'),
   PLANE('\uf072'),
   PLANE_ARRIVAL('\uf5af'),
   PLANE_DEPARTURE('\uf5b0'),
   PLAY('\uf04b'),
   PLAY_CIRCLE('\uf144'),
   PLUG('\uf1e6'),
   PLUS('\uf067'),
   PLUS_CIRCLE('\uf055'),
   PLUS_SQUARE('\uf0fe'),
   PODCAST('\uf2ce'),
   POLL('\uf681'),
   POLL_H('\uf682'),
   POO('\uf2fe'),
   POO_STORM('\uf75a'),
   POOP('\uf619'),
   PORTRAIT('\uf3e0'),
   POUND_SIGN('\uf154'),
   POWER_OFF('\uf011'),
   PRAY('\uf683'),
   PRAYING_HANDS('\uf684'),
   PRESCRIPTION('\uf5b1'),
   PRESCRIPTION_BOTTLE('\uf485'),
   PRESCRIPTION_BOTTLE_ALT('\uf486'),
   PRINT('\uf02f'),
   PROCEDURES('\uf487'),
   PROJECT_DIAGRAM('\uf542'),
   PUZZLE_PIECE('\uf12e'),
   QRCODE('\uf029'),
   QUESTION('\uf128'),
   QUESTION_CIRCLE('\uf059'),
   QUIDDITCH('\uf458'),
   QUOTE_LEFT('\uf10d'),
   QUOTE_RIGHT('\uf10e'),
   QURAN('\uf687'),
   RADIATION('\uf7b9'),
   RADIATION_ALT('\uf7ba'),
   RAINBOW('\uf75b'),
   RANDOM('\uf074'),
   RECEIPT('\uf543'),
   RECORD_VINYL('\uf8d9'),
   RECYCLE('\uf1b8'),
   REDO('\uf01e'),
   REDO_ALT('\uf2f9'),
   REGISTERED('\uf25d'),
   REMOVE_FORMAT('\uf87d'),
   REPLY('\uf3e5'),
   REPLY_ALL('\uf122'),
   REPUBLICAN('\uf75e'),
   RESTROOM('\uf7bd'),
   RETWEET('\uf079'),
   RIBBON('\uf4d6'),
   RING('\uf70b'),
   ROAD('\uf018'),
   ROBOT('\uf544'),
   ROCKET('\uf135'),
   ROUTE('\uf4d7'),
   RSS('\uf09e'),
   RSS_SQUARE('\uf143'),
   RUBLE_SIGN('\uf158'),
   RULER('\uf545'),
   RULER_COMBINED('\uf546'),
   RULER_HORIZONTAL('\uf547'),
   RULER_VERTICAL('\uf548'),
   RUNNING('\uf70c'),
   RUPEE_SIGN('\uf156'),
   SAD_CRY('\uf5b3'),
   SAD_TEAR('\uf5b4'),
   SATELLITE('\uf7bf'),
   SATELLITE_DISH('\uf7c0'),
   SAVE('\uf0c7'),
   SCHOOL('\uf549'),
   SCREWDRIVER('\uf54a'),
   SCROLL('\uf70e'),
   SD_CARD('\uf7c2'),
   SEARCH('\uf002'),
   SEARCH_DOLLAR('\uf688'),
   SEARCH_LOCATION('\uf689'),
   SEARCH_MINUS('\uf010'),
   SEARCH_PLUS('\uf00e'),
   SEEDLING('\uf4d8'),
   SERVER('\uf233'),
   SHAPES('\uf61f'),
   SHARE('\uf064'),
   SHARE_ALT('\uf1e0'),
   SHARE_ALT_SQUARE('\uf1e1'),
   SHARE_SQUARE('\uf14d'),
   SHEKEL_SIGN('\uf20b'),
   SHIELD_ALT('\uf3ed'),
   SHIP('\uf21a'),
   SHIPPING_FAST('\uf48b'),
   SHOE_PRINTS('\uf54b'),
   SHOPPING_BAG('\uf290'),
   SHOPPING_BASKET('\uf291'),
   SHOPPING_CART('\uf07a'),
   SHOWER('\uf2cc'),
   SHUTTLE_VAN('\uf5b6'),
   SIGN('\uf4d9'),
   SIGN_IN_ALT('\uf2f6'),
   SIGN_LANGUAGE('\uf2a7'),
   SIGN_OUT_ALT('\uf2f5'),
   SIGNAL('\uf012'),
   SIGNATURE('\uf5b7'),
   SIM_CARD('\uf7c4'),
   SITEMAP('\uf0e8'),
   SKATING('\uf7c5'),
   SKIING('\uf7c9'),
   SKIING_NORDIC('\uf7ca'),
   SKULL('\uf54c'),
   SKULL_CROSSBONES('\uf714'),
   SLASH('\uf715'),
   SLEIGH('\uf7cc'),
   SLIDERS_H('\uf1de'),
   SMILE('\uf118'),
   SMILE_BEAM('\uf5b8'),
   SMILE_WINK('\uf4da'),
   SMOG('\uf75f'),
   SMOKING('\uf48d'),
   SMOKING_BAN('\uf54d'),
   SMS('\uf7cd'),
   SNOWBOARDING('\uf7ce'),
   SNOWFLAKE('\uf2dc'),
   SNOWMAN('\uf7d0'),
   SNOWPLOW('\uf7d2'),
   SOCKS('\uf696'),
   SOLAR_PANEL('\uf5ba'),
   SORT('\uf0dc'),
   SORT_ALPHA_DOWN('\uf15d'),
   SORT_ALPHA_DOWN_ALT('\uf881'),
   SORT_ALPHA_UP('\uf15e'),
   SORT_ALPHA_UP_ALT('\uf882'),
   SORT_AMOUNT_DOWN('\uf160'),
   SORT_AMOUNT_DOWN_ALT('\uf884'),
   SORT_AMOUNT_UP('\uf161'),
   SORT_AMOUNT_UP_ALT('\uf885'),
   SORT_DOWN('\uf0dd'),
   SORT_NUMERIC_DOWN('\uf162'),
   SORT_NUMERIC_DOWN_ALT('\uf886'),
   SORT_NUMERIC_UP('\uf163'),
   SORT_NUMERIC_UP_ALT('\uf887'),
   SORT_UP('\uf0de'),
   SPA('\uf5bb'),
   SPACE_SHUTTLE('\uf197'),
   SPELL_CHECK('\uf891'),
   SPIDER('\uf717'),
   SPINNER('\uf110'),
   SPLOTCH('\uf5bc'),
   SPRAY_CAN('\uf5bd'),
   SQUARE('\uf0c8'),
   SQUARE_FULL('\uf45c'),
   SQUARE_ROOT_ALT('\uf698'),
   STAMP('\uf5bf'),
   STAR('\uf005'),
   STAR_AND_CRESCENT('\uf699'),
   STAR_HALF('\uf089'),
   STAR_HALF_ALT('\uf5c0'),
   STAR_OF_DAVID('\uf69a'),
   STAR_OF_LIFE('\uf621'),
   STEP_BACKWARD('\uf048'),
   STEP_FORWARD('\uf051'),
   STETHOSCOPE('\uf0f1'),
   STICKY_NOTE('\uf249'),
   STOP('\uf04d'),
   STOP_CIRCLE('\uf28d'),
   STOPWATCH('\uf2f2'),
   STORE('\uf54e'),
   STORE_ALT('\uf54f'),
   STREAM('\uf550'),
   STREET_VIEW('\uf21d'),
   STRIKETHROUGH('\uf0cc'),
   STROOPWAFEL('\uf551'),
   SUBSCRIPT('\uf12c'),
   SUBWAY('\uf239'),
   SUITCASE('\uf0f2'),
   SUITCASE_ROLLING('\uf5c1'),
   SUN('\uf185'),
   SUPERSCRIPT('\uf12b'),
   SURPRISE('\uf5c2'),
   SWATCHBOOK('\uf5c3'),
   SWIMMER('\uf5c4'),
   SWIMMING_POOL('\uf5c5'),
   SYNAGOGUE('\uf69b'),
   SYNC('\uf021'),
   SYNC_ALT('\uf2f1'),
   SYRINGE('\uf48e'),
   TABLE('\uf0ce'),
   TABLE_TENNIS('\uf45d'),
   TABLET('\uf10a'),
   TABLET_ALT('\uf3fa'),
   TABLETS('\uf490'),
   TACHOMETER_ALT('\uf3fd'),
   TAG('\uf02b'),
   TAGS('\uf02c'),
   TAPE('\uf4db'),
   TASKS('\uf0ae'),
   TAXI('\uf1ba'),
   TEETH('\uf62e'),
   TEETH_OPEN('\uf62f'),
   TEMPERATURE_HIGH('\uf769'),
   TEMPERATURE_LOW('\uf76b'),
   TENGE('\uf7d7'),
   TERMINAL('\uf120'),
   TEXT_HEIGHT('\uf034'),
   TEXT_WIDTH('\uf035'),
   TH('\uf00a'),
   TH_LARGE('\uf009'),
   TH_LIST('\uf00b'),
   THEATER_MASKS('\uf630'),
   THERMOMETER('\uf491'),
   THERMOMETER_EMPTY('\uf2cb'),
   THERMOMETER_FULL('\uf2c7'),
   THERMOMETER_HALF('\uf2c9'),
   THERMOMETER_QUARTER('\uf2ca'),
   THERMOMETER_THREE_QUARTERS('\uf2c8'),
   THUMBS_DOWN('\uf165'),
   THUMBS_UP('\uf164'),
   THUMBTACK('\uf08d'),
   TICKET_ALT('\uf3ff'),
   TIMES('\uf00d'),
   TIMES_CIRCLE('\uf057'),
   TINT('\uf043'),
   TINT_SLASH('\uf5c7'),
   TIRED('\uf5c8'),
   TOGGLE_OFF('\uf204'),
   TOGGLE_ON('\uf205'),
   TOILET('\uf7d8'),
   TOILET_PAPER('\uf71e'),
   TOOLBOX('\uf552'),
   TOOLS('\uf7d9'),
   TOOTH('\uf5c9'),
   TORAH('\uf6a0'),
   TORII_GATE('\uf6a1'),
   TRACTOR('\uf722'),
   TRADEMARK('\uf25c'),
   TRAFFIC_LIGHT('\uf637'),
   TRAILER('\uf941'),
   TRAIN('\uf238'),
   TRAM('\uf7da'),
   TRANSGENDER('\uf224'),
   TRANSGENDER_ALT('\uf225'),
   TRASH('\uf1f8'),
   TRASH_ALT('\uf2ed'),
   TRASH_RESTORE('\uf829'),
   TRASH_RESTORE_ALT('\uf82a'),
   TREE('\uf1bb'),
   TROPHY('\uf091'),
   TRUCK('\uf0d1'),
   TRUCK_LOADING('\uf4de'),
   TRUCK_MONSTER('\uf63b'),
   TRUCK_MOVING('\uf4df'),
   TRUCK_PICKUP('\uf63c'),
   TSHIRT('\uf553'),
   TTY('\uf1e4'),
   TV('\uf26c'),
   UMBRELLA('\uf0e9'),
   UMBRELLA_BEACH('\uf5ca'),
   UNDERLINE('\uf0cd'),
   UNDO('\uf0e2'),
   UNDO_ALT('\uf2ea'),
   UNIVERSAL_ACCESS('\uf29a'),
   UNIVERSITY('\uf19c'),
   UNLINK('\uf127'),
   UNLOCK('\uf09c'),
   UNLOCK_ALT('\uf13e'),
   UPLOAD('\uf093'),
   USER('\uf007'),
   USER_ALT('\uf406'),
   USER_ALT_SLASH('\uf4fa'),
   USER_ASTRONAUT('\uf4fb'),
   USER_CHECK('\uf4fc'),
   USER_CIRCLE('\uf2bd'),
   USER_CLOCK('\uf4fd'),
   USER_COG('\uf4fe'),
   USER_EDIT('\uf4ff'),
   USER_FRIENDS('\uf500'),
   USER_GRADUATE('\uf501'),
   USER_INJURED('\uf728'),
   USER_LOCK('\uf502'),
   USER_MD('\uf0f0'),
   USER_MINUS('\uf503'),
   USER_NINJA('\uf504'),
   USER_NURSE('\uf82f'),
   USER_PLUS('\uf234'),
   USER_SECRET('\uf21b'),
   USER_SHIELD('\uf505'),
   USER_SLASH('\uf506'),
   USER_TAG('\uf507'),
   USER_TIE('\uf508'),
   USER_TIMES('\uf235'),
   USERS('\uf0c0'),
   USERS_COG('\uf509'),
   UTENSIL_SPOON('\uf2e5'),
   UTENSILS('\uf2e7'),
   VECTOR_SQUARE('\uf5cb'),
   VENUS('\uf221'),
   VENUS_DOUBLE('\uf226'),
   VENUS_MARS('\uf228'),
   VIAL('\uf492'),
   VIALS('\uf493'),
   VIDEO('\uf03d'),
   VIDEO_SLASH('\uf4e2'),
   VIHARA('\uf6a7'),
   VOICEMAIL('\uf897'),
   VOLLEYBALL_BALL('\uf45f'),
   VOLUME_DOWN('\uf027'),
   VOLUME_MUTE('\uf6a9'),
   VOLUME_OFF('\uf026'),
   VOLUME_UP('\uf028'),
   VOTE_YEA('\uf772'),
   VR_CARDBOARD('\uf729'),
   WALKING('\uf554'),
   WALLET('\uf555'),
   WAREHOUSE('\uf494'),
   WATER('\uf773'),
   WAVE_SQUARE('\uf83e'),
   WEIGHT('\uf496'),
   WEIGHT_HANGING('\uf5cd'),
   WHEELCHAIR('\uf193'),
   WIFI('\uf1eb'),
   WIND('\uf72e'),
   WINDOW_CLOSE('\uf410'),
   WINDOW_MAXIMIZE('\uf2d0'),
   WINDOW_MINIMIZE('\uf2d1'),
   WINDOW_RESTORE('\uf2d2'),
   WINE_BOTTLE('\uf72f'),
   WINE_GLASS('\uf4e3'),
   WINE_GLASS_ALT('\uf5ce'),
   WON_SIGN('\uf159'),
   WRENCH('\uf0ad'),
   X_RAY('\uf497'),
   YEN_SIGN('\uf157'),
   YIN_YANG('\uf6ad');

   private static FontIcon fontIcon = new FontIcon(Resources.fromClasspath("com/gengoai/swing/fa-solid-900.ttf"));
   private final char icon;

   FontAwesome(char icon) {
      this.icon = icon;
   }

   public final Icon create(float size, Color foreground, Color background) {
      return fontIcon.createIcon(Character.toString(icon), size, foreground, background);
   }

   public final Icon create(float size, Color foreground) {
      return fontIcon.createIcon(Character.toString(icon), size, foreground);
   }

   public final Icon create(float size) {
      return fontIcon.createIcon(Character.toString(icon), size);
   }

}//END OF FontAwesome
