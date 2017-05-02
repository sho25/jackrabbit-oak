begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|fulltext
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|BitSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Splitter
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
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
name|PropertyValue
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
name|api
operator|.
name|Type
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
name|commons
operator|.
name|PathUtils
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
name|Query
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
name|QueryImpl
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
name|AndImpl
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
name|ConstraintImpl
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
name|FullTextSearchImpl
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
name|LiteralImpl
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
name|OrImpl
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
name|spi
operator|.
name|query
operator|.
name|PropertyValues
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|util
operator|.
name|Text
operator|.
name|encodeIllegalXMLCharacters
import|;
end_import

begin_comment
comment|/**  * This class can extract excerpts from node.  */
end_comment

begin_class
specifier|public
class|class
name|SimpleExcerptProvider
block|{
specifier|public
specifier|static
specifier|final
name|String
name|REP_EXCERPT_FN
init|=
literal|"rep:excerpt(.)"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|EXCERPT_END
init|=
literal|"</span></div>"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|EXCERPT_BEGIN
init|=
literal|"<div><span>"
decl_stmt|;
specifier|private
specifier|static
name|int
name|maxFragmentSize
init|=
literal|150
decl_stmt|;
specifier|private
name|SimpleExcerptProvider
parameter_list|()
block|{     }
specifier|public
specifier|static
name|String
name|getExcerpt
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|columnName
parameter_list|,
name|Query
name|query
parameter_list|,
name|boolean
name|highlight
parameter_list|)
block|{
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Tree
name|t
init|=
name|query
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|t
operator|==
literal|null
operator|||
operator|!
name|t
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|columnName
operator|=
name|extractExcerptProperty
argument_list|(
name|columnName
argument_list|)
expr_stmt|;
if|if
condition|(
name|columnName
operator|!=
literal|null
operator|&&
name|columnName
operator|.
name|contains
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
for|for
control|(
name|String
name|p
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|columnName
argument_list|)
argument_list|)
control|)
block|{
if|if
condition|(
name|t
operator|.
name|hasChild
argument_list|(
name|p
argument_list|)
condition|)
block|{
name|t
operator|=
name|t
operator|.
name|getChild
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
name|columnName
operator|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|columnName
argument_list|)
expr_stmt|;
block|}
name|StringBuilder
name|text
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
name|separator
init|=
literal|""
decl_stmt|;
for|for
control|(
name|PropertyState
name|p
range|:
name|t
operator|.
name|getProperties
argument_list|()
control|)
block|{
if|if
condition|(
name|p
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
operator|==
name|Type
operator|.
name|STRING
operator|.
name|tag
argument_list|()
operator|&&
operator|(
name|columnName
operator|==
literal|null
operator|||
name|columnName
operator|.
name|equalsIgnoreCase
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
operator|)
condition|)
block|{
name|text
operator|.
name|append
argument_list|(
name|separator
argument_list|)
expr_stmt|;
name|separator
operator|=
literal|" "
expr_stmt|;
for|for
control|(
name|String
name|v
range|:
name|p
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|)
control|)
block|{
name|text
operator|.
name|append
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|searchToken
init|=
name|extractFulltext
argument_list|(
name|query
argument_list|)
decl_stmt|;
if|if
condition|(
name|highlight
operator|&&
name|searchToken
operator|!=
literal|null
condition|)
block|{
return|return
name|highlight
argument_list|(
name|text
argument_list|,
name|searchToken
argument_list|)
return|;
block|}
return|return
name|noHighlight
argument_list|(
name|text
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|String
name|extractExcerptProperty
parameter_list|(
name|String
name|column
parameter_list|)
block|{
comment|// most frequent case first
if|if
condition|(
name|REP_EXCERPT_FN
operator|.
name|equalsIgnoreCase
argument_list|(
name|column
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|column
operator|.
name|substring
argument_list|(
name|column
operator|.
name|indexOf
argument_list|(
literal|"("
argument_list|)
operator|+
literal|1
argument_list|,
name|column
operator|.
name|indexOf
argument_list|(
literal|")"
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|extractFulltext
parameter_list|(
name|Query
name|q
parameter_list|)
block|{
comment|// TODO instanceof should not be used
if|if
condition|(
name|q
operator|instanceof
name|QueryImpl
condition|)
block|{
return|return
name|extractFulltext
argument_list|(
operator|(
operator|(
name|QueryImpl
operator|)
name|q
operator|)
operator|.
name|getConstraint
argument_list|()
argument_list|)
return|;
block|}
return|return
name|ImmutableSet
operator|.
name|of
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|extractFulltext
parameter_list|(
name|ConstraintImpl
name|c
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|tokens
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|// TODO instanceof should not be used,
comment|// as it will break without us noticing if we extend the AST
if|if
condition|(
name|c
operator|instanceof
name|FullTextSearchImpl
condition|)
block|{
name|FullTextSearchImpl
name|f
init|=
operator|(
name|FullTextSearchImpl
operator|)
name|c
decl_stmt|;
if|if
condition|(
name|f
operator|.
name|getFullTextSearchExpression
argument_list|()
operator|instanceof
name|LiteralImpl
condition|)
block|{
name|LiteralImpl
name|l
init|=
operator|(
name|LiteralImpl
operator|)
name|f
operator|.
name|getFullTextSearchExpression
argument_list|()
decl_stmt|;
name|tokens
operator|.
name|add
argument_list|(
name|l
operator|.
name|getLiteralValue
argument_list|()
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|c
operator|instanceof
name|AndImpl
condition|)
block|{
for|for
control|(
name|ConstraintImpl
name|constraint
range|:
operator|(
operator|(
name|AndImpl
operator|)
name|c
operator|)
operator|.
name|getConstraints
argument_list|()
control|)
block|{
name|tokens
operator|.
name|addAll
argument_list|(
name|extractFulltext
argument_list|(
name|constraint
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|c
operator|instanceof
name|OrImpl
condition|)
block|{
for|for
control|(
name|ConstraintImpl
name|constraint
range|:
operator|(
operator|(
name|OrImpl
operator|)
name|c
operator|)
operator|.
name|getConstraints
argument_list|()
control|)
block|{
name|tokens
operator|.
name|addAll
argument_list|(
name|extractFulltext
argument_list|(
name|constraint
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|tokens
return|;
block|}
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|tokenize
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|in
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|tokens
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|in
control|)
block|{
name|tokens
operator|.
name|addAll
argument_list|(
name|tokenize
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|tokens
return|;
block|}
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|tokenize
parameter_list|(
name|String
name|in
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|out
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|StringBuilder
name|token
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|boolean
name|quote
init|=
literal|false
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
name|in
operator|.
name|length
argument_list|()
condition|;
control|)
block|{
specifier|final
name|int
name|c
init|=
name|in
operator|.
name|codePointAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|int
name|length
init|=
name|Character
operator|.
name|charCount
argument_list|(
name|c
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|c
condition|)
block|{
case|case
literal|' '
case|:
if|if
condition|(
name|quote
condition|)
block|{
name|token
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|token
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|out
operator|.
name|add
argument_list|(
name|token
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|token
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
block|}
break|break;
case|case
literal|'"'
case|:
case|case
literal|'\''
case|:
if|if
condition|(
name|quote
condition|)
block|{
name|quote
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|token
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|out
operator|.
name|add
argument_list|(
name|token
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|token
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|quote
operator|=
literal|true
expr_stmt|;
block|}
break|break;
default|default:
name|token
operator|.
name|append
argument_list|(
operator|new
name|String
argument_list|(
name|Character
operator|.
name|toChars
argument_list|(
name|c
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|i
operator|+=
name|length
expr_stmt|;
block|}
if|if
condition|(
name|token
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|out
operator|.
name|add
argument_list|(
name|token
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|out
return|;
block|}
specifier|private
specifier|static
name|String
name|noHighlight
parameter_list|(
name|StringBuilder
name|text
parameter_list|)
block|{
if|if
condition|(
name|text
operator|.
name|length
argument_list|()
operator|>
name|maxFragmentSize
condition|)
block|{
name|int
name|lastSpace
init|=
name|text
operator|.
name|lastIndexOf
argument_list|(
literal|" "
argument_list|,
name|maxFragmentSize
argument_list|)
decl_stmt|;
if|if
condition|(
name|lastSpace
operator|!=
operator|-
literal|1
condition|)
block|{
name|text
operator|.
name|setLength
argument_list|(
name|lastSpace
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|text
operator|.
name|setLength
argument_list|(
name|maxFragmentSize
argument_list|)
expr_stmt|;
block|}
name|text
operator|.
name|append
argument_list|(
literal|" ..."
argument_list|)
expr_stmt|;
block|}
name|StringBuilder
name|excerpt
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"<div><span>"
argument_list|)
decl_stmt|;
name|excerpt
operator|.
name|append
argument_list|(
name|encodeIllegalXMLCharacters
argument_list|(
name|text
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|excerpt
operator|.
name|append
argument_list|(
literal|"</span></div>"
argument_list|)
expr_stmt|;
return|return
name|excerpt
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|static
name|String
name|highlight
parameter_list|(
name|StringBuilder
name|text
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|searchToken
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|tokens
init|=
name|tokenize
argument_list|(
name|searchToken
argument_list|)
decl_stmt|;
name|String
name|escaped
init|=
name|encodeIllegalXMLCharacters
argument_list|(
name|text
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|BitSet
name|highlight
init|=
operator|new
name|BitSet
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|token
range|:
name|tokens
control|)
block|{
name|highlight
argument_list|(
name|escaped
argument_list|,
name|highlight
argument_list|,
name|token
argument_list|)
expr_stmt|;
block|}
name|StringBuilder
name|excerpt
init|=
operator|new
name|StringBuilder
argument_list|(
name|EXCERPT_BEGIN
argument_list|)
decl_stmt|;
name|boolean
name|strong
init|=
literal|false
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
name|escaped
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|highlight
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|&&
operator|!
name|strong
condition|)
block|{
name|strong
operator|=
literal|true
expr_stmt|;
name|excerpt
operator|.
name|append
argument_list|(
literal|"<strong>"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|highlight
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|&&
name|strong
condition|)
block|{
name|strong
operator|=
literal|false
expr_stmt|;
name|excerpt
operator|.
name|append
argument_list|(
literal|"</strong>"
argument_list|)
expr_stmt|;
block|}
name|excerpt
operator|.
name|append
argument_list|(
name|escaped
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|strong
condition|)
block|{
name|excerpt
operator|.
name|append
argument_list|(
literal|"</strong>"
argument_list|)
expr_stmt|;
block|}
name|excerpt
operator|.
name|append
argument_list|(
name|EXCERPT_END
argument_list|)
expr_stmt|;
return|return
name|excerpt
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|void
name|highlight
parameter_list|(
name|String
name|text
parameter_list|,
name|BitSet
name|highlightBits
parameter_list|,
name|String
name|token
parameter_list|)
block|{
name|boolean
name|isLike
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|token
operator|.
name|endsWith
argument_list|(
literal|"*"
argument_list|)
condition|)
block|{
if|if
condition|(
name|token
operator|.
name|length
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// don't highlight the '*' character itself
return|return;
block|}
name|token
operator|=
name|token
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|token
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|isLike
operator|=
literal|true
expr_stmt|;
block|}
name|int
name|index
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|index
operator|<
name|text
operator|.
name|length
argument_list|()
condition|)
block|{
name|index
operator|=
name|text
operator|.
name|indexOf
argument_list|(
name|token
argument_list|,
name|index
argument_list|)
expr_stmt|;
if|if
condition|(
name|index
operator|<
literal|0
condition|)
block|{
break|break;
block|}
name|int
name|endIndex
init|=
name|index
operator|+
name|token
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|isLike
condition|)
block|{
name|int
name|nextSpace
init|=
name|text
operator|.
name|indexOf
argument_list|(
literal|" "
argument_list|,
name|endIndex
argument_list|)
decl_stmt|;
if|if
condition|(
name|nextSpace
operator|!=
operator|-
literal|1
condition|)
block|{
name|endIndex
operator|=
name|nextSpace
expr_stmt|;
block|}
else|else
block|{
name|endIndex
operator|=
name|text
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
block|}
while|while
condition|(
name|index
operator|<
name|endIndex
condition|)
block|{
name|highlightBits
operator|.
name|set
argument_list|(
name|index
operator|++
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
specifier|static
name|PropertyValue
name|getExcerpt
parameter_list|(
name|PropertyValue
name|value
parameter_list|)
block|{
name|Splitter
name|listSplitter
init|=
name|Splitter
operator|.
name|on
argument_list|(
literal|','
argument_list|)
operator|.
name|trimResults
argument_list|()
operator|.
name|omitEmptyStrings
argument_list|()
decl_stmt|;
name|StringBuilder
name|excerpt
init|=
operator|new
name|StringBuilder
argument_list|(
name|EXCERPT_BEGIN
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|v
range|:
name|listSplitter
operator|.
name|splitToList
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
control|)
block|{
name|excerpt
operator|.
name|append
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
name|excerpt
operator|.
name|append
argument_list|(
name|EXCERPT_END
argument_list|)
expr_stmt|;
return|return
name|PropertyValues
operator|.
name|newString
argument_list|(
name|excerpt
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

