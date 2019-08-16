package com.gengoai.config;

import com.gengoai.parsing.v2.*;
import com.gengoai.config.ConfigTokenType;


%%

%class ConfigScanner
%public
%unicode
%type com.gengoai.parsing.v2.ParserToken
%function next
%pack
%char
%line
%yylexthrow{
    com.gengoai.parsing.v2.ParseException
%yylexthrow}


//%{
//public static final class ConfigTokenType  {
//    public static final ParserTokenType IMPORT = ParserTokenType.tokenType("IMPORT");
//    public static final ParserTokenType COMMENT = ParserTokenType.tokenType("COMMENT");
//    public static final ParserTokenType NULL = ParserTokenType.tokenType("NULL");
//    public static final ParserTokenType BOOLEAN = ParserTokenType.tokenType("BOOLEAN");
//    public static final ParserTokenType STRING = ParserTokenType.tokenType("STRING");
//    public static final ParserTokenType APPEND_PROPERTY = ParserTokenType.tokenType("APPEND_PROPERTY");
//    public static final ParserTokenType EQUAL_PROPERTY = ParserTokenType.tokenType("EQUAL_PROPERTY");
//    public static final ParserTokenType BEGIN_OBJECT = ParserTokenType.tokenType("BEGIN_OBJECT");
//    public static final ParserTokenType END_OBJECT = ParserTokenType.tokenType("END_OBJECT");
//    public static final ParserTokenType KEY = ParserTokenType.tokenType("KEY");
//    public static final ParserTokenType MAP = ParserTokenType.tokenType("MAP");
//    public static final ParserTokenType BEGIN_ARRAY = ParserTokenType.tokenType("BEGIN_ARRAY");
//    public static final ParserTokenType END_ARRAY = ParserTokenType.tokenType("END_ARRAY");
//    public static final ParserTokenType VALUE_SEPARATOR = ParserTokenType.tokenType("VALUE_SEPARATOR");
//    public static final ParserTokenType KEY_VALUE_SEPARATOR = ParserTokenType.tokenType("KEY_VALUE_SEPARATOR");
//    public static final ParserTokenType BEAN = ParserTokenType.tokenType("BEAN");
//
//}
//%}

string = (\\\"|[^\"])*
safestring = [_a-zA-Z]+([_a-zA-Z0-9\.]+)*
comment = \#[^\r\n]*
number = -?(0|[1-9][0-9]*)(\.[0-9]+)?([eE][+-]?[0-9]+)?

%%

"@type" { return new ParserToken(ConfigTokenType.KEY, yytext(),yychar);}
"@constructor" { return new ParserToken(ConfigTokenType.KEY,yytext(), yychar);}
"@import" { return new ParserToken(ConfigTokenType.IMPORT,yytext(), yychar);}
"true"|"false" { return  new ParserToken(ConfigTokenType.BOOLEAN,yytext(), yychar);}
"null" { return new ParserToken(ConfigTokenType.NULL,null,yychar);}
{comment} { return new ParserToken(ConfigTokenType.COMMENT,yytext(), yychar); }
"," { return new ParserToken(ConfigTokenType.VALUE_SEPARATOR,yytext(), yychar); }
// {map_operator} { return new ParserToken(yytext(), ConfigTokenType.MAP);}
"[" { return new ParserToken(ConfigTokenType.BEGIN_ARRAY,yytext(), yychar); }
"]" { return new ParserToken(ConfigTokenType.END_ARRAY,yytext(), yychar); }
"@{"{safestring}"}" { return new ParserToken(ConfigTokenType.BEAN,yytext(), yychar); }
"{" { return new ParserToken(ConfigTokenType.BEGIN_OBJECT,yytext(), yychar);}
"}" { return new ParserToken(ConfigTokenType.END_OBJECT,yytext(), yychar);}
":" { return new ParserToken(ConfigTokenType.KEY_VALUE_SEPARATOR,yytext(), yychar);}
"=" { return new ParserToken(ConfigTokenType.EQUAL_PROPERTY,yytext(), yychar);}
"+=" { return new ParserToken(ConfigTokenType.APPEND_PROPERTY,yytext(), yychar);}
\"{string}\" { return new ParserToken(ConfigTokenType.STRING,yytext().substring(1,yytext().length()-1),yychar);}
{safestring} { return new ParserToken(ConfigTokenType.KEY,yytext(), yychar);}
{number} { return new ParserToken(ConfigTokenType.STRING,yytext(), yychar);}

[ \t\r\n\f] { /* ignore white space. */ }
. { throw new com.gengoai.parsing.v2.ParseException("Illegal character: "+yytext()+"\" at line: " + yyline + " char offset: " + yychar + " state: " + yystate()); }