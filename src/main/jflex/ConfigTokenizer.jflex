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

package com.gengoai.config;

import com.gengoai.parsing.*;
import java.util.*;
import java.io.*;

%%

%class ConfigTokenizer
%public
%unicode
%type com.gengoai.parsing.ParserToken
%function next
%pack
%char
%line
%yylexthrow{
    com.gengoai.parsing.ParseException
%yylexthrow}

%{



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
%}

WHITESPACE = [\p{Z}\t]

ALPHANUM = ([:letter:]|[:digit:])+

PROPERTY = ("_"|{ALPHANUM}+([".""_"]{ALPHANUM})*)

ASSIGNMENT_OPERATOR = [:=]

APPEND_OPERATOR = "+" "="

START_SECTION = "{"

END_SECTION = "}"

ESCAPED = "\\" .

VARIABLE = "$" "{" {PROPERTY} "}"

BEAN = "@" "{" {PROPERTY} "}"


SECTION_VALUE = ( [^\r\n}#] | {ESCAPED} | {VARIABLE} | {BEAN})+

VALUE= ( [^\r\n#] | {ESCAPED} | {VARIABLE} | {BEAN} )*

MLINEVALUE = {VALUE} ([^\\] "\\" {NEWLINE} {WHITESPACE}* {VALUE})*

SECTION_MLINEVALUE = {SECTION_VALUE} ([^\\] "\\" {NEWLINE} {WHITESPACE}* {SECTION_VALUE})*

COMMENT = "#" .*

NEWLINE=\r?\n

IMPORT = "@import"
SCRIPT = "@script"

IMPORT_SCRIPT = [^\n]+

%state VALUE
%state SECTION_PROPERTY
%state SECTION_VALUE
%state IMPORT
%state SCRIPT
%state APPENDER
%state START_SECTION
%state START_VALUE
%state START_SECTION_VALUE

%%

<IMPORT,SCRIPT>{
    {IMPORT_SCRIPT}     {yybegin(YYINITIAL); return new ParserToken(yytext(),ConfigTokenType.VALUE); }
}

<YYINITIAL>{
     {PROPERTY} / ({WHITESPACE} {NEWLINE}*)* {START_SECTION}        {stack.push(YYINITIAL);yybegin(START_SECTION); return new ParserToken(yytext(),ConfigTokenType.SECTION_HEADER); }
     {PROPERTY} / {WHITESPACE}* {APPEND_OPERATOR}                   {stack.push(YYINITIAL);yybegin(APPENDER); return new ParserToken(yytext(),ConfigTokenType.APPEND_PROPERTY); }
     {PROPERTY} / {WHITESPACE}* {ASSIGNMENT_OPERATOR}               {stack.push(YYINITIAL);yybegin(START_VALUE); return new ParserToken(yytext(),ConfigTokenType.PROPERTY); }
     {SCRIPT}                                                       {yybegin(SCRIPT); return new ParserToken(yytext(),ConfigTokenType.SCRIPT); }
     {IMPORT}                                                       {yybegin(IMPORT); return new ParserToken(yytext(),ConfigTokenType.IMPORT); }
}

<APPENDER> {
    {APPEND_OPERATOR}   {yybegin(VALUE);}
}

<START_VALUE> {
    {ASSIGNMENT_OPERATOR}   {yybegin(VALUE);}
}


<VALUE>{
    {START_SECTION}     {stack.push(YYINITIAL);yybegin(SECTION_PROPERTY);}
    {MLINEVALUE}        {yybegin(stack.pop());  return new ParserToken(yytext(),ConfigTokenType.VALUE); }
}

<START_SECTION> {
    {START_SECTION}                                 {yybegin(SECTION_PROPERTY);}
}

<SECTION_PROPERTY>{
    {PROPERTY} / ({WHITESPACE} {NEWLINE}*)* {START_SECTION}        {stack.push(SECTION_PROPERTY);yybegin(START_SECTION); return new ParserToken(yytext(),ConfigTokenType.SECTION_HEADER); }
    {PROPERTY}/{WHITESPACE}*{ASSIGNMENT_OPERATOR}                  {yybegin(START_SECTION_VALUE); return new ParserToken(yytext(),ConfigTokenType.PROPERTY);  }
    {PROPERTY} / {WHITESPACE}* {APPEND_OPERATOR}                   {stack.push(SECTION_PROPERTY); yybegin(APPENDER); return new ParserToken(yytext(),ConfigTokenType.APPEND_PROPERTY); }
    {END_SECTION}                                                  {yybegin(stack.pop()); return new ParserToken(yytext(),ConfigTokenType.END_SECTION); }
}


<START_SECTION_VALUE> {
    {ASSIGNMENT_OPERATOR}   {yybegin(SECTION_VALUE);}
}

<SECTION_VALUE>{
    {SECTION_MLINEVALUE}                             {yybegin(SECTION_PROPERTY); return new ParserToken(yytext(),ConfigTokenType.VALUE); }
}

{COMMENT}                       {}
{WHITESPACE}                    {}
{NEWLINE}                       {}
[^]                             {throw new com.gengoai.parsing.ParseException("UNEXPECTED TOKEN \"" + yytext() +"\" at line: " + yyline + " char offset: " + yychar + " state: " + yystate());}
