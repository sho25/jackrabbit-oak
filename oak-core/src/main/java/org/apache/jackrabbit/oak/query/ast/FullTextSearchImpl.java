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
name|query
operator|.
name|ast
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|api
operator|.
name|CoreValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|api
operator|.
name|PropertyState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|api
operator|.
name|Tree
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|query
operator|.
name|ast
operator|.
name|ComparisonImpl
operator|.
name|LikePattern
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|query
operator|.
name|index
operator|.
name|FilterImpl
import|;
end_import

begin_comment
comment|/**  * A fulltext "contains(...)" condition.  */
end_comment

begin_class
specifier|public
class|class
name|FullTextSearchImpl
extends|extends
name|ConstraintImpl
block|{
specifier|private
specifier|final
name|String
name|selectorName
decl_stmt|;
specifier|private
specifier|final
name|String
name|propertyName
decl_stmt|;
specifier|private
specifier|final
name|StaticOperandImpl
name|fullTextSearchExpression
decl_stmt|;
specifier|private
name|SelectorImpl
name|selector
decl_stmt|;
specifier|public
name|FullTextSearchImpl
parameter_list|(
name|String
name|selectorName
parameter_list|,
name|String
name|propertyName
parameter_list|,
name|StaticOperandImpl
name|fullTextSearchExpression
parameter_list|)
block|{
name|this
operator|.
name|selectorName
operator|=
name|selectorName
expr_stmt|;
name|this
operator|.
name|propertyName
operator|=
name|propertyName
expr_stmt|;
name|this
operator|.
name|fullTextSearchExpression
operator|=
name|fullTextSearchExpression
expr_stmt|;
block|}
specifier|public
name|StaticOperandImpl
name|getFullTextSearchExpression
parameter_list|()
block|{
return|return
name|fullTextSearchExpression
return|;
block|}
annotation|@
name|Override
name|boolean
name|accept
parameter_list|(
name|AstVisitor
name|v
parameter_list|)
block|{
return|return
name|v
operator|.
name|visit
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"contains("
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|quote
argument_list|(
name|selectorName
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|propertyName
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|'.'
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|quote
argument_list|(
name|propertyName
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|append
argument_list|(
literal|".*, "
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|append
argument_list|(
name|getFullTextSearchExpression
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|evaluate
parameter_list|()
block|{
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|propertyName
operator|!=
literal|null
condition|)
block|{
name|PropertyState
name|p
init|=
name|selector
operator|.
name|currentProperty
argument_list|(
name|propertyName
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|appendString
argument_list|(
name|buff
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Tree
name|tree
init|=
name|getTree
argument_list|(
name|selector
operator|.
name|currentPath
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|tree
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|PropertyState
name|p
range|:
name|tree
operator|.
name|getProperties
argument_list|()
control|)
block|{
name|appendString
argument_list|(
name|buff
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
block|}
comment|// TODO fulltext conditions: need a way to disable evaluation
comment|// if a fulltext index is used, to avoid filtering too much
comment|// (we don't know what exact options are used in the fulltext index)
comment|// (stop word, special characters,...)
name|CoreValue
name|v
init|=
name|fullTextSearchExpression
operator|.
name|currentValue
argument_list|()
decl_stmt|;
try|try
block|{
name|FullTextExpression
name|expr
init|=
name|FullTextParser
operator|.
name|parse
argument_list|(
name|v
operator|.
name|getString
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|expr
operator|.
name|evaluate
argument_list|(
name|buff
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid expression: "
operator|+
name|fullTextSearchExpression
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
name|void
name|appendString
parameter_list|(
name|StringBuilder
name|buff
parameter_list|,
name|PropertyState
name|p
parameter_list|)
block|{
if|if
condition|(
name|p
operator|.
name|isArray
argument_list|()
condition|)
block|{
for|for
control|(
name|CoreValue
name|v
range|:
name|p
operator|.
name|getValues
argument_list|()
control|)
block|{
name|buff
operator|.
name|append
argument_list|(
name|v
operator|.
name|getString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|buff
operator|.
name|append
argument_list|(
name|p
operator|.
name|getValue
argument_list|()
operator|.
name|getString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|bindSelector
parameter_list|(
name|SourceImpl
name|source
parameter_list|)
block|{
name|selector
operator|=
name|source
operator|.
name|getExistingSelector
argument_list|(
name|selectorName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|restrict
parameter_list|(
name|FilterImpl
name|f
parameter_list|)
block|{
if|if
condition|(
name|propertyName
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|f
operator|.
name|getSelector
argument_list|()
operator|==
name|selector
condition|)
block|{
name|f
operator|.
name|restrictProperty
argument_list|(
name|propertyName
argument_list|,
name|Operator
operator|.
name|NOT_EQUAL
argument_list|,
operator|(
name|CoreValue
operator|)
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
name|f
operator|.
name|restrictFulltextCondition
argument_list|(
name|fullTextSearchExpression
operator|.
name|currentValue
argument_list|()
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|restrictPushDown
parameter_list|(
name|SelectorImpl
name|s
parameter_list|)
block|{
if|if
condition|(
name|s
operator|==
name|selector
condition|)
block|{
name|selector
operator|.
name|restrictSelector
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * A parser for fulltext condition literals. The grammar is defined in the      *<a href="http://www.day.com/specs/jcr/2.0/6_Query.html#6.7.19">      * JCR 2.0 specification, 6.7.19 FullTextSearch</a>,      * as follows (a bit simplified):      *<pre>      * FullTextSearchLiteral ::= Disjunct {' OR ' Disjunct}      * Disjunct ::= Term {' ' Term}      * Term ::= ['-'] SimpleTerm      * SimpleTerm ::= Word | '"' Word {' ' Word} '"'      *</pre>      */
specifier|public
specifier|static
class|class
name|FullTextParser
block|{
name|String
name|text
decl_stmt|;
name|int
name|parseIndex
decl_stmt|;
specifier|public
specifier|static
name|FullTextExpression
name|parse
parameter_list|(
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
name|FullTextOr
name|or
init|=
operator|new
name|FullTextOr
argument_list|()
decl_stmt|;
name|or
operator|.
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
name|or
operator|.
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
name|FullTextAnd
name|and
init|=
operator|new
name|FullTextAnd
argument_list|()
decl_stmt|;
name|and
operator|.
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
name|and
operator|.
name|list
operator|.
name|add
argument_list|(
name|parseTerm
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
condition|)
block|{
if|if
condition|(
operator|++
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
operator|&&
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
literal|' '
condition|)
block|{
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
name|text
argument_list|,
name|not
argument_list|,
name|escaped
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
comment|/**      * The base class for fulltext condition expression.      */
specifier|public
specifier|abstract
specifier|static
class|class
name|FullTextExpression
block|{
specifier|public
specifier|abstract
name|boolean
name|evaluate
parameter_list|(
name|String
name|value
parameter_list|)
function_decl|;
specifier|abstract
name|FullTextExpression
name|simplify
parameter_list|()
function_decl|;
block|}
comment|/**      * A fulltext "and" condition.      */
specifier|static
class|class
name|FullTextAnd
extends|extends
name|FullTextExpression
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
annotation|@
name|Override
specifier|public
name|boolean
name|evaluate
parameter_list|(
name|String
name|value
parameter_list|)
block|{
for|for
control|(
name|FullTextExpression
name|e
range|:
name|list
control|)
block|{
if|if
condition|(
operator|!
name|e
operator|.
name|evaluate
argument_list|(
name|value
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
name|FullTextExpression
name|simplify
parameter_list|()
block|{
return|return
name|list
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|?
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
else|:
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|FullTextExpression
name|e
range|:
name|list
control|)
block|{
if|if
condition|(
name|i
operator|++
operator|>
literal|0
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|buff
operator|.
name|append
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|buff
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|/**      * A fulltext "or" condition.      */
specifier|static
class|class
name|FullTextOr
extends|extends
name|FullTextExpression
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
annotation|@
name|Override
specifier|public
name|boolean
name|evaluate
parameter_list|(
name|String
name|value
parameter_list|)
block|{
for|for
control|(
name|FullTextExpression
name|e
range|:
name|list
control|)
block|{
if|if
condition|(
name|e
operator|.
name|evaluate
argument_list|(
name|value
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
name|FullTextExpression
name|simplify
parameter_list|()
block|{
return|return
name|list
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|?
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|simplify
argument_list|()
else|:
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|FullTextExpression
name|e
range|:
name|list
control|)
block|{
if|if
condition|(
name|i
operator|++
operator|>
literal|0
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|" OR "
argument_list|)
expr_stmt|;
block|}
name|buff
operator|.
name|append
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|buff
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|/**      * A fulltext term, or a "not" term.      */
specifier|static
class|class
name|FullTextTerm
extends|extends
name|FullTextExpression
block|{
specifier|private
specifier|final
name|boolean
name|not
decl_stmt|;
specifier|private
specifier|final
name|String
name|text
decl_stmt|;
specifier|private
specifier|final
name|String
name|filteredText
decl_stmt|;
specifier|private
specifier|final
name|LikePattern
name|like
decl_stmt|;
name|FullTextTerm
parameter_list|(
name|String
name|text
parameter_list|,
name|boolean
name|not
parameter_list|,
name|boolean
name|escaped
parameter_list|)
block|{
name|this
operator|.
name|text
operator|=
name|text
expr_stmt|;
name|this
operator|.
name|not
operator|=
name|not
expr_stmt|;
comment|// for testFulltextIntercapSQL
comment|// filter special characters such as '
comment|// to make tests pass, for example the
comment|// FulltextQueryTest.testFulltextExcludeSQL,
comment|// which searches for:
comment|// "text ''fox jumps'' -other"
comment|// (please note the two single quotes instead of
comment|// double quotes before for and after jumps)
name|boolean
name|pattern
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|escaped
condition|)
block|{
name|filteredText
operator|=
name|text
expr_stmt|;
block|}
else|else
block|{
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|text
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|c
init|=
name|text
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|'*'
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|'%'
argument_list|)
expr_stmt|;
name|pattern
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|==
literal|'?'
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|'_'
argument_list|)
expr_stmt|;
name|pattern
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|==
literal|'_'
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|"\\_"
argument_list|)
expr_stmt|;
name|pattern
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|Character
operator|.
name|isLetterOrDigit
argument_list|(
name|c
argument_list|)
operator|||
literal|" +-:&"
operator|.
name|indexOf
argument_list|(
name|c
argument_list|)
operator|>=
literal|0
condition|)
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
name|this
operator|.
name|filteredText
operator|=
name|buff
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|pattern
condition|)
block|{
name|like
operator|=
operator|new
name|LikePattern
argument_list|(
literal|"%"
operator|+
name|filteredText
operator|+
literal|"%"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|like
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|evaluate
parameter_list|(
name|String
name|value
parameter_list|)
block|{
comment|// for testFulltextIntercapSQL
name|value
operator|=
name|value
operator|.
name|toLowerCase
argument_list|()
expr_stmt|;
if|if
condition|(
name|like
operator|!=
literal|null
condition|)
block|{
return|return
name|like
operator|.
name|matches
argument_list|(
name|value
argument_list|)
return|;
block|}
if|if
condition|(
name|not
condition|)
block|{
return|return
name|value
operator|.
name|indexOf
argument_list|(
name|filteredText
argument_list|)
operator|<
literal|0
return|;
block|}
return|return
name|value
operator|.
name|indexOf
argument_list|(
name|filteredText
argument_list|)
operator|>=
literal|0
return|;
block|}
annotation|@
name|Override
name|FullTextExpression
name|simplify
parameter_list|()
block|{
return|return
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
operator|(
name|not
condition|?
literal|"-"
else|:
literal|""
operator|)
operator|+
literal|"\""
operator|+
name|text
operator|.
name|replaceAll
argument_list|(
literal|"\""
argument_list|,
literal|"\\\""
argument_list|)
operator|+
literal|"\""
return|;
block|}
block|}
block|}
end_class

end_unit

