package com.gengoai.config;

import com.gengoai.parsing.*;
import java.util.*;
import java.io.*;



%%

%class ConfigScanner
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
public enum ConfigTokenType implements ParserTokenType {
    IMPORT,
    COMMENT,
    NULL,
    BOOLEAN,
    STRING,
    APPEND_PROPERTY,
    EQUAL_PROPERTY,
    BEGIN_OBJECT,
    END_OBJECT,
    KEY,
    MAP,
    BEGIN_ARRAY,
    END_ARRAY,
    VALUE_SEPARATOR,
    KEY_VALUE_SEPARATOR,
    BEAN;

    @Override
    public boolean isInstance(ParserTokenType tokenType) {
      return tokenType == this;
    }
}
%}

string = (\\\"|[^\"])*
safestring = [_a-zA-Z]+([_a-zA-Z0-9\.]+)*
comment = \#[^\r\n]*
number = -?(0|[1-9][0-9]*)(\.[0-9]+)?([eE][+-]?[0-9]+)?

%%

"@type" { return new ParserToken(new String(yytext()), ConfigTokenType.KEY);}
"@constructor" { return new ParserToken(new String(yytext()), ConfigTokenType.KEY);}
"@import" { return new ParserToken(new String(yytext()), ConfigTokenType.IMPORT);}
"true"|"false" { return  new ParserToken(new String(yytext()), ConfigTokenType.BOOLEAN);}
"null" { return new ParserToken(null,ConfigTokenType.NULL);}
{comment} { return new ParserToken(new String(yytext()), ConfigTokenType.COMMENT); }
"," { return new ParserToken(new String(yytext()), ConfigTokenType.VALUE_SEPARATOR); }
// {map_operator} { return new ParserToken(new String(yytext()), ConfigTokenType.MAP);}
"[" { return new ParserToken(new String(yytext()), ConfigTokenType.BEGIN_ARRAY); }
"]" { return new ParserToken(new String(yytext()), ConfigTokenType.END_ARRAY); }
"@{"{safestring}"}" { return new ParserToken(new String(yytext()), ConfigTokenType.BEAN); }
"{" { return new ParserToken(new String(yytext()), ConfigTokenType.BEGIN_OBJECT);}
"}" { return new ParserToken(new String(yytext()), ConfigTokenType.END_OBJECT);}
":" { return new ParserToken(new String(yytext()), ConfigTokenType.KEY_VALUE_SEPARATOR);}
"=" { return new ParserToken(new String(yytext()), ConfigTokenType.EQUAL_PROPERTY);}
"+=" { return new ParserToken(new String(yytext()), ConfigTokenType.APPEND_PROPERTY);}
\"{string}\" { return new ParserToken(new String(yytext()).substring(1,yytext().length()-1), ConfigTokenType.STRING);}
{safestring} { return new ParserToken(new String(yytext()), ConfigTokenType.KEY);}
{number} { return new ParserToken(new String(yytext()), ConfigTokenType.STRING);}

[ \t\r\n\f] { /* ignore white space. */ }
. { throw new com.gengoai.parsing.ParseException("Illegal character: "+yytext()+"\" at line: " + yyline + " char offset: " + yychar + " state: " + yystate()); }