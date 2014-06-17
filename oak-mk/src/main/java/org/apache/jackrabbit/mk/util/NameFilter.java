begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|util
package|;
end_package

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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Simple name filter utility class.  *<ul>  *<li>a filter consists of one or more<i>globs</i></li>  *<li>a<i>glob</i> prefixed by {@code -} (dash) is treated as an exclusion pattern;  * all others are considered inclusion patterns</li>  *<li>a leading {@code -} (dash) must be escaped by prepending {@code \} (backslash)  * if it should be interpreted as a literal</li>  *<li>{@code *} (asterisk) serves as a<i>wildcard</i>, i.e. it matches any  * substring in the target name</li>  *<li>{@code *} (asterisk) occurrences within the glob to be interpreted as  * literals must be escaped by prepending {@code \} (backslash)</li>  *<li>a filter matches a target name if any of the inclusion patterns match but  * none of the exclusion patterns</li>  *</ul>  * Examples:  *<p>  * {@code ["foo*", "-foo99"]} matches {@code "foo"} and {@code "foo bar"}  * but not {@code "foo99"}.  *<p>  * {@code ["foo\*"]} matches {@code "foo*"} but not {@code "foo99"}.  *<p>  * {@code ["\-blah"]} matches {@code "-blah"}.  */
end_comment

begin_class
specifier|public
class|class
name|NameFilter
block|{
specifier|public
specifier|static
specifier|final
name|char
name|WILDCARD
init|=
literal|'*'
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|char
name|EXCLUDE_PREFIX
init|=
literal|'-'
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|char
name|ESCAPE
init|=
literal|'\\'
decl_stmt|;
comment|// list of ORed inclusion patterns
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|inclPatterns
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|// list of ORed exclusion patterns
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|exclPatterns
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|boolean
name|containsWildcard
decl_stmt|;
specifier|public
name|NameFilter
parameter_list|(
name|String
index|[]
name|patterns
parameter_list|)
block|{
name|containsWildcard
operator|=
literal|false
expr_stmt|;
for|for
control|(
name|String
name|pattern
range|:
name|patterns
control|)
block|{
if|if
condition|(
name|pattern
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
continue|continue;
block|}
elseif|else
if|if
condition|(
name|pattern
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
name|EXCLUDE_PREFIX
condition|)
block|{
name|pattern
operator|=
name|pattern
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|exclPatterns
operator|.
name|add
argument_list|(
name|pattern
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|inclPatterns
operator|.
name|add
argument_list|(
name|pattern
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|containsWildcard
condition|)
block|{
name|containsWildcard
operator|=
name|containsWildCard
argument_list|(
name|pattern
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|boolean
name|matches
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|boolean
name|matched
init|=
literal|false
decl_stmt|;
comment|// check inclusion patterns
for|for
control|(
name|String
name|pattern
range|:
name|inclPatterns
control|)
block|{
if|if
condition|(
name|internalMatches
argument_list|(
name|name
argument_list|,
name|pattern
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
condition|)
block|{
name|matched
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|matched
condition|)
block|{
comment|// check exclusion patterns
for|for
control|(
name|String
name|pattern
range|:
name|exclPatterns
control|)
block|{
if|if
condition|(
name|internalMatches
argument_list|(
name|name
argument_list|,
name|pattern
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
condition|)
block|{
name|matched
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
block|}
return|return
name|matched
return|;
block|}
specifier|public
name|boolean
name|containsWildcard
parameter_list|()
block|{
return|return
name|containsWildcard
return|;
block|}
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getExclusionPatterns
parameter_list|()
block|{
return|return
name|exclPatterns
return|;
block|}
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getInclusionPatterns
parameter_list|()
block|{
return|return
name|inclPatterns
return|;
block|}
specifier|private
specifier|static
name|boolean
name|containsWildCard
parameter_list|(
name|String
name|pattern
parameter_list|)
block|{
name|int
name|len
init|=
name|pattern
operator|.
name|length
argument_list|()
decl_stmt|;
name|int
name|pos
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|pos
operator|<
name|len
condition|)
block|{
if|if
condition|(
name|pattern
operator|.
name|charAt
argument_list|(
name|pos
argument_list|)
operator|==
name|ESCAPE
operator|&&
name|pos
operator|<
operator|(
name|len
operator|-
literal|1
operator|)
operator|&&
name|pattern
operator|.
name|charAt
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
operator|==
name|WILDCARD
condition|)
block|{
name|pos
operator|+=
literal|2
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|pattern
operator|.
name|charAt
argument_list|(
name|pos
argument_list|)
operator|==
name|WILDCARD
condition|)
block|{
return|return
literal|true
return|;
block|}
name|pos
operator|++
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
comment|/**      * Internal helper used to recursively match the pattern      *      * @param s       The string to be tested      * @param pattern The pattern      * @param sOff    offset within<code>s</code>      * @param pOff    offset within<code>pattern</code>.      * @return true if<code>s</code> matched pattern, else false.      */
specifier|private
specifier|static
name|boolean
name|internalMatches
parameter_list|(
name|String
name|s
parameter_list|,
name|String
name|pattern
parameter_list|,
name|int
name|sOff
parameter_list|,
name|int
name|pOff
parameter_list|)
block|{
name|int
name|pLen
init|=
name|pattern
operator|.
name|length
argument_list|()
decl_stmt|;
name|int
name|sLen
init|=
name|s
operator|.
name|length
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|pOff
operator|>=
name|pLen
condition|)
block|{
return|return
name|sOff
operator|>=
name|sLen
condition|?
literal|true
else|:
literal|false
return|;
block|}
if|if
condition|(
name|sOff
operator|>=
name|sLen
operator|&&
name|pattern
operator|.
name|charAt
argument_list|(
name|pOff
argument_list|)
operator|!=
name|WILDCARD
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// check for a WILDCARD as the next pattern;
comment|// this is handled by a recursive call for
comment|// each postfix of the name.
if|if
condition|(
name|pattern
operator|.
name|charAt
argument_list|(
name|pOff
argument_list|)
operator|==
name|WILDCARD
condition|)
block|{
operator|++
name|pOff
expr_stmt|;
if|if
condition|(
name|pOff
operator|>=
name|pLen
condition|)
block|{
return|return
literal|true
return|;
block|}
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|internalMatches
argument_list|(
name|s
argument_list|,
name|pattern
argument_list|,
name|sOff
argument_list|,
name|pOff
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|sOff
operator|>=
name|sLen
condition|)
block|{
return|return
literal|false
return|;
block|}
name|sOff
operator|++
expr_stmt|;
block|}
block|}
if|if
condition|(
name|pOff
operator|<
name|pLen
operator|&&
name|sOff
operator|<
name|sLen
condition|)
block|{
comment|// check for escape sequences
if|if
condition|(
name|pattern
operator|.
name|charAt
argument_list|(
name|pOff
argument_list|)
operator|==
name|ESCAPE
condition|)
block|{
comment|// * to be interpreted as literal
if|if
condition|(
name|pOff
operator|<
name|pLen
operator|-
literal|1
operator|&&
name|pattern
operator|.
name|charAt
argument_list|(
name|pOff
operator|+
literal|1
argument_list|)
operator|==
name|WILDCARD
condition|)
block|{
operator|++
name|pOff
expr_stmt|;
block|}
comment|// leading - to be interpreted as literal
if|if
condition|(
name|pOff
operator|==
literal|0
operator|&&
name|pLen
operator|>
literal|1
operator|&&
name|pattern
operator|.
name|charAt
argument_list|(
name|pOff
operator|+
literal|1
argument_list|)
operator|==
name|EXCLUDE_PREFIX
condition|)
block|{
operator|++
name|pOff
expr_stmt|;
block|}
block|}
if|if
condition|(
name|pattern
operator|.
name|charAt
argument_list|(
name|pOff
argument_list|)
operator|!=
name|s
operator|.
name|charAt
argument_list|(
name|sOff
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
name|pOff
operator|++
expr_stmt|;
name|sOff
operator|++
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

