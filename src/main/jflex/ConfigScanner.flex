package com.gengoai.config;

import com.gengoai.parsing.*;
import java.util.*;
import java.io.*;



%%

%class Scanner
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
    NULL,
    BOOLEAN,
    APPEND,
    IMPORT,
    COMMENT,
    SECTION_HEADER,
    KEY,
    MAP,
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

%%

"@type" { return new ParserToken(new String(yytext()), ConfigTokenType.KEY);}
"@constructor" { return new ParserToken(new String(yytext()), ConfigTokenType.KEY);}
"@import" { return new ParserToken(new String(yytext()), ConfigTokenType.IMPORT);}
"true"|"false" { return  new ParserToken(new String(yytext()), ConfigTokenType.BOOLEAN);}
"null" { return new ParserToken(null,ConfigTokenType.NULL);}
{comment} { return new ParserToken(new String(yytext()), ConfigTokenType.COMMENT); }
"," { return new ParserToken(new String(yytext()), CommonTypes.COMMA); }
// {map_operator} { return new ParserToken(new String(yytext()), ConfigTokenType.MAP);}
"[" { return new ParserToken(new String(yytext()), CommonTypes.OPENBRACKET); }
"]" { return new ParserToken(new String(yytext()), CommonTypes.CLOSEBRACKET); }
"@{"{safestring}"}" { return new ParserToken(new String(yytext()), ConfigTokenType.BEAN); }
"{" { return new ParserToken(new String(yytext()), CommonTypes.OPENBRACE);}
"}" { return new ParserToken(new String(yytext()), CommonTypes.CLOSEBRACE);}
":" { return new ParserToken(new String(yytext()), CommonTypes.COLON);}
"=" { return new ParserToken(new String(yytext()), CommonTypes.EQUALS);}
"+=" { return new ParserToken(new String(yytext()), ConfigTokenType.APPEND);}
\"{string}\" { return new ParserToken(new String(yytext()).substring(1,yytext().length()-1), CommonTypes.WORD);}
{safestring} { return new ParserToken(new String(yytext()), ConfigTokenType.KEY);}
[ \t\r\n\f] { /* ignore white space. */ }
. { throw new com.gengoai.parsing.ParseException("Illegal character: "+yytext()+"\" at line: " + yyline + " char offset: " + yychar + " state: " + yystate()); }