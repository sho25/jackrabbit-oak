begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|query
operator|.
name|fulltext
package|;
end_package

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_comment
comment|/**  * A parser for fulltext condition literals. The grammar is defined in the  *<a href="http://www.day.com/specs/jcr/2.0/6_Query.html#6.7.19">  * JCR 2.0 specification, 6.7.19 FullTextSearch</a>,  * as follows (a bit simplified):  *<pre>  * FullTextSearchLiteral ::= Disjunct {' OR ' Disjunct}  * Disjunct ::= Term {' ' Term}  * Term ::= ['-'] SimpleTerm  * SimpleTerm ::= Word | '"' Word {' ' Word} '"'  *</pre>  */
end_comment

begin_class
specifier|public
class|class
name|FullTextParser
block|{
comment|/**      * Compatibility for Jackrabbit 2.0 single quoted phrase queries.      * (contains(., "word ''hello world'' word")      * These are queries that delimit a phrase with a single quote      * instead, as in the spec, using double quotes.      */
specifier|private
specifier|static
specifier|final
name|boolean
name|JACKRABBIT_2_SINGLE_QUOTED_PHRASE
init|=
literal|true
decl_stmt|;
specifier|private
name|String
name|propertyName
decl_stmt|;
specifier|private
name|String
name|text
decl_stmt|;
specifier|private
name|int
name|parseIndex
decl_stmt|;
specifier|public
specifier|static
name|FullTextExpression
name|parse
parameter_list|(
name|String
name|propertyName
parameter_list|,
name|String
name|text
parameter_list|)
throws|throws
name|ParseException
block|{
name|FullTextParser
name|p
init|=
operator|new
name|FullTextParser
argument_list|()
decl_stmt|;
name|p
operator|.
name|propertyName
operator|=
name|propertyName
expr_stmt|;
name|p
operator|.
name|text
operator|=
name|text
expr_stmt|;
name|FullTextExpression
name|e
init|=
name|p
operator|.
name|parseOr
argument_list|()
decl_stmt|;
return|return
name|e
return|;
block|}
name|FullTextExpression
name|parseOr
parameter_list|()
throws|throws
name|ParseException
block|{
name|ArrayList
argument_list|<
name|FullTextExpression
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|FullTextExpression
argument_list|>
argument_list|()
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|parseAnd
argument_list|()
argument_list|)
expr_stmt|;
while|while
condition|(
name|parseIndex
operator|<
name|text
operator|.
name|length
argument_list|()
condition|)
block|{
if|if
condition|(
name|text
operator|.
name|substring
argument_list|(
name|parseIndex
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"OR "
argument_list|)
condition|)
block|{
name|parseIndex
operator|+=
literal|3
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|parseAnd
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
name|FullTextOr
name|or
init|=
operator|new
name|FullTextOr
argument_list|(
name|list
argument_list|)
decl_stmt|;
return|return
name|or
operator|.
name|simplify
argument_list|()
return|;
block|}
name|FullTextExpression
name|parseAnd
parameter_list|()
throws|throws
name|ParseException
block|{
name|ArrayList
argument_list|<
name|FullTextExpression
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|FullTextExpression
argument_list|>
argument_list|()
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|parseTerm
argument_list|()
argument_list|)
expr_stmt|;
while|while
condition|(
name|parseIndex
operator|<
name|text
operator|.
name|length
argument_list|()
condition|)
block|{
if|if
condition|(
name|text
operator|.
name|substring
argument_list|(
name|parseIndex
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"OR "
argument_list|)
condition|)
block|{
break|break;
block|}
name|list
operator|.
name|add
argument_list|(
name|parseTerm
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|FullTextAnd
name|and
init|=
operator|new
name|FullTextAnd
argument_list|(
name|list
argument_list|)
decl_stmt|;
return|return
name|and
operator|.
name|simplify
argument_list|()
return|;
block|}
name|FullTextExpression
name|parseTerm
parameter_list|()
throws|throws
name|ParseException
block|{
if|if
condition|(
name|parseIndex
operator|>=
name|text
operator|.
name|length
argument_list|()
condition|)
block|{
throw|throw
name|getSyntaxError
argument_list|(
literal|"term"
argument_list|)
throw|;
block|}
name|boolean
name|not
init|=
literal|false
decl_stmt|;
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|char
name|c
init|=
name|text
operator|.
name|charAt
argument_list|(
name|parseIndex
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|'-'
operator|&&
name|parseIndex
operator|<
name|text
operator|.
name|length
argument_list|()
operator|-
literal|1
operator|&&
name|text
operator|.
name|charAt
argument_list|(
name|parseIndex
operator|+
literal|1
argument_list|)
operator|!=
literal|' '
condition|)
block|{
name|c
operator|=
name|text
operator|.
name|charAt
argument_list|(
operator|++
name|parseIndex
argument_list|)
expr_stmt|;
name|not
operator|=
literal|true
expr_stmt|;
block|}
name|boolean
name|escaped
init|=
literal|false
decl_stmt|;
name|String
name|boost
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|'\"'
condition|)
block|{
name|parseIndex
operator|++
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|parseIndex
operator|>=
name|text
operator|.
name|length
argument_list|()
condition|)
block|{
throw|throw
name|getSyntaxError
argument_list|(
literal|"double quote"
argument_list|)
throw|;
block|}
name|c
operator|=
name|text
operator|.
name|charAt
argument_list|(
name|parseIndex
operator|++
argument_list|)
expr_stmt|;
if|if
condition|(
name|c
operator|==
literal|'\\'
condition|)
block|{
name|escaped
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|parseIndex
operator|>=
name|text
operator|.
name|length
argument_list|()
condition|)
block|{
throw|throw
name|getSyntaxError
argument_list|(
literal|"escaped char"
argument_list|)
throw|;
block|}
name|c
operator|=
name|text
operator|.
name|charAt
argument_list|(
name|parseIndex
operator|++
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|==
literal|'\"'
condition|)
block|{
if|if
condition|(
name|parseIndex
operator|<
name|text
operator|.
name|length
argument_list|()
condition|)
block|{
if|if
condition|(
name|text
operator|.
name|charAt
argument_list|(
name|parseIndex
argument_list|)
operator|==
literal|'^'
condition|)
block|{
name|boost
operator|=
literal|""
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|text
operator|.
name|charAt
argument_list|(
name|parseIndex
argument_list|)
operator|!=
literal|' '
condition|)
block|{
throw|throw
name|getSyntaxError
argument_list|(
literal|"space"
argument_list|)
throw|;
block|}
block|}
name|parseIndex
operator|++
expr_stmt|;
break|break;
block|}
else|else
block|{
name|buff
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|c
operator|==
literal|'\''
operator|&&
name|JACKRABBIT_2_SINGLE_QUOTED_PHRASE
condition|)
block|{
comment|// basically the same as double quote
name|parseIndex
operator|++
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|parseIndex
operator|>=
name|text
operator|.
name|length
argument_list|()
condition|)
block|{
throw|throw
name|getSyntaxError
argument_list|(
literal|"single quote"
argument_list|)
throw|;
block|}
name|c
operator|=
name|text
operator|.
name|charAt
argument_list|(
name|parseIndex
operator|++
argument_list|)
expr_stmt|;
if|if
condition|(
name|c
operator|==
literal|'\\'
condition|)
block|{
name|escaped
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|parseIndex
operator|>=
name|text
operator|.
name|length
argument_list|()
condition|)
block|{
throw|throw
name|getSyntaxError
argument_list|(
literal|"escaped char"
argument_list|)
throw|;
block|}
name|c
operator|=
name|text
operator|.
name|charAt
argument_list|(
name|parseIndex
operator|++
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|==
literal|'\''
condition|)
block|{
if|if
condition|(
name|parseIndex
operator|<
name|text
operator|.
name|length
argument_list|()
condition|)
block|{
if|if
condition|(
name|text
operator|.
name|charAt
argument_list|(
name|parseIndex
argument_list|)
operator|==
literal|'^'
condition|)
block|{
name|boost
operator|=
literal|""
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|text
operator|.
name|charAt
argument_list|(
name|parseIndex
argument_list|)
operator|!=
literal|' '
condition|)
block|{
throw|throw
name|getSyntaxError
argument_list|(
literal|"space"
argument_list|)
throw|;
block|}
block|}
name|parseIndex
operator|++
expr_stmt|;
break|break;
block|}
else|else
block|{
name|buff
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
do|do
block|{
name|c
operator|=
name|text
operator|.
name|charAt
argument_list|(
name|parseIndex
operator|++
argument_list|)
expr_stmt|;
if|if
condition|(
name|c
operator|==
literal|'\\'
condition|)
block|{
name|escaped
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|parseIndex
operator|>=
name|text
operator|.
name|length
argument_list|()
condition|)
block|{
throw|throw
name|getSyntaxError
argument_list|(
literal|"escaped char"
argument_list|)
throw|;
block|}
name|c
operator|=
name|text
operator|.
name|charAt
argument_list|(
name|parseIndex
operator|++
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|==
literal|'^'
condition|)
block|{
name|boost
operator|=
literal|""
expr_stmt|;
break|break;
block|}
elseif|else
if|if
condition|(
name|c
operator|<=
literal|' '
condition|)
block|{
while|while
condition|(
name|parseIndex
operator|<
name|text
operator|.
name|length
argument_list|()
condition|)
block|{
name|c
operator|=
name|text
operator|.
name|charAt
argument_list|(
name|parseIndex
argument_list|)
expr_stmt|;
if|if
condition|(
name|c
operator|>
literal|' '
condition|)
block|{
break|break;
block|}
name|parseIndex
operator|++
expr_stmt|;
block|}
break|break;
block|}
else|else
block|{
name|buff
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
do|while
condition|(
name|parseIndex
operator|<
name|text
operator|.
name|length
argument_list|()
condition|)
do|;
block|}
if|if
condition|(
name|boost
operator|!=
literal|null
condition|)
block|{
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
while|while
condition|(
name|parseIndex
operator|<
name|text
operator|.
name|length
argument_list|()
condition|)
block|{
name|c
operator|=
name|text
operator|.
name|charAt
argument_list|(
name|parseIndex
operator|++
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|c
argument_list|<
literal|'0'
operator|||
name|c
argument_list|>
literal|'9'
operator|)
operator|&&
name|c
operator|!=
literal|'.'
condition|)
block|{
break|break;
block|}
name|b
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
name|boost
operator|=
name|b
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|buff
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
name|getSyntaxError
argument_list|(
literal|"term"
argument_list|)
throw|;
block|}
name|String
name|text
init|=
name|buff
operator|.
name|toString
argument_list|()
decl_stmt|;
name|FullTextTerm
name|term
init|=
operator|new
name|FullTextTerm
argument_list|(
name|propertyName
argument_list|,
name|text
argument_list|,
name|not
argument_list|,
name|escaped
argument_list|,
name|boost
argument_list|)
decl_stmt|;
return|return
name|term
operator|.
name|simplify
argument_list|()
return|;
block|}
specifier|private
name|ParseException
name|getSyntaxError
parameter_list|(
name|String
name|expected
parameter_list|)
block|{
name|int
name|index
init|=
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|parseIndex
argument_list|,
name|text
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|query
init|=
name|text
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|index
argument_list|)
operator|+
literal|"(*)"
operator|+
name|text
operator|.
name|substring
argument_list|(
name|index
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|expected
operator|!=
literal|null
condition|)
block|{
name|query
operator|+=
literal|"; expected: "
operator|+
name|expected
expr_stmt|;
block|}
return|return
operator|new
name|ParseException
argument_list|(
literal|"FullText expression: "
operator|+
name|query
argument_list|,
name|index
argument_list|)
return|;
block|}
block|}
end_class

end_unit

