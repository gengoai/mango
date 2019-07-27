package com.gengoai.config;

import com.gengoai.parsing.*;



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
public static final class ConfigTokenType  {
    public static final ParserTokenType IMPORT = ParserTokenType.tokenType("IMPORT");
    public static final ParserTokenType COMMENT = ParserTokenType.tokenType("COMMENT");
    public static final ParserTokenType NULL = ParserTokenType.tokenType("NULL");
    public static final ParserTokenType BOOLEAN = ParserTokenType.tokenType("BOOLEAN");
    public static final ParserTokenType STRING = ParserTokenType.tokenType("STRING");
    public static final ParserTokenType APPEND_PROPERTY = ParserTokenType.tokenType("APPEND_PROPERTY");
    public static final ParserTokenType EQUAL_PROPERTY = ParserTokenType.tokenType("EQUAL_PROPERTY");
    public static final ParserTokenType BEGIN_OBJECT = ParserTokenType.tokenType("BEGIN_OBJECT");
    public static final ParserTokenType END_OBJECT = ParserTokenType.tokenType("END_OBJECT");
    public static final ParserTokenType KEY = ParserTokenType.tokenType("KEY");
    public static final ParserTokenType MAP = ParserTokenType.tokenType("MAP");
    public static final ParserTokenType BEGIN_ARRAY = ParserTokenType.tokenType("BEGIN_ARRAY");
    public static final ParserTokenType END_ARRAY = ParserTokenType.tokenType("END_ARRAY");
    public static final ParserTokenType VALUE_SEPARATOR = ParserTokenType.tokenType("VALUE_SEPARATOR");
    public static final ParserTokenType KEY_VALUE_SEPARATOR = ParserTokenType.tokenType("KEY_VALUE_SEPARATOR");
    public static final ParserTokenType BEAN = ParserTokenType.tokenType("BEAN");

}
%}

string = (\\\"|[^\"])*
safestring = [_a-zA-Z]+([_a-zA-Z0-9\.]+)*
comment = \#[^\r\n]*
number = -?(0|[1-9][0-9]*)(\.[0-9]+)?([eE][+-]?[0-9]+)?

%%

"@type" { return new ParserToken(yytext(), ConfigTokenType.KEY,yychar);}
"@constructor" { return new ParserToken(yytext(), ConfigTokenType.KEY,yychar);}
"@import" { return new ParserToken(yytext(), ConfigTokenType.IMPORT,yychar);}
"true"|"false" { return  new ParserToken(yytext(), ConfigTokenType.BOOLEAN,yychar);}
"null" { return new ParserToken(null,ConfigTokenType.NULL,yychar);}
{comment} { return new ParserToken(yytext(), ConfigTokenType.COMMENT,yychar); }
"," { return new ParserToken(yytext(), ConfigTokenType.VALUE_SEPARATOR,yychar); }
// {map_operator} { return new ParserToken(yytext(), ConfigTokenType.MAP);}
"[" { return new ParserToken(yytext(), ConfigTokenType.BEGIN_ARRAY,yychar); }
"]" { return new ParserToken(yytext(), ConfigTokenType.END_ARRAY,yychar); }
"@{"{safestring}"}" { return new ParserToken(yytext(), ConfigTokenType.BEAN,yychar); }
"{" { return new ParserToken(yytext(), ConfigTokenType.BEGIN_OBJECT,yychar);}
"}" { return new ParserToken(yytext(), ConfigTokenType.END_OBJECT,yychar);}
":" { return new ParserToken(yytext(), ConfigTokenType.KEY_VALUE_SEPARATOR,yychar);}
"=" { return new ParserToken(yytext(), ConfigTokenType.EQUAL_PROPERTY,yychar);}
"+=" { return new ParserToken(yytext(), ConfigTokenType.APPEND_PROPERTY,yychar);}
\"{string}\" { return new ParserToken(yytext().substring(1,yytext().length()-1), ConfigTokenType.STRING,yychar);}
{safestring} { return new ParserToken(yytext(), ConfigTokenType.KEY,yychar);}
{number} { return new ParserToken(yytext(), ConfigTokenType.STRING,yychar);}

[ \t\r\n\f] { /* ignore white space. */ }
. { throw new com.gengoai.parsing.ParseException("Illegal character: "+yytext()+"\" at line: " + yyline + " char offset: " + yychar + " state: " + yystate()); }