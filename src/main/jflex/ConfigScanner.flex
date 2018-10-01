package com.gengoai.config;

import com.gengoai.parsing.*;
import java.util.*;
import java.io.*;

%%

%class Scanner
%public
%unicode
%type com.gengoai.config.Scanner.Token
%function next
%pack
%char
%line

%{
public static class Token {
    public final String type;

    public Token(String type){
        this.type = type;
    }

}
%}

//Let's build a regular expression for any real number
digit = [0-9]
non_zero_digit = [1-9]
integer = -?(0|{non_zero_digit}{digit}*)

//Include real numbers (floating point) and scientific notation
dot = ["."]
exp = (e|E)("+"|-)
frac = {dot}{digit}+
scientific_notation = {exp}{digit}+
any_number = {integer}{frac}?{scientific_notation}?

//Accept any unicode character except certain control characters
string = (\\\"|[^\"])+

safestring = [^(\\)(\")(\/)(\b)(\f)(\n)(\r)(\t), ]+
%%

//Scan for commas, square brackets, braces and colons
"," { return new Token("Comma"); }
"[" { return new Token("Left Square Bracket"); }
"]" { return new Token("Right Square Bracket"); }
"${" { return new Token("Map"); }
"@{" { return new Token("Bean"); }
"{" { return new Token("Left Brace");}
"}" { return new Token("Right Brace");}
"+" { return new Token("Add"); }
"." { return new Token("Period"); }
":" { return new Token("Colon");}


//Scan for unicode strings: letters, numbers, symbols
\"{string}\" { return new Token(new String(yytext()));}
{safestring} { return new Token(new String(yytext()));}

//Scan for boolean strings
"true"|"false" { return new Token(new String(yytext()));}

Scan for null strings
"null" { return new Token("Null Symbol");}

//Scan for numbers: 0, integers or real numbers (floating point)
{any_number} { return new Token(new String(yytext())); }
[ \t\r\n\f] { /* ignore white space. */ }
. { System.err.println("Illegal character: "+yytext()); }